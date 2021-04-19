package com.dataqin.common.bus

import android.os.Bundle

/**
 * author: wyb
 * date: 2018/4/16.
 * rxjava传递事件类
 */
class RxEvent {
    private var action: String? = null //广播名称
    private var args: Bundle? = null //额外参数

    //不含任何参数的广播
    constructor(action: String) {
        this.action = action
    }

    //带布尔类型的广播
    constructor(action: String, value: Boolean) {
        this.action = action
        if (args == null) args = Bundle()
        args?.putBoolean(action, value)
    }

    //带字符串类型的广播
    constructor(action: String, value: String) {
        this.action = action
        if (args == null) args = Bundle()
        args?.putString(action, value)
    }

    //带数据类的广播
    constructor(action: String, args: Bundle) {
        this.action = action
        this.args = args
    }

    //获取广播名
    fun getAction() = action

    //获取默认布尔值
    fun getBooleanExtra(defaultValue: Boolean): Boolean {
        return if (args == null) defaultValue else args?.getBoolean(action, defaultValue) ?: false
    }

    //获取默认字符串值
    fun getStringExtra(): String {
        return if (args == null) "" else args?.getString(action) ?: ""
    }

    //获取默认类值
    fun getBundleExtras() = args

}
