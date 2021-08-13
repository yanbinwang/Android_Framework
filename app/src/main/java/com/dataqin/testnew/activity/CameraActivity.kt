package com.dataqin.testnew.activity

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.OrientationEventListener
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityCameraBinding
import com.dataqin.testnew.widget.camera.CameraFactory
import com.dataqin.testnew.widget.camera.CameraPreview

/**
 *  Created by wangyanbin
 *
 */
@SuppressLint("ClickableViewAccessibility")
@Route(path = ARouterPath.CameraActivity)
class CameraActivity : BaseActivity<ActivityCameraBinding>(), View.OnClickListener {
    private var cameraOrientation = 0//相机角度
    private val cameraPreview by lazy { CameraPreview(this) }//相机容器
    private val orientationListener by lazy {
        object : OrientationEventListener(this) {
            //监听手机旋转角度
            override fun onOrientationChanged(orientation: Int) {
                cameraOrientation = orientation
            }
        }
    }

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()
        CameraFactory.instance.initialize(binding.flPreview, cameraPreview)
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnReset, binding.btnToggle, binding.btnTouch)
        cameraPreview.setOnTouchListener { _, event ->
            CameraFactory.instance.focusOnTouch(event.x.toInt(), event.y.toInt(), binding.flPreview)
            false
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        CameraFactory.instance.onTouchEvent(event)
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_reset -> CameraFactory.instance.reset()
            R.id.btn_toggle -> CameraFactory.instance.toggleCamera(v)
            R.id.btn_touch -> CameraFactory.instance.focusing()
        }
    }

    override fun onPause() {
        super.onPause()
        orientationListener.disable()
    }

    override fun onDestroy() {
        super.onDestroy()
        CameraFactory.instance.onDestroy()
    }

}