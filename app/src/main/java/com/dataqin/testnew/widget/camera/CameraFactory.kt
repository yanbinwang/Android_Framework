package com.dataqin.testnew.widget.camera

import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.provider.MediaStore.Files.FileColumns
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.TimerHelper
import com.dataqin.common.constant.Constants
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.testnew.widget.camera.callback.OnCameraListener
import com.dataqin.testnew.widget.camera.callback.OnVideoRecordListener
import java.io.FileOutputStream
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 *  Created by wangyanbin
 *  相机帮助类-基于camera实现，部分手机存在相机对焦问题
 *  以下权限需额外添加
 *  <uses-permission android:name="android.permission.FLASHLIGHT" />
 *  <uses-permission android:name="android.permission.RECORD_VIDEO" />
 *  <uses-feature android:name="android.hardware.camera" />
 *  <uses-feature android:name="android.hardware.camera.autofocus" />
 */
@SuppressLint("ClickableViewAccessibility")
class CameraFactory {
    private var safe = true
    private var recording = false
    private var camera: Camera? = null
    private var viewGroup: ViewGroup? = null
    private var cameraPreview: CameraPreview? = null
    private var mediaRecorder: MediaRecorder? = null
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

    fun initialize(viewGroup: ViewGroup, cameraPreview: CameraPreview) {
        this.viewGroup = viewGroup
        this.cameraPreview = cameraPreview
        viewGroup.addView(cameraPreview)
        cameraPreview.setOnTouchListener { _, event ->
            focusOnTouch(event.x.toInt(), event.y.toInt(), viewGroup as FrameLayout)
            false
        }
    }

