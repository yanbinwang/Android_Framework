package com.example.common.utils.helper.update

import android.content.Context
import com.example.common.constant.Constants
import com.example.common.utils.file.callback.OnDownloadListener
import com.example.common.utils.file.factory.DownloadFactory
import com.example.common.utils.helper.update.callback.OnUpdateCallBack
import com.example.common.utils.helper.permission.OnPermissionCallBack
import com.example.common.utils.helper.permission.PermissionHelper
import com.yanzhenjie.permission.runtime.Permission
import io.reactivex.disposables.Disposable

/**
 * Created by WangYanBin on 2020/7/27.
 * 下载应用工具类,应用可能采取弹窗动画等形式
 */
class UpdateHelper private constructor() {

    companion object {
        @JvmStatic
        val instance: UpdateHelper by lazy {
            UpdateHelper()
        }
    }

    //统一下载，进入app以及设置中的检测版本皆是一样的逻辑，弹框-检测权限-开启下载
    fun download(context: Context?, downloadUrl: String, onUpdateCallBack: OnUpdateCallBack?) {
        PermissionHelper.with(context)
            .getPermissions(Permission.Group.STORAGE)
            .setPermissionCallBack(object : OnPermissionCallBack {

                override fun onPermissionListener(isGranted: Boolean) {
                    if (isGranted) {
                        val filePath = Constants.APPLICATION_FILE_PATH + "/安装包"
                        val fileName = Constants.APPLICATION_NAME + ".apk"
                        val disposable: Disposable = DownloadFactory.instance.download(downloadUrl, filePath, fileName, object : OnDownloadListener {

                                override fun onDownloadSuccess(path: String?) {

                                }

                                override fun onDownloading(progress: Int) {

                                }

                                override fun onDownloadFailed(e: Throwable?) {

                                }

                                override fun onDownloadComplete() {
                                    onUpdateCallBack?.onDownloadComplete()
                                }

                            })
                        onUpdateCallBack?.onDownloadStart(disposable)
                    } else {
                        onUpdateCallBack?.onDownloadComplete()
                    }
                }
            })
    }

}