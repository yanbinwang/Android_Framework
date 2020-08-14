package com.example.testnew.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.example.common.base.BaseTitleActivity
import com.example.testnew.R
import com.example.testnew.adapter.TestAdapter
import com.example.testnew.databinding.ActivityTestBinding
import com.example.testnew.model.TestListModel
import java.util.*

/**
 * Created by WangYanBin on 2020/8/14.
 */
class TestActivity : BaseTitleActivity<ActivityTestBinding>(){
    private var list: MutableList<TestListModel> = ArrayList()
    private var adapter: TestAdapter? = null

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("列表").getDefault()
        adapter = TestAdapter(list)
        binding.recTest.layoutManager = GridLayoutManager(this, 1)
        binding.recTest.adapter = adapter
    }

    override fun initData() {
        super.initData()
        for (i in 0..9) {
            list.add(TestListModel("标题$i", "内容$i", R.mipmap.ic_launcher_round))
        }
        adapter?.setList(list)
    }

}