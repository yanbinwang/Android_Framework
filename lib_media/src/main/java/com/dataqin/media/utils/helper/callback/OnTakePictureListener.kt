package com.dataqin.media.utils.helper.callback

import java.io.File

/**
 *  Created by wangyanbin
 *  拍照监听
 */
interface OnTakePictureListener {

    fun onStart()

    fun onSuccess(pictureFile: File)

    fun onFailed()

    fun onComplete()

}