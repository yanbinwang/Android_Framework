package com.dataqin.testnew.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.helper.CameraHelper
import com.dataqin.media.utils.helper.callback.OnTakePictureListener
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityShotBinding
import java.io.File

/**
 *  Created by wangyanbin
 *  拍照
 */
@Route(path = ARouterPath.ShotActivity)
class ShotActivity : BaseActivity<ActivityShotBinding>(), View.OnClickListener {
    private var filePath = ""

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()

        CameraHelper.initialize(this, binding.camera)
        val layoutParams = binding.llMainLeft.layoutParams as RelativeLayout.LayoutParams
        layoutParams.topMargin = Constants.STATUS_BAR_HEIGHT
        binding.llMainLeft.layoutParams = layoutParams
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnShot, binding.btnSwitch, binding.llMainLeft)

        CameraHelper.onTakePictureListener = object : OnTakePictureListener {
            override fun onStart() {
                showDialog()
            }

            override fun onSuccess(pictureFile: File) {
                filePath = pictureFile.path
                VISIBLE(binding.ivShot)
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.RGB_565
                val bitmap = BitmapFactory.decodeFile(filePath, options)
                MediaFileUtil.saveBitmapToSd(bitmap, filePath, 100)
                binding.ivShot.setImageBitmap(bitmap)
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
            R.id.btn_shot -> CameraHelper.takePicture()
            R.id.btn_switch -> CameraHelper.toggleCamera()
            R.id.ll_main_left -> {
                if (View.VISIBLE == binding.ivShot.visibility) {
                    GONE(binding.ivShot)
                    FileUtil.deleteDir(filePath)
                } else {
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FileUtil.deleteDir(filePath)
    }

}