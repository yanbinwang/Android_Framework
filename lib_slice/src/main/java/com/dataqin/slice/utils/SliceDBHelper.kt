package com.dataqin.slice.utils

import com.dataqin.common.BaseApplication
import com.dataqin.common.constant.Constants
import com.dataqin.common.dao.SliceDBDao
import com.dataqin.common.model.SliceDB
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.common.utils.helper.AccountHelper
import java.io.File

/**
 * 只对分片文件做记录，不分片的文件一律不记录
 */
object SliceDBHelper {
    private val dao by lazy { BaseApplication.instance?.daoSession!!.sliceDBDao }

    @JvmStatic
    fun query(): MutableList<SliceDB>? {
        return try {
            dao.queryBuilder()
                .where(SliceDBDao.Properties.UserId.eq(AccountHelper.getUserId()))
                .list()
        } catch (e: Exception) {
            null
        }
    }

    //根据保全号查询相关对象
    @JvmStatic
    fun query(baoquan: String): SliceDB? {
        return try {
            dao.queryBuilder().where(
                SliceDBDao.Properties.Baoquan.eq(baoquan),
                SliceDBDao.Properties.UserId.eq(AccountHelper.getUserId())
            ).unique()
        } catch (e: Exception) {
            null
        }
    }

    //插入数据
    @JvmStatic
    fun insert(model: SliceDB) {
        dao.insert(model)
    }

    //插入数据，在点击上链保全的时候插入
    @JvmStatic
    fun insertOrReplace(model: SliceDB): SliceDB {
        val file = File(model.sourcePath)
        //分片文件->执行分片
        if (file.length() >= 100 * 1024 * 1024) {
            val targetInfo = SliceHelper.getTarget(file, model.index, model.endPointer)
            model.sliceCount = targetInfo.sliceCount
            if (!targetInfo.over) {
                model.tmpPath = targetInfo.tmpPath ?: ""
                model.endPointer = targetInfo.endPointer ?: 0
                model.index = targetInfo.index ?: 0
                model.upload = true
                model.complete = false
            } else {
                model.upload = false
                model.complete = true
            }
        } else {
            model.upload = true
            model.complete = false
        }
        dao.insertOrReplace(model)
        return model
    }

    @JvmStatic
    fun update(model: SliceDB) {
        dao.update(model)
    }

    //删除对应id数据
    @JvmStatic
    fun delete(baoquan: String) {
        dao.deleteByKey(baoquan)
    }

    //删除对应model数据
    @JvmStatic
    fun delete(model: SliceDB) {
        dao.delete(model)
    }

    //整理数据库对应用户的数据
    @JvmStatic
    fun sort(serverList: MutableList<String>) {
        //查询手机内存储的集合
        val localList = query()
        if (null != localList) {
            for (model in localList.filter { !serverList.contains(it.sourcePath) }) {
                delete(model)
                FileUtil.deleteFile(model.sourcePath)
            }
        }
    }

    //是否正在上传
    @JvmStatic
    fun upload(baoquan: String): Boolean {
        var upload = false
        val model = query(baoquan)
        if (null != model) upload = model.upload
        return upload
    }

    //上传状态的变更，用于判断是否还可以进入详情再次上传
    @JvmStatic
    fun uploadState(baoquan: String, upload: Boolean = true) {
        val model = query(baoquan)
        if (null != model) {
            model.upload = upload
            dao.update(model)
        }
    }

    //是否损坏
    @JvmStatic
    fun damage(baoquan: String): Boolean{
        val model = query(baoquan)
        return if (null != model) File(model.sourcePath).exists() else false
    }

    //获取源文件的路径
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

    //获取额外存储值
    @JvmStatic
    fun getExtras(baoquan: String): String {
        var extras = ""
        val model = query(baoquan)
        if (null != model) extras = model.extras
        return extras
    }

}