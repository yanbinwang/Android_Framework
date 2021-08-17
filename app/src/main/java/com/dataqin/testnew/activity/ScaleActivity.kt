package com.dataqin.testnew.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.base.utils.getInAnimation
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
            img.setOnClickListener { finish() }
            ImageLoader.instance.displayImage(img, url as String, R.drawable.shape_scale_loading, R.drawable.shape_image_loading, null)
            list.add(img)
        }
        binding.svpContainer.adapter = ScaleAdapter(list)
        binding.svpContainer.currentItem = index
        binding.svpContainer.startAnimation(getInAnimation())
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

}