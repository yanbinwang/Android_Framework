package com.dataqin.media.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Extras
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.MobileSizeUtil

/**
 *  Created by wangyanbin
 *  录屏服务
 *  <!-- 屏幕录制 -->
 *  <service
 *      android:name="com.sqkj.home.service.ScreenRecordService"
 *      android:enabled="true"
 *      android:exported="false" />
 */
class ScreenRecordService : Service() {
    private var filePath = ""
    private var resultCode = 0
    private var resultData: Intent? = null
    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startForeground(1, Notification())
        } else {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(NotificationChannel(packageName, packageName, NotificationManager.IMPORTANCE_DEFAULT))
            val builder = NotificationCompat.Builder(this, packageName)
            //id不为0即可，该方法表示将服务设置为前台服务
            startForeground(1, builder.build())
            stopForeground(true)//关闭录屏的图标-可注释
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            resultCode = intent?.getIntExtra(Extras.RESULT_CODE, -1) ?: 0
            resultData = intent?.getParcelableExtra(Extras.BUNDLE_BEAN)
            mediaProjection = createMediaProjection()
            mediaRecorder = createMediaRecorder()
            virtualDisplay = createVirtualDisplay()
            mediaRecorder?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    private fun createMediaProjection(): MediaProjection? {
        return (getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager).getMediaProjection(resultCode, resultData)
    }

    private fun createMediaRecorder(): MediaRecorder {
        val file = MediaFileUtil.getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST, Constants.APPLICATION_NAME + "/" + Constants.SCREEN_FILE_PATH)
        filePath = file.toString()
        return MediaRecorder().apply {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                setVideoEncodingBitRate(MobileSizeUtil.getWidth(Constants.SCREEN_WIDTH) * MobileSizeUtil.getHeight(Constants.SCREEN_HEIGHT))
            } else {
                setVideoEncodingBitRate(Constants.SCREEN_WIDTH * Constants.SCREEN_HEIGHT)
            }
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            //高版本手机分辨率会有问题
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                setVideoSize(MobileSizeUtil.getWidth(Constants.SCREEN_WIDTH), MobileSizeUtil.getHeight(Constants.SCREEN_HEIGHT))
            } else {
                setVideoSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)
            }
            setVideoFrameRate(60)
            try {
                //若api低于O，调用setOutputFile(String path),高于使用setOutputFile(File path)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    setOutputFile(filePath)
                } else {
                    setOutputFile(file)
                }
                prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            mediaProjection?.createVirtualDisplay("mediaProjection", MobileSizeUtil.getWidth(Constants.SCREEN_WIDTH), MobileSizeUtil.getHeight(Constants.SCREEN_HEIGHT), Constants.SCREEN_DENSITY, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder?.surface, null, null)
        } else {
            mediaProjection?.createVirtualDisplay("mediaProjection", Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Constants.SCREEN_DENSITY, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder?.surface, null, null)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualDisplay?.release()
        virtualDisplay = null
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
        } catch (e: Exception) {
        }
        mediaProjection?.stop()
        mediaProjection = null
        RxBus.instance.post(RxEvent(Constants.APP_SCREEN_FILE_CREATE, filePath))
    }

}