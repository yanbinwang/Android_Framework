package com.dataqin.testnew.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.dataqin.common.constant.Constants
import com.dataqin.common.widget.dialog.AppDialog
import com.dataqin.common.widget.dialog.callback.OnDialogListener
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap


/**
 *  Created by wangyanbin
 *  针对首页弹框的帮助类，如果后台不能给弹框优先级顺序的记录
 *  1.更新弹框高于一切弹框，只要弹出更新，其余弹框不会弹出
 *  2.次于更新的弹框为通知是否开启，如果app接了推送，用户需要在弹框关闭后，逐步调取接下来的弹框
 *  如果不需要更新，调取
 */
object PopupHelper {
    private var show = false//是否已经弹过
    private var weakActivity: WeakReference<Activity>? = null
    private val popupMap by lazy { ConcurrentHashMap<String, Any>() }//通知管理类，key-通知说明，value-通知对象

    //一些配置通知的label屬性集合
    private val labelList = arrayOf(
        "update_label",//更新
        "push_label",//推送
        "advertisement_label",//广告
        "advertisement_label2"//广告2
    )

    @JvmStatic
    fun initialize(activity: Activity) {
        this.show = false
        this.weakActivity = WeakReference(activity)
        this.popupMap.clear()
        this.popupMap[labelList[1]] = Any()//1留给推送
    }

    /**
     * 添加一个通知对象到指定集合
     * label-通知类型说明
     * any-通知对象
     */
    @JvmStatic
    fun addPopup(index: Int, any: Any) {
        popupMap[labelList[index]] = any
        showPopup()
    }

    /**
     * 逐步弹出窗口
     */
    private fun showPopup() {
        if (!show) {
            //检测当前的通知集合是否已经达到了配置的通知总数
            if (popupMap.size >= labelList.size) {
                show = true
                showUpdate()
            }
        }
    }

    /**
     * 更新
     */
    private fun showUpdate() {
        val model = popupMap[labelList[0]]
        if (model is Any) {
            showNotification()
        } else {
//            var versionModel = model as VersionModel
        }
    }

    /**
     * 通知
     */
    private fun showNotification() {
        if (!isNotificationEnabled()) {
            AppDialog.with(weakActivity?.get()).setOnDialogListener(object : OnDialogListener {
                override fun onDialogConfirm() {
                    settingNotification()
                    showAdvertisement()
                }

                override fun onDialogCancel() {
                    showAdvertisement()
                }
            }).setParams("提示", "是否开启推送通知", "确定", "取消").show()
        } else {
            showAdvertisement()
        }
    }

    /**
     * 广告
     */
    private fun showAdvertisement() {

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
                intent.putExtra("android.provider.extra.APP_PACKAGE", Constants.APPLICATION_ID)
            }
            //5.0-7.0
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", Constants.APPLICATION_ID)
                intent.putExtra("app_uid", weakActivity?.get()?.applicationInfo?.uid)
            }
            //其他
            else -> {
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", Constants.APPLICATION_ID, null)
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        weakActivity?.get()?.startActivity(intent)
    }

}