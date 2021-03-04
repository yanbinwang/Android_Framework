package com.dataqin.common.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.dataqin.common.R;
import com.dataqin.common.base.BaseDialog;
import com.dataqin.common.constant.Constants;
import com.dataqin.common.databinding.ViewDialogHighLightBinding;

import java.util.List;

/**
 * Created by wangyanbin
 * 直接使用dialog盖在页面上，实现引导遮罩，传入绘制的view的layout动态添加
 */
public class HighLightDialog extends BaseDialog<ViewDialogHighLightBinding> {

    public HighLightDialog(Context context) {
        super(context, R.style.dialogStyle);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.rlContainer.getLayoutParams();
        layoutParams.setMargins(0, Constants.STATUS_BAR_HEIGHT, 0, 0);
        binding.rlContainer.setLayoutParams(layoutParams);
    }

    //正着来，倒着加，点击隐藏
    public void setParams(List<Integer> ids) {
        for (int i = ids.size() - 1; i >= 0; i--) {
            View view = LayoutInflater.from(getContext()).inflate(ids.get(i), null);
            view.setOnClickListener(v -> view.setVisibility(View.GONE));
            binding.rlContainer.addView(view);
        }
    }

    public void show() {
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