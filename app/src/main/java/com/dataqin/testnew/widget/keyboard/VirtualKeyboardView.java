package com.dataqin.testnew.widget.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.dataqin.base.widget.SimpleViewGroup;
import com.dataqin.testnew.R;

/**
 * 虚拟键盘控件
 */
public class VirtualKeyboardView extends SimpleViewGroup {
    private View contextView;
    private RelativeLayout rlKeyboardBack;//顶部隐藏键为
    private RecyclerView rvKeyboard;//底部数字键位

    public VirtualKeyboardView(Context context) {
        super(context);
        initialize();
    }

    public VirtualKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public VirtualKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        contextView = View.inflate(getContext(), R.layout.view_virtual_keyboard, null);
        rlKeyboardBack = contextView.findViewById(R.id.rl_keyboard_back);
        rvKeyboard = contextView.findViewById(R.id.rv_keyboard);
    }

    @Override
    public void draw() {
        if (onDetectionInflate()) addView(contextView);
    }

    public RelativeLayout getBack() {
        return rlKeyboardBack;
    }

    public RecyclerView getRecyclerView() {
        return rvKeyboard;
    }

}
