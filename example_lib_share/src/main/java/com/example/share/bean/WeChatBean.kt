package com.example.share.bean

import android.graphics.Bitmap

import java.lang.ref.SoftReference

class WeChatBean {
    var title: String? = null //分享标题
    var content: String? = null //分享内容
    var imgUrl: String? = null //分享图片链接
    var url: String? = null //分享地址
    var id: String? = null //小程序id
    var type: Int = 0 //SendMessageToWX.Req.WXSceneSession微信SendMessageToWX.Req.WXSceneTimeline微信朋友圈
    var bmp: SoftReference<Bitmap>? = null //分享图片

    constructor()

    //分享链接(不指定)
    constructor(title: String?, content: String?, imgUrl: String?, url: String?) {
        this.title = title
        this.content = content
        this.imgUrl = imgUrl
        this.url = url
    }

    //分享链接（指定）
    constructor(title: String?, content: String?, imgUrl: String?, url: String?, type: Int) {
        this.title = title
        this.content = content
        this.imgUrl = imgUrl
        this.url = url
        this.type = type
    }

    //分享微信小程序(不指定)
    constructor(title: String?, content: String?, imgUrl: String?, url: String?, id: String?) {
        this.title = title
        this.content = content
        this.imgUrl = imgUrl
        this.url = url
        this.id = id
    }

    //分享微信小程序（指定）
    constructor(title: String?, content: String?, imgUrl: String?, url: String?, id: String?, type: Int) {
        this.title = title
        this.content = content
        this.imgUrl = imgUrl
        this.url = url
        this.id = id
        this.type = type
    }

    //分享图片
    constructor(type: Int, bmp: SoftReference<Bitmap>?) {
        this.type = type
        this.bmp = bmp
    }

}
