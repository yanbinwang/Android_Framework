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
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Extras
import com.dataqin.media.utils.ComponentsFactory
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.helper.ScreenHelper.previewHeight
import com.dataqin.media.utils.helper.ScreenHelper.previewWidth

/**
 *  Created by wangyanbin
 *  录屏服务
 *  <!-- 屏幕录制 -->
 *  <service
 *      android:name="com.sqkj.home.service.ScreenService"
 *      android:enabled="true"
 *      android:exported="false"
 *      android:foregroundServiceType="mediaProjection"--》 Q开始后台服务需要配置，否则录制不正常  />
 */
class ScreenService : Service() {
    private var filePath = ""
    private var resultCode = 0
    private var resultData: Intent? = null
    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null
    private val componentsFactory by lazy { ComponentsFactory(this) }

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
        }
//        stopForeground(true)//关闭录屏的图标-可注释
        componentsFactory.onStart()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            resultCode = intent?.getIntExtra(Extras.RESULT_CODE, -1) ?: 0
            resultData = intent?.getParcelableExtra(Extras.BUNDLE_BEAN)
            mediaProjection = createMediaProjection()
            mediaRecorder = createMediaRecorder()
            virtualDisplay = createVirtualDisplay()
            mediaRecorder?.start()
        } catch (ignored: Exception) {
        }
        return START_STICKY
    }

    private fun createMediaProjection(): MediaProjection? {
        return (getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager).getMediaProjection(resultCode, resultData!!)
    }

    private fun createMediaRecorder(): MediaRecorder {
        val file = MediaFileUtil.getOutputFile(MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST)
        filePath = file.toString()
        postResult(true)
        return MediaRecorder().apply {
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setVideoEncodingBitRate(5 * previewWidth * previewHeight)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoSize(previewWidth, previewHeight)
            setVideoFrameRate(60)
            try {
                //若api低于O，调用setOutputFile(String path),高于使用setOutputFile(File path)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) setOutputFile(filePath) else setOutputFile(file)
                prepare()
            } catch (ignored: Exception) {
            }
        }
    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        return mediaProjection?.createVirtualDisplay("mediaProjection", previewWidth, previewHeight, Constants.SCREEN_DENSITY, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder?.surface, null, null)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            componentsFactory.onDestroy()
            virtualDisplay?.release()
            virtualDisplay = null
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
            mediaProjection?.stop()
            mediaProjection = null
        } catch (ignored: Exception) {
        } finally {
            postResult(false)
        }
    }

    private fun postResult(create: Boolean) {
        val bundle = Bundle()
        bundle.putString(Extras.FILE_PATH, filePath)
        bundle.putBoolean(Extras.FILE_CREATE, create)
        RxBus.instance.post(RxEvent(Constants.APP_SCREEN_FILE, bundle))
    }

}