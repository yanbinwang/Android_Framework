package com.dataqin.media.utils.factory.callback

import java.io.File

/**
 *  Created by wangyanbin
 *  拍照监听
 */
interface OnTakePictureListener {

    fun onSuccess(pictureFile: File)

    fun onFailed()

}