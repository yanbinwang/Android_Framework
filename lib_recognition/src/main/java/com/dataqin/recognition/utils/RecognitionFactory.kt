package com.dataqin.recognition.utils

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alipay.mobile.android.verify.sdk.ServiceFactory
import com.dataqin.base.utils.LogUtil
import com.dataqin.common.utils.file.FileUtil
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  识别工具类：
 *  1.传递给服务器必要的参数
 *  2.从服务器获取调起值
 *  3.开始调用
 *  4.回调参数判断
 *  1）调用回调9001则onresume中去查询结果
 *  2）未回调9001则通过onNewIntent查询结果
 *
 *  启动顺序
 *  android:launchMode="singleInstance"
 *
 *  配置uri唤起
 *  scheme, host, port, path, pathPrefix, pathPattern 匹配 Intent 中的 Data Uri
 *
 *  具体规则如下：
 *  scheme://host:port/path or pathPrefix or pathPattern
 */
class RecognitionFactory {

    companion object {
        @JvmStatic
        val instance: RecognitionFactory by lazy {
            RecognitionFactory()
        }
    }

    /**
     * 先调取本地服务器接口取号需要用到bizCode
     */
    fun getBizCode(context: Context): String {
        return ServiceFactory.build().getBizCode(context)
    }

    /**
     * 唤起支付宝人脸识别用到的bizCode则是服务器下发的
     * 监听必传
     */
    fun startService(activity: Activity, bizCode: String?, certifyId: String?, url: String?, onRecognitionListener: OnRecognitionListener) {
        val weakActivity = WeakReference(activity)
        if (!FileUtil.isAvailable(weakActivity.get()!!, "com.eg.android.AlipayGphone")) {
            onRecognitionListener.onRecognitionFailure("您尚未安装支付宝")
            return
        }

        if (TextUtils.isEmpty(certifyId) || TextUtils.isEmpty(url) || TextUtils.isEmpty(bizCode)) {
            onRecognitionListener.onRecognitionFailure("初始化失败")
            return
        }

        //封装认证数据
        val requestInfo = JSONObject()
        requestInfo["url"] = url
        requestInfo["certifyId"] = certifyId
        requestInfo["bizCode"] = bizCode
        /**
         * 发起认证
         *
         * 9001 等待支付宝端完成认证
         * 9000 认证通过，业务方需要去支付宝网关接口查询最终状态
         * 6002 网络异常
         * 6001 用户取消了业务流程，主动退出
         * 4000 系统异常
         */
        ServiceFactory.build().startService(weakActivity.get(), requestInfo) { response ->
            LogUtil.e(response.toString())
            when (response["resultStatus"]) {
                // 9001需要等待回调/回前台查询认证结果--告知页面可在onresume中去查询结果
                "9001" -> onRecognitionListener.onRecognitionWaitFor()
                //回调处理-查询结果
                "9000" -> onRecognitionListener.onRecognitionSuccess(certifyId ?: "", JSON.toJSONString(response))
                //其余编号统一回调失败
                else -> onRecognitionListener.onRecognitionFailure("认证失败,请重试")
            }
        }
    }

}