package com.dataqin.share.model

import android.graphics.Bitmap
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import java.lang.ref.SoftReference

/**
 *  Created by wangyanbin
 *  微信分享类
 */
class WechatModel {
    var title: String? = null//分享标题
    var content: String? = null//分享内容
    var imgUrl: String? = null//分享图片链接
    var url: String? = null//分享地址
    var id: String? = null//小程序id
    var type: Int = SendMessageToWX.Req.WXSceneSession //SendMessageToWX.Req.WXSceneSession微信SendMessageToWX.Req.WXSceneTimeline微信朋友圈
    var bmp: SoftReference<Bitmap>? = null //分享图片

    constructor()

    /**
     * 分享链接
     */
    constructor(title: String, content: String, imgUrl: String, url: String, type: Int = SendMessageToWX.Req.WXSceneSession) {
        this.title = title
        this.content = content
        this.imgUrl = imgUrl
        this.url = url
        this.type = type
    }

    /**
     * 分享微信小程序
     */
    constructor(title: String, content: String, imgUrl: String, url: String, id: String, type: Int = SendMessageToWX.Req.WXSceneSession) {
        this.title = title
        this.content = content
        this.imgUrl = imgUrl
        this.url = url
        this.id = id
        this.type = type
    }

    /**
     * 分享图片
     */
    constructor(type: Int, bmp: Bitmap) {
        this.type = type
        this.bmp = SoftReference(bmp)
    }

}