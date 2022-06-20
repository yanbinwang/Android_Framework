package com.dataqin.push.activity

import android.text.TextUtils
import androidx.viewbinding.ViewBinding
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.base.page.PageParams
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Extras
import com.dataqin.push.model.PayLoad
import com.dataqin.push.utils.PushHelper

/**
 *  Created by wangyanbin
 *  无任何页面容器的activity，用于区分跳转的推送页面
 *  1.用户点击构建好的通知后统一先跳转到该页面，在当前页面区分启动状态，跳转至不同页面
 *  2.页面样式采用启动页StartActivity的样式
 */
class PushActivity : BaseActivity<ViewBinding>() {

    override fun initData() {
        super.initData()
        val payload = intent.getSerializableExtra(Extras.BUNDLE_BEAN) as PayLoad
        val clazz = PushHelper.getPage(payload)
        if (!TextUtils.isEmpty(clazz)) if (intent.getBooleanExtra(Extras.IS_RUNNING, false)) navigation(clazz) else navigation(ARouterPath.StartActivity, PageParams().append(Extras.PAYLOAD, if (ARouterPath.StartActivity != clazz) payload else null))
        finish()
    }

}