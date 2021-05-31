package com.dataqin.common.widget.advertising.callback

import android.widget.LinearLayout

/**
 *  Created by wangyanbin
 *  广告抽象类
 */
interface AdGalleryImpl {

    /**
     * @param uris 图片的网络路径数组 ,为空时 加载 adsId
     * @param adsId 图片组资源ID ,测试用
     * @param switchTime 图片切换时间 写0 为不自动切换
     * @param ovalLayout 圆点容器 ,可为空
     * @param focusedId 圆点选中时的背景ID,圆点容器可为空写0
     * @param normalId 圆点正常时的背景ID,圆点容器为空写0
     */
    fun start(uris: List<String>, adsId: IntArray, switchTime: Int, ovalLayout: LinearLayout, focusedId: Int, normalId: Int)

    /**
     * 设置监听
     */
    fun setAdOnItemClickListener(onAdGalleryItemClickListener:OnAdGalleryItemClickListener)

}