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
     * 显示刷新球动画
     */
    fun showDialog()

    /**
     * 不能点击关闭的dialog
     */
    fun showDialog(flag: Boolean)

    /**
     * 隐藏刷新球控件
     */
    fun hideDialog()

    /**
     * 路由跳转
     */
    fun navigation(path: String): Activity

    /**
     * 路由跳转,带参数
     */
    fun navigation(path: String, params: PageParams?): Activity

}
