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
    private final SurfaceHolder mHolder;
    private final String TAG = "CameraPreview";

    public CameraPreview(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceCreated");
        CameraFactory.getInstance().initCamera();
        startPreview(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.i(TAG, "surfaceChanged");
        if (mHolder.getSurface() == null) return;
        try {
            if (CameraFactory.getInstance().getCamera() != null) CameraFactory.getInstance().getCamera().stopPreview();
        } catch (Exception ignored) {
        }
        startPreview(holder);
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
