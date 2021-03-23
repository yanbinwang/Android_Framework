package com.dataqin.testnew.activity

import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.RequestCode
import com.dataqin.common.utils.helper.permission.OnPermissionCallBack
import com.dataqin.common.utils.helper.permission.PermissionHelper
import com.dataqin.map.utils.LocationHelper
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding

/**
 * Created by WangYanBin
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseTitleActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("控制台").hideBack()
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnShot, binding.btnVideoTap, binding.btnRecord, binding.btnScreen)
        LocationHelper.settingGps(this)
    }

    private fun pageTesting(index: Int) {
        PermissionHelper.with(this).setPermissionCallBack(object : OnPermissionCallBack {
            override fun onPermissionListener(isGranted: Boolean) {
                if (isGranted) {
                    when (index) {
                        0 -> navigation(ARouterPath.ShotActivity)
                        1 -> navigation(ARouterPath.VideoTapActivity)
                        2 -> navigation(ARouterPath.RecordActivity)
                        3 -> navigation(ARouterPath.ScreenActivity)
                    }
                }
            }
        }).getPermissions()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_shot -> pageTesting(0)
            R.id.btn_video_tap -> pageTesting(1)
            R.id.btn_record -> pageTesting(2)
            R.id.btn_screen -> pageTesting(3)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                showToast("开启")
            } else {
                showToast("没开")
            }
        }
    }

}