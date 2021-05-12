package com.dataqin.common.widget.dialog;

import android.content.Context;

import com.dataqin.common.base.BaseDialog;
import com.dataqin.common.databinding.ViewDialogAlphaBinding;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wangyanbin
 * 仿高德地图的等待dialog
 */
public class AlphaDialog extends BaseDialog<ViewDialogAlphaBinding> {

    public AlphaDialog(@NotNull Context context) {
        super(context);
        initialize();
    }

    public void show(boolean flag) {
        setCancelable(flag);
        if (!isShowing()) {
            show();
        }
    }

    public void hide() {
        if (isShowing()) {
            dismiss();
        }
    }

    public static AlphaDialog with(Context context) {
        return new AlphaDialog(context);
    }

}