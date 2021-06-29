package com.dataqin.push.utils

import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.dataqin.common.BaseApplication
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Extras
import com.dataqin.common.utils.NotificationFactory
import com.dataqin.common.utils.analysis.GsonUtil
import com.dataqin.push.activity.PushActivity
import com.dataqin.push.model.PushModel

/**
 *  Created by wangyanbin
 *  构架推送工具类
 */
object PushHelper {
    private val context by lazy { BaseApplication.instance?.applicationContext }

    fun send(model: PushModel) {
        //找到合适条件的页面class后，创建推送
        val clazz = getAppPage(model)
        if (TextUtils.isEmpty(clazz)) return
        //如果得到的json返回类是属于普通消息，且APP处于开启状态，则直接点击后不跳转不做操作
        if (ARouterPath.StartActivity == clazz && isAppOnForeground()) {
            NotificationFactory.instance.normal(model.title!!, model.content!!, 0, 0)
        } else {
            val id = "xxxx"//服务器给的特定id
            NotificationFactory.instance.normal(
                model.title!!, model.content!!, 0, 0,
                Intent(context, PushActivity::class.java)
                    .putExtra(Extras.IS_RUNNING, isAppOnForeground())
                    .putExtra(Extras.PAYLOAD, model), id)
        }
    }

    fun getAppPage(model: PushModel): String? {
        var clazz: String? = null
        when (model.sendType) {
            "SCORE_FINE_IN", "SCORE_FINE_OUT" -> clazz = ARouterPath.StartActivity
            "DISOBEY_FINE_IN", "DISOBEY_FINE_OUT" -> clazz = ARouterPath.CameraActivity
        }
        return clazz
    }

    /**
     * 在进程中去寻找当前APP的信息，判断是否在运行
     * 100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
     */
    private fun isAppOnForeground(): Boolean {
        val runningTasks = (context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningTasks(100)
        val packageName = context?.packageName
        for (tasks in runningTasks) {
            if (tasks.topActivity?.packageName == packageName || tasks.baseActivity?.packageName == packageName) {
                return true
            }
        }
        return false
    }

}