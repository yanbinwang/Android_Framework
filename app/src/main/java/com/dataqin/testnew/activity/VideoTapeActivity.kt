package com.dataqin.testnew.activity

import android.annotation.SuppressLint
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.media.utils.helper.GSYVideoHelper
import com.dataqin.media.widget.camera.CameraFactory
import com.dataqin.media.widget.camera.CameraPreview
import com.dataqin.media.widget.camera.callback.OnVideoRecordListener
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityVideoTapeBinding

/**
 *  Created by wangyanbin
 *  录像
 */
@SuppressLint("ClickableViewAccessibility")
@Route(path = ARouterPath.VideoTapeActivity)
class VideoTapeActivity : BaseActivity<ActivityVideoTapeBinding>(), View.OnClickListener {
    private var isStop = true//是否正常停止
    private var cameraPreview: CameraPreview? = null
    private var filePath = ""//视频路径

    override fun initView() {
        super.initView()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        statusBarBuilder.setTransparentStatus()
        val layoutParams = binding.rlTitle.layoutParams as RelativeLayout.LayoutParams
        layoutParams.topMargin = Constants.STATUS_BAR_HEIGHT
        binding.rlTitle.layoutParams = layoutParams
        //装载相机
        cameraPreview = CameraPreview(this)
        binding.flCameraPreview.addView(cameraPreview)
        GSYVideoHelper.initialize(this, binding.pvVideo)
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.llMainLeft, binding.ivTake, binding.ivSwitch)

        CameraFactory.getInstance().setOnVideoRecordListener(object : OnVideoRecordListener{
            override fun onStartRecorder(path: String?) {
                if (null != path) {
                    GONE(binding.pvVideo)
                    VISIBLE(binding.flCameraPreview)
                    binding.ivTake.setBackgroundResource(R.mipmap.ic_stop_video)
                    filePath = path
                } else {
                    isStop = false
                    onStopRecorder()
                }
            }

            override fun onStopRecorder() {
                binding.ivTake.setBackgroundResource(R.mipmap.ic_start_video)
                if (isStop) {
                    GONE(binding.flCameraPreview)
                    VISIBLE(binding.pvVideo)
                    GSYVideoHelper.setUrl(filePath)
                } else {
                    GONE(binding.pvVideo)
                    VISIBLE(binding.flCameraPreview)
                    GSYVideoHelper.onDestroy()
                }
            }
        })
        cameraPreview?.setOnTouchListener { _, event ->
            CameraFactory.getInstance().focusOnTouch(event.x.toInt(), event.y.toInt(), binding.flCameraPreview)
            false
        }
    }

    override fun finish() {
        if (binding.pvVideo.visibility == View.VISIBLE) {
            FileUtil.deleteDir(filePath)
            GONE(binding.pvVideo)
            VISIBLE(binding.flCameraPreview)
        } else {
            super.finish()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_main_left -> finish()
            R.id.iv_take -> {
                if(binding.pvVideo.visibility == View.GONE){
                    CameraFactory.getInstance().startRecorder(cameraPreview?.holder?.surface)
                }else{
                    isStop = true
                    CameraFactory.getInstance().stopRecorder()
                }
            }
            R.id.iv_switch -> {
                ENABLED(1000, binding.ivSwitch)
                CameraFactory.getInstance().switchCamera()
                binding.flCameraPreview.removeAllViews()
                binding.flCameraPreview.addView(cameraPreview)
            }
        }
    }

}