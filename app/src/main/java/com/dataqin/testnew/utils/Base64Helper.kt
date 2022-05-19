package com.dataqin.testnew.utils

import android.os.Looper
import android.util.Base64
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

/**
 * base64处理类
 */
object Base64Helper {
    private val executors by lazy { Executors.newSingleThreadExecutor() }
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }

    /**
     * base64:字符串
     * suffix:后缀名
     * root:缓存文件路径-》默认保存至app目录下的文件缓存文件夹内
     * delete:是否清空目录-》默认每次都清空
     */
    @Synchronized
    @JvmOverloads
    @JvmStatic
    fun base64(base64: String, suffix: String, listener: OnThreadListener? = null, root: String = "${Constants.APPLICATION_FILE_PATH}/文件缓存", delete: Boolean = true) {
        executors.execute {
            weakHandler.post { listener?.onStart() }
            var path = ""
            try {
                //清空缓存目录
                if (delete) FileUtil.deleteDir(root)
                //创建目录
                val storeDir = File(root)
                if (!storeDir.mkdirs()) storeDir.createNewFile()
                //生成缓存文件
                val cacheFile = File(storeDir, "${System.currentTimeMillis()}_cache.${suffix}")
                path = cacheFile.absolutePath
                val decodeBytes = Base64.decode(pure(base64), 0)
                val outputStream: FileOutputStream?
                outputStream = FileOutputStream(cacheFile, false)
                outputStream.write(decodeBytes)
                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
            } finally {
                weakHandler.post { listener?.onStop(path) }
            }
        }
        executors.isShutdown
    }

    /**
     * 剔除字符串中data:image/png;base64的前缀
     */
    @JvmStatic
    fun pure(encodedString: String) = encodedString.substring(encodedString.indexOf(",") + 1)

    interface OnThreadListener {
        /**
         * 线程开始执行
         */
        fun onStart()

        /**
         * 线程停止执行
         */
        fun onStop(path: String? = null)
    }

}