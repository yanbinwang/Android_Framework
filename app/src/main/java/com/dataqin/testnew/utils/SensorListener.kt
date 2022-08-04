package com.dataqin.testnew.utils

import android.content.Context
import android.view.OrientationEventListener

class SensorListener(var context: Context) : OrientationEventListener(context) {
    private val SENSOR_ANGLE = 10
    var rotate = 0

    override fun onOrientationChanged(orientation: Int) {
        if (orientation == ORIENTATION_UNKNOWN) return
        //下面是手机旋转准确角度与四个方向角度（0 90 180 270）的转换
        rotate = if (orientation > 360 - SENSOR_ANGLE || orientation < SENSOR_ANGLE) {
            0
        } else if (orientation > 90 - SENSOR_ANGLE && orientation < 90 + SENSOR_ANGLE) {
            90
        } else if (orientation > 180 - SENSOR_ANGLE && orientation < 180 + SENSOR_ANGLE) {
            180
        } else if (orientation > 270 - SENSOR_ANGLE && orientation < 270 + SENSOR_ANGLE) {
            270
        } else {
            return
        }
    }

}