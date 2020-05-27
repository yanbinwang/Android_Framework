package com.example.common.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.example.common.BaseApplication
import com.example.common.BuildConfig
import com.example.common.constant.Constants
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


/**
 * author: wyb
 * date: 2017/9/21.
 * 异常抓包类，处理闪退的异常文件
 */
@SuppressLint("StaticFieldLeak")
class CrashHandler : Thread.UncaughtExceptionHandler {
    private var context: Context? = null
    private var logFile: File? = null //log文件
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    private val TAG = "CrashHandler" //文件name

    companion object {
        private var instance: CrashHandler? = null

        @Synchronized
        fun getInstance(): CrashHandler {
            if (instance == null) {
                instance = CrashHandler()
            }
            return instance!!
        }
    }

    init {
        context = BaseApplication.getInstance().applicationContext
        //获取默认异常处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //将此类设为默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        if (!handleException(throwable)) {
            //未经过人为处理,则调用系统默认处理异常,弹出系统强制关闭的对话框
            mDefaultHandler!!.uncaughtException(thread, throwable)
        } else {
            //已经人为处理,系统自己退出
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }
    }

    //抓包后生成log日志文件
    private fun handleException(throwable: Throwable?): Boolean {
        if (throwable == null) {
            return false
        }
        handleErrorMessage(throwable)
        return true
    }

    //根据日期存储错误日志
    @Synchronized
    private fun handleErrorMessage(throwable: Throwable) {
        val stringBuilder = StringBuilder()
        val causeString = getThrowableInfo(throwable)
        stringBuilder.append(causeString)
        val result = stringBuilder.toString()
        if (!TextUtils.isEmpty(result)) {
            LogUtil.e(TAG, result) //给出错误的log提示
            //如果具备权限，写入本地
            if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (FileUtil.hasSDCard()) {
                        logFile = File(FileUtil.createCacheDir() + File.separator + Constants.APPLICATION_NAME + "_v" + BuildConfig.VERSION_NAME + "_exception_" + SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault()).format(Date()) + ".log")
                        logFile!!.createNewFile() //6.0+的系统需要写入权限才能生成对应文件
                    }

                    val bufferedWriter = BufferedWriter(FileWriter(logFile, true))
                    bufferedWriter.write(result)
                    bufferedWriter.write("\n")
                    bufferedWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getThrowableInfo(ex: Throwable): String {
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter) // 写入错误信息
        var cause: Throwable? = ex.cause
        while (null != cause) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        return writer.toString()
    }

}