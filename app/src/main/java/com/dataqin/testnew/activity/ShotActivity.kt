package com.dataqin.testnew.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityShotBinding
import com.dataqin.testnew.utils.helper.CameraHelper

/**
 *  Created by wangyanbin
 *  拍照
 */
@Route(path = ARouterPath.ShotActivity)
class ShotActivity : BaseActivity<ActivityShotBinding>(), View.OnClickListener {

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()
//        val layoutParams = binding.cvFinder.getChildAt(0).layoutParams as FrameLayout.LayoutParams
//        layoutParams.height = Constants.SCREEN_HEIGHT
//        binding.cvFinder.getChildAt(0).layoutParams = layoutParams

//        var previewView = binding.cvFinder.getChildAt(0) as PreviewView
//        previewView.surfaceProvider
//        previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER)

//        previewView.post {
//            log("手机宽："+Constants.SCREEN_WIDTH+"\n手机高：" + Constants.SCREEN_HEIGHT+"\n相机宽：" + binding.cvFinder.getChildAt(0).width + "\n相机高：" + binding.cvFinder.getChildAt(0).height)
//        }

        //相机绑定页面生命周期
        CameraHelper.initialize(this, this, binding.cvFinder)
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnShot, binding.btnSwitch)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            //拍照
            R.id.btn_shot -> CameraHelper.takePicture()
            //镜头翻转
            R.id.btn_switch -> CameraHelper.toggleCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CameraHelper.onDestroy()
    }

}