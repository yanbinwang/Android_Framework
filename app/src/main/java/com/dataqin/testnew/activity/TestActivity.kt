package com.dataqin.testnew.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.R
import com.dataqin.testnew.adapter.TestAdapter
import com.dataqin.testnew.databinding.ActivityTestBinding
import com.dataqin.testnew.model.TestListModel
import java.util.*

/**
 * Created by WangYanBin on 2020/8/14.
 */
@Route(path = ARouterPath.TestActivity)
class TestActivity : BaseTitleActivity<ActivityTestBinding>() {
    private val list by lazy { ArrayList<TestListModel>() }
    private val adapter by lazy { TestAdapter(list) }

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
        adapter.data = list
    }

}