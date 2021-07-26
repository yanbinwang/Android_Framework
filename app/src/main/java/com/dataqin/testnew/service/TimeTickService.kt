package com.dataqin.testnew.service

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.dataqin.base.utils.DateUtil
import com.dataqin.base.utils.TimerHelper
import com.dataqin.testnew.R

/**
 *  Created by wangyanbin
 *  定时器测试
 */
class TimeTickService : Service() {
    private var timerCount :Long = 0
    private var tvTimer: TextView? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startForeground(1, Notification())
        } else {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    packageName,
                    packageName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
            val builder = NotificationCompat.Builder(this, packageName)
            startForeground(1, builder.build())
        }
        stopForeground(true)//关闭录屏的图标
        //设置弹框
        if (alertDialog == null) {
            val view = View.inflate(this, R.layout.view_time_tick, null)
            tvTimer = view.findViewById(R.id.tv_timer)
            view?.layoutParams?.width = tvTimer?.width
            val builder = AlertDialog.Builder(this, R.style.dialogStyle)
            builder.setView(view)
            alertDialog = builder.create()
            alertDialog?.window?.setType(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            alertDialog?.window?.decorView?.setPadding(0, 0, 0, 0);
            alertDialog?.window?.decorView?.background = ColorDrawable(Color.TRANSPARENT)
            alertDialog?.setCancelable(false)
            view?.post {
                val params = alertDialog?.window?.attributes
                params?.gravity = Gravity.TOP
                params?.verticalMargin = 0f
                params?.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                params?.height = view.measuredHeight
                alertDialog?.window?.attributes = params
            }
        }
        alertDialog?.show()

        TimerHelper.startTask(object : TimerHelper.OnTaskListener {
            override fun run() {
                timerCount++
                tvTimer?.text = DateUtil.getSecondFormat(timerCount)
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
    }

}