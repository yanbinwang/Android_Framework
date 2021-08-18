package com.dataqin.testnew.widget.keyboard;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dataqin.base.utils.DisplayUtilKt;
import com.dataqin.base.utils.LogUtil;
import com.dataqin.base.widget.SimpleViewGroup;
import com.dataqin.common.widget.xrecyclerview.manager.SCommonItemDecoration;
import com.dataqin.testnew.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 密码控件
 */
public class PasswordView extends SimpleViewGroup {
    private View contextView;
    private ImageView ivCancel;//取消
    private VirtualKeyboardView vkvPassword;//虚拟按键
    private TextView[] tvList;//用数组保存6个TextView
    private ImageView[] imgList;//用数组保存6个TextView
    private int currentIndex = -1;//用于记录当前输入密码格位置
    private final ArrayList<Map<String, String>> valueList = new ArrayList<>();

    public PasswordView(Context context) {
        super(context);
        initialize();
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        contextView = View.inflate(getContext(), R.layout.view_password, null);
        vkvPassword = contextView.findViewById(R.id.vkv_password);
        ivCancel = contextView.findViewById(R.id.iv_cancel);

        //赋值，基础变量
        RecyclerView recyclerView = vkvPassword.getRecyclerView();
        tvList = new TextView[6];
        imgList = new ImageView[6];
        tvList[0] = contextView.findViewById(R.id.tv_password_1);
        tvList[1] = contextView.findViewById(R.id.tv_password_2);
        tvList[2] = contextView.findViewById(R.id.tv_password_3);
        tvList[3] = contextView.findViewById(R.id.tv_password_4);
        tvList[4] = contextView.findViewById(R.id.tv_password_5);
        tvList[5] = contextView.findViewById(R.id.tv_password_6);
        imgList[0] = contextView.findViewById(R.id.iv_password_1);
        imgList[1] = contextView.findViewById(R.id.iv_password_2);
        imgList[2] = contextView.findViewById(R.id.iv_password_3);
        imgList[3] = contextView.findViewById(R.id.iv_password_4);
        imgList[4] = contextView.findViewById(R.id.iv_password_5);
        imgList[5] = contextView.findViewById(R.id.iv_password_6);
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", "");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            } else if (i == 12) {
                map.put("name", "");
            }
            valueList.add(map);
        }
        KeyBoardAdapter adapter = new KeyBoardAdapter(valueList);
        adapter.setOnItemClickListener(position -> {
            //点击0~9按钮
            if (position < 11 && position != 9) {
                //判断输入位置————要小心数组越界
                if (currentIndex >= -1 && currentIndex < 5) {
                    ++currentIndex;
                    tvList[currentIndex].setText(valueList.get(position).get("name"));
                    tvList[currentIndex].setVisibility(View.INVISIBLE);
                    imgList[currentIndex].setVisibility(View.VISIBLE);
                }
            } else {
                //点击退格键
                if (position == 11) {
                    //判断是否删除完毕————要小心数组越界
                    if (currentIndex - 1 >= -1) {
                        tvList[currentIndex].setText("");
                        tvList[currentIndex].setVisibility(View.VISIBLE);
                        imgList[currentIndex].setVisibility(View.INVISIBLE);
                        currentIndex--;
                    }
                }
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
        SparseArray<SCommonItemDecoration.ItemDecorationProps> propMap = new SparseArray<>();
        SCommonItemDecoration.ItemDecorationProps prop1 = new SCommonItemDecoration.ItemDecorationProps(DisplayUtilKt.dip2px(getContext(), 1), DisplayUtilKt.dip2px(getContext(), 1), true, true);
        propMap.put(0, prop1);
        recyclerView.addItemDecoration(new SCommonItemDecoration(propMap));
    }

    @Override
    public void draw() {
        if (onDetectionInflate()) addView(contextView);
    }

    /**
     * 设置监听方法，在第6位输入完成后触发
     */
    public void setOnPasswordInputFinishListener(OnPasswordInputFinishListener listener) {
        tvList[5].addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    //每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
                    StringBuilder strPassword = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        strPassword.append(tvList[i].getText().toString().trim());
                    }
                    LogUtil.e("strPassword :" + strPassword);
                    //接口中要实现的方法，完成密码输入完成后的响应逻辑
                    listener.inputFinish(strPassword.toString());
                }
            }
        });
    }

    /**
     * 恢复默认
     */
    public void restore() {
        while (currentIndex - 1 >= -1) {
            tvList[currentIndex].setText("");
            tvList[currentIndex].setVisibility(View.VISIBLE);
            imgList[currentIndex].setVisibility(View.INVISIBLE);
            currentIndex--;
        }
    }

    public VirtualKeyboardView getVirtualKeyboardView() {
        return vkvPassword;
    }

    public ImageView getIvCancel() {
        return ivCancel;
    }

    public interface OnPasswordInputFinishListener {

        void inputFinish(String password);

    }

}