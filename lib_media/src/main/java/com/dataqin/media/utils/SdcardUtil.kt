package com.dataqin.media.utils

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.dataqin.base.utils.LogUtil.d
import com.dataqin.media.model.SdcardFormat
import com.dataqin.media.model.SdcardStateModel
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

/**
 *  Created by wangyanbin
 *  sd卡工具类
 */
object SdcardUtil {
    private val SDCARD_PATH = getSDCardPath()
    private const val DIR_SINGLE_SDCARD_NAME = "内置存储卡"
    private const val DIR_SDCARD_NAME = "内置存储卡"
    private const val DIR_EXT_SDCARD_NAME = "扩展存储卡"
    private const val SD_PHY_SIZE_1G = (1000 * 1000 * 1000).toLong()
    private const val SD_LOGIC_SIZE_1G = (1024 * 1024 * 1024).toLong()
    private const val SD_LOGIC_DIFF = SD_LOGIC_SIZE_1G / SD_PHY_SIZE_1G.toDouble()
    private const val TAG = "SdcardUtil"

    // <editor-fold defaultstate="collapsed" desc="sd卡容量的操作方法">
    /**
     * 获得内置sd卡剩余容量，即可用大小，单位M
     * @param context
     * @return
     */
    fun getInnerSDAvailableSize(context: Context): Long {
        val sdcardStateModelList = getSdCardStateModels(context)
        return if (sdcardStateModelList.size > 0) {
            sdcardStateModelList[0].freeSize / 1024 / 1024
        } else {
            0
        }
    }

    /**
     * 获得内置sd卡已用容量 单位M
     * @param context
     * @return
     */
    fun getInnerSDUsedSize(context: Context): Long {
        val sdcardStateModelList = getSdCardStateModels(context)
        return if (sdcardStateModelList.size > 0) {
            val sdCardStateModel = sdcardStateModelList[0]
            (sdCardStateModel.totalSize - sdCardStateModel.freeSize) / 1024 / 1024
        } else {
            0
        }
    }

    /**
     * 获得sd卡剩余容量，即可用大小，单位M
     * @param context
     * @return
     */
    fun getSDAvailableSize(context: Context): Long {
        val sdcardStateModelList = getSdCardStateModels(context)
        return if (sdcardStateModelList.size > 0) {
            sdcardStateModelList[sdcardStateModelList.size - 1].freeSize / 1024 / 1024
        } else {
            0
        }
    }

