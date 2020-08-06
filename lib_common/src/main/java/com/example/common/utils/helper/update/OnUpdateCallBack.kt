package com.example.common.utils.helper.update

import io.reactivex.disposables.Disposable

/**
 * Created by WangYanBin on 2020/7/29.
 * 下载回调,返回对应的下载事务，用于销毁防止内存泄漏
 */
interface OnUpdateCallBack {

    //开始下载
    fun onStart(disposable: Disposable?)

    //完成下载
    fun onComplete()

}