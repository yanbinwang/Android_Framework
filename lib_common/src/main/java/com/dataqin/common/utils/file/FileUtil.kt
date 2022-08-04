package com.dataqin.common.utils.file

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import androidx.core.content.FileProvider
import com.dataqin.base.utils.DateUtil
import com.dataqin.base.utils.LogUtil
import com.dataqin.base.utils.ToastUtil
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.constant.Constants
import java.io.*
import java.lang.ref.SoftReference
import java.text.DecimalFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by WangYanBin on 2020/7/1.
 * 文件工具类
 */
@SuppressLint("QueryPermissionsNeeded")
object FileUtil {
    private val weakHandler by lazy { WeakHandler(Looper.getMainLooper()) }

    /**
     * 是否安装了XXX应用
     */
    @JvmStatic
    fun isAvailable(context: Context, packageName: String): Boolean {
        val packages = context.packageManager.getInstalledPackages(0)
        for (i in packages.indices) {
            if (packages[i].packageName == packageName) return true
        }
        return false
    }

    /**
     * 是否Root-报错或获取失败都为未Root
     */
    @JvmStatic
    fun isRoot(): Boolean {
        var file: File
        val paths = arrayOf("/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/")
        try {
            for (element in paths) {
                file = File(element + "su")
                if (file.exists()) return true
            }
        } catch (ignored: Exception) {
        }
        return false
    }

