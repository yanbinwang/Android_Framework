package com.dataqin.testnew.activity

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.os.Handler
import android.os.HandlerThread
import android.view.SurfaceHolder
import com.dataqin.common.base.BaseActivity
import com.dataqin.testnew.databinding.ActivityTestBinding
import java.lang.Exception


/**
 *  Created by wangyanbin
 *
 */
class TestActivity : BaseActivity<ActivityTestBinding>() {
    private val mCameraManager by lazy { getSystemService(Context.CAMERA_SERVICE) }
    private var mCameraId = CameraCharacteristics.LENS_FACING_FRONT.toString()
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mHandler: Handler? = null

    override fun initView() {
        super.initView()

        mSurfaceHolder = binding.surfaceview.holder
        mSurfaceHolder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                initCamera()
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                TODO("Not yet implemented")
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initCamera() {
        val handlerThread = HandlerThread("Camera2")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)
        try {
            mCameraId = CameraCharacteristics.LENS_FACING_FRONT.toString()

        } catch (e: Exception) {
            log("open camera failed." + e.message)
        }
    }
}