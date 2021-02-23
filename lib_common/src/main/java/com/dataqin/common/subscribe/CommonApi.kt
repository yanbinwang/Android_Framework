package com.dataqin.common.subscribe

import com.dataqin.common.http.repository.ApiResponse
import io.reactivex.rxjava3.core.Flowable
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * author:wyb
 * 通用接口类
 */
interface CommonApi {

    @Streaming
    @GET
    fun getDownloadApi(@Url downloadUrl: String): Flowable<ResponseBody>

    @FormUrlEncoded
    @POST("http://www.baidu.com")
    fun getSendVerificationApi(@Header("User-Agent") agent: String, @FieldMap map: Map<String, String>): Flowable<ApiResponse<Any>>

    @FormUrlEncoded
    @POST("http://www.baidu.com")
    fun getVerificationApi(@Header("User-Agent") agent: String, @FieldMap map: Map<String, String>): Flowable<ApiResponse<Any>>

}
