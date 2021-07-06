package com.dataqin.testnew.widget.advertising.callback

import android.widget.LinearLayout

/**
 *  Created by wangyanbin
 *  广告抽象类
 */
interface AdvertisingImpl {

    /**
     * @param uriList 图片的网络路径数组 ,为空时 加载 adsId
     * @param ovalLayout 圆点容器 ,可为空
     * @param margin 左右边距，可为空写0
     * @param focusedId 圆点选中时的背景ID,圆点容器可为空写0
     * @param normalId 圆点正常时的背景ID,圆点容器为空写0
     * @param switchTime 图片切换时间 写0 为不自动切换
     */
    fun start(uriList: List<String>, ovalLayout: LinearLayout? = null, margin: Int, focusedId: Int, normalId: Int, switchTime: Int)

    /**
     * 设置监听
     */
    fun setOnAdvertisingItemClickListener(onAdvertisingItemClickListener: OnAdvertisingItemClickListener)

}