package com.dataqin.testnew.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dataqin.common.base.binding.BaseAdapter
import com.dataqin.common.base.binding.BaseViewBindingHolder
import com.dataqin.common.constant.Constants
import com.dataqin.testnew.databinding.ItemHomeBodyBinding
import com.dataqin.testnew.databinding.ItemHomeBottomBinding
import com.dataqin.testnew.databinding.ItemHomeHeadBinding
import com.dataqin.testnew.model.HomeModel

/**
 * Created by WangYanBin on 2020/8/14.
 */
class HomeAdapter(model: HomeModel) : BaseAdapter<HomeModel>(model) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewBindingHolder {
        var holder: BaseViewBindingHolder? = null
        when (viewType) {
            Constants.ADAPTER_ITEM_VIEW_TYPE_HEAD -> holder = BaseViewBindingHolder(ItemHomeHeadBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            Constants.ADAPTER_ITEM_VIEW_TYPE_BODY -> holder = BaseViewBindingHolder(ItemHomeBodyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            Constants.ADAPTER_ITEM_VIEW_TYPE_BOTTOM -> holder = BaseViewBindingHolder(ItemHomeBottomBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        return holder!!
    }

    override fun getItemCount(): Int {
        return if (null == t) 0 else t!!.list.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> Constants.ADAPTER_ITEM_VIEW_TYPE_HEAD
            1 -> Constants.ADAPTER_ITEM_VIEW_TYPE_BODY
            else -> Constants.ADAPTER_ITEM_VIEW_TYPE_BOTTOM
        }
    }

    override fun convert(holder: BaseViewBindingHolder, item: HomeModel?) {
        when (holder.itemViewType) {
            Constants.ADAPTER_ITEM_VIEW_TYPE_HEAD -> {
                var headBinding: ItemHomeHeadBinding? = holder.getBinding()

            }
            Constants.ADAPTER_ITEM_VIEW_TYPE_BODY -> {
                var bodyBinding: ItemHomeBodyBinding? = holder.getBinding()

            }
            Constants.ADAPTER_ITEM_VIEW_TYPE_BOTTOM -> {
                var bottomBinding: ItemHomeBottomBinding? = holder.getBinding()

            }
        }
    }

}