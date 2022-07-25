package com.dataqin.media.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Extras
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.media.utils.helper.ShotHelper.previewHeight
import com.dataqin.media.utils.helper.ShotHelper.previewWidth

/**
 *  Created by wangyanbin
 *  录屏服务
 *  <!-- 屏幕录制 -->
 *  <service
 *      android:name="com.sqkj.home.service.ShotService"
 *      android:enabled="true"
 *      android:exported="false"
 *      android:foregroundServiceType="mediaProjection"--》 Q开始后台服务需要配置，否则录制不正常，同时息屏时间过长也会导致录制失败  />
 */
@SuppressLint("WrongConstant")
class ShotService : Service() {
    private var resultCode = 0
    private var resultData: Intent? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null

    companion object {
        var launch = false
        var saving = false
        var imageReader: ImageReader? = null

        /**
         * 服务截屏的方法
         */
        @JvmStatic
        fun capture(context: Context) {
            if(launch && !saving) startCapture(context)
        }

        /**
         * 生成图片保存到本地
         */
        @JvmStatic
        private fun startCapture(context: Context) {
            val image = imageReader?.acquireLatestImage()
            val width = image?.width ?: 0
            val height = image?.height ?: 0
            val planes = image!!.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val i = rowStride - pixelStride * width
            val bitmap = Bitmap.createBitmap(width + i / pixelStride, height, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(buffer)
            val captureBit = Bitmap.createBitmap(bitmap, 0, 0, width, height)
            image.close()
            FileUtil.saveBitmap(context, captureBit, object : FileUtil.OnThreadListener {
                override fun onStart() {
                    saving = true
                }

                override fun onStop(path: String?) {
                    saving = false
                }
            })
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startForeground(1, Notification())
        } else {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(NotificationChannel(packageName, packageName, NotificationManager.IMPORTANCE_DEFAULT))
            val builder = NotificationCompat.Builder(this, packageName)
            startForeground(1, builder.build())
        }
        stopForeground(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            launch = true
            resultCode = intent?.getIntExtra(Extras.RESULT_CODE, -1) ?: 0
            resultData = intent?.getParcelableExtra(Extras.BUNDLE_BEAN)
            mediaProjection = createMediaProjection()
            imageReader = createImageReader()
            virtualDisplay = createVirtualDisplay()
        } catch (ignored: Exception) {
        }
        return START_STICKY
    }

    private fun createMediaProjection(): MediaProjection? {
        return (getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager).getMediaProjection(resultCode, resultData!!)
    }

    private fun createImageReader(): ImageReader {
        return ImageReader.newInstance(previewWidth, previewHeight, PixelFormat.RGBA_8888, 2)
    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        return mediaProjection?.createVirtualDisplay("mediaProjection", previewWidth, previewHeight, Constants.SCREEN_DENSITY, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader?.surface, null, null)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            launch = false
            saving = false
            virtualDisplay?.release()
            virtualDisplay = null
            imageReader?.close()
            imageReader = null
            mediaProjection?.stop()
            mediaProjection = null
        } catch (ignored: Exception) {
        }
    }

}