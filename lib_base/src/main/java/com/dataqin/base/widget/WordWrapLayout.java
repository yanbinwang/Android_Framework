package com.dataqin.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dataqin.base.utils.DisplayUtilKt;

/**
 * author: wyb
 * date: 2017/11/24.
 * 自动换行的容器
 */
public class WordWrapLayout extends ViewGroup {
    private static int PADDING_HORIZONTAL;//水平方向padding
    private static int PADDING_VERTICAL;//垂直方向padding
    private static int MARGIN_CHILD;//view左右间距

    public WordWrapLayout(Context context) {
        super(context);
        initialize();
    }

    public WordWrapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public WordWrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    //默认的一些属性设置
    private void initialize() {
        PADDING_HORIZONTAL = DisplayUtilKt.dip2px(getContext(), 10);
        PADDING_VERTICAL = DisplayUtilKt.dip2px(getContext(), 5);
        MARGIN_CHILD = DisplayUtilKt.dip2px(getContext(), 10);
    }

    //返回控件的位置
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //得到容器内所有的子view的数量
        int childCount = getChildCount();
        //得到控件的实际宽度（排除margin后）
        int actualWidth = r - l;
        //设置横纵坐标0,0开始，总行数为1
        int x = 0;
        int y;
        int rows = 1;
        for (int i = 0; i < childCount; i++) {
            //得到容器内的一个view的实际宽高
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            //x坐标等于view本身的宽度加上设置的左右的margin
            x += width + MARGIN_CHILD;
            //如果x累加的长度大于了实际容器的长度
            if (x > actualWidth) {
                //x等于view本身的长度加上间距（清空之前累加的值，算作第二行的第一个）
                x = width + MARGIN_CHILD;
                //总行数+1
                rows++;
            }
            //计算view纵坐标间距
            y = (rows - 1) * (height + MARGIN_CHILD);
            //重新对view的方向进行绘制
            view.layout(x - width - MARGIN_CHILD, y, x - MARGIN_CHILD, y + height);
        }
    }

    //返回控件的实际长宽
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置横纵坐标0,0开始，总行数为1
        int x = 0;
        int y = 0;
        int rows = 1;
        //控件的实际宽度
        int actualWidth = MeasureSpec.getSize(widthMeasureSpec);
        //得到控件子view的总数
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            //给子view设置内部的padding
            View child = getChildAt(index);
            child.setPadding(PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            //x坐标等于view本身的宽度加上设置的左右的margin
            x += width + MARGIN_CHILD;
            //如果x累加的长度大于了实际容器的长度
            if (x > actualWidth) {
                //x等于view本身的长度加上间距（清空之前累加的值，算作第二行的第一个）
                x = width;
                //总行数+1
                rows++;
            }
            //计算view纵坐标间距
            y = rows * (height + MARGIN_CHILD);
        }
        //重新对view的显示长宽绘制，应等于计算出来的view的长宽的宽高加上margin和padding等操作的值
        setMeasuredDimension(actualWidth, y);
    }

}