    /**
     * 获得sd卡已用容量 单位M
     * @param context
     * @return
     */
    fun getSDUsedSize(context: Context): Long {
        val sdcardStateModelList = getSdCardStateModels(context)
        return if (sdcardStateModelList.size > 0) {
            val sdCardStateModel = sdcardStateModelList[sdcardStateModelList.size - 1]
            (sdCardStateModel.totalSize - sdCardStateModel.freeSize) / 1024 / 1024
        } else {
            0
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sd卡基础属性获取方法">
    /**
     * sd卡是否可用
     * @return
     */
    @JvmStatic
    fun isMounted(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * 获取sd卡路径
     * @return
     */
    @JvmStatic
    fun getSDCardPath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }

    /**
     * 传入的sd卡路径是否可再次写入
     * @param path
     * @return
     */
    @JvmStatic
    fun sdCardCanWrite(path: String?): Boolean {
        if (path == null) {
            return false
        }
        val sdCardRoot = File(path)
        if (!sdCardRoot.canWrite()) {
            return false
        }
        //canWrite() 在4.4系统不起作用，只要路径存在总是返回true
        val testPath = File(File(path), ".testwrite" + System.currentTimeMillis())
        return if (testPath.mkdirs()) {
            testPath.delete()
            true
        } else {
            false
        }
    }

    /**
     * 获取SD卡显示路径.<br>
     * 类似<strong>/storage/emulated/0</strong>不需要显示路径.<br>
     * 类似<strong>/storage/extSdCard/Android/data/{pageageName}/files</strong>
     * 只显示从<strong>/Android</strong>开头的路径.
     */
    fun getShowSDPath(stat: SdcardStateModel): String {
        var showPath = ""
        val path = stat.rootPath
        if (!stat.canWrite) {
            val index = path!!.indexOf("Android/data/")
            if (index != -1) {
                showPath = path.substring(index)
            }
        } else {
            showPath = path!!.substring(path.lastIndexOf(File.separator) + 1)
            if (showPath == "0") {
                showPath = ""
            }
        }
        return showPath
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sd卡详细的对象获取">
    fun getSdCardStateModels(context: Context): ArrayList<SdcardStateModel> {
        var list = ArrayList<SdcardStateModel>()
        try {
            val process = Runtime.getRuntime().exec("mount")
            val br = BufferedReader(InputStreamReader(process.inputStream))
            var str: String
            var lowerStr: String
            while (br.readLine().also { str = it } != null) {
                lowerStr = str.toLowerCase()
                if (!testBasicFilter(lowerStr)) {
                    continue
                }
                val cols = str.split("\\s+").toTypedArray() ?: continue
                val path = findSDCardPath(cols)
                d(TAG, "path--------0-------$path")
                if (TextUtils.isEmpty(path)) {
                    continue
                }
                val format = findSDCardFormat(cols) ?: continue
                val minorIdx = if (SdcardFormat.vfat === format || SdcardFormat.exfat === format || SdcardFormat.texfat === format) findVoldDevNodeMinorIndex(cols) else -100
                val stat = SdcardStateModel(path!!, format, minorIdx, "")
                d(TAG, "path--------1-------$path")
                if (!compareData(list, stat.totalSize)) {
                    continue
                }

                if (!sdCardCanWrite(path)) {
                    stat.canWrite = false
                    val filePath = ContextCompat.getExternalFilesDirs(context, null)
                    for (f in filePath) {
                        if (f != null) {
                            if (f.absolutePath.startsWith(path)) {
                                stat.rootPath = f.absolutePath
                                d(TAG, "path--------if-------$path")
                                list.add(stat)
                                break
                            }
                        }
                    }
                } else {
                    d(TAG, "path--------else-------$path")
                    list.add(stat)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return list
        }
        list = sortSDCardList(list)
        var idx = 0
        val size = list.size
        while (idx < size) {
            when (idx) {
                0 -> list[0].name = if (size == 1) DIR_SINGLE_SDCARD_NAME else DIR_SDCARD_NAME
                1 -> list[1].name = DIR_EXT_SDCARD_NAME
                else -> list[idx].name = DIR_EXT_SDCARD_NAME + idx
            }
            idx++
        }
        return list
    }

    //是否能通过基本过滤
    private fun testBasicFilter(str: String): Boolean {
        val keys = arrayOf(
            "sd", "emmc", "hwuserdata", "udisk",
            "ext_card", "usbotg", "disk1", "disk2", "disk3", "disk4",
            "usbdrivea", "usbdriveb", "usbdrivec", "usbdrived", "storage",
            "external"
        )
        for (key in keys) {
            if (str.contains(key)) {
                return true
            }
        }
        return false
    }

    //根据mount信息解析sdcard路径
    private fun findSDCardPath(mountInfo: Array<String>): String? {
        var lowerStr: String
        for (col in mountInfo) {
            lowerStr = col.toLowerCase()
            // lenovo 部分手机会把扩展卡bind镜像 /mnt/extrasd_bind
            if (lowerStr.contains("sd") && !lowerStr.contains("extrasd_bind")
                || lowerStr.contains("emmc")
                || lowerStr.contains("ext_card")
                || lowerStr.contains("external_sd")
                || lowerStr.contains("usbstorage")
            ) {
                val pDir: String = SdcardUtil.getParentPath(col)
                // onda平板 扩展卡 /mnt/sdcard/external_sdcard, 三星note扩展卡
                // /mnt/sdcard/external_sd
                // Sony C6603 扩展卡 /storage/removable/sdcard1
                if (pDir == getParentPath(SDCARD_PATH) || pDir == SDCARD_PATH || pDir == SDCARD_PATH + "/" || pDir == "/storage/" || pDir == "/storage/removable/") {
                    return col
                }
            }
            if (col.contains("/storage/") && !col.contains("self") && !col.contains("legacy")) {
                d(TAG, "storage--------------$col")
                return col
            }
            if (col == "/mnt/ext_sdcard") {
                // 华为p6扩展卡
                return col
            }
            if (col == "/udisk") {
                // coolpad 内置卡 /udisk
                return col
            }
            if (col == "/HWUserData") {
                // 部分 huawei 内置卡 /HWUserData
                return col
            }
            if (col == "/storage/external") {
                // coolpad8720l 外置卡
                return col
            }
            if (col == "/Removable/MicroSD") {
                // ASUS_T00G
                return col
            }
        }
        return null
    }

    //取上一级路径
    private fun getParentPath(path: String): String {
        var parentPath: String? = path
        return if (parentPath != null && parentPath.isNotEmpty()) {
            parentPath = parentPath.substring(0, path.length - 1) // 去掉最后一个字符 ， 以兼容以“/”
            // 结尾的路径
            parentPath.substring(0, path.lastIndexOf(File.separator) + 1)
        } else {
            ""
        }
    }

    //根据mount信息解析sdcard分区格式
    private fun findSDCardFormat(mountInfo: Array<String>): SdcardFormat? {
        var formatMinLength = 0
        var formatMaxLength = 0
        for (format in SdcardFormat.values()) {
            val len = format.toString().length
            if (len > formatMaxLength) {
                formatMaxLength = len
            } else if (len < formatMinLength) {
                formatMinLength = len
            }
        }
        for (col in mountInfo) {
            if (col.length < formatMinLength || col.length > formatMaxLength) {
                continue
            }
            for (format in SdcardFormat.values()) {
                if (format.toString() == col) {
                    return format
                }
            }
        }
        return null
    }

    //1.判断如果总容量小于2G,则排除 2.排除内置或外置重复路径
    fun compareData(list: ArrayList<SdcardStateModel>, capacity: Long): Boolean {
        //排除内置或外置重复路径
        if (list.size > 0) {
            for (i in list.indices) {
                if (list[i].totalSize == capacity) {
                    d(TAG, "duplicate-------------------------")
                    return false
                }
            }
        }
        //判断如果总容量小于2G
        if (capacity / 1073741824 < 2) {
            d(TAG, "capacity/ 1073741824-------------------------" + capacity / 1073741824)
            return false
        }
        return true
    }

    //解析Vold(vfat格式)次设备号
    private fun findVoldDevNodeMinorIndex(mountInfo: Array<String>): Int {
        val voldInfo = findVoldDevNodeIndex(mountInfo)
        if (TextUtils.isEmpty(voldInfo)) {
            return -1
        }
        val infos = voldInfo!!.split(":").toTypedArray()
        return if (infos.size < 2) {
            -1
        } else Integer.valueOf(infos[1])
    }

    //解析Vold设备号
    private fun findVoldDevNodeIndex(mountInfo: Array<String>?): String? {
        if (mountInfo == null || mountInfo.isEmpty()) {
            return null
        }
        val voldInfo = mountInfo[0]
        return if (TextUtils.isEmpty(voldInfo)) { null } else voldInfo.replaceFirst("/dev/block/vold/".toRegex(), "")
    }

    //根据设备挂载次序排序SDCard
    private fun sortSDCardList(list: ArrayList<SdcardStateModel>): ArrayList<SdcardStateModel> {
        val resultList = ArrayList<SdcardStateModel>()
        var minIdx = 0
        for (stat in list) {
            if (minIdx == 0) {
                resultList.add(stat)
                minIdx = stat.voldMinorIdx
                continue
            }
            if (stat.voldMinorIdx < minIdx || isInnerSdcard(stat.rootPath, stat.totalSize)) {
                resultList.add(0, stat)
                minIdx = stat.voldMinorIdx
            } else {
                resultList.add(stat)
            }
        }
        return resultList
    }

    private fun isInnerSdcard(path: String?, totalSize: Long): Boolean {
        var innerPath = path
        return try {
            if (!innerPath!!.endsWith("/")) {
                innerPath = "$innerPath/"
            }
            !isPhySize(totalSize) && Environment.getExternalStorageDirectory().absoluteFile.canonicalPath + "/" == innerPath
        } catch (e: IOException) {
            false
        }
    }

    private fun isPhySize(totalSize: Long): Boolean {
        var result = false
        var count = totalSize / SD_PHY_SIZE_1G
        count = if (count % 2 == 0L) {
            count + 0
        } else {
            count + 1
        }
        if (!nCF3(count.toInt()) || 0 >= totalSize) {
            return result
        }
        val real_diff = SD_LOGIC_SIZE_1G * count / totalSize.toDouble()
        // 1.063 <= real_diff <= 1.083
        result = (real_diff >= SD_LOGIC_DIFF - 0.01 && real_diff <= SD_LOGIC_DIFF + 0.01)
        return result
    }

    private fun nCF3(n: Int): Boolean {
        var boo = true
        val s = Integer.toBinaryString(n)
        val b = s.toByteArray()
        for (i in 1 until b.size) {
            if (b[i].toInt() != 48) {
                boo = false
                break
            }
        }
        return boo
    }
    // </editor-fold>

}