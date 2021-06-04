package com.dataqin.testnew.widget.camera

import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.provider.MediaStore.Files.FileColumns
import android.text.TextUtils
import android.util.Log
import android.view.Surface
import android.widget.FrameLayout
import com.dataqin.base.utils.LogUtil
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.testnew.widget.camera.callback.OnCameraListener
import com.dataqin.testnew.widget.camera.callback.OnVideoRecordListener
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 *  Created by wangyanbin
 *  相机帮助类-基于camera实现，部分手机存在相机对焦问题
 */
class CameraFactory {
    private var safe = true
    private var recording = false
    private var mMediaRecorder: MediaRecorder? = null
    private var mCamera: Camera? = null
    private var cameraId = CameraInfo.CAMERA_FACING_BACK //前置或后置摄像头
    private val TAG = "CameraInterface"
    var onCameraListener: OnCameraListener? = null
    var onVideoRecordListener: OnVideoRecordListener? = null

    companion object {
        @JvmStatic
        val instance: CameraFactory by lazy {
            CameraFactory()
        }
    }

    fun initCamera() {
        if (mCamera == null) mCamera = getCameraInstance()
        if (mCamera == null) return
        val params = mCamera?.parameters
        val focusModes = params?.supportedFocusModes
        //设置拍照后存储的图片格式
        params?.pictureFormat = PixelFormat.JPEG
        if (focusModes?.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)!!) {
            params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            LogUtil.i(TAG, "params.setFocusMode : " + Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
            LogUtil.i(TAG, "params.setFocusMode : " + Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
        }
        //设置PreviewSize和PictureSize
        var previewWidth = 0
        var previewHeight = 0
        try {
            //选择合适的预览尺寸
            val sizeList = params.supportedPreviewSizes
            //如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
            if (sizeList.size > 1) {
                for (cur in sizeList) {
                    if (cur.width >= previewWidth && cur.height >= previewHeight) {
                        previewWidth = cur.width
                        previewHeight = cur.height
                        break
                    }
                }
            }
            //获得摄像区域的大小
            params.setPreviewSize(previewWidth, previewHeight)
            //获得保存图片的大小
            params.setPictureSize(previewWidth, previewHeight)
            mCamera?.parameters = params
        } catch (ignored: Exception) {
        }
        //预览旋转90度
        mCamera?.setDisplayOrientation(90)
    }

    private fun getCameraInstance(): Camera? {
        var camera: Camera? = null
        try {
            camera = Camera.open(cameraId)
        } catch (e: Exception) {
            LogUtil.e(TAG, "getCameraInstance: $e")
        }
        LogUtil.d(TAG, "getCameraInstance: $camera")
        return camera
    }

    /**
     * 获取前后摄像头id
     */
    fun getCameraId(): Int {
        return cameraId
    }

    /**
     * 获取相机类
     */
    fun getCamera(): Camera? {
        if (mCamera == null) mCamera = getCameraInstance()
        return mCamera
    }

    /**
     * 旋转镜头
     */
    fun toggleCamera() {
        if (getCamera() != null) {
            val cameraInfo = CameraInfo()
            Camera.getCameraInfo(cameraId, cameraInfo)
            cameraId = if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                CameraInfo.CAMERA_FACING_FRONT
            } else {
                CameraInfo.CAMERA_FACING_BACK
            }
            releaseCamera()
        }
    }

    /**
     * 触碰相机镜头调整
     */
    fun focusOnTouch(x: Int, y: Int, preview: FrameLayout) {
        val rect = Rect(x - 100, y - 100, x + 100, y + 100)
        var left = rect.left * 2000 / preview.width - 1000
        var top = rect.top * 2000 / preview.height - 1000
        var right = rect.right * 2000 / preview.width - 1000
        var bottom = rect.bottom * 2000 / preview.height - 1000
        //如果超出了(-1000,1000)到(1000, 1000)的范围，则会导致相机崩溃
        left = if (left < -1000) -1000 else left
        top = if (top < -1000) -1000 else top
        right = if (right > 1000) 1000 else right
        bottom = if (bottom > 1000) 1000 else bottom
        focusOnRect(Rect(left, top, right, bottom))
    }

