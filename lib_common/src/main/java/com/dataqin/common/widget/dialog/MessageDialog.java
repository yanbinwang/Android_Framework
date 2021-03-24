package com.dataqin.common.widget.dialog;

import android.content.Context;

import com.dataqin.common.base.BaseDialog;
import com.dataqin.common.databinding.ViewDialogMessageBinding;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wangyanbin
 * 仿高德地图的等待dialog
 */
public class MessageDialog extends BaseDialog<ViewDialogMessageBinding> {

    public MessageDialog(@NotNull Context context) {
        super(context);
        initialize();
    }

    public void show() {
        show("正在加载，请稍后......");
    }

    public void show(String message) {
        binding.tvLabel.setText(message);
        setCancelable(false);
        if (!isShowing()) {
            show();
        }
    }

    public void hide() {
        if (isShowing()) {
            dismiss();
        }
    }

}