package com.dataqin.common.widget.xrecyclerview.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import androidx.core.view.MotionEventCompat;

import com.dataqin.common.R;
import com.dataqin.common.widget.xrecyclerview.refresh.callback.SwipeRefreshLayoutDirection;

/**
 * 下拉刷新、加载更多、分页索引
 *
 * @author xutao
 */
@SuppressLint("NewApi")
public class SwipeRefreshLayout extends ViewGroup {
    public boolean isTop;//是不是下拉
    public int firstIndex = 0;//第一页
    public int index = firstIndex;//页数索引
    private boolean mBothDirection;
    private boolean mRefreshing = false;
    private boolean mOriginalOffsetCalculated = false;
    private boolean mIsBeingDragged;
    private boolean mReturningToStart;
    private boolean mNotify;
    private int mCircleWidth;
    private int mCircleHeight;
    private int mCircleViewIndex = -1;
    private int mCurrentTargetOffsetTop;
    private int mActivePointerId = INVALID_POINTER;
    private float mStartingScale;
    private float mTotalDragDistance;
    private float mInitialMotionY;
    private View mTarget;
    private SwipeRefreshLayoutDirection mDirection;
    private CircleImageView mCircleView;
    private MaterialProgressDrawable mProgress;
    private OnRefreshListener mListener;
    private Animation mAlphaStartAnimation;
    private Animation mAlphaMaxAnimation;
    private final int mTouchSlop;
    private final int mMediumAnimationDuration;
    private final float mSpinnerFinalOffset;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);
    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;
    private static final float MAX_PROGRESS_ANGLE = .8f;
    private static final int SCALE_DOWN_DURATION = 150;
    private static final int ALPHA_ANIMATION_DURATION = 300;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private static final int[] LAYOUT_ATTRS = new int[]{android.R.attr.enabled};
    protected int mFrom;
    protected int mOriginalOffsetTop;

    private final AnimationListener mRefreshListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                mProgress.setAlpha(MAX_ALPHA);
                mProgress.start();
                if (mNotify) {
                    if (mListener != null) {
                        if (isTop) {
                            index = firstIndex;
                            mListener.onRefresh(index);
                        } else {
                            index++;
                            mListener.onLoad(index);
                        }
                    }
                }
            } else {
                mProgress.stop();
                mCircleView.setVisibility(View.GONE);
                setColorViewAlpha(MAX_ALPHA);
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop);
            }
            mCurrentTargetOffsetTop = mCircleView.getTop();
        }
    };

    private void setColorViewAlpha(int targetAlpha) {
        mCircleView.getBackground().setAlpha(targetAlpha);
        mProgress.setAlpha(targetAlpha);
    }

    public void setSize(int size) {
        if (size != MaterialProgressDrawable.LARGE && size != MaterialProgressDrawable.DEFAULT) {
            return;
        }
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (size == MaterialProgressDrawable.LARGE) {
            mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER_LARGE * metrics.density);
        } else {
            mCircleHeight = mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
        }
        mCircleView.setImageDrawable(null);
        mProgress.updateSizes(size);
        mCircleView.setImageDrawable(mProgress);
    }

    public SwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        setWillNotDraw(false);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();
        final TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.SwipeRefreshLayout);
        SwipeRefreshLayoutDirection direction = SwipeRefreshLayoutDirection.getFromInt(a2.getInt(R.styleable.SwipeRefreshLayout_direction, 0));
        if (direction != SwipeRefreshLayoutDirection.BOTH) {
            mDirection = direction;
            mBothDirection = false;
        } else {
            mDirection = SwipeRefreshLayoutDirection.TOP;
            mBothDirection = true;
        }
        a2.recycle();
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
        mCircleHeight = (int) (CIRCLE_DIAMETER * metrics.density);
        createProgressView();
        setChildrenDrawingOrderEnabled(true);
        mSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
        mTotalDragDistance = mSpinnerFinalOffset;
        //设置刷新动画颜色
