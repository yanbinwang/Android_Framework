package com.dataqin.testnew.widget.scale

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.dataqin.testnew.R

/**
 * Created by wangyanbin
 * 伸缩图片适配器
 */
class ScaleAdapter(var data: List<ScaleImageView>, var fileList: ArrayList<*>) : PagerAdapter() {

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
        //此处不能引用全局工具类，内部多媒体库容易和视频库代码报错
        Glide.with(container.context)
            .load(fileList[position])
            .placeholder(R.drawable.shape_scale_loading)
            .error(R.drawable.shape_image_loading)
            .dontAnimate()
            .into(img)
        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        return img
    }

}