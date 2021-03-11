package com.dataqin.media.utils.helper.callback

/**
 *  Created by wangyanbin
 *  录音监听
 */
interface OnRecorderListener {

    fun onStartRecord(path: String)

    fun onStopRecord()

}