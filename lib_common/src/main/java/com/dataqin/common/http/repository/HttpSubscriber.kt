package com.dataqin.common.http.repository

/**
 * Created by WangYanBin on 2020/6/19.
 * 项目中使用的网络请求回调对象
 */
abstract class HttpSubscriber<T> : ResourceSubscriber<ApiResponse<T>>() {

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    final override fun onResult(data: ApiResponse<T>?, throwable: Throwable?) {
        if (null != data) {
            val msg = data.msg
            val code = data.code
            if (0 == code) {
                onSuccess(data.data)
            } else {
                //                //账号还没有登录，解密失败，重新获取
                //                if (100005 == e || 100008 == e) {
                //                    AccountHelper.signOut()
                //                    instance.post(RxEvent(Constants.APP_USER_LOGIN_OUT))
                //                    ARouter.getInstance().build(ARouterPath.LoginActivity).navigation()
                //                }
                //                //账号被锁定--进入账号锁定页（其余页面不关闭）
                //                if (100002 == e) {
                //                    ARouter.getInstance().build(ARouterPath.UnlockIPActivity).navigation()
                //                }
                onFailed(throwable, msg, code)
            }
        } else onFailed(throwable, "", -1)
    }
    // </editor-fold>

    /**
     * 请求成功，直接回调对象
     */
    open fun onSuccess(data: T?) {}

    /**
     * 请求失败，获取失败原因
     */
    open fun onFailed(e: Throwable?, msg: String?, code: Int?) {
        onFailed(e, msg)
    }

    open fun onFailed(e: Throwable?, msg: String?) {}

}