package com.dataqin.slice.utils

import com.dataqin.slice.model.SliceInfo
import com.dataqin.slice.model.SliceTmp
import java.io.File
import java.io.RandomAccessFile
import kotlin.math.ceil

object SliceHelper {

    /**
     * 指定分片的目标文件
     * @param targetFile  源文件地址
     * @param index 源文件的顺序标识
     * @param endPointer 偏移状态
     * @param cutSize 分片长度（100 * 1024 * 1024）
     * @return string 源文件路径
     */
    @Synchronized
    fun getTarget(targetFile: File, index: Int, endPointer: Long = 0, cutSize: Long = 100 * 1024 * 1024): SliceInfo {
        val info = SliceInfo()
        try {
            //计算切割文件总数
            val sliceCount = ceil(targetFile.length() / cutSize.toDouble()).toInt()
            info.sliceCount = sliceCount
            val accessFile = RandomAccessFile(targetFile, "r")
            //文件的总长度
            val length = accessFile.length()
            //文件切片后的长度
            val maxSize = length.div(sliceCount)
            //如果光标已经是最后一个，标记为完成
            if (index + 1 >= sliceCount) {
                info.over = true
                return info
            }
            //最后一片单独处理
            if (length - endPointer > 0) {
                val tmp = getTmpFile(targetFile.absolutePath, sliceCount - 1, endPointer, length)
                info.endPointer = tmp.endPointer
                info.tmpPath = tmp.tmpPath
            } else {
                info.index = index + 1
                val end = (index + 1) * maxSize
                val tmp = getTmpFile(targetFile.absolutePath, sliceCount - 1, endPointer, end)
                info.endPointer = tmp.endPointer
                info.tmpPath = tmp.tmpPath
            }
        } catch (e: Exception) {
        }
        return info
    }

    /**
     * 指定文件的边界，产生一个tmp文件
     *
     * @param filePath  源文件地址
     * @param index 源文件的顺序标识
     * @param begin 开始指针的位置
     * @param end   结束指针的位置
     * @return string 源文件路径
     */
    @Synchronized
    private fun getTmpFile(filePath: String, index: Int, begin: Long, end: Long): SliceTmp {
        val tmp = SliceTmp()
        try {
            val file = File(filePath)
            //申明文件切割后的文件磁盘
            val inFile = RandomAccessFile(file, "r")
            //定义一个可读，可写的文件并且后缀名为.tmp的二进制文件
            val tmpFile = File("${file.name.split(".")[0]}_${index}.tmp")
            val outFile = RandomAccessFile(tmpFile, "rw")
            //申明具体每一文件的字节数组
            val b = ByteArray(1024)
            var n: Int
            //从指定位置读取文件字节流
            inFile.seek(begin)
            //判断文件流读取的边界
            while (inFile.read(b).also { n = it } != -1 && inFile.filePointer <= end) {
                //从指定每一份文件的范围，写入不同的文件
                outFile.write(b, 0, n)
            }
            //定义当前读取文件的指针
            tmp.endPointer = inFile.filePointer
            //关闭输入流
            inFile.close()
            //关闭输出流
            outFile.close()
            tmp.tmpPath = tmpFile.absolutePath
        } catch (e: Exception) {
        }
        return tmp
    }

}