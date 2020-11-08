package com.dataqin.testnew.adapter

import com.dataqin.common.base.binding.BaseQuickAdapter
import com.dataqin.common.base.binding.BaseViewBindingHolder
import com.dataqin.testnew.databinding.ItemTestBinding
import com.dataqin.testnew.model.TestListModel

/**
 * Created by WangYanBin on 2020/8/14.
 */
class TestAdapter(list: MutableList<TestListModel>) : BaseQuickAdapter<TestListModel, ItemTestBinding>(list) {

    override fun convert(holder: BaseViewBindingHolder, item: TestListModel?) {
        if (null != item) {
//            val binding: ItemTestBinding = holder.getBinding()
//            binding.ivImg.setBackgroundResource(item.avatar)
//            binding.tvTitle.text = item.title
//            binding.tvDescribe.text = item.describe
            holder.getBinding<ItemTestBinding>().apply {
                ivImg.setBackgroundResource(item.avatar)
                tvTitle.text = item.title
                tvDescribe.text = item.describe
            }
        }
    }
}