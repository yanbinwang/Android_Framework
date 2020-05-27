package com.example.common.http.upload

import android.content.Context
import com.example.common.utils.CompressUtil
import com.example.common.widget.dialog.LoadingDialog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

/**
 * author: wyb
 * date: 2017/10/9.
 * 图片上传工具类
 */
class FilesUploadUtil(private val context: Context) {
    private var loadingDialog: LoadingDialog = LoadingDialog(context)
    private var onFilesUploadListener: OnFilesUploadListener? = null

    fun toUpload(filePaths: ArrayList<String>) {
        toUpload(filePaths, 0)
    }

    //手动配置压缩比例
    fun toUpload(filePaths: ArrayList<String>, fileMaxSize: Long = 0) {
        val parts = ArrayList<MultipartBody.Part>()
        for (path in filePaths) {
            val file = File(path)
            //对传入的图片进行压缩
            var fileCompress: File
            fileCompress = if (0L == fileMaxSize) {
                CompressUtil.scale(context, file)
            } else {
                CompressUtil.scale(context, file, fileMaxSize)
            }
            //部分手机图片需要进行旋转
            fileCompress = CompressUtil.degreeImage(context, fileCompress)
            val requestFile: RequestBody
            requestFile = if (file.name.endsWith(".png")) {
                RequestBody.create(MediaType.parse("image/png"), fileCompress)
            } else {
                RequestBody.create(MediaType.parse("image/jpeg"), fileCompress)
            }
            val filePart = MultipartBody.Part.createFormData("file[]", fileCompress.name, requestFile)
            parts.add(filePart)
        }
        loadingDialog.show(false)
//        BaseSubscribe.getUploadFile(RequestCode.CODE_406, parts, object : RxSubscribe<UploadBean>() {
//            override fun onSuccess(data: UploadBean?) {
//                if (null != onUploadListener) {
//                    val list = data!!.list
//                    if (null != list && list.isNotEmpty()) {
//                        onUploadListener!!.onUploadImageSuccess(list)
//                    } else {
//                        onUploadListener!!.onUploadImageFailed()
//                    }
//                }
//            }
//
//            override fun onFailed(e: Throwable?, msg: String?) {
//                if (null != onUploadListener) {
//                    onUploadListener!!.onUploadImageFailed()
//                }
//            }
//
//            override fun onFinish() {
//                loadingDialog.hide()
//            }
//        })
    }

    //上传图片监听
    fun setOnUploadImageListener(onFilesUploadListener: OnFilesUploadListener) {
        this.onFilesUploadListener = onFilesUploadListener
    }

}