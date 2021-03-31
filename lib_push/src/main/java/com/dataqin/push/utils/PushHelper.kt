package com.dataqin.push.utils

import android.app.ActivityManager
import android.content.Context
import com.dataqin.common.BaseApplication
import com.dataqin.push.model.PushModel

/**
 *  Created by wangyanbin
 *  构架推送工具类
 */
object PushHelper {
    private val context by lazy { BaseApplication.instance?.applicationContext }

    fun send(model: PushModel) {
        //找到合适条件的页面class后，创建推送
    }

    /**
     * 在进程中去寻找当前APP的信息，判断是否在运行
     * 100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
     */
    private fun isAppOnForeground(): Boolean {
        val runningTasks = (context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningTasks(100)
        val packageName = context?.packageName
        for (tasks in runningTasks) {
            if (tasks.topActivity.packageName == packageName || tasks.baseActivity.packageName == packageName) {
                return true
            }
        }
        return false
    }

}