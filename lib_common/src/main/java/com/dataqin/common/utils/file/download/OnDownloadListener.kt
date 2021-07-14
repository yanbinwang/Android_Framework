package com.dataqin.common.utils.file.download

/**
 * Created by WangYanBin on 2020/6/1.
 */
abstract class OnDownloadListener {

    /**
     * 下载开始
     */
    open fun onStart() {}

    /**
     * 下载成功
     */
    open fun onSuccess(path: String?) {
        onComplete()
    }

    /**
     * 下载进度
     */
    open fun onLoading(progress: Int = 0) {}

    /**
     * 下载失败
     */
    open fun onFailed(e: Throwable?) {
        onComplete()
    }

    /**
     * 下载完成
     */
    open fun onComplete() {}

}