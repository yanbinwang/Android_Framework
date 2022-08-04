package com.dataqin.media.utils.helper

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Extras
import com.dataqin.common.constant.RequestCode
import com.dataqin.media.service.ScreenService
import java.lang.ref.WeakReference
import java.util.*

/**
 *  Created by wangyanbin
 *  录屏工具类
 */
object ScreenHelper {
    private var weakActivity: WeakReference<Activity>? = null
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    var waitingTime = 0
    var previewWidth = 0
    var previewHeight = 0

    @JvmStatic
    fun initialize(activity: Activity) {
        weakActivity = WeakReference(activity)
        //获取录屏屏幕宽高，高版本进行修正
        previewWidth = Constants.SCREEN_WIDTH
        previewHeight = Constants.SCREEN_HEIGHT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val decorView = activity.window.decorView
            decorView.post {
                val displayCutout = decorView.rootWindowInsets.displayCutout
                val rectLists = displayCutout?.boundingRects
                if (null != rectLists && rectLists.size > 0) {
                    previewWidth = Constants.SCREEN_WIDTH - displayCutout.safeInsetLeft - displayCutout.safeInsetRight
                    previewHeight = Constants.SCREEN_HEIGHT - displayCutout.safeInsetTop - displayCutout.safeInsetBottom
                }
            }
        }
    }

    /**
     * 开始录屏
     * 尝试唤起手机录屏弹窗，会在onActivityResult中回调结果
     */
    @JvmStatic
    fun startScreen() {
        waitingTime = 0
        if (timer == null) {
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    waitingTime++
                }
            }
            timer?.schedule(timerTask, 0, 1000)
        }
        val mediaProjectionManager = weakActivity?.get()?.getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
        weakActivity?.get()?.startActivityForResult(permissionIntent, RequestCode.SERVICE_REQUEST)
    }

    /**
     * 处理录屏的回调
     */
    @JvmStatic
    fun startScreenResult(resultCode: Int, data: Intent?) {
        val service = Intent(weakActivity?.get()!!, ScreenService::class.java)
        service.putExtra(Extras.RESULT_CODE, resultCode)
        service.putExtra(Extras.BUNDLE_BEAN, data)
        weakActivity?.get()?.startService(service)
        weakActivity?.get()?.moveTaskToBack(true)
    }

    /**
     * 结束录屏
     */
    @JvmStatic
    fun stopScreen() = weakActivity?.get()?.stopService(Intent(weakActivity?.get()!!, ScreenService::class.java))

}