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
import android.view.*
import android.widget.FrameLayout
import com.dataqin.base.utils.CompressUtil
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.TimerHelper
import com.dataqin.common.constant.Constants
import com.dataqin.media.utils.MediaFileUtil
import com.dataqin.media.utils.helper.callback.OnTakePictureListener
import com.dataqin.media.utils.helper.callback.OnVideoRecordListener
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
    private var oldDist = 0
    private var cameraOrientation = 0//相机角度
    private var videFilePath = ""
    private var safe = true
    private var recording = false
    private var camera: Camera? = null
    private var viewGroup: ViewGroup? = null
    private var cameraPreview: CameraPreview? = null
    private var mediaRecorder: MediaRecorder? = null
    private var cameraId = CameraInfo.CAMERA_FACING_BACK //前置或后置摄像头
    private var orientationListener: OrientationEventListener? = null//监听手机旋转角度
    private val TAG = "CameraInterface"
    var onTakePictureListener: OnTakePictureListener? = null
    var onVideoRecordListener: OnVideoRecordListener? = null

    companion object {
        @JvmStatic
        val instance by lazy { CameraFactory() }
    }

    fun initialize(viewGroup: ViewGroup, cameraPreview: CameraPreview) {
        this.viewGroup = viewGroup
        this.cameraPreview = cameraPreview
        this.orientationListener = object : OrientationEventListener(viewGroup.context) {
            override fun onOrientationChanged(orientation: Int) {
                cameraOrientation = orientation
            }
        }
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
            if (!focusModes.isNullOrEmpty()) {
                parameters.focusMode = when {
                    focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) -> Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) -> Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                    else -> null
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
    }

    private fun log(title: String, content: String) = LogUtil.e(TAG, " " + "\n————————————————————————${title}————————————————————————\n${content}\n————————————————————————${title}————————————————————————")

    private fun instanceCamera(): Camera? {
        var camera: Camera? = null
        try {
            camera = Camera.open(cameraId)
        } catch (e: Exception) {
            log("相机类初始化", "状态：失败\n原因：$e")
        }
        log("相机类初始化", "状态：成功")
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
    fun focusing() = run {
        if (null != viewGroup) focusOnTouch(
            Constants.SCREEN_WIDTH / 2,
            Constants.SCREEN_HEIGHT,
            viewGroup as FrameLayout
        )
    }

    /**
     * 复位
     */
    fun reset() {
        viewGroup?.removeAllViews()
        viewGroup?.addView(cameraPreview)
    }

    /**
     * 页面需要相机缩放则重写点击事件
     */
    fun onTouchEvent(event: MotionEvent?) {
        if (event?.pointerCount == 1) {
            handleFocusMetering(event)
        } else {
            when (event?.action) {
                MotionEvent.ACTION_POINTER_DOWN -> oldDist = getFingerSpacing(event).toInt()
                MotionEvent.ACTION_MOVE -> {
                    val newDist = getFingerSpacing(event)
                    if (newDist > oldDist) {
                        log("相机类手势", "状态：放大手势")
                        handleZoom(true)
                    } else if (newDist < oldDist) {
                        log("相机类手势", "状态：缩小手势")
                        handleZoom(false)
                    }
                    oldDist = newDist.toInt()
                }
            }
        }
    }

    private fun getFingerSpacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun handleZoom(zoomIn: Boolean) {
        val params = camera?.parameters
        if (params?.isZoomSupported == true) {
            val maxZoom = params.maxZoom
            var zoom = params.zoom
            if (zoomIn && zoom < maxZoom) {
                zoom++
            } else if (zoom > 0) {
                zoom--
            }
            params.zoom = zoom
            camera?.parameters = params
        }
    }

    private fun handleFocusMetering(event: MotionEvent) {
        val params = camera?.parameters!!
        val previewSize = params.previewSize
        val focusRect = calculateTapArea(event.x, event.y, 1f, previewSize)
        val meteringRect = calculateTapArea(event.x, event.y, 1.5f, previewSize)
        camera?.cancelAutoFocus()
        if (params.maxNumFocusAreas > 0) {
            val focusAreas = ArrayList<Camera.Area>()
            focusAreas.add(Camera.Area(focusRect, 800))
            params.focusAreas = focusAreas
        }
        if (params.maxNumMeteringAreas > 0) {
            val meteringAreas = ArrayList<Camera.Area>()
            meteringAreas.add(Camera.Area(meteringRect, 800))
            params.meteringAreas = meteringAreas
        }
        val currentFocusMode = params.focusMode
        params.focusMode = Camera.Parameters.FOCUS_MODE_MACRO
        camera?.parameters = params
        camera?.autoFocus { _, camera ->
            val parameters = camera.parameters
            parameters.focusMode = currentFocusMode
            camera.parameters = parameters
        }
    }

    private fun calculateTapArea(x: Float, y: Float, coefficient: Float, previewSize: Camera.Size): Rect {
        val areaSize = 300 * coefficient
        val centerX = (x / previewSize.width - 1000)
        val centerY = (y / previewSize.height - 1000)
        val left = clamp((centerX - areaSize / 2).toInt(), -1000, 1000)
        val top = clamp((centerY - areaSize / 2).toInt(), -1000, 1000)
        val rectF = RectF(left.toFloat(), top.toFloat(), left + areaSize, top + areaSize)
        return Rect(
            rectF.left.roundToInt(),
            rectF.top.roundToInt(),
            rectF.right.roundToInt(),
            rectF.bottom.roundToInt()
        )
    }

    private fun clamp(x: Int, min: Int, max: Int): Int {
        if (x > max) return max
        if (x < min) return min
        return x
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
            log("相机类焦点", "状态:${parameters.maxNumFocusAreas}")
            if (parameters.maxNumFocusAreas > 0) {
                val focusAreas = ArrayList<Camera.Area>()
                focusAreas.add(Camera.Area(rect, 1000))
                parameters.focusAreas = focusAreas
            }
            try {
                camera.cancelAutoFocus() //先要取消掉进程中所有的聚焦功能
                camera.parameters = parameters
                camera.autoFocus { success: Boolean, _: Camera? ->
                    log("相机类焦点", "状态:$success")
                }
            } catch (ignored: Exception) {
            }
        }
    }

    /**
     * 开始拍照
     */
    fun takePicture() {
        if (camera != null && safe) {
            safe = false
            onTakePictureListener?.onStart()
            onTakePictureListener?.onShutter()
            camera?.takePicture(null, null, { data, _ ->
                safe = true
                val pictureFile = MediaFileUtil.getOutputFile(FileColumns.MEDIA_TYPE_IMAGE)
                if (pictureFile == null) {
                    log("相机类拍照", "状态：失败\n原因：Error creating media file, check storage permissions")
                    onTakePictureListener?.onFailed()
                } else {
                    try {
                        val fileOutputStream = FileOutputStream(pictureFile)
                        fileOutputStream.write(data)
                        fileOutputStream.close()
                        val file = CompressUtil.degree(viewGroup?.context!!, pictureFile)
                        onTakePictureListener?.onSuccess(file)
                        //再次进入preview
                        camera?.startPreview()
                        camera?.cancelAutoFocus()
                    } catch (e: Exception) {
                        log("相机类拍照", "状态：失败\n原因：${e.message}")
                        onTakePictureListener?.onFailed()
                    }
                }
            })
        }
    }

    /**
     * 开启或停止录像
     */
    fun startOrStopRecorder(surface: Surface?) = if (recording) stopRecorder() else startRecorder(surface)

    private fun prepareVideoRecorder(surface: Surface?) {
        videFilePath = MediaFileUtil.getOutputFile(FileColumns.MEDIA_TYPE_VIDEO).toString()
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
            log("相机类录像", "状态：失败\n原因：${e.message}")
            videFilePath = ""
            releaseMediaRecorder()
        }
    }

    private fun releaseMediaRecorder() {
        try {
            mediaRecorder?.reset()
            mediaRecorder?.release()
            mediaRecorder = null
            camera?.lock()
        } catch (ignored: Exception) {
        } finally {
            onVideoRecordListener?.onStopRecorder(videFilePath)
        }
    }

    /**
     * 开始录像
     */
    fun startRecorder(surface: Surface?) {
        if (recording) return
        onVideoRecordListener?.onStartRecorder()
        prepareVideoRecorder(surface)
        if (!TextUtils.isEmpty(videFilePath)) {
            recording = true
            mediaRecorder?.start()
            onVideoRecordListener?.onRecording()
        } else releaseMediaRecorder()
    }

    /**
     * 停止录像
     */
    fun stopRecorder() {
        try {
            if (!recording) return
            recording = false
            onVideoRecordListener?.onTakenRecorder()
            mediaRecorder?.apply {
                setOnErrorListener(null)
                setOnInfoListener(null)
                setPreviewDisplay(null)
                stop()
            }
        } catch (e: Exception) {
            log("相机类录像", "状态：失败\n原因：${Log.getStackTraceString(e)}")
        } finally {
            releaseMediaRecorder()
        }
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
        orientationListener?.disable()
        cameraId = CameraInfo.CAMERA_FACING_BACK
        camera = null
        cameraPreview = null
        viewGroup = null
    }

}