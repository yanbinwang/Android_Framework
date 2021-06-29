package com.dataqin.push.model

import java.io.Serializable

/**
 *  Created by wangyanbin
 *  推送类
 */
class PushModel :Serializable {
    var sendType: String? = null//消息发送类型
    var title: String? = null//通知栏显示标题
    var content: String? = null//通知栏显示内容
    var icon: String? = null//通知栏图标
    var env: String? = null//推送环境，prod-线上环境
}