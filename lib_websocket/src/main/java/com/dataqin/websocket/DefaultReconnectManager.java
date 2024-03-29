package com.dataqin.websocket;

import com.dataqin.websocket.utils.LogTable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * 负责 WebSocket 重连
 * <p>
 * Created by ZhangKe on 2018/6/24.
 */
public class DefaultReconnectManager implements ReconnectManager {
    private int reconnectCount = 1;
    private int finishCount = 1;
    private WebSocketManager mWebSocketManager;
    /**
     * 是否正在重连
     */
    private volatile boolean reconnecting;
    /**
     * 被销毁
     */
    private volatile boolean destroyed;
    /**
     * 是否需要停止重连
     */
    private volatile boolean needStopReconnect = false;
    /**
     * 是否已连接
     */
    private volatile boolean connected = false;
    /**
     * 重连锁
     */
    private final Object BLOCK = new Object();
    private final OnConnectListener mOnDisconnectListener;
    private final ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    private static final String TAG = "WSDefaultRM";

    public DefaultReconnectManager(WebSocketManager webSocketManager, OnConnectListener onDisconnectListener) {
        this.mWebSocketManager = webSocketManager;
        this.mOnDisconnectListener = onDisconnectListener;
        reconnecting = false;
        destroyed = false;
    }

    @Override
    public boolean reconnecting() {
        return reconnecting;
    }

    @Override
    public void startReconnect() {
        if (reconnecting) {
            LogTable.i(TAG, "Reconnecting, do not call again.");
            return;
        }
        if (destroyed) {
            LogTable.e(TAG, "ReconnectManager is destroyed!!!");
            return;
        }
        needStopReconnect = false;
        reconnecting = true;
        try {
            singleThreadPool.execute(getReconnectRunnable());
        } catch (RejectedExecutionException e) {
            LogTable.e(TAG, "线程队列已满，无法执行此次任务。", e);
            reconnecting = false;
        }
    }

    private Runnable getReconnectRunnable() {
        return () -> {
            if (destroyed || needStopReconnect) {
                reconnecting = false;
                return;
            }
            LogTable.d(TAG, "开始重连:" + reconnectCount);
            reconnectCount++;
            reconnecting = true;
            connected = false;
            try {
                int count = mWebSocketManager.getSetting().getReconnectFrequency();
                for (int i = 0; i < count; i++) {
                    LogTable.i(TAG, String.format("第%s次重连", i + 1));
                    mWebSocketManager.reconnectOnce();
                    synchronized (BLOCK) {
                        try {
                            BLOCK.wait(mWebSocketManager.getSetting().getConnectTimeout());
                            if (connected) {
                                LogTable.i(TAG, "reconnectOnce success!");
                                mOnDisconnectListener.onConnected();
                                return;
                            }
                            if (needStopReconnect) {
                                break;
                            }
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
                //重连失败
                LogTable.i(TAG, "reconnectOnce failed!");
                mOnDisconnectListener.onDisconnect();
            } finally {
                LogTable.d(TAG, "重连结束:" + finishCount);
                finishCount++;
                reconnecting = false;
                LogTable.i(TAG, "reconnecting = false");
            }
        };
    }

    @Override
    public void stopReconnect() {
        needStopReconnect = true;
        if (singleThreadPool != null) {
            singleThreadPool.shutdownNow();
        }
    }

    @Override
    public void onConnected() {
        connected = true;
        synchronized (BLOCK) {
            LogTable.i(TAG, "onConnected()->BLOCK.notifyAll()");
            BLOCK.notifyAll();
        }
    }

    @Override
    public void onConnectError(Throwable th) {
        connected = false;
        synchronized (BLOCK) {
            LogTable.i(TAG, "onConnectError(Throwable)->BLOCK.notifyAll()");
            BLOCK.notifyAll();
        }
    }

    /**
     * 销毁资源，并停止重连
     */
    @Override
    public void destroy() {
        destroyed = true;
        stopReconnect();
        mWebSocketManager = null;
    }

}