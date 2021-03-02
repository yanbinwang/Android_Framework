package com.dataqin.slicing.model

/**
 *  Created by wangyanbin
 *  SlicingDBModel的extrasJson转换类
 */
class ExtrasModel {
    private var timerCount: Long = 0//总时长
    private var startTime: Long = 0//开始时间
    private var endTime: Long = 0//结束时间
    private var sourcePath: String? = null//文件路径
    private var address: String? = null//定位地址
    private var fileType: String? = null//文件类型 1.照片取证 2.录音取证 3.录像取证 4.录屏取证
    private var label: String? = null//文件标签
    private var latitude: Double = 0.0//纬度
    private var longitude: Double = 0.0//纬度

    constructor(timerCount: Long, startTime: Long, endTime: Long, sourcePath: String, address: String, fileType: String) {
        this.timerCount = timerCount
        this.startTime = startTime
        this.endTime = endTime
        this.sourcePath = sourcePath
        this.address = address
        this.fileType = fileType
    }

}