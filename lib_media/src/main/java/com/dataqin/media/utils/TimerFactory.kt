package com.dataqin.media.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Looper
import android.view.*
import android.widget.TextView
import com.dataqin.base.utils.DateUtil
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.utils.helper.ConfigHelper
import com.dataqin.media.R
import java.util.*

/**
 * 录屏小组件工具栏
 */
class TimerFactory(var context: Context, var move: Boolean = true) {
    private var tvTimer: TextView? = null
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var tickDialog: AlertDialog? = null
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }

    companion object {
        @Volatile
        var timerCount: Long = 0//录制时的时间在应用回退到页面时赋值页面的时间
    }

    /**
     * 初始化时调用，点击事件，弹框的初始化
     */
    init {
        if (null == tickDialog) {
            val view = LayoutInflater.from(context).inflate(R.layout.view_floating_timer, null)
            tvTimer = view.findViewById(R.id.tv_timer)
            //设置一个自定义的弹框
            val builder = AlertDialog.Builder(context, R.style.dialogStyle)
            builder.setView(view)
            tickDialog = builder.create()
            tickDialog?.apply {
                window?.setType(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
                window?.decorView?.setPadding(0, 0, 0, 0)
                window?.decorView?.background = ColorDrawable(Color.TRANSPARENT)
                setCancelable(false)
                view?.post {
                    val params = window?.attributes
                    params?.gravity = Gravity.TOP or Gravity.END
                    params?.verticalMargin = 0f
                    params?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    params?.height = view.measuredHeight
                    window?.attributes = params
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))//透明
                    //配置移动，只支持上下
                    if (null != params && move) {
                        view.setOnTouchListener(object : View.OnTouchListener {
                            private var lastX = 0
                            private var lastY = 0
                            private var paramX = 0
                            private var paramY = 0

                            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                                when (event?.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        lastX = event.rawX.toInt()
                                        lastY = event.rawY.toInt()
                                        paramX = params.x
                                        paramY = params.y
                                    }
                                    MotionEvent.ACTION_MOVE -> {
                                        val dx = event.rawX.toInt() - lastX
                                        val dy = event.rawY.toInt() - lastY
                                        params.x = paramX - dx
                                        params.y = paramY + dy
                                        window?.attributes = params
                                    }
                                }
                                return true
                            }
                        })
                    }
                }
            }
        }
    }

    /**
     * 开启定时器计时按秒累加，毫秒级的操作不能被获取
     */
    fun onStart() {
        timerCount = 0
        if (timer == null) {
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    weakHandler.post {
                        timerCount++
                        //每秒做一次检测，当程序退到后台显示计时器
                        if (null != tickDialog) {
                            if (!ConfigHelper.isAppOnForeground()) {
                                if (!tickDialog!!.isShowing) tickDialog?.show()
                            } else {
                                tickDialog?.dismiss()
                            }
                        }
                        tvTimer?.text = DateUtil.getSecondFormat(timerCount - 1)
                    }
                }
            }
            timer?.schedule(timerTask, 0, 1000)
        }
    }

    /**
     * 挂载的服务销毁同时调用，结束计时器,弹框等
     */
    fun onDestroy() {
        timerCount = 0
        timerTask?.cancel()
        timer?.cancel()
        timerTask = null
        timer = null
        tickDialog?.dismiss()
        tickDialog = null
    }

}