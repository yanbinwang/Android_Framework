package com.dataqin.common.utils.builder

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dataqin.common.constant.Constants
import java.lang.ref.WeakReference

/**
 * author:wyb
 * 通知工具类
 */
class NotificationBuilder(activity: Activity) {
    private val weakActivity by lazy { WeakReference(activity) }
    private val notificationManager by lazy { weakActivity.get()?.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as NotificationManager }
    private var builder: NotificationCompat.Builder? = null

    /**
     * 构建通知栏
     */
    fun createNotification(id: Int, smallIcon: Int, largeIcon: Bitmap, title: String, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.PUSH_CHANNEL_ID,
                Constants.PUSH_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(false)
            channel.enableLights(false)
            notificationManager.createNotificationChannel(channel)
        }
        builder = NotificationCompat.Builder(weakActivity.get()!!, Constants.PUSH_CHANNEL_ID)
        builder?.apply {
            setContentTitle(title)
            setContentText(text)
            setSmallIcon(smallIcon)
            setLargeIcon(largeIcon)
            setOngoing(true)
            setAutoCancel(true)
            setWhen(System.currentTimeMillis())
        }
        notificationManager.notify(id, builder?.build())
    }

    /**
     * 构建通知栏跳转
     */
    fun setPendingIntent(id: Int, title: String, text: String, setupApkIntent: Intent) {
        val contentIntent = PendingIntent.getActivity(
            weakActivity.get(),
            0,
            setupApkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder?.apply {
            setContentIntent(contentIntent)
            setContentTitle(title)
            setContentText(text)
            setProgress(0, 0, false)
            setDefaults(Notification.DEFAULT_ALL)
        }
        val notification = builder?.build()
        notification?.flags = Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(id, notification)
    }

    /**
     * 构建进度条
     */
    fun setProgress(id: Int, progress: Int, title: String, text: String) {
        builder?.apply {
            setContentTitle(title)
            setContentText(text)
            setProgress(100, progress, false)
            setWhen(System.currentTimeMillis())
        }
        val notification = builder?.build()
        notification?.flags = Notification.FLAG_AUTO_CANCEL or Notification.FLAG_ONLY_ALERT_ONCE
        notificationManager.notify(id, notification)
    }

    /**
     * 取消构建
     */
    fun setCancel(id: Int, title: String, text: String) {
        builder?.apply {
            setContentTitle(title)
            setContentText(text)
        }
        val notification = builder?.build()
        notification?.flags = Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(id, notification)
    }

    /**
     * 判断当前是否开启通知，方便用户接受推送消息
     */
    fun isNotificationEnabled(): Boolean {
        return try {
            NotificationManagerCompat.from(weakActivity.get()!!).areNotificationsEnabled()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 跳转通知的设置界面
     */
    fun settingNotification() {
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
                intent.putExtra("app_uid", weakActivity.get()?.applicationInfo?.uid)
            }
            //其他
            else -> {
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", Constants.APPLICATION_ID, null)
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        weakActivity.get()?.startActivity(intent)
    }

}