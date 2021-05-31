package com.dataqin.common.widget.advertising;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dataqin.base.utils.WeakHandler;
import com.dataqin.common.imageloader.ImageLoader;
import com.dataqin.common.widget.advertising.adapter.AdAdapter;
import com.dataqin.common.widget.advertising.callback.AdGalleryImpl;
import com.dataqin.common.widget.advertising.callback.OnAdGalleryItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangyanbin
 * 无限滚动广告栏组件
 */
@SuppressLint("ClickableViewAccessibility")
public class AdGallery extends Gallery implements AdGalleryImpl, OnItemClickListener, OnItemSelectedListener, OnTouchListener {
    private int switchTime;//图片切换时间
    private int curIndex;//当前选中的数组索引
    private int oldIndex;//上次选中的数组索引
    private int focusedId;//圆点选中时的背景ID
    private int normalId;//圆点正常时的背景ID
    private int[] adsId;//图片资源ID组
    private List<String> urisList;//图片网络路径数组
    private List<ImageView> imgList;//ImageView组
    private AdAdapter adapter;//图片适配器
    private Timer timer;//自动滚动的定时器
    private LinearLayout ovalLayout;//圆点容器
    private OnAdGalleryItemClickListener onAdGalleryItemClickListener;//条目单击事件接口

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    public AdGallery(Context context) {
        super(context);
    }

    public AdGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdGallery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int kEvent;
        //检查是否往左滑动
        if (isScrollingLeft(e1, e2)) {
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
        } else {
            //检查是否往右滑动
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(kEvent, null);
        return true;
    }

    //检查是否往左滑动
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > (e1.getX() + 50);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    //点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != onAdGalleryItemClickListener) {
            onAdGalleryItemClickListener.onItemClick(curIndex);
        }
    }

    //图片切换事件
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        curIndex = position % imgList.size();
        if (ovalLayout != null && imgList.size() > 1) { // 切换圆点
            ovalLayout.getChildAt(oldIndex).setBackgroundResource(normalId); // 圆点取消
            ovalLayout.getChildAt(curIndex).setBackgroundResource(focusedId);// 圆点选中
            oldIndex = curIndex;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()) {
            startTimer();
        } else {
            stopTimer();
        }
        return false;
    }

    //开始自动滚动任务 图片大于1张才滚动
    private void startTimer() {
        if (timer == null && imgList.size() > 1 && switchTime > 0) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    weakHandler.sendEmptyMessage(0);
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

    private final WeakHandler weakHandler = new WeakHandler(msg -> {
        //不包含spacing会导致onKeyDown()失效!
        //失效onKeyDown()前先调用onScroll(null,1,0)可处理
        onScroll(null, null, 1, 0);
        onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
        return false;
    });
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="实现方法">
    @Override
    public void start(@NotNull List<String> uris, @NotNull int[] adsId, int switchTime, @NotNull LinearLayout ovalLayout, int focusedId, int normalId) {
        this.urisList = uris;
        this.adsId = adsId;
        this.switchTime = switchTime;
        this.ovalLayout = ovalLayout;
        this.focusedId = focusedId;
        this.normalId = normalId;
        initImages();// 初始化图片组
        setAdapter(adapter);
        this.setOnItemClickListener(this);
        this.setOnTouchListener(this);
        this.setOnItemSelectedListener(this);
        this.setSoundEffectsEnabled(false);
        this.setAnimationDuration(700); //动画时间
        this.setUnselectedAlpha(1); //未选中项目的透明度
        //不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
        setSpacing(0);
        //取靠近中间 图片数组的整倍数
        setSelection((getCount() / 2 / imgList.size()) * imgList.size()); //默认选中中间位置为起始位置
        setFocusableInTouchMode(true);
        //初始化圆点
        initOvalLayout();
        //开始自动滚动任务
        startTimer();
    }

    //初始化图片组
    private void initImages() {
        imgList = new ArrayList<>();
        int length = urisList != null ? urisList.size() : adsId.length;
        for (int i = 0; i < length; i++) {
            //实例化ImageView的对象
            ImageView imageview = new ImageView(getContext());
            //设置缩放方式
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            imageview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            // 本地加载图片
            if (urisList == null) {
                //为ImageView设置要显示的图片
                imageview.setImageResource(adsId[i]);
            } else {
                // 网络加载图片
                ImageLoader.getInstance().displayImage(imageview, urisList.get(i));
//                Glide.with(getContext()).load(urisList.get(i)).into(imageview);
            }
            imgList.add(imageview);
        }
        adapter = new AdAdapter(imgList);
    }

    //初始化圆点
    private void initOvalLayout() {
        if (ovalLayout != null && imgList.size() < 2) {// 如果只有一第图时不显示圆点容器
            ovalLayout.getLayoutParams().height = 0;
        } else if (ovalLayout != null) {
            // 圆点的大小是 圆点窗口的 20%;
            int ovalHeight = (int) (ovalLayout.getLayoutParams().height * 0.2);
            // 圆点的左右外边距是 圆点窗口的 20%;
//			int Ovalmargin = (int) (mOvalLayout.getLayoutParams().height * 0.2);
            //去除边距
            int ovalMargin = 0;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ovalHeight * 5, ovalHeight);
            layoutParams.setMargins(ovalMargin, 0, ovalMargin, 0);
            for (int i = 0; i < imgList.size(); i++) {
                //圆点
                View view = new View(getContext());
                view.setLayoutParams(layoutParams);
                view.setBackgroundResource(normalId);
                ovalLayout.addView(view);
            }
            //选中第一个
            ovalLayout.getChildAt(0).setBackgroundResource(focusedId);
        }
    }

    @Override
    public void setAdOnItemClickListener(OnAdGalleryItemClickListener onAdGalleryItemClickListener) {
        this.onAdGalleryItemClickListener = onAdGalleryItemClickListener;
    }
    // </editor-fold>

}