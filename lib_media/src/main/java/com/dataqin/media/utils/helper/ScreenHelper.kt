package com.dataqin.media.utils.helper

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.appcompat.app.AppCompatActivity
import com.dataqin.common.constant.Extras
import com.dataqin.common.constant.RequestCode
import com.dataqin.media.service.ScreenRecordService
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  录屏工具类
 */
object ScreenHelper {
    private var weakActivity: WeakReference<Activity>? = null

    @JvmStatic
    fun initialize(activity: Activity) {
        weakActivity = WeakReference(activity)
    }

    /**
     * 开始录屏
     */
    @JvmStatic
    fun startScreen() {
        val mediaProjectionManager = weakActivity?.get()?.getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
        weakActivity?.get()?.startActivityForResult(permissionIntent, RequestCode.SERVICE_REQUEST)
    }

    /**
     * 处理回调
     */
    @JvmStatic
    fun startScreenResult(resultCode: Int, data: Intent?) {
        stopScreen()
        val service = Intent(weakActivity?.get()!!, ScreenRecordService::class.java)
        service.putExtra(Extras.RESULT_CODE, resultCode)
        service.putExtra(Extras.BUNDLE_BEAN, data)
        weakActivity?.get()?.startService(service)
        weakActivity?.get()?.moveTaskToBack(true)
    }

    /**
     * 结束录屏
     */
    @JvmStatic
    fun stopScreen() {
        weakActivity?.get()?.stopService(Intent(weakActivity?.get()!!, ScreenRecordService::class.java))//先停止，提高稳定性
    }

}