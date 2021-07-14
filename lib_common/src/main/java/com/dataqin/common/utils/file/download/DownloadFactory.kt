package com.dataqin.common.utils.file.download

import android.os.Looper
import android.util.Patterns
import com.dataqin.base.utils.ToastUtil
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.BaseApplication
import com.dataqin.common.R
import com.dataqin.common.bus.RxSchedulers
import com.dataqin.common.http.repository.ResourceSubscriber
import com.dataqin.common.subscribe.CommonSubscribe
import com.dataqin.common.utils.file.FileUtil
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.Executors

/**
 * author: wyb
 * 下载单例
 * url下载得到的是一个ResponseBody对象，对该对象还需开启异步线程进行下载和UI刷新
 * 故下载的完成回调需要在线程池内外做判断
 */
class DownloadFactory private constructor() {
    private val content by lazy { BaseApplication.instance?.applicationContext!! }
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }
    private val executors by lazy { Executors.newSingleThreadExecutor() }

    companion object {
        @JvmStatic
        val instance: DownloadFactory by lazy {
            DownloadFactory()
        }
    }

    fun download(downloadUrl: String, filePath: String, fileName: String, onDownloadListener: OnDownloadListener?) {
        if (!Patterns.WEB_URL.matcher(downloadUrl).matches()) {
            ToastUtil.mackToastSHORT(content.getString(R.string.toast_download_url_error), content)
            return
        }
        FileUtil.deleteDir(filePath)
        CommonSubscribe.getDownloadApi(downloadUrl)
            .compose(RxSchedulers.ioMain())
            .subscribeWith(object : ResourceSubscriber<ResponseBody>() {
                override fun onStart() {
                    super.onStart()
                    onDownloadListener?.onStart()
                }

                override fun onResult(data: ResponseBody?, throwable: Throwable?) {
                    if (null != data) {
                        executors.execute {
                            var inputStream: InputStream? = null
                            var fileOutputStream: FileOutputStream? = null
                            try {
                                val file = File(FileUtil.isExistDir(filePath), fileName)
                                val buf = ByteArray(2048)
                                val total = data.contentLength()
                                inputStream = data.byteStream()
                                fileOutputStream = FileOutputStream(file)
                                var len: Int
                                var sum: Long = 0
                                while (((inputStream.read(buf)).also { len = it }) != -1) {
                                    fileOutputStream.write(buf, 0, len)
                                    sum += len.toLong()
                                    val progress = (sum * 1.0f / total * 100).toInt()
                                    weakHandler.post { onDownloadListener?.onLoading(progress) }
                                }
                                fileOutputStream.flush()
                                weakHandler.post { onDownloadListener?.onSuccess(file.path) }
                            } catch (e: Exception) {
                                weakHandler.post { onDownloadListener?.onFailed(e) }
                            } finally {
                                inputStream?.close()
                                fileOutputStream?.close()
                            }
                        }
                        executors.isShutdown
                    } else onDownloadListener?.onFailed(throwable)
                }
            })
    }

}