package com.example.common.base;

import android.view.View;
import android.widget.FrameLayout;

import androidx.viewbinding.ViewBinding;

import com.example.common.R;
import com.example.common.utils.builder.TitleBuilder;

/**
 * Created by WangYanBin on 2020/6/10.
 * 带标题的基类，将整一个xml插入容器
 */
public abstract class BaseTitleActivity<VB extends ViewBinding> extends BaseActivity<VB> {
    protected TitleBuilder titleBuilder;//标题栏

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    @Override
    public void initView() {
        super.initView();
        titleBuilder = new TitleBuilder(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(R.layout.activity_base);
        FrameLayout addMainContextFrame = findViewById(R.id.fl_base_container);
        addMainContextFrame.addView(binding.getRoot());
    }
    // </editor-fold>

}
