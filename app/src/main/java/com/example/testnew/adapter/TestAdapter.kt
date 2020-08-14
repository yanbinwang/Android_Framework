package com.example.testnew.adapter

import com.example.common.base.binding.BaseQuickAdapter
import com.example.common.base.binding.BaseViewBindingHolder
import com.example.testnew.databinding.ItemTestBinding
import com.example.testnew.model.TestListModel

/**
 * Created by WangYanBin on 2020/8/14.
 */
class TestAdapter(list: MutableList<TestListModel>) : BaseQuickAdapter<TestListModel, ItemTestBinding>(list) {

    override fun convert(holder: BaseViewBindingHolder, item: TestListModel?) {
        if (null != item) {
            val binding: ItemTestBinding = holder.getBinding()
            binding.ivImg.setBackgroundResource(item.avatar)
            binding.tvTitle.text = item.title
            binding.tvDescribe.text = item.describe
        }
    }
}