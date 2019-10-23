package com.ow.basemodule.utils.http

/**
 * author: wyb
 * date: 2019/7/29.
 * 接口外层地址（与服务器约定好对应格式）
 */
class BaseModel<T> {
    var e: Int = 0
    var msg: String? = null
    var data: T? = null
}
