package com.dataqin.common.http.repository

import java.util.*

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

}
