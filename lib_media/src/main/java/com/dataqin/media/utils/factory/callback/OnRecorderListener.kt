package com.dataqin.media.utils.factory.callback

/**
 *  Created by wangyanbin
 *  录音监听
 */
interface OnRecorderListener {

    fun onStartRecord(path: String)

    fun onStopRecord()

}