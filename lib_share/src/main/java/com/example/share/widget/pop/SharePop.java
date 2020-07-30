package com.example.share.widget.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;

import com.example.common.widget.popupwindow.BasePopupWindow;
import com.example.share.R;
import com.example.share.databinding.ViewPopShareBinding;
import com.example.share.model.WeChatModel;
import com.example.share.utils.ShareSDKUtil;
import com.example.share.widget.pop.callback.OnSharePopListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;


/**
 * author: wyb
 * date: 2017/11/23.
 * 分享弹框
 */
@SuppressLint("InflateParams")
public class SharePop extends BasePopupWindow implements View.OnClickListener {
    private WeChatModel weChatModel;
    private OnSharePopListener onSharePopListener;

    public SharePop(Activity activity) {
        super(activity);
        createViewBinding(ViewPopShareBinding.inflate(getActivity().getLayoutInflater()));
        initialize();
    }

    private void initialize() {
        ViewPopShareBinding binding = getBinding();
        binding.llShareWechat.setOnClickListener(this);
        binding.llShareWechatMoments.setOnClickListener(this);
        binding.tvShareCancel.setOnClickListener(this);
        binding.rlShareContainer.setOnClickListener(this);
    }

    public void setWeChatModel(WeChatModel weChatModel) {
        this.weChatModel = weChatModel;
    }

    public void setOnSharePopListener(OnSharePopListener onSharePopListener) {
        this.onSharePopListener = onSharePopListener;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rl_share_container || viewId == R.id.tv_share_cancel) {
            if (null != onSharePopListener) {
                onSharePopListener.onShareCancel();
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
