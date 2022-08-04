package com.dataqin.media.utils

import android.content.Context
import android.media.MediaPlayer
import android.provider.MediaStore
import com.dataqin.base.utils.DateUtil
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.SdcardUtil
import com.dataqin.common.constant.Constants
import java.io.File
import java.util.*

/**
 *  Created by wangyanbin
 *  相机文件管理工具类
 */
object MediaFileUtil {
    private const val TAG = "MediaFileUtil"

    /**
     * 获取对应文件类型的存储地址
     */
    @JvmStatic
    fun getOutputFile(mimeType: Int): File? {
        if (!SdcardUtil.hasSdcard()) {
            log("未找到手机sd卡", "暂无")
            return null
        }
        //根据类型在sd卡picture目录下建立对应app名称的对应类型文件
        var prefix = "${Constants.APPLICATION_FILE_PATH}/证据文件/"
        var suffix = ""
        when (mimeType) {
            //拍照/抓拍
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                prefix += "拍照"
                suffix = "jpg"
            }
            //录像
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                prefix += "录像"
                suffix = "mp4"
            }
            //录音
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> {
                prefix += "录音"
                suffix = "wav"
            }
            //录屏
            MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST -> {
                prefix += "录屏"
                suffix = "mp4"
            }
        }
        //先在包名目录下建立对应类型的文件夹，构建失败直接返回null
        val storageDir = File(prefix)
        if (!storageDir.exists()) {
            log("开始创建文件目录", storageDir.path)
            if (!storageDir.mkdirs()) {
                log("创建文件目录失败", "暂无")
                return null
            }
        } else log("文件目录已创建", storageDir.path)
        return File("${storageDir.path}/${DateUtil.getDateTime("yyyyMMdd_HHmmss", Date())}.${suffix}")
    }

    private fun log(status: String, path: String) = LogUtil.i(TAG, " \n————————————————————————多媒体文件————————————————————————\n状态：${status}\n路径: ${path}\n————————————————————————多媒体文件————————————————————————")

    /**
     * 传入指定大小的文件长度，扫描sd卡空间是否足够
     * 需有1G的默认大小的空间
     */
    @JvmOverloads
    @JvmStatic
    fun setScanDisk(context: Context, space: Long = 1024) = SdcardUtil.getSdcardAvailableCapacity(context) > space

    /**
     * 返回时长(音频，视频)
     */
    private fun getDuration(sourcePath: String): Int {
        val file = File(sourcePath)
        val medialPlayer = MediaPlayer()
        medialPlayer.setDataSource(file.path)
        medialPlayer.prepare()
        val time = medialPlayer.duration//视频时长（毫秒）
        val duration = (time / 1000).toString()
        LogUtil.e(TAG, "时长：${duration}")
        return time
    }

}