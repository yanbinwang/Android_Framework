package com.example.share.utils

import android.app.Activity
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.Platform.ShareParams
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.example.common.BaseApplication
import com.example.common.bus.RxBus
import com.example.common.bus.RxBusEvent
import com.example.common.constant.Constants
import com.example.share.model.WeChatModel
import com.example.share.utils.callback.OnShareListener
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by WangYanBin on 2020/8/14.
 */
class ShareSDKUtil private constructor() {
    private val context = BaseApplication.instance.applicationContext

    companion object {
        @JvmStatic
        val instance by lazy {
            ShareSDKUtil()
        }
    }

    //分享链接
    fun shareWebPage(weChatModel: WeChatModel) {
        val shareParams = ShareParams()
        shareParams.title = weChatModel.title
        shareParams.text = weChatModel.content
        shareParams.imageUrl = weChatModel.imgUrl
        shareParams.url = weChatModel.url
        shareParams.shareType = Platform.SHARE_TEXT

        val platform = ShareSDK.getPlatform(getPlatformType(weChatModel.type))
        platform.platformActionListener = object : PlatformActionListener {

            override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
                //分享成功的回调
                RxBus.instance.post(RxBusEvent(Constants.APP_SHARE_SUCCESS))
            }

            override fun onCancel(p0: Platform?, p1: Int) {
                //取消分享的回调
                RxBus.instance.post(RxBusEvent(Constants.APP_SHARE_CANCEL))
            }

            override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
                //失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
                RxBus.instance.post(RxBusEvent(Constants.APP_SHARE_FAILURE))
            }
        }
        platform.share(shareParams)
    }

    //分享微信小程序
    fun shareMiniProgram(weChatModel: WeChatModel) {
        val onekeyShare = OnekeyShare()
        onekeyShare.setPlatform(Wechat.NAME)
        onekeyShare.disableSSOWhenAuthorize()
        onekeyShare.setTitle(weChatModel.title)
        onekeyShare.text = weChatModel.content
        onekeyShare.setImageUrl(weChatModel.imgUrl)
        onekeyShare.setUrl(weChatModel.url)

        onekeyShare.shareContentCustomizeCallback =
            ShareContentCustomizeCallback { _, shareParams ->
                shareParams?.shareType =
                    Platform.SHARE_WXMINIPROGRAM //分享小程序类型,修改为Platform.OPEN_WXMINIPROGRAM可直接打开微信小程序
                shareParams?.wxUserName = weChatModel.id //配置小程序原始ID，前面有截图说明
                shareParams?.wxPath = weChatModel.url //分享小程序页面的具体路径
            }
        onekeyShare.show(context)
    }

    //分享图片
    fun shareImage(weChatModel: WeChatModel) {
        val onekeyShare = OnekeyShare()
        onekeyShare.setPlatform(getPlatformType(weChatModel.type))
        //关闭sso授权
        onekeyShare.disableSSOWhenAuthorize()
        onekeyShare.setImageData(weChatModel.bmp?.get()) //确保SDcard下面存在此张图片
        // 启动分享GUI
        onekeyShare.show(context)
    }

    //微信授权登录
    fun authorize(activity: Activity, callback: OnShareListener) {
        val weakActivity = WeakReference(activity)
        val platform = ShareSDK.getPlatform(Wechat.NAME)
        ShareSDK.setActivity(weakActivity.get())
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        platform.platformActionListener = object : PlatformActionListener {

            override fun onComplete(Platform: Platform?, i: Int, hashMap: HashMap<String, Any>?) {
                weakActivity.get()?.runOnUiThread(Runnable { callback.onAuthorizeSuccess() })
            }

            override fun onCancel(Platform: Platform?, i: Int) {
//                mActivity.get().runOnUiThread(() -> ToastUtil.showToast("取消授权"));
            }

            override fun onError(Platform: Platform?, i: Int, throwable: Throwable?) {
//                mActivity.get().runOnUiThread(() -> ToastUtil.showToast("授权失败,请确认手机是否安装了微信"));
                throwable?.printStackTrace()
            }
        }
        //authorize
        platform.authorize() //要功能不要数据，在监听oncomplete中不会返回用户数据
    }

    //删除微信授权信息
    fun removeAccount(activity: Activity) {
        val weakActivity = WeakReference(activity)
        val platform = ShareSDK.getPlatform(Wechat.NAME)
        ShareSDK.setActivity(weakActivity.get()) //抖音登录适配安卓9.0
        if (platform.isAuthValid) {
            platform.removeAccount(true) //执行此操作就可以移除掉本地授权状态和授权信息
        }
    }

    private fun getPlatformType(type: Int): String {
        return if (type == SendMessageToWX.Req.WXSceneTimeline) WechatMoments.NAME else Wechat.NAME
    }

}