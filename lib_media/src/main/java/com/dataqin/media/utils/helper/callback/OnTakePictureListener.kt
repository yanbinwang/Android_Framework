package com.dataqin.media.utils.helper.callback

import java.io.File

/**
 *  Created by wangyanbin
 *  拍照监听
 */
abstract class OnTakePictureListener {

    open fun onStart() {}

    open fun onSuccess(pictureFile: File) {
        onComplete()
    }

    open fun onFailed() {
        onComplete()
    }

    open fun onComplete() {}

}