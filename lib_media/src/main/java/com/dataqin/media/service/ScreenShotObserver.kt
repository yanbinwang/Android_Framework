package com.dataqin.media.service

import android.content.ContentUris
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.BitmapFactory
import android.provider.BaseColumns
import android.provider.MediaStore
import com.dataqin.base.utils.LogUtil.e
import com.dataqin.common.BaseApplication
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import java.io.File

/**
 *  Created by wangyanbin
 *  监听产生文件，获取对应路径
 */
class ScreenShotObserver : ContentObserver(null) {
    private var filePath = ""
    private val context by lazy { BaseApplication.instance?.applicationContext!! }
    private val TAG = "ScreenShotObserver"

    companion object {
        @JvmStatic
        val instance: ScreenShotObserver by lazy {
            ScreenShotObserver()
        }
    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        //Query [ 图片媒体集 ] 包括： DCIM/ 和 Pictures/ 目录
//        val columns = arrayOf(MediaStore.MediaColumns.DATE_ADDED, MediaStore.MediaColumns.DATA)
        val columns = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.MIME_TYPE, MediaStore.Images.Media.SIZE)
        var cursor: Cursor? = null
        try {
//            cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.MediaColumns.DATE_MODIFIED + " desc")
            cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)))
                    val queryPath = contentUri.path
                    if (filePath != queryPath) {
                        filePath = queryPath ?: ""
                        e(TAG, "queryPath:$filePath")
                        //判断当前路径是否为图片，是的话捕获当前路径
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(queryPath, options)
                        if (options.outWidth != -1) {
                            val file = File(filePath)
                            e(TAG, " \n生成图片的路径:$filePath\n手机截屏的路径：${file.parent}")
                            RxBus.instance.post(RxEvent(Constants.APP_SHOT_PATH, file.parent ?: ""), RxEvent(Constants.APP_SHOT_IMAGE_PATH, filePath))
                        }
                    }
//                    //获取监听的路径
//                    val queryPath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
//                    if (filePath != queryPath) {
//                        filePath = queryPath
//                        e(TAG, "queryPath:$queryPath")
//                        //判断当前路径是否为图片，是的话捕获当前路径
//                        val options = BitmapFactory.Options()
//                        options.inJustDecodeBounds = true
//                        BitmapFactory.decodeFile(queryPath, options)
//                        if (options.outWidth != -1) {
//                            val file = File(queryPath)
//                            e(TAG, " \n生成图片的路径:$queryPath\n手机截屏的路径：${file.parent}")
//                            RxBus.instance.post(RxEvent(Constants.APP_SHOT_PATH, file.parent ?: ""), RxEvent(Constants.APP_SHOT_IMAGE_PATH, queryPath))
//                        }
//                    }
                }
            }
        } catch (ignored: Exception) {
        } finally {
            cursor?.close()
        }
    }

    /**
     * 注册监听
     */
    fun register() = context.contentResolver.registerContentObserver(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        true,
        this
    )

    /**
     * 注销监听
     */
    fun unregister() = context.contentResolver.unregisterContentObserver(this)

}