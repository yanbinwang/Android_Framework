package com.dataqin.share.utils.helper

import android.app.Activity
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.Platform.ShareParams
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.dataqin.base.utils.ToastUtil
import com.dataqin.common.BaseApplication
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import com.dataqin.share.model.WeChatModel
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import java.lang.ref.WeakReference
import java.util.*

/**
 *  Created by wangyanbin
 *  微信分享工具类
 */
object WechatShareHelper {
    private val context by lazy { BaseApplication.instance?.applicationContext }

    /**
     * 分享链接
     */
    @JvmStatic
    fun shareWebPage(model: WeChatModel) {
        val shareParams = ShareParams()
        shareParams.title = model.title
        shareParams.text = model.content
        shareParams.imageUrl = model.imgUrl
        shareParams.url = model.url
        shareParams.shareType = Platform.SHARE_TEXT
        val platform = ShareSDK.getPlatform(getPlatformType(model.type))
        platform.platformActionListener = object : PlatformActionListener {
            override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
                RxBus.instance.post(RxEvent(Constants.APP_SHARE_SUCCESS))
            }

            override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
                RxBus.instance.post(RxEvent(Constants.APP_SHARE_FAILURE))
            }

            override fun onCancel(p0: Platform?, p1: Int) {
                RxBus.instance.post(RxEvent(Constants.APP_SHARE_CANCEL))
            }
        }
        platform.share(shareParams)
    }

    /**
     * 分享微信小程序
     */
    @JvmStatic
    fun shareMiniProgram(model: WeChatModel) {
        val onekeyShare = OnekeyShare()
        onekeyShare.setPlatform(Wechat.NAME)
        onekeyShare.disableSSOWhenAuthorize()
        onekeyShare.setTitle(model.title)
        onekeyShare.text = model.content
        onekeyShare.setImageUrl(model.imgUrl)
        onekeyShare.setUrl(model.url)
        onekeyShare.shareContentCustomizeCallback = ShareContentCustomizeCallback { _: Platform?, shareParams: ShareParams ->
                shareParams.shareType = Platform.SHARE_WXMINIPROGRAM //分享小程序类型,修改为Platform.OPEN_WXMINIPROGRAM可直接打开微信小程序
                shareParams.wxUserName = model.id //配置小程序原始ID，前面有截图说明
                shareParams.wxPath = model.url //分享小程序页面的具体路径
            }
        onekeyShare.show(context)
    }

    /**
     * 分享图片
     */
    @JvmStatic
    fun shareImage(model: WeChatModel) {
        val onekeyShare = OnekeyShare()
        onekeyShare.setPlatform(getPlatformType(model.type))
        //关闭sso授权
        onekeyShare.disableSSOWhenAuthorize()
        onekeyShare.setImageData(model.bmp?.get())//确保SDcard下面存在此张图片
        //启动分享GUI
        onekeyShare.show(context)
    }

    /**
     * 微信授权登录
     */
    @JvmStatic
    fun authorize(activity: Activity) {
        val weakActivity = WeakReference(activity)
        ShareSDK.setActivity(weakActivity.get())
        val platform = ShareSDK.getPlatform(Wechat.NAME)
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        platform.platformActionListener = object : PlatformActionListener {
            override fun onComplete(platform: Platform, i: Int, hashMap: HashMap<String, Any>) {
                weakActivity.get()?.runOnUiThread { ToastUtil.mackToastSHORT("授权成功", context!!) }
            }

            override fun onError(platform: Platform, i: Int, throwable: Throwable) {
                weakActivity.get()?.runOnUiThread { ToastUtil.mackToastSHORT("授权失败,请确认手机是否安装了微信", context!!) }
            }

            override fun onCancel(platform: Platform, i: Int) {
                weakActivity.get()?.runOnUiThread { ToastUtil.mackToastSHORT("取消授权", context!!) }
            }
        }
        //authorize
        platform.authorize() //要功能不要数据，在监听oncomplete中不会返回用户数据
    }

    /**
     * 删除微信授权信息
     */
    @JvmStatic
    fun removeAccount(activity: Activity) {
        val weakActivity = WeakReference(activity)
        ShareSDK.setActivity(weakActivity.get()) //抖音登录适配安卓9.0
        val platform = ShareSDK.getPlatform(Wechat.NAME)
        if (platform.isAuthValid) {
            platform.removeAccount(true) //执行此操作就可以移除掉本地授权状态和授权信息
        }
    }

    //获取分享方式
    private fun getPlatformType(type: Int): String? {
        return if (type == SendMessageToWX.Req.WXSceneTimeline) WechatMoments.NAME else Wechat.NAME
    }

}