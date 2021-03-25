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
 * 渐隐显示
 */
fun View.fade() {
    isEnabled = false
    visibility = View.VISIBLE
    animatorSet.playTogether(ObjectAnimator.ofFloat(this, "alpha", 0f, 0.5f, 1f))
    animatorSet.duration = 500
    animatorSet.start()
    weakHandler.postDelayed({
        isEnabled = true
    }, 500)
}

/**
 * 底部弹出
 * 1s不可操作
 */
fun View.shown() {
    isEnabled = false
    visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.set_translate_bottom_in)
    startAnimation(animation)
    weakHandler.postDelayed({
        isEnabled = true
    }, 500)
}