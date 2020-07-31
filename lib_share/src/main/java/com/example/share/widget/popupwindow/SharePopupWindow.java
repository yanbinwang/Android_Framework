package com.example.share.widget.popupwindow;

import android.app.Activity;
import android.view.View;

import com.example.common.base.BasePopupWindow;
import com.example.share.R;
import com.example.share.databinding.ViewPopupShareBinding;
import com.example.share.model.WeChatModel;
import com.example.share.utils.ShareSDKUtil;
import com.example.share.widget.popupwindow.callback.OnShareListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;


/**
 * author: wyb
 * date: 2017/11/23.
 * 分享弹框
 */
public class SharePopupWindow extends BasePopupWindow<ViewPopupShareBinding> implements View.OnClickListener {
    private WeChatModel weChatModel;
    private OnShareListener onShareListener;

    public SharePopupWindow(Activity activity) {
        super(activity);
        initialize();
    }

    @Override
    protected void initialize() {
        super.initialize();
        binding.llShareWechat.setOnClickListener(this);
        binding.llShareWechatMoments.setOnClickListener(this);
        binding.tvShareCancel.setOnClickListener(this);
        binding.rlShareContainer.setOnClickListener(this);
    }

    public void setWeChatModel(WeChatModel weChatModel) {
        this.weChatModel = weChatModel;
    }

    public void setOnShareListener(OnShareListener onShareListener) {
        this.onShareListener = onShareListener;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rl_share_container || viewId == R.id.tv_share_cancel) {
            if (null != onShareListener) {
                onShareListener.onShareCancel();
            }
            dismiss();
        } else if (viewId == R.id.ll_share_wechat) {
            weChatModel.setType(SendMessageToWX.Req.WXSceneSession);
            ShareSDKUtil.getInstance().shareWebPage(weChatModel);
            dismiss();
        } else if (viewId == R.id.ll_share_wechat_moments) {
            weChatModel.setType(SendMessageToWX.Req.WXSceneTimeline);
            ShareSDKUtil.getInstance().shareWebPage(weChatModel);
            dismiss();
        }
    }

}
