package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dataqin.base.utils.StringUtil
import com.dataqin.base.utils.ToastUtil
import com.dataqin.common.constant.Extras
import com.dataqin.common.constant.RequestCode
import com.dataqin.common.utils.helper.TimeTaskHelper
import com.dataqin.media.R
import com.dataqin.media.service.ScreenRecordService
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  录屏工具类
 */
@SuppressLint("WrongConstant", "StaticFieldLeak")
object ScreenHelper {
    private var isWindow = false
    private var timerCount = 0
    private var weakActivity: WeakReference<Activity>? = null

    @JvmStatic
    fun initialize(activity: Activity) {
        weakActivity = WeakReference(activity)
    }

    /**
     * 开始录屏
     * 尝试唤起手机录屏弹窗，会在onActivityResult中回调结果
     */
    @JvmStatic
    fun startScreen(isWindow: Boolean = false) {
        this.isWindow = isWindow
        if (isWindow) {
            if (WindowHelper.checkFloatWindowPermission(weakActivity?.get()!!)) {
                start()
            } else {
                ToastUtil.mackToastSHORT("当前无权限，请授权", weakActivity?.get()!!)
                val packageURI = Uri.parse("package:" + weakActivity?.get()?.packageName)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                weakActivity?.get()?.startActivity(intent)
            }
        } else {
            start()
        }
    }

    private fun start() {
        val mediaProjectionManager = weakActivity?.get()?.getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
        weakActivity?.get()?.startActivityForResult(permissionIntent, RequestCode.SERVICE_REQUEST)
    }

    /**
     * 处理录屏的回调
     */
    @JvmStatic
    fun startScreenResult(resultCode: Int, data: Intent?) {
        stopScreen()
        if (isWindow) {
            timerCount = 0
            val view = LayoutInflater.from(weakActivity?.get()).inflate(R.layout.view_timer_window, null)
            TimeTaskHelper.startTask(1000, object : TimeTaskHelper.OnCountUpListener {
                override fun run() {
                    timerCount++
                    view.findViewById<TextView>(R.id.tv_count).text =
                        StringUtil.getSecondFormat(timerCount.toLong())
                    WindowHelper.update()
                }
            })
            WindowHelper.initialize(view)
        }
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
        if (isWindow) {
            TimeTaskHelper.stopTask()
            WindowHelper.onDestroy()
        }
        weakActivity?.get()?.stopService(Intent(weakActivity?.get()!!, ScreenRecordService::class.java))
    }

}