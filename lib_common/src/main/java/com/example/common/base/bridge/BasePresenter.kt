package com.example.common.base.bridge

import android.app.Activity
import android.content.Context
import com.example.common.bus.RxManager
import io.reactivex.disposables.Disposable
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/**
 * author: wyb
 * date: 2018/7/26.
 * 基础Presenter层，可在此处加载初始化
 * 完成View和Presenter的关联性
 */
abstract class BasePresenter<T : BaseView> {
    protected var activity: WeakReference<Activity>? = null
    protected var context: WeakReference<Context>? = null
    protected var view: SoftReference<T>? = null
    private var rxManager: RxManager? = null

    fun attachView(activity: Activity?, context: Context?, view: T?) {
        this.activity = WeakReference(activity!!)
        this.context = WeakReference(context!!)
        this.view = SoftReference(view!!)
        this.rxManager = RxManager()
    }

    fun detachView() {
        activity?.clear()
        context?.clear()
        rxManager?.clear()
        view = null
    }

    protected fun addDisposable(disposable: Disposable?) {
        if (null != disposable) {
            rxManager?.add(disposable)
        }
    }

}
