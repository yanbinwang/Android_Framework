package com.dataqin.media.utils

import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import com.dataqin.base.utils.DateUtil
import com.dataqin.base.utils.DateUtil.EN_YMDHMS
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.SdcardUtil
import com.dataqin.common.constant.Constants
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 *  Created by wangyanbin
 *  相机文件管理工具类
 */
object MediaFileUtil {
    private const val TAG = "MediaFileUtil"

    //获取对应文件类型的存储地址
    @JvmStatic
    fun getOutputMediaFile(type: Int): File? {
        if (!SdcardUtil.hasSdcard()) {
            LogUtil.e(TAG, "can not get sdcard!")
            return null
        }
        //根据类型在sd卡picture目录下建立对应app名称的对应类型文件
        var prefix = Constants.APPLICATION_NAME + "/"
        var suffix = ""
        when (type) {
            //拍照/抓拍
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                prefix += "Image"
                suffix = ".jpg"
            }
            //录像
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                prefix += "Video"
                suffix = ".mp4"
            }
            //录音
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> {
                prefix += "Audio"
                suffix = ".wav"
            }
            //录屏
            MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST -> {
                prefix += "PlayList"
                suffix = ".mp4"
            }
        }
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), prefix)
        if (!mediaStorageDir.exists()) {
            LogUtil.i(TAG, "mkdirs: " + mediaStorageDir.path)
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.e(TAG, "failed to create directory")
                return null
            }
        } else LogUtil.i(TAG, "mkdirs,文件夹已存在： " + mediaStorageDir.path)
        return File(mediaStorageDir.path + File.separator + DateUtil.getDateTimeStr("yyyyMMdd_HHmmss", Date()) + suffix)
    }

    /**
     * 传入指定大小的文件长度，扫描sd卡空间是否足够
     * 需有1G的默认大小的空间
     */
    @JvmStatic
    fun scanDisk(space: Long = 1024): Boolean {
        //对本地存储空间做一次扫描检测
        val availableSize = SdcardUtil.getSdcardAvailableCapacity()
        LogUtil.e(TAG, "sd availableSize: " + availableSize + "M")
        return availableSize > space
    }

    /**
     * 将bitmap存成文件至指定目录下
     */
    @JvmStatic
    fun saveBitToSd(bitmap: Bitmap, root: String = Constants.APPLICATION_FILE_PATH + "/图片", quality: Int = 100) {
        val filePath: String
        try {
            val file = File(root)
            if (!file.mkdirs()) file.createNewFile()//需要权限
            filePath = "$root/" + DateUtil.getDateTimeStr(EN_YMDHMS, Date()) + ".jpg"
            val fileOutputStream = FileOutputStream(filePath)
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (ignored: Exception) {
        } finally {
            bitmap.recycle()
        }
//        val file = File(path)
//        if (file.exists()) file.delete()
//        return try {
//            val fileOutputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
//            fileOutputStream.flush()
//            fileOutputStream.close()
//            LogUtil.i(TAG, "图片已经保存!")
//            true
//        } catch (e: FileNotFoundException) {
//            LogUtil.e(TAG, "图片保存失败!")
//            false
//        } catch (e: IOException) {
//            LogUtil.e(TAG, "图片保存失败!")
//            false
//        } finally {
//            bitmap.recycle()
//        }
    }

}