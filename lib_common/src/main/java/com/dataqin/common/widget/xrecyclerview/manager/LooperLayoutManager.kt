package com.dataqin.common.widget.xrecyclerview.manager

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

/**
 *  Created by wangyanbin
 *  无限滚动
 */
class LooperLayoutManager : RecyclerView.LayoutManager() {

    /**
     * 给 itemView 设置默认的LayoutParams
     */
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * 打开滚动开关
     */
    override fun canScrollHorizontally(): Boolean {
        return true
    }

    /**
     * 对RecyclerView进行初始化布局
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (itemCount <= 0) return
        //如果当前时准备状态，直接返回
        if (state?.isPreLayout == true) return
        //将视图分离放入scrap缓存中，以准备重新对view进行排版
        detachAndScrapAttachedViews(recycler!!)
        var actualWidth = 0
        for (i in 0 until itemCount) {
            //初始化，将在屏幕内的view填充
            val itemView = recycler.getViewForPosition(i)
            addView(itemView)
            //测量itemView的宽高
            measureChildWithMargins(itemView, 0, 0)
            val width = getDecoratedMeasuredWidth(itemView)
            val height = getDecoratedMeasuredHeight(itemView)
            //根据itemView的宽高进行布局
            layoutDecorated(itemView, actualWidth, 0, actualWidth + width, height)
            actualWidth += width
            //如果当前布局过的itemView的宽度总和大于RecyclerView的宽，则不再进行布局
            if (actualWidth > getWidth()) break
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        //横向滑动的时候，对左右两边按顺序填充itemView
        val travel = fill(dx, recycler)
        if (travel == 0) return 0
        //滑动
        offsetChildrenHorizontal(-travel)
        //回收已经不可见的itemView
        recyclerHideView(dx, recycler)
        return travel
    }

    /**
     * 左右滑动的时候，填充
     */
    private fun fill(dx: Int, recycler: Recycler?): Int {
        if (dx > 0) {
            //向左滚动
            val lastView = getChildAt(childCount - 1) ?: return 0
            val lastPos = getPosition(lastView)
            //可见的最后一个itemView完全滑进来了，需要补充新的
            if (lastView.right < width) {
                //判断可见的最后一个itemView的索引，如果是最后一个，则将下一个itemView设置为第一个，否则设置为当前索引的下一个
                val scrap = if (lastPos == itemCount - 1) {
                    recycler?.getViewForPosition(0)
                } else {
                    recycler?.getViewForPosition(lastPos + 1)
                }
                if (scrap == null) return dx
                //将新的itemViewadd进来并对其测量和布局
                addView(scrap)
                measureChildWithMargins(scrap, 0, 0)
                val width = getDecoratedMeasuredWidth(scrap)
                val height = getDecoratedMeasuredHeight(scrap)
                layoutDecorated(scrap, lastView.right, 0, lastView.right + width, height)
                return dx
            }
        } else {
            //向右滚动
            val firstView = getChildAt(0) ?: return 0
            val firstPos = getPosition(firstView)
            if (firstView.left >= 0) {
                val scrap = if (firstPos == 0) {
                    recycler?.getViewForPosition(itemCount - 1)
                } else {
                    recycler?.getViewForPosition(firstPos - 1)
                }
                if (scrap == null) return 0
                addView(scrap, 0)
                measureChildWithMargins(scrap, 0, 0)
                val width = getDecoratedMeasuredWidth(scrap)
                val height = getDecoratedMeasuredHeight(scrap)
                layoutDecorated(scrap, firstView.left - width, 0, firstView.left, height)
            }
        }
        return dx
    }

    /**
     * 回收界面不可见的view
     */
    private fun recyclerHideView(dx: Int, recycler: Recycler?) {
        if (null != recycler) {
            for (i in 0 until childCount) {
                val view = getChildAt(i) ?: continue
                if (dx > 0) {
                    //向左滚动，移除左边不在内容里的view
                    if (view.right < 0) removeAndRecycleView(view, recycler)
                } else {
                    //向右滚动，移除右边不在内容里的view
                    if (view.left > width) removeAndRecycleView(view, recycler)
                }
            }
        }
    }

}