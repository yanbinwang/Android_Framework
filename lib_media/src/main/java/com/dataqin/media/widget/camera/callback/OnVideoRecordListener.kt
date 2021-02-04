package com.dataqin.media.widget.camera.callback

/**
 *  Created by wangyanbin
 *  录像监听
 */
interface OnVideoRecordListener {

    fun onStartRecorder(path: String?)

    fun onStopRecorder()

}