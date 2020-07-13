package com.example.testnew.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.testnew.databinding.ItemTestBinding;
import com.example.testnew.model.TestListModel;

import java.util.List;

/**
 * Created by WangYanBin on 2020/7/13.
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    private List<TestListModel> mList;

    public TestAdapter(List<TestListModel> mList) {
        this.mList = mList;
    }

    public int getItemCount() {
        return mList.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.binding
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemTestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTestBinding binding;

        private ViewHolder(ItemTestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}