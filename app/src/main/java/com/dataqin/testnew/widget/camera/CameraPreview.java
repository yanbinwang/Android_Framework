package com.dataqin.testnew.widget.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dataqin.base.utils.LogUtil;

/**
 * Created by wangyanbin
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private final SurfaceHolder surfaceHolder;
    private final String TAG = "CameraPreview";

    public CameraPreview(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceCreated");
        CameraFactory.getInstance().init();
        startPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.i(TAG, "surfaceChanged");
        if (surfaceHolder.getSurface() == null) return;
        try {
            if (CameraFactory.getInstance().getCamera() != null) CameraFactory.getInstance().getCamera().stopPreview();
        } catch (Exception ignored) {
        } finally {
            startPreview(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceDestroyed");
        CameraFactory.getInstance().releaseCamera();
    }

    private void startPreview(SurfaceHolder holder) {
        LogUtil.i(TAG, "startPreview");
        try {
            if (CameraFactory.getInstance().getCamera() != null) {
                CameraFactory.getInstance().getCamera().setPreviewDisplay(holder);
                CameraFactory.getInstance().getCamera().startPreview();
                CameraFactory.getInstance().getCamera().cancelAutoFocus();
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

}