package com.example.common.utils.http.download.callback

/**
 * author: wyb
 * date: 2018/1/22.
 * 下载监听
 */
interface OnDownloadListener {

    fun onDownloadSuccess(path: String?)

    fun onDownloadFailed(e: Throwable?)

    fun onDownloadFinish()

}
