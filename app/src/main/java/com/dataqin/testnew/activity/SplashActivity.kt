package com.dataqin.testnew.activity

import android.content.Intent
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath

/**
 *  Created by wangyanbin
 *  app整体启动页
 *  安卓本身bug会在初次安装应用后点击图标再次拉起启动页，造成界面显示不不正常
 */
class SplashActivity : BaseActivity<ViewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action == Intent.ACTION_MAIN) {
            finish()
            return
        }
        navigation(ARouterPath.StartActivity).finish()
    }

}