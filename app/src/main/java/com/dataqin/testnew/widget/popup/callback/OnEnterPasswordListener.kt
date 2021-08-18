package com.dataqin.testnew.widget.popup.callback

interface OnEnterPasswordListener {

    /**
     * 返回数字密码
     */
    fun enterPassword(password: String?)

    /**
     * 销毁调用
     */
    fun enterPasswordDismiss()

}