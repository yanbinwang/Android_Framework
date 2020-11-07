package com.dataqin.common.http.encryption;

import android.os.StrictMode;
import android.text.TextUtils;

import com.dataqin.base.utils.StringUtil;
import com.dataqin.common.constant.RequestCode;
import com.dataqin.common.http.factory.RetrofitFactory;
import com.dataqin.common.http.repository.ApiResponse;
import com.dataqin.common.http.repository.HttpParams;
import com.dataqin.common.model.KeyModel;
import com.dataqin.common.subscribe.CommonApi;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 请求发起前先校验key
 */
public class BaseRequest {

    //校验key
    public String proofPublicKey() {
        String timestamp = StringUtil.getTimeStamp();
        if (TextUtils.isEmpty(RSAKeyFactory.getInstance().getStrPublicKey())) {
            try {
                //规避安卓系统对于请求阻塞的策略，在主线程中发起一个获取key的请求
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                HttpParams params = new HttpParams();
                params.setTimestamp(timestamp);
                Call call = RetrofitFactory.getInstance().create(CommonApi.class).getPublicKeyApi(SecurityUtil.buildHeader(RequestCode.CODE_400, timestamp), new HttpParams().getSignParams());
                //发起拿取key值的请求
                Response response = call.execute();
                ApiResponse body = (ApiResponse) response.body();
                if (null != body) {
                    KeyModel data = (KeyModel) body.getData();
                    RSAKeyFactory.getInstance().setStrPublicKey(data.getK());
                    RSAKeyFactory.getInstance().setStrEncrypt(data.getEncrypt());
//                    UserUtil.setKeyBean(body.data);
                }
            } catch (Exception ignored) {
            }
        }
        return timestamp;
    }

    //构建请求头部
    public String buildHeader(String header, String timestamp) {
        return SecurityUtil.buildHeader(header, timestamp);
    }

}
