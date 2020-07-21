package com.example.testnew.adapter;

import com.example.common.base.binding.BaseQuickAdapter;
import com.example.common.base.binding.BaseViewBindingHolder;
import com.example.testnew.databinding.ItemTestBinding;
import com.example.testnew.model.TestListModel;

import java.util.List;

/**
 * Created by WangYanBin on 2020/7/13.
 */
public class TestAdapter extends BaseQuickAdapter<TestListModel, ItemTestBinding> {

    public TestAdapter(List<TestListModel> list) {
        super(list);
    }

    @Override
    protected void convert(BaseViewBindingHolder holder, TestListModel item) {
        if (null != item) {
            ItemTestBinding binding = holder.getBinding();
            binding.ivImg.setBackgroundResource(item.getAvatar());
            binding.tvTitle.setText(item.getTitle());
            binding.tvDescribe.setText(item.getDescribe());
        }
    }

}