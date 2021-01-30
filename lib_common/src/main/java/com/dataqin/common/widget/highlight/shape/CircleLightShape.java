package com.dataqin.common.widget.highlight.shape;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.dataqin.common.widget.highlight.HeightLight;

/**
 * Created by caizepeng on 16/8/20.
 */
public class CircleLightShape extends BaseLightShape {
    public CircleLightShape() {
        super();
    }

    public CircleLightShape(float dx, float dy) {
        super(dx, dy);
    }

    public CircleLightShape(float dx, float dy, float blurRadius) {
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
        canvas.drawCircle(rectF.left + (rectF.width() / 2), rectF.top + (rectF.height() / 2),
                Math.max(rectF.width(), rectF.height()) / 2, paint);
    }

    @Override
    protected void resetRectF4Shape(RectF viewPosInfoRectF, float dx, float dy) {
        viewPosInfoRectF.inset(dx, dy);
    }

}