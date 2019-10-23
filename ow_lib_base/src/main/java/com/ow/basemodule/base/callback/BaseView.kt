package com.ow.basemodule.base.callback

/**
 * author: wyb
 * date: 2018/7/26.
 * 基础view层,包含基础的view操作
 */
interface BaseView {

    fun log(msg: String?) //显示log

    fun showToast(msg: String?) //Toast显示

    fun showDialog() //显示刷新球动画

    fun showDialog(msg: String?) //显示刷新球动画

    fun showDialog(msg: String?, isClose: Boolean?) //不能点击关闭的dialog

    fun hideDialog() //隐藏刷新球控件

}
