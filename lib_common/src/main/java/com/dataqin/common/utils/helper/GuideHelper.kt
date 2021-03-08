package com.dataqin.common.utils.helper

import android.app.Activity
import com.app.hubert.guide.NewbieGuide
import com.app.hubert.guide.model.GuidePage
import com.tencent.mmkv.MMKV
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  引导页遮罩helper
 */
object GuideHelper {
    private val mmkv by lazy { MMKV.defaultMMKV() }

    fun show(activity: Activity, label: String, vararg pages: GuidePage) {
        if (!mmkv.decodeBool(label, false)) {
            mmkv.encode(label, true)
            val weakActivity = WeakReference(activity)
            val builder = NewbieGuide.with(weakActivity.get())//传入activity
                .setLabel(label)//设置引导层标示，用于区分不同引导层，必传！否则报错
                .alwaysShow(true)
            for (page in pages) {
                builder.addGuidePage(page)
            }
            builder.show()
        }
    }

}