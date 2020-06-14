package com.example.common.base;

import android.widget.FrameLayout;

import com.example.common.R;
import com.example.common.base.bridge.BasePresenter;
import com.example.common.utils.TitleBuilder;

import butterknife.ButterKnife;

/**
 * Created by WangYanBin on 2020/6/10.
 * 带标题的基类，将整一个xml插入容器
 */
public abstract class BaseTitleActivity<P extends BasePresenter> extends BaseActivity<P> {
    protected TitleBuilder titleBuilder;//标题栏

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    @Override
    public void initView() {
        super.initView();
        titleBuilder = new TitleBuilder(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);
        FrameLayout addMainContextFrame = findViewById(R.id.fl_base_container);
        addMainContextFrame.addView(getLayoutInflater().inflate(layoutResID, null));
        unBinder = ButterKnife.bind(this);
    }
    // </editor-fold>

}
