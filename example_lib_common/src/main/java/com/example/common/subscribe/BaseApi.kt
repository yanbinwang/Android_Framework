package com.example.common.subscribe

import com.example.common.bean.BaseBean
import com.example.common.bean.UploadBean
import com.example.common.constant.Constants.URL
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * author:wyb
 * 通用接口类
 */
interface BaseApi {

    @Streaming
    @GET
    fun download(@Url downloadUrl: String): Flowable<ResponseBody>

    @Multipart
    @Streaming
    @POST(URL)
    fun getUploadFile(@Header("User-Agent") agent: String, @Part partList: List<MultipartBody.Part>): Flowable<BaseBean<UploadBean>>

    @FormUrlEncoded
    @POST(URL)
    fun getSendVerification(@Header("User-Agent") agent: String, @FieldMap map: Map<String, String>): Flowable<BaseBean<Any>>

    @FormUrlEncoded
    @POST(URL)
    fun getVerification(@Header("User-Agent") agent: String, @FieldMap map: Map<String, String>): Flowable<BaseBean<Any>>

}
