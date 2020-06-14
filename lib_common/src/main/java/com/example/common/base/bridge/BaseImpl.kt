package com.example.common.base.bridge

import android.view.View
import com.example.common.widget.empty.EmptyLayout
import com.example.common.widget.xrecyclerview.XRecyclerView
import io.reactivex.disposables.Disposable

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
     * 添加事务
     */
    fun addDisposable(disposable: Disposable?)

    /**
     * 接口返回提示
     */
    fun doResponse(msg: String?): Boolean

    /**
     * 遮罩层处理
     */
    fun emptyState(emptyLayout: EmptyLayout?, msg: String?)

    /**
     * 列表遮罩层处理
     */
    fun emptyState(xRecyclerView: XRecyclerView?, msg: String?, length: Int)

    /**
     * 列表遮罩层处理（自定义错误图片）
     */
    fun emptyState(xRecyclerView: XRecyclerView?, msg: String?, length: Int, imgInt: Int, emptyStr: String?)

    /**
     * 赋值-文案
     */
    fun setText(res: Int, str: String?)

    /**
     * 赋值-颜色
     */
    fun setTextColor(res: Int, color: Int)

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

}
