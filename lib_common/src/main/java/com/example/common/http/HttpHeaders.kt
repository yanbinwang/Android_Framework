package com.example.common.http

import android.annotation.SuppressLint
import android.os.Build
import androidx.collection.ArrayMap
import com.example.common.BuildConfig
import java.util.*


/**
 * author: wyb
 * date: 2019/8/1.
 */
@SuppressLint("StaticFieldLeak")
open class HttpHeaders {

    //获取每次发起请求需要的基础头
    protected open fun defaultHeaders(): ArrayMap<String, String> {
        val headersMap = ArrayMap<String, String>()
//        val token: String? = UserUtil.getToken()//取得本地token
//        if (!TextUtils.isEmpty(token)) {
//            headersMap["Authorization"] = "basic $token"
//        }
        headersMap["yoogurt-request-id"] = UUID.randomUUID().toString()
        headersMap["X-yoogurt-system-type"] = "1"
        headersMap["X-yoogurt-system-name"] = "Android"
        headersMap["X-yoogurt-system-version"] = Build.VERSION.RELEASE
        headersMap["X-yoogurt-api-version"] = "v1"
        headersMap["X-yoogurt-app-version"] = java.lang.String.valueOf(BuildConfig.VERSION_CODE)
        headersMap["X-yoogurt-phone-model"] = Build.MODEL
        return headersMap
    }

    //添加自定义的头
    protected open fun customizeHeaders(baseHeadersMap: ArrayMap<String, String>, addHeadersMap: ArrayMap<String, String>): ArrayMap<String, String> {
        if (!addHeadersMap.isEmpty) {
            val set: Set<String?> = addHeadersMap.keys
            for (key in set) {
                val value = addHeadersMap[key]
                baseHeadersMap[key] = value
            }
        }
        return baseHeadersMap
    }

}