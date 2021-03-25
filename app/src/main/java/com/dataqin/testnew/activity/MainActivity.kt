package com.dataqin.testnew.activity

import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.RequestCode
import com.dataqin.common.utils.helper.permission.OnPermissionCallBack
import com.dataqin.common.utils.helper.permission.PermissionHelper
import com.dataqin.map.utils.helper.LocationHelper
import com.dataqin.map.utils.helper.refresh
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding
import com.dataqin.testnew.presenter.MainPresenter
import com.dataqin.testnew.presenter.contract.MainContract

/**
 * Created by WangYanBin
 * 如果进应用就是地图，则在地图onload生命周期结束后，先移动到默认位置，然后调取整体权限获取接口，走顺序弹出通知的逻辑
 * 已经具备权限了，直接调取定位，未具备根据顺序弹出对应的弹框。权限的优先级最高
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseTitleActivity<ActivityMainBinding>(), View.OnClickListener,MainContract.View {
    private val presenter by lazy { createPresenter(MainPresenter::class.java) }

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("控制台").hideBack()
//        presenter.setEmptyView(baseBinding.flBaseContainer)
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnShot, binding.btnVideoTap, binding.btnRecord, binding.btnScreen)

//        presenter.getEmptyView()?.showError()
//        presenter.getEmptyView()?.setOnEmptyRefreshListener(object : OnEmptyRefreshListener {
//            override fun onRefreshListener() {
//                presenter.getOperation()
//            }
//        })
//        LocationHelper.settingGps(this)
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
            R.id.btn_shot -> v.refresh()
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

    override fun getOperation() {

    }

    override fun onDestroy() {
        super.onDestroy()
        LocationHelper.destroy()
    }

}