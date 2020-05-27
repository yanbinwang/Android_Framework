package com.example.common.base.bridge

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.example.common.R
import com.example.common.utils.NetWorkUtil
import com.example.common.bus.RxManager
import com.example.common.widget.empty.EmptyLayout
import com.example.common.widget.xrecyclerview.XRecyclerView
import io.reactivex.disposables.Disposable
import java.lang.ref.SoftReference

/**
 * author: wyb
 * date: 2018/7/26.
 * 基础presenter层，可在此处加载初始化
 * 完成View和Presenter的关联性
 */
abstract class BasePresenter<T : BaseView> {
    protected var view: T? = null
    protected var context: Context? = null
    private var viewRef: SoftReference<T>? = null
    private var rxManager: RxManager? = null
//    private var currentCount: Int = 0
//    private var totalCount: Int = 0
    private var index: Int = 1
    private var hasNextPage: Boolean? = false

    fun attachView(context: Context, view: T?) {
        if (null == view) {
            throw NullPointerException("BasePresenter#attechView view can not be null")
        }
        this.context = context
        this.view = view
        this.viewRef = SoftReference(view)
        this.rxManager = RxManager()
    }

    fun detachView() {
        rxManager!!.clear()
        viewRef!!.clear()
        viewRef = null
        view = null
    }

    protected fun addDisposable(disposable: Disposable?) {
        if (null != disposable) {
            rxManager!!.add(disposable)
        }
    }

    protected fun doResponse(msg: String?): Boolean {
        var message = msg
        if (TextUtils.isEmpty(msg)) {
            message = context!!.getString(R.string.label_response_err)
        }
        if (!NetWorkUtil.isNetworkAvailable()) {
            view!!.showToast(context!!.getString(R.string.label_response_net_err))
        } else {
            view!!.showToast(message!!)
        }
        return true
    }

    //针对页面
    @Synchronized
    fun emptyState(emptyLayout: EmptyLayout?, msg: String?) {
        emptyLayout!!.visibility = View.VISIBLE
        if (doResponse(msg)) {
            emptyLayout.showEmpty()
        }
        if (!NetWorkUtil.isNetworkAvailable()) {
            emptyLayout.showError()
        }
    }

    //针对列表
    @Synchronized
    fun emptyState(xRecyclerView: XRecyclerView?, msg: String?, length: Int?) {
        emptyState(xRecyclerView, msg, length, R.mipmap.img_data_empty, EmptyLayout.EMPTY_TXT)
    }

    //针对列表
    @Synchronized
    fun emptyState(xRecyclerView: XRecyclerView?, msg: String?, length: Int?, imgInt: Int?, emptyStr: String?) {
        doResponse(msg)
        if (length!! > 0) {
            return
        }
        xRecyclerView!!.setVisibilityEmptyView(View.VISIBLE)
        if (!NetWorkUtil.isNetworkAvailable()) {
            xRecyclerView.showError()
        } else {
            xRecyclerView.showEmpty(imgInt!!, emptyStr)
        }
    }

    //刷新清空
    fun onRefresh() {
        index = 1
    }

    fun onLoad(): Boolean {
        return if (hasNextPage!!) {
            ++index
            true
        } else {
            false
        }
    }

    //设置是否需要加载更多
    fun hasNextPage(hasNextPage: Boolean?) {
        this.hasNextPage = hasNextPage
    }

    //获取当前的数组长度
    fun getIndex(): Int {
        return index
    }

//    //刷新清空
//    fun onRefresh() {
//        currentCount = 0
//        totalCount = 0
//    }
//
//    //是否需要加载更多
//    fun hasNextPage(): Boolean {
//        return currentCount < totalCount
//    }
//
//    //获取当前的数组长度
//    fun getCurrentCount(): Int {
//        return currentCount
//    }
//
//    //列加数组长度
//    fun setCurrentCount(currentCount: Int) {
//        this.currentCount += currentCount
//    }
//
//    //设置总数
//    fun setTotalCount(totalCount: Int) {
//        this.totalCount = totalCount
//    }

}
