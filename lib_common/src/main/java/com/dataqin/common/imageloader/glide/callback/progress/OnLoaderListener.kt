package com.dataqin.common.imageloader.glide.callback.progress

/**
 *  Created by wangyanbin
 *  加载进度条的监听
 */
interface OnLoaderListener {

    fun onStart()

    fun onProgress(progress: Int = 0)

    fun onComplete()

}