package com.example.common.http.download

import android.annotation.SuppressLint
import android.os.Looper
import com.example.common.http.factory.RetrofitFactory
import com.example.common.subscribe.BaseApi
import com.example.common.utils.FileUtil
import com.example.framework.widget.WeakHandler
import io.reactivex.android.schedulers.AndroidSchedulers
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
class DownloadFactory {
    private val weakHandler: WeakHandler = WeakHandler(Looper.getMainLooper())

    companion object {
        private var instance: DownloadFactory? = null

        @Synchronized
        fun getInstance(): DownloadFactory {
            if (instance == null) {
                instance = DownloadFactory()
            }
            return instance!!
        }
    }

    fun download(downloadUrl: String, saveDir: String, fileName: String, onDownloadListener: OnDownloadListener) {
        FileUtil.deleteDir(saveDir)
        RetrofitFactory.getInstance().create(BaseApi::class.java).download(downloadUrl)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ResourceSubscriber<ResponseBody>() {
                override fun onNext(responseBody: ResponseBody?) {
                    object : Thread() {
                        override fun run() {
                            var inputStream: InputStream? = null
                            val buf = ByteArray(2048)
                            var len: Int
                            var fileOutputStream: FileOutputStream? = null
                            try {
                                inputStream = responseBody!!.byteStream()
                                val total = responseBody.contentLength()
                                val file = File(FileUtil.isExistDir(saveDir), fileName)
                                fileOutputStream = FileOutputStream(file)
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
                                onError(e)
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
            })
    }

}
