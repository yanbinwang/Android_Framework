package com.dataqin.common.http.repository

import com.dataqin.common.utils.analysis.GsonUtil
import retrofit2.HttpException
import java.lang.reflect.ParameterizedType

/**
 * Created by WangYanBin on 2020/9/17.
 * 在一个正确运行的事件序列中 onComplete 和 onError 有且只有一个，并且是事件序列中的最后一个
 * onComplete 和 onError(二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个
 * 故手动在处理后回调一次 onComplete 销毁该次事务，保证 onComplete 会被调用（用于项目中请求结束的一些操作）
 */
abstract class ResourceSubscriber<T> : io.reactivex.rxjava3.subscribers.ResourceSubscriber<T>() {

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    final override fun onNext(t: T) {
        onResult(t)
    }

    final override fun onError(t: Throwable?) {
        try {
            val responseBody = (t as? HttpException)?.response()?.errorBody()
            if (null != responseBody) {
                val type = javaClass.genericSuperclass as ParameterizedType
                val tClass: Class<T> = type.actualTypeArguments[0] as Class<T>
                val tModel = GsonUtil.jsonToObj(responseBody.string(), tClass::class.java)
                onResult(tModel as? T?, t)
            } else {
                onResult(null, t)
            }
        } catch (e: Exception) {
            onResult(null, e)
        }
        onComplete()
    }

    override fun onComplete() {
        if (!isDisposed) {
            dispose()
        }
    }
    // </editor-fold>

    /**
     * 回调请求结果（onError中的值也转成对应泛型返回）
     */
    open fun onResult(data: T? = null, throwable: Throwable? = null) {}

}