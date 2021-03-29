package com.dataqin.pay.utils.alipay

/**
 *  Created by wangyanbin
 *  支付宝回调监听
 */
interface OnAlipayListener {
    /**
     * 未安装
     */
    fun onUninstalled()

    /**
     * 成功付款
     */
    fun onSuccess()

    /**
     * 取消付款
     */
    fun onCancel()

    /**
     * 付款失败
     */
    fun onFailure()
}