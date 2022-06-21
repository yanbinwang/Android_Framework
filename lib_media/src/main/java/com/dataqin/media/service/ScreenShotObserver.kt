package com.dataqin.media.service

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Build
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
 *  1.具备读写权限
 *  2.安卓10开始已淘汰MediaStore.MediaColumns.DATA方法，没法捕获绝对路径，只有通过RELATIVE_PATH捕获相对路径
 */
@SuppressLint("Range")
class ScreenShotObserver : ContentObserver(null) {
    private var filePath = ""
    private val context by lazy { BaseApplication.instance?.applicationContext!! }

    companion object {
        @JvmStatic
        val instance by lazy { ScreenShotObserver() }
    }

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        //Query [ 图片媒体集 ] 包括： DCIM/ 和 Pictures/ 目录
        val columns = arrayOf(
            MediaStore.MediaColumns.DATE_ADDED,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.MediaColumns.RELATIVE_PATH else MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.MediaColumns.DATE_MODIFIED + " desc")
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    //获取监听的路径
                    val queryPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) "${(context.getExternalFilesDir(null)?.absolutePath ?: "").split("Android")[0]}${getQueryResult(cursor, columns[1])}${getQueryResult(cursor, columns[3])}" else getQueryResult(cursor, columns[1])
                    //保证每次获取到的路径不重复
                    if (filePath != queryPath) {
                        filePath = queryPath
                        //判断当前路径是否为图片，是的话捕获当前路径
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(queryPath, options)
                        if (options.outWidth != -1) {
                            val file = File(queryPath)
                            e("ScreenShotObserver", " \n生成图片的路径:$queryPath\n手机截屏的路径：${file.parent}")
                            RxBus.instance.post(RxEvent(Constants.APP_SHOT_PATH, file.parent ?: ""), RxEvent(Constants.APP_SHOT_IMAGE_PATH, queryPath))
                        }
                    }
                }
            }
        } catch (ignored: Exception) {
        } finally {
            cursor?.close()
        }
    }

    /**
     * 返回查询结果
     */
    private fun getQueryResult(cursor: Cursor, columnName: String) = cursor.getString(cursor.getColumnIndex(columnName))

    /**
     * 注册监听
     */
    fun register() = context.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this)

    /**
     * 注销监听
     */
    fun unregister() = context.contentResolver.unregisterContentObserver(this)

}