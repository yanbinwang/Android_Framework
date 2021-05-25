package com.dataqin.media.utils

/**
 *  Created by wangyanbin
 *  安卓10录屏尺寸工具类
 *  部分手机对屏幕大小随意修改，如通过代码获取屏幕宽高，传入录屏类中会报错
 *  针对此类问题，在录屏时对屏幕的宽高，录屏的画布，屏幕分辨率等都做一定的换算修正
 *  更改为谷歌官方公布的比例
 */
object MobileSizeUtil {

    @JvmStatic
    fun getWidth(width: Int): Int {
        if (width <= 240) return 240
        if (width in 241..320) return 320
        if (width in 321..480) return 480
        if (width in 481..720) return 720
        if (width in 721..1080) return 1080
        if (width in 1081..3840) return 3840
        return 3840
    }

    @JvmStatic
    fun getHeight(height: Int): Int {
        if (height <= 320) return 320
        if (height in 321..480) return 480
        if (height in 481..800) return 800
        if (height in 801..1280) return 1280
        if (height in 1281..1920) return 1920
        if (height in 1921..2160) return 2160
        return 2160
    }

}