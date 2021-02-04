package com.dataqin.media.widget.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dataqin.base.utils.LogUtil;

/**
 * Created by wangyanbin
 * 相机容器
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder surfaceHolder;
    private final String TAG = "CameraPreview";

    public CameraPreview(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //translucent半透明 transparent透明
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceCreated");
        //默认全屏的比例
        CameraInterface.getInstance().initCamera();
        startPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.i(TAG, "surfaceChanged");
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            if (CameraInterface.getInstance().getCamera() != null)
                CameraInterface.getInstance().getCamera().stopPreview();
        } catch (Exception ignored) {
        }
        startPreview(holder);
    }

    private void startPreview(SurfaceHolder holder) {
        LogUtil.i(TAG, "startPreview");
        try {
            if (CameraInterface.getInstance().getCamera() != null) {
                CameraInterface.getInstance().getCamera().setPreviewDisplay(holder);
                CameraInterface.getInstance().getCamera().startPreview();
                CameraInterface.getInstance().getCamera().cancelAutoFocus();
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceDestroyed");
        CameraInterface.getInstance().releaseCamera();
    }

}