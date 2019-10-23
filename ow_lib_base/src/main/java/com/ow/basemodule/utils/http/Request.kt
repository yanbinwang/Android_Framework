package com.ow.basemodule.utils.http

import android.annotation.SuppressLint
import android.os.StrictMode
import android.text.TextUtils
import com.ow.basemodule.constant.RequestCode
import com.ow.basemodule.subscribe.BaseApi
import com.ow.basemodule.utils.StringUtil
import com.ow.basemodule.utils.UserUtil
import com.ow.basemodule.utils.http.encryption.RSAKeyFactory
import com.ow.basemodule.utils.http.encryption.SecurityUtil
import com.ow.framework.net.RetrofitFactory


/**
 * author: wyb
 * date: 2019/8/1.
 */
@SuppressLint("StaticFieldLeak")
open class Request {

    protected fun initRequest(): String {
        getPublicKey()
        return StringUtil.getTimeStamp()
    }

    private fun getPublicKey() {
        if (TextUtils.isEmpty(RSAKeyFactory.getInstance().strPublicKey)) {
            try {
                //规避安卓系统对于请求阻塞的策略，在主线程中发起一个获取key的请求
                StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
                val timestamp = StringUtil.getTimeStamp()
                val call = RetrofitFactory.getInstance().create(BaseApi::class.java).getKeyApi(SecurityUtil.buildHeader(RequestCode.CODE_400, timestamp), Params().getParams(timestamp))
                //发起拿取key值的请求
                val response = call.execute()
                val body = response.body()
                if (null != body) {
                    val data = body.data
                    RSAKeyFactory.getInstance().strPublicKey = data!!.k
                    RSAKeyFactory.getInstance().strEncrypt = data.encrypt
                    UserUtil.setKeyBean(body.data)
                }
            } catch (ignored: Exception) {
            }
        }
    }

}