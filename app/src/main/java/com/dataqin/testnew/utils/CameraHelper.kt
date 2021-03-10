package com.dataqin.testnew.utils

import android.provider.MediaStore.Files.FileColumns
import androidx.lifecycle.LifecycleOwner
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Constants.CAMERA_FILE_PATH
import com.dataqin.media.utils.helper.MediaFileHelper
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.FileCallback
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.*
import java.io.File

/**
 *  Created by wangyanbin
 *  相机帮助类
 */
object CameraHelper {
    private var cvFinder: CameraView? = null

    //相机初始化
    fun initialize(owner: LifecycleOwner, cvFinder: CameraView, mode: Mode = Mode.PICTURE) {
        this.cvFinder = cvFinder
        cvFinder.setLifecycleOwner(owner)
        cvFinder.keepScreenOn = true//是否保持屏幕高亮
        cvFinder.playSounds = true//拍照是否播放声音
        cvFinder.engine = Engine.CAMERA2//相机底层类型
        cvFinder.preview = Preview.GL_SURFACE//绘制相机的装载控件
        cvFinder.audio = Audio.ON//是否开启声音
        cvFinder.facing = Facing.BACK//打开时镜头默认后置
        cvFinder.flash = Flash.AUTO//闪光灯自动
        cvFinder.mode = mode//拍照模式还是录像模式
    }

    //镜头翻转
    fun toggleCamera() {
        cvFinder?.toggleFacing()
    }

    fun takePicture() {
        cvFinder?.takePicture()
        cvFinder?.addCameraListener(object : CameraListener() {

            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                val pictureFile = MediaFileHelper.getOutputMediaFile(
                    FileColumns.MEDIA_TYPE_IMAGE,
                    Constants.APPLICATION_NAME + "/" + CAMERA_FILE_PATH
                )
                result.toFile(pictureFile!!, object : FileCallback {
                    override fun onFileReady(file: File?) {

                    }
                })
            }

        })
    }

}