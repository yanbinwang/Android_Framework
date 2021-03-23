package com.dataqin.share.widget.popup;

import android.app.Activity;
import android.view.View;

import com.dataqin.common.base.BasePopupWindow;
import com.dataqin.share.R;
import com.dataqin.share.databinding.ViewPopupShareBinding;
import com.dataqin.share.model.WeChatModel;
import com.dataqin.share.utils.helper.ShareHelper;
import com.dataqin.share.widget.popup.callback.OnSharePopupClickListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wangyanbin
 * 分享
 */
public class SharePopup extends BasePopupWindow<ViewPopupShareBinding> implements View.OnClickListener {
    private WeChatModel weChatModel;
    private OnSharePopupClickListener onSharePopupClickListener;

    public SharePopup(@NotNull Activity activity) {
        super(activity);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        binding.llWechat.setOnClickListener(this);
        binding.llWechatMoments.setOnClickListener(this);
        binding.tvCancel.setOnClickListener(this);
        binding.rlContainer.setOnClickListener(this);
    }

    public void setWeChatModel(WeChatModel weChatModel) {
        this.weChatModel = weChatModel;
    }

    public void setOnSharePopupClickListener(OnSharePopupClickListener onSharePopupClickListener) {
        this.onSharePopupClickListener = onSharePopupClickListener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_container || v.getId() == R.id.tv_cancel) {
            if (null != onSharePopupClickListener) {
                onSharePopupClickListener.onShareCancel();
            }
            dismiss();
        } else if (v.getId() == R.id.ll_wechat) {
            weChatModel.setType(SendMessageToWX.Req.WXSceneSession);
            ShareHelper.shareWebPage(weChatModel);
            dismiss();
        } else if (v.getId() == R.id.ll_wechat_moments) {
            weChatModel.setType(SendMessageToWX.Req.WXSceneTimeline);
            ShareHelper.shareWebPage(weChatModel);
            dismiss();
        }
    }
}
