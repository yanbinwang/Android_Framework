package com.dataqin.testnew.wxapi

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.dataqin.base.utils.ToastUtil
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 *  Created by wangyanbin
 * 微信客户端支付示例
 * 该类只负责掉起微信和支付操作的界面
 * 需要支付的界面需要配置一个去除activity动画样式的style
 * 否则会造成闪屏
 */
class WXPayEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
    private var iwxapi: IWXAPI? = null// IWXAPI 是第三方app和微信通信的openapi接口

    override fun onCreate(savedInstanceState: Bundle?) {
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID)
        iwxapi?.handleIntent(intent, this)
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        iwxapi?.handleIntent(intent, this)
    }

    override fun onReq(req: BaseReq?) {
    }

    override fun onResp(resp: BaseResp?) {
        when (resp?.errCode) {
            //支付成功
            BaseResp.ErrCode.ERR_OK -> doResult("支付成功", Constants.APP_PAY_SUCCESS)
            //支付取消
            BaseResp.ErrCode.ERR_USER_CANCEL -> doResult("支付取消", Constants.APP_PAY_FAILURE)
            //支付失败
            else -> doResult("支付失败", Constants.APP_PAY_FAILURE)
        }
        finish()
    }

    private fun doResult(text: String?, action: String) {
        if (!TextUtils.isEmpty(text)) ToastUtil.mackToastSHORT(text!!, this)
        RxBus.instance.post(RxEvent(action))
    }

}