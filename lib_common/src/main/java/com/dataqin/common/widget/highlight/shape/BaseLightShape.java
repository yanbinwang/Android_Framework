package com.dataqin.common.widget.highlight.shape;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.dataqin.common.widget.highlight.HeightLight;

/**
 * 高亮形状的超类
 */
public abstract class BaseLightShape implements HeightLight.LightShape {
    protected float dx;//水平方向偏移
    protected float dy;//垂直方向偏移
    protected float blurRadius = 15;//模糊半径 默认15

    public BaseLightShape() {
    }

    /**
     * @param dx 水平方向偏移
     * @param dy 垂直方向偏移
     */
    public BaseLightShape(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * @param dx         水平方向偏移
     * @param dy         垂直方向偏移
     * @param blurRadius 模糊半径 默认15px 0不模糊
     */
    public BaseLightShape(float dx, float dy, float blurRadius) {
        this.dx = dx;
        this.dy = dy;
        this.blurRadius = blurRadius;
    }

    @Override
    public void shape(Bitmap bitmap, HeightLight.ViewPosInfo viewPosInfo) {
        resetRectF4Shape(viewPosInfo.rectF, dx, dy);
        drawShape(bitmap, viewPosInfo);
    }

    /**
     * reset RectF for Shape by dx and dy.
     *
     * @param viewPosInfoRectF
     * @param dx
     * @param dy
     */
    protected abstract void resetRectF4Shape(RectF viewPosInfoRectF, float dx, float dy);

    /**
     * draw shape into bitmap
     *
     * @param bitmap
     * @param viewPosInfo
     */
    protected abstract void drawShape(Bitmap bitmap, HeightLight.ViewPosInfo viewPosInfo);

}