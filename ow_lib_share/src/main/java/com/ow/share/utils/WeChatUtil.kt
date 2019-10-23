package com.ow.share.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Patterns
import com.ow.basemodule.constant.Constants
import com.ow.basemodule.utils.http.download.DownLoadUtil
import com.ow.basemodule.utils.http.download.callback.OnDownloadListener
import com.ow.framework.utils.ToastUtil
import com.ow.share.R
import com.ow.share.bean.WeChatBean
import com.ow.share.bean.WeChatMethod
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/**
 * 微信分享工具类
 */
class WeChatUtil(activity: Activity) {
    private var scene: Int = 0
    private var iwxapi: IWXAPI? = null
    private var shareIcon: SoftReference<Bitmap>? = null
    private var weChatMethod: WeChatMethod? = null
    private var weChatBean = WeChatBean()
    private val downLoadUtil: DownLoadUtil
    private val mActivity: WeakReference<Activity> = WeakReference(activity)

    init {
        downLoadUtil = DownLoadUtil(mActivity.get())
        regToWx()
    }

    //通过WXAPIFactory工厂，获取IWXAPI的实例,将应用的appId注册到微信
    private fun regToWx() {
        iwxapi = WXAPIFactory.createWXAPI(mActivity.get(), Constants.WX_APP_ID, true)
        iwxapi!!.registerApp(Constants.WX_APP_ID)
    }

    //检验是否安装微信
    private val isWXAppInstalled: Boolean
        get() {
            if (!iwxapi!!.isWXAppInstalled) {
                ToastUtil.mackToastLONG(mActivity.get()!!.getString(R.string.wechat_uninstalled_txt), mActivity.get()!!)
                return false
            } else {
                return true
            }
        }

    //通过设定的枚举类，走不同分享形式
    private fun share() {
        when (weChatMethod) {
            WeChatMethod.TEXT -> weChatTxt()
            WeChatMethod.IMAGE -> weChatBit()
            WeChatMethod.MUSIC -> weChatMusic()
            WeChatMethod.VIDEO -> weChatVideo()
            WeChatMethod.WEBPAGE -> weChatWeb()
            WeChatMethod.MINIPROGRAM -> if (SendMessageToWX.Req.WXSceneTimeline == scene) {
                ToastUtil.mackToastLONG(mActivity.get()!!.getString(R.string.wechat_share_failure_txt), mActivity.get()!!)
            } else {
                weChatMiniProgram()
            }
        }
    }

    /**
     * 分享网页到朋友圈或者好友，视频和音乐的分享和网页大同小异，只是创建的对象不同。
     * 详情参考官方文档：
     * https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317340&token=&lang=zh_CN
     */
    private fun weChatTxt() {
        val textObj = WXTextObject()
        textObj.text = weChatBean.shareText
        val msg = WXMediaMessage(textObj)
        sendReq(msg)
    }

    private fun weChatBit() {
        val imgObj = WXImageObject(weChatBean.bmp!!.get())
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj
        sendReq(msg)
    }

    private fun weChatMusic() {
        val musicObj = WXMusicObject()
        musicObj.musicUrl = weChatBean.shareUrl
        val msg = WXMediaMessage()
        msg.mediaObject = musicObj
        sendReq(msg)
    }

    private fun weChatVideo() {
        val videoObj = WXVideoObject()
        videoObj.videoUrl = weChatBean.shareUrl
        val msg = WXMediaMessage(videoObj)
        sendReq(msg)
    }

    private fun weChatWeb() {
        val webPageObj = WXWebpageObject()
        webPageObj.webpageUrl = weChatBean.shareUrl
        val msg = WXMediaMessage(webPageObj)
        sendReq(msg)
    }

    private fun weChatMiniProgram() {
        val wxMiniProgramObject = WXMiniProgramObject()
        wxMiniProgramObject.webpageUrl = weChatBean.shareUrl
        wxMiniProgramObject.userName = weChatBean.userName
        wxMiniProgramObject.path = weChatBean.path
        val msg = WXMediaMessage(wxMiniProgramObject)
        sendReq(msg)
    }

    private fun sendReq(msg: WXMediaMessage) {
        msg.title = weChatBean.title
        msg.description = weChatBean.describe
        msg.thumbData = Util.bmpToByteArray(shareIcon!!.get(), true)
        val req = SendMessageToWX.Req()
        req.transaction = System.currentTimeMillis().toString()
        req.message = msg
        req.scene = scene
        iwxapi!!.sendReq(req)
    }

    //设置分享内容(分享发生改变时需要重新赋值)
    fun setWeChatBean(weChatMethod: WeChatMethod, weChatBean: WeChatBean, scene: Int) {
        this.weChatMethod = weChatMethod
        this.weChatBean = weChatBean
        this.scene = scene
    }

    //直接设置左侧的分享图标（截屏分享时，可直接设置大图展示）
    fun setShareIcon(shareIcon: SoftReference<Bitmap>) {
        this.shareIcon = shareIcon
    }

    //调取分享
    fun toShare() {
        if (isWXAppInstalled) {
            if (TextUtils.isEmpty(weChatBean.bitUrl) || !Patterns.WEB_URL.matcher(weChatBean.bitUrl).matches()) {
                shareIcon = SoftReference(BitmapFactory.decodeResource(mActivity.get()!!.resources, R.mipmap.ic_share))
                share()
            } else {
                if (null != shareIcon) {
                    share()
                } else {
                    downLoadUtil.download(weChatBean.bitUrl, weChatBean.title, object : OnDownloadListener {
                        override fun onDownloadSuccess(path: String) {
                            shareIcon = SoftReference(BitmapFactory.decodeFile(path))
                        }

                        override fun onDownloadFailed(e: Throwable) {
                            shareIcon = SoftReference(BitmapFactory.decodeResource(mActivity.get()!!.resources, R.mipmap.ic_share))
                        }

                        override fun onDownloadFinish() {
                            share()
                        }
                    })
                }
            }
        }
    }

}