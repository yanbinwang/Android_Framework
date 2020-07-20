package com.example.testnew.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.common.base.binding.BaseQuickAdapter;
import com.example.common.base.binding.BaseViewBindingHolder;
import com.example.common.constant.Constants;
import com.example.testnew.databinding.ItemHomeBodyBinding;
import com.example.testnew.databinding.ItemHomeBottomBinding;
import com.example.testnew.databinding.ItemHomeHeadBinding;
import com.example.testnew.model.HomeModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by WangYanBin on 2020/7/20.
 */
public class HomeAdapter extends BaseQuickAdapter<HomeModel> {


    public HomeAdapter(@Nullable HomeModel homeModel) {
        super(homeModel);
    }

    @NonNull
    @Override
    public BaseViewBindingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewBindingHolder holder = null;
        switch (viewType) {
            case Constants.ADAPTER_ITEM_VIEW_TYPE_HEAD:
                holder = new BaseViewBindingHolder(ItemHomeHeadBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                break;
            case Constants.ADAPTER_ITEM_VIEW_TYPE_BODY:
                holder = new BaseViewBindingHolder(ItemHomeBodyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                break;
            case Constants.ADAPTER_ITEM_VIEW_TYPE_BOTTOM:
                holder = new BaseViewBindingHolder(ItemHomeBottomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
                break;
        }
        return holder;
    }

    @Override
    public int getItemCount() {
        HomeModel model = getT();
        if (null != model) {
            return model.getList().size() + 2;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position) {
            return Constants.ADAPTER_ITEM_VIEW_TYPE_HEAD;
        } else if (1 == position) {
            return Constants.ADAPTER_ITEM_VIEW_TYPE_BODY;
        } else {
            return Constants.ADAPTER_ITEM_VIEW_TYPE_BOTTOM;
        }
    }

    @Override
    protected void convert(@NotNull BaseViewBindingHolder holder, @Nullable HomeModel item) {
        switch (holder.getItemViewType()) {
            case Constants.ADAPTER_ITEM_VIEW_TYPE_HEAD:
                ItemHomeHeadBinding headBinding = holder.getBinding();

                break;
            case Constants.ADAPTER_ITEM_VIEW_TYPE_BODY:
                ItemHomeBodyBinding bodyBinding = holder.getBinding();
//                 bodyBinding.mainBodyLin
                break;
            case Constants.ADAPTER_ITEM_VIEW_TYPE_BOTTOM:
                ItemHomeBottomBinding binding = holder.getBinding();

                break;
        }
    }

}
