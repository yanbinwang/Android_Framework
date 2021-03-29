package com.dataqin.testnew.activity

import android.graphics.drawable.Drawable
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.base.utils.AnimationLoader
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Extras
import com.dataqin.common.imageloader.ImageLoader
import com.dataqin.common.imageloader.glide.callback.GlideRequestListener
import com.dataqin.common.widget.dialog.MessageDialog
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
    private val fileList by lazy { intent.getSerializableExtra(Extras.FILE_PATH) as ArrayList<*> }
    private val messageDialog by lazy { MessageDialog.with(this).setParams("图片加载中，请稍后......") }
    private var count = 0

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
            ImageLoader.instance.displayImage(img, url as String, R.drawable.shape_scale_loading, R.drawable.shape_loading_normal, object : GlideRequestListener<Drawable?>() {
                    override fun onStart() {
                        showDialog()
                    }

                    override fun onComplete(resource: Drawable?) {
                        hideDialog()
                    }
                }
            )
            list.add(img)
        }
        val adapter = ScaleAdapter(list)
        binding.svpContainer.adapter = adapter
        binding.svpContainer.startAnimation(AnimationLoader.getInAnimation(this))
    }

    override fun showDialog(flag: Boolean) {
        if (fileList.size > 1 && !messageDialog.isShowing) {
            count = 0
            messageDialog.show()
        }
    }

    override fun hideDialog() {
        if (fileList.size > 1) {
            count++
            if (count >= fileList.size - 1) messageDialog.hide()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

}