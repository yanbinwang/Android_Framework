package com.dataqin.testnew.activity

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.view.KeyEvent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Extras
import com.dataqin.common.constant.RequestCode
import com.dataqin.media.service.ScreenRecordService
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityScreenBinding

/**
 *  Created by wangyanbin
 *  录屏
 *  路径通过广播拿取
 */
@Route(path = ARouterPath.ScreenActivity)
class ScreenActivity : BaseActivity<ActivityScreenBinding>(), View.OnClickListener {

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnStart, binding.btnEnd)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> {
                val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
                startActivityForResult(permissionIntent, RequestCode.SERVICE_REQUEST)
            }
            R.id.btn_end -> {
                showToast("结束录屏")
                stopService(Intent(this, ScreenRecordService::class.java))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.SERVICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                showToast("开始录屏")
                stopService(Intent(this, ScreenRecordService::class.java))//先停止，提高稳定性
                val service = Intent(this, ScreenRecordService::class.java)
                service.putExtra(Extras.RESULT_CODE, resultCode)
                service.putExtra(Extras.BUNDLE_BEAN, data)
                startService(service)
                moveTaskToBack(true)
            } else {
                //傻逼用户自己取消
                showToast("取消录屏")
            }
        }
    }

    //录屏需屏蔽返回键
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, ScreenRecordService::class.java))
    }

}