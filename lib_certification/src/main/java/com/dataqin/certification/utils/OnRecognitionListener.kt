package com.dataqin.certification.utils

/**
 *  Created by wangyanbin
 *  人脸识别回调监听
 */
interface OnRecognitionListener {

    fun onRecognitionWaitFor()

    fun onRecognitionSuccess(certifyId: String, response: String)

    fun onRecognitionFailure(result: String)

}