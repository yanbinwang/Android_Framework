package com.dataqin.testnew.activity

import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.amap.api.maps.model.LatLng
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.RequestCode
import com.dataqin.common.utils.helper.permission.OnPermissionCallBack
import com.dataqin.common.utils.helper.permission.PermissionHelper
import com.dataqin.map.utils.LocationFactory
import com.dataqin.map.utils.LocationSubscriber
import com.dataqin.map.utils.helper.MapHelper
import com.dataqin.map.utils.helper.refresh
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding
import com.dataqin.testnew.presenter.MainPresenter
import com.dataqin.testnew.presenter.contract.MainContract

/**
 * Created by WangYanBin
 * 地图库采用高德地图，获取经纬度必须具备定位权限
 * 如果进应用就是地图，则在进首页前先给个软提示页面，列出所有权限问用户索要，如果还不接受，则直接进应用，在地图onload生命周期结束后，先移动到给定的默认位置，
 * 再进首页前弹出拦截的权限按钮进行权限的索要
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseTitleActivity<ActivityMainBinding>(), View.OnClickListener,
    MainContract.View {
    private val presenter by lazy { createPresenter(MainPresenter::class.java) }

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("控制台").hideBack()
//        presenter.setEmptyView(baseBinding.flBaseContainer)

        LocationFactory.instance.locationSubscriber = object : LocationSubscriber() {
            override fun onSuccess(model: AMapLocation) {
                super.onSuccess(model)
                if (normal) {
                    MapHelper.moveCamera(LatLng(model.latitude, model.longitude))
                } else {
                    //执行打卡
                }
            }

            override fun onFailed() {
                super.onFailed()
                if (normal) {
                    MapHelper.moveCamera()
                } else {
                    LocationFactory.instance.settingGps(activity.get()!!)
                }
            }
        }
//        LocationFactory.instance.start(this)
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
//        LocationFactory.instance.start(this)
//        LocationFactory.instance.onLocationCallBack = object :LocationFactory.OnLocationCallBack{
//            override fun onSuccess(model: AMapLocation) {
//                log(GsonUtil.objToJson(model)+"")
//            }
//
//            override fun onFailed() {
//                TODO("Not yet implemented")
//            }
//        }


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
        LocationFactory.instance.stop()
    }

}