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

import static com.dataqin.common.utils.NetWorkUtil.isNetworkAvailable;

/**
 * Created by android on 2017/8/7.
 *
 * @author Wyb
 * <p>
 * 数据为空时候显示的页面（适用于列表，详情等）
 * 情况如下：
 * <p>
 * 1.加载中-无按钮
 * 2.空数据-无按钮
 * 3.加载错误(无网络，服务器错误)-有按钮
 */
@SuppressLint("InflateParams")
public class EmptyLayout extends SimpleViewGroup {
    private View view;
    private ImageView ivEmpty;//图案
    private TextView tvEmpty;//文本
    private TextView tvRefresh;//刷新按钮
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
        view = LayoutInflater.from(context).inflate(R.layout.view_empty, null);
        ivEmpty = view.findViewById(R.id.iv_empty);
        tvEmpty = view.findViewById(R.id.tv_empty);
        tvRefresh = view.findViewById(R.id.tv_refresh);
        //设置样式
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));//设置LayoutParams
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_f6f8ff));
        //设置监听
        tvRefresh.setOnClickListener(v -> {
            //进入加载中，并停止刷新动画
            showLoading();
            if (null != onEmptyRefreshListener) {
                onEmptyRefreshListener.onRefreshClick();
            }
        });
        view.setOnClickListener(null);
        showLoading();
    }

    @Override
    public void drawView() {
        if (onFinish()) addView(view);
    }

    /**
     * 设置列表所需的emptyview
     */
    public View setListView(View listView) {
        removeView(view);
        ((ViewGroup) listView.getParent()).addView(view);//添加到当前的View hierarchy
        return view;
    }

    /**
     * 数据加载中
     */
    public void showLoading() {
        setVisibility(View.VISIBLE);
        ivEmpty.setImageResource(R.mipmap.img_data_loading);
        tvEmpty.setText(getContext().getString(R.string.label_data_loading));
        tvRefresh.setVisibility(View.GONE);
    }

    public void showEmpty() {
        showEmpty(-1, null);
    }

    /**
     * 数据为空--只会在200并且无数据的时候展示
     */
    public void showEmpty(int resId, String text) {
        setVisibility(View.VISIBLE);
        ivEmpty.setImageResource(-1 == resId ? R.mipmap.img_data_empty : resId);
        tvEmpty.setText(TextUtils.isEmpty(text) ? getContext().getString(R.string.label_data_empty) : text);
        tvRefresh.setVisibility(View.GONE);
    }

    public void showError() {
        showError(-1, null);
    }

    /**
     * 数据加载失败-无网络，服务器请求
     * 无网络优先级最高
     */
    public void showError(int resId, String text) {
        setVisibility(View.VISIBLE);
        if(!isNetworkAvailable()){
            ivEmpty.setImageResource(R.mipmap.img_data_net_error);
            tvEmpty.setText(getContext().getString(R.string.label_data_net_error));
        }else{
            ivEmpty.setImageResource(-1 == resId ? R.mipmap.img_data_error : resId);
            tvEmpty.setText(TextUtils.isEmpty(text) ? getContext().getString(R.string.label_data_error) : text);
        }
        tvRefresh.setVisibility(View.VISIBLE);
    }

    /**
     * 设置背景颜色
     */
    public void setBackgroundColor(int color) {
        view.setBackgroundColor(color);
    }

    /**
     * 设置点击
     */
    public void setOnEmptyRefreshListener(OnEmptyRefreshListener onEmptyRefreshListener) {
        this.onEmptyRefreshListener = onEmptyRefreshListener;
    }

}