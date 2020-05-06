package com.example.common.widget.xrecyclerview.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.example.common.R;
import com.example.framework.utils.DisplayUtil;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

/**
 * author:wyb
 * 对第三方自定义刷新控件再次封装
 */
public class XRefreshLayout extends TwinklingRefreshLayout {
    private Context context;
    private int refreshDirection;

    public XRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public XRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.XRefreshLayout);
        refreshDirection = mTypedArray.getInt(R.styleable.XRefreshLayout_direction, 2);
        mTypedArray.recycle();
        //定义刷新控件的一些属性
        setHeaderHeight(DisplayUtil.INSTANCE.dip2px(context, 25));
        setMaxHeadHeight(DisplayUtil.INSTANCE.dip2px(context, 30));
        setBottomHeight(DisplayUtil.INSTANCE.dip2px(context, 15));
        setMaxBottomHeight(DisplayUtil.INSTANCE.dip2px(context, 20));
        setDirection(refreshDirection);
    }

    //设置刷新样式
    public void setDirection(int refreshDirection) {
        this.refreshDirection = refreshDirection;
        switch (refreshDirection) {
            //顶部
            case 0:
                setHeaderView(new RefreshHeaderView(context));
                setEnableRefresh(true);
                setEnableLoadmore(false);
                setEnableOverScroll(false);
                break;
            //底部
            case 1:
                setBottomView(new RefreshBottomView(context));
                setEnableRefresh(false);
                setEnableLoadmore(true);
                setEnableOverScroll(false);
                break;
            //都有（默认）
            case 2:
                setHeaderView(new RefreshHeaderView(context));
                setBottomView(new RefreshBottomView(context));
                setEnableRefresh(true);
                setEnableLoadmore(true);
                setEnableOverScroll(true);
                break;
        }
    }

    public int getDirection() {
        return refreshDirection;
    }

}
