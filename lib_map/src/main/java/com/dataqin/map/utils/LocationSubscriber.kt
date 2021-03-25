package com.dataqin.map.utils

import com.amap.api.location.AMapLocation

/**
 *  Created by wangyanbin
 *  定位的状态订阅
 *  定位后只需判断此次定位的状态和是否需要移动地图即可
 */
abstract class LocationSubscriber {
    var granted = false//是否需要授权，授权的为为精确定位，用于打卡和地图矫正
    var move = false//是否需要在完成定位后移动到坐标点

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