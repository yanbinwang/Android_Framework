package com.dataqin.media.utils.factory

import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import androidx.lifecycle.LifecycleOwner
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Constants.CAMERA_FILE_PATH
import com.dataqin.common.constant.Constants.VIDEO_FILE_PATH
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.factory.callback.OnTakePictureListener
import com.dataqin.media.utils.factory.callback.OnVideoRecordListener
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.*

/**
 *  Created by wangyanbin
 *  相机帮助类
 *  https://github.com/natario1/CameraView
 */
class CameraFactory private constructor() {
    private var cvFinder: CameraView? = null
    var onTakePictureListener: OnTakePictureListener? = null
    var onVideoRecordListener: OnVideoRecordListener? = null

    companion object {
        @JvmStatic
        val instance: CameraFactory by lazy {
            CameraFactory()
        }
    }

    /**
     *  相机初始化
     *  如果相机需要缩放
     *  app:cameraGesturePinch="zoom"
     *  如果需要制定模式
     *  app:cameraMode="picture|video"
     */
    fun initialize(owner: LifecycleOwner, cvFinder: CameraView) {
        this.cvFinder = cvFinder
        cvFinder.setLifecycleOwner(owner)
        cvFinder.setExperimental(true)//拍照快门声
        cvFinder.keepScreenOn = true//是否保持屏幕高亮
        cvFinder.playSounds = true//录像是否录制声音
        cvFinder.audio = Audio.ON//录制开启声音
        cvFinder.engine = Engine.CAMERA2//相机底层类型
        cvFinder.preview = Preview.GL_SURFACE//绘制相机的装载控件
        cvFinder.facing = Facing.BACK//打开时镜头默认后置
        cvFinder.flash = Flash.AUTO//闪光灯自动
    }

    /**
     * 镜头翻转
     */
    fun toggleCamera() {
        cvFinder?.toggleFacing()
    }

    /**
     * 拍照/抓拍
     */
    fun takePicture(isSnapshot: Boolean = false) {
        onTakePictureListener?.onStart()
        if (isSnapshot) {
            cvFinder?.takePictureSnapshot()
        } else {
            cvFinder?.takePicture()
        }
        cvFinder?.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                //在sd卡的Picture文件夹下创建对应的文件
                val pictureFile = MediaFileUtil.getOutputMediaFile(MEDIA_TYPE_IMAGE, Constants.APPLICATION_NAME + "/" + CAMERA_FILE_PATH)
                if (null != pictureFile) {
                    result.toFile(pictureFile) { file ->
                        if (null != file) {
                            onTakePictureListener?.onSuccess(file)
                        } else {
                            onTakePictureListener?.onFailed()
                        }
                    }
                } else {
                    onTakePictureListener?.onFailed()
                }
                onTakePictureListener?.onComplete()
            }
        })
    }

    /**
     * 开始录像
     */
    fun startRecorder() {
        val videoFile = MediaFileUtil.getOutputMediaFile(MEDIA_TYPE_VIDEO, Constants.APPLICATION_NAME + "/" + VIDEO_FILE_PATH)
        if (null != videoFile) {
            cvFinder?.takeVideo(videoFile)
            cvFinder?.addCameraListener(object : CameraListener() {
                override fun onVideoTaken(result: VideoResult) {
                    super.onVideoTaken(result)
                    onVideoRecordListener?.onRecorderTaken(result.file.path)
                }

                override fun onVideoRecordingStart() {
                    super.onVideoRecordingStart()
                    onVideoRecordListener?.onStartRecorder()
                }

                //onVideoRecordingEnd比onVideoTaken先调取
                override fun onVideoRecordingEnd() {
                    super.onVideoRecordingEnd()
                    onVideoRecordListener?.onStopRecorder()
                }
            })
        } else {
            onVideoRecordListener?.onStopRecorder()
        }
    }

    /**
     * 停止录像
     */
    fun stopRecorder() {
        cvFinder?.stopVideo()
    }

}