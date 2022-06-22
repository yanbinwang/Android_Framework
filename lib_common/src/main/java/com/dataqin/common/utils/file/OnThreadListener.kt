package com.dataqin.common.utils.file

interface OnThreadListener {

    /**
     * 线程开始执行
     */
    fun onStart()

    /**
     * 线程停止执行
     */
    fun onStop(path: String? = null)

}