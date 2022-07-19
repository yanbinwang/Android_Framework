package com.dataqin.media.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants

/**
 *  Created by wangyanbin
 *  按键监听广播
 */
class KeyEventReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS -> {
                //Home键,菜单键
                when (intent.getStringExtra("reason")) {
//                    "homekey", "recentapps" -> RxBus.instance.post(RxEvent(Constants.APP_ACTION_CLOSE_SYSTEM_DIALOGS))
                }
            }
//            //电源键
//            Intent.ACTION_SCREEN_OFF, Intent.ACTION_SCREEN_ON ->
        }
    }
}