package com.dataqin.testnew.activity

import android.view.Gravity
import android.view.WindowManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.databinding.ActivityTransBinding

/**
 *  Created by wangyanbin
 *  透明页-底部直接显示activity
 */
@Route(path = ARouterPath.TransActivity)
class TransActivity :BaseActivity<ActivityTransBinding>(){

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //即设定DecorView在PhoneWindow里的位置
        val layoutParams = window.decorView.layoutParams as WindowManager.LayoutParams
        layoutParams.gravity = Gravity.BOTTOM
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        windowManager.updateViewLayout(window.decorView, layoutParams)
    }

}