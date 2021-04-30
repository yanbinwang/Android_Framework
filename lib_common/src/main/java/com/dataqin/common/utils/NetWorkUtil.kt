package com.dataqin.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.dataqin.common.BaseApplication

/**
 * author: wyb
 * date: 2018/8/13.
 * 网路监测类
 */
@SuppressLint("StaticFieldLeak", "MissingPermission")
object NetWorkUtil {
    private val context by lazy { BaseApplication.instance?.applicationContext!! }
    private val connectivityManager by lazy { context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    /**
     * 验证是否联网
     */
    @JvmStatic
    fun isNetworkAvailable(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            return networkInfo.state == NetworkInfo.State.CONNECTED
        }
        return false
    }

    /**
     * 无线网络=1 移动网络=0 没有连接网络=-1
     */
    @JvmStatic
    fun getNetWorkState(): Int {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                return 1
            } else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                return 0
            }
        } else return -1
        return -1
    }

    /**
     * 判断当前网络环境是否为wifi
     */
    @JvmStatic
    fun isWifi() = connectivityManager.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI

}