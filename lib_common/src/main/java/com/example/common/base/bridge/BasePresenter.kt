package com.example.common.base.bridge

import android.content.Context
import com.example.common.BaseApplication
import com.example.common.bus.RxManager
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.ref.SoftReference

/**
 * author: wyb
 * date: 2018/7/26.
 * 基础Presenter层，可在此处加载初始化
 * 完成View和Presenter的关联性
 */
abstract class BasePresenter<T : BaseView> {
    private var view: SoftReference<T>? = null
    private var rxManager: RxManager? = null

    fun attachView(view: T?) {
        this.view = SoftReference(view!!)
        this.rxManager = RxManager()
    }

    fun detachView() {
        view?.clear()
        rxManager?.clear()
    }

    protected fun addDisposable(disposable: Disposable?) {
        if (null != disposable) {
            rxManager?.add(disposable)
        }
    }

    protected fun getView(): T {
        return view?.get()!!
    }

    protected fun getContext(): Context {
        return BaseApplication.instance.applicationContext
    }

}
