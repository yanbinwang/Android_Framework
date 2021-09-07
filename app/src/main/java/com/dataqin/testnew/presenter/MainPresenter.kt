package com.dataqin.testnew.presenter

import com.dataqin.common.base.page.setState
import com.dataqin.testnew.presenter.contract.MainContract

/**
 *  Created by wangyanbin
 *
 */
class MainPresenter : MainContract.Presenter(){

    override fun getOperation() {
        disposeView()
        getView()?.getOperation()
        getEmptyView().setState("dfsdfds")
    }

}