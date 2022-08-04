package com.dataqin.testnew.widget.keyboard;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dataqin.base.widget.SimpleViewGroup;
import com.dataqin.testnew.R;
import com.dataqin.testnew.widget.keyboard.callback.OnInputListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 密码输入控件
 */
public class PasswordInputView extends SimpleViewGroup {
    private View view;
    private LinearLayout llPassword;
    private ImageView ivCancel;
    private VirtualKeyboardView vkKeyboard;
    private TextView[] tvList;//用数组保存6个TextView
    private ImageView[] imgList;//用数组保存6个TextView
    private int currentIndex = -1;//用于记录当前输入密码格位置
    private final ArrayList<Map<String, String>> valueList = new ArrayList<>();

    public PasswordInputView(Context context) {
        super(context);
        initialize();
    }

    public PasswordInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public PasswordInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        Context context = getContext();
        view = LayoutInflater.from(context).inflate(R.layout.view_password_input, null);
        llPassword = view.findViewById(R.id.ll_password);
        ivCancel = view.findViewById(R.id.iv_cancel);
        vkKeyboard = view.findViewById(R.id.vk_keyboard);
        //初始化按钮上应该显示的数字
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
        tvList = new TextView[6];
        tvList[0] = view.findViewById(R.id.tv_password1);
        tvList[1] = view.findViewById(R.id.tv_password2);
        tvList[2] = view.findViewById(R.id.tv_password3);
        tvList[3] = view.findViewById(R.id.tv_password4);
        tvList[4] = view.findViewById(R.id.tv_password5);
        tvList[5] = view.findViewById(R.id.tv_password6);

        imgList = new ImageView[6];
        imgList[0] = view.findViewById(R.id.iv_password1);
        imgList[1] = view.findViewById(R.id.iv_password2);
        imgList[2] = view.findViewById(R.id.iv_password3);
        imgList[3] = view.findViewById(R.id.iv_password4);
        imgList[4] = view.findViewById(R.id.iv_password5);
        imgList[5] = view.findViewById(R.id.iv_password6);

        VirtualKeyboardAdapter adapter = new VirtualKeyboardAdapter(valueList);
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
        vkKeyboard.getRecyclerView().setAdapter(adapter);
    }

    @Override
    public void drawView() {
        if (onFinish()) addView(view);
    }

    /**
     * 获取顶层支付整体
     *
     * @return
     */
    public LinearLayout getPassword() {
        return llPassword;
    }

    /**
     * 获取关闭按钮
     *
     * @return
     */
    public ImageView getCancel() {
        return ivCancel;
    }

    /**
     * 获取底部虚拟键盘
     *
     * @return
     */
    public VirtualKeyboardView getVirtualKeyboardView() {
        return vkKeyboard;
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnPasswordInputListener(OnInputListener listener) {
        tvList[5].addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    StringBuilder strPassword = new StringBuilder();//每次触发都要先将strPassword置空，再重新获取，避免由于输入删除再输入造成混乱
                    for (int i = 0; i < 6; i++) {
                        strPassword.append(tvList[i].getText().toString().trim());
                    }
                    listener.onFinish(strPassword.toString());//接口中要实现的方法，完成密码输入完成后的响应逻辑
                }
            }
        });
    }

    /**
     * 键盘复位
     */
    public void restore() {
        while (currentIndex - 1 >= -1) {
            tvList[currentIndex].setText("");
            tvList[currentIndex].setVisibility(View.VISIBLE);
            imgList[currentIndex].setVisibility(View.INVISIBLE);
            currentIndex--;
        }
    }

}