//        setColorSchemeResources(android.R.color.holo_blue_light,
//                android.R.color.holo_red_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_green_light);
        setColorSchemeResources(R.color.blue_3d81f2);
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        if (mCircleViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            return mCircleViewIndex;
        } else if (i >= mCircleViewIndex) {
            return i + 1;
        } else {
            return i;
        }
    }

    private void createProgressView() {
        mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER / 2);
        mProgress = new MaterialProgressDrawable(getContext(), this);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        mCircleView.setVisibility(View.GONE);
        addView(mCircleView);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public void setRefreshing(boolean refreshing) {
        if (refreshing && !mRefreshing) {
            mRefreshing = true;
            int endTarget;
            endTarget = (int) (mSpinnerFinalOffset + mOriginalOffsetTop);
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);
        } else {
            setRefreshing(refreshing, false);
        }
    }

    private void startScaleUpAnimation(AnimationListener listener) {
        mCircleView.setVisibility(View.VISIBLE);
        mProgress.setAlpha(MAX_ALPHA);
        Animation mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleAnimation);
    }

    private void setAnimationProgress(float progress) {
        mCircleView.setScaleX(progress);
        mCircleView.setScaleY(progress);
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
            } else {
                startScaleDownAnimation(mRefreshListener);
            }
        }
    }

    private void startScaleDownAnimation(AnimationListener listener) {
        Animation mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mCircleView.setAnimationListener(listener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownAnimation);
    }

    private void startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private void startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.getAlpha(), MAX_ALPHA);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                mProgress.setAlpha((int) (startingAlpha + ((endingAlpha - startingAlpha) * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        mCircleView.setAnimationListener(null);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(alpha);
        return alpha;
    }

    public void setProgressBackgroundColor(int colorRes) {
        mCircleView.setBackgroundColor(colorRes);
        mProgress.setBackgroundColor(getResources().getColor(colorRes));
    }

    public void setColorSchemeResources(int... colorResIds) {
        final Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    public void setColorSchemeColors(int... colors) {
        ensureTarget();
        mProgress.setColorSchemeColors(colors);
    }

    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void ensureTarget() {
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mCircleView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    public void setDistanceToTriggerSync(int distance) {
        mTotalDragDistance = distance;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        int circleWidth = mCircleView.getMeasuredWidth();
        int circleHeight = mCircleView.getMeasuredHeight();
        mCircleView.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetTop, (width / 2 + circleWidth / 2), mCurrentTargetOffsetTop + circleHeight);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
        mCircleView.measure(MeasureSpec.makeMeasureSpec(mCircleWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mCircleHeight, MeasureSpec.EXACTLY));
        if (!mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true;
            switch (mDirection) {
                case BOTTOM:
                    mCurrentTargetOffsetTop = mOriginalOffsetTop = getMeasuredHeight() - mCircleView.getMeasuredHeight();
                    break;
                case TOP:
                default:
                    mCurrentTargetOffsetTop = mOriginalOffsetTop = -mCircleView.getMeasuredHeight();
                    break;
            }
        }
        mCircleViewIndex = -1;
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mCircleView) {
                mCircleViewIndex = index;
                break;
            }
        }
    }

    public boolean canChildScrollUp() {
        return mTarget.canScrollVertically(-1);
    }

    public boolean canChildScrollDown() {
        return mTarget.canScrollVertically(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        final int action = MotionEventCompat.getActionMasked(ev);
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        switch (mDirection) {
            case BOTTOM:
                if (!isEnabled() || mReturningToStart || (!mBothDirection && canChildScrollDown()) || mRefreshing) {
                    return false;
                }
                break;
            case TOP:
            default:
                if (!isEnabled() || mReturningToStart || (!mBothDirection && canChildScrollUp()) || mRefreshing) {
                    return false;
                }
                break;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCircleView.getTop());
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                if (mBothDirection) {
                    if (y > mInitialMotionY) {
                        setRawDirection(SwipeRefreshLayoutDirection.TOP);
                    } else if (y < mInitialMotionY) {
                        setRawDirection(SwipeRefreshLayoutDirection.BOTTOM);
                    }
                    if ((mDirection == SwipeRefreshLayoutDirection.BOTTOM && canChildScrollDown()) || (mDirection == SwipeRefreshLayoutDirection.TOP && canChildScrollUp())) {
                        return false;
                    }
                }
                float yDiff;
                switch (mDirection) {
                    case BOTTOM:
                        yDiff = mInitialMotionY - y;
                        break;
                    case TOP:
                    default:
                        yDiff = y - mInitialMotionY;
                        break;
                }
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                    mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }
        return mIsBeingDragged;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = ev.findPointerIndex(activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }
        switch (mDirection) {
            case BOTTOM:
                if (!isEnabled() || mReturningToStart || canChildScrollDown() || mRefreshing) {
                    return false;
                }
                break;
            case TOP:
            default:
                if (!isEnabled() || mReturningToStart || canChildScrollUp() || mRefreshing) {
                    return false;
                }
                break;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                float overscrollTop;
                switch (mDirection) {
                    case BOTTOM:
                        overscrollTop = (mInitialMotionY - y) * DRAG_RATE;
                        break;
                    case TOP:
                    default:
                        overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                        break;
                }
                if (mIsBeingDragged) {
                    mProgress.showArrow(true);
                    float originalDragPercent = overscrollTop / mTotalDragDistance;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
                    float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
                    float slingshotDist = mSpinnerFinalOffset;
                    float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;
                    int targetY;
                    if (mDirection == SwipeRefreshLayoutDirection.TOP) {
                        targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
                    } else {
                        targetY = mOriginalOffsetTop - (int) ((slingshotDist * dragPercent) + extraMove);
                    }
                    if (mCircleView.getVisibility() != View.VISIBLE) {
                        mCircleView.setVisibility(View.VISIBLE);
                    }
                    mCircleView.setScaleX(1f);
                    mCircleView.setScaleY(1f);
                    if (overscrollTop < mTotalDragDistance) {
                        if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mAlphaStartAnimation)) {
                            startProgressAlphaStartAnimation();
                        }
                        float strokeStart = (float) (adjustedPercent * .8f);
                        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
                        mProgress.setArrowScale(Math.min(1f, adjustedPercent));
                    } else {
                        if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                            startProgressAlphaMaxAnimation();
                        }
                    }
                    float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
                    mProgress.setProgressRotation(rotation);
                    setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop  /* requires update */);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(index);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                float overscrollTop;
                switch (mDirection) {
                    case BOTTOM:
                        overscrollTop = (mInitialMotionY - y) * DRAG_RATE;
                        isTop = false;
                        break;
                    case TOP:
                    default:
                        overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                        isTop = true;
                        break;
                }
                mIsBeingDragged = false;
                if (overscrollTop > mTotalDragDistance) {
                    setRefreshing(true, true);
                } else {
                    mRefreshing = false;
                    mProgress.setStartEndTrim(0f, 0f);
                    AnimationListener listener;
                    listener = new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            startScaleDownAnimation(null);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    };
                    animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
                    mProgress.showArrow(false);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }
        return true;
    }

    private void animateOffsetToCorrectPosition(int from, AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, AnimationListener listener) {
        mFrom = from;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mAnimateToStartPosition);
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget;
            switch (mDirection) {
                case BOTTOM:
                    endTarget = getMeasuredHeight() - (int) (mSpinnerFinalOffset);
                    break;
                case TOP:
                default:
                    endTarget = (int) (mSpinnerFinalOffset - Math.abs(mOriginalOffsetTop));
                    break;
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mCircleView.getTop();
            setTargetOffsetTopAndBottom(offset);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop;
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mCircleView.getTop();
        setTargetOffsetTopAndBottom(offset);
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private void startScaleDownReturnToStartAnimation(int from, AnimationListener listener) {
        mFrom = from;
        mStartingScale = mCircleView.getScaleX();
        Animation mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (mStartingScale + (-mStartingScale * interpolatedTime));
                setAnimationProgress(targetScale);
                moveToStart(interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownToStartAnimation);
    }

    private void setTargetOffsetTopAndBottom(int offset) {
        mCircleView.bringToFront();
        mCircleView.offsetTopAndBottom(offset);
        mCurrentTargetOffsetTop = mCircleView.getTop();
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    public interface OnRefreshListener {
        void onRefresh(int index);

        void onLoad(int index);
    }

    public SwipeRefreshLayoutDirection getDirection() {
        return mBothDirection ? SwipeRefreshLayoutDirection.BOTH : mDirection;
    }

    public void setDirection(SwipeRefreshLayoutDirection direction) {
        if (direction == SwipeRefreshLayoutDirection.BOTH) {
            mBothDirection = true;
        } else {
            mBothDirection = false;
            mDirection = direction;
        }
        switch (mDirection) {
            case BOTTOM:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = getMeasuredHeight() - mCircleView.getMeasuredHeight();
                break;
            case TOP:
            default:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = -mCircleView.getMeasuredHeight();
                break;
        }
    }

    private void setRawDirection(SwipeRefreshLayoutDirection direction) {
        if (mDirection == direction) {
            return;
        }
        mDirection = direction;
        switch (mDirection) {
            case BOTTOM:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = getMeasuredHeight() - mCircleView.getMeasuredHeight();
                break;
            case TOP:
            default:
                mCurrentTargetOffsetTop = mOriginalOffsetTop = -mCircleView.getMeasuredHeight();
                break;
        }
    }

    /**
     * 获得从第一页开始索引
     */
    public int getFirstIndex() {
        return firstIndex;
    }

    /**
     * 设置从第几页开始（默认值为0）
     */
    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    /**
     * 获得当前索引
     */
    public int getIndex() {
        return index;
    }

}