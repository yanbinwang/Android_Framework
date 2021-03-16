package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.dataqin.common.BaseApplication
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.handler.WeakHandler
import kotlin.math.abs

/**
 *  Created by wangyanbin
 *  悬浮窗构建类
 */
@SuppressLint("StaticFieldLeak", "WrongConstant", "ClickableViewAccessibility")
object FloatWindowHelper {
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var windowsView: View? = null
    private var downX = 0
    private var downY = 0

    @JvmStatic
    fun initialize(windowsView: View) {
        this.windowManager = BaseApplication.instance!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        this.windowsView = windowsView
        layoutParams = WindowManager.LayoutParams()
        //设置类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        //设置行为选项
//        layoutParams?.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        layoutParams?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//不屏蔽返回键
        //设置悬浮窗的显示位置
        layoutParams?.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
        //如果悬浮窗图片为透明图片，需要设置该参数为PixelFormat.RGBA_8888
        layoutParams?.format = PixelFormat.RGBA_8888
        //设置悬浮窗的宽度
        layoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        //设置悬浮窗的高度
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        regTouch()
        //设置悬浮窗的布局/加载显示悬浮窗
        windowManager?.addView(windowsView, layoutParams)
    }

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
                        layoutParams!!.x += disX
                        layoutParams!!.y += disY
                        update()
                        lastX = event.rawX.toInt()
                        lastY = event.rawY.toInt()
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
                            onClick()
                        }
                        if (x > Constants.SCREEN_WIDTH / 2) {
                            //放手后移到右边
                            handler.sendEmptyMessage(0)
                        } else {
                            //移到左边
                            handler.sendEmptyMessage(1)
                        }
                    }
                }
                return true
            }
        })
    }

    private val handler = WeakHandler { msg ->
        when (msg.what) {
            0 -> layoutParams?.x = Constants.SCREEN_WIDTH
            1 -> layoutParams?.x = 0
        }
        update()
        false
    }

    private fun onClick() {
        //
    }

    fun update() {
        windowManager?.updateViewLayout(windowsView, layoutParams)
    }

    @JvmStatic
    fun onDestroy() {
        windowManager?.removeView(windowsView)
    }

}