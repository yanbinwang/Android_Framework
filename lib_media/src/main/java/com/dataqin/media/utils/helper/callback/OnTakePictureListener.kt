package com.dataqin.media.utils.helper.callback

import java.io.File

/**
 *  Created by wangyanbin
 *  拍照监听
 */
abstract class OnTakePictureListener {

    /**
     * 开始拍摄
     */
    open fun onStart() {}

    /**
     * 开始捕获图像回调，即点击拍摄后开始回调
     */
    open fun onShutter() {}

    /**
     * 点击拍摄后且成功拿到结果的回调
     */
    open fun onSuccess(pictureFile: File) {
        onComplete()
    }

    /**
     * 点击拍摄后且失败的回调
     */
    open fun onFailed() {
        onComplete()
    }

    /**
     * 表明这一次拍照行为完成
     */
    open fun onComplete() {}

}