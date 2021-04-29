package com.dataqin.testnew.presenter

import com.dataqin.common.base.page.PageHandler
import com.dataqin.testnew.presenter.contract.MainContract

/**
 *  Created by wangyanbin
 *
 */
class MainPresenter : MainContract.Presenter(){

    override fun getOperation() {
        disposeView()
        getView()?.getOperation()
        PageHandler.setState(getEmptyView(),"dfsdfds")
    }

}