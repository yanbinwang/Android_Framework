package com.ow.share.widget.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.ow.share.R;
import com.ow.share.bean.WeChatBean;
import com.ow.share.bean.WeChatMethod;
import com.ow.share.utils.WeChatUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.lang.ref.WeakReference;


/**
 * author: wyb
 * date: 2017/11/23.
 * 分享弹框
 */
@SuppressLint("InflateParams")
public class SharePop implements View.OnClickListener {
    private WeakReference<Activity> mActivity;
    private WindowManager.LayoutParams layoutParams;
    private View popView;
    private PopupWindow sharePop;
    private WeChatBean weChatBean;
    private WeChatUtil weChatUtil;

    public SharePop(Activity activity) {
        super();
        mActivity = new WeakReference<>(activity);
        weChatUtil = new WeChatUtil(mActivity.get());
        layoutParams = mActivity.get().getWindow().getAttributes();
    }

    public void showPop(View view) {
        layoutParams.alpha = 0.7f;
        mActivity.get().getWindow().setAttributes(layoutParams);

        if (popView == null) {
            popView = LayoutInflater.from(mActivity.get()).inflate(R.layout.view_pop_share, null);
            RelativeLayout shareContainerRel = popView.findViewById(R.id.share_container_rel);
            LinearLayout shareWechatLin = popView.findViewById(R.id.share_wechat_lin);
            LinearLayout shareWechatMomentsLin = popView.findViewById(R.id.share_wechat_moments_lin);
            shareContainerRel.setOnClickListener(this);
            shareWechatLin.setOnClickListener(this);
            shareWechatMomentsLin.setOnClickListener(this);
        }

        if (sharePop == null) {
            sharePop = new PopupWindow();
            sharePop.setContentView(popView);
            sharePop.setFocusable(true);
            sharePop.setOutsideTouchable(true);
            sharePop.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Slide slideIn = new Slide();
                slideIn.setDuration(500);
                //设置为进入
                slideIn.setMode(Visibility.MODE_IN);
                //设置从底部进入
                slideIn.setSlideEdge(Gravity.BOTTOM);
                sharePop.setEnterTransition(slideIn);

                Slide slideOut = new Slide();
                slideOut.setDuration(500);
                //设置为退出
                slideOut.setMode(Visibility.MODE_OUT);
                //设置从底部退出
                slideOut.setSlideEdge(Gravity.BOTTOM);
                sharePop.setExitTransition(slideOut);
            } else {
                sharePop.setAnimationStyle(R.style.pushBottomAnimStyle);
            }
            sharePop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            sharePop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            sharePop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            sharePop.setOnDismissListener(() -> {
                layoutParams.alpha = 1f;
                mActivity.get().getWindow().setAttributes(layoutParams);
            });
        }

        sharePop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private void hidePop() {
        if (sharePop != null) {
            sharePop.dismiss();
        }
    }

    public void setWeChatBean(WeChatBean weChatBean) {
        this.weChatBean = weChatBean;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.share_container_rel) {
            hidePop();
        } else if (viewId == R.id.share_wechat_lin) {
            weChatUtil.setWeChatBean(WeChatMethod.WEBPAGE, weChatBean, SendMessageToWX.Req.WXSceneSession);
            weChatUtil.toShare();
            hidePop();
        } else if (viewId == R.id.share_wechat_moments_lin) {
            weChatUtil.setWeChatBean(WeChatMethod.WEBPAGE, weChatBean, SendMessageToWX.Req.WXSceneTimeline);
            weChatUtil.toShare();
            hidePop();
        }
    }

}
