package com.example.common.widget.dialog;

import android.content.Context;

import com.example.common.R;
import com.example.common.base.binding.BaseDialog;
import com.example.common.databinding.ViewDialogLoadingBinding;

/**
 * Created by wyb on 2017/6/28.
 * 加载动画view
 * https://blog.csdn.net/shulianghan/article/details/105066654
 */
public class LoadingDialog extends BaseDialog<ViewDialogLoadingBinding> {

    public LoadingDialog(Context context) {
        super(context, R.style.loadingStyle);
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

}