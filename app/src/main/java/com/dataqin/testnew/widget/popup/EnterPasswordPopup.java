package com.dataqin.testnew.widget.popup;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.dataqin.common.base.BasePopupWindow;
import com.dataqin.testnew.databinding.ViewPopupEnterPasswordBinding;
import com.dataqin.testnew.widget.popup.callback.OnEnterPasswordListener;

import org.jetbrains.annotations.NotNull;

/**
 * 输入支付密码
 */
public class EnterPasswordPopup extends BasePopupWindow<ViewPopupEnterPasswordBinding> {
    private OnEnterPasswordListener onEnterPasswordListener;

    public EnterPasswordPopup(@NotNull Activity activity) {
        super(activity, true);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        binding.passwordView.setOnPasswordInputFinishListener(password -> {
            dismiss();
            if (null != onEnterPasswordListener) {
                onEnterPasswordListener.enterPassword(password);
            }
            Toast.makeText(getActivity(), "支付成功，密码为：" + password, Toast.LENGTH_SHORT).show();
        });
        binding.passwordView.getIvCancel().setOnClickListener(v -> dismiss());//监听X关闭按钮
        binding.passwordView.getVirtualKeyboardView().getBack().setOnClickListener(v -> dismiss());//监听键盘上方的返回
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (null != onEnterPasswordListener) {
            onEnterPasswordListener.enterPasswordDismiss();
        }
    }

    public void showPopup(View view) {
        binding.passwordView.restore();
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void setOnEnterPasswordListener(OnEnterPasswordListener onEnterPasswordListener) {
        this.onEnterPasswordListener = onEnterPasswordListener;
    }

}