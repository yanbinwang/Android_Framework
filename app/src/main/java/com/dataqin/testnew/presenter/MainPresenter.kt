package com.dataqin.testnew.presenter

import com.dataqin.testnew.presenter.contract.MainContract

/**
 *  Created by wangyanbin
 *
 */
class MainPresenter : MainContract.Presenter(){

    override fun getOperation() {
        dispose()
        getView()?.getOperation()
    }

}