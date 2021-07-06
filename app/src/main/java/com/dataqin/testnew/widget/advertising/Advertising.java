package com.dataqin.testnew.widget.advertising;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.dataqin.base.utils.DisplayUtilKt;
import com.dataqin.base.utils.WeakHandler;
import com.dataqin.base.widget.SimpleViewGroup;
import com.dataqin.testnew.R;
import com.dataqin.testnew.widget.advertising.adapter.AdvertisingAdapter;
import com.dataqin.testnew.widget.advertising.callback.AdvertisingImpl;
import com.dataqin.testnew.widget.advertising.callback.OnAdvertisingItemClickListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;

/**
 * Created by wangyanbin
 * 广告控件
 */
@SuppressLint("ClickableViewAccessibility")
public class Advertising extends SimpleViewGroup implements AdvertisingImpl {
    private int switchTime;//图片切换时间
    private int curIndex;//当前选中的数组索引
    private int oldIndex;//上次选中的数组索引
    private int margin;//左右边距
    private int focusedId;//圆点选中时的背景ID
    private int normalId;//圆点正常时的背景ID
    private boolean allow = true;//是否允许滑动
    private ViewPager2 banner;
    private Timer timer;//自动滚动的定时器
    private LinearLayout ovalLayout;//圆点容器
    private OnAdvertisingItemClickListener onAdvertisingItemClickListener;
    private List<String> list;//图片网络路径数组
    private final AdvertisingAdapter adapter = new AdvertisingAdapter(new ArrayList<>());//图片适配器
    private final WeakHandler weakHandler = new WeakHandler(Looper.getMainLooper());

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    public Advertising(Context context) {
        super(context);
        initialize();
    }

    public Advertising(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public Advertising(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        banner = new ViewPager2(getContext());
        banner.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);//去除水波纹
        banner.setAdapter(adapter);
        banner.setOrientation(ORIENTATION_HORIZONTAL);
        banner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                curIndex = position % list.size();
                if (ovalLayout != null && list.size() > 1) {//切换圆点
                    ovalLayout.getChildAt(oldIndex).setBackgroundResource(normalId);//圆点取消
                    ovalLayout.getChildAt(curIndex).setBackgroundResource(focusedId);//圆点选中
                    oldIndex = curIndex;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                allow = positionOffsetPixels == 0;
            }
        });
    }

    @Override
    public void draw() {
        if (onDetectionInflate()) addView(banner);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTimer();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="实现方法">
    public void start(@NotNull List<String> uriList) {
        start(uriList, null);
    }

    public void start(@NotNull List<String> uriList, @Nullable LinearLayout ovalLayout) {
        start(uriList, ovalLayout, 10, R.mipmap.ic_ad_select, R.mipmap.ic_ad_unselect, 3000);
    }

    @Override
    public void start(@NotNull List<String> uriList, @Nullable LinearLayout ovalLayout, int margin, int focusedId, int normalId, int switchTime) {
        this.list = uriList;
        this.ovalLayout = ovalLayout;
        this.margin = margin;
        this.focusedId = focusedId;
        this.normalId = normalId;
        this.switchTime = switchTime;
        //设置数据
        initData();
//        //自动滚动
//        startTimer();
    }

    /**
     * 初始化圆点,图片数据
     */
    private void initData() {
        //如果只有一第图时不显示圆点容器
        if (ovalLayout != null && list.size() < 2) {
            ovalLayout.getLayoutParams().height = 0;
        } else if (ovalLayout != null) {
            ovalLayout.setGravity(Gravity.CENTER);
            //如果true代表垂直，否则水平
            boolean direction = ovalLayout.getLayoutParams().height > ovalLayout.getLayoutParams().width;
            //左右边距
            int ovalMargin = DisplayUtilKt.dip2px(getContext(), margin);
            //添加圆点
            for (int i = 0; i < list.size(); i++) {
                ImageView imageView = new ImageView(getContext());
                ovalLayout.addView(imageView);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                if (direction) {
                    layoutParams.setMargins(ovalMargin, 0, ovalMargin, 0);
                } else {
                    layoutParams.setMargins(0, ovalMargin, 0, ovalMargin);
                }
                imageView.setLayoutParams(layoutParams);
                imageView.setBackgroundResource(normalId);
            }
            //选中第一个
            ovalLayout.getChildAt(0).setBackgroundResource(focusedId);
        }
        //设置图片数据
        adapter.setData(list);
        adapter.setOnItemClickListener(position -> {
            if (null != onAdvertisingItemClickListener) {
                onAdvertisingItemClickListener.onItemClick(position);
            }
        });
        //设置默认选中的起始位置
        int position = 0;
        if (list.size() > 1) {
            int halfPosition = Integer.MAX_VALUE / 2;
            position = halfPosition - (halfPosition % list.size());
        }
        banner.setCurrentItem(position, false);
    }

    //开始自动滚动任务 图片大于1张才滚动
    private void startTimer() {
        if (timer == null && list.size() > 1) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    if (allow) {
                        weakHandler.post(() -> {
                            int current = banner.getCurrentItem();
                            int position = current + 1;
                            if (current == 0 || current == Integer.MAX_VALUE) {
                                int halfPosition = Integer.MAX_VALUE / 2;
                                position = halfPosition - (halfPosition % list.size());
                            }
                            banner.setCurrentItem(position);
                        });
                    }
                }
            }, switchTime, switchTime);
        }
    }

    //停止自动滚动任务
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void setOnAdvertisingItemClickListener(@NotNull OnAdvertisingItemClickListener onAdvertisingItemClickListener) {
        this.onAdvertisingItemClickListener = onAdvertisingItemClickListener;
    }

    public void onResume() {
        startTimer();
    }

    public void onPause() {
        stopTimer();
    }
    // </editor-fold>

}