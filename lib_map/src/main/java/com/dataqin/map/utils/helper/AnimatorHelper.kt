package com.dataqin.map.utils.helper

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AnimationUtils
import com.dataqin.base.utils.WeakHandler
import com.dataqin.map.R

/**
 * 刷新
 * 3s不可操作
 */
fun View.refresh() {
    isEnabled = false
    visibility = View.VISIBLE
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(ObjectAnimator.ofFloat(this, "rotation", 0f, 360f))
    animatorSet.duration = 500
    animatorSet.start()
    WeakHandler().postDelayed({
        isEnabled = true
    }, 3000)
}

/**
 * 底部弹出
 * 0.5s不可操作
 */
fun View.shown() {
    isEnabled = false
    visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.set_translate_bottom_in)
    startAnimation(animation)
    WeakHandler().postDelayed({
        isEnabled = true
    }, 500)
}