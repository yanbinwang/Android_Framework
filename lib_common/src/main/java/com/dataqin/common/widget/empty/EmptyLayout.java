package com.dataqin.common.widget.empty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dataqin.base.widget.SimpleViewGroup;
import com.dataqin.common.R;

/**
 * Created by android on 2017/8/7.
 *
 * @author Wyb
 * <p>
 * 数据为空时候显示的页面（适用于列表，详情等）
 * 情况如下：
 * <p>
 * 1.加载中-无按钮
 * 2.空布局(没有数据或服务器接口访问失败的时候显示)-有按钮
 * 3.网络错误(只有断网情况下会显示)-有按钮
 */
@SuppressLint("InflateParams")
public class EmptyLayout extends SimpleViewGroup {
    private View contextView;
    private ImageView ivEmpty;//内容
    private TextView tvEmpty;//文本
    private TextView tvRefresh;//刷新
    private OnEmptyRefreshListener onEmptyRefreshListener;

    public EmptyLayout(Context context) {
        super(context);
        initialize();
    }

    public EmptyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initialize();
    }

    public EmptyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        Context context = getContext();
        contextView = LayoutInflater.from(context).inflate(R.layout.view_empty, null);
        ivEmpty = contextView.findViewById(R.id.iv_empty);
        tvEmpty = contextView.findViewById(R.id.tv_empty);
        tvRefresh = contextView.findViewById(R.id.tv_refresh);

        contextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));//设置LayoutParams
        contextView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_f6f8ff));

        tvRefresh.setOnClickListener(v -> {
            //进入加载中，并停止刷新动画
            showLoading();
            if (null != onEmptyRefreshListener) {
                onEmptyRefreshListener.onRefreshListener();
            }
        });
        contextView.setOnClickListener(null);
        showLoading();
    }

    @Override
    public void draw() {
        if (detectionInflate()) addView(contextView);
    }

    //设置列表所需的emptyview
    public View setListView(View listView) {
        removeView(contextView);
        ((ViewGroup) listView.getParent()).addView(contextView);//添加到当前的View hierarchy
        return contextView;
    }

    //当数据正在加载的时候显示（接口返回快速时会造成闪屏）
    public void showLoading() {
        ivEmpty.setVisibility(View.VISIBLE);
        ivEmpty.setBackgroundResource(0);
        ivEmpty.setImageResource(R.mipmap.img_loading);
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText("正在努力加载中...");
        tvRefresh.setVisibility(View.GONE);
    }

    //当数据为空时(显示需要显示的图片，以及内容字)
    public void showEmpty() {
        showEmpty(-1, null);
    }

    //当数据为空时(显示需要显示的图片，以及内容字)---传入图片-1：原图 0：不需要图片 default：传入的图片
    public void showEmpty(int resId, String emptyText) {
        ivEmpty.setVisibility(View.VISIBLE);
        ivEmpty.setBackgroundResource(0);
        if (-1 == resId) {
            ivEmpty.setImageResource(R.mipmap.img_data_empty);
        } else if (0 == resId) {
            ivEmpty.setVisibility(View.GONE);
        } else {
            ivEmpty.setImageResource(resId);
        }
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(TextUtils.isEmpty(emptyText) ? "暂无数据" : emptyText);
        tvRefresh.setVisibility(View.VISIBLE);
    }

    //当数据错误时（没有网络）
    public void showError() {
        ivEmpty.setVisibility(View.VISIBLE);
        ivEmpty.setBackgroundResource(0);
        ivEmpty.setImageResource(R.mipmap.img_net_err);
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText("暂无网络，试试刷新页面吧~");
        tvRefresh.setVisibility(View.VISIBLE);
    }

    //设置背景颜色
    public void setBackgroundColor(int color) {
        contextView.setBackgroundColor(color);
    }

    //设置点击
    public void setOnEmptyRefreshListener(OnEmptyRefreshListener onEmptyRefreshListener) {
        this.onEmptyRefreshListener = onEmptyRefreshListener;
    }

}