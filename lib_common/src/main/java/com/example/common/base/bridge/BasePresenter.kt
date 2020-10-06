package com.example.common.base.bridge

import android.app.Activity
import android.content.Context
import com.example.common.bus.RxManager
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
    private var rxManager = RxManager()

    // <editor-fold defaultstate="collapsed" desc="构造和内部方法">
    fun initialize(activity: Activity?, context: Context?, view: BaseView?) {
        this.weakActivity = WeakReference(activity)
        this.weakContext = WeakReference(context)
        this.softView = SoftReference(view)
    }

    fun detachView() {
        weakActivity?.clear()
        weakContext?.clear()
        softView?.clear()
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
