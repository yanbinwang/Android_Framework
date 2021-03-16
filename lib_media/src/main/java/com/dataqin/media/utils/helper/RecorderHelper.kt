package com.dataqin.media.utils.helper

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.provider.MediaStore
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Constants.AUDIO_FILE_PATH
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.helper.callback.OnRecorderListener
import java.io.IOException

/**
 *  Created by wangyanbin
 *  音频工具类
 */
object RecorderHelper  {
    private val mediaPlayer by lazy { MediaPlayer() }
    private var mediaRecorder: MediaRecorder? = null
    var onRecorderListener: OnRecorderListener? = null

    /**
     * 开始录音
     */
    @JvmStatic
    fun startRecord() {
        var filePath = ""
        try {
            val destDir = MediaFileUtil.getOutputMediaFile(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO, Constants.APPLICATION_NAME + "/" + AUDIO_FILE_PATH)
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

    /**
     * 停止录音
     */
    @JvmStatic
    fun stopRecord() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
        } catch (e: RuntimeException) {
        }
    }

    /**
     * 设置播放的音频地址
     */
    @JvmStatic
    fun setDataSource(path: String) {
        try {
            mediaPlayer.setDataSource(path)
            mediaPlayer.isLooping = true //设置是否循环播放
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
        }
    }

    /**
     * 当前音频是否正在播放
     */
    @JvmStatic
    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    /**
     * 开始播放
     */
    @JvmStatic
    fun onStart() {
        if (isPlaying()) return
        mediaPlayer.start()
    }

    /**
     * 停止播放
     */
    @JvmStatic
    fun onPause() {
        if (!isPlaying()) return
        mediaPlayer.pause()
    }

    /**
     * 销毁-释放资源
     */
    @JvmStatic
    fun onDestroy() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
        } catch (e: RuntimeException) {
        }
    }

}