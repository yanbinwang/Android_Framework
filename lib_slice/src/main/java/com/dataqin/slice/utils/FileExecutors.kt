package com.dataqin.slice.utils

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.dataqin.base.utils.LogUtil
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.bus.RxSchedulers
import com.dataqin.common.constant.Constants
import com.dataqin.common.http.repository.HttpSubscriber
import com.dataqin.common.model.SliceDB
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.common.utils.helper.AccountHelper
import com.dataqin.slice.subscribe.SliceSubscribe
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *  Created by wangyanbin
 *  文件线程池提交
 */
object FileExecutors {
    private val TAG = "FileExecutors"

    /**
     * corePoolSize-核心线程数
     * maximumPoolSize-最大线程数
     * keepAliveTime-超时时长
     * unit-时间单位
     * workQueue-缓冲队列
     */
    private var executors = ThreadPoolExecutor(100, 200, 1, TimeUnit.HOURS, LinkedBlockingQueue())
    private var weakHandler = Handler(Looper.getMainLooper())

    fun onDestroy() {
        executors.shutdownNow()
        executors = ThreadPoolExecutor(100, 200, 1, TimeUnit.HOURS, LinkedBlockingQueue())
    }

    fun submit(southPath: String, fileType: String, baoquan_no: String, extras: String) {
        try {
            if (!SliceDBHelper.upload(baoquan_no)) {
                LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n开始上传:$southPath\n————————————————————————文件上传————————————————————————")
                when (fileType) {
                    "3", "4" -> {
                        if (File(southPath).length() >= 100 * 1024 * 1024) {
                            executors.execute {
                                //先查询一次数据表中是否存在该数据
                                var model = SliceDBHelper.query(baoquan_no)
                                //不具备则构建一条数据
                                if (null == model) {
                                    model = SliceDB(baoquan_no, southPath, AccountHelper.getUserId(), extras, false, true, "", 0, 0, 0)
                                    SliceDBHelper.insert(model)
                                }
                                //更新列表
                                SliceDBHelper.uploadState(baoquan_no)
                                RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                                //执行插入或替换，生成分片文件
                                weakHandler.post { toPartUpload(SliceDBHelper.insertOrReplace(model), fileType, baoquan_no) }
                            }
                            executors.isShutdown
                        } else toUpload(southPath, fileType, baoquan_no, extras)
                    }
                    else -> toUpload(southPath, fileType, baoquan_no, extras)
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun toPartUpload(model: SliceDB, fileType: String, baoquan_no: String) {
        try {
            executors.execute {
                if (!model.complete && !TextUtils.isEmpty(model.tmpPath)) {
                    RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                    val paramsFile = File(model.tmpPath)
                    val builder = MultipartBody.Builder()
                    builder.setType(MultipartBody.FORM)
                    builder.addFormDataPart("baoquan", baoquan_no)
                    builder.addFormDataPart("totalNum", model.sliceCount.toString())
                    builder.addFormDataPart("file", paramsFile.name, paramsFile.asRequestBody("video".toMediaTypeOrNull()))
                    SliceSubscribe.getPartUploadApi(builder.build().parts)
                        .compose(RxSchedulers.ioMain())
                        .subscribeWith(object : HttpSubscriber<Any>() {
                            override fun onSuccess(data: Any?) {
                                super.onSuccess(data)
                                //成功删除这个切片
                                FileUtil.deleteFile(model.tmpPath)
                                RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                                weakHandler.post { toPartUpload(SliceDBHelper.insertOrReplace(model), fileType, baoquan_no) }
                                LogUtil.e(TAG, " \n————————————————————————文件上传-分片————————————————————————\n文件路径：${model.sourcePath}\n分片路径：${model.tmpPath}\n上传状态：成功\n————————————————————————文件上传-分片————————————————————————")
                            }

                            override fun onFailed(e: Throwable?, msg: String?) {
                                super.onFailed(e, msg)
                                FileUtil.deleteFile(model.tmpPath)
                                if(msg != "该保全号信息有误") {
                                    RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                                    weakHandler.post { toPartUpload(SliceDBHelper.insertOrReplace(model), fileType, baoquan_no) }
                                } else {
                                    var index = model.index -1
                                    if(index < 0) index = 0
                                    model.index = index
                                    SliceDBHelper.update(model)
                                    SliceDBHelper.uploadState(baoquan_no, false)
                                    RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                                }
                                LogUtil.e(TAG, " " + "\n————————————————————————文件上传-分片————————————————————————\n文件路径：${model.sourcePath}\n分片路径：${model.tmpPath}\n上传状态：失败\n失败原因：" + if (TextUtils.isEmpty(msg)) e.toString() else "$msg\n————————————————————————文件上传-分片————————————————————————")
                            }
                        })
                } else {
                    FileUtil.deleteFile(model.sourcePath)
                    SliceDBHelper.delete(baoquan_no)
                    RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE), RxEvent(Constants.APP_EVIDENCE_UPDATE, fileType))
                }
            }
            executors.isShutdown
        } catch (e: Exception) {
        }
    }

    private fun toUpload(southPath: String, fileType: String, baoquan_no: String, extras: String) {
        try {
            executors.execute {
                SliceDBHelper.insertOrReplace(SliceDB(baoquan_no, southPath, AccountHelper.getUserId(), extras, false, true, "", 0, 0, 0))
                RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                var complete = false
                val mediaType = when (fileType) {
                    "1" -> "image"
                    "2" -> "audio"
                    else -> "video"
                }
                val paramsFile = File(southPath)
                val builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)
                builder.addFormDataPart("baoquan", baoquan_no)
                builder.addFormDataPart("file", paramsFile.name, paramsFile.asRequestBody(mediaType.toMediaTypeOrNull()))
                SliceSubscribe.getUploadApi(builder.build().parts)
                    .compose(RxSchedulers.ioMain())
                    .subscribeWith(object : HttpSubscriber<Any>() {
                        override fun onSuccess(data: Any?) {
                            super.onSuccess(data)
                            complete = true
                            FileUtil.deleteFile(southPath)
                            SliceDBHelper.delete(baoquan_no)
                            LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n文件路径：$southPath\n上传状态：成功\n————————————————————————文件上传————————————————————————")
                        }

                        override fun onFailed(e: Throwable?, msg: String?) {
                            super.onFailed(e, msg)
                            SliceDBHelper.uploadState(baoquan_no, false)
                            LogUtil.e(TAG, " " + "\n————————————————————————文件上传————————————————————————\n文件路径：" + southPath + "\n上传状态：失败\n失败原因：" + if (TextUtils.isEmpty(msg)) e.toString() else "$msg\n————————————————————————文件上传————————————————————————")
                        }

                        override fun onComplete() {
                            super.onComplete()
                            if (complete) {
                                RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_UPDATE, fileType))
                                LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n上传完毕\n————————————————————————文件上传————————————————————————")
                            }
                            RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                        }
                    })
            }
            executors.isShutdown
        } catch (e: Exception) {
        }
    }

}