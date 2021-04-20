package com.dataqin.common.widget.xrecyclerview.manager;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Set dividers' properties(horizontal and vertical space...) of item with type.
 * 通过item type 设置边框属性
 * Created by bosong on 2017/3/10.
 */
@SuppressLint("WrongConstant")
public class SCommonItemDecoration extends RecyclerView.ItemDecoration {
    private final SparseArray<ItemDecorationProps> mPropMap; // itemType -> prop

    public SCommonItemDecoration(SparseArray<ItemDecorationProps> propMap) {
        mPropMap = propMap;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        RecyclerView.Adapter adapter = parent.getAdapter();
        int itemType = adapter.getItemViewType(position);

        ItemDecorationProps props;
        if (mPropMap != null) {
            props = mPropMap.get(itemType);
        } else {
            return;
        }
        if (props == null) {
            return;
        }
        int spanIndex = 0;
        int spanSize = 1;
        int spanCount = 1;
        int orientation = OrientationHelper.VERTICAL;
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            spanIndex = lp.getSpanIndex();
            spanSize = lp.getSpanSize();
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            spanCount = layoutManager.getSpanCount(); // Assume that there're spanCount items in this row/column.
            orientation = layoutManager.getOrientation();
        } else if (parent.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            spanIndex = lp.getSpanIndex();
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            spanCount = layoutManager.getSpanCount(); // Assume that there're spanCount items in this row/column.
            spanSize = lp.isFullSpan() ? spanCount : 1;
            orientation = layoutManager.getOrientation();
        }

        boolean isFirstRowOrColumn, isLastRowOrColumn;
        int prePos = position > 0 ? position - 1 : -1;
        int nextPos = position < adapter.getItemCount() - 1 ? position + 1 : -1;
        // Last position on the last row 上一行的最后一个位置
        int preRowPos = position > spanIndex ? position - (1 + spanIndex) : -1;
        // First position on the next row 下一行的第一个位置
        int nextRowPos = position < adapter.getItemCount() - (spanCount - spanIndex) ? position + (spanCount - spanIndex) : -1;
        isFirstRowOrColumn = position == 0 || prePos == -1 || itemType != adapter.getItemViewType(prePos) || preRowPos == -1 || itemType != adapter.getItemViewType(preRowPos);
        isLastRowOrColumn = position == adapter.getItemCount() - 1 || nextPos == -1 || itemType != adapter.getItemViewType(nextPos) || nextRowPos == -1 || itemType != adapter.getItemViewType(nextRowPos);

        int left = 0, top = 0, right = 0, bottom = 0;
        if (orientation == GridLayoutManager.VERTICAL) {
            if (props.getHasVerticalEdge()) {
                left = props.getVerticalSpace() * (spanCount - spanIndex) / spanCount;
                right = props.getVerticalSpace() * (spanIndex + (spanSize - 1) + 1) / spanCount;
            } else {
                left = props.getVerticalSpace() * spanIndex / spanCount;
                right = props.getVerticalSpace() * (spanCount - (spanIndex + spanSize - 1) - 1) / spanCount;
            }

            if (isFirstRowOrColumn) { // First row
                if (props.getHasHorizontalEdge()) {
                    top = props.getHorizontalSpace();
                }
            }
            if (isLastRowOrColumn) { // Last row
                if (props.getHasHorizontalEdge()) {
                    bottom = props.getHorizontalSpace();
                }
            } else {
                bottom = props.getHorizontalSpace();
            }
        } else {
            if (props.getHasHorizontalEdge()) {
                top = props.getHorizontalSpace() * (spanCount - spanIndex) / spanCount;
                bottom = props.getHorizontalSpace() * (spanIndex + (spanSize - 1) + 1) / spanCount;
            } else {
                top = props.getHorizontalSpace() * spanIndex / spanCount;
                bottom = props.getHorizontalSpace() * (spanCount - (spanIndex + spanSize - 1) - 1) / spanCount;
            }
            if (isFirstRowOrColumn) { // First column
                if (props.getHasVerticalEdge()) {
                    left = props.getVerticalSpace();
                }
            }
            if (isLastRowOrColumn) { // Last column
                if (props.getHasVerticalEdge()) {
                    right = props.getVerticalSpace();
                }
            } else {
                right = props.getVerticalSpace();
            }
        }

        outRect.set(left, top, right, bottom);
    }

    public static class ItemDecorationProps {
        private final boolean hasVerticalEdge;
        private final boolean hasHorizontalEdge;
        private final int verticalSpace;
        private final int horizontalSpace;

        public ItemDecorationProps(int horizontalSpace, int verticalSpace, boolean hasHorizontalEdge, boolean hasVerticalEdge) {
            this.verticalSpace = verticalSpace;
            this.horizontalSpace = horizontalSpace;
            this.hasHorizontalEdge = hasHorizontalEdge;
            this.hasVerticalEdge = hasVerticalEdge;
        }

        public int getHorizontalSpace() {
            return this.horizontalSpace;
        }

        public int getVerticalSpace() {
            return this.verticalSpace;
        }

        public boolean getHasHorizontalEdge() {
            return this.hasHorizontalEdge;
        }

        public boolean getHasVerticalEdge() {
            return this.hasVerticalEdge;
        }
    }

}