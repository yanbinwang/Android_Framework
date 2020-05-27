package com.example.common.imagloader

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.example.common.BaseApplication
import com.example.common.R
import com.example.common.imagloader.glide.CornerTransform
import com.example.common.imagloader.glide.GlideModuleImpl

/**
 * author: wyb
 * date: 2017/11/15.
 * 加载图片工具类
 */
class ImageLoaderFactory(private var context: Context) : GlideModuleImpl() {
    private var glideRequest: RequestManager = Glide.with(this.context)

    companion object {
        private var instance: ImageLoaderFactory? = null

        @Synchronized
        fun getInstance(): ImageLoaderFactory {
            if (instance == null) {
                instance = ImageLoaderFactory(BaseApplication.instance)
            }
            return instance!!
        }
    }

    fun display(view: ImageView, model: Any) {
        display(view, model, R.drawable.shape_loading_normal, 0, null)
    }

    fun display(view: ImageView, model: Any, errorId: Int) {
        display(view, model, R.drawable.shape_loading_normal, errorId, null)
    }

    fun display(view: ImageView, model: Any, requestListener: RequestListener<Drawable>) {
        display(view, model, R.drawable.shape_loading_normal, 0, requestListener)
    }

    fun display(view: ImageView, model: Any, placeholderId: Int, errorId: Int, requestListener: RequestListener<Drawable>?) {
        glideRequest
                .load(if (model is String) model.toString() else model.toString().toInt())
                .placeholder(placeholderId)
                .error(errorId)
                .dontAnimate()
                .listener(requestListener)
                .into(view)
    }

    fun displayRound(view: ImageView, string: String, roundingRadius: Int) {
        displayRound(view, string, 0, roundingRadius)
    }

    fun displayRound(view: ImageView, string: String, errorId: Int, roundingRadius: Int) {
        glideRequest.load(string)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(roundingRadius)))
                .placeholder(R.drawable.shape_loading_normal)
                .error(errorId)
                .dontAnimate()
                .into(view)
    }

    fun displayRound(view: ImageView, string: String, roundingRadius: Int, leftTop: Boolean, rightTop: Boolean, leftBottom: Boolean, rightBottom: Boolean) {
        displayRound(view, string, 0, roundingRadius, leftTop, rightTop, leftBottom, rightBottom)
    }

    fun displayRound(view: ImageView, string: String, errorId: Int, roundingRadius: Int, leftTop: Boolean, rightTop: Boolean, leftBottom: Boolean, rightBottom: Boolean) {
        val transformation = CornerTransform(
            context,
            roundingRadius.toFloat()
        )
        transformation.setExceptCorner(leftTop, rightTop, leftBottom, rightBottom)
        glideRequest
                .load(string)
                .transform(transformation)
                .placeholder(R.drawable.shape_loading_normal)
                .error(errorId)
                .dontAnimate()
                .into(view)
    }

    fun displayCircle(view: ImageView, string: String) {
        displayCircle(view, string, R.drawable.shape_loading_round)
    }

    fun displayCircle(view: ImageView, string: String, errorId: Int) {
        glideRequest
                .load(string)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.shape_loading_round)
                .error(errorId)
                .dontAnimate()
                .into(view)
    }

}