package com.dataqin.slice.subscribe

import com.dataqin.common.http.repository.ApiResponse
import io.reactivex.rxjava3.core.Flowable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * author:wyb
 * 证据库接口类
 */
interface SliceApi {

    @Multipart
    @Streaming
    @POST("evidences/partUpload")
    fun getPartUploadApi(@Part partList: List<MultipartBody.Part>): Flowable<ApiResponse<Any>>

    @Multipart
    @Streaming
    @POST("evidences/upload")
    fun getUploadApi(@Part partList: List<MultipartBody.Part>): Flowable<ApiResponse<Any>>

}
