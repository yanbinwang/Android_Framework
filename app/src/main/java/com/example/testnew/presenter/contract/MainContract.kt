package com.example.testnew.presenter.contract

import com.example.common.base.bridge.BasePresenter
import com.example.common.base.bridge.BaseView


/**
 * Created by WangYanBin on 2020/6/30.
 */
interface MainContract {

    abstract class Presenter : BasePresenter<View>() {

        abstract fun getUserInfo() //获取用户信息

    }

    interface View : BaseView {

        //获取用户信息回调
        fun getUserInfoSuccess(data: Any?)

        fun getUserInfoFailure(e: Throwable?, msg: String?)

    }
}