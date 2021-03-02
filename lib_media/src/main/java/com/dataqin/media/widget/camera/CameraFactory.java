package com.dataqin.media.widget.camera;

import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.widget.FrameLayout;

import com.dataqin.base.utils.LogUtil;
import com.dataqin.common.constant.Constants;
import com.dataqin.media.model.CameraFileModel;
import com.dataqin.media.utils.helper.MediaFileHelper;
import com.dataqin.media.widget.camera.callback.OnCameraListener;
import com.dataqin.media.widget.camera.callback.OnVideoRecordListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
import static android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static com.dataqin.common.constant.Constants.CAMERA_FILE_PATH;
import static com.dataqin.common.constant.Constants.VIDEO_FILE_PATH;

/**
 * Created by wangyanbin
 * 相机类
 */
public class CameraFactory {
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//前置或后置摄像头
    private boolean isRecording = false;
    private boolean isSafe = true;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private OnCameraListener onCameraListener;
    private OnVideoRecordListener onVideoRecordListener;
    private static CameraFactory instance;
    private static final String TAG = "CameraInterface";

    // <editor-fold defaultstate="collapsed" desc="基础方法">
    public static synchronized CameraFactory getInstance() {
        if (instance == null) {
            instance = new CameraFactory();
        }
        return instance;
    }

    public void initCamera() {
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }
        if (mCamera == null) {
            return;
        }

