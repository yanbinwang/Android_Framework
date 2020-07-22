package com.example.share.utils;

import android.app.Activity;
import android.content.Context;

import com.example.common.BaseApplication;
import com.example.common.bus.RxBus;
import com.example.common.bus.RxBusEvent;
import com.example.common.constant.Constants;
import com.example.share.model.WeChatModel;
import com.example.share.utils.callback.OnShareListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;


public class ShareSDKUtil {
    private Context context;
    private static ShareSDKUtil shareUtil;

    private ShareSDKUtil() {
        this.context = BaseApplication.instance.getApplicationContext();
    }

    public static ShareSDKUtil getInstance() {
        if (shareUtil == null) {
            synchronized (ShareSDKUtil.class) {
                if (shareUtil == null) {
                    shareUtil = new ShareSDKUtil();
                }
            }
        }
        return shareUtil;
    }

    //分享链接
    public void shareWebPage(WeChatModel weChatModel) {
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setTitle(weChatModel.getTitle());
        shareParams.setText(weChatModel.getContent());
        shareParams.setImageUrl(weChatModel.getImgUrl());
        shareParams.setUrl(weChatModel.getUrl());
        shareParams.setShareType(Platform.SHARE_TEXT);

        Platform platform = ShareSDK.getPlatform(getPlatformType(weChatModel.getType()));
        platform.setPlatformActionListener(new PlatformActionListener() {
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                RxBus.getInstance().post(new RxBusEvent(Constants.APP_SHARE_FAILURE));
            }

            public void onComplete(Platform arg0, int arg1, HashMap arg2) {
                //分享成功的回调
                RxBus.getInstance().post(new RxBusEvent(Constants.APP_SHARE_SUCCESS));
            }

            public void onCancel(Platform arg0, int arg1) {
                //取消分享的回调
                RxBus.getInstance().post(new RxBusEvent(Constants.APP_SHARE_CANCEL));
            }
        });
        platform.share(shareParams);
    }

    //分享微信小程序
    public void shareMiniProgram(WeChatModel weChatModel) {
        OnekeyShare onekeyShare = new OnekeyShare();
        onekeyShare.setPlatform(Wechat.NAME);
        onekeyShare.disableSSOWhenAuthorize();
        onekeyShare.setTitle(weChatModel.getTitle());
        onekeyShare.setText(weChatModel.getContent());
        onekeyShare.setImageUrl(weChatModel.getImgUrl());
        onekeyShare.setUrl(weChatModel.getUrl());

        onekeyShare.setShareContentCustomizeCallback((platform, shareParams) -> {
            shareParams.setShareType(Platform.SHARE_WXMINIPROGRAM);//分享小程序类型,修改为Platform.OPEN_WXMINIPROGRAM可直接打开微信小程序
            shareParams.setWxUserName(weChatModel.getId());//配置小程序原始ID，前面有截图说明
            shareParams.setWxPath(weChatModel.getUrl());//分享小程序页面的具体路径
        });
        onekeyShare.show(context);
    }

    //分享图片
    public void shareImage(WeChatModel weChatModel) {
        OnekeyShare onekeyShare = new OnekeyShare();
        onekeyShare.setPlatform(getPlatformType(weChatModel.getType()));
        //关闭sso授权
        onekeyShare.disableSSOWhenAuthorize();
        onekeyShare.setImageData(weChatModel.getBmp().get());//确保SDcard下面存在此张图片
        // 启动分享GUI
        onekeyShare.show(context);
    }

    //微信授权登录
    public void authorize(Activity activity, OnShareListener callback) {
        WeakReference<Activity> mActivity = new WeakReference<>(activity);
        Platform platform = ShareSDK.getPlatform(Wechat.NAME);
        ShareSDK.setActivity(mActivity.get());
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                mActivity.get().runOnUiThread(() -> callback.onAuthorizeSuccess());
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
//                mActivity.get().runOnUiThread(() -> ToastUtil.showToast("授权失败,请确认手机是否安装了微信"));
                throwable.printStackTrace();
            }

            @Override
            public void onCancel(Platform platform, int i) {
//                mActivity.get().runOnUiThread(() -> ToastUtil.showToast("取消授权"));
            }
        });
        //authorize
        platform.authorize();//要功能不要数据，在监听oncomplete中不会返回用户数据
    }

    //删除微信授权信息
    public void removeAccount(Activity activity) {
        WeakReference<Activity> mActivity = new WeakReference<>(activity);
        Platform platform = ShareSDK.getPlatform(Wechat.NAME);
        ShareSDK.setActivity(mActivity.get());//抖音登录适配安卓9.0
        if (platform.isAuthValid()) {
            platform.removeAccount(true);//执行此操作就可以移除掉本地授权状态和授权信息
        }
    }

    //获取分享方式
    private String getPlatformType(int type){
        return type == SendMessageToWX.Req.WXSceneTimeline ? WechatMoments.NAME : Wechat.NAME;
    }

}
