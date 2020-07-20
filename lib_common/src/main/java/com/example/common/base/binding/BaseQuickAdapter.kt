package com.example.common.base.binding

import androidx.recyclerview.widget.RecyclerView
import com.example.common.widget.xrecyclerview.callback.OnItemClickListener

/**
 * Created by WangYanBin on 2020/7/17.
 * 基础适配器
 */
abstract class BaseQuickAdapter<T> : RecyclerView.Adapter<BaseViewBindingHolder?> {
    private var data: MutableList<T> = ArrayList()
    private var t: T? = null
    private var state: State? = null
    private var onItemClickListener: OnItemClickListener? = null

    //默认是返回对象
    constructor() {
        state = State.OBJECT
    }

    //传入对象的方法
    constructor(model: T?) {
        if (t != null) {
            t = model
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
        holder.itemView.setOnClickListener { onItemClickListener?.setOnItemClickListener(position) }
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

    //设置点击
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    //获取对象
    fun getT(): T? {
        return t
    }

    //获取集合
    fun getList(): MutableList<T>? {
        return data
    }

    //统一回调
    protected abstract fun convert(holder: BaseViewBindingHolder, item: T?)

    enum class State { COLLECTION, OBJECT }

}