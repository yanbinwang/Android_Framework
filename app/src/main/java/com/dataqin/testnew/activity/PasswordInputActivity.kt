package com.dataqin.testnew.activity

import android.view.KeyEvent
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.databinding.ActivityPasswordInputBinding
import com.dataqin.testnew.widget.keyboard.KeyboardHelper

/**
 * 密码输入页
 */
@Route(path = ARouterPath.PasswordInputActivity)
class PasswordInputActivity : BaseTitleActivity<ActivityPasswordInputBinding>() {

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("输入密码").getDefault()
        KeyboardHelper.initialize(this, binding.etAmount, binding.vkKeyboard)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (KeyboardHelper.onKeyDown()) finish()
            return true
        }
        return false
    }

}