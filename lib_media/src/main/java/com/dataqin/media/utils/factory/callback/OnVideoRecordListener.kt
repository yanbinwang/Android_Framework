package com.dataqin.media.utils.factory.callback

/**
 *  Created by wangyanbin
 *  录像监听
 */
interface OnVideoRecordListener {

    fun onStartRecorder()

    fun onRecorderTaken(path: String)

    fun onStopRecorder()

}