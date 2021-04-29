package com.dataqin.common.base.bridge

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dataqin.common.base.page.PageHandler
import com.dataqin.common.bus.RxManager
import com.dataqin.common.widget.empty.EmptyLayout
import com.dataqin.common.widget.xrecyclerview.XRecyclerView
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/**
 * author: wyb
 * date: 2018/7/26.
 * 基础Presenter层，可在此处加载初始化
 * 完成View和Presenter的关联性
 */
abstract class BasePresenter<T : BaseView> {
    private var weakActivity: WeakReference<Activity>? = null//引用的activity
    private var weakContext: WeakReference<Context>? = null//引用的context
    private var softView: SoftReference<BaseView>? = null//基础UI操作
    private var softEmpty: SoftReference<EmptyLayout>? = null//遮罩UI
    private var softRecycler: SoftReference<XRecyclerView>? = null//列表UI
    private var rxManager = RxManager()//请求管理器

    // <editor-fold defaultstate="collapsed" desc="构造和内部方法">
    fun initialize(activity: Activity?, context: Context?, view: BaseView?) {
        this.weakActivity = WeakReference(activity)
        this.weakContext = WeakReference(context)
        this.softView = SoftReference(view)
    }

    fun setEmptyView(container: ViewGroup) {
        this.softEmpty = SoftReference(PageHandler.getEmptyView(container))
    }

    fun setEmptyView(xRecyclerView: XRecyclerView) {
        this.softEmpty = SoftReference(PageHandler.getEmptyView(xRecyclerView))
        this.softRecycler = SoftReference(xRecyclerView)
    }

    fun getEmptyView() = softEmpty?.get()!!

    fun getRecyclerView() = softRecycler?.get()!!

    fun getString(resId: Int) = weakContext?.get()?.getString(resId)!!

    fun disposeView() {
        softRecycler?.get()?.finishRefreshing()
        softEmpty?.get()?.visibility = View.GONE
    }

    fun detachView() {
        weakActivity?.clear()
        weakContext?.clear()
        softView?.clear()
        softEmpty?.clear()
        softRecycler?.clear()
        rxManager.clear()
    }

    protected fun addDisposable(disposable: Disposable?) {
        if (null != disposable) {
            rxManager.add(disposable)
        }
    }

    protected fun getView() = softView?.get() as? T

    protected fun getActivity() = weakActivity?.get()

    protected fun getContext() = weakContext?.get()
    // </editor-fold>

}