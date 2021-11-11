package com.dataqin.testnew.widget.popup;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.dataqin.common.base.BasePopupWindow;
import com.dataqin.testnew.R;
import com.dataqin.testnew.databinding.ViewPopupEditBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by wangyanbin
 */
public class EditPopup extends BasePopupWindow<ViewPopupEditBinding> implements View.OnClickListener {
    private InputMethodManager manager;

    public EditPopup(@NotNull Activity activity) {
        super(activity, true);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        setTransition(false);
        manager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        binding.tvSend.setOnClickListener(this);
        binding.rlContainer.setOnClickListener(this);
    }

    public boolean showPopup(View view) {
        binding.llContainer.setVisibility(View.VISIBLE);
        manager.hideSoftInputFromWindow(binding.etContainer.getWindowToken(), 0);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
        manager.showSoftInput(binding.etContainer, 2);

        binding.etContainer.setFocusable(true);
        binding.etContainer.setFocusableInTouchMode(true);
        binding.etContainer.requestFocus();
        binding.etContainer.findFocus();

        showAtLocation(view, Gravity.BOTTOM, 0, 0);
        return true;
    }

    @Override
    public void dismiss() {
        binding.llContainer.setVisibility(View.GONE);
        super.dismiss();
        manager.hideSoftInputFromWindow(binding.etContainer.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_container) {
            dismiss();
        } else if (v.getId() == R.id.tv_send) {
            dismiss();
        }
    }

}