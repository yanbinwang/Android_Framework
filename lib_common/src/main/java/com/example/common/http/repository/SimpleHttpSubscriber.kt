package com.example.common.http.repository

/**
 * Created by WangYanBin on 2020/6/19.
 * 项目中使用的网络请求回调对象（不包外层对象）
 */
abstract class SimpleHttpSubscriber<T> : ResourceSubscriber<T>() {

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    override fun doResult(data: T?, throwable: Throwable?) {
        if (null != data) {
            onSuccess(data)
        } else {
            onFailed(throwable)
        }
    }
    // </editor-fold>

    /**
     * 请求成功，直接回调对象
     */
    protected abstract fun onSuccess(data: T?)

    /**
     * 请求失败，获取失败原因
     */
    protected abstract fun onFailed(e: Throwable?)

}