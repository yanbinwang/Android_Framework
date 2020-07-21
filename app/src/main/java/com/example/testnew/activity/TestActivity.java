package com.example.testnew.activity;

import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.common.base.BaseTitleActivity;
import com.example.common.constant.ARouterPath;
import com.example.testnew.R;
import com.example.testnew.adapter.TestAdapter;
import com.example.testnew.databinding.ActivityTestBinding;
import com.example.testnew.model.TestListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangYanBin on 2020/7/13.
 */
@Route(path = ARouterPath.TestActivity)
public class TestActivity extends BaseTitleActivity<ActivityTestBinding> {
    private List<TestListModel> list = new ArrayList<>();
    private TestAdapter adapter;

    @Override
    public void initView() {
        super.initView();
        titleBuilder.setTitle("列表").getDefault();
        adapter = new TestAdapter(list);
        binding.recTest.setLayoutManager(new GridLayoutManager(this,1));
        binding.recTest.setAdapter(adapter);
    }

    @Override
    public void initData() {
        super.initData();
        for (int i = 0; i < 10; i++) {
            list.add(new TestListModel("标题" + i,"内容" + i, R.mipmap.ic_launcher_round));
        }
        adapter.setList(list);
    }
}
