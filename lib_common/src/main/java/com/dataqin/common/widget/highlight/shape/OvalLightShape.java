package com.dataqin.common.widget.highlight.shape;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.dataqin.common.widget.highlight.HeightLight;


/**
 * 椭圆形高亮形状
 */
public class OvalLightShape extends BaseLightShape {
    public OvalLightShape() {
        super();
    }

    public OvalLightShape(float dx, float dy) {
        super(dx, dy);
    }

    public OvalLightShape(float dx, float dy, float blurRadius) {
        super(dx, dy, blurRadius);
    }

    @Override
    protected void drawShape(Bitmap bitmap, HeightLight.ViewPosInfo viewPosInfo) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setAntiAlias(true);
        if (blurRadius > 0) {
            paint.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.SOLID));
        }
        RectF rectF = viewPosInfo.rectF;
        canvas.drawOval(rectF, paint);
    }

    @Override
    protected void resetRectF4Shape(RectF viewPosInfoRectF, float dx, float dy) {
        //默认根据dx dy横向和竖向缩小RectF范围
        viewPosInfoRectF.inset(dx, dy);
    }

}