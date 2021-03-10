package com.dataqin.media.model

import android.os.StatFs
import java.io.File

/**
 * Created by wangyanbin
 * Android4.4增加了SD卡读写权限设置，分为内置存储和外置SD卡，对权限见下表：<br>
 * <table width="60%" border="1" align="center">
 * <tr>
 * <th align="center">Action</th>
 * <th align="center">Primary</th>
 * <th align="center">Secondary</th>
 * </tr>
 * <tbody>
 * <tr>
 * <td>Read Top-Level Directories</td>
 * <td align="center">R</td>
 * <td align="center">R</td>
 * </tr>
 * <tr>
 * <td>Write Top-Level Directories</td>
 * <td align="center">W</td>
 * <td align="center">N</td>
 * </tr>
 * <tr>
 * <td>Read My Package&#8217;s Android Data Directory</td>
 * <td align="center">Y</td>
 * <td align="center">Y</td>
 * </tr>
 * <tr>
 * <td>Write My Package&#8217;s Android Data Directory</td>
 * <td align="center">Y</td>
 * <td align="center">Y</td>
 * </tr>
 * <tr>
 * <td>Read Another Package&#8217;s Android Data Directory</td>
 * <td align="center">R</td>
 * <td align="center">R</td>
 * </tr>
 * <tr>
 * <td>Write Another Package&#8217;s Android Data Directory</td>
 * <td align="center">W</td>
 * <td align="center">N</td>
 * </tr>
 * </tbody>
 * </table>
 * <p style="text-align: center;">
 * <strong>R = With Read Permission, W = With Write Permission, Y =
 * Always, N = Never </strong>
 * </p>
 * 根据上面表格判断SD类型，这个属性代表了Write Top-Level Directories的Secondary(外置SD卡).<br>
 * 由于部分手机厂商没有遵循Google新的SD卡规范，所以在部分Android4.4手机上外置SD卡的根目录仍然有读写
 * 权限.所以只有在Android4.4以上手机，并且外置SD卡不可写的情况此属性才为<strong>false</strong>.
 */
class SdcardStateModel {
    var voldMinorIdx = 0
    var totalSize: Long = 0
    var freeSize: Long = 0
    var canWrite = true
    var isCaseSensitive = false
    var rootPath: String? = null
    var excludePath: String? = null//排除路径，某些手机会将扩展卡挂载在sdcard下面
    var name: String? = null
    var format: SdcardFormat? = null

    constructor(path: String, format: SdcardFormat, voldMinorIdx: Int, excludePath: String = "") {
        val stat = getDiskCapacity(path)
        if (stat != null) {
            this.freeSize = stat.free
            this.totalSize = stat.total
        }
        this.rootPath = path
        this.format = format
        this.isCaseSensitive = checkCaseSensitive(format)
        this.voldMinorIdx = voldMinorIdx
        this.excludePath = excludePath
    }

    private fun checkCaseSensitive(format: SdcardFormat): Boolean {
        return format != SdcardFormat.vfat && format != SdcardFormat.exfat
    }

    private fun setUpExcludePath(excludePath: String) {
        val excludeStat = getDiskCapacity(excludePath)
        if (excludeStat != null) {
            freeSize -= excludeStat.free
            totalSize -= excludeStat.total
        }
        this.excludePath = excludePath
    }

    private fun refreshDiskCapacity() {
        val stat = getDiskCapacity(rootPath!!)
        if (stat != null) {
            freeSize = stat.free
            totalSize = stat.total
        }
    }

    private fun getDiskCapacity(path: String): DiskStateModel? {
        val file = File(path)
        if (!file.exists()) {
            return null
        }
        val stat = StatFs(path)
        val blockSize = stat.blockSize.toLong()
        val totalBlockCount = stat.blockCount.toLong()
        val feeBlockCount = stat.availableBlocks.toLong()
        return DiskStateModel(blockSize * feeBlockCount, blockSize * totalBlockCount)
    }

}