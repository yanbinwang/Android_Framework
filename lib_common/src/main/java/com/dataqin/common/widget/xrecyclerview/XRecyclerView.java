package com.dataqin.common.widget.xrecyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataqin.base.utils.DisplayUtil;
import com.dataqin.base.widget.SimpleViewGroup;
import com.dataqin.common.R;
import com.dataqin.common.base.binding.BaseAdapter;
import com.dataqin.common.widget.empty.EmptyLayout;
import com.dataqin.common.widget.xrecyclerview.callback.OnEmptyClickListener;
import com.dataqin.common.widget.xrecyclerview.manager.SCommonItemDecoration;
import com.dataqin.common.widget.xrecyclerview.refresh.SwipeRefreshLayout;
import com.dataqin.common.widget.xrecyclerview.refresh.XRefreshLayout;
import com.dataqin.common.widget.xrecyclerview.refresh.callback.SwipeRefreshLayoutDirection;

/**
 * author: wyb
 * date: 2017/11/20.
 * <p>
 * 一般自定义view或viewGroup基本上都会去实现onMeasure、onLayout、onDraw方法，还有另外两个方法是onFinishInflate和onSizeChanged。
 * onFinishInflate方法只有在布局文件中加载view实例会回调，如果直接new一个view的话是不会回调的。
 */
@SuppressWarnings("rawtypes")
@SuppressLint("InflateParams")
public class XRecyclerView extends SimpleViewGroup {
    private EmptyLayout empty;//自定义封装的空布局
    private XRefreshLayout refresh;//刷新控件 类型1才有
    private DetectionRecyclerView recycler;//数据列表
    private OnEmptyClickListener onEmptyClickListener;//空布局点击
    private int refreshId, emptyId, directionId;//页面类型(0无刷新-1带刷新)刷新类型（0顶部-1底部-2全部）是否具有空布局（0无-1有）

