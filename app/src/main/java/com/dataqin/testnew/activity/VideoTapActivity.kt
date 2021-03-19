package com.dataqin.testnew.activity

import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.media.utils.helper.CameraHelper
import com.dataqin.media.utils.helper.GSYVideoHelper
import com.dataqin.media.utils.helper.callback.OnTakePictureListener
import com.dataqin.media.utils.helper.callback.OnVideoRecordListener
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityVideoTapBinding
import com.dataqin.testnew.widget.ShotDialog
import java.io.File

/**
 *  Created by wangyanbin
 *  录像
 */
@Route(path = ARouterPath.VideoTapActivity)
class VideoTapActivity : BaseActivity<ActivityVideoTapBinding>(), View.OnClickListener {
    private var filePath = ""
    private val shotDialog by lazy { ShotDialog(this) }

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()

        CameraHelper.initialize(this, binding.camera)
        GSYVideoHelper.initialize(this, binding.pvVideo)
        val layoutParams = binding.llMainLeft.layoutParams as RelativeLayout.LayoutParams
        layoutParams.topMargin = Constants.STATUS_BAR_HEIGHT
        binding.llMainLeft.layoutParams = layoutParams
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnStart, binding.btnStop, binding.btnCatch, binding.btnSwitch, binding.llMainLeft)

        CameraHelper.onVideoRecordListener = object : OnVideoRecordListener {
            override fun onStartRecorder() {
                showToast("开始录像")
            }

            override fun onStopRecorder(path: String?) {
                hideDialog()
                if (!TextUtils.isEmpty(path)) {
                    filePath = path!!
                    VISIBLE(binding.pvVideo)
                    GSYVideoHelper.setUrl(filePath)
                } else {
                    showToast("操作失败，请重试")
                }
            }
        }
        CameraHelper.onTakePictureListener = object : OnTakePictureListener {
            override fun onStart() {
                showDialog()
            }

            override fun onSuccess(pictureFile: File) {
                shotDialog.show(pictureFile.path)
            }

            override fun onFailed() {
                showToast("操作失败，请重试")
            }

            override fun onComplete() {
                hideDialog()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> CameraHelper.startRecorder(this)
            R.id.btn_stop -> {
                showDialog()
                CameraHelper.stopRecorder()
            }
            R.id.btn_catch -> CameraHelper.takePicture(true)
            R.id.btn_switch -> CameraHelper.toggleCamera()
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

    override fun onPause() {
        GSYVideoHelper.onPause()
        super.onPause()
    }

    override fun onResume() {
        GSYVideoHelper.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoHelper.onDestroy()
        FileUtil.deleteDir(filePath)
    }

}