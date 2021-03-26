package com.dataqin.map.service

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.core.app.ActivityCompat
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
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
class MapReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //如果网络状态发生变化则需要重新定位-具备权限才会发送对应广播
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action) {
            val netWorkState = NetWorkUtil.getNetWorkState()
            if (-1 != netWorkState) {
                if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission_group.LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    RxBus.instance.post(RxEvent(Constants.APP_MAP_CONNECTIVITY))
                }
            }
        }
    }
}