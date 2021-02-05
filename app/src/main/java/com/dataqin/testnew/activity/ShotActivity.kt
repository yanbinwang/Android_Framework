package com.dataqin.testnew.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityShotBinding

/**
 *  Created by wangyanbin
 *
 */
@Route(path = ARouterPath.ShotActivity)
class ShotActivity : BaseActivity<ActivityShotBinding>(), View.OnClickListener {

    override fun initView() {
        super.initView()
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnShot, binding.btnSwitch)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_shot -> {
            }
            R.id.btn_switch -> {
            }
        }
    }


}