    private fun focusOnRect(rect: Rect) {
        if (getCamera() != null) {
            //先获取当前相机的参数配置对象
            val parameters = getCamera()?.parameters!!
            //设置聚焦模式
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            LogUtil.i(TAG, "parameters.getMaxNumFocusAreas() : " + parameters.maxNumFocusAreas)
            if (parameters.maxNumFocusAreas > 0) {
                val focusAreas: MutableList<Camera.Area> = ArrayList()
                focusAreas.add(Camera.Area(rect, 1000))
                parameters.focusAreas = focusAreas
            }
            try {
                getCamera()?.cancelAutoFocus() // 先要取消掉进程中所有的聚焦功能
                getCamera()?.parameters = parameters
                getCamera()?.autoFocus { success: Boolean, _: Camera? ->
                    LogUtil.i(TAG, "autoFocusCallback success:$success")
                }
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * 开始拍照
     */
    fun takePicture() {
        if (mCamera != null && safe) {
            safe = false
            mCamera?.takePicture(null, null, object : Camera.PictureCallback {
                override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
                    val pictureFile = MediaFileUtil.getOutputFile(FileColumns.MEDIA_TYPE_IMAGE)
                    safe = true
                    if (pictureFile == null) {
                        LogUtil.e(TAG, "Error creating media file, check storage permissions")
                        onCameraListener?.onTakePictureFail(data)
                        return
                    }
                    try {
                        val fos = FileOutputStream(pictureFile)
                        fos.write(data)
                        fos.close()
                        onCameraListener?.onTakePictureSuccess(pictureFile)
                        //再次进入preview
                        mCamera?.startPreview()
                        mCamera?.cancelAutoFocus()
                    } catch (e: FileNotFoundException) {
                        LogUtil.e(TAG, "File not found: " + e.message)
                        onCameraListener?.onTakePictureFail(data)
                    } catch (e: IOException) {
                        LogUtil.e(TAG, "Error accessing file: " + e.message)
                        onCameraListener?.onTakePictureFail(data)
                    }
                }
            })
        }
    }

    /**
     * 开启或停止录像
     */
    fun startOrStopRecorder(surface: Surface?) {
        if (recording) {
            stopRecorder()
        } else {
            val path = prepareVideoRecorder(surface)
            if (!TextUtils.isEmpty(path)) {
                mMediaRecorder?.start()
                recording = true
                onVideoRecordListener?.onStartRecorder(path)
            } else {
                releaseMediaRecorder()
            }
        }
    }

    private fun prepareVideoRecorder(surface: Surface?): String {
        var path = MediaFileUtil.getOutputFile(FileColumns.MEDIA_TYPE_VIDEO).toString()
        mCamera?.unlock()
        mMediaRecorder = MediaRecorder()
        mMediaRecorder?.setCamera(mCamera)
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mMediaRecorder?.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P))
        mMediaRecorder?.setOutputFile(path)
        mMediaRecorder?.setPreviewDisplay(surface)
        try {
            mMediaRecorder?.setOrientationHint(90)
            mMediaRecorder?.prepare()
        } catch (e: IllegalStateException) {
            LogUtil.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.message)
            path = ""
            releaseMediaRecorder()
        } catch (e: IOException) {
            LogUtil.d(TAG, "IOException preparing MediaRecorder: " + e.message)
            path = ""
            releaseMediaRecorder()
        } finally {
            return path
        }
    }

    private fun releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder?.reset()
            mMediaRecorder?.release()
            mMediaRecorder = null
            mCamera?.lock()
        }
    }

    /**
     * 开始录像
     */
    fun startRecorder(surface: Surface?) {
        LogUtil.i("startRecorder")
        if (recording) return
        startOrStopRecorder(surface)
    }

    /**
     * 停止录像
     */
    fun stopRecorder() {
        LogUtil.i("stopRecorder")
        if (!recording) return
        try {
            mMediaRecorder?.setOnErrorListener(null)
            mMediaRecorder?.setOnInfoListener(null)
            mMediaRecorder?.setPreviewDisplay(null)
            mMediaRecorder?.stop()
        } catch (e: Exception) {
            LogUtil.e(Log.getStackTraceString(e))
        }
        releaseMediaRecorder()
        mCamera?.lock()
        recording = false
        onVideoRecordListener?.onStopRecorder()
    }

    /**
     * 销毁相机
     */
    fun releaseCamera() {
        if (recording) stopRecorder()
        if (mCamera != null) {
            mCamera?.release()
            mCamera = null
        }
    }

    /**
     * 销毁稍镜头调整
     */
    fun onDestroy() {
        cameraId = CameraInfo.CAMERA_FACING_BACK
    }

}