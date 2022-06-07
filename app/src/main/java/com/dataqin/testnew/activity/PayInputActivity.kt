package com.dataqin.testnew.activity

import android.view.Gravity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.databinding.ActivityPayInputBinding
import com.dataqin.testnew.widget.keyboard.InputPopup

@Route(path = ARouterPath.PayInputActivity)
class PayInputActivity :BaseTitleActivity<ActivityPayInputBinding>(){
    private val inputPopup by lazy {
        InputPopup(
            this
        )
    }

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("唤起键盘").getDefault()
    }

    override fun initEvent() {
        super.initEvent()
        binding.btnShow.setOnClickListener { inputPopup.showAtLocation(it, Gravity.BOTTOM, 0, 0); }
    }

}