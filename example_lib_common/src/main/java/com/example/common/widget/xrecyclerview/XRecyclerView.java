package com.example.common.widget.xrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;

import com.example.common.R;
import com.example.common.widget.empty.EmptyLayout;
import com.example.common.widget.xrecyclerview.callback.OnEmptyClickListener;
import com.example.common.widget.xrecyclerview.callback.OnRefreshListener;
import com.example.common.widget.xrecyclerview.refresh.SwipeRefreshLayout;
import com.example.common.widget.xrecyclerview.refresh.SwipeRefreshLayoutDirection;
import com.example.framework.utils.DisplayUtil;

/**
 * author: wyb
 * date: 2017/11/20.
 * <p>
 * 一般自定义view或viewGroup基本上都会去实现onMeasure、onLayout、onDraw方法，还有另外两个方法是onFinishInflate和onSizeChanged。
 * onFinishInflate方法只有在布局文件中加载view实例会回调，如果直接new一个view的话是不会回调的。
 */
@SuppressLint("InflateParams")
public class XRecyclerView extends ViewGroup {
    private Context context;
    private int recyclerViewType, containerType, refreshDirection;//页面类型(0无刷新-1带刷新)是否具有空布局（0无-1有）刷新类型（0顶部-1底部-2全部）
    private EmptyLayout emptyLayout;//自定义封装的空布局
    private SwipeRefreshLayout swipeRefreshLayout;//刷新控件 类型1才有
    private DetectionRecyclerView detectionRecyclerView;//数据列表
    private OnEmptyClickListener onEmptyClickListener;//空布局点击
    private OnRefreshListener onRefreshListener;//刷新回调

    public XRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.XRecyclerView);
        recyclerViewType = mTypedArray.getInt(R.styleable.XRecyclerView_recyclerViewType, 0);
        containerType = mTypedArray.getInt(R.styleable.XRecyclerView_containerType, 0);
        refreshDirection = mTypedArray.getInt(R.styleable.XRecyclerView_refreshDirection, 2);
        mTypedArray.recycle();
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

    //当view被手机绘制好的时候各个view方法的初始化
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initType(recyclerViewType);
    }

    private void initType(int recType) {
        View view = null;
        switch (recType) {
            //不带刷新
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.view_xrecyclerview, null);
                detectionRecyclerView = view.findViewById(R.id.m_rec);
                if (0 != containerType) {
                    emptyLayout = new EmptyLayout(context);
                    detectionRecyclerView.setEmptyView(emptyLayout.setListView(detectionRecyclerView));
                    detectionRecyclerView.setHasFixedSize(true);
                    detectionRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    emptyLayout.setOnEmptyRefreshListener(() -> {
                        if (null != onEmptyClickListener) {
                            onEmptyClickListener.onClickListener();
                        }
                    });
                }
                break;
            //带刷新
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.view_xrecyclerview_refresh, null);
                emptyLayout = view.findViewById(R.id.empty_layout);
                swipeRefreshLayout = view.findViewById(R.id.m_refresh);
                detectionRecyclerView = view.findViewById(R.id.m_rec);
                swipeRefreshLayout.setColorSchemeResources(R.color.blue_2e60df);
                switch (refreshDirection) {
                    case 0:
                        swipeRefreshLayout.setDirection(SwipeRefreshLayoutDirection.TOP);
                        break;
                    case 1:
                        swipeRefreshLayout.setDirection(SwipeRefreshLayoutDirection.BOTTOM);
                        break;
                    case 2:
                        swipeRefreshLayout.setDirection(SwipeRefreshLayoutDirection.BOTH);
                        break;
                }
                detectionRecyclerView.setHasFixedSize(true);
                detectionRecyclerView.setItemAnimator(new DefaultItemAnimator());
                if (0 != containerType) {
                    emptyLayout.setOnEmptyRefreshListener(() -> {
                        if (null != onEmptyClickListener) {
                            onEmptyClickListener.onClickListener();
                        }
                    });
                } else {
                    emptyLayout.setVisibility(View.GONE);
                }
                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh(int index) {
                        if (null != onRefreshListener) {
                            onRefreshListener.onRefresh();
                        }
                    }

                    @Override
                    public void onLoad(int index) {
                        if (null != onRefreshListener) {
                            onRefreshListener.onLoad();
                        }
                    }
                });
                break;
        }
        addView(view);
    }

    //当数据正在加载的时候显示
    public void showLoading() {
        if (0 != containerType) {
            emptyLayout.showLoading();
        }
    }

    //当数据为空时(显示需要显示的图片，以及内容字)
    public void showEmpty() {
        if (0 != containerType) {
            emptyLayout.showEmpty();
        }
    }

    //当数据为空时(显示需要显示的图片，以及内容字)---传入图片
    public void showEmpty(int imgInt, String emptyStr) {
        if (0 != containerType) {
            emptyLayout.showEmpty(imgInt, emptyStr);
        }
    }

    //当数据错误时（没有网络）
    public void showError() {
        if (0 != containerType) {
            emptyLayout.showError();
        }
    }

    //类型1的时候才会显示
    public void setVisibilityEmptyView(int visibility) {
        if (recyclerViewType == 1 && 0 != containerType) {
            emptyLayout.setVisibility(visibility);
        }
    }

    //设置禁止刷新
    public void setRefreshing(boolean refreshing) {
        if (recyclerViewType == 1) {
            swipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    //修改背景颜色
    public void setBackgroundColor(int color) {
        emptyLayout.setBackgroundColor(color);
    }

    //选择下标
    public void scrollToPosition(int position) {
        detectionRecyclerView.scrollToPosition(position);
    }

    //添加分隔线
    public void addItemDecoration(int horizontalSpace, int verticalSpace, boolean hasHorizontalEdge, boolean hasVerticalEdge) {
        SparseArray<SCommonItemDecoration.ItemDecorationProps> propMap = new SparseArray<>();
        SCommonItemDecoration.ItemDecorationProps prop1 = new SCommonItemDecoration.ItemDecorationProps(DisplayUtil.INSTANCE.dip2px(context, horizontalSpace), DisplayUtil.INSTANCE.dip2px(context, verticalSpace), hasHorizontalEdge, hasVerticalEdge);
        propMap.put(0, prop1);
        detectionRecyclerView.addItemDecoration(new SCommonItemDecoration(propMap));
    }

    //返回页面整体
    public DetectionRecyclerView getRecyclerView() {
        return detectionRecyclerView;
    }

    public void setOnEmptyViewClickListener(OnEmptyClickListener onEmptyClickListener) {
        this.onEmptyClickListener = onEmptyClickListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

}
