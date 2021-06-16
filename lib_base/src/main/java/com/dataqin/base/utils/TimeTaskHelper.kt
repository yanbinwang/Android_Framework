package com.dataqin.base.utils

import android.os.Looper
import java.util.*

/**
 *  Created by wangyanbin
 *  时间工具类
 *  默认1秒，分计数和倒计时
 */
object TimeTaskHelper {
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var countDownTimer: Timer? = null
    private var countDownTimerTask: TimerTask? = null
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
    fun startCountDown(second: Long, onCountDownListener: OnCountDownListener?) {
        var time = 0L
        if (null == countDownTimer) {
            countDownTimer = Timer()
            countDownTimerTask = object : TimerTask() {
                override fun run() {
                    time++
                    if (time == second) {
                        weakHandler.post { onCountDownListener?.onFinish() }
                        stopCountDown()
                    } else {
                        weakHandler.post { onCountDownListener?.onTick(second - time) }
                    }
                }
            }
            countDownTimer?.schedule(countDownTimerTask, 1000)
        }
    }

    /**
     * 倒计时-结束
     */
    @JvmStatic
    fun stopCountDown() {
        countDownTimerTask?.cancel()
        countDownTimer?.cancel()
        countDownTimerTask = null
        countDownTimer = null
    }

    /**
     * 页面销毁时调取
     */
    @JvmStatic
    fun destroy() {
        stopTask()
        stopCountDown()
    }

    interface OnTaskListener {

        fun run()

    }

    interface OnCountDownListener {

        fun onTick(second: Long)//返回秒

        fun onFinish()

    }

}