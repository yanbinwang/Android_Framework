package com.dataqin.testnew.widget.scale

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter

/**
 * Created by wangyanbin
 * 伸缩图片适配器
 */
class ScaleAdapter(var data: List<ScaleImageView>) : PagerAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(data[position])
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val img = data[position]
        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        return img
    }

}