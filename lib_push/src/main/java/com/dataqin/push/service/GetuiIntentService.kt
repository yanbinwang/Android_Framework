package com.dataqin.push.service

import android.content.Context
import com.dataqin.base.utils.LogUtil
import com.igexin.sdk.GTIntentService
import com.igexin.sdk.PushManager
import com.igexin.sdk.message.GTCmdMessage
import com.igexin.sdk.message.GTNotificationMessage
import com.igexin.sdk.message.GTTransmitMessage

/**
 *  Created by wangyanbin
 *  个推服务
 *  继承 GTIntentService 接收来自个推的消息，所有消息在线程中回调，如果注册了该服务，则务必要在 AndroidManifest 中声明，否则无法接受消息
 */
class GetuiIntentService : GTIntentService(){

    override fun onReceiveServicePid(p0: Context?, p1: Int) {
    }

    /**
     * 接收 cid
     */
    override fun onReceiveClientId(p0: Context?, p1: String?) {
    }

    /**
     * 处理透传消息
     */
    override fun onReceiveMessageData(p0: Context?, p1: GTTransmitMessage?) {
        val appid = p1?.appid
        val taskid = p1?.taskId
        val messageid = p1?.messageId
        val payload = p1?.payload
        val pkg = p1?.pkgName
        val cid = p1?.clientId
        //第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        val result = PushManager.getInstance().sendFeedbackMessage(p0, taskid, messageid, 90001)
        LogUtil.d(TAG, "call sendFeedbackMessage = " + if (result) "success" else "failed")
        LogUtil.d(
            TAG,
            "onReceiveMessageData -> appid = $appid\ntaskid = $taskid\nmessageid = $messageid\npkg = $pkg\ncid = $cid"
        )
        if(null == payload){
            LogUtil.e(TAG, "receiver payload = null")
        }else{
            val data = String(payload)
            LogUtil.d(TAG, "receiver payload = $data")
            //将payload强转为对象，传到对应的工具类中
        }
    }

    /**
     * cid 离线上线通知
     */
    override fun onReceiveOnlineState(p0: Context?, p1: Boolean) {
    }

    /**
     * 各种事件处理回执
     */
    override fun onReceiveCommandResult(p0: Context?, p1: GTCmdMessage?) {
    }

    /**
     * 通知到达，只有个推通道下发的通知会回调此方法
     */
    override fun onNotificationMessageArrived(p0: Context?, p1: GTNotificationMessage?) {
    }

    /**
     * 通知点击，只有个推通道下发的通知会回调此方法
     */
    override fun onNotificationMessageClicked(p0: Context?, p1: GTNotificationMessage?) {
    }

}