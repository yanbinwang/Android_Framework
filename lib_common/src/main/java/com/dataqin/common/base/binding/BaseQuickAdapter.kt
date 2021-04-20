package com.dataqin.common.base.binding

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * Created by WangYanBin on 2020/7/17.
 * 快捷适配器，传入对应的ViewBinding即可
 */
abstract class BaseQuickAdapter<T, VB : ViewBinding> : BaseAdapter<T> {
    protected var context: Context? = null

    constructor() : super()

    constructor(model: T?) : super(model)

    constructor(list: MutableList<T>?) : super(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewBindingHolder {
        context = parent.context
        var binding: VB? = null
        val superclass = javaClass.genericSuperclass
        val aClass = (superclass as ParameterizedType).actualTypeArguments[1] as? Class<*>
        try {
            val method = aClass?.getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.javaPrimitiveType)
            binding = method?.invoke(null, LayoutInflater.from(parent.context), parent, false) as VB
        } catch (e: Exception) {
        } finally {
            return BaseViewBindingHolder(binding!!)
        }
    }

}