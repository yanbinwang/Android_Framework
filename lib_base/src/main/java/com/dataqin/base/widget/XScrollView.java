package com.dataqin.base.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.dataqin.base.utils.LogUtil;

/**
 * Created by wyb on 2017/6/28.
 * 可监听滑动范围的scrollview
 */
public class XScrollView extends NestedScrollView implements NestedScrollView.OnScrollChangeListener {
    private int lastScrollY;//记录上一次滑动
    private long lastTime; //上一次记录的时间
    private boolean isStart = false;
    private boolean bottom = false;//是否滚动到底了
    private boolean top = false;//是否滚动在顶部
    private AddScrollChangeListener addScrollChangeListener;
    private final Handler handler;
    public int totalHeight = 0;//整個滾動内容高度
    public int viewHeight = 0;//当前view的高度

    //滚动状态
    public enum ScrollState {
        DRAG,      // 拖拽中
        SCROLLING, // 正在滚动
        IDLE       // 已停止
    }

    public XScrollView(@NonNull Context context) {
        this(context, null);
    }

    public XScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnScrollChangeListener(this);
        handler = new Handler(context.getMainLooper());
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        //实时滚动回调
        if (addScrollChangeListener != null) {
            addScrollChangeListener.onScrollChange(scrollX, scrollY, oldScrollX, oldScrollY);
        }
        if (totalHeight > viewHeight && (totalHeight - viewHeight) == scrollY) {
            LogUtil.e("[NewNestedScrollView]->onScrollChange = bottom");
            bottom = true;
        } else {
            bottom = false;
        }
        if (getScrollY() <= 0) {
            LogUtil.e("[NewNestedScrollView]->onScrollChange = top");
            top = true;
        } else {
            top = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        totalHeight = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            totalHeight += view.getMeasuredHeight();
        }
        viewHeight = getHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isStart = false;
                LogUtil.e("[NewNestedScrollView]->DRAG 拖拽中");
                if (addScrollChangeListener != null) {
                    addScrollChangeListener.onScrollState(ScrollState.DRAG);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                isStart = true;
                start();
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 开始计算是否停止还是正在滚性滑动
     */
    private void start() {
        new Thread(() -> {
            //表示已停止
            while (isStart) {
                if ((System.currentTimeMillis() - lastTime) > 50) {
                    int newScrollY = getScrollY();
                    lastTime = System.currentTimeMillis();
                    if (newScrollY - lastScrollY == 0) {
                        isStart = false;
                        LogUtil.e("[NewNestedScrollView]->IDLE 停止滚动");
                        handler.post(() -> {
                            if (addScrollChangeListener != null) {
                                addScrollChangeListener.onScrollState(ScrollState.IDLE);
                            }
                        });
                    } else {
                        handler.post(() -> {
                            LogUtil.e("[NewNestedScrollView]->SCROLLING 正在滚动中");
                            if (isStart && addScrollChangeListener != null) {
                                addScrollChangeListener.onScrollState(ScrollState.SCROLLING);
                            }
                        });
                    }
                    lastScrollY = newScrollY;
                }
            }
        }).start();
    }

    /**
     * 是否动到底
     *
     * @return
     */
    public boolean isBottom() {
        return bottom;
    }

    /**
     * 是否滚动到了 顶部
     *
     * @return
     */
    public boolean isTop() {
        return top;
    }

    /**
     * 设置监听
     *
     * @param addScrollChangeListener
     * @return
     */
    public XScrollView addScrollChangeListener(AddScrollChangeListener addScrollChangeListener) {
        this.addScrollChangeListener = addScrollChangeListener;
        return this;
    }

    public interface AddScrollChangeListener {
        /**
         * 滚动监听
         *
         * @param scrollX
         * @param scrollY
         * @param oldScrollX
         * @param oldScrollY
         */
        void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY);

        /**
         * 滚动状态
         *
         * @param state
         */
        void onScrollState(ScrollState state);

    }

}