package com.example.common.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding

abstract class BaseLazyFragment<VB : ViewBinding> : BaseFragment<VB>() {
    private var isLoaded = false//是否被加载

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded && !isHidden) {
            initData()
            isLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
    }
    // </editor-fold>

}