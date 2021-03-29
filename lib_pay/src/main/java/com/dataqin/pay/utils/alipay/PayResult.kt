package com.dataqin.pay.utils.alipay

import android.text.TextUtils

/**
 *  Created by wangyanbin
 *  支付宝支付參數类
 */
class PayResult {
    var resultStatus: String? = null
    var result: String? = null
    var memo: String? = null

    constructor(rawResult: String?) {
        if (TextUtils.isEmpty(rawResult)) return
        val resultParams = rawResult!!.split(";".toRegex()).toTypedArray()
        for (resultParam in resultParams) {
            if (resultParam.startsWith("resultStatus")) {
                resultStatus = gatValue(resultParam, "resultStatus")
            }
            if (resultParam.startsWith("result")) {
                result = gatValue(resultParam, "result")
            }
            if (resultParam.startsWith("memo")) {
                memo = gatValue(resultParam, "memo")
            }
        }
    }

    private fun gatValue(content: String, key: String): String? {
        val prefix = "$key={"
        return content.substring(content.indexOf(prefix) + prefix.length, content.lastIndexOf("}"))
    }

    override fun toString(): String {
        return ("resultStatus={$resultStatus};memo={$memo};result={$result}")
    }

}