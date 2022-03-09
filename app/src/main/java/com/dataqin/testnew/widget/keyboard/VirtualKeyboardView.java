package com.dataqin.testnew.widget.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataqin.base.utils.DisplayUtilKt;
import com.dataqin.base.widget.SimpleViewGroup;
import com.dataqin.common.widget.xrecyclerview.manager.SCommonItemDecoration;
import com.dataqin.testnew.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 虚拟键盘
 */
public class VirtualKeyboardView extends SimpleViewGroup {
    private View view;
    private RecyclerView recKeyboard;
    private ImageView ivBack;
    private final ArrayList<Map<String, String>> valueList = new ArrayList<>();

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
        view = LayoutInflater.from(getContext()).inflate(R.layout.view_virtual_keyboard, null);
        recKeyboard = view.findViewById(R.id.rec_keyboard);
        ivBack = view.findViewById(R.id.iv_back);
        //初始化按钮上应该显示的数字
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", ".");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            } else if (i == 12) {
                map.put("name", "");
            }
            valueList.add(map);
        }
        setAdapter(new VirtualKeyboardAdapter(valueList));
    }

    @Override
    public void drawView() {
        if (onFinish()) addView(view);
    }

    /**
     * 设置键盘列表数据
     * @param adapter
     */
    public void setAdapter(VirtualKeyboardAdapter adapter) {
        recKeyboard.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recKeyboard.setAdapter(adapter);
        SparseArray<SCommonItemDecoration.ItemDecorationProps> propMap = new SparseArray<>();
        SCommonItemDecoration.ItemDecorationProps prop1 = new SCommonItemDecoration.ItemDecorationProps(DisplayUtilKt.dip2px(getContext(), 1), DisplayUtilKt.dip2px(getContext(), 1), true, true);
        propMap.put(0, prop1);
        recKeyboard.addItemDecoration(new SCommonItemDecoration(propMap));
    }

    /**
     * 获取键盘整体列表
     * @return
     */
    public RecyclerView getRecyclerView() {
        return recKeyboard;
    }

    /**
     * 获取关闭输入框按钮
     * @return
     */
    public ImageView getBack() {
        return ivBack;
    }

    /**
     * 获取当前数据信息
     * @return
     */
    public ArrayList<Map<String, String>> getValueList() {
        return valueList;
    }

}