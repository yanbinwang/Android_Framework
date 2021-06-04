package com.dataqin.media.utils

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
    private val outputPatch = Constants.APPLICATION_FILE_PATH + "/文件"//保存位置
    private const val TAG = "MediaFileUtil"

    //获取对应文件类型的存储地址
    @JvmStatic
    fun getOutputFile(mimeType: Int): File? {
        if (!SdcardUtil.hasSdcard()) {
            LogUtil.e(TAG, "can not get sdcard!")
            return null
        }
        //根据类型在sd卡picture目录下建立对应app名称的对应类型文件
        var prefix = Constants.APPLICATION_NAME + "/"
        var suffix = ""
        when (mimeType) {
            //拍照/抓拍
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                prefix += "拍照"
                suffix = ".jpg"
            }
            //录像
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                prefix += "录像"
                suffix = ".mp4"
            }
            //录音
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> {
                prefix += "录音"
                suffix = ".wav"
            }
            //录屏
            MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST -> {
                prefix += "录屏"
                suffix = ".mp4"
            }
        }
//        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), prefix)
        val mediaStorageDir = File("$outputPatch/$prefix")
        if (!mediaStorageDir.exists()) {
            LogUtil.i(TAG, "mkdirs: " + mediaStorageDir.path)
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.e(TAG, "failed to create directory")
                return null
            }
        } else LogUtil.i(TAG, "mkdirs,文件夹已存在： ${mediaStorageDir.path}")
        return File(mediaStorageDir.path + File.separator + DateUtil.getDateTimeStr("yyyyMMdd_HHmmss", Date()) + suffix)
    }

    /**
     * 传入指定大小的文件长度，扫描sd卡空间是否足够
     * 需有1G的默认大小的空间
     */
    @JvmStatic
    fun setScanDisk(space: Long = 1024): Boolean {
        //对本地存储空间做一次扫描检测
        val availableSize = SdcardUtil.getSdcardAvailableCapacity()
        LogUtil.e(TAG, "sd availableSize: " + availableSize + "M")
        return availableSize > space
    }

}