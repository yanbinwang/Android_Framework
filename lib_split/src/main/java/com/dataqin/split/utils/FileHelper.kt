package com.dataqin.split.utils

import android.text.TextUtils
import com.dataqin.common.BaseApplication
import com.dataqin.common.dao.MobileFileDBDao
import com.dataqin.common.model.MobileFileDB
import com.dataqin.common.utils.file.DocumentHelper
import com.dataqin.common.utils.helper.AccountHelper
import java.io.File

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

    //外层先query查找对应数据库，没有找到值的话，重新insert，找到值的话，获取里面的内容
    @JvmStatic
    fun submit(model: MobileFileDB): DocumentHelper.SplitInfo {
        val targetFile = File(model.sourcePath)
        val cutSize = (100 * 1024 * 1024).toLong()
        val targetLength = targetFile.length()
        val count = if (targetLength.mod(cutSize) == 0L) targetLength.div(cutSize).toInt() else targetLength.div(cutSize).plus(1).toInt()
        return DocumentHelper.split(model.sourcePath, targetLength, model.filePointer, model.index, count)
    }

    //接口上传成功调取
    @JvmStatic
    fun update(sourcePath: String, filePointer: Long, index: Int) {
        val model = query(sourcePath)
        if (null != model) {
            model.filePointer = filePointer
            model.index = index
            insert(model)
        }
    }

    //标记文件的状态
    @JvmStatic
    fun updateState(sourcePath: String, complete: Boolean = false) {
        val model = query(sourcePath)
        if (null != model) {
            model.complete = complete
            model.upload = false
            dao.update(model)
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

    @JvmStatic
    fun updateAll(upload: Boolean = false) {
        val daoList = query()
        if (null != daoList) {
            for (model in daoList) {
                update(model.sourcePath, upload)
            }
        }
    }

    //文件是否上传完成
    @JvmStatic
    fun isComplete(sourcePath: String): Boolean {
        var isComplete = true
        val model = query(sourcePath)
        if (null != model) isComplete = model.complete
        return isComplete
    }

    //文件是否正在上传
    @JvmStatic
    fun isUpload(sourcePath: String): Boolean {
        var isUpload = false
        val model = query(sourcePath)
        if (null != model) isUpload = model.upload
        return isUpload
    }
    // </editor-fold>
}