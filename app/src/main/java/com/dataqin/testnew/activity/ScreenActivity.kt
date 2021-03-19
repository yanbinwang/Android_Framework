package com.dataqin.testnew.activity

import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.bus.RxBus
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.RequestCode
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.helper.GSYVideoHelper
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
    private var filePath = ""

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()

        ScreenHelper.initialize(this)
        GSYVideoHelper.initialize(this, binding.pvVideo)
        val layoutParams = binding.llMainLeft.layoutParams as RelativeLayout.LayoutParams
        layoutParams.topMargin = Constants.STATUS_BAR_HEIGHT
        binding.llMainLeft.layoutParams = layoutParams
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnStart, binding.btnEnd, binding.llMainLeft)

        addDisposable(RxBus.instance.toFlowable {
            when (it.getAction()) {
                Constants.APP_SCREEN_FILE_CREATE -> {
                    hideDialog()
                    filePath = it.getStringExtra()!!
                    VISIBLE(binding.pvVideo)
                    GSYVideoHelper.setUrl(filePath)
                    binding.btnEnd.isEnabled = false
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> {
                //预留1G的存储空间
                if (MediaFileUtil.scanDisk()) {
                    ScreenHelper.startScreen()
                    binding.btnEnd.isEnabled = true
                } else {
                    showToast("磁盘空间不足")
                }
            }
            R.id.btn_end -> {
                showDialog()
                showToast("结束录屏")
                ScreenHelper.stopScreen()
            }
            R.id.ll_main_left -> {
                if (View.VISIBLE == binding.pvVideo.visibility) {
                    GONE(binding.pvVideo)
                    GSYVideoHelper.onDestroy()
                    FileUtil.deleteDir(filePath)
                } else {
                    finish()
                }
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
        GSYVideoHelper.onDestroy()
        FileUtil.deleteDir(filePath)
    }

}