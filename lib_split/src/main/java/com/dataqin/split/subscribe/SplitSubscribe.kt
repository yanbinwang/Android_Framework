package com.dataqin.split.subscribe

import com.dataqin.common.http.factory.RetrofitFactory
import okhttp3.MultipartBody

/**
 * author:wyb
 * 通用接口类
 */
object SplitSubscribe : SplitApi {
    private val splitApi by lazy { RetrofitFactory.instance.create(SplitApi::class.java) }

    override fun getPartUploadApi(partList: List<MultipartBody.Part>) = splitApi.getPartUploadApi(partList)

    override fun getPartCombineApi(params: Map<String, String>) = splitApi.getPartCombineApi(params)

    override fun getUploadApi(partList: List<MultipartBody.Part>) = splitApi.getUploadApi(partList)

}