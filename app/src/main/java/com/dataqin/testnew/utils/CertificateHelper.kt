package com.dataqin.testnew.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.dataqin.common.utils.handler.WeakHandler
import com.dataqin.testnew.R
import java.util.concurrent.Executors


/**
 *  Created by wangyanbin
 *  生成证书类
 */
@SuppressLint("InflateParams")
object CertificateHelper {
    private val weakHandler = WeakHandler(Looper.getMainLooper())
    private val executors = Executors.newSingleThreadExecutor()

    fun create(activity: Activity, result: String, onCertificateListener: OnCertificateListener?) {
        onCertificateListener?.onStart()
        val view = LayoutInflater.from(activity).inflate(R.layout.view_certificate, null)
//        val svContainer = view.findViewById<ScrollView>(R.id.sv_container)
//        val rlContainer = view.findViewById<RelativeLayout>(R.id.rl_container)
        val tvContext = view.findViewById<TextView>(R.id.tv_context)
        tvContext.text = result
        layoutView(activity, view)
        executors.execute {
            try {
                val bitmap = loadBitmapFromView(view)
                weakHandler.post {
                    onCertificateListener?.onResult(bitmap!!)
                }
            } catch (e: Exception) {
            } finally {
                weakHandler.post {
                    onCertificateListener?.onComplete()
                }
            }
        }
        executors.isShutdown
    }

    private fun layoutView(activity: Activity, view: View) {
        val metric = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metric)
        val width = metric.widthPixels//屏幕宽度（像素）
        val height = metric.heightPixels//屏幕高度（像素）
        //整个View的大小 参数是左上角 和右下角的坐标
        view.layout(0, 0, width, height)
//        val measuredWidth: Int = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
//        val measuredHeight: Int = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST)
        val measuredWidth: Int = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight: Int = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        /**
         * 当measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小
         */
        view.measure(measuredWidth, measuredHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    private fun loadBitmapFromView(view: View): Bitmap? {
        val w = view.width
        val h = view.height
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        //如果不设置canvas画布为白色，则生成透明
        view.layout(0, 0, w, h)
        view.draw(c)
        return bmp
    }

    interface OnCertificateListener {

        fun onStart()

        fun onResult(bitmap: Bitmap)

        fun onComplete()

    }

}