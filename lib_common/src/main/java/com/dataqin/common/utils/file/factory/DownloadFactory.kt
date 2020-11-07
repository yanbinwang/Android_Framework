package com.dataqin.common.utils.file.factory

import android.os.Looper
import com.dataqin.common.bus.RxSchedulers
import com.dataqin.common.http.repository.ResourceSubscriber
import com.dataqin.common.subscribe.CommonSubscribe.getDownloadApi
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.common.utils.file.callback.OnDownloadListener
import com.dataqin.common.utils.handler.WeakHandler
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
    private val weakHandler = WeakHandler(Looper.getMainLooper())
    private val executors = Executors.newSingleThreadExecutor()

    companion object {
        @JvmStatic
        val instance: DownloadFactory by lazy {
            DownloadFactory()
        }
    }

    fun download(downloadUrl: String, filePath: String, fileName: String, onDownloadListener: OnDownloadListener?) {
        FileUtil.deleteDir(filePath)
        getDownloadApi(downloadUrl).compose(RxSchedulers.ioMain())
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
                                weakHandler.post { onDownloadListener?.onComplete() }
                            }
                        }
                        executors.isShutdown
                    } else {
                        onDownloadListener?.onFailed(throwable)
                        onDownloadListener?.onComplete()
                    }
                }
            })
    }

}