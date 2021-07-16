package com.dataqin.media.utils.helper.callback

/**
 *  Created by wangyanbin
 *  录像监听
 */
abstract class OnVideoRecordListener {

    /**
     * 开始录制
     */
    open fun onStartRecorder() {}

    /**
     * 正在录制
     */
    open fun onRecording() {}

    /**
     * 调取停止录制时会有文件存储的过程，此时会调取当前回调
     */
    open fun onTakenRecorder() {}

    /**
     * 停止录制
     */
    open fun onStopRecorder(path: String?) {}

}