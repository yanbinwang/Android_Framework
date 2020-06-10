package com.example.common.base.bridge

import android.view.View
import io.reactivex.disposables.Disposable

/**
 * author: wyb
 * 基类的基础方法
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
     * 添加事务
     */
    fun addDisposable(disposable: Disposable?)

    /**
     * 虚拟键盘开启
     */
    fun openDecor(view: View?)

    /**
     * 虚拟键盘关闭
     */
    fun closeDecor()

    /**
     * 获取控件信息
     */
    fun getViewValue(view: View?): String

    /**
     * 让一个view获得焦点
     */
    fun setViewFocus(view: View?)

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

    /**
     * 是否为空
     */
    fun isEmpty(vararg objs: Any?): Boolean

    /**
     * 赋值-文案带默认值
     */
    fun processedString(source: String?, defaultStr: String?): String

    /**
     * 赋值-文案
     */
    fun setText(res: Int, str: String?)

    /**
     * 赋值-颜色
     */
    fun setTextColor(res: Int, color: Int)

}
