package com.example.common.base.binding

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by WangYanBin on 2020/7/17.
 * 基础适配器
 */
abstract class BaseQuickAdapter<T> : RecyclerView.Adapter<BaseViewBindingHolder?> {
    private var state: State? = null
    private var t: T? = null
    private var data: MutableList<T> = ArrayList()

    //默认是返回对象
    constructor() {
        state = State.OBJECT
    }

    //传入对象的方法
    constructor(t: T?) {
        if (t != null) {
            this.t = t
            state = State.OBJECT
        }
    }

    //传入集合的方法
    constructor(list: MutableList<T>?) {
        if (list != null) {
            data = list
            state = State.COLLECTION
        }
    }

    override fun getItemCount(): Int {
        if (state == State.COLLECTION) {
            return data.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: BaseViewBindingHolder, position: Int) {
        convert(holder, if (state == State.COLLECTION) data[position] else t)
    }

    //设置集合
    fun setData(list: MutableList<T>?) {
        if (list != null) {
            data = list
            notifyDataSetChanged()
        }
    }

    //设置对象
    fun setData(data: T?) {
        t = data
        notifyDataSetChanged()
    }

    //添加集合
    fun setList(list: MutableList<T>?) {
        if (list != null) {
            data.addAll(list)
        }
    }

    //统一回调
    protected abstract fun convert(holder: BaseViewBindingHolder, item: T?)

    enum class State { COLLECTION, OBJECT }

}