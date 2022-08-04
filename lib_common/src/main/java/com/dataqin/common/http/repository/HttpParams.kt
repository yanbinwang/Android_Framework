package com.dataqin.common.http.repository

import com.dataqin.common.utils.analysis.GsonUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * 请求参数类
 */
class HttpParams {
    var map: MutableMap<String, String> = HashMap()

    //参数的添加
    fun append(key: String, value: String?): HttpParams {
        if (value != null) {
            map[key] = value
        }
        return this
    }

    /**
     * 请求转换
     */
    fun params() = ((GsonUtil.objToJson(map)) ?: "").toRequestBody("application/json; charset=utf-8".toMediaType())

}
