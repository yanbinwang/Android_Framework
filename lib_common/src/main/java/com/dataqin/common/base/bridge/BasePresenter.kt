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
    private var weakActivity: WeakReference<Activity>? = null
    private var weakContext: WeakReference<Context>? = null
    private var softView: SoftReference<BaseView>? = null
    private var softEmpty: SoftReference<EmptyLayout>? = null
    private var softRecycler: SoftReference<XRecyclerView>? = null
    private var rxManager = RxManager()

    // <editor-fold defaultstate="collapsed" desc="构造和内部方法">
    fun initialize(activity: Activity?, context: Context?, view: BaseView?) {
        this.weakActivity = WeakReference(activity)
        this.weakContext = WeakReference(context)
        this.softView = SoftReference(view)
    }

    fun addEmptyView(container: ViewGroup): EmptyLayout {
        this.softEmpty = SoftReference(PageHandler.getEmpty(container))
        showEmptyView()
        return softEmpty?.get()!!
    }

    fun addEmptyView(xRecyclerView: XRecyclerView): EmptyLayout {
        this.softEmpty = SoftReference(PageHandler.getListEmpty(xRecyclerView))
        this.softRecycler = SoftReference(xRecyclerView)
        showEmptyView()
        return softEmpty?.get()!!
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

    protected fun showEmptyView() {
        softEmpty?.get()?.showLoading()
    }

    protected fun hideEmptyView() {
        softRecycler?.get()?.finishRefreshing()
        softEmpty?.get()?.visibility = View.GONE
    }

    protected fun getView() = softView?.get() as? T

    protected fun getEmpty() = softEmpty?.get()

    protected fun getRecycler() = softRecycler?.get()

    protected fun getActivity() = weakActivity?.get()

    protected fun getContext() = weakContext?.get()
    // </editor-fold>

}