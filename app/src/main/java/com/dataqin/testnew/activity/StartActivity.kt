package com.dataqin.testnew.activity

import android.os.Build
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.amap.api.maps.model.LatLng
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.analysis.GsonUtil
import com.dataqin.common.utils.helper.ConfigHelper
import com.dataqin.map.utils.LocationFactory
import com.dataqin.map.utils.LocationSubscriber
import com.dataqin.testnew.databinding.ActivityStartBinding

/**
 *  Created by wangyanbin
 *  启动页，通常可在此做一些基础判断
 *  1.进入引导页,权限页（6.0+的手机），引导页存储对应的参数值-ConfigHelper.storageBehavior(Constants.KEY_INITIAL, true)
 *  3.免登陆，进首页
 */
@Route(path = ARouterPath.StartActivity)
class StartActivity : BaseActivity<ActivityStartBinding>() {

    override fun initView() {
        super.initView()
        LocationFactory.instance.start(object : LocationSubscriber() {
            override fun onSuccess(model: AMapLocation) {
                super.onSuccess(model)
                Constants.LATLNG_JSON = GsonUtil.objToJson(LatLng(model.latitude, model.longitude))
            }

            override fun onComplete() {
                super.onComplete()
                //先判断是否是初始化过app
                //如果是，验证权限是否具有，没有直接跳到权限申请页，权限申请页的下一页是引导页，具有直接跳转到引导页
                //如果不是，验证是否需要免登陆，随后跳转到首页
                if (!ConfigHelper.obtainBehavior(Constants.KEY_INITIAL)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //先去权限页，再去引导页
                    } else {
                        //去引导页
                    }
                } else {
                    //做免登陆，去首页
                }
                navigation(ARouterPath.MainActivity).finish()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        LocationFactory.instance.stop()
    }

}