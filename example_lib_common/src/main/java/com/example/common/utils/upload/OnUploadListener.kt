package com.example.common.utils.upload

/**
 * author: wyb
 * date: 2019/1/19.
 * 图片上传回调
 */
interface OnUploadListener {

    fun onUploadImageSuccess(ossUrl: List<String>?)

    fun onUploadImageFailed()

}
