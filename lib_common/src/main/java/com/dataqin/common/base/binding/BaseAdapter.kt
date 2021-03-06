package com.dataqin.common.base.binding

import androidx.recyclerview.widget.RecyclerView
import com.dataqin.common.widget.xrecyclerview.callback.OnItemClickListener

/**
 * Created by WangYanBin on 2020/7/17.
 * 基础适配器，适用于定制页面，加头加尾，需要重写onCreateViewHolder
 */
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewBindingHolder?> {
    //适配器类型-后续可扩展
    private var itemType = BaseItemType.Model
    //数据类型为集合
    var data: MutableList<T> = ArrayList()
        set(value) {
            //设置集合类型不相同时替换
            if (value !== field) {
                field.clear()
                if (!value.isNullOrEmpty()) {
                    field.addAll(value)
                }
            } else {
                field.clear()
                if (!value.isNullOrEmpty()) {
                    field.addAll(ArrayList(value))
                }
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
            itemType = BaseItemType.Model
        }
    }

    //传入集合的方法
    constructor(list: MutableList<T>?) {
        if (list != null) {
            data = list
            itemType = BaseItemType.List
        }
    }

    override fun getItemCount(): Int {
        return when (itemType) {
            BaseItemType.List -> data.size
            BaseItemType.Model -> 0
        }
    }

    override fun onBindViewHolder(holder: BaseViewBindingHolder, position: Int) {
        holder.itemView.setOnClickListener { onItemClickListener?.setOnItemClickListener(position) }
        convert(
            holder, when (itemType) {
                BaseItemType.List -> data[position]
                BaseItemType.Model -> t
            }
        )
    }

    //统一回调
    protected abstract fun convert(holder: BaseViewBindingHolder, item: T?)

}