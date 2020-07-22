package com.example.common.utils.file.factory

import android.annotation.SuppressLint
import android.os.Looper
import com.example.common.subscribe.BaseSubscribe
import com.example.common.utils.file.FileUtil
import com.example.common.utils.file.callback.OnDownloadListener
import com.example.framework.utils.WeakHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * author: wyb
 * 下载单例
 */
@SuppressLint("CheckResult")
class DownloadFactory private constructor() {
    private val weakHandler: WeakHandler =
        WeakHandler(Looper.getMainLooper())

    companion object {
        @JvmStatic
        val instance: DownloadFactory by lazy {
            DownloadFactory()
        }
    }

    fun download(downloadUrl: String, filePath: String, fileName: String, onDownloadListener: OnDownloadListener) : Disposable {
        FileUtil.deleteDir(filePath)
        return BaseSubscribe.download(downloadUrl)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : ResourceSubscriber<ResponseBody>() {

                override fun onNext(responseBody: ResponseBody?) {
                    object : Thread() {
                        override fun run() {
                            var inputStream: InputStream? = null
                            var fileOutputStream: FileOutputStream? = null
                            try {
                                val file = File(FileUtil.isExistDir(filePath), fileName)
                                val buf = ByteArray(2048)
                                val total = responseBody!!.contentLength()
                                inputStream = responseBody.byteStream()
                                fileOutputStream = FileOutputStream(file)
                                var len: Int
                                var sum: Long = 0
                                while (((inputStream.read(buf)).also { len = it }) != -1) {
                                    fileOutputStream.write(buf, 0, len)
                                    sum += len.toLong()
                                    val progress = (sum * 1.0f / total * 100).toInt()
                                    weakHandler.post { onDownloadListener.onDownloading(progress) }
                                }
                                fileOutputStream.flush()
                                weakHandler.post { onDownloadListener.onDownloadSuccess(file.path) }
                            } catch (e: Exception) {
                                weakHandler.post { onDownloadListener.onDownloadFailed(e) }
                            } finally {
                                try {
                                    inputStream?.close()
                                    fileOutputStream?.close()
                                } catch (ignored: IOException) {
                                }
                            }
                        }
                    }.start()
                }

                override fun onError(t: Throwable?) {
                    weakHandler.post { onDownloadListener.onDownloadFailed(t) }
                }

                override fun onComplete() {
                    if (!isDisposed) {
                        dispose()
                    }
                }
            });
    }

}