    fun initCamera() {
        camera = instanceCamera()
        if (null != camera) {
            val parameters = camera?.parameters
            //设置拍照后存储的图片格式
            parameters?.pictureFormat = PixelFormat.JPEG
            val focusModes = parameters?.supportedFocusModes
            if (focusModes?.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)!!) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                LogUtil.i(TAG, "params.setFocusMode : ${Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE}")
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                LogUtil.i(TAG, "params.setFocusMode : ${Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO}")
            }
            //设置PreviewSize和PictureSize
            var previewWidth = 0
            var previewHeight = 0
            try {
                //选择合适的预览尺寸
                val previewSizeList = parameters.supportedPreviewSizes
                if (previewSizeList.size > 1) {
                    for (cur in previewSizeList) {
                        if (cur.width >= previewWidth && cur.height >= previewHeight) {
                            previewWidth = cur.width
                            previewHeight = cur.height
                            break
                        }
                    }
                }
                //获得摄像区域的大小
                parameters.setPreviewSize(previewWidth, previewHeight)
                //获得保存图片的大小
                parameters.setPictureSize(previewWidth, previewHeight)
                camera?.parameters = parameters
            } catch (ignored: Exception) {
            } finally {
                //预览旋转90度
                camera?.setDisplayOrientation(90)
            }
        }
    }

    private fun instanceCamera(): Camera? {
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
    fun getCameraId() = cameraId

    /**
     * 获取相机类
     */
    fun getCamera(): Camera? {
        if (camera == null) camera = instanceCamera()
        return camera
    }

    /**
     * 旋转镜头
     */
    fun toggleCamera(view: View? = null) {
        view?.isEnabled = false
        TimerHelper.schedule(object : TimerHelper.OnTaskListener {
            override fun run() {
                view?.isEnabled = true
            }
        })
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
        reset()
    }

    /**
     * 对焦
     */
    fun focusing() {
        if (null != viewGroup) focusOnTouch(Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT, viewGroup as FrameLayout)
    }

    /**
     * 复位
     */
    fun reset() {
        viewGroup?.removeAllViews()
        viewGroup?.addView(cameraPreview)
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
        val camera = getCamera()
        if (camera != null) {
            //先获取当前相机的参数配置对象
            val parameters = camera.parameters
            //设置聚焦模式
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            LogUtil.i(TAG, "parameters.getMaxNumFocusAreas() : " + parameters.maxNumFocusAreas)
            if (parameters.maxNumFocusAreas > 0) {
                val focusAreas = ArrayList<Camera.Area>()
                focusAreas.add(Camera.Area(rect, 1000))
                parameters.focusAreas = focusAreas
            }
            try {
                camera.cancelAutoFocus() //先要取消掉进程中所有的聚焦功能
                camera.parameters = parameters
                camera.autoFocus { success: Boolean, _: Camera? ->
                    LogUtil.i(TAG, "autoFocusCallback success:$success")
                }
            } catch (ignored: Exception) {
            }
        }
    }

    fun getFingerSpacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        LogUtil.e(TAG, "getFingerSpacing ，计算距离 = ${sqrt(x * x + y * y)}")
        return sqrt(x * x + y * y)
    }

    fun handleZoom(zoomIn: Boolean) {
        LogUtil.e(TAG, "进入缩小放大方法")
        val params = camera?.parameters
        if (params!!.isZoomSupported) {
            val maxZoom = params.maxZoom
            var zoom = params.zoom
            if (zoomIn && zoom < maxZoom) {
                LogUtil.e(TAG, "进入放大方法zoom=$zoom")
                zoom++
            } else if (zoom > 0) {
                LogUtil.e(TAG, "进入缩小方法zoom=$zoom")
                zoom--
            }
            params.zoom = zoom
            camera?.parameters = params
        } else LogUtil.e(TAG, "zoom not supported")
    }

    fun handleFocusMetering(event: MotionEvent) {
        LogUtil.e(TAG, "进入handleFocusMetering")
        val params = camera?.parameters!!
        val previewSize = params.previewSize
        val focusRect = calculateTapArea(event.x, event.y, 1f, previewSize)
        val meteringRect = calculateTapArea(event.x, event.y, 1.5f, previewSize)
        camera?.cancelAutoFocus()
        if (params.maxNumFocusAreas > 0) {
            val focusAreas = ArrayList<Camera.Area>()
            focusAreas.add(Camera.Area(focusRect, 800))
            params.focusAreas = focusAreas
        } else LogUtil.e(TAG, "focus areas not supported")
        if (params.maxNumMeteringAreas > 0) {
            val meteringAreas = ArrayList<Camera.Area>()
            meteringAreas.add(Camera.Area(meteringRect, 800))
            params.meteringAreas = meteringAreas
        } else LogUtil.e(TAG, "metering areas not supported")
        val currentFocusMode = params.focusMode
        params.focusMode = Camera.Parameters.FOCUS_MODE_MACRO
        camera?.parameters = params
        camera?.autoFocus { _, camera ->
            val parameters = camera.parameters
            parameters.focusMode = currentFocusMode
            camera.parameters = parameters
        }
    }

    private fun calculateTapArea(x: Float, y: Float, coefficient: Float, previewSize: Camera.Size?): Rect {
        val focusAreaSize = 300
        val areaSize = focusAreaSize * coefficient
        val centerX = (x / previewSize!!.width - 1000)
        val centerY = (y / previewSize.height - 1000)
        val left = clamp((centerX - areaSize / 2).toInt(), -1000, 1000)
        val top = clamp((centerY - areaSize / 2).toInt(), -1000, 1000)
        val rectF = RectF(left.toFloat(), top.toFloat(), left + areaSize, top + areaSize)
        return Rect(rectF.left.roundToInt(), rectF.top.roundToInt(), rectF.right.roundToInt(), rectF.bottom.roundToInt())
    }

    private fun clamp(x: Int, min: Int, max: Int): Int {
        if (x > max) return max
        if (x < min) return min
        return x
    }

    /**
     * 开始拍照
     */
    fun takePicture() {
        if (camera != null && safe) {
            safe = false
            camera?.takePicture(null, null, { data, _ ->
                safe = true
                val pictureFile = MediaFileUtil.getOutputFile(FileColumns.MEDIA_TYPE_IMAGE)
                if (pictureFile == null) {
                    LogUtil.e(TAG, "Error creating media file, check storage permissions")
                    onCameraListener?.onTakePictureFail(data)
                } else {
                    try {
                        val fileOutputStream = FileOutputStream(pictureFile)
                        fileOutputStream.write(data)
                        fileOutputStream.close()
                        onCameraListener?.onTakePictureSuccess(pictureFile)
                        //再次进入preview
                        camera?.startPreview()
                        camera?.cancelAutoFocus()
                    } catch (e: Exception) {
                        LogUtil.e(TAG, "Error: ${e.message}")
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
                recording = true
                mediaRecorder?.start()
                onVideoRecordListener?.onStartRecorder(path)
            } else releaseMediaRecorder()
        }
    }

    private fun prepareVideoRecorder(surface: Surface?): String {
        var videFilePath = MediaFileUtil.getOutputFile(FileColumns.MEDIA_TYPE_VIDEO).toString()
        try {
            camera?.unlock()
            mediaRecorder = MediaRecorder()
            mediaRecorder?.apply {
                setCamera(camera)
                setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
                setVideoSource(MediaRecorder.VideoSource.CAMERA)
                setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P))
                setOutputFile(videFilePath)
                setPreviewDisplay(surface)
                setOrientationHint(90)
                prepare()
            }
        } catch (e: Exception) {
            LogUtil.d(TAG, "Exception preparing MediaRecorder: " + e.message)
            videFilePath = ""
            releaseMediaRecorder()
        } finally {
            return videFilePath
        }
    }

    private fun releaseMediaRecorder() {
        try {
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
            camera?.lock()
        } catch (ignored: Exception) {
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
        recording = false
        try {
            mediaRecorder?.apply {
                setOnErrorListener(null)
                setOnInfoListener(null)
                setPreviewDisplay(null)
                stop()
            }
        } catch (e: Exception) {
            LogUtil.e(Log.getStackTraceString(e))
        }
        releaseMediaRecorder()
        camera?.lock()
        onVideoRecordListener?.onStopRecorder()
    }

    /**
     * 销毁相机
     */
    fun releaseCamera() {
        if (recording) stopRecorder()
        camera?.release()
        camera = null
    }

    /**
     * 销毁稍镜头调整
     */
    fun onDestroy() {
        releaseMediaRecorder()
        camera = null
        cameraPreview = null
        viewGroup = null
        cameraId = CameraInfo.CAMERA_FACING_BACK
    }

}