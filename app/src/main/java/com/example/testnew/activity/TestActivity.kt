package com.example.testnew.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.common.base.BaseTitleActivity
import com.example.common.constant.ARouterPath
import com.example.testnew.R
import com.example.testnew.adapter.TestAdapter
import com.example.testnew.databinding.ActivityTestBinding
import com.example.testnew.model.TestListModel
import java.util.*

/**
 * Created by WangYanBin on 2020/8/14.
 */
@Route(path = ARouterPath.TestActivity)
class TestActivity : BaseTitleActivity<ActivityTestBinding>() {
    private var list = ArrayList<TestListModel>()
    private var adapter = TestAdapter(list)

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("列表").getDefault()
        binding.recTest.layoutManager = GridLayoutManager(this, 1)
        binding.recTest.adapter = adapter
    }

    override fun initData() {
        super.initData()
        for (i in 0..9) {
            list.add(TestListModel("标题$i", "内容$i", R.mipmap.ic_launcher_round))
        }
        adapter.setList(list)
    }

}