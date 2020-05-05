package com.example.glide.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.example.framework.R

/**
 * author: wyb
 * date: 2017/11/15.
 * 加载图片工具类
 */
class GlideUtil(private var context: Context) : GlideModuleImpl() {
    private var glideRequest: RequestManager? = null //图片类

    init {
        glideRequest = Glide.with(this.context)
    }

    fun display(view: ImageView, string: String) {
        display(view, string, R.drawable.img_loading)
    }

    fun display(view: ImageView, string: String, id: Int) {
        glideRequest!!.load(string).placeholder(R.drawable.img_loading).error(id).dontAnimate().into(view)
    }

    fun display(view: ImageView, string: String, headerString: String) {
        display(view, string, headerString, R.drawable.img_loading)
    }

    fun display(view: ImageView, string: String, headerString: String, id: Int) {
        val glideUrl = GlideUrl(string, LazyHeaders.Builder().addHeader("Cookie", headerString).build())
        glideRequest!!.load(glideUrl).placeholder(R.drawable.img_loading).error(id).dontAnimate().into(view)
    }

    fun display(view: ImageView, string: String, requestListener: RequestListener<Drawable>) {
        display(view, string, R.drawable.img_loading, true, requestListener)
    }

    fun display(view: ImageView, string: String, isPlaceholder: Boolean, requestListener: RequestListener<Drawable>) {
        display(view, string, R.drawable.img_loading, isPlaceholder, requestListener)
    }

    fun display(view: ImageView, string: String, id: Int, isPlaceholder: Boolean, requestListener: RequestListener<Drawable>) {
        if (isPlaceholder) {
            glideRequest!!.load(string).placeholder(R.drawable.img_loading).error(id).dontAnimate().listener(requestListener).into(view)
        } else {
            glideRequest!!.load(string).error(id).dontAnimate().listener(requestListener).into(view)
        }
    }

    fun display(view: ImageView, pathId: Int, requestListener: RequestListener<Drawable>) {
        display(view, pathId, R.drawable.img_loading, true, requestListener)
    }

    fun display(view: ImageView, pathId: Int, isPlaceholder: Boolean, requestListener: RequestListener<Drawable>) {
        display(view, pathId, R.drawable.img_loading, isPlaceholder, requestListener)
    }

    fun display(view: ImageView, pathId: Int, id: Int, isPlaceholder: Boolean, requestListener: RequestListener<Drawable>) {
        if (isPlaceholder) {
            glideRequest!!.load(pathId).placeholder(R.drawable.img_loading).error(id).dontAnimate().listener(requestListener).into(view)
        } else {
            glideRequest!!.load(pathId).error(id).dontAnimate().listener(requestListener).into(view)
        }
    }

    fun displayRound(view: ImageView, string: String, roundingRadius: Int) {
        displayRound(view, string, R.drawable.img_loading, roundingRadius)
    }

    fun displayRound(view: ImageView, string: String, id: Int, roundingRadius: Int) {
        glideRequest!!.load(string).apply(RequestOptions.bitmapTransform(RoundedCorners(roundingRadius))).placeholder(R.drawable.img_loading).error(id).dontAnimate().into(view)
    }

    fun displayRound(view: ImageView, string: String, roundingRadius: Int, leftTop: Boolean, rightTop: Boolean, leftBottom: Boolean, rightBottom: Boolean) {
        displayRound(view, string, R.drawable.img_loading, roundingRadius, leftTop, rightTop, leftBottom, rightBottom)
    }

    fun displayRound(view: ImageView, string: String, id: Int, roundingRadius: Int, leftTop: Boolean, rightTop: Boolean, leftBottom: Boolean, rightBottom: Boolean) {
        val transformation = CornerTransform(
            context,
            roundingRadius.toFloat()
        )
        transformation.setExceptCorner(leftTop, rightTop, leftBottom, rightBottom)
        glideRequest!!.load(string).transform(transformation).placeholder(R.drawable.img_loading).error(id).dontAnimate().into(view)
    }

    fun displayCircle(view: ImageView, string: String) {
        displayCircle(view, string, R.drawable.img_loading_circle)
    }

    fun displayCircle(view: ImageView, string: String, id: Int) {
        glideRequest!!.load(string).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.img_loading_circle).error(id).dontAnimate().into(view)
    }

}
