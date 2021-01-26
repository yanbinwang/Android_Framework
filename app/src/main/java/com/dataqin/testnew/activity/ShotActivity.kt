package com.dataqin.testnew.activity

import android.view.View
import androidx.camera.view.PreviewView
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
class ShotActivity : BaseActivity<ActivityShotBinding>(),View.OnClickListener {

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()
        //相机绑定页面生命周期
        CameraHelper.initialize(this, this, binding.cvFinder)

//        binding.cvFinder.scaleType = PreviewView.ScaleType.FILL_CENTER
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnShot, binding.btnShot)
    }

    override fun onClick(v: View?) {
        when(v?.id){
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