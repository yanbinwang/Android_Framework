package com.ow.testnew.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ow.basemodule.constant.Constants;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * author: wyb
 * date: 2017/12/5.
 * 微信客户端支付示例
 * 该类只负责掉起微信和支付操作的界面
 * 需要支付的界面需要配置一个去除activity动画样式的style
 * 否则会造成闪屏
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI iwxapi;// IWXAPI 是第三方app和微信通信的openapi接口

    @Override
    public void onCreate(Bundle savedInstanceState) {
        iwxapi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        iwxapi.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
//        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
//            //支付成功
//            EventBus.getDefault().post(new EventBusBean(Constants.APP_PAY_SUCCESS));
//        } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
//            //支付取消
//            EventBus.getDefault().post(new EventBusBean(Constants.APP_PAY_CANCEL));
//        } else {
//            //支付失败
//            EventBus.getDefault().post(new EventBusBean(Constants.APP_PAY_FAILURE));
//        }
        finish();
    }

}