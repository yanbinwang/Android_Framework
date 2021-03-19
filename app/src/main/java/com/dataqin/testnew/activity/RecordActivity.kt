package com.dataqin.testnew.activity

import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.media.utils.helper.RecorderHelper
import com.dataqin.media.utils.helper.callback.OnRecorderListener
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityRecordBinding

/**
 *  Created by wangyanbin
 *  录音
 */
@Route(path = ARouterPath.RecordActivity)
class RecordActivity : BaseActivity<ActivityRecordBinding>(), View.OnClickListener {
    private var filePath = ""

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()

        val layoutParams = binding.llMainLeft.layoutParams as RelativeLayout.LayoutParams
        layoutParams.topMargin = Constants.STATUS_BAR_HEIGHT
        binding.llMainLeft.layoutParams = layoutParams
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnStart, binding.btnEnd, binding.btnPlay, binding.llMainLeft)

        RecorderHelper.onRecorderListener = object : OnRecorderListener {
            override fun onStartRecord(path: String) {
                showToast("开始录音")
                filePath = path
            }

            override fun onStopRecord() {
                showToast("结束录音")
                RecorderHelper.setDataSource(filePath)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> RecorderHelper.startRecord()
            R.id.btn_end -> RecorderHelper.stopRecord()
            R.id.btn_play -> RecorderHelper.onStart()
            R.id.ll_main_left -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RecorderHelper.onDestroy()
        FileUtil.deleteDir(filePath)
    }

}