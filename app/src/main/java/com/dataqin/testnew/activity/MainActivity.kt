package com.dataqin.testnew.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.utils.helper.permission.OnPermissionCallBack
import com.dataqin.common.utils.helper.permission.PermissionHelper
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding

/**
 * Created by WangYanBin
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseTitleActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("CameraX").hideBack()
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnShot, binding.btnCamera)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_shot -> PermissionHelper.with(this)
                .setPermissionCallBack(object : OnPermissionCallBack {
                    override fun onPermissionListener(isGranted: Boolean) {
                        if (isGranted) {
                            navigation(ARouterPath.ShotActivity)
                        }
                    }
                }).getPermissions()
            R.id.btn_camera -> {
            }
        }
    }

}