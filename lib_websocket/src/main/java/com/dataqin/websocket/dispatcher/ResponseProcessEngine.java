package com.dataqin.websocket.dispatcher;


import com.dataqin.websocket.response.ErrorResponse;
import com.dataqin.websocket.response.Response;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 响应消息处理类
 * <p>
 * Created by ZhangKe on 2019/3/25.
 */
public class ResponseProcessEngine {
    private final EngineThread mThread;

    public ResponseProcessEngine() {
        mThread = new EngineThread();
        mThread.start();
    }

    public void onMessageReceive(Response message, IResponseDispatcher dispatcher, ResponseDelivery delivery) {
        if (message == null || dispatcher == null || delivery == null) {
            return;
        }
        EngineEntity entity = EngineEntity.obtain();
        entity.dispatcher = dispatcher;
        entity.delivery = delivery;
        entity.isError = false;
        entity.response = message;
        entity.errorResponse = null;
        mThread.add(entity);
    }

    public void onSendDataError(ErrorResponse errorResponse, IResponseDispatcher dispatcher, ResponseDelivery delivery) {
        if (errorResponse == null || dispatcher == null || delivery == null) {
            return;
        }
        EngineEntity entity = EngineEntity.obtain();
        entity.dispatcher = dispatcher;
        entity.delivery = delivery;
        entity.isError = true;
        entity.errorResponse = errorResponse;
        entity.response = null;
        mThread.add(entity);
    }

    public static class EngineEntity {
        boolean isError;
        Response response;
        ErrorResponse errorResponse;
        IResponseDispatcher dispatcher;
        ResponseDelivery delivery;
        private static final Queue<EngineEntity> ENTITY_POOL = new ArrayDeque<>(10);

        static EngineEntity obtain() {
            EngineEntity engineEntity = ENTITY_POOL.poll();
            if (engineEntity == null) {
                engineEntity = new EngineEntity();
            }
            return engineEntity;
        }

        static void release(EngineEntity entity) {
            ENTITY_POOL.offer(entity);
        }
    }

}