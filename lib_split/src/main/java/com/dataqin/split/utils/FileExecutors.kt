package com.dataqin.split.utils

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.StringUtil
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.bus.RxSchedulers
import com.dataqin.common.constant.Constants
import com.dataqin.common.http.repository.HttpParams
import com.dataqin.common.http.repository.HttpSubscriber
import com.dataqin.common.model.MobileFileDB
import com.dataqin.common.utils.file.DocumentHelper
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.common.utils.helper.AccountHelper
import com.dataqin.split.subscribe.SplitSubscribe
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *  Created by wangyanbin
 *  文件线程池提交
 */
object FileExecutors {
    private val TAG = "FileExecutors"
    private var frequencyMap = ConcurrentHashMap<String, Int>()//切片集合，key->切片源文件路径 value->当前的切片数量

    /**
     * corePoolSize-核心线程数
     * maximumPoolSize-最大线程数
     * keepAliveTime-超时时长
     * unit-时间单位
     * workQueue-缓冲队列
     */
    private var executors = ThreadPoolExecutor(100, 200, 1, TimeUnit.HOURS, LinkedBlockingQueue())
    private var weakHandler = Handler(Looper.getMainLooper())

    fun destroy() {
        executors.shutdownNow()
        executors = ThreadPoolExecutor(100, 200, 1, TimeUnit.HOURS, LinkedBlockingQueue())
        frequencyMap.clear()
    }

    /**
     * sourcePath--本地数据库主键值，即数据库id（文件路径）
     * fileType-文件类型
     * extrasJson-轮询失败用于取得历史数据，再次发起提交
     */
    fun submit(sourcePath: String, fileType: String, baoquan_no: String, isZip: Boolean = false) {
        if (!FileHelper.isUpload(sourcePath)) {
            LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n开始上传:${sourcePath}\n————————————————————————文件上传————————————————————————")
            when (fileType) {
                "3", "4" -> {
                    if (File(sourcePath).length() >= 100 * 1024 * 1024) {
                        executors.execute {
                            //查询/创建一条用于存表的数据，并重新插入一次
                            val fileDB = query(sourcePath, baoquan_no)
                            FileHelper.insert(fileDB)
                            //先将当前查询/创建的数据在未上传列表内刷出来，文件分片需要一些时间
                            FileHelper.update(sourcePath, true)
                            RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                            //开始分片，并获取分片信息
                            val tmp = FileHelper.split(fileDB)
                            weakHandler.post { toPartUpload(fileDB.index, sourcePath, tmp, fileType, baoquan_no, isZip) }
                        }
                        executors.isShutdown
                    } else toUpload(sourcePath, fileType, baoquan_no, isZip)
                }
                else -> toUpload(sourcePath, fileType, baoquan_no, isZip)
            }
        } else LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n正在上传:${sourcePath}\n————————————————————————文件上传————————————————————————")
    }

