package com.example.common.http.upload

/**
 * author: wyb
 * date: 2019/1/19.
 * 图片上传回调
 */
interface OnFilesUploadListener {

    fun onFilesUploadStart()

    fun onFilesUploadSuccess(ossUrl: List<String>?)

    fun onFilesUploadFailed(e: Throwable?)

    fun onFilesUploadFinish()

}
