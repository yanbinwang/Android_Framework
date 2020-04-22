package com.example.common.utils.http

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * author: wyb
 * date: 2019/7/30.
 */
class RxManager {
    private val compositeDisposable = CompositeDisposable()

    //单纯的Observables 和 Subscribers管理
    fun add(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    //单个presenter生命周期结束，取消订阅和所有rxbus观察
    fun clear() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

}
