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
 * 拍照，录像，录音，录屏，在产生文件时如果过大都是需要有过度动画的
 * 需要书写一个dialog全屏拦截用户手势，保证文件的产生
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

}