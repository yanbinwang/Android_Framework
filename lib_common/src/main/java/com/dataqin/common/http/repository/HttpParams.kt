package com.dataqin.common.http.repository

import com.dataqin.common.http.encryption.SecurityUtil
import com.dataqin.common.utils.NetWorkUtil
import java.util.*

/**
 * 请求参数类
 */
class HttpParams {
    var map: MutableMap<String, String> = HashMap()//请求的参数map
    var timestamp = ""//当前时间戳需要与秘钥key的时间戳一致

    //参数的添加
    fun append(key: String, value: String?): HttpParams {
        if (value != null) {
            map[key] = value
        }
        return this
    }

    //获取参数加密
    fun getSignParams(): Map<String, String> {
        map["timestamp"] = timestamp
        map["network"] = NetWorkUtil.getAPNType()
        map["sign"] = SecurityUtil.doSign(map)
        map = SecurityUtil.sortParams(map)
        if (SecurityUtil.needEncrypt()) {
            val param = SecurityUtil.doEncrypt(map)
            map.clear()
            map["param"] = param
            map["timestamp"] = timestamp
        }
        return map
    }

}
