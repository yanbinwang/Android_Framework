package com.example.common.utils.file.factory

import android.annotation.SuppressLint
import android.os.Looper
import com.example.common.subscribe.BaseSubscribe
import com.example.common.utils.file.FileUtil
import com.example.common.utils.file.callback.OnDownloadListener
import com.example.common.utils.handler.WeakHandler
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subscribers.ResourceSubscriber
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.Executors

/**
 * author: wyb
 * 下载单例
 */
@SuppressLint("CheckResult")
class DownloadFactory private constructor() {
    private val weakHandler = WeakHandler(Looper.getMainLooper())
    private val executors = Executors.newSingleThreadExecutor();

    companion object {
        @JvmStatic
        val instance: DownloadFactory by lazy {
            DownloadFactory()
        }
    }

    fun download(downloadUrl: String, filePath: String, fileName: String, onDownloadListener: OnDownloadListener?) {
        FileUtil.deleteDir(filePath)
        BaseSubscribe.getDownload(downloadUrl)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : ResourceSubscriber<ResponseBody>() {

                override fun onStart() {
                    super.onStart()
                    weakHandler.post { onDownloadListener?.onStart() }
                }

                override fun onNext(responseBody: ResponseBody?) {
                    doResult(responseBody, null)
                }

                override fun onError(t: Throwable?) {
                    doResult(null, t)
                }

                private fun doResult(responseBody: ResponseBody?, throwable: Throwable?) {
                    if (null != responseBody) {
                        executors.execute {
                            var inputStream: InputStream? = null
                            var fileOutputStream: FileOutputStream? = null
                            try {
                                val file = File(FileUtil.isExistDir(filePath), fileName)
                                val buf = ByteArray(2048)
                                val total = responseBody.contentLength()
                                inputStream = responseBody.byteStream()
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
                                onComplete()
                            }
                        }
                        executors.isShutdown
                    } else {
                        weakHandler.post { onDownloadListener?.onFailed(throwable) }
                        onComplete()
                    }
                }

                override fun onComplete() {
                    weakHandler.post { onDownloadListener?.onComplete() }
                    if (!isDisposed) {
                        dispose()
                    }
                }
            })
    }

}