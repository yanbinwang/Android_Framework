package com.dataqin.map.utils

import com.amap.api.location.AMapLocation

/**
 *  Created by wangyanbin
 *  定位的状态订阅
 */
abstract class LocationSubscriber {

    open fun onSuccess(model: AMapLocation) {
    }

    open fun onFailed() {
    }

}