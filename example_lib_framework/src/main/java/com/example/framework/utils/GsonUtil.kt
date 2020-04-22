package com.example.framework.utils

import com.google.gson.GsonBuilder

/**
 * author:wyb
 * 对象转换类
 */
object GsonUtil {
    private val gson = GsonBuilder().setLenient() // json宽松,针对json格式不规范
            .disableHtmlEscaping() //防止特殊字符出现乱码
            .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter()).create()

    fun <T> jsonToObj(json: String, className: Class<T>): T? {
        var ret: T? = null
        try {
            ret = gson.fromJson(json, className)
        } catch (ignored: Exception) {
        }
        return ret
    }

    fun objToJson(obj: Any): String? {
        var ret: String? = null
        try {
            ret = gson.toJson(obj)
        } catch (ignored: Exception) {
        }
        return ret
    }

}