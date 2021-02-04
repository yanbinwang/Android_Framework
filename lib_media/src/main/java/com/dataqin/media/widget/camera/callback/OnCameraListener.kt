package com.dataqin.media.widget.camera.callback

import java.io.File

/**
 *  Created by wangyanbin
 *  拍照监听
 */
interface OnCameraListener {

    fun onTakePictureSuccess(pictureFile: File?)

    fun onTakePictureFail(data: ByteArray?)

}