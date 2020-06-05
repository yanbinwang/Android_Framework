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
     * 初始化控件的适配器，控件属性
     */
    fun initView()

    /**
     * 初始化监听行为
     */
    fun initEvent()

    /**
     * 初始化数据，发起网络请求
     */
    fun initData();

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
     * 路由跳转
     */
    fun navigation(path:String?):Activity

    /**
     * 路由跳转,带参数
     */
    fun navigation(path:String?,params: PageParams):Activity

}
