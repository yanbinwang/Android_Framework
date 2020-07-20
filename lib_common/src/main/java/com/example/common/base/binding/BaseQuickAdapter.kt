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
    private var itemType: ItemType? = null
    private var onItemClickListener: OnItemClickListener? = null

    //默认是返回对象
    constructor() {
        itemType = ItemType.Model
    }

    //传入对象的方法
    constructor(model: T?) {
        if (t != null) {
            t = model
            itemType = ItemType.Model
        }
    }

    //传入集合的方法
    constructor(list: MutableList<T>?) {
        if (list != null) {
            data = list
            itemType = ItemType.List
        }
    }

    override fun getItemCount(): Int {
        return if (itemType == ItemType.List) data.size else 0
    }

    override fun onBindViewHolder(holder: BaseViewBindingHolder, position: Int) {
        holder.itemView.setOnClickListener { onItemClickListener?.setOnItemClickListener(position) }
        convert(holder, if (itemType == ItemType.List) data[position] else t)
    }

    //设置对象
    fun setModel(model: T?) {
        t = model
        itemType = ItemType.Model
        notifyDataSetChanged()
    }

    //获取对象
    fun getModel(): T? {
        return t
    }

    //添加集合
    fun setList(list: MutableList<T>?) {
        //设置集合类型不相同时自动替换
        if (list !== data) {
            data.clear()
            if (!list.isNullOrEmpty()) {
                data.addAll(list)
            }
        } else {
            if (!list.isNullOrEmpty()) {
                val newList = ArrayList(list)
                data.clear()
                data.addAll(newList)
            } else {
                data.clear()
            }
        }
        itemType = ItemType.List
        notifyDataSetChanged()
    }

    //获取集合
    fun getList(): MutableList<T>? {
        return data
    }

    //设置点击
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    //统一回调
    protected abstract fun convert(holder: BaseViewBindingHolder, item: T?)

    enum class ItemType { Model, List }

}