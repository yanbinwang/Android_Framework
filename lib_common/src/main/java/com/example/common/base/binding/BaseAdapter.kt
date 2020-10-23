package com.example.common.base.binding

import androidx.recyclerview.widget.RecyclerView
import com.example.common.widget.xrecyclerview.callback.OnItemClickListener

/**
 * Created by WangYanBin on 2020/7/17.
 * 基础适配器，适用于定制页面，加头加尾，需要重写onCreateViewHolder
 */
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseViewBindingHolder?> {
    //数据类型为集合
    var data: MutableList<T> = ArrayList()
        set(value) {
            itemType = ItemType.List
            //设置集合类型不相同时自动替换
            if (value !== field) {
                field.clear()
                if (!value.isNullOrEmpty()) {
                    field.addAll(value)
                }
            } else {
                if (!value.isNullOrEmpty()) {
                    val newList = ArrayList(value)
                    field.clear()
                    field.addAll(newList)
                } else {
                    field.clear()
                }
            }
            notifyDataSetChanged()
        }

    //数据类型为对象
    var t: T? = null
        set(value) {
            itemType = ItemType.Model
            field = value
            notifyDataSetChanged()
        }
    var onItemClickListener: OnItemClickListener? = null
    //适配器类型-后续可扩展
    private var itemType = ItemType.Model

    //默认是返回对象
    constructor()

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
        return when (itemType) {
            ItemType.List -> data.size
            ItemType.Model -> 0
        }
    }

    override fun onBindViewHolder(holder: BaseViewBindingHolder, position: Int) {
        holder.itemView.setOnClickListener { onItemClickListener?.setOnItemClickListener(position) }
        convert(
            holder, when (itemType) {
                ItemType.List -> data[position]
                ItemType.Model -> t
            }
        )
    }

    //统一回调
    protected abstract fun convert(holder: BaseViewBindingHolder, item: T?)

    //适配器枚举类型
    private enum class ItemType { Model, List }

//    //设置对象
//    fun setModel(model: T?) {
//        t = model
//        itemType = ItemType.Model
//        notifyDataSetChanged()
//    }
//
//    //获取对象
//    fun getModel(): T? {
//        return t
//    }
//
//    //添加集合
//    fun setList(list: MutableList<T>?) {
//        //设置集合类型不相同时自动替换
//        if (list !== data) {
//            data.clear()
//            if (!list.isNullOrEmpty()) {
//                data.addAll(list)
//            }
//        } else {
//            if (!list.isNullOrEmpty()) {
//                val newList = ArrayList(list)
//                data.clear()
//                data.addAll(newList)
//            } else {
//                data.clear()
//            }
//        }
//        itemType = ItemType.List
//        notifyDataSetChanged()
//    }
//
//    //获取集合
//    fun getList(): MutableList<T>? {
//        return data
//    }
//
//    //设置点击
//    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
//        this.onItemClickListener = onItemClickListener
//    }

}