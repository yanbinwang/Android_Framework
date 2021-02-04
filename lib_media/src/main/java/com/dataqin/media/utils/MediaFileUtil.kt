package com.dataqin.media.utils

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.dataqin.base.utils.LogUtil
import com.dataqin.common.constant.Constants.VIDEO_FILE_PATH
import com.dataqin.media.model.MediaFileInfoModel
import java.io.File
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Created by wangyanbin
 *  相机文件管理工具类
 */
object MediaFileUtil {
    private const val TYPE_PHOTO = 0
    private const val TYPE_VIDEO = 1
    private const val TAG = "MediaFileUtil"

    //获取对应文件类型的存储地址
    @JvmStatic
    fun getOutputMediaFile(type: Int, filePath: String?): File? {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            LogUtil.e(TAG, "can not get sdcard!")
            return null
        }
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filePath)
        if (!mediaStorageDir.exists()) {
            LogUtil.i(TAG, "mkdirs: " + mediaStorageDir.path)
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.e(TAG, "failed to create directory")
                return null
            }
        } else {
            LogUtil.i(TAG, "mkdirs,文件夹已存在： " + mediaStorageDir.path)
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return when (type) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> File(mediaStorageDir.path + File.separator + timeStamp + ".jpg")
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> File((mediaStorageDir.path + File.separator + timeStamp + ".mp4"))
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO -> File((mediaStorageDir.path + File.separator + timeStamp + ".wav"))
            MediaStore.Files.FileColumns.MEDIA_TYPE_PLAYLIST -> File((mediaStorageDir.path + File.separator + timeStamp + ".mp4"))
            else -> return null
        }
    }

    //获取对应大小的文字
    @JvmStatic
    fun getFormatSize(size: Double): String {
        val byteResult = size / 1024
        if (byteResult < 1) {
//            return size + "Byte";
            return "<1K"
        }
        val kiloByteResult = byteResult / 1024
        if (kiloByteResult < 1) {
            val result1 = BigDecimal(byteResult.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "K"
        }
        val mByteResult = kiloByteResult / 1024
        if (mByteResult < 1) {
            val result2 = BigDecimal(kiloByteResult.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "M"
        }
        val gigaByteResult = mByteResult / 1024
        if (gigaByteResult < 1) {
            val result3 = BigDecimal(mByteResult.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val teraByteResult = BigDecimal(gigaByteResult)
        return (teraByteResult.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB")
    }

    //扫描磁盘空间
    @JvmStatic
    fun spaceScanning(context: Context): Boolean {
        var isEnableRecord = true
        //对本地存储空间做一次扫描检测
        val availableSize: Long = SdCardUtil.getSDAvailableSize(context)
        LogUtil.e(TAG,"sd availableSize: " + availableSize + "M")
        if (availableSize < 1024) {
            var successCount = 0
            LogUtil.e(TAG,"剩余空间少于1G，开始删除文件!")
            val path: String? = getMediaStorageDir(VIDEO_FILE_PATH)
            if (path != null) {
                val fileFileInfoArrayList = getListFilesByTime(path, TYPE_VIDEO)
                LogUtil.e(TAG,"GetFiles: $fileFileInfoArrayList")
                //删除最早的三个文件
                if (fileFileInfoArrayList.size > 3) {
                    for (i in 0..2) {
                        val file = File(fileFileInfoArrayList[i].path)
                        if (file.exists()) {
                            val result = file.delete()
                            LogUtil.e(TAG,"recycleSdSpace: " + result + ",file: " + file.name)
                            successCount++
                        }
                    }
                }
                if (successCount < 2) {
                    isEnableRecord = false
                    LogUtil.e(TAG,"空间不足，无法开始录像!")
                } else {
                    isEnableRecord = true
                }
            }
        }
        return isEnableRecord
    }

    //获取文件存储文件夹
    @JvmStatic
    fun getMediaStorageDir(filePath: String?): String? {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            LogUtil.e(TAG, "can not get sdcard!")
            return null
        }
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filePath)
        if (!mediaStorageDir.exists()) {
            LogUtil.i(TAG, "mkdirs: " + mediaStorageDir.path)
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.e(TAG, "failed to create directory")
                return null
            }
        } else {
            LogUtil.i(TAG, "mkdirs,文件夹已存在： " + mediaStorageDir.path)
        }
        return mediaStorageDir.path
    }

    private fun getListFilesByTime(path: String?, fileType: Int): ArrayList<MediaFileInfoModel> {
        val files = if (fileType == TYPE_PHOTO) {
            File(path).listFiles { file ->
                val tmp = file.name.toLowerCase()
                tmp.endsWith(".png") || tmp.endsWith(".jpg")
            }
        } else {
            File(path).listFiles { file ->
                val tmp = file.name.toLowerCase()
                tmp.endsWith(".mp4")
            }
        }
        val fileList = ArrayList<MediaFileInfoModel>() //将需要的子文件信息存入到FileInfo里面
        for (i in files.indices) {
            val file = files[i]
            val fileInfo = MediaFileInfoModel()
            fileInfo.name = file.name
            fileInfo.path = file.path
            fileInfo.lastModified = file.lastModified()
            fileList.add(fileInfo)
        }
        //通过重写Comparator的实现类FileComparator来实现按文件创建时间排序。按时间从小到大排序
        fileList.sortWith { file1, file2 -> if (file1!!.lastModified < file2!!.lastModified) -1 else 1 }
        return fileList
    }

}