package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.media.MediaActionSound
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.view.MotionEvent
import androidx.lifecycle.LifecycleOwner
import com.dataqin.base.utils.ToastUtil
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.helper.callback.OnCameraTouchListener
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
@SuppressLint("ClickableViewAccessibility")
object CameraHelper {
    private var cvFinder: CameraView? = null
    private val sound by lazy { MediaActionSound() }
    var onTakePictureListener: OnTakePictureListener? = null
    var onVideoRecordListener: OnVideoRecordListener? = null
    var onCameraTouchListener: OnCameraTouchListener? = null

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
        cvFinder.apply {
            setLifecycleOwner(owner)
            keepScreenOn = true//是否保持屏幕高亮
            playSounds = true//录像是否录制声音
            audio = Audio.ON//录制开启声音
            engine = Engine.CAMERA2//相机底层类型
            preview = Preview.GL_SURFACE//绘制相机的装载控件
            facing = Facing.BACK//打开时镜头默认后置
            flash = Flash.AUTO//闪光灯自动
            setOnTouchListener { _, event ->
                when (event?.action) {
                    MotionEvent.ACTION_UP -> onCameraTouchListener?.onUp()
                    else -> onCameraTouchListener?.onDown(cvFinder.zoom)
                }
                false
            }
        }
    }

    /**
     * 复位
     */
    @JvmStatic
    fun reset() = run { cvFinder?.zoom = 0f }

    /**
     * 镜头翻转
     */
    @JvmStatic
    fun toggleFacing() = cvFinder?.toggleFacing()

    /**
     * 开关闪光灯
     */
    @JvmStatic
    fun flash(): Boolean {
        if(cvFinder?.facing == Facing.FRONT) {
            ToastUtil.mackToastSHORT("闪光灯仅支持后置摄像头", cvFinder?.context!!)
            return false
        }
        return if(cvFinder?.flash == Flash.TORCH) {
            cvFinder?.flash = Flash.OFF
            false
        } else {
            cvFinder?.flash = Flash.TORCH
            true
        }
    }

    /**
     * 拍照/抓拍
     */
    @JvmOverloads
    @JvmStatic
    fun takePicture(snapshot: Boolean = true) {
        cvFinder?.apply {
            if (isTakingPicture) {
                ToastUtil.mackToastSHORT("正在生成图片,请勿频繁操作...", context)
                return
            }
            sound.play(MediaActionSound.SHUTTER_CLICK)
            onTakePictureListener?.onStart()
            if (snapshot) takePictureSnapshot() else takePicture()
            addCameraListener(object : CameraListener() {
                override fun onPictureShutter() {
                    super.onPictureShutter()
                    onTakePictureListener?.onShutter()
                }

                override fun onPictureTaken(result: PictureResult) {
                    super.onPictureTaken(result)
                    //在sd卡的Picture文件夹下创建对应的文件
                    val pictureFile = MediaFileUtil.getOutputFile(MEDIA_TYPE_IMAGE)
                    if (null != pictureFile) result.toFile(pictureFile) { if (null != it) onTakePictureListener?.onSuccess(it) else onTakePictureListener?.onFailed() } else onTakePictureListener?.onFailed()
                }
            })
        }
    }

    /**
     * 开始录像
     */
    @JvmOverloads
    @JvmStatic
    fun takeVideo(snapshot: Boolean = true) {
        cvFinder?.apply {
            if (isTakingVideo) {
                ToastUtil.mackToastSHORT("正在生成视频,请勿频繁操作...", context)
                return
            }
            val videoFile = MediaFileUtil.getOutputFile(MEDIA_TYPE_VIDEO)
            if (null != videoFile) {
                onVideoRecordListener?.onStartRecorder()
                if (snapshot) takeVideoSnapshot(videoFile) else takeVideo(videoFile)
                addCameraListener(object : CameraListener() {
                    //正式完成录制的回调，获取路径
                    override fun onVideoTaken(result: VideoResult) {
                        super.onVideoTaken(result)
                        onVideoRecordListener?.onStopRecorder(result.file.path)
                    }

                    override fun onVideoRecordingStart() {
                        super.onVideoRecordingStart()
                        onVideoRecordListener?.onRecording(videoFile)
                    }

//                //onVideoRecordingEnd比onVideoTaken先调取,文件此时可能还在存储
//                override fun onVideoRecordingEnd() {
//                    super.onVideoRecordingEnd()
//                    onVideoRecordListener?.onStopRecorder()
//                }
                })
            } else onVideoRecordListener?.onStopRecorder(null)
        }
    }

    /**
     * 停止录像
     */
    @JvmStatic
    fun stopVideo() {
        onVideoRecordListener?.onTakenRecorder()
        cvFinder?.stopVideo()
    }

}