package com.example.common.base.bridge

import android.app.Activity
import com.example.common.base.page.PageParams

/**
 * author: wyb
 * date: 2018/7/26.
 * 基础view层,包含基础的view操作
 */
interface BaseView {

    /**
     * 显示log
     */
    fun log(msg: String?)

    /**
     * Toast显示
     */
    fun showToast(msg: String?)

    /**
     * 显示刷新球动画
     */
    fun showDialog()

    /**
     * 不能点击关闭的dialog
     */
    fun showDialog(isClose: Boolean? = false)

    /**
     * 隐藏刷新球控件
     */
    fun hideDialog()

    /**
     * 非空判断
     */
    fun isEmpty(vararg anys: Any?): Boolean

    /**
     * 防止报空
     */
    fun processedString(source: String?, defaultStr: String?): String

    /**
     * 路由跳转
     */
    fun navigation(path:String?):Activity

    /**
     * 路由跳转,带参数
     */
    fun navigation(path:String?,params: PageParams):Activity

}
