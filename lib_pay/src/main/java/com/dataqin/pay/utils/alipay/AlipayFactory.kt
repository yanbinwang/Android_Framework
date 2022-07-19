package com.dataqin.pay.utils.alipay

import android.app.Activity
import android.os.Message
import android.text.TextUtils
import com.alipay.sdk.app.PayTask
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.ToastUtil
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.pay.R
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  支付宝支付工具栏
 */
class AlipayFactory {
    private var weakActivity: WeakReference<Activity>? = null
    private val SDK_PAY_FLAG = 1
    private val TAG = "AlipayFactory"

    companion object {
        @JvmStatic
        val instance by lazy { AlipayFactory() }
    }

    fun toPay(activity: Activity, payInfo: String) {
        weakActivity = WeakReference(activity)
        //未安装给出提示并且取消订单
        if (!FileUtil.isAvailable(weakActivity?.get()!!, "com.eg.android.AlipayGphone")) {
            doResult(weakActivity?.get()?.getString(R.string.toast_alipay_uninstall), Constants.APP_PAY_FAILURE)
            return
        }
        showToast(weakActivity?.get()?.getString(R.string.toast_alipay_pay_loading))
        Thread {
            //构造PayTask对象
            val payTask = PayTask(weakActivity?.get()!!)
            //调用支付接口，获取支付结果
            val result = payTask.pay(payInfo, true)
            LogUtil.e(TAG, "支付结果:\n$result")
            //处理结果
            if (TextUtils.isEmpty(result)) {
                doResult(weakActivity?.get()?.getString(R.string.toast_pay_failure), Constants.APP_PAY_FAILURE)
            } else {
                val msg = Message()
                msg.what = SDK_PAY_FLAG
                msg.obj = result
                weakHandler.sendMessage(msg)
            }
        }.start()
    }

    private fun doResult(text: String?, action: String) {
        showToast(text)
        RxBus.instance.post(RxEvent(action))
    }

    private fun showToast(text: String?) {
        if (!TextUtils.isEmpty(text)) ToastUtil.mackToastSHORT(text!!, weakActivity?.get()!!)
    }

    private val weakHandler = WeakHandler {
        when (it.what) {
            SDK_PAY_FLAG -> {
                val payResult = PayResult(it.obj as String)
                /**
                 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/docs/doc.htm?
                 * spm=a219a.7629140.0.0.M0HfOm&treeId=59&articleId=103671&docType=1) 建议商户依赖异步通知
                 */
                val resultStatus = payResult.resultStatus
                //判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    doResult(weakActivity?.get()?.getString(R.string.toast_pay_success), Constants.APP_PAY_SUCCESS)
                } else {
                    //用户中途取消
                    if (TextUtils.equals(resultStatus, "6001")) {
                        doResult(weakActivity?.get()?.getString(R.string.toast_pay_cancel), Constants.APP_PAY_FAILURE)
                        //正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                    } else {
                        doResult(weakActivity?.get()?.getString(R.string.toast_pay_failure), Constants.APP_PAY_FAILURE)
                    }
                }
            }
        }
        false
    }

}