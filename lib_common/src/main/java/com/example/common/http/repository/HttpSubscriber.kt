package com.example.common.http.repository

import com.alibaba.android.arouter.launcher.ARouter
import com.example.common.bus.RxBus.Companion.instance
import com.example.common.bus.RxEvent
import com.example.common.constant.ARouterPath
import com.example.common.constant.Constants
import com.example.common.utils.analysis.GsonUtil.jsonToObj
import com.example.common.utils.helper.AccountHelper
import io.reactivex.rxjava3.subscribers.ResourceSubscriber
import retrofit2.HttpException

/**
 * Created by WangYanBin on 2020/6/19.
 * 项目中使用的网络请求回调对象
 */
abstract class HttpSubscriber<T> : ResourceSubscriber<ApiResponse<T>>() {

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    override fun onNext(apiResponse: ApiResponse<T>?) {
        doResult(apiResponse)
    }

    override fun onError(throwable: Throwable?) {
        try {
            val responseBody = (throwable as? HttpException)?.response()?.errorBody()
            if (null != responseBody) {
                val baseModel = jsonToObj(responseBody.string(), ApiResponse::class.java)
                doResult(baseModel as? ApiResponse<T>?, throwable)
            } else {
                doResult(null, throwable)
            }
        } catch (e: Exception) {
            doResult(null, e)
        }
        //在一个正确运行的事件序列中,onComplete()和onError()有且只有一个，并且是事件序列中的最后一个
        //onComplete()和onError()二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个
        //故手动在处理后回调一次onComplete()销毁该次事务，保证onComplete()会被调用（用于项目中请求结束的一些操作）
        onComplete()
    }

    override fun onComplete() {
        if (!isDisposed) {
            dispose()
        }
    }

    private fun doResult(apiResponse: ApiResponse<T>? = null, throwable: Throwable? = null) {
        if (null != apiResponse) {
            val msg = apiResponse.msg
            val e = apiResponse.e
            if (0 == e) {
                onSuccess(apiResponse.data)
            } else {
                //账号还没有登录，解密失败，重新获取
                if (100005 == e || 100008 == e) {
                    AccountHelper.signOut()
                    instance.post(RxEvent(Constants.APP_USER_LOGIN_OUT))
                    ARouter.getInstance().build(ARouterPath.LoginActivity).navigation()
                }
                //账号被锁定--进入账号锁定页（其余页面不关闭）
                if (100002 == e) {
                    ARouter.getInstance().build(ARouterPath.UnlockIPActivity).navigation()
                }
                onFailed(throwable, msg)
            }
        } else {
            onFailed(throwable, "")
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
    protected abstract fun onFailed(e: Throwable?, msg: String?)

}