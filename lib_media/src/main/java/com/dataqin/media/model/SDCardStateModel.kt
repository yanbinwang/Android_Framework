package com.dataqin.media.model

import android.os.StatFs
import java.io.File

/**
 *  Created by wangyanbin
 *
 */
class SDCardStateModel {
    var voldMinorIdx = 0
    var totalSize: Long = 0
    var freeSize: Long = 0
    var canWrite = true
    var isCaseSensitive = false
    var rootPath: String? = null
    var excludePath: String? = null// 排除路径， 某些手机会将扩展卡挂载在sdcard下面
    var name: String? = null
    var format: Format? = null
    var SDCARD_MAX_COUNT = 1024 // voldMinorIdx 小于此值的为sdcard内置设备，大于此值的是u盘

    companion object {
        //计算目标路径的磁盘使用情况
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

    enum class Format {
        vfat, exfat, ext4, fuse, sdcardfs, texfat
    }

    constructor(path: String, format: Format, voldMinorIdx: Int, excludePath: String = "") {
        val stat = getDiskCapacity(path)
        if (stat != null) {
            freeSize = stat.free
            totalSize = stat.total
        }
        rootPath = path
        this.format = format
        isCaseSensitive = checkCaseSensitive(format)
        this.voldMinorIdx = voldMinorIdx
        this.excludePath = excludePath
    }

    private fun checkCaseSensitive(format: Format): Boolean {
        return format != Format.vfat && format != Format.exfat
    }

}