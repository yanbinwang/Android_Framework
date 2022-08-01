package com.dataqin.testnew.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.OrientationEventListener;
import android.widget.RelativeLayout;

/**
 * 屏幕宽高由外层注入，第一次记录对应的宽高，打开锁
 */
public class RotateLayout extends RelativeLayout {
    private boolean inflateLock;
    private int width, height, rotation;//初始宽，初始高，旋转角度
    private float x, y;//初始x，y
    private final OrientationEventListener eventListener = new OrientationEventListener(getContext()) {
        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) return;
            //下面是手机旋转准确角度与四个方向角度（0 90 180 270）的转换
            int SENSOR_ANGLE = 10;
            int rotation;
            if (orientation > 360 - SENSOR_ANGLE || orientation < SENSOR_ANGLE) {
                rotation = 0;
            } else if (orientation > 90 - SENSOR_ANGLE && orientation < 90 + SENSOR_ANGLE) {
                rotation = 270;
            } else if (orientation > 180 - SENSOR_ANGLE && orientation < 180 + SENSOR_ANGLE) {
                rotation = 180;
            } else if (orientation > 270 - SENSOR_ANGLE && orientation < 270 + SENSOR_ANGLE) {
                rotation = 90;
            } else {
                return;
            }
            setRotate(rotation);
        }
    };

    public RotateLayout(Context context) {
        super(context);
    }

    public RotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        inflateLock = false;
    }

    public void enable() {
        eventListener.enable();
    }

    public void disable() {
        eventListener.disable();
    }

    /**
     * 设置post后后续到的当前控件的横屏宽高
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        post(() -> {
            this.inflateLock = true;
            this.width = getMeasuredWidth();
            this.height = getMeasuredHeight();
            this.x = getPivotX();
            this.y = getPivotY();
        });
    }

    /**
     * 旋转
     *
     * @param rotation
     */
    public void setRotate(int rotation) {
        if (inflateLock) {
            this.rotation = rotation;
            this.setRotation(rotation);
            boolean portrait = rotation == 0 || rotation == 180;
            LayoutParams params = (LayoutParams) getLayoutParams();
            params.width = portrait ? width : height;
            params.height = portrait ? height : width;
            this.setLayoutParams(params);
            this.requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (inflateLock) {
            //根据记录的原生比例调整坐标轴
            boolean portrait = rotation == 0 || rotation == 180;
            setMeasuredDimension(portrait ? width : height, portrait ? height : width);
            if (rotation == 90) {
                setPivotX(x);
                setPivotY(x);
            }
            if (rotation == 180) {
                setPivotX(x);
                setPivotY(y);
            }
            if (rotation == 270) {
                setPivotX(y);
                setPivotY(y);
            }
        }
    }

}
