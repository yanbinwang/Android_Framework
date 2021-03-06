package com.dataqin.common.base.page

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.dataqin.base.utils.ToastUtil.mackToastSHORT
import com.dataqin.common.BaseApplication
import com.dataqin.common.R
import com.dataqin.common.utils.NetWorkUtil.isNetworkAvailable
import com.dataqin.common.widget.empty.EmptyLayout
import com.dataqin.common.widget.xrecyclerview.XRecyclerView

/**
 * 页面工具类
 * 1.接口提示
 * 2.遮罩层操作
 */
@SuppressLint("StaticFieldLeak")
object PageHandler {
    /**
     * 提示方法，根据接口返回的msg提示
     */
    @JvmStatic
    fun doResponse(msg: String?) {
        var str = msg
        val context = BaseApplication.instance?.applicationContext!!
        if (TextUtils.isEmpty(str)) {
            str = context.getString(R.string.label_response_err)
        }
        mackToastSHORT(if (!isNetworkAvailable()) context.getString(R.string.label_response_net_err) else str!!, context)
    }

    /**
     * 详情页调取方法
     */
    @JvmStatic
    fun setState(container: ViewGroup, msg: String?) {
        setState(container, msg, -1, null)
    }

    @JvmStatic
    fun setState(container: ViewGroup, msg: String?, imgRes: Int, emptyText: String?) {
        val emptyLayout = if (container is EmptyLayout) {
            container
        } else {
            getEmptyView(container)
        }
        doResponse(msg)
        emptyLayout.visibility = View.VISIBLE
        if (!isNetworkAvailable()) {
            emptyLayout.showError()
        } else {
            emptyLayout.showEmpty(imgRes, emptyText)
        }
    }

    /**
     * 列表页调取方法
     */
    @JvmStatic
    fun setState(xRecyclerView: XRecyclerView, refresh: Boolean, msg: String?, length: Int) {
        setState(xRecyclerView, refresh, msg, length, -1, null)
    }

    @JvmStatic
    fun setState(xRecyclerView: XRecyclerView, refresh: Boolean, msg: String?, length: Int, imgRes: Int, emptyText: String?) {
        val emptyLayout = getEmptyView(xRecyclerView)
        xRecyclerView.finishRefreshing()
        //区分此次刷新是否成功
        if (refresh) {
            emptyLayout.visibility = View.GONE
        } else {
            if (length > 0) {
                doResponse(msg)
                return
            }
            setState(emptyLayout, msg, imgRes, emptyText)
        }
    }

    /**
     * 详情页
     */
    @JvmStatic
    fun getEmptyView(container: ViewGroup): EmptyLayout {
        val emptyLayout: EmptyLayout?
        if (container.childCount <= 1) {
            emptyLayout = EmptyLayout(container.context)
            emptyLayout.draw()
            emptyLayout.showLoading()
            container.addView(emptyLayout)
        } else {
            emptyLayout = container.getChildAt(1) as EmptyLayout
        }
        return emptyLayout
    }

    /**
     * 列表页
     */
    @JvmStatic
    fun getEmptyView(xRecyclerView: XRecyclerView): EmptyLayout {
        return xRecyclerView.emptyView
    }

}