package com.example.common.base;

import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.example.common.databinding.ActivityAlphaBaseBinding;
import com.example.common.databinding.ActivityBaseBinding;
import com.example.common.utils.builder.TitleBuilder;

/**
 * Created by WangYanBin on 2020/6/10.
 * 带标题的基类，将整一个xml插入容器
 */
public abstract class BaseAlphaTitleActivity<VB extends ViewBinding> extends BaseActivity<VB> {
    protected TitleBuilder titleBuilder;//标题栏

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    @Override
    public void setContentView(View view) {
        ActivityAlphaBaseBinding baseBinding = ActivityAlphaBaseBinding.inflate(getLayoutInflater());
        baseBinding.flBaseContainer.addView(binding.getRoot());
        super.setContentView(baseBinding.getRoot());
        titleBuilder = new TitleBuilder(this, baseBinding.titleContainer);
    }
    // </editor-fold>

}
