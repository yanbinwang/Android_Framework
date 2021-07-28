package com.dataqin.common.widget.dialog;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.dataqin.common.base.BaseDialog;
import com.dataqin.common.databinding.ViewDialogBinding;
import com.dataqin.common.widget.dialog.callback.OnDialogListener;

/**
 * author: wyb
 * date: 2017/8/25.
 * 类似苹果的弹出窗口类
 */
public class AppDialog extends BaseDialog<ViewDialogBinding> {
    private OnDialogListener onDialogListener;

    public AppDialog(@NonNull Context context) {
        super(context);
        initialize(true, false);
    }

    public AppDialog setParams(String tipText, String contentText, String sureText) {
        return setParams(tipText, contentText, sureText, "");
    }

    public AppDialog setParams(String tipText, String contentText, String sureText, String cancelText) {
        return setParams(tipText, contentText, sureText, cancelText, true);
    }

    //App统一提示框
    public AppDialog setParams(String tipText, String contentText, String sureText, String cancelText, boolean center) {
        //如果没有传入标题字段,则隐藏标题view
        if (TextUtils.isEmpty(tipText)) {
            binding.tvDialogTip.setVisibility(View.GONE);
        }
        //如果没有传入取消字段,则隐藏取消view
        if (TextUtils.isEmpty(cancelText)) {
            binding.viewLine.setVisibility(View.GONE);
            binding.tvDialogCancel.setVisibility(View.GONE);
        }
        //文案方向
        binding.tvDialogContainer.setGravity(center ? Gravity.CENTER : Gravity.LEFT);

        //对控件赋值
        binding.tvDialogTip.setText(TextUtils.isEmpty(tipText) ? "" : tipText);
        binding.tvDialogContainer.setText(TextUtils.isEmpty(contentText) ? "" : contentText);
        binding.tvDialogSure.setText(TextUtils.isEmpty(sureText) ? "" : sureText);
        binding.tvDialogCancel.setText(TextUtils.isEmpty(cancelText) ? "" : cancelText);

        //点击了取消按钮的回调
        binding.tvDialogCancel.setOnClickListener(v -> {
            dismiss();
            if (null != onDialogListener) {
                onDialogListener.onCancel();
            }
        });

        //点击了确定按钮的回调
        binding.tvDialogSure.setOnClickListener(v -> {
            dismiss();
            if (null != onDialogListener) {
                onDialogListener.onConfirm();
            }
        });
        return this;
    }

    public AppDialog setType() {
        getWindow().setType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        return this;
    }

    public AppDialog setOnDialogListener(OnDialogListener onDialogListener) {
        this.onDialogListener = onDialogListener;
        return this;
    }

    public static AppDialog with(Context context) {
        return new AppDialog(context);
    }

}