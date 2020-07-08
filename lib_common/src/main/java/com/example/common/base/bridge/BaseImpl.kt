package com.example.common.base.bridge

import android.view.View
import com.example.common.base.proxy.SimpleTextWatcher

/**
 * author: wyb
 * 基类Controller层
 * 主要针对View层的控件和Model层的数据做取值和基础逻辑的处理
 */
interface BaseImpl {

    /**
     * 初始化控件
     */
    fun initView()

    /**
     * 初始化事件
     */
    fun initEvent()

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 对象判空（批量）
     */
    fun isEmpty(vararg objs: Any?): Boolean

    /**
     * 虚拟键盘开启
     */
    fun openDecor(view: View?)

    /**
     * 虚拟键盘关闭
     */
    fun closeDecor()

    /**
     * 让一个view获得焦点
     */
    fun getFocus(view: View?)

    /**
     * 获取控件的基础值
     */
    fun getParameters(view: View?): String?

    /**
     * 批量注入输入监听
     */
    fun onTextChanged(simpleTextWatcher: SimpleTextWatcher?, vararg views: View?)

    /**
     * 批量注入点击事件
     */
    fun onClick(onClickListener: View.OnClickListener?, vararg views: View?)

    /**
     * 控件显示
     */
    fun VISIBLE(vararg views: View?)

    /**
     * 控件隐藏（占位）
     */
    fun INVISIBLE(vararg views: View?)

    /**
     * 控件隐藏（不占位）
     */
    fun GONE(vararg views: View?)

}
