package com.example.common.bus

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableTransformer
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Created by WangYanBin on 2020/9/7.
 */
object RxSchedulers {

    fun <T> ioMain(): FlowableTransformer<T, T> {
        return FlowableTransformer { upstream: Flowable<T> ->
            upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        }
    }

}