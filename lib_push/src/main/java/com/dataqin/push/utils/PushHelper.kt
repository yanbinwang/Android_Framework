package com.dataqin.push.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.dataqin.common.BaseApplication
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Extras
import com.dataqin.push.R
import com.dataqin.push.activity.PushActivity
import com.dataqin.push.model.PayLoad

/**
 *  Created by wangyanbin
 *  构架推送工具类
 */
object PushHelper {
    private val context by lazy { BaseApplication.instance?.applicationContext }

    fun send(payload: PayLoad) {
        //找到合适条件的页面class后，创建推送
        val clazz = getPage(payload)
        //没拿到结果不做处理
        if (TextUtils.isEmpty(clazz)) return
        //如果得到的json返回类是属于普通消息，且APP处于开启状态，则点击通知后不跳转
        if (ARouterPath.StartActivity == clazz && isAppOnForeground()) {
            NotificationFactory.instance.normal(payload.title!!, payload.content!!, R.mipmap.push_small, R.mipmap.push)
        } else {
            val id = "xxxx"//服务器给的特定id
            NotificationFactory.instance.normal(payload.title!!, payload.content!!, R.mipmap.push_small, R.mipmap.push, Intent(context, PushActivity::class.java).putExtra(Extras.IS_RUNNING, isAppOnForeground()).putExtra(Extras.PAYLOAD, payload), id)
        }
    }

    /**
     * 获取要跳转页面的路由路径
     */
    fun getPage(payload: PayLoad): String {
        return when (payload.sendType) {
            "SCORE_FINE_IN", "SCORE_FINE_OUT" -> ARouterPath.StartActivity
//            "DISOBEY_FINE_IN", "DISOBEY_FINE_OUT" -> ARouterPath.CameraActivity
            else -> ""
        }
    }

    /**
     * 在进程中去寻找当前APP的信息，判断是否在运行
     * 100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
     */
    private fun isAppOnForeground(): Boolean {
        val processes = (context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).runningAppProcesses ?: return false
        for (process in processes) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && process.processName.equals(context?.packageName)) return true
        }
        return false
    }

}