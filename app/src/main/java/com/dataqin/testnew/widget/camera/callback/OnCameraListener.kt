package com.dataqin.testnew.widget.camera.callback

import java.io.File

/**
 *  Created by wangyanbin
 *
 */
interface OnCameraListener {

    fun onTakePictureSuccess(pictureFile: File?)

    fun onTakePictureFail(data: ByteArray?)

}