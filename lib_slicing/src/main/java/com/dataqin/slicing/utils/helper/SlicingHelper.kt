package com.dataqin.slicing.utils.helper

import android.os.Environment
import android.text.TextUtils
import com.dataqin.common.BaseApplication
import com.dataqin.common.constant.Constants
import com.dataqin.common.dao.SlicingDBModelDao
import com.dataqin.common.model.SlicingDBModel
import com.dataqin.common.utils.analysis.GsonUtil
import com.dataqin.common.utils.helper.AccountHelper
import com.dataqin.slicing.model.SlicingModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File


/**
 *  Created by wangyanbin
 *  分片的数据库管理类
 */
object SlicingHelper {
    private val dao by lazy { BaseApplication.instance?.daoSession!!.slicingDBModelDao }

    // <editor-fold defaultstate="collapsed" desc="数据库基础增删改查">
    //查询对应路径的具体信息
    @JvmStatic
    fun query(sourcePath: String): SlicingDBModel? {
        return try {
            if (!TextUtils.isEmpty(AccountHelper.getUserId())) {
                dao.queryBuilder().where(
                    SlicingDBModelDao.Properties.SourcePath.eq(sourcePath),
                    SlicingDBModelDao.Properties.UserId.eq(AccountHelper.getUserId())
                ).unique()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    //插入对应数据
    @JvmStatic
    fun insert(model: SlicingDBModel?, isSlicing: Boolean = false): MutableList<SlicingModel> {
        var list = ArrayList<SlicingModel>()
        if (null != model) {
            if (isSlicing) {
                //文件分割，插入前分割
                val tmpList = DocumentHelper.getSplitFile(File(model.sourcePath), 100 * 1024 * 1024)
                //集合存储状态,转换为json
                val slicingList = ArrayList<SlicingModel>()
                for (i in tmpList.indices) {
                    val state = SlicingModel(tmpList[i], false)
                    slicingList.add(state)
                }
                model.slicingJson = GsonUtil.objToJson(slicingList)
                list = slicingList
            }
            dao.insertOrReplace(model)
        }
        return list
    }

    //全部切片完成上传，通常此时这条数据已经被删除不存在了
    @JvmStatic
    fun update(model: SlicingDBModel, isComplete: Boolean = false) {
        model.isComplete = isComplete
        dao.update(model)
    }

    //上传状态的变更，用于判断是否还可以进入详情再次上传
    @JvmStatic
    fun updateSubmit(model: SlicingDBModel, isSubmit: Boolean = true) {
        model.isSubmit = isSubmit
        dao.update(model)
    }

    //更新数据库中所有数据的上传状态
    @JvmStatic
    fun updateAll(isUpload: Boolean = false) {
        val daoList = dao.loadAll()
        for (model in daoList) {
            updateSubmit(model, isUpload)
        }
    }

    //删除对应id都数据
    @JvmStatic
    fun delete(id: String) {
        dao.deleteByKey(id)
    }

    @JvmStatic
    fun delete() {
        dao.deleteAll()
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="项目操作方法">
    //获取查表的路径
    @JvmStatic
    fun getSourcePath(type: String, title: String): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/" + Constants.APPLICATION_NAME + "/" + type + "/" + title
    }

    //根据类型获取查表的id
    @JvmStatic
    fun getDBSourcePath(appType: String, title: String): String {
        return when (appType) {
            "1" -> getSourcePath(Constants.CAMERA_FILE_PATH, title)
            "2" -> getSourcePath(Constants.AUDIO_FILE_PATH, title)
            "3" -> getSourcePath(Constants.VIDEO_FILE_PATH, title)
            else -> getSourcePath(Constants.SCREEN_FILE_PATH, title)
        }
    }

    //获取切片集合
    @JvmStatic
    fun getSlicingList(sourcePath: String): MutableList<SlicingModel> {
        val model = query(sourcePath)
        if(null != model){
            return getSlicingList(model)
        }
        return ArrayList()
    }

    //获取切片集合
    @JvmStatic
    fun getSlicingList(model: SlicingDBModel?): MutableList<SlicingModel> {
        var slicingList = ArrayList<SlicingModel>()
        if(null != model){
            slicingList = try {
                Gson().fromJson(model.slicingJson, object : TypeToken<List<SlicingModel>>() {}.type)
            } catch (e: Exception) {
                ArrayList()
            }
        }
        return slicingList
    }

    //获取未上传的切片个数
    @JvmStatic
    fun getNotSubmittedCount(slicingList: MutableList<SlicingModel>): Int {
        var threadCount = 0
        if (slicingList.isNotEmpty()) {
            for (i in slicingList.indices) {
                if (!slicingList[i].isSubmit) {
                    threadCount++
                }
            }
        }
        return threadCount
    }

    //获取对应参数
    @JvmStatic
    fun getExtrasJson(model: SlicingDBModel?): String {
        var extrasJson = ""
        if (null != model) {
            extrasJson = model.extrasJson
        }
        return extrasJson
    }

    //单个切片状态变更
    @JvmStatic
    fun tmpState(model: SlicingDBModel?, sourcePath: String, isSubmit: Boolean = false) {
        if (null != model) {
            val stateList: MutableList<SlicingModel> = Gson().fromJson(
                model.slicingJson,
                object : TypeToken<List<SlicingModel>>() {}.type
            )
            for (i in stateList.indices) {
                if (stateList[i].sourcePath == sourcePath) {
                    stateList[i].isSubmit = isSubmit
                    break
                }
            }
            model.slicingJson = GsonUtil.objToJson(stateList)
            dao.update(model)
            if (isTmpComplete(model)) {
                update(model, true)
            }
        }
    }

    //未完成上传的切片是否有损坏
    @JvmStatic
    fun isDamage(model: SlicingDBModel?): Boolean{
        var isDamage = false
        if (null != model) {
            //切片的数据如果源文件和切片一块有丢失就直接是损坏
            if (model.isSlicing) {
                if (!File(model.sourcePath).exists()) {
                    isDamage = true
                } else {
                    val slicingList = getSlicingList(model)
                    if (slicingList.isNotEmpty()) {
                        for (i in slicingList.indices) {
                            val stateModel = slicingList[i]
                            if (!File(stateModel.sourcePath).exists() && !stateModel.isSubmit) {
                                isDamage = true
                                break
                            }
                        }
                    }
                }
            } else {
                //文件丢失，标记为损坏
                if (!File(model.sourcePath).exists()) {
                    isDamage = true
                }
            }
        } else {
            isDamage = true//对应数据都查询不到的数据就是损坏
        }
        return isDamage
    }

    //是否完成整体文件的上传
    @JvmStatic
    fun isTmpComplete(model: SlicingDBModel?): Boolean {
        var isComplete = true
        val slicingList = getSlicingList(model)
        if (slicingList.isNotEmpty()) {
            for (i in slicingList.indices) {
                if (!slicingList[i].isSubmit) {
                    isComplete = false
                    break
                }
            }
        } else {
            isComplete = false
        }
        return isComplete
    }

    //是否完成整体文件的上传
    @JvmStatic
    fun isComplete(model: SlicingDBModel?): Boolean {
        var isComplete = true
        if (null != model) {
            isComplete = model.isComplete
        }
        return isComplete
    }

    @JvmStatic
    fun isSubmit(model: SlicingDBModel?): Boolean {
        var isUpload = false
        if (null != model) {
            isUpload = model.isSubmit
        }
        return isUpload
    }

    @JvmStatic
    fun isSlicing(model: SlicingDBModel?): Boolean {
        var isSlicing = false
        if (null != model) {
            isSlicing = model.isSlicing
        }
        return isSlicing
    }
    // </editor-fold>

}