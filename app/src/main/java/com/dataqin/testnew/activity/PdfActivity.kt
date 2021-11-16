package com.dataqin.testnew.activity

import android.R
import android.graphics.Bitmap
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.download.DownloadFactory
import com.dataqin.common.utils.file.download.OnDownloadListener
import com.dataqin.testnew.databinding.ActivityPdfBinding
import java.io.File
import android.os.ParcelFileDescriptor

import android.graphics.pdf.PdfRenderer
import android.R.attr.bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import com.dataqin.common.utils.file.FileUtil


/**
 * 装载pdf文件的页面
 */
@Route(path = ARouterPath.PdfActivity)
class PdfActivity : BaseTitleActivity<ActivityPdfBinding>() {

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("PDF页").getDefault()
        downloadPdf()
    }

    private fun downloadPdf() {
        val url = "http://pdf.dfcfw.com/pdf/H2_AN201807051163584888_1.pdf"
        val filePath = "${Constants.APPLICATION_FILE_PATH}/PDF证书"
        val path = url.split("/")
        val fileName = path[path.size - 1]
        DownloadFactory.instance.download(url, filePath, fileName, object : OnDownloadListener() {
            override fun onStart() {
                super.onStart()
                showDialog()
            }

            override fun onSuccess(path: String?) {
                super.onSuccess(path)
                try {
//                    FileUtil.sendFile(activity.get()!!,path!!)
//                    val file = File(path)
//                    binding.pdfContainer.fromFile(file)
//                        .defaultPage(1)//默认显示第1页
//                        .showMinimap(false) //pdf放大的时候，是否在屏幕的右上角生成小地图
//                        .swipeVertical(true) //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
//                        .enableSwipe(true) //是否允许翻页，默认是允许翻页
//                        .onLoad {
//                            try {
//                                val renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
//                                val page = renderer.openPage(0)//选择渲染哪一页的渲染数据
//                                val width = page.width
//                                val height = page.height
//                                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//                                val canvas = Canvas(bitmap)
//                                canvas.drawColor(Color.WHITE)
//                                canvas.drawBitmap(bitmap, 0f, 0f, null)
//                                val r =  Rect(0, 0, width, height);
//                                page.render(bitmap, r, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//                                page.close()
//                                renderer.close()
//                                //放置本地，观测
//                                FileUtil.saveBitmapThread(baseContext, bitmap, object : FileUtil.OnThreadListener {
//                                    override fun onStart() {
//                                    }
//
//                                    override fun onStop(path: String?) {
//                                    }
//                                })
//                            }catch (e:Exception){}
//                        }.load()
                } catch (e: Exception) {
                }
            }

            override fun onComplete() {
                super.onComplete()
                hideDialog()
            }
        })
    }

}