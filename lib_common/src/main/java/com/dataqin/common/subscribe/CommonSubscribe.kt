package com.dataqin.common.subscribe

import com.dataqin.common.http.factory.RetrofitFactory

/**
 * author:wyb
 * 通用接口类
 */
object CommonSubscribe : CommonApi {
    private val commonApi by lazy { RetrofitFactory.instance.create(CommonApi::class.java) }
    private val downloadApi by lazy { RetrofitFactory.instance.create2(CommonApi::class.java) }

    override fun getDownloadApi(downloadUrl: String) = downloadApi.getDownloadApi(downloadUrl)

    override fun getSendVerificationApi(agent: String, map: Map<String, String>)= commonApi.getSendVerificationApi(agent, map)

    override fun getVerificationApi(agent: String, map: Map<String, String>) = commonApi.getVerificationApi(agent, map)

//    //上传图片接口
//    fun getUploadFile(header: Int, partList: MutableList<MultipartBody.Part>, resourceSubscriber: ResourceSubscriber<BaseBean<UploadBean>>): Disposable {
//        val params = Params().getParams(timestamp)
//        partList.add(MultipartBody.Part.createFormData("timestamp", timestamp))
//        partList.add(MultipartBody.Part.createFormData("param", params["param"] ?: error("")))
//        val flowable = baseApi.getUploadFile(SecurityUtil.buildHeader(header, timestamp), partList)
//        return RetrofitFactory.getInstance().subscribeWith(flowable, resourceSubscriber)
//    }
//
//    //发送短信验证码-600
//    fun getSendVerification(header: Int, params: Params, resourceSubscriber: ResourceSubscriber<BaseBean<Any>>): Disposable {
//        val flowable = baseApi.getSendVerification(SecurityUtil.buildHeader(header, timestamp), params.getParams(timestamp))
//        return RetrofitFactory.getInstance().subscribeWith(flowable, resourceSubscriber)
//    }
//
//    //短信验证码验证-601
//    fun getVerification(header: Int, params: Params, resourceSubscriber: ResourceSubscriber<BaseBean<Any>>): Disposable {
//        val flowable = baseApi.getVerification(SecurityUtil.buildHeader(header, timestamp), params.getParams(timestamp))
//        return RetrofitFactory.getInstance().subscribeWith(flowable, resourceSubscriber)
//    }

}
