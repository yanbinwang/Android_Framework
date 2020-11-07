package com.dataqin.common.bus

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 * author:wyb
 * 全局刷新工具
 */
class RxBus private constructor() {
    private val mBus: FlowableProcessor<Any> = PublishProcessor.create<Any>().toSerialized()

    companion object {
        @JvmStatic
        val instance: RxBus by lazy {
            RxBus()
        }
    }

    fun post(obj: Any) {
        mBus.onNext(obj)
    }

    fun <T> toFlowable(tClass: Class<T>): Flowable<T> {
        return mBus.ofType(tClass)
    }

    fun toFlowable(): Flowable<Any> {
        return mBus
    }

    fun hasSubscribers(): Boolean {
        return mBus.hasSubscribers()
    }

    fun <T> toDefaultFlowable(eventType: Class<T>, act: Consumer<T>): Disposable {
        return mBus.ofType(eventType).compose { upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }.subscribe(act)
    }

}
