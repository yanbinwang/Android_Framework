package com.dataqin.map.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.dataqin.common.utils.NetWorkUtil

/**
 *  Created by wangyanbin
 *  地图网络改变监听广播
 *  private val aMapReceiver by lazy { AMapReceiver() }
 *
 *  val intentFilter = IntentFilter()
 *  intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
 *  registerReceiver(aMapReceiver, intentFilter)
 *
 *  unregisterReceiver(aMapReceiver)
 */
class AMapReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        //如果网络状态发生变化则需要重新定位
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action) {
            val netWorkState: Int = NetWorkUtil.getNetWorkState()
            if (-1 != netWorkState) {
                //广播
            }
        }
    }
}