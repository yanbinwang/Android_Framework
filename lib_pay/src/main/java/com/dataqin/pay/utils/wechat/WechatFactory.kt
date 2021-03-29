package com.dataqin.pay.utils.wechat

import android.content.Context
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.ToastUtil
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * Created by wangyanbin
 * 微信支付类
 */
class WechatFactory {

    companion object {
        @JvmStatic
        val instance: WechatFactory by lazy {
            WechatFactory()
        }
    }

    fun toPay(context: Context, req: PayReq) {
        //通过WXAPIFactory工厂，获取IWXAPI的实例
        val wxApi = WXAPIFactory.createWXAPI(context, Constants.WX_APP_ID, true)
        //将应用的appId注册到微信
        wxApi.registerApp(Constants.WX_APP_ID)
        //开始支付
        if (!wxApi.isWXAppInstalled) {
            showToast(context,"您尚未安装微信")
            return
        }
        if (!wxApi.isWXAppSupportAPI) {
            showToast(context,"当前微信版本不支持")
            return
        }
        val sendResult: Boolean = wxApi.sendReq(req)
        LogUtil.e("支付状态:$sendResult")
        if (!sendResult) {
            showToast(context,"支付失败")
        }
    }

    private fun showToast(context: Context, text: String){
        ToastUtil.mackToastSHORT(text, context)
        RxBus.instance.post(RxEvent(Constants.APP_PAY_ERROR))
    }

}