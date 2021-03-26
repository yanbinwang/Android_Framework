package com.dataqin.testnew.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap


/**
 *  Created by wangyanbin
 *  针对首页弹框的帮助类，如果后台不能给弹框优先级顺序的记录
 *  1.更新弹框高于一切弹框，只要弹出更新，其余弹框不会弹出
 *  2.次于更新的弹框为通知是否开启，如果app接了推送，用户需要在弹框关闭后，逐步调取接下来的弹框
 */
object PopupHelper {
    private val popupMap by lazy { ConcurrentHashMap<String, Any>() }//通知管理类，key-通知说明，value-通知对象
    private var popupNum = 0//通知数量
    private var show = false//是否已经弹过
    private var weakActivity: WeakReference<Activity>? = null

    @JvmStatic
    fun initialize(activity: Activity, popupNum: Int = 0) {
        this.popupMap.clear()
        this.show = false
        this.weakActivity = WeakReference(activity)
        this.popupNum = popupNum
    }

    /**
     * 添加一个通知对象到指定集合
     * label-通知类型说明
     * any-通知对象
     */
    private fun addPopup(label: String, any: Any) {
        popupMap[label] = any
        showPopup()
    }

    /**
     * 逐步弹出窗口
     */
    private fun showPopup() {
        if(!show){
            //如果此时添加的对象已经达到了对应通知的数量，按照添加的顺序逐个展示对应弹框-每个弹框一个方法
            if (testingPopup()) {
                show = true
            }
        }
    }

    /**
     * 检测当前的通知集合是否已经达到了配置的通知总数
     */
    private fun testingPopup(): Boolean {
        return popupMap.size >= popupNum
    }

    /**
     * 判断当前是否开启通知，方便用户接受推送消息
     */
    private fun isNotificationEnabled(): Boolean {
        return try {
            NotificationManagerCompat.from(weakActivity?.get()!!).areNotificationsEnabled()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 跳转通知的设置界面
     */
    private fun settingNotification() {
        val intent = Intent()
        when {
            //8.0+
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("android.provider.extra.APP_PACKAGE", weakActivity?.get()?.packageName)
            }
            //5.0-7.0
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", weakActivity?.get()?.packageName)
                intent.putExtra("app_uid", weakActivity?.get()?.applicationInfo?.uid)
            }
            //其他
            else -> {
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", weakActivity?.get()?.packageName, null)
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        weakActivity?.get()?.startActivity(intent)
    }

}