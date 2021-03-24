package com.dataqin.map.utils.helper

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AnimationUtils
import com.dataqin.map.R

/**
 * 刷新
 */
fun View.refresh() {
    visibility = View.VISIBLE
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(ObjectAnimator.ofFloat(this, "rotation", 0f, 360f))
    animatorSet.duration = 500
    animatorSet.start()
}

/**
 * 底部弹出
 */
fun View.shown() {
    visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.set_translate_bottom_in)
    startAnimation(animation)
}