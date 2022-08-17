package com.dataqin.split.utils

import android.text.TextUtils
import com.dataqin.common.BaseApplication
import com.dataqin.common.constant.Constants
import com.dataqin.common.dao.MobileFileDBDao
import com.dataqin.common.model.MobileFileDB
import com.dataqin.common.utils.file.DocumentHelper
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.common.utils.helper.AccountHelper
import java.io.File
import java.io.RandomAccessFile

object FileHelper {
    private val dao by lazy { BaseApplication.instance?.daoSession!!.mobileFileDBDao }

    // <editor-fold defaultstate="collapsed" desc="数据库基础增删改查">
    //查询当前用户本机数据库存储的所有集合
    @JvmStatic
    fun query(): MutableList<MobileFileDB>? {
        return try {
            if (!TextUtils.isEmpty(AccountHelper.getUserId())) dao.queryBuilder().where(
                MobileFileDBDao.Properties.UserId.eq(AccountHelper.getUserId())
            ).list() else null
        } catch (e: Exception) {
            null
        }
    }

    //查询对应路径的具体信息
    @JvmStatic
    fun query(sourcePath: String): MobileFileDB? {
        return try {
            if (!TextUtils.isEmpty(AccountHelper.getUserId())) dao.queryBuilder().where(
                MobileFileDBDao.Properties.SourcePath.eq(sourcePath),
                MobileFileDBDao.Properties.UserId.eq(AccountHelper.getUserId())
            ).unique() else null
        } catch (e: Exception) {
            null
        }
    }

    //插入对应数据
    @JvmStatic
    fun insert(model: MobileFileDB?) {
        if (null != model) dao.insertOrReplace(model)
    }

    //删除对应sourcePath数据
    @JvmStatic
    fun delete(sourcePath: String) {
        dao.deleteByKey(sourcePath)
    }

    //删除对应model数据
    @JvmStatic
    fun delete(model: MobileFileDB) {
        dao.delete(model)
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="项目操作方法">
    //获取查表的路径
    @JvmStatic
    fun getSourcePath(type: String, title: String): String {
        return "${Constants.APPLICATION_FILE_PATH}/证据文件/${AccountHelper.getUserId()}/${
            when (type) {
                "1" -> "拍照"
                "2" -> "录像"
                "3" -> "录音"
                else -> "录屏"
            }
        }取证/${title}"
    }

    @JvmStatic
    fun sort(serverList: MutableList<String>) {
        //查询手机内存储的集合
        val localList = query()
        if (null != localList) {
            for (model in localList.filter { !serverList.contains(it.sourcePath) }) {
                delete(model)
                //删除对应的断点续传文件夹
                val file = File(model.sourcePath)
                val fileName = file.name.split(".")[0]
                FileUtil.deleteDir("${file.parent}/${fileName}_record")
                //删除源文件
                FileUtil.deleteFile(model.sourcePath)
            }
        }
    }

    //外层先query查找对应数据库，没有找到值的话，重新insert，找到值的话，获取里面的内容
    @JvmStatic
    fun split(fileDB: MobileFileDB): DocumentHelper.TmpInfo {
        //获取切片源文件
        val targetFile = File(fileDB.sourcePath)
        //设置每片切片大小
        val cutSize = (100 * 1024 * 1024).toLong()
        //获取目标文件,预分配文件所占的空间,在磁盘中创建一个指定大小的文件(r:只读)
        val accessFile = RandomAccessFile(targetFile, "r")
        //文件的总大小
        val length = accessFile.length()
        //计算切片的片数
        val count = if (length.mod(cutSize) == 0L) length.div(cutSize) else length.div(cutSize).plus(1)
        //计算每片切片的实际大小
        val maxSize = length.div(count)
        //确定切割的结尾
        var end = (fileDB.index + 1).times(maxSize)
        //如果当前是分片的最后一片，结尾为文件本身长度
        if(fileDB.index + 1 >= count) end = length
        //返回切割好的信息
        val tmp = DocumentHelper.write(fileDB.sourcePath, fileDB.index, fileDB.filePointer, end)
        return DocumentHelper.TmpInfo(tmp.filePath ?: "", tmp.filePointer)
    }

    //接口回调200成功存储此次断点和下标
    @JvmStatic
    fun update(sourcePath: String, filePointer: Long, index: Int) {
        val model = query(sourcePath)
        if (null != model) {
            model.filePointer = filePointer
            model.index = index
            insert(model)
        }
    }

    //开始上传文件
    @JvmStatic
    fun update(sourcePath: String, upload: Boolean = true) {
        val model = query(sourcePath)
        if (null != model) {
            model.upload = upload
            dao.update(model)
        }
    }

    //更新所有文件的上传状态
    @JvmStatic
    fun updateAll(upload: Boolean = false) {
        val daoList = query()
        if (null != daoList) {
            for (model in daoList) {
                update(model.sourcePath, upload)
            }
        }
    }

    //标记文件此时的状态
    @JvmStatic
    fun complete(sourcePath: String, complete: Boolean = false) {
        val model = query(sourcePath)
        if (null != model) {
            model.complete = complete
            model.upload = false
            dao.update(model)
        }
    }

    //文件是否正在上传
    @JvmStatic
    fun isUpload(sourcePath: String): Boolean {
        var isUpload = false
        val model = query(sourcePath)
        if (null != model) isUpload = model.upload
        return isUpload
    }

    //文件是否上传完成
    @JvmStatic
    fun isComplete(sourcePath: String): Boolean {
        var isComplete = true
        val model = query(sourcePath)
        if (null != model) isComplete = model.complete
        return isComplete
    }
    // </editor-fold>
}