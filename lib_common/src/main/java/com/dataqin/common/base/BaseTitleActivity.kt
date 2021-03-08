package com.dataqin.common.base

import android.view.View
import androidx.viewbinding.ViewBinding
import com.dataqin.common.base.page.PageHandler
import com.dataqin.common.databinding.ActivityBaseBinding
import com.dataqin.common.utils.builder.TitleBuilder
import com.dataqin.common.widget.empty.EmptyLayout

/**
 * Created by WangYanBin on 2020/6/10.
 * 带标题的基类，将整一个xml插入容器
 * 带标题的详情页一般带有EmptyView，当子页面调取方法时添加
 */
abstract class BaseTitleActivity<VB : ViewBinding> : BaseActivity<VB>() {
    private val baseBinding by lazy { ActivityBaseBinding.inflate(layoutInflater) }
    protected val titleBuilder by lazy { TitleBuilder(this, baseBinding.titleContainer) } //标题栏
    protected val emptyLayout by lazy { EmptyLayout(this) }
    protected var drawEmpty = false

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    override fun setContentView(view: View?) {
        baseBinding.flBaseContainer.addView(binding.root)
        super.setContentView(baseBinding.root)
    }

    override fun initView() {
        super.initView()
        //不需要的界面手动关闭，看UI设计
        if (drawEmpty) {
            showLoading()
        }
    }

    protected fun showLoading() {
        initialize()
        emptyLayout.showLoading()
    }

    protected fun showEmpty(resId: Int = -1, emptyText: String = "") {
        initialize()
        emptyLayout.showEmpty(resId, emptyText)
    }

    protected fun showError() {
        initialize()
        emptyLayout.showError()
    }

    protected fun setEmptyState(msg: String?, resId: Int = -1, emptyText: String = "") {
        initialize()
        PageHandler.setEmptyState(emptyLayout, msg, resId, emptyText)
    }

    protected fun hideEmpty() {
        initialize()
        GONE(emptyLayout)
    }

    private fun initialize() {
        if (baseBinding.flBaseContainer.childCount <= 1) {
            emptyLayout.draw()
            baseBinding.flBaseContainer.addView(emptyLayout)
        }
    }
    // </editor-fold>

}