    private fun toPartUpload(position: Int, sourcePath: String, tmpInfo: DocumentHelper.TmpInfo, fileType: String, baoquan_no: String, isZip: Boolean = false) {
        executors.execute {
            val paramsFile = File(tmpInfo.filePath ?: "")
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            builder.addFormDataPart("baoquan", baoquan_no)
            builder.addFormDataPart("totalNum", tmpInfo.getTotal(sourcePath).toString())
            builder.addFormDataPart("file", paramsFile.name, paramsFile.asRequestBody((if (isZip) "zip" else "video").toMediaTypeOrNull()))
            SplitSubscribe.getPartUploadApi(builder.build().parts)
                .compose(RxSchedulers.ioMain())
                .subscribeWith(object : HttpSubscriber<Any>() {
                    override fun onSuccess(data: Any?) {
                        super.onSuccess(data)
                        LogUtil.e(TAG, " \n————————————————————————文件上传-分片————————————————————————\n文件路径：${sourcePath}\n分片数量:${tmpInfo.getTotal(sourcePath)}\n当前下标：${position}\n当片路径：${tmpInfo.filePath}\n当片大小：${StringUtil.getFormatSize(File(tmpInfo.filePath?:"").length().toDouble())}\n上传状态：成功\n————————————————————————文件上传-分片————————————————————————")
                        //成功记录此次分片，并删除这个切片
                        FileUtil.deleteFile(tmpInfo.filePath)
                        //此次分片服务器已经收到了，手机本地记录一下
                        FileHelper.update(sourcePath, tmpInfo.filePointer, position)
                        //重新获取当前数据库中存储的值
                        val fileDB = FileHelper.query(sourcePath)
                        if (null != fileDB) {
                            fileDB.index = fileDB.index + 1
                            if (fileDB.index < tmpInfo.getTotal(sourcePath)) {
                                //获取下一块分片,并在手机本地记录
                                val nextTmp = FileHelper.split(fileDB)
                                FileHelper.update(sourcePath, nextTmp.filePointer, fileDB.index)
                                //再开启下一次传输
                                weakHandler.post { toPartUpload(fileDB.index, sourcePath, nextTmp, fileType, baoquan_no, isZip) }
                            } else if (fileDB.index >= tmpInfo.getTotal(sourcePath)) toCombinePart(sourcePath, baoquan_no, fileType)
                        }
                    }

                    override fun onFailed(e: Throwable?, msg: String?) {
                        super.onFailed(e, msg)
                        if (msg == "该保全号信息有误") {
                            FileUtil.deleteFile(tmpInfo.filePath)
                            FileUtil.deleteFile(sourcePath)
                            FileHelper.delete(sourcePath)
                            weakHandler.post { RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_UPDATE, fileType), RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE)) }
                        } else FileHelper.update(sourcePath, false)
                        LogUtil.e(TAG, " \n————————————————————————文件上传-分片————————————————————————\n文件路径：${sourcePath}\n上传状态：失败\n失败原因：${if (TextUtils.isEmpty(msg)) e.toString() else msg}\n————————————————————————文件上传-分片————————————————————————")
                    }

                    override fun onComplete() {
                        super.onComplete()
                        weakHandler.post { RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE)) }
                    }
                })
        }
        executors.isShutdown
    }

    private fun toCombinePart(sourcePath: String, baoquan_no: String, fileType: String) {
        SplitSubscribe.getPartCombineApi(HttpParams().append("baoquan_no", baoquan_no).map)
            .compose(RxSchedulers.ioMain())
            .subscribeWith(object : HttpSubscriber<Any>() {
                override fun onComplete() {
                    super.onComplete()
                    //删除源文件，清空表
                    FileUtil.deleteFile(sourcePath)
                    FileHelper.update(sourcePath, true)
                    FileHelper.delete(sourcePath)
                    weakHandler.post { RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_UPDATE, fileType), RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE)) }
                }
            })
    }

    private fun toUpload(sourcePath: String, fileType: String, baoquan_no: String, isZip: Boolean = false) {
        FileHelper.insert(query(sourcePath, baoquan_no))
        RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
        executors.execute {
            var complete = false
            val mediaType = when (fileType) {
                "1" -> "image"
                "2" -> "audio"
                else -> if (isZip) "zip" else "video"
            }
            val paramsFile = File(sourcePath)
            val builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)
            builder.addFormDataPart("baoquan", baoquan_no)
            builder.addFormDataPart("file", paramsFile.name, paramsFile.asRequestBody(mediaType.toMediaTypeOrNull()))
            SplitSubscribe.getUploadApi(builder.build().parts)
                .compose(RxSchedulers.ioMain())
                .subscribeWith(object : HttpSubscriber<Any>() {
                    override fun onSuccess(data: Any?) {
                        super.onSuccess(data)
                        complete = true
                        FileUtil.deleteFile(sourcePath)
                        FileHelper.update(sourcePath, true)
                        FileHelper.delete(sourcePath)
                        LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n文件路径：$sourcePath\n上传状态：成功\n————————————————————————文件上传————————————————————————")
                    }

                    override fun onFailed(e: Throwable?, msg: String?) {
                        super.onFailed(e, msg)
                        FileHelper.update(sourcePath)
                        LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n文件路径：" + sourcePath + "\n上传状态：失败\n失败原因：${if (TextUtils.isEmpty(msg)) e.toString() else msg}\n————————————————————————文件上传————————————————————————")
                    }

                    override fun onComplete() {
                        super.onComplete()
                        weakHandler.post {
                            if (complete) {
                                RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_UPDATE, fileType))
                                LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n上传完毕\n————————————————————————文件上传————————————————————————")
                            }
                            RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                        }
                    }
                })
        }
        executors.isShutdown
    }

    private fun query(sourcePath: String, baoquan_no: String): MobileFileDB {
        var model = FileHelper.query(sourcePath)
        if (model == null) model = MobileFileDB(sourcePath, AccountHelper.getUserId(), baoquan_no, 0, 0, true, false)
        return model
    }

}