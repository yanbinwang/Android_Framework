package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.dataqin.common.BaseApplication
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.handler.WeakHandler
import java.lang.Math.abs
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  悬浮窗构建类
 */
@SuppressLint("StaticFieldLeak", "WrongConstant", "ClickableViewAccessibility")
object WindowHelper {
    private var downX = 0
    private var downY = 0
    private var windowsView: View? = null
    private val windowManager by lazy { BaseApplication.instance?.getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    private val layoutParams by lazy { WindowManager.LayoutParams() }
    private val touchHandler = WeakHandler { msg ->
        when (msg.what) {
            0 -> layoutParams.x = Constants.SCREEN_WIDTH
            1 -> layoutParams.x = 0
        }
        update()
        false
    }
    var onWindowClickListener: OnWindowClickListener? = null

    @JvmStatic
    fun initialize(view: View, move: Boolean = false) {
        windowsView = view
        //设置类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        //设置行为选项
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        //设置悬浮窗的显示位置
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        //如果悬浮窗图片为透明图片，需要设置该参数为PixelFormat.RGBA_8888
        layoutParams.format = PixelFormat.RGBA_8888
        //设置悬浮窗的宽度
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        //设置悬浮窗的高度
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        if (move) {
            regTouch()
        }
        //设置悬浮窗的布局/加载显示悬浮窗
        windowManager.addView(windowsView, layoutParams)
    }

    //配置悬浮窗的移动
    private fun regTouch() {
        windowsView?.setOnTouchListener(object : View.OnTouchListener {
            private var lastX = 0
            private var lastY = 0

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    //按下 事件
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
                        downX = lastX
                        downY = lastY
                    }
                    //移动 事件
                    MotionEvent.ACTION_MOVE -> {
                        val disX = (event.rawX - lastX).toInt()
                        val disY = (event.rawY - lastY).toInt()
                        layoutParams.x += disX
                        layoutParams.y += disY
                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
                        update()
                    }
                    //抬起 事件
                    MotionEvent.ACTION_UP -> {
                        val x = event.rawX.toInt()
                        val y = event.rawY.toInt()
                        var upX = x - downX
                        var upY = y - downY
                        upX = abs(upX)
                        upY = abs(upY)
                        if (upX < 20 && upY < 20) {
                            //点击进入指定页面
                            onWindowClickListener?.onClick()
                        }
                        if (x > Constants.SCREEN_WIDTH / 2) {
                            //放手后移到右边
                            touchHandler.sendEmptyMessage(0)
                        } else {
                            //移到左边
                            touchHandler.sendEmptyMessage(1)
                        }
                    }
                }
                return true
            }
        })
    }

    @JvmStatic
    fun checkFloatWindowPermission(activity: Activity): Boolean {
        val weakActivity = WeakReference(activity)
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> Settings.canDrawOverlays(weakActivity.get())
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> getAppOps(activity)
            //4.4以下一般都可以直接添加悬浮窗
            else -> true
        }
    }

    //判断app悬浮窗是否允许
    private fun getAppOps(activity: Activity): Boolean {
        val weakActivity = WeakReference(activity)
        try {
            val obj = weakActivity.get()?.getSystemService("appops") ?: return false
            val localClass: Class<*> = obj.javaClass
            val arrayOfClass: Array<Class<*>?> = arrayOfNulls(3)
            arrayOfClass[0] = Integer.TYPE
            arrayOfClass[1] = Integer.TYPE
            arrayOfClass[2] = String::class.java
            val method = localClass.getMethod("checkOp", *arrayOfClass)
            val arrayOfObject1 = arrayOfNulls<Any>(3)
            arrayOfObject1[0] = Integer.valueOf(24)
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid())
            arrayOfObject1[2] = weakActivity.get()?.packageName
            val m = (method.invoke(obj, *arrayOfObject1) as Int).toInt()
            return m == AppOpsManager.MODE_ALLOWED
        } catch (ignored: Exception) {
        }
        return false
    }

    @JvmStatic
    fun update() {
        windowManager.updateViewLayout(windowsView, layoutParams)
    }

    @JvmStatic
    fun onDestroy() {
        windowManager.removeView(windowsView)
    }

    interface OnWindowClickListener {

        fun onClick()

    }

}