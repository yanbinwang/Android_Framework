package com.dataqin.slice.model

/**
 *  Created by wangyanbin
 *
 */
class EvidenceExtrasModel (
    var appType:String?=null,
    var title: String? = null,//标题
    var baoquan: String? = null,//保全号
    var saveTime: String? = null,//存证时间
    var points: String? = null,//积分
    var isLack:Int = 0,//0不缺 1缺
    var parts: PartModel = PartModel(),//文件对象
    var id :String? = null,
    var extras :String? = null,//额外单位字段
    var isPart :Int = 0//1是 0否
)