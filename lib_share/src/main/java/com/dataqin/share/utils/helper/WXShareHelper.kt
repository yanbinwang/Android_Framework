package com.dataqin.share.utils.helper

import android.app.Activity
import android.os.Looper
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.Platform.ShareParams
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.framework.ShareSDK
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.BaseApplication
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import com.dataqin.share.model.WechatModel
import com.dataqin.share.utils.helper.callback.OnWXAuthorizeListener
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import java.util.*

/**
 *  Created by wangyanbin
 *  微信分享工具类
 */
object WXShareHelper {
    private val context by lazy { BaseApplication.instance?.applicationContext }
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }

    /**
     * 分享链接
     */
    @JvmStatic
    fun shareWebPage(model: WechatModel) {
        val shareParams = ShareParams()
        shareParams.apply {
            title = model.title
            text = model.content
            imageUrl = model.imgUrl
            url = model.url
            shareType = Platform.SHARE_TEXT
        }
        val platform = ShareSDK.getPlatform(getPlatformType(model.type))
        platform.apply {
            platformActionListener = object : PlatformActionListener {
                override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
                    weakHandler.post { RxBus.instance.post(RxEvent(Constants.APP_SHARE_SUCCESS)) }
                }

                override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {
                    weakHandler.post { RxBus.instance.post(RxEvent(Constants.APP_SHARE_FAILURE)) }
                }

                override fun onCancel(p0: Platform?, p1: Int) {
                    weakHandler.post { RxBus.instance.post(RxEvent(Constants.APP_SHARE_CANCEL)) }
                }
            }
            share(shareParams)
        }
    }

    /**
     * 分享微信小程序
     */
    @JvmStatic
    fun shareMiniProgram(model: WechatModel) {
        OnekeyShare().apply {
            setPlatform(Wechat.NAME)
            disableSSOWhenAuthorize()
            setTitle(model.title)
            text = model.content
            setImageUrl(model.imgUrl)
            setUrl(model.url)
            shareContentCustomizeCallback = ShareContentCustomizeCallback { _: Platform?, shareParams: ShareParams ->
                shareParams.apply {
                    shareType = Platform.SHARE_WXMINIPROGRAM //分享小程序类型,修改为Platform.OPEN_WXMINIPROGRAM可直接打开微信小程序
                    wxUserName = model.id //配置小程序原始ID，前面有截图说明
                    wxPath = model.url //分享小程序页面的具体路径
                }
            }
            show(context)
        }
    }

    /**
     * 分享图片
     */
    @JvmStatic
    fun shareImage(model: WechatModel) {
        OnekeyShare().apply {
            setPlatform(getPlatformType(model.type))
            //关闭sso授权
            disableSSOWhenAuthorize()
            setImageData(model.bmp?.get())//确保SDcard下面存在此张图片
            //启动分享GUI
            show(context)
        }
    }

    /**
     * 微信授权登录
     */
    @JvmStatic
    fun authorize(activity: Activity, onWXAuthorizeListener: OnWXAuthorizeListener?) {
        ShareSDK.setActivity(activity)
        //回调信息，可以在这里获取基本的授权返回的信息，但是注意如果做提示和UI操作要传到主线程handler里去执行
        ShareSDK.getPlatform(Wechat.NAME).apply {
            platformActionListener = object : PlatformActionListener {
                override fun onComplete(platform: Platform, i: Int, hashMap: HashMap<String, Any>) {
                    weakHandler.post { onWXAuthorizeListener?.onComplete(hashMap) }
                }

                override fun onError(platform: Platform, i: Int, throwable: Throwable) {
                    weakHandler.post { onWXAuthorizeListener?.onError(throwable) }
                }

                override fun onCancel(platform: Platform, i: Int) {
                    weakHandler.post { onWXAuthorizeListener?.onCancel() }
                }
            }
            //要功能不要数据，在监听oncomplete中不会返回用户数据
            authorize()
        }
    }

    /**
     * 删除微信授权信息
     */
    @JvmStatic
    fun removeAccount(activity: Activity) {
        ShareSDK.setActivity(activity) //抖音登录适配安卓9.0
        val platform = ShareSDK.getPlatform(Wechat.NAME)
        if (platform.isAuthValid) platform.removeAccount(true) //执行此操作就可以移除掉本地授权状态和授权信息
    }

    /**
     * 获取分享方式
     */
    private fun getPlatformType(type: Int) = if (type == SendMessageToWX.Req.WXSceneTimeline) WechatMoments.NAME else Wechat.NAME

}