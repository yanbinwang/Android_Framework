package com.dataqin.testnew.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.databinding.ActivityTestBinding
import com.dataqin.testnew.utils.CameraHelper


/**
 *  Created by wangyanbin
 *
 */
@Route(path = ARouterPath.TestActivity)
class TestActivity : BaseActivity<ActivityTestBinding>() {

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparentStatus()

//        binding.camera.setLifecycleOwner(this)
        CameraHelper.initialize(this, binding.camera)
    }

}