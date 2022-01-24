package com.dataqin.testnew.activity

import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.databinding.ActivityStartBinding

/**
 *  Created by wangyanbin
 *  启动页，通常可在此做一些基础判断
 *  1.进入引导页,权限页（6.0+的手机），引导页存储对应的参数值-ConfigHelper.storageBehavior(Constants.KEY_INITIAL, true)
 *  3.免登陆，进首页
 */
@Route(path = ARouterPath.StartActivity)
class StartActivity : BaseActivity<ActivityStartBinding>() {
    private val weakHandler by lazy {
        WeakHandler {
            navigation(ARouterPath.MainActivity).finish()
            false
        }
    }

    override fun initView() {
        super.initView()
//        statusBarBuilder.setTransparentStatus()//30api使用全屏刘海部分会黑色，改为透明白电池
//        //先判断是否是初始化过app
//        //如果是，验证权限是否具有，没有直接跳到权限申请页，权限申请页的下一页是引导页，具有直接跳转到引导页
//        //如果不是，验证是否需要免登陆，随后跳转到首页
//        if (!ConfigHelper.obtainBehavior(Constants.KEY_INITIAL)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                //先去权限页，再去引导页
//            } else {
//                //去引导页-引导页中获取定位
//            }
//        } else {
//            //做免登陆，去首页
//            LocationFactory.instance.start(object : LocationSubscriber() {
//                override fun onComplete() {
//                    super.onComplete()
//                    navigation(ARouterPath.MainActivity).finish()
//                    overridePendingTransition(0,0)
//                }
//            })
//        }
        weakHandler.sendEmptyMessageDelayed(0, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
//        LocationFactory.instance.stop()
    }

}