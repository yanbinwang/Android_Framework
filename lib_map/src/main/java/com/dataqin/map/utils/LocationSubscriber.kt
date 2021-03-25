package com.dataqin.map.utils

import com.amap.api.location.AMapLocation

/**
 *  Created by wangyanbin
 *  定位的状态订阅
 */
abstract class LocationSubscriber {
    var normal = true

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