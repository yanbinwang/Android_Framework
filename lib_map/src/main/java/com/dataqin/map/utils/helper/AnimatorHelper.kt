package com.dataqin.map.utils.helper

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import com.dataqin.base.utils.WeakHandler
import com.dataqin.map.R

private val animatorSet by lazy { AnimatorSet() }
private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }

/**
 * 刷新
 * 3s不可操作
 */
fun View.refresh() {
    isEnabled = false
    visibility = View.VISIBLE
    animatorSet.playTogether(ObjectAnimator.ofFloat(this, "rotation", 0f, 360f))
    animatorSet.duration = 500
    animatorSet.start()
    weakHandler.postDelayed({
        isEnabled = true
    }, 3000)
}

/**
 * 打卡时间的：符号的动画
 */
fun View.pass() {
    val animation = AnimationUtils.loadAnimation(context, R.anim.set_sign_in)
    startAnimation(animation)
}

/**
 * 底部弹出
 * 0.5s不可操作
 */
fun View.shown() {
    if(View.VISIBLE == visibility) return
    isEnabled = false
    visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.set_translate_bottom_in)
    startAnimation(animation)
    weakHandler.postDelayed({
        isEnabled = true
    }, 500)
}

/**
 * 底部隐藏
 * 0.5s不可操作
 */
fun View.hidden() {
    if(View.GONE == visibility) return
    isEnabled = false
    val animation = AnimationUtils.loadAnimation(context, R.anim.set_translate_bottom_out)
    startAnimation(animation)
    weakHandler.postDelayed({
        isEnabled = true
        visibility = View.GONE
    }, 500)
}

/**
 * 渐隐显示
 */
fun View.fadeIn(view: View? = null) {
    if (0f != alpha) return
    view?.isEnabled = false
    isEnabled = false
    visibility = View.VISIBLE
    animatorSet.playTogether(ObjectAnimator.ofFloat(this, "alpha", 0f, 0.5f, 1f))
    animatorSet.duration = 500
    animatorSet.start()
    weakHandler.postDelayed({
        view?.isEnabled = true
        isEnabled = true
    }, 500)
}

/**
 * 渐隐隐藏
 */
fun View.fadeOut(view: View? = null) {
    if (0f == alpha) return
    view?.isEnabled = false
    isEnabled = false
    visibility = View.VISIBLE
    animatorSet.playTogether(ObjectAnimator.ofFloat(this, "alpha", 1f, 0.5f, 0f))
    animatorSet.duration = 500
    animatorSet.start()
    weakHandler.postDelayed({
        view?.isEnabled = true
        isEnabled = true
    }, 500)
}