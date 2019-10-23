package com.ow.share.bean

import android.graphics.Bitmap

import java.lang.ref.SoftReference

class WeChatBean {
    var bitUrl: String? = null //图片链接(截屏分享需要额外赋值)
    var title: String? = null //标题
    var describe: String? = null //简介
    var shareUrl: String? = null //链接地址(分享音乐url,分享视频url,分享网页url)
    var shareText: String? = null //分享文字文案
    var userName: String? = null //小程序端提供参数
    var path: String? = null //小程序端提供参数
    var bmp: SoftReference<Bitmap>? = null //分享图片

    constructor()

    constructor(bitUrl: String, title: String, describe: String, shareUrl: String, bmp: SoftReference<Bitmap>) {
        this.bitUrl = bitUrl
        this.title = title
        this.describe = describe
        this.shareUrl = shareUrl
        this.bmp = bmp
    }

    constructor(bitUrl: String, title: String, describe: String, shareUrl: String, shareText: String, bmp: SoftReference<Bitmap>) {
        this.bitUrl = bitUrl
        this.title = title
        this.describe = describe
        this.shareUrl = shareUrl
        this.shareText = shareText
        this.bmp = bmp
    }

    constructor(bitUrl: String, title: String, describe: String, shareUrl: String, shareText: String, userName: String, path: String, bmp: SoftReference<Bitmap>) {
        this.bitUrl = bitUrl
        this.title = title
        this.describe = describe
        this.shareUrl = shareUrl
        this.shareText = shareText
        this.userName = userName
        this.path = path
        this.bmp = bmp
    }

}
