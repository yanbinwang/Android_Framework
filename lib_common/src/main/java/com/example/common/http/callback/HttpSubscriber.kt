package com.example.common.http.callback

import com.alibaba.android.arouter.launcher.ARouter
import com.example.common.bus.RxBus.Companion.instance
import com.example.common.bus.RxBusEvent
import com.example.common.constant.ARouterPath
import com.example.common.constant.Constants
import com.example.common.utils.analysis.GsonUtil.jsonToObj
import com.example.common.utils.helper.AccountHelper
import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException

/**
 * Created by WangYanBin on 2020/6/19.
 */
abstract class HttpSubscriber<T> : ResourceSubscriber<ApiResponse<T>>() {

    override fun onNext(apiResponse: ApiResponse<T>?) {
        doResult(apiResponse, null)
    }

    override fun onError(throwable: Throwable?) {
        try {
            val responseBody = (throwable as HttpException).response()?.errorBody()
            if (null != responseBody) {
                val baseModel = jsonToObj(responseBody.string(), ApiResponse::class.java)
                doResult(baseModel as ApiResponse<T>?, throwable)
            } else {
                doResult(null, throwable)
            }
        } catch (e: Exception) {
            doResult(null, e)
        }
    }

    private fun doResult(apiResponse: ApiResponse<T>?, throwable: Throwable?) {
        if (null != apiResponse) {
            val msg = apiResponse.msg
            val e = apiResponse.e
            if (0 == e) {
                onSuccess(apiResponse.data)
            } else {
                //账号还没有登录，解密失败，重新获取
                if (100005 == e || 100008 == e) {
                    AccountHelper.signOut()
                    instance.post(RxBusEvent(Constants.APP_USER_LOGIN_OUT))
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
        onComplete()
    }

    override fun onComplete() {
        if (!isDisposed) {
            dispose()
        }
    }

    protected abstract fun onSuccess(data: T?)

    protected abstract fun onFailed(e: Throwable?, msg: String?)

}