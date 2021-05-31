package com.dataqin.common.widget.advertising.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

/**
 * Created by wangyanbin
 * 无限循环适配器
 */
class AdAdapter(private var imgList: MutableList<ImageView>) : BaseAdapter() {

    //如果只有一张图时不滚动
    override fun getCount(): Int {
        return if (imgList.size < 2) imgList.size else Int.MAX_VALUE
    }

    //返回ImageView
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return imgList[position % imgList.size]
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}