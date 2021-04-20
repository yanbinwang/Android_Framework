package com.dataqin.certification.utils

/**
 *  Created by wangyanbin
 *  人脸识别回调监听
 */
interface OnRecognitionListener {

    fun onWaitFor()

    fun onSuccess(certifyId: String, response: String)

    fun onFailure(result: String)

}