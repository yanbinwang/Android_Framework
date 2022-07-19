package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.dataqin.base.utils.ToastUtil
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Extras
import com.dataqin.common.constant.RequestCode
import com.dataqin.media.service.ShotService
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  截屏工具类
 */
@SuppressLint("WrongConstant")
object ShotHelper {
    private var weakActivity: WeakReference<Activity>? = null
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
     * 开始截屏
     */
    @JvmStatic
    fun startScreenShot() {
        if (!ShotService.launch) {
            ToastUtil.mackToastSHORT("请授权后再操作", weakActivity?.get()!!)
            val mediaProjectionManager = weakActivity?.get()?.getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
            weakActivity?.get()?.startActivityForResult(permissionIntent, RequestCode.MEDIA_REQUEST)
        } else ShotService.startCapture(weakActivity?.get()!!)
    }

    /**
     * 处理截屏的回调
     */
    @JvmStatic
    fun startScreenShot(resultCode: Int, data: Intent?) {
        val service = Intent(weakActivity?.get()!!, ShotService::class.java)
        service.putExtra(Extras.RESULT_CODE, resultCode)
        service.putExtra(Extras.BUNDLE_BEAN, data)
        weakActivity?.get()?.startService(service)
    }

    /**
     * 结束截屏
     */
    @JvmStatic
    fun stopScreenShot() = weakActivity?.get()?.stopService(Intent(weakActivity?.get()!!, ShotService::class.java))

}