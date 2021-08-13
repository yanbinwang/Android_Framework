package com.dataqin.media.utils.helper

import android.graphics.Bitmap.CompressFormat
import android.media.MediaMetadataRetriever
import com.dataqin.base.utils.DateUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*
import kotlin.collections.ArrayList

/**
 * 视频抽帧工具栏
 */
object VideoHelper {

    /**
     * @param videoPath 原视频路径
     * @param savePath  抽帧后图片的保存路径
     *
     * 对原视频抽帧，按秒取一组图片（异步线程内执行）
     */
    @JvmStatic
    fun getFrames(videoPath: String, savePath: String): MutableList<String> {
        val thumbPaths = ArrayList<String>()
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)
        //取得视频的长度(单位为毫秒)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        //取得视频的长度(单位为秒)
        val seconds = Integer.valueOf(time ?: "0") / 1000
        //创建图片保存路径
        if (!File(savePath).exists()) File(savePath).mkdirs()
        //得到每一秒时刻的bitmap比如第一秒,第二秒
        for (i in 1..seconds) {
            //获取的是微秒
            val bitmap = retriever.getFrameAtTime((i * 1000 * 1000).toLong(), MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            val path = savePath + File.separator + i + ".jpg"
            thumbPaths.add(path)
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(path)
                bitmap?.compress(CompressFormat.JPEG, 100, fos)
            } catch (ignored: Exception) {
            } finally {
                try {
                    fos?.close()
                } catch (ignored: IOException) {
                }
            }
        }
        return thumbPaths
    }

    /**
     * @param videoPath 原视频路径
     * @param savePath  抽帧后图片的保存路径
     * @param second  取的图片的秒数
     *
     * 对原视频抽帧，按传入时间戳（秒）取对应图片
     */
    @Synchronized
    @JvmStatic
    fun getFrames(videoPath: String, savePath: String, second: Int): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)
        if (!File(savePath).exists()) File(savePath).mkdirs()
        //获取的是微秒
        val bitmap = retriever.getFrameAtTime((second * 1000 * 1000).toLong(), MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
//        val path = savePath + File.separator + "${DateUtil.getDateTime("yyyyMMdd_HHmmss", Date())}_${File(videoPath).name}_${second}s.jpg"
        val path = savePath + File.separator + "${File(videoPath).name}_${second}s.jpg"
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(path)
            bitmap?.compress(CompressFormat.JPEG, 100, fos)
        } catch (ignored: Exception) {
        } finally {
            try {
                fos?.close()
            } catch (ignored: IOException) {
            }
        }
        return path
    }

}