package com.dataqin.testnew.activity

import android.content.Intent
import android.view.KeyEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.RequestCode
import com.dataqin.media.utils.helper.ScreenHelper
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityScreenBinding

/**
 *  Created by wangyanbin
 *  录屏
 *  路径通过广播拿取
 */
@Route(path = ARouterPath.ScreenActivity)
class ScreenActivity : BaseActivity<ActivityScreenBinding>(), View.OnClickListener {

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()
        ScreenHelper.initialize(this)
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnStart, binding.btnEnd)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> ScreenHelper.startScreen()
            R.id.btn_end -> {
                showToast("结束录屏")
                ScreenHelper.stopScreen()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.SERVICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                showToast("开始录屏")
                ScreenHelper.startScreenResult(resultCode, data)
            } else {
                //傻逼用户自己取消
                showToast("取消录屏")
            }
        }
    }

    //录屏需屏蔽返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        ScreenHelper.stopScreen()
    }

}