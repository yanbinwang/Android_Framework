package com.ow.basemodule.utils.http.download

import android.annotation.SuppressLint
import android.os.Looper
import com.ow.basemodule.subscribe.BaseApi
import com.ow.basemodule.utils.FileUtil
import com.ow.basemodule.utils.http.download.callback.OnDownloadFactoryListener
import com.ow.framework.net.RetrofitFactory
import com.ow.framework.utils.LogUtil
import com.ow.framework.widget.WeakHandler
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

    fun download(downloadUrl: String, saveDir: String, fileName: String, onDownloadFactoryListener: OnDownloadFactoryListener) {
        FileUtil.deleteDir(saveDir)
        RetrofitFactory.getInstance().create(BaseApi::class.java).download(downloadUrl).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : ResourceSubscriber<ResponseBody>() {
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
                                doLoading(progress, onDownloadFactoryListener)
                            }
                            fileOutputStream.flush()
                            doSuccess(file, onDownloadFactoryListener)
                        } catch (e: Exception) {
                            doFailed(e, onDownloadFactoryListener)
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
                doFailed(t, onDownloadFactoryListener)
            }

            override fun onComplete() {
                if (!isDisposed) {
                    dispose()
                }
            }
        })
    }

    //文件下载中
    private fun doLoading(progress: Int?, onDownloadFactoryListener: OnDownloadFactoryListener?) {
        LogUtil.e("DownloadFactory", progress.toString())
        weakHandler.post {
            onDownloadFactoryListener?.onDownloading(progress)
        }
    }

    //文件下载成功
    private fun doSuccess(file: File?, onDownloadFactoryListener: OnDownloadFactoryListener?) {
        weakHandler.post {
            onDownloadFactoryListener?.onDownloadSuccess(file!!.path)
        }
    }

    //文件下载失败
    private fun doFailed(e: Throwable?, onDownloadFactoryListener: OnDownloadFactoryListener?) {
        weakHandler.post {
            onDownloadFactoryListener?.onDownloadFailed(e)
        }
    }

}
