package com.dataqin.websocket.dispatcher;

import android.os.Process;

import com.dataqin.websocket.utils.LogTable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于处理接收到的数据的线程
 * <p>
 * Created by ZhangKe on 2019/3/25.
 */
public class EngineThread extends Thread {
    private boolean stop;
    private ExecutorService executorService;
    private final ArrayBlockingQueue<ResponseProcessEngine.EngineEntity> jobQueue = new ArrayBlockingQueue<>(10);
    private final String TAG = "WSEngineThread";

    @Override
    public synchronized void start() {
        stop = false;
        super.start();
    }

    @Override
    public void run() {
        super.run();
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (!stop) {
            try {
                ResponseProcessEngine.EngineEntity entity = jobQueue.take();
                if (entity.isError) {
                    entity.dispatcher.onSendDataError(entity.errorResponse, entity.delivery);
                } else {
                    entity.response.onResponse(entity.dispatcher, entity.delivery);
                }
                ResponseProcessEngine.EngineEntity.release(entity);
            } catch (InterruptedException e) {
                if (stop) {
                    return;
                }
            } catch (Exception e) {
                LogTable.e(TAG, "run()->Exception", e);
            }
        }
    }

    public void add(final ResponseProcessEngine.EngineEntity entity) {
        if (!jobQueue.offer(entity)) {
            LogTable.e(TAG, "Offer response to Engine failed!start an thread to put.");
            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();
            }
            executorService.execute(() -> {
                if (stop) {
                    return;
                }
                try {
                    jobQueue.put(entity);
                } catch (Exception e) {
                    if (stop) {
                        LogTable.e(TAG, "put response failed!", e);
                    } else {
                        interrupt();
                    }
                }
            });
        }
    }

    /**
     * 结束线程
     */
    public void quit() {
        stop = true;
        jobQueue.clear();
        EngineThread.this.interrupt();
    }

}