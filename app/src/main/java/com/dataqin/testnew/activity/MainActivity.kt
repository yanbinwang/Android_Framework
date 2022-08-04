package com.dataqin.testnew.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.bus.RxBus
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.RequestCode
import com.dataqin.common.utils.helper.permission.OnPermissionCallBack
import com.dataqin.common.utils.helper.permission.PermissionHelper
import com.dataqin.map.utils.helper.fadeIn
import com.dataqin.map.utils.helper.fadeOut
import com.dataqin.map.utils.helper.hidden
import com.dataqin.map.utils.helper.shown
import com.dataqin.media.service.ShotService
import com.dataqin.media.utils.helper.ScreenHelper
import com.dataqin.media.utils.helper.ShotHelper
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding
import com.dataqin.testnew.presenter.contract.MainContract


/**
 * Created by WangYanBin
 * 地图库采用高德地图，获取经纬度必须具备定位权限
 * 如果进应用就是地图，则在进首页前先给个软提示页面，列出所有权限问用户索要，如果还不接受，则直接进应用，在地图onload生命周期结束后，先移动到给定的默认位置，
 * 再进首页前弹出拦截的权限按钮进行权限的索要
 * cameraview_tts
 * http://zcpt-test.obs.cn-east-3.myhuaweicloud.com/uploads/2021/08/26/3AEF64F8FD3D4F109C4C24F2C6FCC3CF.mp4
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseTitleActivity<ActivityMainBinding>(), View.OnClickListener, MainContract.View {

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("控制台").hideBack()
        ScreenHelper.initialize(this)
        ShotHelper.initialize(this)
    }

    override fun onStart() {
        super.onStart()
        binding.rlRotate.enable()
    }

    override fun onStop() {
        super.onStop()
        binding.rlRotate.disable()
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnTest, binding.btnTest2, binding.btnTest3, binding.btnTest4, binding.btnTest5)
        addDisposable(RxBus.instance.toFlowable {
            when (it.getAction()) {
                Constants.APP_SHOT -> if (ShotService.launch) ShotService.capture(this) else ShotHelper.startScreenShot()
            }
        })
    }

    override fun getOperation() {
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_test -> binding.tvView.fadeIn()
            R.id.btn_test2 -> binding.tvView.fadeOut()
            R.id.btn_test3 -> binding.tvView.shown()
            R.id.btn_test4 -> binding.tvView.hidden()
            R.id.btn_test5 -> {
                PermissionHelper.with(this).setPermissionCallBack(object : OnPermissionCallBack {
                    override fun onPermission(isGranted: Boolean) {
                        if (isGranted) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(baseContext)) {
                                showToast("请授权上层显示")
                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                intent.data = Uri.parse("package:$packageName")
                                startActivity(intent)
                            } else ScreenHelper.startScreen()
                        }
                    }
                }).requestPermissions()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.SERVICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                ScreenHelper.startScreenResult(resultCode, data)
            }
        }
        if (requestCode == RequestCode.MEDIA_REQUEST) {
            if (resultCode == RESULT_OK) {
                ShotHelper.startScreenShot(resultCode, data)
            }
        }
    }

}