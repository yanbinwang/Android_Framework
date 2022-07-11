package com.dataqin.common.base.binding

import androidx.recyclerview.widget.RecyclerView
import com.dataqin.common.widget.xrecyclerview.callback.OnItemClickListener

/**
 * Created by WangYanBin on 2020/7/17.
 * 基础适配器，适用于定制页面，加头加尾，需要重写onCreateViewHolder
 */
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewBindingHolder?> {
    //适配器类型-后续可扩展
    private var itemType = BaseItemType.MODEL

    //数据类型为集合
    var data: MutableList<T> = ArrayList()
        set(value) {
            //设置集合类型不相同时替换
            if (value !== field) {
                if (!value.isNullOrEmpty()) field.addAll(value)
            } else {
                field.clear()
                if (!value.isNullOrEmpty()) field.addAll(ArrayList(value))
            }
            notifyDataSetChanged()
        }
    //数据类型为对象
    var t: T? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onItemClickListener: OnItemClickListener? = null

    //默认是返回对象
    constructor()

    //传入对象的方法
    constructor(model: T?) {
        if (t != null) {
            t = model
            itemType = BaseItemType.MODEL
        }
    }

    //传入集合的方法
    constructor(list: MutableList<T>?) {
        if (list != null) {
            data = list
            itemType = BaseItemType.LIST
        }
    }

    override fun getItemCount(): Int {
        return when (itemType) {
            BaseItemType.LIST -> data.size
            BaseItemType.MODEL -> 0
        }
    }

    override fun onBindViewHolder(holder: BaseViewBindingHolder, position: Int) {
        //注意判断当前适配器是否具有头部view
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(holder.absoluteAdapterPosition) }
        convert(holder, when (itemType) {
                BaseItemType.LIST -> data[position]
                BaseItemType.MODEL -> t
            })
    }

    //统一回调
    protected abstract fun convert(holder: BaseViewBindingHolder, item: T?)

}