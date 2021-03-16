package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager

/**
 *  Created by wangyanbin
 *  悬浮窗构建类
 */
@SuppressLint("StaticFieldLeak", "WrongConstant")
object FloatWindowHelper {
    private var layoutParams: WindowManager.LayoutParams? = null
    private var context: Context? = null
    private var windowManager: WindowManager? = null
    private var floatWindowView: View? = null

    @JvmStatic
    fun initialize(context: Context, windowManager: WindowManager, floatWindowView: View) {
        this.context = context
        this.windowManager = windowManager
        this.floatWindowView = floatWindowView
        //设置悬浮窗布局属性
         layoutParams = WindowManager.LayoutParams()
        //设置类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        //设置行为选项
        layoutParams?.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        //设置悬浮窗的显示位置
        layoutParams?.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        //如果悬浮窗图片为透明图片，需要设置该参数为PixelFormat.RGBA_8888
        layoutParams?.format = PixelFormat.RGBA_8888
        //设置悬浮窗的宽度
        layoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        //设置悬浮窗的高度
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        //设置悬浮窗的布局/加载显示悬浮窗
        windowManager.addView(floatWindowView, layoutParams)
    }

    fun update(){
        windowManager?.updateViewLayout(floatWindowView, layoutParams)
    }

    @JvmStatic
    fun onDestroy() {
        windowManager?.removeView(floatWindowView)
    }

}