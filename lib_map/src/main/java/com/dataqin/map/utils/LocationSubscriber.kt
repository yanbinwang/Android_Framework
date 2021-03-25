package com.dataqin.map.utils

import com.amap.api.location.AMapLocation

/**
 *  Created by wangyanbin
 *  定位的状态订阅
 */
abstract class LocationSubscriber {
    var normal = true//普通定位还是签到打卡
    var move = false//是否需要移动

    open fun onStart() {}

    open fun onSuccess(model: AMapLocation) {
        onComplete()
    }

    /**
     * true表示当前只是做定位，不需要处理结果，false需要
     */
    open fun onFailed() {
        onComplete()
    }

    open fun onComplete() {}

}