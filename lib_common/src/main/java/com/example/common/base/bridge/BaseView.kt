package com.example.common.base.bridge

import android.app.Activity
import com.example.common.base.page.PageParams

/**
 * author: wyb
 * date: 2018/7/26.
 * 基础View层,包含能在Presenter层对View的操作
 */
interface BaseView {

    /**
     * 显示log
     */
    fun log(msg: String)

    /**
     * Toast显示
     */
    fun showToast(msg: String)

    /**
     * 刷新动画dialog
     */
    fun showDialog(flag: Boolean = false)

    /**
     * 隐藏刷新球控件
     */
    fun hideDialog()

    /**
     * 路由跳转
     */
    fun navigation(path: String, params: PageParams ?= null): Activity

}