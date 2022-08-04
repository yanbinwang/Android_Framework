package com.dataqin.common.utils.dispatcher

import android.content.Context
import com.dataqin.websocket.WebSocketHandler
import com.dataqin.websocket.WebSocketSetting

object WebSocketHelper {

    /**
     * 初始化成功后即开始建立连接
     * 在application中初始化
     */
    @JvmStatic
    fun init(context: Context) {
        val setting = WebSocketSetting()
        //连接地址，必填，例如 wss://localhost:8080
        setting.connectUrl = "ws://192.168.113.115:7104/"
        //设置连接超时时间
        setting.connectTimeout = 10 * 1000
        //设置心跳间隔时间
        setting.connectionLostTimeout = 60
        //设置断开后的重连次数，可以设置的很大，不会有什么性能上的影响
        setting.reconnectFrequency = Int.MAX_VALUE
        //设置 Headers
//        setting.setHttpHeaders(header)
        //设置消息分发器，接收到数据后先进入该类中处理，处理完再发送到下游
        setting.setResponseProcessDispatcher(AppResponseDispatcher())
        //接收到数据后是否放入子线程处理，需要调用 ResponseProcessDispatcher 才有意义
        setting.setProcessDataOnBackground(true)
        //注册网络监听广播
        WebSocketHandler.registerNetworkChangedReceiver(context)
        //网络状态发生变化后是否重连，需要调用 WebSocketHandler.registerNetworkChangedReceiver(context) 才有意义
        setting.setReconnectWithNetworkChanged(true)
        //通过 init 方法初始化默认的 WebSocketManager 对象
        val manager = WebSocketHandler.init(setting)
        //启动连接
        manager.start()
    }

    /**
     * 重新建立连接
     */
    @JvmStatic
    fun reconnect() = WebSocketHandler.getDefault().reconnect()

    /**
     * 断开连接
     */
    @JvmStatic
    fun disConnect() = WebSocketHandler.getDefault().disConnect()

    /**
     * 发送消息
     */
    @JvmStatic
    fun send(text: String) = WebSocketHandler.getDefault().send(text)

}