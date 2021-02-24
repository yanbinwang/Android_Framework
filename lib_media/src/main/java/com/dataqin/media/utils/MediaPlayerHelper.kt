package com.dataqin.media.utils

import android.media.MediaPlayer
import java.io.IOException

/**
 *  Created by wangyanbin
 *  多媒体工具类
 */
object MediaPlayerHelper {
    private val mediaPlayer by lazy { MediaPlayer() }

    @JvmStatic
    fun setDataSource(path: String) {
        try {
            mediaPlayer.setDataSource(path)
            mediaPlayer.isLooping = true //设置是否循环播放
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun start() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    @JvmStatic
    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    @JvmStatic
    fun destroy() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.release()
        } catch (e: RuntimeException) {
        }
    }

}