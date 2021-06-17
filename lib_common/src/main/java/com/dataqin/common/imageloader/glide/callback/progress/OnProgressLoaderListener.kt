package com.dataqin.common.imageloader.glide.callback.progress

/**
 *  Created by wangyanbin
 *
 */
interface OnProgressLoaderListener {

    fun onProgress(progress: Int = 0)

    fun onComplete()

}