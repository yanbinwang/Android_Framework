package com.dataqin.testnew.widget.keyboard

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.dataqin.common.widget.xrecyclerview.callback.OnItemClickListener
import com.dataqin.testnew.R

/**
 * 输入帮助类
 */
@SuppressLint("StaticFieldLeak")
object InputHelper {
    private var etInput: EditText? = null
    private var vkKeyboard: VirtualKeyboardView? = null
    private var enterAnim: Animation? = null
    private var exitAnim: Animation? = null

    @JvmStatic
    fun initialize(activity: Activity, etInput: EditText, vkKeyboard: VirtualKeyboardView) {
        //建立对应的绑定关系，让edittext不再弹出系统的输入框
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        try {
            val setShowSoftInputOnFocus = EditText::class.java.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
            setShowSoftInputOnFocus.isAccessible = true
            setShowSoftInputOnFocus.invoke(etInput, false)
        } catch (e: Exception) {
        }
        enterAnim = AnimationUtils.loadAnimation(activity, R.anim.set_password_bottom_in)
        exitAnim = AnimationUtils.loadAnimation(activity, R.anim.set_password_bottom_out)
        this.etInput = etInput
        this.vkKeyboard = vkKeyboard
        etInput.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) shown() else hidden() }
        etInput.setOnClickListener { shown() }
        vkKeyboard.back.setOnClickListener { hidden() }
        val valueList = vkKeyboard.valueList
        (vkKeyboard.recyclerView.adapter as VirtualKeyboardAdapter).onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                //点击0~9按钮
                if (position < 11 && position != 9) {
                    var amount = etInput.text.toString().trim { it <= ' ' }
                    amount += valueList[position]["name"]
                    etInput.setText(amount)
                    etInput.setSelection(etInput.text.length)
                } else {
                    //点击退格键
                    if (position == 9) {
                        var amount = etInput.text.toString().trim { it <= ' ' }
                        if (!amount.contains(".")) {
                            amount += valueList[position]["name"]
                            etInput.setText(amount)
                            etInput.setSelection(etInput.text.length)
                        }
                    }
                    //点击退格键
                    if (position == 11) {
                        var amount = etInput.text.toString().trim { it <= ' ' }
                        if (amount.isNotEmpty()) {
                            amount = amount.substring(0, amount.length - 1)
                            etInput.setText(amount)
                            etInput.setSelection(etInput.text.length)
                        }
                    }
                }
            }
        }
    }

    /**
     * 按下返回键时的处理
     */
    @JvmStatic
    fun onKeyDown(): Boolean {
        var close = false
        if (vkKeyboard?.visibility == View.VISIBLE) hidden() else close = true
        return close
    }

    /**
     * 弹出时的处理
     */
    @JvmStatic
    fun shown() {
        if (vkKeyboard?.visibility != View.VISIBLE) {
            vkKeyboard?.isFocusable = true
            vkKeyboard?.isFocusableInTouchMode = true
            vkKeyboard?.startAnimation(enterAnim)
            vkKeyboard?.visibility = View.VISIBLE
        }
    }

    /**
     * 关闭时的处理
     */
    @JvmStatic
    fun hidden() {
        vkKeyboard?.startAnimation(exitAnim)
        vkKeyboard?.visibility = View.GONE
    }

}