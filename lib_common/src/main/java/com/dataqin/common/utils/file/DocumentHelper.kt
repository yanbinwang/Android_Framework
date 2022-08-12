package com.dataqin.common.utils.file

import android.text.TextUtils
import java.io.File
import java.io.FileInputStream
import java.io.RandomAccessFile
import java.math.BigInteger
import java.security.MessageDigest

/**
 * 文件工具类
 */
object DocumentHelper {

    /**
     * 开始创建并写入tmp文件
     * @param filePath  分割文件地址
     * @param fileSize 分割文件大小
     */
    class TmpInfo(var filePath: String? = null, var filePointer: Long = 0)

    class SplitInfo(var filePath: String? = null, var filePointer: Long = 0, var index: Int, var totalNum:Int)

    /**
     * 文件分割
     *
     * @param targetFile 分割的文件
     * @param cutSize    分割文件的大小
     */
    @JvmStatic
    fun split(targetFile: File, cutSize: Long): MutableList<String> {
        val splitList = ArrayList<String>()
        try {
            //计算需要分割的文件总数
            val targetLength = targetFile.length()
            val count = if (targetLength.mod(cutSize) == 0L) targetLength.div(cutSize).toInt() else targetLength.div(cutSize).plus(1).toInt()
            //获取目标文件,预分配文件所占的空间,在磁盘中创建一个指定大小的文件(r:只读)
            val accessFile = RandomAccessFile(targetFile, "r")
            //文件的总大小
            val length = accessFile.length()
            //文件切片后每片的最大大小
            val maxSize = length / count
            //初始化偏移量
            var offSet = 0L
            //开始切片
            for (i in 0 until count - 1) {
                val begin = offSet
                val end = (i + 1) * maxSize
                val tmpInfo = getWrite(targetFile.absolutePath, i, begin, end)
                offSet = tmpInfo.filePointer
                splitList.add(tmpInfo.filePath ?: "")
            }
            if (length - offSet > 0) splitList.add(getWrite(targetFile.absolutePath, count - 1, offSet, length).filePath ?: "")
            accessFile.close()
        } catch (e: Exception) {
        } finally {
            //确保返回的集合中不包含空路径
            for (i in splitList.indices.reversed()) {
                if (TextUtils.isEmpty(splitList[i])) {
                    splitList.removeAt(i)
                }
            }
        }
        return splitList
    }

    /**
     * 传入记录的分片文件信息，如果丢失，则相当于重新分片
     * 切片时记录每个切片的offSet（即filePointer）
     * 取出数据库中的filePointer以及index
     */
    @JvmStatic
    fun split(filePath: String, length: Long, filePointer: Long, index: Int, count: Int): SplitInfo {
        //开始切片
        val end = (index + 1) * (length / count)
        val model = getWrite(filePath, index, filePointer, end)
        return SplitInfo(model.filePath, model.filePointer, index, count)
    }

    /**
     * 开始创建并写入tmp文件
     * @param filePath  源文件地址
     * @param index 源文件的顺序标识
     * @param begin 开始指针的位置
     * @param end   结束指针的位置
     */
    @JvmStatic
    private fun getWrite(filePath: String, index: Int, begin: Long, end: Long): TmpInfo {
        val info = TmpInfo()
        try {
            //源文件
            val file = File(filePath)
            //申明文件切割后的文件磁盘
            val inAccessFile = RandomAccessFile(file, "r")
            //定义一个可读，可写的文件并且后缀名为.tmp的二进制文件
            val tmpFile = File("${file.parent}/${file.name.split(".")[0]}_${index}.tmp")
            //如果不存在，则创建一个或继续写入
            val outAccessFile = RandomAccessFile(tmpFile, "rw")
            //申明具体每一文件的字节数组
            val b = ByteArray(1024)
            var n: Int
            //从指定位置读取文件字节流
            inAccessFile.seek(begin)
            //判断文件流读取的边界，从指定每一份文件的范围，写入不同的文件
            while (inAccessFile.read(b).also { n = it } != -1 && inAccessFile.filePointer <= end) {
                outAccessFile.write(b, 0, n)
            }
            //关闭输入输出流,赋值
            info.filePointer = inAccessFile.filePointer
            inAccessFile.close()
            outAccessFile.close()
            info.filePath = tmpFile.absolutePath
        } catch (e: Exception) {
        }
        return info
    }

    /**
     * 获取文件哈希值
     * 满足64位哈希，不足则前位补0
     */
    @JvmStatic
    fun getSignature(file: File): String {
        var hash = ""
        try {
            val inputStream = FileInputStream(file)
            val digest = MessageDigest.getInstance("SHA-256")
            val array = ByteArray(1024)
            var len: Int
            while (inputStream.read(array, 0, 1024).also { len = it } != -1) {
                digest.update(array, 0, len)
            }
            inputStream.close()
            val bigInt = BigInteger(1, digest.digest())
            hash = bigInt.toString(16)
            if (hash.length < 64) {
                for (i in 0 until 64 - hash.length) {
                    hash = "0$hash"
                }
            }
        } catch (e: Exception) {
        }
        return hash
    }

}