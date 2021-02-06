package com.dataqin.testnew.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.handler.WeakHandler
import com.dataqin.testnew.databinding.ViewCertificateBinding
import java.util.concurrent.Executors


/**
 *  Created by wangyanbin
 *  生成证书类
 */
object CertificateHelper {
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }
    private val executors by lazy { Executors.newSingleThreadExecutor() }

    fun create(context: Context, result: String, onCertificateListener: OnCertificateListener?) {
        onCertificateListener?.onStart()
        val binding = ViewCertificateBinding.inflate(LayoutInflater.from(context))
        binding.tvContext.text = result
        loadLayout(binding.root)
        executors.execute {
            try {
                val bitmap = loadBitmap(binding.root)
                weakHandler.post { onCertificateListener?.onResult(bitmap!!) }
            } catch (e: Exception) {
            } finally {
                weakHandler.post { onCertificateListener?.onComplete() }
            }
        }
        executors.isShutdown
    }

//    private fun layoutView(activity: Activity, view: View) {
//        val metric = DisplayMetrics()
//        activity.windowManager.defaultDisplay.getMetrics(metric)
//        val width = metric.widthPixels//屏幕宽度（像素）
//        val height = metric.heightPixels//屏幕高度（像素）
//        view.layout(0, 0, width, height)
//        val measuredWidth: Int = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
//        val measuredHeight: Int = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
//        view.measure(measuredWidth, measuredHeight)
//        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
//    }

    /**
     * 当measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局
     * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小
     */
    private fun loadLayout(view: View) {
        //整个View的大小 参数是左上角 和右下角的坐标
        view.layout(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)
        val measuredWidth: Int = View.MeasureSpec.makeMeasureSpec(Constants.SCREEN_WIDTH, View.MeasureSpec.EXACTLY)
        val measuredHeight: Int = View.MeasureSpec.makeMeasureSpec(Constants.SCREEN_HEIGHT, View.MeasureSpec.EXACTLY)
        view.measure(measuredWidth, measuredHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    //如果不设置canvas画布为白色，则生成透明
    private fun loadBitmap(view: View): Bitmap? {
        val width = view.width
        val height = view.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.layout(0, 0, width, height)
        view.draw(canvas)
        return bitmap
    }

    interface OnCertificateListener {

        fun onStart()

        fun onResult(bitmap: Bitmap)

        fun onComplete()

    }

}