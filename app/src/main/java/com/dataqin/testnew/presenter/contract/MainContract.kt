package com.dataqin.testnew.presenter.contract

import com.dataqin.common.base.bridge.BasePresenter
import com.dataqin.common.base.bridge.BaseView

/**
 * Created by WangYanBin
 * 首页
 */
interface MainContract {

    abstract class Presenter : BasePresenter<View>() {

        abstract fun getOperation() //开始执行事务

    }

    interface View : BaseView {

        //执行事务回调
        fun getOperation()

    }

}