package com.dataqin.media.utils.helper.callback

import java.io.File

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
     * 正在录制，返回设定的文件类，可获取到录制的文件的大小，文件长度等
     */
    open fun onRecording(file: File?) {}

    /**
     * 调取停止录制时会有文件存储的过程，此时会调取当前回调
     */
    open fun onTakenRecorder() {}

    /**
     * 停止录制
     */
    open fun onStopRecorder(path: String?) {}

}