package com.example.common.utils.helper.download

import android.content.Context
import com.example.common.BaseApplication
import com.example.common.constant.Constants
import com.example.common.utils.file.callback.OnDownloadListener
import com.example.common.utils.file.factory.DownloadFactory
import com.example.common.utils.helper.download.callback.DownloadState
import io.reactivex.disposables.Disposable

/**
 * Created by WangYanBin on 2020/7/27.
 * 下载类，目前对下载应用和图片做了区分
 */
class DownLoadHelper {
    private val context: Context? = BaseApplication.getInstance().applicationContext

    companion object {
        @JvmStatic
        val instance: DownLoadHelper by lazy {
            DownLoadHelper()
        }
    }

    fun download(state: DownloadState, fileName: String, downloadUrl: String): Disposable? {
        val filePath: String = when (state) {
            DownloadState.APP -> Constants.APPLICATION_FILE_PATH + "/安装包"
            DownloadState.PICTURE -> Constants.APPLICATION_FILE_PATH + "/图片"
        }
        return DownloadFactory.instance.download(downloadUrl, filePath, fileName, object : OnDownloadListener {

                override fun onDownloadSuccess(path: String?) {
                    doSuccessResult(state, path)
                }

                override fun onDownloading(progress: Int) {
                    doLoadingResult(state, progress)
                }

                override fun onDownloadFailed(e: Throwable?) {
                    doFailedResult(state, e)
                }

            })
    }

    private fun doSuccessResult(state: DownloadState, path: String?) {

    }

    private fun doLoadingResult(state: DownloadState, progress: Int) {

    }

    private fun doFailedResult(state: DownloadState, e: Throwable?) {

    }

}