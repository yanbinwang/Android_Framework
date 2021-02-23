package com.dataqin.slicing.utils

import android.os.Handler
import android.os.Looper
import com.dataqin.base.utils.LogUtil
import com.dataqin.common.model.SlicingDBModel
import com.dataqin.common.utils.helper.AccountHelper
import com.dataqin.slicing.model.SlicingModel
import com.dataqin.slicing.utils.helper.SlicingHelper
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
    private var frequencyMap = ConcurrentHashMap<String, Int>()

    /**
     * corePoolSize-核心线程数
     * maximumPoolSize-最大线程数
     * keepAliveTime-超时时长
     * unit-时间单位
     * workQueue-缓冲队列
     */
    private var executors = ThreadPoolExecutor(100, 200, 1, TimeUnit.HOURS, LinkedBlockingQueue())
    private var weakHandler = Handler(Looper.getMainLooper())

    /**
     * path--本地数据库主键值，即数据库id（文件路径）
     * fileType-文件类型
     * extrasJson-轮询失败用于取得历史数据，再次发起提交
     */
    fun onStart(sourcePath: String, baoquan_no: String, extrasJson: String) {
        val model = SlicingHelper.query(sourcePath)
        if(null != model){
            try {
                //双保险
                if (!SlicingHelper.isSubmit(model)) {
                    LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n开始上传:$sourcePath\n————————————————————————文件上传————————————————————————")
                    executors.execute {
                        //是否需要切片
                        if (File(model.sourcePath).length() >= 100 * 1024 * 1024) {
                            //通过id查询一下是否有文件切片未提交
                            var slicingList = SlicingHelper.getSlicingList(model)
                            //不存在切片则先执行切片插入数据库再获取
                            if (slicingList.isEmpty() || slicingList.size == 0) {
                                //先插一条数据并刷出来，切片需要一定的时间
                                val dbModel = SlicingDBModel(model.sourcePath, AccountHelper.getUserId(), baoquan_no, "",extrasJson, true, true, false)
                                SlicingHelper.insert(dbModel)
//                                RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                                //再执行分片，切片好后去上传
                                SlicingHelper.insert(dbModel,true)
                                slicingList = SlicingHelper.getSlicingList(sourcePath)
                            } else {
                                //锁对应id的列表,拿取没传完的整体文件数，接口全走完后再解锁
                                SlicingHelper.updateSubmit(model, true)
//                                RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                            }
                            LogUtil.e(TAG, " " + "\n————————————————————————文件上传-分片————————————————————————\n总切片数：" + slicingList.size + "\n未上传完成的数量：" + SlicingHelper.getNotSubmittedCount(slicingList) + "\n————————————————————————文件上传-分片————————————————————————")
                            //根据path声明一个用于统计请求总数的map集合
                            frequencyMap[model.sourcePath] = 0
                            //获取数据库中未传完的分片总数
                            val threadCount = SlicingHelper.getNotSubmittedCount(slicingList)
                            //声明一个集合，记录未传完的分片集合
                            val threadCountList = ArrayList<SlicingModel>()
                            for (i in slicingList.indices) {
                                if (!slicingList[i].isSubmit) {
                                    threadCountList.add(slicingList[i])
                                }
                            }
                            //发起未传完分片的请求，一个完成接口回调后执行下一个
                            weakHandler.post {
//                                toPartUpload(threadCountList, model, threadCount, stateList.size)
                            }
                        } else {
                            SlicingHelper.insert(SlicingDBModel(model.sourcePath, AccountHelper.getUserId(), baoquan_no, "",extrasJson, false, true, false))
//                            RxBus.instance.post(RxEvent(Constants.APP_EVIDENCE_EXTRAS_UPDATE))
                            //走整体接口
                        }
                    }
                    executors.isShutdown
                } else {
                    LogUtil.e(TAG, " \n————————————————————————文件上传————————————————————————\n正在上传:$sourcePath\n————————————————————————文件上传————————————————————————")
                }
            } catch (e: Exception) {
            }
        }
    }

    fun onDestroy() {
        executors.shutdownNow()
        executors = ThreadPoolExecutor(100, 200, 1, TimeUnit.HOURS, LinkedBlockingQueue())
        frequencyMap.clear()
    }


}