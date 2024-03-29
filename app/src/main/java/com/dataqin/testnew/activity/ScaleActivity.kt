package com.dataqin.testnew.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.base.utils.getInAnimation
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Extras
import com.dataqin.testnew.databinding.ActivityScaleBinding
import com.dataqin.testnew.widget.scale.ScaleAdapter
import com.dataqin.testnew.widget.scale.ScaleImageView

/**
 *  Created by wangyanbin
 *  伸缩页->本地图片路径慎用
 */
@Route(path = ARouterPath.ScaleActivity)
class ScaleActivity : BaseActivity<ActivityScaleBinding>() {
    private val index by lazy { intent.getIntExtra(Extras.FILE_INDEX, 0) }
    private val fileList by lazy { intent.getSerializableExtra(Extras.FILE_PATH) as ArrayList<*> }

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparent(false)
    }

    override fun initData() {
        super.initData()
        val list = ArrayList<ScaleImageView>()
        for (url in fileList) {
            val img = ScaleImageView(this)
            img.adjustViewBounds = true
            img.setOnClickListener { finish() }
            list.add(img)
        }
        binding.svpContainer.apply {
            adapter = ScaleAdapter(list, fileList)
            currentItem = index
            startAnimation(getInAnimation())
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

}