    public XRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.XRecyclerView);
        refreshId = mTypedArray.getInt(R.styleable.XRecyclerView_refresh, 0);
        directionId = mTypedArray.getInt(R.styleable.XRecyclerView_refreshDirection, 2);
        emptyId = mTypedArray.getInt(R.styleable.XRecyclerView_empty, 0);
        mTypedArray.recycle();
    }

    @Override
    public void draw() {
        if (onDetectionInflate()) initView(refreshId);
    }

    private void initView(int refreshId) {
        View view = null;
        switch (refreshId) {
            //不带刷新
            case 0:
                view = LayoutInflater.from(getContext()).inflate(R.layout.view_xrecyclerview, null);
                recycler = view.findViewById(R.id.drv_container);
                if (0 != emptyId) {
                    empty = new EmptyLayout(getContext());
                    recycler.setEmptyView(empty.setListView(recycler));
                    recycler.setHasFixedSize(true);
                    recycler.setItemAnimator(new DefaultItemAnimator());
                    empty.setOnEmptyRefreshListener(() -> {
                        if (null != onEmptyClickListener) {
                            onEmptyClickListener.onClick();
                        }
                    });
                }
                break;
            //带刷新
            case 1:
                view = LayoutInflater.from(getContext()).inflate(R.layout.view_xrecyclerview_refresh, null);
                empty = view.findViewById(R.id.el_container);
                refresh = view.findViewById(R.id.xrl_refresh);
                recycler = view.findViewById(R.id.drv_container);
                //设置刷新的方式，默认上下皆有
                switch (directionId) {
                    case 0:
                        refresh.setDirection(SwipeRefreshLayoutDirection.TOP);
                        break;
                    case 1:
                        refresh.setDirection(SwipeRefreshLayoutDirection.BOTTOM);
                        break;
                    case 2:
                        refresh.setDirection(SwipeRefreshLayoutDirection.BOTH);
                        break;
                }
                recycler.setHasFixedSize(true);
                recycler.setItemAnimator(new DefaultItemAnimator());
                if (0 != emptyId) {
                    empty.setOnEmptyRefreshListener(() -> {
                        if (null != onEmptyClickListener) {
                            onEmptyClickListener.onClick();
                        }
                    });
                } else {
                    empty.setVisibility(View.GONE);
                }
                break;
        }
        addView(view);
    }

    /**
     * 类型1的时候才会显示
     */
    public void setEmptyVisibility(int visibility) {
        if (refreshId == 1 && 0 != emptyId) {
            empty.setVisibility(visibility);
        }
    }

    /**
     * 设置默认recycler的输出manager
     * 默认一行一个，线样式可自画可调整
     */
    public <T extends BaseAdapter> void setAdapter(T adapter) {
        setAdapter(adapter, 0);
    }

    public <T extends BaseAdapter> void setAdapter(T adapter, int spanCount) {
        setAdapter(adapter, spanCount, 0, 0, false, false);
    }

    public <T extends BaseAdapter> void setAdapter(T adapter, int spanCount, int horizontalSpace) {
        setAdapter(adapter, spanCount, horizontalSpace, 0, true, false);
    }

    public <T extends BaseAdapter> void setAdapter(T adapter, int spanCount, int horizontalSpace, int verticalSpace) {
        setAdapter(adapter, spanCount, horizontalSpace, verticalSpace, true, true);
    }

    public <T extends BaseAdapter> void setAdapter(T adapter, int spanCount, int horizontalSpace, int verticalSpace, boolean hasHorizontalEdge, boolean hasVerticalEdge) {
        recycler.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        recycler.setAdapter(adapter);
        addItemDecoration(horizontalSpace, verticalSpace, hasHorizontalEdge, hasVerticalEdge);
    }

    /**
     * 设置横向左右滑动的adapter
     */
    public void setHorizontalAdapter(BaseAdapter adapter) {
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recycler.setAdapter(adapter);
    }

    /**
     * 修改空布局背景颜色
     */
    public void setEmptyBackgroundColor(int color) {
        empty.setBackgroundColor(color);
    }

    /**
     * 空布局刷新
     */
    public void setOnEmptyViewClickListener(OnEmptyClickListener onEmptyClickListener) {
        this.onEmptyClickListener = onEmptyClickListener;
    }

    /**
     * 刷新页面刷新
     */
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        if (refreshId == 1) {
            refresh.setOnRefreshListener(onRefreshListener);
        }
    }

    /**
     * 获取空布局
     */
    public EmptyLayout getEmptyView() {
        return empty;
    }

    /**
     * 返回页面整体
     */
    public DetectionRecyclerView getRecyclerView() {
        return recycler;
    }

    /**
     * 设置停止刷新
     */
    public void finishRefreshing() {
        if (refreshId == 1) {
            refresh.finishRefreshing();
        }
    }

    /**
     * 当数据正在加载的时候显示
     */
    public void showLoading() {
        if (0 != emptyId) {
            setEmptyVisibility(View.VISIBLE);
            empty.showLoading();
        }
    }

    public void showEmpty() {
        showEmpty(-1, null);
    }

    /**
     * 当数据为空时(显示需要显示的图片，以及内容字)
     */
    public void showEmpty(int imgInt, String text) {
        if (0 != emptyId) {
            setEmptyVisibility(View.VISIBLE);
            empty.showEmpty(imgInt, text);
        }
    }

    public void showError() {
        if (0 != emptyId) {
            setEmptyVisibility(View.VISIBLE);
            empty.showError();
        }
    }

    /**
     * 当数据异常时(显示需要显示的图片，以及内容字)
     */
    public void showError(int imgInt, String text) {
        if (0 != emptyId) {
            setEmptyVisibility(View.VISIBLE);
            empty.showError(imgInt, text);
        }
    }

    /**
     * 滚动至指定下标
     */
    public void scrollToPosition(int position) {
        recycler.scrollToPosition(position);
    }

    /**
     * 添加分隔线
     */
    public void addItemDecoration(int horizontalSpace, int verticalSpace, boolean hasHorizontalEdge, boolean hasVerticalEdge) {
        SparseArray<SCommonItemDecoration.ItemDecorationProps> propMap = new SparseArray<>();
        SCommonItemDecoration.ItemDecorationProps prop1 = new SCommonItemDecoration.ItemDecorationProps(DisplayUtil.dip2px(getContext(), horizontalSpace), DisplayUtil.dip2px(getContext(), verticalSpace), hasHorizontalEdge, hasVerticalEdge);
        propMap.put(0, prop1);
        recycler.addItemDecoration(new SCommonItemDecoration(propMap));
    }

}
