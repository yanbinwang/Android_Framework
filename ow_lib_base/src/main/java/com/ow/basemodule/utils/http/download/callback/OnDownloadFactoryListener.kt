package com.ow.basemodule.utils.http.download.callback

/**
 * author: wyb
 * date: 2018/1/22.
 * 下载文件监听
 */
interface OnDownloadFactoryListener {

    //下载成功
    fun onDownloadSuccess(path: String?)

    //下载进度
    fun onDownloading(progress: Int?)

    //下载失败
    fun onDownloadFailed(e: Throwable?)

}
