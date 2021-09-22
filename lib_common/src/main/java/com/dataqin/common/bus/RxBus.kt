package com.dataqin.common.bus

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.processors.FlowableProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * author:wyb
 * 全局刷新工具
 */
class RxBus private constructor() {
    private var disposable: Disposable? = null//轮询
    private val processor: FlowableProcessor<Any> by lazy { PublishProcessor.create<Any>().toSerialized() }

    companion object {
        @JvmStatic
        val instance by lazy { RxBus() }
    }

    fun post(vararg objs: Any) {
        for (obj in objs) {
            processor.onNext(obj)
        }
    }

    @JvmOverloads
    fun interval(act: Consumer<Long>, second: Long = 1) {
        disposable = Flowable.interval(0, second, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(act)
    }

    fun dispose() {
        disposable?.dispose()
        disposable = null
    }

    fun <T> toFlowable(tClass: Class<T>): Flowable<T> {
        return processor.ofType(tClass)
    }

    fun <T> toDefaultFlowable(eventType: Class<T>, act: Consumer<T>): Disposable {
        return processor.ofType(eventType).compose { upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }.subscribe(act)
    }

    fun toFlowable(): Flowable<Any> {
        return processor
    }

    fun toFlowable(consumer: Consumer<RxEvent>): Disposable {
        return toFlowable(RxEvent::class.java).subscribe(consumer)
    }

    fun hasSubscribers(): Boolean {
        return processor.hasSubscribers()
    }

}
