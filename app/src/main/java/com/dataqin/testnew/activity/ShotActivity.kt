package com.dataqin.testnew.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityShotBinding
import com.dataqin.media.utils.factory.CameraFactory
import com.dataqin.media.utils.factory.callback.OnTakePictureListener
import java.io.File

/**
 *  Created by wangyanbin
 *  拍照
 */
@Route(path = ARouterPath.ShotActivity)
class ShotActivity : BaseActivity<ActivityShotBinding>(), View.OnClickListener {

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()
        CameraFactory.instance.initialize(this, binding.camera)
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnShot, binding.btnSwitch)

        CameraFactory.instance.onTakePictureListener = object : OnTakePictureListener {
            override fun onSuccess(pictureFile: File) {
                showToast("拍摄完成\n状态：成功\n地址：" + pictureFile.path)
            }

            override fun onFailed() {
                showToast("拍摄完成\n状态：失败")
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_shot -> CameraFactory.instance.takePicture()
            R.id.btn_switch -> CameraFactory.instance.toggleCamera()
        }
    }

}