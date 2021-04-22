package com.dataqin.common.utils.analysis

import com.google.gson.GsonBuilder

/**
 * author:wyb
 * 对象转换类
 */
object GsonUtil {
    private val gson by lazy {
        GsonBuilder().setLenient()//json宽松,针对json格式不规范
            .disableHtmlEscaping()//防止特殊字符出现乱码
            .registerTypeAdapter(Boolean::class.java, BooleanTypeAdapter()).create()
    }

    @JvmStatic
    fun <T> jsonToObj(json: String, className: Class<T>): T? {
        var t: T? = null
        try {
            t = gson.fromJson(json, className)
        } catch (ignored: Exception) {
        }
        return t
    }

    @JvmStatic
    fun objToJson(obj: Any): String? {
        var json: String? = null
        try {
            json = gson.toJson(obj)
        } catch (ignored: Exception) {
        }
        return json
    }

}