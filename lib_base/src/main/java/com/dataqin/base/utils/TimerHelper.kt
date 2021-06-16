package com.dataqin.base.utils

import android.os.Looper
import java.util.*

/**
 *  Created by wangyanbin
 *  时间工具类
 *  默认1秒，分计数和倒计时
 *  一个页面计时和倒计时有且只有一个存在，多种可考虑使用rxjava自带
 */
object TimerHelper {
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var downTimer: Timer? = null
    private var downTimerTask: TimerTask? = null
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }

    /**
     * 延时任务-容易造成内存泄漏
     */
    @JvmStatic
    fun schedule(millisecond: Long = 1000, onTaskListener: OnTaskListener?) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                weakHandler.post { onTaskListener?.run() }
            }
        }, millisecond)
    }

    /**
     * 计时-开始
     */
    @JvmStatic
    fun startTask(millisecond: Long = 1000, onTaskListener: OnTaskListener?) {
        if (timer == null) {
            timer = Timer()
            timerTask = object : TimerTask() {
                override fun run() {
                    weakHandler.post { onTaskListener?.run() }
                }
            }
            timer?.schedule(timerTask, millisecond)
        }
    }

    /**
     * 计时-结束
     */
    @JvmStatic
    fun stopTask() {
        timerTask?.cancel()
        timer?.cancel()
        timerTask = null
        timer = null
    }

    /**
     * 倒计时-开始
     * second-秒
     */
    @JvmStatic
    fun startDownTask(second: Long, onCountDownListener: OnCountDownListener?) {
        var time = 0L
        if (null == downTimer) {
            downTimer = Timer()
            downTimerTask = object : TimerTask() {
                override fun run() {
                    time++
                    if (time == second) {
                        weakHandler.post { onCountDownListener?.onFinish() }
                        stopDownTask()
                    } else {
                        weakHandler.post { onCountDownListener?.onTick(second - time) }
                    }
                }
            }
            downTimer?.schedule(downTimerTask, 1000)
        }
    }

    /**
     * 倒计时-结束
     */
    @JvmStatic
    fun stopDownTask() {
        downTimerTask?.cancel()
        downTimer?.cancel()
        downTimerTask = null
        downTimer = null
    }

    /**
     * 页面销毁时调取
     */
    @JvmStatic
    fun destroy() {
        stopTask()
        stopDownTask()
    }

    interface OnTaskListener {

        fun run()

    }

    interface OnCountDownListener {

        fun onTick(second: Long)//返回秒

        fun onFinish()

    }

}