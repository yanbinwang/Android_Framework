package com.dataqin.media.utils.factory

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.provider.MediaStore
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Constants.AUDIO_FILE_PATH
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.factory.callback.OnRecorderListener
import java.io.IOException

/**
 *  Created by wangyanbin
 *  音频工具类
 */
class RecorderFactory private constructor() {
    private val mediaPlayer by lazy { MediaPlayer() }
    private var mediaRecorder: MediaRecorder? = null
    var onRecorderListener: OnRecorderListener? = null

    companion object {
        @JvmStatic
        val instance: RecorderFactory by lazy {
            RecorderFactory()
        }
    }

    //开始录音
    fun startRecord() {
        var filePath = ""
        try {
            val destDir = MediaFileUtil.getOutputMediaFile(
                MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO,
                Constants.APPLICATION_NAME + "/" + AUDIO_FILE_PATH
            )
            filePath = destDir.toString()
            mediaRecorder = MediaRecorder()
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)//设置麦克风
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            //若api低于O，调用setOutputFile(String path),高于使用setOutputFile(File path)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                mediaRecorder?.setOutputFile(filePath)
            } else {
                mediaRecorder?.setOutputFile(destDir)
            }
            mediaRecorder?.prepare()
            mediaRecorder?.start()
        } catch (e: Exception) {
        } finally {
            onRecorderListener?.onStartRecord(filePath)
        }
    }

    //停止录音
    fun stopRecord() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
        } catch (e: RuntimeException) {
        }
    }

    fun setDataSource(path: String) {
        try {
            mediaPlayer.setDataSource(path)
            mediaPlayer.isLooping = true //设置是否循环播放
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun onStart() {
        if (isPlaying()) return
        mediaPlayer.start()
    }

    fun onPause() {
        if (!isPlaying()) return
        mediaPlayer.pause()
    }

    fun onDestroy() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
        } catch (e: RuntimeException) {
        }
    }

}