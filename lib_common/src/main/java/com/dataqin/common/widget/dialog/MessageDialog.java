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

    public MessageDialog setParams() {
        setParams("正在加载，请稍后......");
        return this;
    }

    public MessageDialog setParams(String message) {
        binding.tvLabel.setText(message);
        return this;
    }

    public static MessageDialog with(Context context) {
        return new MessageDialog(context);
    }

}