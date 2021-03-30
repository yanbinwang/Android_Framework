package com.dataqin.share.utils.helper.callback

import java.util.HashMap

/**
 *  Created by wangyanbin
 *  微信授权监听
 */
interface OnWXAuthorizeListener {

    fun onComplete(hashMap: HashMap<String, Any>)//授权成功

    fun onError(throwable: Throwable)//授权失败,请确认手机是否安装了微信

    fun onCancel()//取消授权

}