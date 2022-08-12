package com.dataqin.split.subscribe

import com.dataqin.common.http.repository.ApiResponse
import io.reactivex.rxjava3.core.Flowable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * author:wyb
 * 分片接口类
 */
interface SplitApi {

    @Multipart
    @Streaming
    @POST("evidences/onlyPartUpload")
    fun getPartUploadApi(@Part partList: List<MultipartBody.Part>): Flowable<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("evidences/combine")
    fun getPartCombineApi(@FieldMap params: Map<String, String>): Flowable<ApiResponse<Any>>

    @Multipart
    @Streaming
    @POST("evidences/upload")
    fun getUploadApi(@Part partList: List<MultipartBody.Part>): Flowable<ApiResponse<Any>>

}
