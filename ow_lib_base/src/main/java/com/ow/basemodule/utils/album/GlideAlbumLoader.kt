package com.ow.basemodule.utils.album

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ow.basemodule.R
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.AlbumLoader

/**
 * author: wyb
 * date: 2017/8/29.
 * 相册使用glide图片加载库
 */
class GlideAlbumLoader : AlbumLoader {

    override fun load(imageView: ImageView, albumFile: AlbumFile) {
        load(imageView, albumFile.path)
    }

    override fun load(imageView: ImageView, url: String) {
        Glide.with(imageView.context).load(url).placeholder(R.drawable.img_loading).error(R.drawable.img_loading).dontAnimate().into(imageView)
    }

}
