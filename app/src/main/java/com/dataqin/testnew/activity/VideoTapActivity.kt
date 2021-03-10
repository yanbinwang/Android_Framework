package com.dataqin.testnew.activity

import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.media.utils.factory.CameraFactory
import com.dataqin.media.utils.factory.callback.OnTakePictureListener
import com.dataqin.media.utils.factory.callback.OnVideoRecordListener
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityVideoTapBinding
import java.io.File

/**
 *  Created by wangyanbin
 *  录像
 */
@Route(path = ARouterPath.VideoTapActivity)
class VideoTapActivity : BaseActivity<ActivityVideoTapBinding>(), View.OnClickListener {

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()
        CameraFactory.instance.initialize(this, binding.camera)
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnStart, binding.btnStop, binding.btnCatch, binding.btnSwitch)

        CameraFactory.instance.onVideoRecordListener = object : OnVideoRecordListener {

            override fun onStartRecorder() {
                showToast("开始录制")
            }

            override fun onStopRecorder(path: String?) {
                if (!TextUtils.isEmpty(path)) {
                    showToast("结束录制\n状态：成功\n地址：$path")
                } else {
                    showToast("结束录制\n状态：失败")
                }
            }

        }
        CameraFactory.instance.onTakePictureListener = object : OnTakePictureListener {
            override fun onStart() {
                showDialog()
            }

            override fun onSuccess(pictureFile: File) {
                showToast("抓拍完成\n状态：成功\n地址：" + pictureFile.path)
            }

            override fun onFailed() {
                showToast("抓拍完成\n状态：失败")
            }

            override fun onComplete() {
                hideDialog()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> CameraFactory.instance.startRecorder()
            R.id.btn_stop -> CameraFactory.instance.stopRecorder()
            R.id.btn_catch -> CameraFactory.instance.takePicture(true)
            R.id.btn_switch -> CameraFactory.instance.toggleCamera()
        }
    }

}