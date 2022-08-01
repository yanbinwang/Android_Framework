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
    private int width, height, rotate;//初始宽，初始高，旋转角度
    private float x, y;//初始x，y
    private final OrientationEventListener eventListener = new OrientationEventListener(getContext()) {
        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) return;
            //下面是手机旋转准确角度与四个方向角度（0 90 180 270）的转换
            int SENSOR_ANGLE = 10;
            int rotate;
            if (orientation > 360 - SENSOR_ANGLE || orientation < SENSOR_ANGLE) {
                rotate = 0;
            } else if (orientation > 90 - SENSOR_ANGLE && orientation < 90 + SENSOR_ANGLE) {
                rotate = 270;
            } else if (orientation > 180 - SENSOR_ANGLE && orientation < 180 + SENSOR_ANGLE) {
                rotate = 180;
            } else if (orientation > 270 - SENSOR_ANGLE && orientation < 270 + SENSOR_ANGLE) {
                rotate = 90;
            } else {
                return;
            }
            setRotate(rotate);
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
     * 销毁时解除锁定
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        inflateLock = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (inflateLock) {
            //根据记录的原生比例调整坐标轴
            boolean portrait = rotate == 0 || rotate == 180;
            setMeasuredDimension(portrait ? width : height, portrait ? height : width);
            if (rotate == 90) {
                setPivotX(x);
                setPivotY(x);
            }
            if (rotate == 180) {
                setPivotX(x);
                setPivotY(y);
            }
            if (rotate == 270) {
                setPivotX(y);
                setPivotY(y);
            }
        }
    }

    /**
     * 旋转时候宽高替换
     *
     * @param rotate
     */
    private void setRotate(int rotate) {
        if (inflateLock) {
            this.rotate = rotate;
            this.setRotation(rotate);
            boolean portrait = rotate == 0 || rotate == 180;
            LayoutParams params = (LayoutParams) getLayoutParams();
            params.width = portrait ? width : height;
            params.height = portrait ? height : width;
            this.setLayoutParams(params);
            this.requestLayout();
        }
    }

    /**
     * 获取当前方向角
     * @return
     */
    public int getRotate() {
        return rotate;
    }

    /**
     * 页面注册监听
     */
    public void enable() {
        eventListener.enable();
    }

    /**
     * 页面销毁监听
     */
    public void disable() {
        eventListener.disable();
    }

}
