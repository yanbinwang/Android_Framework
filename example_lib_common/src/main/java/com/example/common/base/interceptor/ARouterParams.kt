package com.example.common.base.interceptor

import java.util.*

/**
 * author:wyb
 */
class ARouterParams {
    private var map: MutableMap<String, Any> = HashMap()

    fun append(key: String, value: Any?): ARouterParams {
        if (value != null) {
            map[key] = value
        }
        return this
    }

    fun setMap(map: MutableMap<String, Any>): ARouterParams {
        this.map = map
        return this
    }

    fun getParams():MutableMap<String, Any>{
        return map
    }

}
