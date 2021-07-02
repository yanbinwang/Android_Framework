package com.dataqin.media.utils.helper

import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import androidx.lifecycle.LifecycleOwner
import com.dataqin.base.utils.ToastUtil
import com.dataqin.common.widget.dialog.MessageDialog
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.helper.callback.OnTakePictureListener
import com.dataqin.media.utils.helper.callback.OnVideoRecordListener
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
object CameraHelper {
    private var cvFinder: CameraView? = null
    private var messageDialog: MessageDialog? = null
    var onTakePictureListener: OnTakePictureListener? = null
    var onVideoRecordListener: OnVideoRecordListener? = null

    /**
     *  相机初始化
     *  如果相机需要缩放
     *  app:cameraGesturePinch="zoom"
     *  如果需要制定模式
     *  app:cameraMode="picture|video"
     */
    @JvmStatic
    fun initialize(owner: LifecycleOwner, cvFinder: CameraView) {
        this.cvFinder = cvFinder
        this.messageDialog = MessageDialog(cvFinder.context).setParams("正在生成视频,请勿频繁操作...")
        cvFinder.apply {
            setLifecycleOwner(owner)
            setExperimental(true)//拍照快门声
            keepScreenOn = true//是否保持屏幕高亮
            playSounds = true//录像是否录制声音
            audio = Audio.ON//录制开启声音
//            engine = Engine.CAMERA2//相机底层类型
            engine = Engine.CAMERA1//相机底层类型
            preview = Preview.GL_SURFACE//绘制相机的装载控件
            facing = Facing.BACK//打开时镜头默认后置
            flash = Flash.AUTO//闪光灯自动
        }
    }

    /**
     * 复位
     */
    @JvmStatic
    fun reset() {
        cvFinder?.zoom = 0f
    }

    /**
     * 镜头翻转
     */
    @JvmStatic
    fun toggleCamera() {
        cvFinder?.toggleFacing()
    }

    /**
     * 拍照/抓拍
     */
    @JvmStatic
    fun takePicture(snapshot: Boolean = false) {
        if (cvFinder?.isTakingPicture == true) {
            ToastUtil.mackToastSHORT("正在生成图片,请勿频繁操作...", cvFinder?.context!!)
            return
        }
        if (snapshot) {
            cvFinder?.takePictureSnapshot()
        } else {
            cvFinder?.takePicture()
        }
        cvFinder?.addCameraListener(object : CameraListener() {
            override fun onPictureShutter() {
                super.onPictureShutter()
                onTakePictureListener?.onStart()
            }

            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                //在sd卡的Picture文件夹下创建对应的文件
                val pictureFile = MediaFileUtil.getOutputFile(MEDIA_TYPE_IMAGE)
                if (null != pictureFile) {
                    result.toFile(pictureFile) {
                        if (null != it) {
                            onTakePictureListener?.onSuccess(it)
                        } else {
                            onTakePictureListener?.onFailed()
                        }
                    }
                } else onTakePictureListener?.onFailed()
            }
        })
    }

    /**
     * 开始录像
     */
    @JvmStatic
    fun startRecorder() {
        if (cvFinder?.isTakingVideo == true) {
            ToastUtil.mackToastSHORT("正在生成视频,请勿频繁操作...", cvFinder?.context!!)
            return
        }
        val videoFile = MediaFileUtil.getOutputFile(MEDIA_TYPE_VIDEO)
        if (null != videoFile) {
            cvFinder?.takeVideo(videoFile)
            cvFinder?.addCameraListener(object : CameraListener() {
                //正式完成录制的回调，获取路径
                override fun onVideoTaken(result: VideoResult) {
                    super.onVideoTaken(result)
                    onVideoRecordListener?.onStopRecorder(result.file.path)
                    messageDialog?.hide()
                }

                override fun onVideoRecordingStart() {
                    super.onVideoRecordingStart()
                    onVideoRecordListener?.onStartRecorder()
                }

//                //onVideoRecordingEnd比onVideoTaken先调取,文件此时可能还在存储
//                override fun onVideoRecordingEnd() {
//                    super.onVideoRecordingEnd()
//                    onVideoRecordListener?.onStopRecorder()
//                }
            })
        } else {
            onVideoRecordListener?.onStopRecorder(null)
            messageDialog?.hide()
        }
    }

    /**
     * 停止录像
     */
    @JvmStatic
    fun stopRecorder() {
        onVideoRecordListener?.onTakenRecorder()
        messageDialog?.show(false)
        cvFinder?.stopVideo()
    }

}