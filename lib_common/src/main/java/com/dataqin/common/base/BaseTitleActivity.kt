package com.dataqin.common.base

import android.view.View
import androidx.viewbinding.ViewBinding
import com.dataqin.common.base.page.PageHandler
import com.dataqin.common.databinding.ActivityBaseBinding
import com.dataqin.common.utils.builder.TitleBuilder

/**
 * Created by WangYanBin on 2020/6/10.
 * 带标题的基类，将整一个xml插入容器
 * 带标题的详情页一般带有EmptyView，当子页面调取方法时添加
 */
abstract class BaseTitleActivity<VB : ViewBinding> : BaseActivity<VB>() {
    private val baseBinding by lazy { ActivityBaseBinding.inflate(layoutInflater) }
    protected val titleBuilder by lazy { TitleBuilder(this, baseBinding.titleContainer) } //标题栏
    protected val emptyLayout by lazy { PageHandler.getEmpty(baseBinding.flBaseContainer) }

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    override fun setContentView(view: View?) {
        baseBinding.flBaseContainer.addView(binding.root)
        super.setContentView(baseBinding.root)
    }

//    override fun initView() {
//        super.initView()
//        emptyLayout?.showLoading()
//    }
    // </editor-fold>

}