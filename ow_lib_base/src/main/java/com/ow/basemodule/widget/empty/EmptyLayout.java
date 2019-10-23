package com.ow.basemodule.widget.empty;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ow.basemodule.R;


/**
 * Created by android on 2017/8/7.
 *
 * @author Wyb
 * <p>
 * 数据为空时候显示的页面（适用于列表，详情等）
 * 情况如下：
 * <p>
 * 1.加载中
 * 2.加载错误(只有断网情况下会显示点击刷新按钮)
 * 3.空布局(没有数据的时候显示)
 */
@SuppressLint("InflateParams")
public class EmptyLayout extends ViewGroup {
    private Context context;
    private View contextView;
    private SwipeRefreshLayout emptyRefresh;//外层刷新
    private ImageView emptyImg;//内容
    private TextView emptyTxt;//文本
    private OnEmptyRefreshListener onEmptyRefreshListener;
    public static String EMPTY_TXT = "没有数据";//数据为空时的内容
    public static String ERROR_TXT = "没有网络";//数据加载失败的内容

    public EmptyLayout(Context context) {
        super(context);
        init(context, null);
    }

    public EmptyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public EmptyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        EMPTY_TXT = context.getString(R.string.view_label_data_empty_txt);
        ERROR_TXT = context.getString(R.string.view_label_data_net_txt);
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            v.layout(0, 0, r, b);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(contextView);
    }

    private void initView() {
        contextView = LayoutInflater.from(context).inflate(R.layout.view_empty, null);
        emptyRefresh = contextView.findViewById(R.id.empty_refresh);
        emptyImg = contextView.findViewById(R.id.empty_img);
        emptyTxt = contextView.findViewById(R.id.empty_txt);
        emptyRefresh.setColorSchemeColors(ContextCompat.getColor(context, R.color.purple_8f94fb));
        emptyRefresh.setOnRefreshListener(() -> {
            emptyRefresh.setRefreshing(false);
            if (null != onEmptyRefreshListener) {
                onEmptyRefreshListener.onRefreshListener();
            }
        });
        contextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));//设置LayoutParams
        contextView.setOnClickListener(null);
    }

    //设置列表所需的emptyview
    public View setListView(View listView) {
        removeView(contextView);
        ((ViewGroup) listView.getParent()).addView(contextView);//添加到当前的View hierarchy
        return contextView;
    }

    //当数据正在加载的时候显示（接口返回快速时会造成闪屏）
    public void showLoading() {
        emptyRefresh.setVisibility(View.GONE);
        emptyImg.setVisibility(View.GONE);
        emptyTxt.setVisibility(View.GONE);
    }

    //当数据为空时(显示需要显示的图片，以及内容字)
    public void showEmpty() {
        emptyRefresh.setVisibility(View.VISIBLE);
        emptyImg.setVisibility(View.VISIBLE);
        emptyImg.setBackgroundResource(0);
        emptyImg.setImageResource(R.mipmap.img_data_empty);
        emptyTxt.setVisibility(View.VISIBLE);
        emptyTxt.setText(EMPTY_TXT);
    }

    //当数据为空时(显示需要显示的图片，以及内容字)---传入图片-1：原图 0：不需要图片 default：传入的图片
    public void showEmpty(int resId, String emptyStr) {
        emptyRefresh.setVisibility(View.VISIBLE);
        emptyImg.setBackgroundResource(0);
        if (-1 == resId) {
            emptyImg.setVisibility(View.VISIBLE);
            emptyImg.setImageResource(R.mipmap.img_net_err);
        } else if (0 == resId) {
            emptyImg.setVisibility(View.GONE);
        } else {
            emptyImg.setVisibility(View.VISIBLE);
            emptyImg.setImageResource(resId);
        }
        emptyTxt.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(emptyStr)) {
            emptyTxt.setText(EMPTY_TXT);
        } else {
            emptyTxt.setText(emptyStr);
        }
    }

    //当数据错误时（没有网络）
    public void showError() {
        emptyRefresh.setVisibility(View.VISIBLE);
        emptyImg.setVisibility(View.VISIBLE);
        emptyImg.setBackgroundResource(0);
        emptyImg.setImageResource(R.mipmap.img_net_err);
        emptyTxt.setVisibility(View.VISIBLE);
        emptyTxt.setText(ERROR_TXT);
    }

    //设置背景颜色
    public void setBackgroundColor(int color) {
        emptyRefresh.setBackgroundColor(color);
    }

    //设置点击
    public void setOnEmptyRefreshListener(OnEmptyRefreshListener onEmptyRefreshListener) {
        this.onEmptyRefreshListener = onEmptyRefreshListener;
    }

}