package com.dataqin.testnew.utils

import android.app.Activity
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
    private var end = false//是否已经弹过
    private var weakActivity: WeakReference<Activity>? = null
    private val popupMap by lazy { ConcurrentHashMap<String, Any?>() }//通知管理类，key-通知说明，value-通知对象

    //一些配置通知的label屬性集合
    private val labelList = arrayOf(
        "update",//更新
        "push",//推送
        "advertisement",//广告
        "advertisement2"//广告2
    )

    @JvmStatic
    fun initialize(activity: Activity) {
        this.end = false
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
    fun add(index: Int, any: Any?) {
        popupMap[labelList[index]] = any
        show()
    }

    /**
     * 逐步弹出窗口
     */
    private fun show() {
        if (!end) {
            //检测当前的通知集合是否已经达到了配置的通知总数
            if (popupMap.size >= labelList.size) {
                end = true
                update()
            }
        }
    }

    /**
     * 更新
     */
    private fun update() {
        val model = popupMap[labelList[0]]
//        var versionModel = model as VersionModel
//        if(null != versionModel) {
//
//        } else notification()
    }

    /**
     * 通知
     */
    private fun notification() {
//        if (!NotificationFactory.instance.isEnabled(weakActivity?.get()!!)) {
//            AppDialog.with(weakActivity?.get()).setOnDialogListener(object : OnDialogListener {
//                override fun onConfirm() {
//                    NotificationFactory.instance.setting(weakActivity?.get()!!)
//                    advertisement()
//                }
//
//                override fun onCancel() {
//                    advertisement()
//                }
//            }).setParams("提示", "是否开启推送通知", "确定", "取消").show()
//        } else advertisement()
    }

    /**
     * 广告
     */
    private fun advertisement() {

    }

}