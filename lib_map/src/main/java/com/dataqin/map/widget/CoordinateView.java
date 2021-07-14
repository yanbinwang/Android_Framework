package com.dataqin.map.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.dataqin.map.R;

/**
 * Created by wangyanbin
 * 定位坐标view
 */
@SuppressLint("AppCompatCustomView")
public class CoordinateView extends ImageView {
    private AnimationDrawable animationDrawable;

    public CoordinateView(Context context) {
        super(context);
//        initialize();
    }

    public CoordinateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        initialize();
    }

    public CoordinateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initialize();
    }

//    private void initialize() {
//        setBackgroundResource(R.drawable.animation_coordinate);
//        animationDrawable = (AnimationDrawable) getBackground();
//    }

//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        animationDrawable.stop();
//        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
//            Drawable frame = animationDrawable.getFrame(i);
//            if (frame instanceof BitmapDrawable) {
//                ((BitmapDrawable) frame).getBitmap().recycle();
//            }
//            frame.setCallback(null);
//        }
//        animationDrawable.setCallback(null);
//    }

    public void start() {
//        stop();
//        animationDrawable.start();
        stop();
        setBackgroundResource(R.drawable.animation_coordinate);
        animationDrawable = (AnimationDrawable) getBackground();
        animationDrawable.start();
    }

    public void stop() {
//        animationDrawable.stop();
//        animationDrawable.selectDrawable(0);
        if (null != animationDrawable) {
            animationDrawable.stop();
            for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
                Drawable frame = animationDrawable.getFrame(i);
                if (frame instanceof BitmapDrawable) {
                    ((BitmapDrawable) frame).getBitmap().recycle();
                }
                frame.setCallback(null);
            }
            animationDrawable.setCallback(null);
        }
    }

}
