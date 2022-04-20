package com.dataqin.testnew.utils

import android.util.Base64
import com.dataqin.common.bus.RxBus
import com.dataqin.common.bus.RxEvent
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

/**
 * 接口中包含base64字段，拿取对象跳转页面会直接闪退
 * 故而在接口层面拿到对象后，先调工具类中线程池下载对应文件，并在结束后发送广播通知到页面
 * 接口对象在传入对应base64字段到工具类后，立即清空对应字段，然后传输到页面
 */
class Base64Helper {
    private var executors = Executors.newSingleThreadExecutor()

    @Synchronized
    private fun base64(base64: String, suffix: String, root: String = "${Constants.APPLICATION_FILE_PATH}/详情缓存") {
        executors.execute {
            try {
                FileUtil.deleteDir(root)
                val storeDir = File(root)
                if (!storeDir.mkdirs()) storeDir.createNewFile()
                val fileCache = File(storeDir, "${System.currentTimeMillis()}_cache${suffix}")
                val decodeBytes = Base64.decode(base64, 0)
                val outputStream: FileOutputStream?
                outputStream = FileOutputStream(fileCache, false)
                outputStream.write(decodeBytes)
                outputStream.flush()
                outputStream.close()
                RxBus.instance.post(RxEvent(Constants.APP_BASE64_UPDATE, fileCache.absoluteFile))
            } catch (e: Exception) {
            }
        }
        executors.isShutdown
    }

}