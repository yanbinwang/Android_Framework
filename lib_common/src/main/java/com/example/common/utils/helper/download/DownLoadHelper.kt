package com.example.common.utils.helper.download

import com.example.common.constant.Constants
import com.example.common.utils.file.callback.OnDownloadListener
import com.example.common.utils.file.factory.DownloadFactory
import io.reactivex.disposables.Disposable

/**
 * Created by WangYanBin on 2020/7/27.
 * 下载应用工具类
 */
class DownLoadHelper {

    companion object {
        @JvmStatic
        val instance: DownLoadHelper by lazy {
            DownLoadHelper()
        }
    }

    fun download(fileName: String, downloadUrl: String): Disposable? {
        return DownloadFactory.instance.download(downloadUrl, Constants.APPLICATION_FILE_PATH + "/安装包", fileName, object : OnDownloadListener {

                override fun onDownloadSuccess(path: String?) {

                }

                override fun onDownloading(progress: Int) {

                }

                override fun onDownloadFailed(e: Throwable?) {

                }

            })
    }

}