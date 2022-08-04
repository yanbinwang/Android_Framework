package com.dataqin.media.utils.helper

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.provider.MediaStore
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.helper.callback.OnRecorderListener

/**
 *  Created by wangyanbin
 *  音频工具类
 */
object RecorderHelper {
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
            val recorderFile = MediaFileUtil.getOutputFile(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)
            filePath = recorderFile.toString()
            mediaRecorder = MediaRecorder()
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)//设置麦克风
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                //若api低于O，调用setOutputFile(String path),高于使用setOutputFile(File path)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    setOutputFile(filePath)
                } else {
                    setOutputFile(recorderFile)
                }
                prepare()
                start()
            }
        } catch (ignored: Exception) {
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
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (ignored: Exception) {
        } finally {
            onRecorderListener?.onStopRecord()
        }
    }

    /**
     * 设置播放的音频地址
     */
    @JvmStatic
    fun setDataSource(path: String) {
        try {
            mediaPlayer.apply {
                setDataSource(path)
                isLooping = true //设置是否循环播放
                prepareAsync()
            }
        } catch (ignored: Exception) {
        }
    }

    /**
     * 当前音频是否正在播放
     */
    @JvmStatic
    fun isPlaying() = mediaPlayer.isPlaying

    /**
     * 开始播放
     */
    @JvmStatic
    fun onStart() {
        try {
            if (isPlaying()) return
            mediaPlayer.start()
        } catch (ignored: Exception) {
        }
    }

    /**
     * 停止播放
     */
    @JvmStatic
    fun onPause() {
        try {
            if (!isPlaying()) return
            mediaPlayer.pause()
        } catch (ignored: Exception) {
        }
    }

    /**
     * 销毁-释放资源
     */
    @JvmStatic
    fun onDestroy() {
        try {
            mediaPlayer.apply {
                stop()
                reset()
                release()
            }
        } catch (ignored: Exception) {
        }
    }

}