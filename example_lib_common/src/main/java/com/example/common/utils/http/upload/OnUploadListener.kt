package com.example.common.utils.http.upload

/**
 * author: wyb
 * date: 2019/1/19.
 * 图片上传回调
 */
interface OnUploadListener {

    fun onUploadSuccess(ossUrl: List<String>?)

    fun onUploadFailed()

}
