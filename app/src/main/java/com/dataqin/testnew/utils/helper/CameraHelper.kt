package com.dataqin.testnew.utils.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Looper
import android.view.WindowManager
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraView
import androidx.camera.view.video.OnVideoSavedCallback
import androidx.camera.view.video.OutputFileResults
import androidx.lifecycle.LifecycleOwner
import com.dataqin.base.utils.DateUtil
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.common.utils.handler.WeakHandler
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

/**
 *  Created by wangyanbin
 *  相机工具类
 */
@SuppressLint("UnsafeExperimentalUsageError", "MissingPermission")
object CameraHelper {
    private var weakActivity: WeakReference<Activity>? = null
    private var cvFinder: CameraView? = null
    private var shootMedia: MediaPlayer? = null
    private var recordingMedia: MediaPlayer? = null
    private val executors by lazy { Executors.newSingleThreadExecutor() }
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }
    var onCameraListener: OnCameraListener? = null
    var onVideoRecordListener: OnVideoRecordListener? = null

    fun initialize(activity: Activity, lifecycleOwner: LifecycleOwner, cvFinder: CameraView, recording: Boolean = false) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val audio = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volume = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        if (volume != 0) {
            shootMedia = MediaPlayer.create(activity, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"))
            recordingMedia = MediaPlayer.create(activity, Uri.parse("file:///system/media/audio/ui/camera_focus.ogg"))
        }
        cvFinder.bindToLifecycle(lifecycleOwner)
        cvFinder.captureMode = if (recording) CameraView.CaptureMode.VIDEO else CameraView.CaptureMode.IMAGE
        this.weakActivity = WeakReference(activity)
        this.cvFinder = cvFinder
    }

    //拍照
    fun takePicture() {
        shootMedia?.start()
        val filePath = Constants.APPLICATION_FILE_PATH + "/Photo"
        val fileName = DateUtil.getDateTimeStr(DateUtil.EN_YMDHMS_FORMAT, System.currentTimeMillis()) + ".jpg"
        val pictureFile = File(FileUtil.isExistDir(filePath), fileName)
        cvFinder?.takePicture(ImageCapture.OutputFileOptions.Builder(pictureFile).build(), executors, object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    weakHandler.post {
                        onCameraListener?.onTakePictureSuccess(pictureFile)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    weakHandler.post {
                        onCameraListener?.onTakePictureFail(exception)
                    }
                }
            })
    }

    //开始录像
    fun startRecording() {
        if (cvFinder?.isRecording == true) {
            return
        }
        recordingMedia?.start()
        val filePath = Constants.APPLICATION_FILE_PATH + "/Video"
        val fileName = DateUtil.getDateTimeStr(DateUtil.EN_YMDHMS_FORMAT, System.currentTimeMillis()) + ".mp4"
        val videoFile = File(FileUtil.isExistDir(filePath), fileName)
        cvFinder?.startRecording(videoFile, executors, object : OnVideoSavedCallback {
            override fun onVideoSaved(outputFileResults: OutputFileResults) {
                weakHandler.post {
                    onVideoRecordListener?.onStartRecorder(videoFile)
                }
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
            }
        })
    }

    //停止录像
    fun stopRecording() {
        shootMedia?.start()
        cvFinder?.stopRecording()
        onVideoRecordListener?.onStopRecorder()
    }

    //镜头翻转
    fun toggleCamera() {
        cvFinder?.toggleCamera()
    }

    //是否在录制
    fun isRecording(): Boolean {
        return cvFinder?.isRecording ?: false
    }

    //销毁
    fun onDestroy() {
        executors.isShutdown
    }

    interface OnCameraListener {

        fun onTakePictureSuccess(pictureFile: File?)

        fun onTakePictureFail(exception: ImageCaptureException)

    }

    interface OnVideoRecordListener {

        fun onStartRecorder(videoFile: File?)

        fun onStopRecorder()

    }

}