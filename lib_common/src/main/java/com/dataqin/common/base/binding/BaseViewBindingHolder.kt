package com.dataqin.common.base.binding

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by WangYanBin on 2020/7/17.
 * 基础复用的ViewHolder，传入对应的ViewBinding拿取布局Binding
 */
open class BaseViewBindingHolder(private val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun <VB : ViewBinding?> getBinding(): VB {
        return binding as VB
    }

}