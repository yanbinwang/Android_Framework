package com.dataqin.media.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
//        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), prefix)
        val mediaStorageDir = File(Constants.APPLICATION_FILE_PATH + prefix)
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
    fun scanDisk(space: Long = 1024): Boolean {
        //对本地存储空间做一次扫描检测
        val availableSize = SdcardUtil.getSdcardAvailableCapacity()
        LogUtil.e(TAG, "sd availableSize: " + availableSize + "M")
        return availableSize > space
    }

    /**
     * 将bitmap存成文件至指定目录下-读写权限
     * BitmapFactory.decodeResource(resources, R.mipmap.img_qr_code)
     */
    @JvmStatic
    fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        root: String = Constants.APPLICATION_FILE_PATH + "/图片",
        formatJpg: Boolean = false,
        quality: Int = 100
    ): Boolean {
//        try {
//            val file = File(root)
//            if (!file.mkdirs()) file.createNewFile()//需要权限
//            val fileOutputStream = FileOutputStream("$root/" + DateUtil.getDateTimeStr(EN_YMDHMS, Date()) + ".jpg")
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
//            fileOutputStream.flush()
//            fileOutputStream.close()
//        } catch (ignored: Exception) {
//        } finally {
//            bitmap.recycle()
//        }
        try {
            val storeDir = File(root)
            if (!storeDir.mkdirs()) storeDir.createNewFile()//需要权限
            val file = File(
                storeDir,
                DateUtil.getDateTimeStr(EN_YMDHMS, Date()) + if (formatJpg) ".jpg" else ".png"
            )
            //通过io流的方式来压缩保存图片
            val fileOutputStream = FileOutputStream(file)
            val result = bitmap.compress(
                if (formatJpg) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
                quality,
                fileOutputStream
            )//png的话100不响应，但是可以维持图片透明度
            fileOutputStream.flush()
            fileOutputStream.close()
            //保存图片后发送广播通知更新数据库
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
            return result
        } catch (ignored: Exception) {
        } finally {
            bitmap.recycle()
        }
        return false
    }

    @JvmStatic
    fun saveBitmap(context: Context, bitmap: Bitmap, quality: Int = 100): Boolean {
        return saveBitmap(context, bitmap, Constants.APPLICATION_FILE_PATH + "/图片", true, quality)
    }

}