        Camera.Parameters params = mCamera.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();
        //设置拍照后存储的图片格式
        params.setPictureFormat(PixelFormat.JPEG);
        if (focusModes.contains(FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(FOCUS_MODE_CONTINUOUS_PICTURE);
            LogUtil.i(TAG, "params.setFocusMode : " + FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModes.contains(FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(FOCUS_MODE_CONTINUOUS_VIDEO);
            LogUtil.i(TAG, "params.setFocusMode : " + FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        //设置PreviewSize和PictureSize
        int previewWidth = 0;
        int previewHeight = 0;
        try {
            //选择合适的预览尺寸
            List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
            //如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
            if (sizeList.size() > 1) {
                for (Camera.Size cur : sizeList) {
                    if (cur.width >= previewWidth && cur.height >= previewHeight) {
                        previewWidth = cur.width;
                        previewHeight = cur.height;
                        break;
                    }
                }
            }
            //获得摄像区域的大小
            params.setPreviewSize(previewWidth, previewHeight);
            //获得保存图片的大小
            params.setPictureSize(previewWidth, previewHeight);
            mCamera.setParameters(params);
        } catch (Exception ignored) {
        }
        //预览旋转90度
        mCamera.setDisplayOrientation(90);
    }

    public void focusOnTouch(int x, int y, FrameLayout preview) {
        Rect rect = new Rect(x - 100, y - 100, x + 100, y + 100);
        int left = rect.left * 2000 / preview.getWidth() - 1000;
        int top = rect.top * 2000 / preview.getHeight() - 1000;
        int right = rect.right * 2000 / preview.getWidth() - 1000;
        int bottom = rect.bottom * 2000 / preview.getHeight() - 1000;
        //如果超出了(-1000,1000)到(1000, 1000)的范围，则会导致相机崩溃
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        focusOnRect(new Rect(left, top, right, bottom));
    }

    private void focusOnRect(Rect rect) {
        if (getCamera() != null) {
            //先获取当前相机的参数配置对象
            Camera.Parameters parameters = getCamera().getParameters();
            //设置聚焦模式-部分手机不支持，所以之后的设置使用try.catch
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            LogUtil.i(TAG, "parameters.getMaxNumFocusAreas() : " + parameters.getMaxNumFocusAreas());
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<>();
                focusAreas.add(new Camera.Area(rect, 1000));
                parameters.setFocusAreas(focusAreas);
            }
            try {
                getCamera().cancelAutoFocus(); //先要取消掉进程中所有的聚焦功能
                getCamera().setParameters(parameters);
                getCamera().autoFocus((success, camera) -> LogUtil.i(TAG, "autoFocusCallback success:" + success));
            } catch (Exception ignored) {
            }
        }
    }

    public Camera getCamera() {
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }
        return mCamera;
    }

    public int getCameraId() {
        return cameraId;
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(cameraId);
        } catch (Exception e) {
            LogUtil.e(TAG, "getCameraInstance: " + e);
        }
        LogUtil.d(TAG, "getCameraInstance: " + c);
        return c;
    }

    public void onDestroy(){
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="拍照">
    public void takePicture() {
        if (mCamera != null && isSafe) {
            isSafe = false;
            mCamera.takePicture(null, null, pictureCallback);
        }
    }

    private Camera.PictureCallback pictureCallback = (data, camera) -> {
        File pictureFile = MediaFileHelper.getOutputMediaFile(MEDIA_TYPE_IMAGE, Constants.APPLICATION_NAME + "/" + CAMERA_FILE_PATH);
        isSafe = true;
        if (pictureFile == null) {
            LogUtil.e(TAG, "Error creating media file, check storage permissions");
            onTakePictureFail(data);
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            if (onCameraListener != null) {
                onCameraListener.onTakePictureSuccess(pictureFile);
            }
            //再次进入preview
            mCamera.startPreview();
            mCamera.cancelAutoFocus();
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "File not found: " + e.getMessage());
            onTakePictureFail(data);
        } catch (IOException e) {
            LogUtil.e(TAG, "Error accessing file: " + e.getMessage());
            onTakePictureFail(data);
        }
    };

    private void onTakePictureFail(byte[] data) {
        if (onCameraListener != null) {
            onCameraListener.onTakePictureFail(data);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="录像">
    public void startOrStopRecorder(Surface surface) {
        if (isRecording) {
            stopRecorder();
        } else {
            CameraFileModel fileInfo = prepareVideoRecorder(surface);
            if (fileInfo.getSuccess()) {
                mMediaRecorder.start();
                isRecording = true;
                if (onVideoRecordListener != null) {
                    onVideoRecordListener.onStartRecorder(fileInfo.getFilePath());
                }
            } else {
                releaseMediaRecorder();
            }
        }
    }

    private CameraFileModel prepareVideoRecorder(Surface surface) {
        String path = MediaFileHelper.getOutputMediaFile(MEDIA_TYPE_VIDEO, Constants.APPLICATION_NAME + "/" + VIDEO_FILE_PATH).toString();
        mCamera.unlock();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        mMediaRecorder.setOutputFile(path);
        mMediaRecorder.setPreviewDisplay(surface);
        try {
            mMediaRecorder.setOrientationHint(90);
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            LogUtil.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return new CameraFileModel(false, null);
        } catch (IOException e) {
            LogUtil.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return new CameraFileModel(false, null);
        }
        return new CameraFileModel(true, path);
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    public void releaseCamera() {
        if (isRecording) {
            stopRecorder();
        }
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void startRecorder(Surface surface) {
        LogUtil.i("startRecorder");
        if (isRecording) return;
        startOrStopRecorder(surface);
    }

    public void stopRecorder() {
        LogUtil.i("stopRecorder");
        if (!isRecording)
            return;
        try {
            //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
            //报错为：RuntimeException:stop failed
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            mMediaRecorder.stop();
        } catch (Exception e) {
            LogUtil.e(Log.getStackTraceString(e));
        }
        releaseMediaRecorder();
        mCamera.lock();
        isRecording = false;
        if (onVideoRecordListener != null) {
            onVideoRecordListener.onStopRecorder();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="手势操作">
    public void toggleCamera() {
        if (getCamera() != null) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            releaseCamera();
        }
    }

    //    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getPointerCount() == 1) {
//            handleFocusMetering(event, camera);
//        } else {
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    oldDist = getFingerSpacing(event);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    float newDist = getFingerSpacing(event);
//                    if (newDist > oldDist) {
//                        Log.e("Camera","进入放大手势");
//                        handleZoom(true, camera);
//                    } else if (newDist < oldDist) {
//                        Log.e("Camera","进入缩小手势");
//                        handleZoom(false, camera);
//                    }
//                    oldDist = newDist;
//                    break;
//            }
//        }
//        return true;
//    }

    public float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        LogUtil.e(TAG,"getFingerSpacing ，计算距离 = " + (float) Math.sqrt(x * x + y * y));
        return (float) Math.sqrt(x * x + y * y);
    }

    public void handleZoom(boolean isZoomIn) {
        LogUtil.e("Camera", "进入缩小放大方法");
        Camera.Parameters params = mCamera.getParameters();
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            int zoom = params.getZoom();
            if (isZoomIn && zoom < maxZoom) {
                LogUtil.e(TAG, "进入放大方法zoom=" + zoom);
                zoom++;
            } else if (zoom > 0) {
                LogUtil.e(TAG, "进入缩小方法zoom=" + zoom);
                zoom--;
            }
            params.setZoom(zoom);
            mCamera.setParameters(params);
        } else {
            LogUtil.e(TAG, "zoom not supported");
        }
    }

    public void handleFocusMetering(MotionEvent event) {
        LogUtil.e("Camera", "进入handleFocusMetering");
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size previewSize = params.getPreviewSize();
        Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f, previewSize);
        Rect meteringRect = calculateTapArea(event.getX(), event.getY(), 1.5f, previewSize);
        mCamera.cancelAutoFocus();
        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            params.setFocusAreas(focusAreas);
        } else {
            LogUtil.e(TAG, "focus areas not supported");
        }
        if (params.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(meteringRect, 800));
            params.setMeteringAreas(meteringAreas);
        } else {
            LogUtil.e(TAG, "metering areas not supported");
        }
        final String currentFocusMode = params.getFocusMode();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        mCamera.setParameters(params);
        mCamera.autoFocus((success, camera) -> {
            Camera.Parameters params1 = camera.getParameters();
            params1.setFocusMode(currentFocusMode);
            camera.setParameters(params1);
        });
    }

    private Rect calculateTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / previewSize.width - 1000);
        int centerY = (int) (y / previewSize.height - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="设置监听">
    public void setOnCameraListener(OnCameraListener onCameraListener) {
        this.onCameraListener = onCameraListener;
    }

    public void setOnVideoRecordListener(OnVideoRecordListener onVideoRecordListener) {
        this.onVideoRecordListener = onVideoRecordListener;
    }
    // </editor-fold>

}