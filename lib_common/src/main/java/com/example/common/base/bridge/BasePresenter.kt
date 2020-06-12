package com.example.common.base.bridge

import android.app.Activity
import android.content.Context
import com.example.common.bus.RxManager
import io.reactivex.disposables.Disposable
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/**
 * author: wyb
 * date: 2018/7/26.
 * 基础Presenter层，可在此处加载初始化
 * 完成View和Presenter的关联性
 */
abstract class BasePresenter<T : BaseView> {
    protected var activity: WeakReference<Activity>? = null
    protected var context: WeakReference<Context>? = null
    protected var view: SoftReference<T>? = null
    private var rxManager: RxManager? = null
//    private var currentCount: Int = 0
//    private var totalCount: Int = 0
//    private var index: Int = 1
//    private var hasNextPage: Boolean? = false

    fun attachView(activity: Activity?, context: Context?, view: T?) {
        this.activity = WeakReference(activity!!)
        this.context = WeakReference(context!!)
        this.view = SoftReference(view!!)
        this.rxManager = RxManager()
    }

    fun detachView() {
        activity?.clear()
        context?.clear()
        rxManager?.clear()
        view = null
    }

    protected fun addDisposable(disposable: Disposable?) {
        if (null != disposable) {
            rxManager?.add(disposable)
        }
    }

//刷新页面的逻辑，对页面page的处理可放在P层实现
//    //刷新清空
//    fun onRefresh() {
//        index = 1
//    }
//
//    fun onLoad(): Boolean {
//        return if (hasNextPage!!) {
//            ++index
//            true
//        } else {
//            false
//        }
//    }
//
//    //设置是否需要加载更多
//    fun hasNextPage(hasNextPage: Boolean?) {
//        this.hasNextPage = hasNextPage
//    }
//
//    //获取当前的数组长度
//    fun getIndex(): Int {
//        return index
//    }
//
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
