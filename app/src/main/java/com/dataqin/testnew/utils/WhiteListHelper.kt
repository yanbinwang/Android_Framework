package com.dataqin.testnew.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.dataqin.common.BaseApplication


/**
 * 厂商白名单检测工具栏
 * 检测开启后再开启定位的服务
 * <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
 */
@RequiresApi(api = Build.VERSION_CODES.M)
@SuppressLint("BatteryLife")
object WhiteListHelper {
    private val context by lazy { BaseApplication.instance?.applicationContext }

    /**
     * 检测
     */
    fun testing(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isIgnoringBatteryOptimizations()) {
                return true
            } else {
                if (isHuawei()) {
                    try {
                        showActivity("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
                    } catch (e: Exception) {
                        showActivity("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.bootstart.BootStartActivity")
                    }
                    return false
                }
                if (isXiaomi()) {
                    showActivity("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
                    return false
                }
                if (isOPPO()) {
                    try {
                        showActivity("com.coloros.phonemanager")
                    } catch (e1: Exception) {
                        try {
                            showActivity("com.oppo.safe")
                        } catch (e2: Exception) {
                            try {
                                showActivity("com.coloros.oppoguardelf")
                            } catch (e3: Exception) {
                                showActivity("com.coloros.safecenter")
                            }
                        }
                    }
                    return false
                }
                if (isVIVO()) {
                    showActivity("com.iqoo.secure")
                    return false
                }
                if (isMeizu()) {
                    showActivity("com.meizu.safe")
                    return false
                }
                if (isSamsung()) {
                    try {
                        showActivity("com.samsung.android.sm_cn")
                    } catch (e: java.lang.Exception) {
                        showActivity("com.samsung.android.sm")
                    }
                    return false
                }
                if (isLeTV()) {
                    showActivity("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")
                    return false
                }
                if (isSmartisan()) {
                    showActivity("com.smartisanos.security")
                    return false
                }
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    intent.data = Uri.parse("package:" + context?.packageName)
                    context?.startActivity(intent)
                } catch (e: Exception) {
                } finally {
                    return false
                }
            }
        } else {
            return true
        }
    }

    private fun isIgnoringBatteryOptimizations() = (context?.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(context?.packageName)

    /**
     * 跳转到指定应用的首页
     */
    private fun showActivity(packageName: String) = context?.startActivity(context?.packageManager!!.getLaunchIntentForPackage(packageName))

    /**
     * 跳转到指定应用的指定页面
     */
    private fun showActivity(packageName: String, activityDir: String) {
        val intent = Intent()
        intent.component = ComponentName(packageName, activityDir)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    private fun isHuawei() = Build.BRAND != null && (Build.BRAND.toLowerCase() == "huawei" || Build.BRAND.toLowerCase() == "honor")

    private fun isXiaomi() = Build.BRAND != null && Build.BRAND.toLowerCase() == "xiaomi"

    private fun isOPPO() = Build.BRAND != null && Build.BRAND.toLowerCase() == "oppo"

    private fun isVIVO() = Build.BRAND != null && Build.BRAND.toLowerCase() == "vivo"

    private fun isMeizu() = Build.BRAND != null && Build.BRAND.toLowerCase() == "meizu"

    private fun isSamsung() = Build.BRAND != null && Build.BRAND.toLowerCase() == "samsung"

    private fun isLeTV() = Build.BRAND != null && Build.BRAND.toLowerCase() == "letv"

    private fun isSmartisan() = Build.BRAND != null && Build.BRAND.toLowerCase() == "smartisan"

}