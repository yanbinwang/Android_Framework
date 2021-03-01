package com.dataqin.testnew.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.OrientationEventListener
import android.view.View
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.media.utils.helper.MediaFileHelper
import com.dataqin.media.widget.camera.CameraFactory
import com.dataqin.media.widget.camera.CameraPreview
import com.dataqin.media.widget.camera.callback.OnCameraListener
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityShotBinding
import java.io.File

/**
 *  Created by wangyanbin
 *  拍照页
 */
@SuppressLint("ClickableViewAccessibility")
@Route(path = ARouterPath.ShotActivity)
class ShotActivity : BaseActivity<ActivityShotBinding>(), View.OnClickListener {
    private var cameraOrientation = 0//相机角度
    private var filePath = ""//图片路径
    private var cameraPreview: CameraPreview? = null//相机容器
    private var orientationListener: OrientationEventListener? = null//监听手机旋转角度

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()
        val layoutParams = binding.rlTitle.layoutParams as RelativeLayout.LayoutParams
        layoutParams.topMargin = Constants.STATUS_BAR_HEIGHT
        binding.rlTitle.layoutParams = layoutParams
        //装载相机
        cameraPreview = CameraPreview(this)
        binding.flCameraPreview.addView(cameraPreview)
    }


    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.llMainLeft, binding.ivTake, binding.ivSwitch)

        CameraFactory.getInstance().setOnCameraListener(object : OnCameraListener {
            override fun onTakePictureSuccess(pictureFile: File?) {
                if (null != pictureFile) {
                    try {
                        filePath = pictureFile.path
                        val options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.RGB_565
                        val bitmap = MediaFileHelper.rotateBitmap(BitmapFactory.decodeFile(filePath, options), CameraFactory.getInstance().cameraId, cameraOrientation)
                        MediaFileHelper.saveBitmapToSd(bitmap, filePath, 100)
                        binding.ivShowImg.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                    } finally {
                        hideDialog()
                        binding.ivTake.isEnabled = true
                        binding.ivSwitch.isEnabled = true
                        binding.ivShowImg.visibility = View.VISIBLE
                    }
                } else {
                    onTakePictureFail(null)
                }
            }

            override fun onTakePictureFail(data: ByteArray?) {
                hideDialog()
                showToast("操作失败,请重试")
                binding.ivTake.isEnabled = true
                binding.ivSwitch.isEnabled = true
                binding.ivShowImg.visibility = View.GONE
            }
        })
        orientationListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                cameraOrientation = orientation
            }
        }
        cameraPreview?.setOnTouchListener { _, event ->
            CameraFactory.getInstance().focusOnTouch(event.x.toInt(), event.y.toInt(), binding.flCameraPreview)
            false
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_main_left -> finish()
            R.id.iv_take -> {
                showDialog()
                binding.ivTake.isEnabled = false
                binding.ivSwitch.isEnabled = false
                CameraFactory.getInstance().takePicture()
            }
            R.id.iv_switch -> {
                ENABLED(1000, binding.ivSwitch)
                CameraFactory.getInstance().switchCamera()
                binding.flCameraPreview.removeAllViews()
                binding.flCameraPreview.addView(cameraPreview)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        orientationListener?.disable()
    }

    override fun onDestroy() {
        super.onDestroy()
        CameraFactory.getInstance().onDestroy()
    }

}