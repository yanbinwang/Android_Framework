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
import com.example.common.imagloader.glide.callback.GlideImpl
import com.example.common.imagloader.glide.callback.GlideModule
import com.example.common.imagloader.glide.transform.CornerTransform
import java.io.File

/**
 * Created by WangYanBin on 2020/5/29.
 * 图片加载库使用Application上下文，Glide请求将不受Activity/Fragment生命周期控制。
 */
class ImageLoaderFactory : GlideModule(), GlideImpl {
    private val context: Context = BaseApplication.getInstance().applicationContext
    private val manager: RequestManager

    companion object {
        private var instance: ImageLoaderFactory? = null

        @Synchronized
        fun getInstance(): ImageLoaderFactory {
            if (instance == null) {
                instance = ImageLoaderFactory()
            }
            return instance!!
        }
    }

    init {
        manager = Glide.with(context)
    }

    override fun displayImage(view: ImageView?, string: String?) {
        displayImage(view, string, 0)
    }

    override fun displayImage(view: ImageView?, string: String?, errorId: Int) {
        displayImage(view, string, R.drawable.shape_loading_normal, errorId, null)
    }

    override fun displayImage(view: ImageView?, string: String?, requestListener: RequestListener<Drawable?>?) {
        displayImage(view, string, R.drawable.shape_loading_normal, 0, requestListener)
    }

    override fun displayImage(view: ImageView?, string: String?, placeholderId: Int, errorId: Int, requestListener: RequestListener<Drawable?>?) {
        manager
            .load(string)
            .placeholder(placeholderId)
            .error(errorId)
            .dontAnimate()
            .listener(requestListener)
            .into(view!!)
    }

    override fun displayRoundImage(view: ImageView?, string: String?, roundingRadius: Int) {
        displayRoundImage(view, string, 0, roundingRadius)
    }

    override fun displayRoundImage(view: ImageView?, string: String?, errorId: Int, roundingRadius: Int) {
        manager.load(string)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(roundingRadius)))
            .placeholder(R.drawable.shape_loading_normal)
            .error(errorId)
            .dontAnimate()
            .into(view!!)
    }

    override fun displayRoundImage(view: ImageView?, string: String?, roundingRadius: Int, leftTop: Boolean, rightTop: Boolean, leftBottom: Boolean, rightBottom: Boolean) {
        displayRoundImage(view, string, 0, roundingRadius, leftTop, rightTop, leftBottom, rightBottom)
    }

    override fun displayRoundImage(view: ImageView?, string: String?, errorId: Int, roundingRadius: Int, leftTop: Boolean, rightTop: Boolean, leftBottom: Boolean, rightBottom: Boolean) {
        val transformation = CornerTransform(context, roundingRadius.toFloat())
        transformation.setExceptCorner(leftTop, rightTop, leftBottom, rightBottom)
        manager
            .load(string)
            .transform(transformation)
            .placeholder(R.drawable.shape_loading_normal)
            .error(errorId)
            .dontAnimate()
            .into(view!!)
    }

    override fun displayCircleImage(view: ImageView?, string: String?) {
        displayCircleImage(view, string, R.drawable.shape_loading_round)
    }

    override fun displayCircleImage(view: ImageView?, string: String?, errorId: Int) {
        manager
            .load(string)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.shape_loading_round)
            .error(errorId)
            .dontAnimate()
            .into(view!!)
    }

    override val cacheDir: File?
        get() = Glide.getPhotoCacheDir(context)

    override fun clearMemoryCache() {
        Glide.get(context).clearMemory()
    }

    override fun clearDiskCache() {
        Glide.get(context).clearDiskCache()
    }

}