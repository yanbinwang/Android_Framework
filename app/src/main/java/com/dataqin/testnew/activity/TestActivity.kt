package com.dataqin.testnew.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.base.page.PageHandler
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.databinding.ActivityTestBinding

/**
 *  Created by wangyanbin
 *
 */
@Route(path = ARouterPath.TestActivity)
class TestActivity :BaseTitleActivity<ActivityTestBinding>(){

    override fun initView() {
        super.initView()
    }
}