package com.example.common.base;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.viewbinding.ViewBinding;

import com.example.common.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by WangYanBin on 2020/7/13.
 * 所有弹出窗口的基类，弹框本身操作并不应该复杂，只拿取对应的binding即可
 * 如果具有复杂的交互则直接写成activity加入对应的透明动效
 */
public abstract class BasePopupWindow<VB extends ViewBinding> extends PopupWindow {
    protected VB binding;
    protected WeakReference<Activity> weakActivity;
    private WindowManager.LayoutParams layoutParams;
    private boolean dark;

    public BasePopupWindow(Activity activity) {
        this(activity, false);
    }

    public BasePopupWindow(Activity activity, boolean dark) {
        this.weakActivity = new WeakReference<>(activity);
        this.layoutParams = weakActivity.get().getWindow().getAttributes();
        this.dark = dark;
    }

    protected void initialize() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            try {
                Class<VB> vbClass = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
                Method method = vbClass.getMethod("inflate", LayoutInflater.class);
                binding = (VB) method.invoke(null, weakActivity.get().getLayoutInflater());
            } catch (Exception e) {
                e.printStackTrace();
            }
            setContentView(binding.getRoot());
            setFocusable(true);
            setOutsideTouchable(true);
            setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            setAnimationStyle(R.style.pushBottomAnimStyle);//默认底部弹出，可重写
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            setDismissAttributes();
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        setShowAttributes();
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        setShowAttributes();
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        setShowAttributes();
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        setShowAttributes();
        super.showAtLocation(parent, gravity, x, y);
    }

    private void setShowAttributes() {
        if (dark) {
            layoutParams.alpha = 0.7f;
            weakActivity.get().getWindow().setAttributes(layoutParams);
        }
    }

    private void setDismissAttributes() {
        if (dark) {
            setOnDismissListener(() -> {
                layoutParams.alpha = 1f;
                weakActivity.get().getWindow().setAttributes(layoutParams);
            });
        }
    }

}