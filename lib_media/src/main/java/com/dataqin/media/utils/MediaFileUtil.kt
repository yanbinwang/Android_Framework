package com.dataqin.media.utils

import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import com.dataqin.base.utils.DateUtil
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.SdcardUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 *  Created by wangyanbin
 *  相机文件管理工具类
 */
object MediaFileUtil {
    private const val TAG = "MediaFileUtil"

    //获取对应文件类型的存储地址
    @JvmStatic
    fun getOutputMediaFile(type: Int, filePath: String?): File? {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            LogUtil.e(TAG, "can not get sdcard!")
            return null
        }
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            filePath
        )
        if (!mediaStorageDir.exists()) {
            LogUtil.i(TAG, "mkdirs: " + mediaStorageDir.path)
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.e(TAG, "failed to create directory")
                return null
            }
        } else LogUtil.i(TAG, "mkdirs,文件夹已存在： " + mediaStorageDir.path)

        return when (type) {
            //拍照/抓拍
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> getSuffix(mediaStorageDir, "jpg")
            //录像
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> getSuffix(mediaStorageDir, "mp4")
            //录音
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> getSuffix(mediaStorageDir, "wav")
            //录屏
            MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST -> getSuffix(mediaStorageDir, "mp4")
            else -> return null
        }
    }

    private fun getSuffix(mediaStorageDir: File, suffix: String) = File(mediaStorageDir.path + File.separator + DateUtil.getDateTimeStr("yyyyMMdd_HHmmss", Date()) + "." + suffix)

    //传入指定大小的文件长度，扫描sd卡空间是否足够
    @JvmStatic
    fun scanDisk(space: Long = 1024): Boolean {
        //对本地存储空间做一次扫描检测
        val availableSize = SdcardUtil.getSdcardAvailableCapacity()
        LogUtil.e(TAG, "sd availableSize: " + availableSize + "M")
        return availableSize < space
    }

    //将bitmap存成文件
    @JvmStatic
    fun saveBitmapToSd(bitmap: Bitmap, path: String?, quality: Int): Boolean {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
        return try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            LogUtil.i(TAG, "图片已经保存!")
            true
        } catch (e: FileNotFoundException) {
            LogUtil.e(TAG, "图片保存失败!")
            false
        } catch (e: IOException) {
            LogUtil.e(TAG, "图片保存失败!")
            false
        }
    }

}