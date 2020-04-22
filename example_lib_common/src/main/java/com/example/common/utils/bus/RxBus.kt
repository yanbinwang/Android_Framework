package com.example.common.utils.bus

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers

/**
 * author:wyb
 * 全局刷新工具
 */
class RxBus private constructor() {
    private val mBus: FlowableProcessor<Any> = PublishProcessor.create<Any>().toSerialized()

    companion object {
        private var instance: RxBus? = null

        @Synchronized
        fun getInstance(): RxBus {
            if (instance == null) {
                instance = RxBus()
            }
            return instance!!
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
