package com.ow.basemodule.subscribe

import com.ow.basemodule.bean.UploadBean
import com.ow.basemodule.utils.http.BaseModel
import com.ow.basemodule.utils.http.Params
import com.ow.basemodule.utils.http.Request
import com.ow.basemodule.utils.http.encryption.SecurityUtil
import com.ow.framework.net.RetrofitFactory
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.ResourceSubscriber
import okhttp3.MultipartBody

/**
 * author:wyb
 * 通用接口类
 */
object BaseSubscribe : Request() {
    private val baseApi = RetrofitFactory.getInstance().create(BaseApi::class.java)

    //上传图片接口
    fun getUploadFile(header: Int, partList: MutableList<MultipartBody.Part>, resourceSubscriber: ResourceSubscriber<BaseModel<UploadBean>>): Disposable {
        val timestamp = initRequest()
        val params = Params().getParams(timestamp)
        partList.add(MultipartBody.Part.createFormData("timestamp", timestamp))
        partList.add(MultipartBody.Part.createFormData("param", params["param"] ?: error("")))
        val flowable = baseApi.getUploadFile(SecurityUtil.buildHeader(header, timestamp), partList)
        return RetrofitFactory.getInstance().subscribeWith(flowable, resourceSubscriber)
    }

    //发送短信验证码-600
    fun getSendVerification(header: Int, params: Params, resourceSubscriber: ResourceSubscriber<BaseModel<Any>>): Disposable {
        val timestamp = initRequest()
        val flowable = baseApi.getSendVerification(SecurityUtil.buildHeader(header, timestamp), params.getParams(timestamp))
        return RetrofitFactory.getInstance().subscribeWith(flowable, resourceSubscriber)
    }

    //短信验证码验证-601
    fun getVerification(header: Int, params: Params, resourceSubscriber: ResourceSubscriber<BaseModel<Any>>): Disposable {
        val timestamp = initRequest()
        val flowable = baseApi.getVerification(SecurityUtil.buildHeader(header, timestamp), params.getParams(timestamp))
        return RetrofitFactory.getInstance().subscribeWith(flowable, resourceSubscriber)
    }

}
