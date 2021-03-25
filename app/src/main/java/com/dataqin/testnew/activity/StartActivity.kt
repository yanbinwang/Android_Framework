package com.dataqin.testnew.activity

import android.os.Build
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.helper.ConfigHelper
import com.dataqin.testnew.databinding.ActivityStartBinding

/**
 *  Created by wangyanbin
 *  启动页，通常可在此做一些基础判断
 *  1.进入引导页,权限页（6.0+的手机），引导页存储对应的参数值-ConfigHelper.storageBehavior(Constants.KEY_INITIAL, true)
 *  3.免登陆，进首页
 */
class StartActivity : BaseActivity<ActivityStartBinding>() {

    override fun initView() {
        super.initView()
        //想判断是否是第一次安装app
        //如果是，验证权限是否具有，没有直接跳到权限申请页，权限申请页的下一页是引导页，具有直接跳转到引导页
        //如果不是，验证是否需要免登陆，随后跳转到首页
        if(!ConfigHelper.obtainBehavior(Constants.KEY_INITIAL)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //先去权限页，再去引导页
            } else {
                //去引导页
            }
        }else{
            //做免登陆，去首页
        }
    }

}