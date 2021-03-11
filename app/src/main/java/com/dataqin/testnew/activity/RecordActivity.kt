package com.dataqin.testnew.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.media.utils.factory.RecorderFactory
import com.dataqin.media.utils.factory.callback.OnRecorderListener
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
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnStart, binding.btnEnd, binding.btnPlay)

        RecorderFactory.instance.onRecorderListener = object : OnRecorderListener {
            override fun onStartRecord(path: String) {
                filePath = path
            }

            override fun onStopRecord() {
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> {
                showToast("开始录音")
                RecorderFactory.instance.startRecord()
            }
            R.id.btn_end -> {
                showToast("结束录音")
                RecorderFactory.instance.stopRecord()
            }
            R.id.btn_play -> {
                showToast("播放")
                RecorderFactory.instance.setDataSource(filePath)
                RecorderFactory.instance.onStart()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RecorderFactory.instance.onDestroy()
    }

}