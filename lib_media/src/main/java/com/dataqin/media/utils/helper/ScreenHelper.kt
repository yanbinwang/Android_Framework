package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Binder
import android.os.Build
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
    private var isFloat = false
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
    fun startScreen(isFloat: Boolean = false) {
        this.isFloat = isFloat
        if (isFloat) {
            if (checkFloatWindowPermission()) {
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

    private fun checkFloatWindowPermission(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Settings.canDrawOverlays(weakActivity?.get())
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> getAppOps()
            //4.4以下一般都可以直接添加悬浮窗
            else -> true
        }
    }

    //判断app悬浮窗是否允许
    private fun getAppOps(): Boolean {
        try {
            val obj = weakActivity?.get()?.getSystemService("appops") ?: return false
            val localClass: Class<*> = obj.javaClass
            val arrayOfClass: Array<Class<*>?> = arrayOfNulls(3)
            arrayOfClass[0] = Integer.TYPE
            arrayOfClass[1] = Integer.TYPE
            arrayOfClass[2] = String::class.java
            val method = localClass.getMethod("checkOp", *arrayOfClass)
            val arrayOfObject1 = arrayOfNulls<Any>(3)
            arrayOfObject1[0] = Integer.valueOf(24)
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid())
            arrayOfObject1[2] = weakActivity?.get()?.packageName
            val m = (method.invoke(obj, *arrayOfObject1) as Int).toInt()
            return m == AppOpsManager.MODE_ALLOWED
        } catch (ignored: Exception) {
        }
        return false
    }

    /**
     * 处理录屏的回调
     */
    @JvmStatic
    fun startScreenResult(resultCode: Int, data: Intent?) {
        stopScreen()
        if(isFloat){
            timerCount = 0
            val view = LayoutInflater.from(weakActivity?.get()).inflate(R.layout.view_timer_window, null)
            TimeTaskHelper.startTask(1000, object : TimeTaskHelper.OnCountUpListener {
                override fun run() {
                    timerCount++
                    view.findViewById<TextView>(R.id.tv_count).text = StringUtil.getSecondFormat(timerCount.toLong())
                    FloatWindowHelper.update()
                }
            })
            FloatWindowHelper.initialize(view)
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
        if(isFloat) {
            TimeTaskHelper.stopTask()
            FloatWindowHelper.onDestroy()
        }
        weakActivity?.get()?.stopService(Intent(weakActivity?.get()!!, ScreenRecordService::class.java))
    }

}