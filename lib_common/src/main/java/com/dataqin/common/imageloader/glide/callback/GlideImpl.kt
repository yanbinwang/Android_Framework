package com.dataqin.common.imageloader.glide.callback

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.dataqin.common.imageloader.glide.callback.progress.OnLoaderListener
import java.io.File

/**
 * Created by WangYanBin on 2020/5/29.
 */
interface GlideImpl {

    //---------------------------------------------图片加载开始---------------------------------------------
    fun displayZoomImage(view: ImageView, string: String?, listener: GlideRequestListener<Bitmap?>? = null)

    fun displayCoverImage(view: ImageView, string: String?)

    fun displayProgressImage(view: ImageView, string: String, listener: OnLoaderListener? = null)

    fun displayImage(view: ImageView, string: String?)

    fun displayImage(view: ImageView, string: String?, errorId: Int)

    fun displayImage(view: ImageView, string: String?, listener: GlideRequestListener<Drawable?>? = null)

    fun displayImage(view: ImageView, string: String?, placeholderId: Int, errorId: Int, listener: GlideRequestListener<Drawable?>? = null)

    //---------------------------------------------图片加载结束---------------------------------------------

    //---------------------------------------------圆角图片加载开始---------------------------------------------
    fun displayRoundImage(view: ImageView, string: String?, roundingRadius: Int)

    fun displayRoundImage(view: ImageView, string: String?, errorId: Int, roundingRadius: Int)

    fun displayRoundImage(view: ImageView, string: String?, roundingRadius: Int, overRide: BooleanArray)

    fun displayRoundImage(view: ImageView, string: String?, errorId: Int, roundingRadius: Int, overRide: BooleanArray)
    //---------------------------------------------圆角图片加载开始---------------------------------------------

    //---------------------------------------------圆形图片加载开始---------------------------------------------
    fun displayCircleImage(view: ImageView, string: String?)

    fun displayCircleImage(view: ImageView, string: String?, errorId: Int)
    //---------------------------------------------圆形图片加载开始---------------------------------------------

    //---------------------------------------------图片库方法开始---------------------------------------------
    fun downloadImage(context: Context, string: String?, width: Int, height: Int, listener: GlideRequestListener<File?>?)

    fun clearMemoryCache(context: Context)

    fun clearDiskCache(context: Context)

    val cacheDir: File?
    //---------------------------------------------图片库方法结束---------------------------------------------

}