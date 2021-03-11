package com.dataqin.common.utils

import android.annotation.SuppressLint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

/**
 *  Created by wangyanbin
 *  按钮，控件行为工具类
 */
@SuppressLint("SetTextI18n")
object MethodUtil {

    /**
     * 密码是否可见
     */
    fun changeVisibility(isDisplay: Boolean, editText: EditText, imageView: ImageView): Boolean {
        if (!isDisplay) {
            // display password text, for example "123456"
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            try {
                editText.setSelection(editText.text.length)
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            imageView.setBackgroundResource(R.mipmap.ic_text_show)
        } else {
            // hide password, display "."
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            try {
                editText.setSelection(editText.text.length)
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            imageView.setBackgroundResource(R.mipmap.ic_text_hide)
        }
        editText.postInvalidate()
        return !isDisplay
    }

    /**
     * 倒计时
     */
    fun countDown(text: TextView, second: Long) {
        TimerUtil.countDown(second, object : TimerUtil.OnCountDownListener {
            override fun onTick(millisecond: Long) {
                text.isEnabled = false
                text.text = "已发送 " + (millisecond / 1000) + " S"
            }

            override fun onFinish() {
                text.isEnabled = true
                text.text = "重发验证码"
            }
        })
    }

    /**
     * 计时-销毁
     */
    fun destroy() {
        TimerUtil.destroy()
    }

}