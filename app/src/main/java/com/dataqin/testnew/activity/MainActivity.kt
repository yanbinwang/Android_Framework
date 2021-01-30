package com.dataqin.testnew.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.widget.highlight.HeightLight
import com.dataqin.common.widget.highlight.interfaces.HeightLightInterface
import com.dataqin.common.widget.highlight.position.OnLeftPosCallback
import com.dataqin.common.widget.highlight.position.OnRightPosCallback
import com.dataqin.common.widget.highlight.position.OnTopPosCallback
import com.dataqin.common.widget.highlight.shape.CircleLightShape
import com.dataqin.common.widget.highlight.shape.RectLightShape
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding

/**
 * Created by WangYanBin
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var heightLight: HeightLight? = null

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        //界面初始化后直接显示高亮布局
////        if(hasFocus) heightLight.show()
//    }

    override fun initView() {
        super.initView()
        showNextTipViewOnCreated()//oncreate后调取
    }

    private fun showNextTipViewOnCreated(){
        heightLight = HeightLight(this)
            .anchor(binding.idContainer)//如果是Activity上增加引导层，不需要设置anchor
            .autoRemove(false)
            .enableNext()
            .setOnLayoutCallback { //界面布局完成添加tipview
                heightLight?.addHighLight(
                    binding.btnRightLight, R.layout.info_gravity_left_down,
                    OnLeftPosCallback(45f),
                    RectLightShape()
                )?.addHighLight(
                    binding.btnLight, R.layout.info_gravity_left_down,
                    OnRightPosCallback(5f),
                    CircleLightShape()
                )?.addHighLight(
                    binding.btnBottomLight, R.layout.info_gravity_left_down,
                    OnTopPosCallback(),
                    CircleLightShape()
                )
                //然后显示高亮布局
                heightLight?.show()
            }
            .setClickCallback {
                showToast("clicked and show next tip view by yourself")
                heightLight?.next()
            }
    }

}