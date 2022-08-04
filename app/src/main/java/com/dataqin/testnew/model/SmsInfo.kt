package com.dataqin.testnew.model

class SmsInfo(
    var _id: Int = 0,//主键标识
    var address: String? = null,//发送地址（手机号）
    var type: Int = 0,//类型
    var body: String? = null,//短信内容
    var date: Long = 0L,//时间
)