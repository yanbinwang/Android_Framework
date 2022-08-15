package com.dataqin.common.widget.xrecyclerview.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * Private class created to work around issues with AnimationListeners being
 * called before the animation is actually complete and support shadows on older
 * platforms.
 */
@SuppressLint({"AppCompatCustomView", "ResourceType", "ViewConstructor"})
class CircleImageView extends ImageView {
    private Animation.AnimationListener mListener;
    private final int mShadowRadius;
    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private static final float SHADOW_RADIUS = 3.5f;

    public CircleImageView(Context context, int color, final float radius) {
        super(context);
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int diameter = (int) (radius * density * 2);
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);
        mShadowRadius = (int) (density * SHADOW_RADIUS);
        ShapeDrawable circle;
        OvalShape oval = new OvalShadow(mShadowRadius, diameter);
        circle = new ShapeDrawable(oval);
        setLayerType(View.LAYER_TYPE_SOFTWARE, circle.getPaint());
        circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset, KEY_SHADOW_COLOR);
        final int padding = (int) mShadowRadius;
        setPadding(padding, padding, padding, padding);
        circle.getPaint().setColor(color);
        setBackground(circle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight() + mShadowRadius * 2);
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    public void setBackgroundColor(int colorRes) {
        if (getBackground() instanceof ShapeDrawable) {
            final Resources res = getResources();
            ((ShapeDrawable) getBackground()).getPaint().setColor(res.getColor(colorRes));
        }
    }

    private class OvalShadow extends OvalShape {
        private final int mShadowRadius;
        private final int mCircleDiameter;
        private final Paint mShadowPaint;

        public OvalShadow(int shadowRadius, int circleDiameter) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            mCircleDiameter = circleDiameter;
            mShadowPaint.setShader(new RadialGradient(mCircleDiameter / 2, mCircleDiameter / 2, mShadowRadius, new int[]{FILL_SHADOW_COLOR, Color.TRANSPARENT}, null, Shader.TileMode.CLAMP));
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = CircleImageView.this.getWidth();
            final int viewHeight = CircleImageView.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2 + mShadowRadius), mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2), paint);
        }
    }

}