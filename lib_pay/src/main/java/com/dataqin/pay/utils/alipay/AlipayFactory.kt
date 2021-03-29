package com.dataqin.pay.utils.alipay

import android.app.Activity
import android.content.Context
import android.os.Message
import android.text.TextUtils
import com.alipay.sdk.app.PayTask
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.ToastUtil
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.utils.file.FileUtil
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *
 */
class AlipayFactory {
    private var onAlipayListener: OnAlipayListener? = null
    private val SDK_PAY_FLAG = 1
    private val TAG = "AlipayFactory"

    companion object {
        @JvmStatic
        val instance: AlipayFactory by lazy {
            AlipayFactory()
        }
    }

    fun toPay(activity: Activity, payInfo: String, onAlipayListener: OnAlipayListener?) {
        this.onAlipayListener = onAlipayListener
        val weakActivity = WeakReference(activity)
        //未安装给出提示并且取消订单
        if (!FileUtil.isAvailable(weakActivity.get()!!, "com.eg.android.AlipayGphone")) {
            showToast(weakActivity.get()!!,"您尚未安装支付宝")
            onAlipayListener?.onUninstalled()
            return
        }
        showToast(weakActivity.get()!!,"正在发起支付宝支付,请稍后...")
        Thread {
            //构造PayTask 对象
            val payTask = PayTask(weakActivity.get()!!)
            //调用支付接口，获取支付结果
            val result = payTask.pay(payInfo, true)
            LogUtil.e(TAG, "支付结果:\n$result")
            if (TextUtils.isEmpty(result)) {
                onAlipayListener?.onFailure()
            } else {
                val msg = Message()
                msg.what = SDK_PAY_FLAG
                msg.obj = result
                weakHandler.sendMessage(msg)
            }
        }.start()
    }

    private fun showToast(context: Context, text: String) = ToastUtil.mackToastSHORT(text, context)

    private val weakHandler = WeakHandler { msg ->
        when (msg.what) {
            SDK_PAY_FLAG -> {
                val payResult = PayResult(msg.obj as String)

                /**
                 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/docs/doc.htm?
                 * spm=a219a.7629140.0.0.M0HfOm&treeId=59&articleId=103671&docType=1) 建议商户依赖异步通知
                 */
                /**
                 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/docs/doc.htm?
                 * spm=a219a.7629140.0.0.M0HfOm&treeId=59&articleId=103671&docType=1) 建议商户依赖异步通知
                 */
                val resultStatus = payResult.resultStatus
                //判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    onAlipayListener?.onSuccess()
                } else {
                    //用户中途取消
                    if (TextUtils.equals(resultStatus, "6001")) {
                        onAlipayListener?.onCancel()
                        //正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                    } else {
                        onAlipayListener?.onFailure()
                    }
                }
            }
        }
        false
    }
}