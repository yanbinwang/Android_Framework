package com.dataqin.common.utils.helper

import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 *  Created by wangyanbin
 *  导航栏帮助类
 */
object NavigationHelper {
    private var navigationView: BottomNavigationView? = null
    private var ids = ArrayList<Int>()
    var onNavigationItemSelectedListener: OnNavigationItemSelectedListener? = null

    /**
     * 初始化
     */
    @JvmStatic
    fun initialize(navigationView: BottomNavigationView, ids: ArrayList<Int>) {
        NavigationHelper.navigationView = navigationView
        NavigationHelper.ids = ids
        //去除长按的toast提示
        for (position in ids.indices) {
            (navigationView.getChildAt(0) as ViewGroup).getChildAt(position).findViewById<View>(ids[position]).setOnLongClickListener { true }
        }
        //最多配置5个
        navigationView.setOnNavigationItemSelectedListener {
            onNavigationItemSelectedListener?.onNavigationItemSelected(
                when (it.itemId) {
                    ids[0] -> 0
                    ids[1] -> 1
                    ids[2] -> 2
                    ids[3] -> 3
                    ids[4] -> 4
                    else -> -1
                }
            )
            true
        }
    }

    /**
     * 选中下标
     */
    @JvmStatic
    fun selectedItem(index: Int) = navigationView?.menu?.getItem(index)?.itemId

    interface OnNavigationItemSelectedListener {

        fun onNavigationItemSelected(index: Int = 0)

    }

}