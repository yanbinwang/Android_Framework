package com.ow.testnew.wxapi;

import android.app.Activity;
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
 * 微信客户端回调activity示例
 * 该类只负责掉起微信和响应微信关闭等的操作，与支付无关
 * 用作对分享是否成功的判断
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        iwxapi.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
        finish();
    }

    @Override
    public void onReq(BaseReq arg0) {
    }

    //微信登录为getType为1，分享为0
    @Override
    public void onResp(BaseResp resp) {
//        if (1 == resp.getType()) {
//
//        } else {
//            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
//                //发送成功
//                EventBus.getDefault().post(new EventBusBean(Constants.APP_SHARE_SUCCESS));
//            } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
//                //发送取消
//                EventBus.getDefault().post(new EventBusBean(Constants.APP_SHARE_CANCEL));
//            } else {
//                //发送失败
//                EventBus.getDefault().post(new EventBusBean(Constants.APP_SHARE_FAILURE));
//            }
//        }
    }

}