    /**
     * 判断手机是否开启开发者模式
     */
    @JvmStatic
    fun isAdbEnabled(context: Context) = (Settings.Secure.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) > 0)

    /**
     * 判断下载目录是否存在,不存在则创建，并返回路径
     */
    @JvmStatic
    @Throws(IOException::class)
    fun isExistDir(filePath: String): String {
        val downloadFile = File(filePath)
        if (!downloadFile.mkdirs()) downloadFile.createNewFile()
        return downloadFile.absolutePath
    }

    /**
     * 复制文件到目标目录下
     */
    @JvmStatic
    @Throws(IOException::class)
    fun copyFile(srcPath: String, destPath: String) = copyFile(File(srcPath), File(destPath))

    @JvmStatic
    @Throws(IOException::class)
    fun copyFile(srcFile: File, destFile: File) {
        if (!destFile.exists()) destFile.createNewFile()
        FileInputStream(srcFile).channel.use { source ->
            FileOutputStream(destFile).channel.use { destination ->
                destination.transferFrom(source, 0, source.size())
            }
        }
    }

    /**
     * 删除文件
     */
    @JvmStatic
    fun deleteFile(filePath: String?) {
        if (TextUtils.isEmpty(filePath)) return
        val file = File(filePath!!)
        if (file.isFile && file.exists()) file.delete()
    }

    /**
     * 删除路径下的所有文件
     */
    @JvmStatic
    fun deleteDir(filePath: String) = deleteDirWithFile(File(filePath))

    @JvmStatic
    fun deleteDirWithFile(dir: File) {
        if (!dir.exists() || !dir.isDirectory) return
        for (file in dir.listFiles()!!) {
            if (file.isFile) file.delete() //删除所有文件
            else if (file.isDirectory) deleteDirWithFile(file) //递规的方式删除文件夹
        }
        dir.delete() //删除目录本身
    }

    /**
     * 将指定路径下的所有文件打成压缩包,如果路径在sd卡中，安卓11需要获取外部操作
     * File fileDir = new File(rootDir + "/DCIM/Screenshots");
     * File zipFile = new File(rootDir + "/" + taskId + ".zip");
     *
     * @param srcFilePath 要压缩的文件或文件夹路径
     * @param zipFilePath 压缩完成的Zip路径
     */
    @JvmStatic
    @Throws(Exception::class)
    fun zipFolder(srcFilePath: String, zipFilePath: String) {
        //创建ZIP
        val outputSteam = ZipOutputStream(FileOutputStream(zipFilePath))
        //创建文件
        val file = File(srcFilePath)
        //压缩
        zipFiles(file.parent + File.separator, file.name, outputSteam)
        //完成和关闭
        outputSteam.finish()
        outputSteam.close()
    }

    /**
     * 打包时打符合条件的文件生成zip
     */
    @JvmStatic
    @Throws(Exception::class)
    fun zipFolder(srcFilePath: String, zipFilePath: String, pathList: ArrayList<String>) {
        //创建ZIP
        val outputSteam = ZipOutputStream(FileOutputStream(zipFilePath))
        //创建文件
        val file = File(srcFilePath)
        //获取目标文件下的子类
        for (childPath in file.list()) {
            val childFile = File(childPath)
            val path = pathList.filter { childFile.name == File(it).name }
            if (path.isNotEmpty() && !TextUtils.isEmpty(path[0])) {
                log(" \n文件名：${childFile.name}满足要求，开始压缩")
                zipFiles("${file.parent}/${file.name}/", childPath, outputSteam)
            }
        }
        //完成和关闭
        outputSteam.finish()
        outputSteam.close()
    }

    @Throws(Exception::class)
    private fun zipFiles(folderPath: String, fileName: String, outputSteam: ZipOutputStream) {
        log(" \n压缩路径:$folderPath\n压缩文件名:$fileName")
        val file = File(folderPath + fileName)
        if (file.isFile) {
            val entry = ZipEntry(fileName)
            val inputStream = FileInputStream(file)
            outputSteam.putNextEntry(entry)
            var len: Int
            val buffer = ByteArray(4096)
            while (inputStream.read(buffer).also { len = it } != -1) {
                outputSteam.write(buffer, 0, len)
            }
            outputSteam.closeEntry()
        } else {
            //文件夹
            val fileList = file.list()
            //没有子文件和压缩
            if (fileList.isEmpty()) {
                val zipEntry = ZipEntry(fileName + File.separator)
                outputSteam.putNextEntry(zipEntry)
                outputSteam.closeEntry()
            }
            //子文件和递归
            for (i in fileList.indices) {
                zipFiles("$folderPath$fileName/", fileList[i], outputSteam)
            }
        }
    }

    /**
     * @param folderPath 要打成压缩包文件的路径
     * @param zipPath 压缩完成的Zip路径（包含压缩文件名）-"${Constants.SDCARD_PATH}/10086.zip"
     */
    @JvmStatic
    fun zipFolder(folderPath: String, zipPath: String, onThreadListener: OnThreadListener?) {
        Thread {
            weakHandler.post { onThreadListener?.onStart() }
            val fileDir = File(folderPath)
            val zipFile = File(zipPath)
            try {
                if (fileDir.exists()) zipFolder(fileDir.absolutePath, zipFile.absolutePath)
            } catch (e: Exception) {
                log("打包图片生成压缩文件异常: $e")
            } finally {
                weakHandler.post { onThreadListener?.onStop() }
            }
        }.start()
    }

    @JvmStatic
    fun zipFolder(folderPath: String, zipPath: String, pathList: ArrayList<String>, onThreadListener: OnThreadListener?) {
        Thread {
            weakHandler.post { onThreadListener?.onStart() }
            val fileDir = File(folderPath)
            val zipFile = File(zipPath)
            try {
                if (fileDir.exists()) zipFolder(fileDir.absolutePath, zipFile.absolutePath, pathList)
            } catch (e: Exception) {
                log("打包图片生成压缩文件异常: $e")
            } finally {
                weakHandler.post { onThreadListener?.onStop() }
            }
        }.start()
    }

    /**
     * 将bitmap存成文件至指定目录下-读写权限
     * BitmapFactory.decodeResource(resources, R.mipmap.img_qr_code)
     */
    @JvmOverloads
    @JvmStatic
    fun saveBitmap(context: Context, bitmap: Bitmap, root: String = "${Constants.APPLICATION_FILE_PATH}/图片", formatJpg: Boolean = true, quality: Int = 100): Boolean {
        try {
            val storeDir = File(root)
            if (!storeDir.mkdirs()) storeDir.createNewFile()//需要权限
            val file = File(storeDir, DateUtil.getDateTime(DateUtil.EN_YMDHMS, Date()) + if (formatJpg) ".jpg" else ".png")
            //通过io流的方式来压缩保存图片
            val fileOutputStream = FileOutputStream(file)
            val result = bitmap.compress(if (formatJpg) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG, quality, fileOutputStream)//png的话100不响应，但是可以维持图片透明度
            fileOutputStream.flush()
            fileOutputStream.close()
            //保存图片后发送广播通知更新数据库
            MediaStore.Images.Media.insertImage(context.contentResolver, file.absolutePath, file.name, null)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://${file.path}")))
            return result
        } catch (ignored: Exception) {
        } finally {
            bitmap.recycle()
        }
        return false
    }

    /**
     * 保存图片
     */
    @JvmStatic
    fun saveBitmap(context: Context, bitmap: Bitmap, onThreadListener: OnThreadListener?) {
        weakHandler.post { onThreadListener?.onStart() }
        Thread {
            val type = saveBitmap(context, bitmap)
            weakHandler.post {
                ToastUtil.mackToastSHORT(if (type) "保存成功" else "保存失败", context)
                onThreadListener?.onStop()
            }
        }.start()
    }

    /**
     * 保存pdf文件存成图片形式
     */
    @JvmOverloads
    @JvmStatic
    fun savePdfBitmap(context: Context, file: File, index: Int = 0, onThreadListener: OnThreadListener?) {
        val renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
        val page = renderer.openPage(index)//选择渲染哪一页的渲染数据
        val width = page.width
        val height = page.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val rent = Rect(0, 0, width, height)
        page.render(bitmap, rent, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        renderer.close()
        saveBitmap(context, bitmap, onThreadListener)
    }

    /**
     * base64文件流的形式加载文件，需要先下载，之后在放置
     */
    @JvmStatic
    fun saveBase64(base64: String, suffix: String, root: String = "${Constants.APPLICATION_FILE_PATH}/缓存", clear: Boolean = true, onThreadListener: OnThreadListener?) {
        Thread {
            weakHandler.post { onThreadListener?.onStart() }
            if (clear) deleteDir(root)
            val storeDir = File(root)
            if (!storeDir.mkdirs()) storeDir.createNewFile()
            val file = File(storeDir, "${System.currentTimeMillis()}_cache${suffix}")
            val pdfAsBytes = Base64.decode(base64, 0)
            val fileOutputStream: FileOutputStream?
            try {
                fileOutputStream = FileOutputStream(file, false)
                fileOutputStream.write(pdfAsBytes)
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: Exception) {
            } finally {
                weakHandler.post { onThreadListener?.onStop(file.absolutePath) }
            }
        }.start()
    }

    /**
     * 读取文件到文本（文本，找不到文件或读取错返回null）
     */
    @JvmStatic
    fun readText(filePath: String): String? {
        val file = File(filePath)
        if (file.exists()) {
            try {
                val stringBuilder = StringBuilder()
                var str: String?
                val bufferedReader = BufferedReader(InputStreamReader(FileInputStream(file)))
                while (bufferedReader.readLine().also { str = it } != null) stringBuilder.append(str)
                return stringBuilder.toString()
            } catch (ignored: Exception) {
            }
        }
        return null
    }

    /**
     * 发送文件
     * image -> 图片
     */
    @JvmOverloads
    @JvmStatic
    fun sendFile(context: Context, filePath: String, type: String? = "*/*") {
        val file = File(filePath)
        if (!file.exists()) {
            ToastUtil.mackToastSHORT("文件路径错误", context)
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, "${Constants.APPLICATION_ID}.fileProvider", file))
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, file)
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = type//此处可发送多种文件
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, "分享文件"))
    }

    /**
     * 打开压缩包
     */
    @JvmStatic
    fun openZip(context: Context, filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val file = File(filePath)
            val contentUri = FileProvider.getUriForFile(context, "${Constants.APPLICATION_ID}.fileProvider", file)
            intent.setDataAndType(contentUri, "application/x-zip-compressed")
        } else {
            intent.setDataAndType(Uri.parse("file://$filePath"), "application/x-zip-compressed")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * 打开world
     */
    fun openWorld(context: Context, filePath: String) {
        val file = File(filePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            uri = FileProvider.getUriForFile(context, "${Constants.APPLICATION_ID}.fileProvider", file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
        } else uri = Uri.parse("file://$file")
        intent.setDataAndType(uri, "application/msword")
        context.startActivity(intent)
    }

    /**
     * 转换文件大小格式
     */
    @JvmStatic
    fun formatFileSize(fileSize: Long): String {
        val format = DecimalFormat("#.00")
        return when {
            fileSize < 1024 -> "${format.format(fileSize.toDouble())}B"
            fileSize < 1048576 -> "${format.format(fileSize.toDouble() / 1024)}K"
            fileSize < 1073741824 -> "${format.format(fileSize.toDouble() / 1048576)}M"
            else -> "${format.format(fileSize.toDouble() / 1073741824)}G"
        }
    }

    /**
     * 获取文件大小
     */
    @JvmStatic
    fun getFileSize(file: File): Long {
        var size: Long = 0
        for (mFile in file.listFiles()) {
            size = if (mFile.isDirectory) {
                size + getFileSize(mFile)
            } else {
                size + mFile.length()
            }
        }
        return size
    }

    /**
     * 获取当前app的图标
     */
    @JvmStatic
    fun getApplicationIcon(context: Context): Bitmap? {
        try {
            val drawable = context.packageManager.getApplicationIcon(Constants.APPLICATION_ID)
            val bitmap = SoftReference(Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565))
            val canvas = Canvas(bitmap.get()!!)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            drawable.draw(canvas)
            return bitmap.get()
        } catch (ignored: Exception) {
        }
        return null
    }

    /**
     * 获取安装跳转的行为
     */
    @JvmStatic
    fun getSetupApk(context: Context, apkFilePath: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val file = File(apkFilePath)
            val contentUri = FileProvider.getUriForFile(context, "${Constants.APPLICATION_ID}.fileProvider", file)
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.parse("file://$apkFilePath"), "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }

    /**
     * 获取手机cpu信息-报错或获取失败显示暂无
     */
    @JvmStatic
    fun getCpuInfo(): String {
        try {
            val result = BufferedReader(FileReader("/proc/cpuinfo")).readLine().split(":\\s+".toRegex(), 2).toTypedArray()[1]
            return if ("0" == result) "暂无" else result
        } catch (ignored: Exception) {
        }
        return "暂无"
    }

    /**
     * 日志
     */
    private fun log(msg: String) = LogUtil.e("FileUtil", msg)

    interface OnThreadListener {

        /**
         * 线程开始执行
         */
        fun onStart()

        /**
         * 线程停止执行
         */
        fun onStop(path: String? = null)

    }

}