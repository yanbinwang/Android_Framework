package com.dataqin.testnew.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.base.utils.AnimationLoader
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Extras
import com.dataqin.common.imageloader.ImageLoader
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityScaleBinding
import com.dataqin.testnew.widget.scale.ScaleAdapter
import com.dataqin.testnew.widget.scale.ScaleImageView

/**
 *  Created by wangyanbin
 *  伸缩页
 */
@Route(path = ARouterPath.ScaleActivity)
class ScaleActivity : BaseActivity<ActivityScaleBinding>() {
    private val pathList by lazy { intent.getSerializableExtra(Extras.FILE_PATH) as ArrayList<*> }

    override fun initView() {
        super.initView()
        statusBarBuilder.setTransparent(false)
    }

    override fun initData() {
        super.initData()
        val list = ArrayList<ScaleImageView>()
        for (url in pathList) {
            val img = ScaleImageView(this)
            img.setOnClickListener { finish() }
            ImageLoader.instance.displayImage(
                img, url as String,
                R.drawable.shape_scale_loading, R.drawable.shape_loading_normal, null
            )
            list.add(img)
        }
        val adapter = ScaleAdapter(list)
        binding.svpContainer.adapter = adapter
        binding.svpContainer.startAnimation(AnimationLoader.getInAnimation(this))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

}