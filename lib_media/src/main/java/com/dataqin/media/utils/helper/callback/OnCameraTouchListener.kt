package com.dataqin.media.utils.helper.callback

/**
 * 相机事件
 */
interface OnCameraTouchListener {

    fun onUp()

    fun onDown(zoom: Float)

}