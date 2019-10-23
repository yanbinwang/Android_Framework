package com.ow.basemodule.subscribe

import com.ow.basemodule.bean.KeyBean
import com.ow.basemodule.bean.UploadBean
import com.ow.basemodule.constant.Constants.URL
import com.ow.basemodule.utils.http.BaseModel
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * author:wyb
 * 通用接口类
 */
interface BaseApi {

    @Streaming
    @GET
    fun download(@Url downloadUrl: String): Flowable<ResponseBody>

    @FormUrlEncoded
    @POST(URL)
    fun getKeyApi(@Header("User-Agent") agent: String, @FieldMap params: Map<String, String>): Call<BaseModel<KeyBean>>

    @Multipart
    @Streaming
    @POST(URL)
    fun getUploadFile(@Header("User-Agent") agent: String, @Part partList: List<MultipartBody.Part>): Flowable<BaseModel<UploadBean>>

    @FormUrlEncoded
    @POST(URL)
    fun getSendVerification(@Header("User-Agent") agent: String, @FieldMap map: Map<String, String>): Flowable<BaseModel<Any>>

    @FormUrlEncoded
    @POST(URL)
    fun getVerification(@Header("User-Agent") agent: String, @FieldMap map: Map<String, String>): Flowable<BaseModel<Any>>

}
