package com.dataqin.push.service

import android.content.Context
import com.dataqin.base.utils.LogUtil
import com.dataqin.common.BuildConfig
import com.dataqin.common.utils.analysis.GsonUtil
import com.dataqin.push.model.PayLoad
import com.dataqin.push.utils.PushHelper
import com.igexin.sdk.GTIntentService
import com.igexin.sdk.PushManager
import com.igexin.sdk.message.GTCmdMessage
import com.igexin.sdk.message.GTNotificationMessage
import com.igexin.sdk.message.GTTransmitMessage

/**
 *  Created by wangyanbin
 *  个推服务
 *  继承 GTIntentService 接收来自个推的消息，所有消息在线程中回调，如果注册了该服务，则务必要在 AndroidManifest 中声明，否则无法接受消息
 *
 *  <!-- 个推 -->
 *  <service
 *     android:name="com.dataqin.push.service.GetuiPushService"
 *     android:exported="true"
 *     android:label="PushService"
 *     android:process=":pushservice"/>
 *  <service
 *     android:name="com.dataqin.push.service.GetuiIntentService"
 *     android:permission="android.permission.BIND_JOB_SERVICE"/>
 */
class GetuiIntentService : GTIntentService() {

    override fun onReceiveServicePid(context: Context?, pid: Int) {}

    /**
     * 接收 cid
     */
    override fun onReceiveClientId(context: Context?, clientid: String?) {
        LogUtil.e(TAG, "onReceiveClientId -> clientid = $clientid")
    }

    /**
     * 处理透传消息
     */
    override fun onReceiveMessageData(context: Context?, msg: GTTransmitMessage?) {
        val appid = msg?.appid
        val taskid = msg?.taskId
        val messageid = msg?.messageId
        val payload = msg?.payload
        val pkg = msg?.pkgName
        val cid = msg?.clientId
        //第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        val result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001)
        LogUtil.d(TAG, "call sendFeedbackMessage = " + if (result) "success" else "failed")
        LogUtil.d(TAG, "onReceiveMessageData -> appid = $appid\ntaskid = $taskid\nmessageid = $messageid\npkg = $pkg\ncid = $cid")
        if (null == payload) {
            LogUtil.e(TAG, "receiver payload = null")
        } else {
            val data = String(payload)
            LogUtil.d(TAG, "receiver payload = $data")
            //将payload强转为对象，传到对应的工具类中
            val payLoad = GsonUtil.jsonToObj(data, PayLoad::class.java)
            if (null != payLoad && !(BuildConfig.DEBUG && payLoad.env.equals("prod"))) {
                LogUtil.d(TAG, "符合推送环境要求")
                PushHelper.send(payLoad)
            }
        }
    }

    /**
     * cid 离线上线通知
     */
    override fun onReceiveOnlineState(context: Context?, online: Boolean) {}

    /**
     * 各种事件处理回执
     */
    override fun onReceiveCommandResult(context: Context?, cmdMessage: GTCmdMessage?) {}

    /**
     * 通知到达，只有个推通道下发的通知会回调此方法
     */
    override fun onNotificationMessageArrived(context: Context?, gtNotificationMessage: GTNotificationMessage?) {}

    /**
     * 通知点击，只有个推通道下发的通知会回调此方法
     */
    override fun onNotificationMessageClicked(context: Context?, gtNotificationMessage: GTNotificationMessage?) {}

}