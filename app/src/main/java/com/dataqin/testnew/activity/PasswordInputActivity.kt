package com.dataqin.testnew.activity

import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.widget.xrecyclerview.callback.OnItemClickListener
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityPasswordInputBinding
import com.dataqin.testnew.widget.keyboard.VirtualKeyboardAdapter

/**
 * 密码输入页
 */
@Route(path = ARouterPath.PasswordInputActivity)
class PasswordInputActivity : BaseTitleActivity<ActivityPasswordInputBinding>() {
    private val enterAnim by lazy { AnimationUtils.loadAnimation(this, R.anim.set_password_bottom_in) }//进入动画
    private val exitAnim by lazy { AnimationUtils.loadAnimation(this, R.anim.set_password_bottom_out) } //进退出动画

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("输入密码").getDefault()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        try {
            val setShowSoftInputOnFocus = EditText::class.java.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
            setShowSoftInputOnFocus.isAccessible = true
            setShowSoftInputOnFocus.invoke(binding.etAmount, false)
        } catch (e: Exception) {
        }
    }

    override fun initEvent() {
        super.initEvent()
        binding.etAmount.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) shown() else hidden() }
        binding.etAmount.setOnClickListener { shown() }
        binding.vkKeyboard.back.setOnClickListener { hidden() }
        val valueList = binding.vkKeyboard.valueList
        (binding.vkKeyboard.recyclerView.adapter as VirtualKeyboardAdapter).onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                //点击0~9按钮
                if (position < 11 && position != 9) {
                    var amount = getParameters(binding.etAmount)
                    amount += valueList[position]["name"]
                    binding.etAmount.setText(amount)
                    binding.etAmount.setSelection(binding.etAmount.text.length)
                } else {
                    //点击退格键
                    if (position == 9) {
                        var amount = getParameters(binding.etAmount)
                        if (!amount.contains(".")) {
                            amount += valueList[position]["name"]
                            binding.etAmount.setText(amount)
                            binding.etAmount.setSelection(binding.etAmount.text.length)
                        }
                    }
                    //点击退格键
                    if (position == 11) {
                        var amount = getParameters(binding.etAmount)
                        if (amount.isNotEmpty()) {
                            amount = amount.substring(0, amount.length - 1)
                            binding.etAmount.setText(amount)
                            binding.etAmount.setSelection(binding.etAmount.text.length)
                        }
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.vkKeyboard.visibility == View.VISIBLE) hidden() else finish()
            return true
        }
        return false
    }

    private fun shown() {
        if (binding.vkKeyboard.visibility != View.VISIBLE) {
            binding.vkKeyboard.isFocusable = true
            binding.vkKeyboard.isFocusableInTouchMode = true
            binding.vkKeyboard.startAnimation(enterAnim)
            VISIBLE(binding.vkKeyboard)
        }
    }

    private fun hidden() {
        binding.vkKeyboard.startAnimation(exitAnim)
        GONE(binding.vkKeyboard)
    }

}