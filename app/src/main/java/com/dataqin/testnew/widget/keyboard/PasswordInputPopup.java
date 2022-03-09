package com.dataqin.testnew.widget.keyboard;

import android.app.Activity;
import android.view.View;

import androidx.annotation.Nullable;

import com.dataqin.base.utils.AnimationLoaderKt;
import com.dataqin.base.utils.TimerHelper;
import com.dataqin.base.utils.ToastUtil;
import com.dataqin.common.base.BasePopupWindow;
import com.dataqin.testnew.databinding.ViewPopupPasswordInputBinding;
import com.dataqin.testnew.widget.keyboard.callback.OnPasswordListener;

import org.jetbrains.annotations.NotNull;

public class PasswordInputPopup extends BasePopupWindow<ViewPopupPasswordInputBinding> {
    private OnPasswordListener onPasswordListener;

    public PasswordInputPopup(@NotNull Activity activity) {
        super(activity, true);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        binding.pivPassword.setOnPasswordInputListener(password -> {
            if (null != onPasswordListener) onPasswordListener.onFinish(password);
            ToastUtil.mackToastSHORT("支付成功，密码为：" + password, getActivity());
            dismiss();
        });
        binding.pivPassword.getCancel().setOnClickListener(view -> dismiss());
        binding.pivPassword.getVirtualKeyboardView().getBack().setOnClickListener(view -> dismiss());
    }

    public void setOnPasswordListener(OnPasswordListener onPasswordListener) {
        this.onPasswordListener = onPasswordListener;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        binding.pivPassword.getPassword().setVisibility(View.GONE);
    }

    @Override
    public void showAtLocation(@Nullable View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        binding.pivPassword.restore();
        TimerHelper.schedule(() -> {
            binding.pivPassword.getPassword().setVisibility(View.VISIBLE);
            binding.pivPassword.getPassword().startAnimation(AnimationLoaderKt.getInAnimation(getActivity()));
        },500);
    }

}