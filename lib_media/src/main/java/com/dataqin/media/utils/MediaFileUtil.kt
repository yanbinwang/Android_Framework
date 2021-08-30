package com.dataqin.media.utils

import android.content.Context
import android.os.Looper
import android.provider.MediaStore
import android.util.Base64
import com.dataqin.base.utils.DateUtil
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.SdcardUtil
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.media.utils.helper.VideoHelper
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 *  Created by wangyanbin
 *  相机文件管理工具类
 */
object MediaFileUtil {
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }
    private const val TAG = "MediaFileUtil"

    //获取对应文件类型的存储地址
    @JvmStatic
    fun getOutputFile(mimeType: Int): File? {
        if (!SdcardUtil.hasSdcard()) {
            LogUtil.e(TAG, "can not get sdcard!")
            return null
        }
        //根据类型在sd卡picture目录下建立对应app名称的对应类型文件
        var prefix = Constants.APPLICATION_FILE_PATH + "/文件/"
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
        val mediaStorageDir = File(prefix)
        if (!mediaStorageDir.exists()) {
            LogUtil.i(TAG, "mkdirs: " + mediaStorageDir.path)
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.e(TAG, "failed to create directory")
                return null
            }
        } else LogUtil.i(TAG, "mkdirs,文件夹已存在： ${mediaStorageDir.path}")
        return File(
            mediaStorageDir.path + File.separator + DateUtil.getDateTime(
                "yyyyMMdd_HHmmss",
                Date()
            ) + suffix
        )
    }

    /**
     * 传入指定大小的文件长度，扫描sd卡空间是否足够
     * 需有1G的默认大小的空间
     */
    @JvmStatic
    fun setScanDisk(context: Context, space: Long = 1024): Boolean {
        //对本地存储空间做一次扫描检测
        val availableSize = SdcardUtil.getSdcardAvailableCapacity(context)
        LogUtil.e(TAG, "sd availableSize: " + availableSize + "M")
        return availableSize > space
    }

    /**
     * 传入视频原路径，并通过秒数集合，批量生成图片，并打包成压缩包保存到指定路径下
     */
    @JvmStatic
    fun setHandleVideo(videoPath: String, secondList: MutableList<Int>, zipFilePath: String, onThreadListener: FileUtil.OnThreadListener?) {
        Thread {
            weakHandler.post { onThreadListener?.onStart() }
            //在‘视频抽帧’文件夹下建立一个以抽帧文件名命名的文件夹，方便后续对当前文件夹打压缩包
            val savePath = Constants.APPLICATION_FILE_PATH + "/文件/视频抽帧/${File(videoPath).name}"
            val thumbPaths = ArrayList<String>()
            for (i in secondList) {
                val thumbPath = VideoHelper.getFrames(videoPath, savePath, i)
                thumbPaths.add(thumbPath)
            }
            try {
                FileUtil.zipFolder(savePath, zipFilePath)
            } catch (ignored: Exception) {
            } finally {
                //清空当前文件夹和其下的所有图片
                FileUtil.deleteDir(savePath)
                weakHandler.post { onThreadListener?.onStop() }
            }
        }.start()
    }

    /**
     * base64文件流的形式加载文件，需要先下载，之后在放置
     */
    @JvmStatic
    fun getBase64File(base64: String, suffix: String, root: String = Constants.APPLICATION_FILE_PATH + "/缓存", clear: Boolean = true, onThreadListener: FileUtil.OnThreadListener?) {
        Thread {
            weakHandler.post { onThreadListener?.onStart() }
            if (clear) FileUtil.deleteDir(root)
            val storeDir = File(root)
            if (!storeDir.mkdirs()) storeDir.createNewFile()
            val file = File(storeDir, "${System.currentTimeMillis()}_cache${suffix}")
            val pdfAsBytes = Base64.decode(base64, 0)
            val fileOutputStream: FileOutputStream?
            try {
                fileOutputStream = FileOutputStream(file, false)
                fileOutputStream.write(pdfAsBytes)
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: Exception) {
            } finally {
                weakHandler.post { onThreadListener?.onStop(file.absolutePath) }
            }
        }.start()
    }

}