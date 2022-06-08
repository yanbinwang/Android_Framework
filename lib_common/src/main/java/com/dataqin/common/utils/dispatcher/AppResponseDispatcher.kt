package com.dataqin.common.utils.dispatcher

import android.os.Looper
import android.text.TextUtils
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.WeakHandler
import com.zhangke.websocket.dispatcher.IResponseDispatcher
import com.zhangke.websocket.dispatcher.ResponseDelivery
import com.zhangke.websocket.response.ErrorResponse
import org.java_websocket.framing.Framedata
import java.nio.ByteBuffer

/**
 * app消息分发器
 */
class AppResponseDispatcher : IResponseDispatcher {
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }

    override fun onConnected(delivery: ResponseDelivery?) {
        log("连接成功")
    }

    override fun onConnectFailed(cause: Throwable?, delivery: ResponseDelivery?) {
        log("连接失败")
    }

    override fun onDisconnect(delivery: ResponseDelivery?) {
        log("连接断开")
    }

    override fun onMessage(message: String?, delivery: ResponseDelivery?) {
        log("接收到文本消息:\n${message}")
        if (!TextUtils.isEmpty(message)) {
            weakHandler.post {

            }
        }
    }

    override fun onMessage(byteBuffer: ByteBuffer?, delivery: ResponseDelivery?) {
        log("接收到二进制消息:\n${byteBuffer}")
    }

    override fun onPing(framedata: Framedata?, delivery: ResponseDelivery?) {
        log("接收到 ping:\n${framedata}")
    }

    override fun onPong(framedata: Framedata?, delivery: ResponseDelivery?) {
        log("接收到 pong:\n${framedata}")
    }

    override fun onSendDataError(error: ErrorResponse?, delivery: ResponseDelivery?) {
        log("消息发送失败或接受到错误消息:\n${error}")
    }

    private fun log(msg: String) = LogUtil.e("WebSocketHelper", msg)

}