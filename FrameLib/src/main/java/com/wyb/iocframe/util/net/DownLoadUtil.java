package com.wyb.iocframe.util.net;

import android.content.Context;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;

/**
 * Created by android on 2017/5/8.
 */
public class DownLoadUtil {
    //用来标志请求的what, 类似handler的what一样，这里用来区分请求
    private static final int NOHTTP_WHAT = 0x001;
    // 请求队列
    private DownloadRequest request;
    // 请求类
    private DownloadQueue downloadQueue;
    // 回调接口
    private downloadCallBack downloadCallBack;
    //持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载
    private static DownLoadUtil instance = null;

    //私有构造方法，防止被实例化
    private DownLoadUtil(){
        downloadQueue = NoHttp.newDownloadQueue();
    }

    //静态工程方法，创建实例
    public static synchronized DownLoadUtil getInstance(){
        instance = new DownLoadUtil();
        return instance;
    }

    public void toDownLoadFile(Context context, String url, String fielDir, String fileName, downloadCallBack downloadCallBack){
        this.downloadCallBack = downloadCallBack;
        if(!isSDCardAvaiable()){
            Exception e = new mException("sd卡不存在");
            downloadCallBack.onFailureCallBack(e);
            return;
        }
        request = NoHttp.createDownloadRequest(url, fielDir, fileName, true, false);
		/*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
         * request: 请求对象
         * onResponseListener 回调对象，接受请求结果
         */
        downloadQueue.add(NOHTTP_WHAT, request, downloadListener);
    }

    //判断手机是否有sd卡
    private boolean isSDCardAvaiable() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    //自定义异常
    private class mException extends Exception{
        public mException(String msg) {
            super(msg);
        }
    }

    private DownloadListener downloadListener = new DownloadListener() {

        public void onDownloadError(int what, Exception exception) {
            if (downloadCallBack != null) {
                downloadCallBack.onFailureCallBack(exception);
            }
        }

        public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {}

        public void onProgress(int what, int progress, long fileCount, long speed) {}

        public void onFinish(int what, String filePath) {
            if(downloadCallBack != null){
                downloadCallBack.onSuccessCallBack(filePath);
            }
        }

        public void onCancel(int what) {}
    };

    public interface downloadCallBack {
        void onSuccessCallBack(String filePath);

        void onFailureCallBack(Exception e);
    }

}
