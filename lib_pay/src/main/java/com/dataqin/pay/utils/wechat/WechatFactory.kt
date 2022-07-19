package com.dataqin.pay.utils.wechat

import android.content.Context
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.ToastUtil
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import com.dataqin.pay.R
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * Created by wangyanbin
 * 微信支付工具类
 */
class WechatFactory {

    companion object {
        @JvmStatic
        val instance by lazy { WechatFactory() }
    }

    fun toPay(context: Context, req: PayReq) {
        //通过WXAPIFactory工厂，获取IWXAPI的实例
        val wxApi = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, true)
        //将应用的appId注册到微信
        wxApi.registerApp(Constants.WX_APP_ID)
        //开始支付
        if (!wxApi.isWXAppInstalled) {
            doResult(context, context.getString(R.string.toast_wechat_uninstall))
            return
        }
        if (!wxApi.isWXAppSupportAPI) {
            doResult(context, context.getString(R.string.toast_wechat_pay_error))
            return
        }
        val sendResult = wxApi.sendReq(req)
        LogUtil.e("支付状态:$sendResult")
        if (!sendResult) doResult(context, context.getString(R.string.toast_pay_failure))
    }

    private fun doResult(context: Context, text: String) {
        ToastUtil.mackToastSHORT(text, context)
        RxBus.instance.post(RxEvent(Constants.APP_PAY_FAILURE))
    }

}