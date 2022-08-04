package com.dataqin.media.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangyanbin
 * 相机外层边框，通过代码绘制
 */
@SuppressLint("DrawAllocation")
public class ViewfinderView extends View {
    private final int screenRate;//四个边角的长度
    private final Paint paint = new Paint();//画笔对象
    private static final int CORNER_WIDTH = 1;//四个边角的粗细

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //手机的屏幕密度
        float density = context.getResources().getDisplayMetrics().density;
        //像素转化成dp
        screenRate = (int) (20 * density);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect frame = new Rect(0, 0, getWidth(), getHeight());
//        //画笔颜色
//        int maskColor = Color.GREEN;
//        paint.setColor(maskColor);
        //画扫描框边上的角，总共8个部分
        paint.setColor(Color.YELLOW);
//        paint.setColor(ContextCompat.getColor(getContext(), R.color.yellow_ffdd27));
        canvas.drawRect(frame.left, frame.top, frame.left + screenRate, frame.top + CORNER_WIDTH, paint);
        canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top + screenRate, paint);
        canvas.drawRect(frame.right - screenRate, frame.top, frame.right, frame.top + CORNER_WIDTH, paint);
        canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top + screenRate, paint);
        canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left + screenRate, frame.bottom, paint);
        canvas.drawRect(frame.left, frame.bottom - screenRate, frame.left + CORNER_WIDTH, frame.bottom, paint);
        canvas.drawRect(frame.right - screenRate, frame.bottom - CORNER_WIDTH, frame.right, frame.bottom, paint);
        canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - screenRate, frame.right, frame.bottom, paint);
    }

}