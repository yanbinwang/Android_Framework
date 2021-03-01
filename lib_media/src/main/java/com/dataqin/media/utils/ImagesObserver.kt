package com.dataqin.media.utils

import android.database.ContentObserver
import android.database.Cursor
import android.graphics.BitmapFactory
import android.provider.MediaStore
import com.dataqin.base.utils.LogUtil.e
import com.dataqin.common.BaseApplication

/**
 *  Created by wangyanbin
 *  监听系统图片数据库文件的产生，获取其路径
 */
class ImagesObserver : ContentObserver(null) {
    private var imageNum = 0
    private var context = BaseApplication.instance?.applicationContext!!
    private val TAG = "ImagesObserver"

    companion object {
        @JvmStatic
        val instance: ImagesObserver by lazy {
            ImagesObserver()
        }
    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        val columns = arrayOf(MediaStore.MediaColumns.DATE_ADDED, MediaStore.MediaColumns.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.MediaColumns.DATE_MODIFIED + " desc")
            if (cursor == null) {
                return
            }
            val count = cursor.count
            if (imageNum == 0) {
                imageNum = count
            } else if (imageNum >= count) {
                return
            }
            imageNum = count
            if (cursor.moveToFirst()) {
                //获取监听的路径
                val filePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                e(TAG, "filePath:$filePath")
                //判断当前路径是否为图片，是的话捕获当前路径
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(filePath, options);
                if (options.outWidth != -1) {
                    e(TAG, "发送生成图片的路径广播")
//                    WeakHandler(Looper.getMainLooper()).post{}
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null) {
                try {
                    cursor.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    //注册监听
    fun register() {
        context.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this)
    }

    //注销监听
    fun unregister() {
        context.contentResolver.unregisterContentObserver(this)
    }

}