package com.dataqin.media.utils.factory

/**
 *  Created by wangyanbin
 *  音频工具类
 */
class MediaFactory private constructor() {

    companion object {
        @JvmStatic
        val instance: MediaFactory by lazy {
            MediaFactory()
        }
    }



}