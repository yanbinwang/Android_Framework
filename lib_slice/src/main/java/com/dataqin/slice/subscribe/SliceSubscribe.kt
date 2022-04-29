package com.dataqin.slice.subscribe

import com.dataqin.common.http.factory.RetrofitFactory
import okhttp3.MultipartBody

/**
 * author:wyb
 * 证据库接口类
 */
object SliceSubscribe : SliceApi {
    private val sliceApi by lazy { RetrofitFactory.instance.create(SliceApi::class.java) }

    /**
     *  获取分片上传
     */
    override fun getPartUploadApi(partList: List<MultipartBody.Part>) = sliceApi.getPartUploadApi(partList)

    /**
     * 获取完整文件上传
     */
    override fun getUploadApi(partList: List<MultipartBody.Part>) = sliceApi.getUploadApi(partList)

}