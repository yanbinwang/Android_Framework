package com.dataqin.common.imageloader.album

import android.app.Activity
import androidx.core.content.ContextCompat
import com.dataqin.base.utils.ToastUtil
import com.dataqin.common.R
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.RequestCode
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import com.yanzhenjie.durban.Controller
import com.yanzhenjie.durban.Durban
import java.lang.ref.WeakReference

/**
 * author: wyb
 * date: 2017/9/29.
 * 调用该类之前需检测权限，activity属性设为
 * android:configChanges="orientation|keyboardHidden|screenSize"
 */
class AlbumHelper(activity: Activity) {
    private val weakActivity = WeakReference(activity)
    private var onAlbumListener: OnAlbumListener? = null //单选回调监听

    /**
     * 跳转至相机-拍照
     */
    fun takePicture(filePath: String, hasTailor: Boolean = false): AlbumHelper {
        Album.camera(weakActivity.get())
            .image()
            .filePath(filePath)
            .onResult { if (hasTailor) toTailor(it) else onAlbumListener?.onAlbum(it) }
            .start()
        return this
    }

    /**
     * 跳转至相机-录像(时间不一定能指定，大多数手机不兼容)
     */
    fun recordVideo(filePath: String, duration: Long = 1000 * 60 * 60): AlbumHelper {
        Album.camera(weakActivity.get())
            .video()
            .filePath(filePath)
            .quality(1)//视频质量, [0, 1].
            .limitDuration(duration)//视频的最长持续时间以毫秒为单位
//                           .limitBytes(Long.MAX_VALUE)//视频的最大大小，以字节为单位
            .onResult { onAlbumListener?.onAlbum(it) }
            .start()
        return this
    }

    /**
     * 跳转至相册
     */
    fun imageSelection(hasCamera: Boolean = true, hasTailor: Boolean = false): AlbumHelper {
        //选择图片
        Album.image(weakActivity.get())
            //多选模式为：multipleChoice,单选模式为：singleChoice()
            .singleChoice()
            //状态栏是深色背景时的构建newDarkBuilder ，状态栏是白色背景时的构建newLightBuilder
            .widget(Widget.newDarkBuilder(weakActivity.get())
                    //标题 ---标题颜色只有黑色白色
                    .title(" ")
                    //状态栏颜色
                    .statusBarColor(ContextCompat.getColor(weakActivity.get()!!, R.color.grey_333333))
                    //Toolbar颜色
                    .toolBarColor(ContextCompat.getColor(weakActivity.get()!!, R.color.grey_333333))
                    .build())
            //是否具备相机
            .camera(hasCamera)
            //页面列表的列数
            .columnCount(3)
            .onResult {
                val resultSize = it[0].size
                if (resultSize > 10 * 1024 * 1024) {
                    ToastUtil.mackToastSHORT(weakActivity.get()!!.getString(R.string.toast_album_choice), weakActivity.get()!!.applicationContext)
                    return@onResult
                }
                if (hasTailor) toTailor(it[0].path) else onAlbumListener?.onAlbum(it[0].path)
            }.start()
        return this
    }

    /**
     * 开始裁剪
     */
    private fun toTailor(vararg imagePathArray: String) {
        Durban.with(weakActivity.get())
            //裁剪界面的标题
            .title(" ")
            //状态栏颜色
            .statusBarColor(ContextCompat.getColor(weakActivity.get()!!, R.color.grey_333333))
            //Toolbar颜色
            .toolBarColor(ContextCompat.getColor(weakActivity.get()!!, R.color.grey_333333))
            //图片路径list或者数组
            .inputImagePaths(*imagePathArray)
            //图片输出文件夹路径
            .outputDirectory("${Constants.APPLICATION_FILE_PATH}/裁剪图片")
            //裁剪图片输出的最大宽高
            .maxWidthHeight(500, 500)
            //裁剪时的宽高比
            .aspectRatio(1f, 1f)
            //图片压缩格式：JPEG、PNG
            .compressFormat(Durban.COMPRESS_JPEG)
            //图片压缩质量，请参考：Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)
            .compressQuality(90)
            //裁剪时的手势支持：ROTATE, SCALE, ALL, NONE.
            .gesture(Durban.GESTURE_SCALE).controller(
                Controller.newBuilder()
                    //是否开启控制面板
                    .enable(false)
                    //是否有旋转按钮
                    .rotation(true)
                    //旋转控制按钮上面的标题
                    .rotationTitle(true)
                    //是否有缩放按钮
                    .scale(true)
                    //缩放控制按钮上面的标题
                    .scaleTitle(true)
                    .build())
            //创建控制面板配置
            .requestCode(RequestCode.PHOTO_REQUEST).start()
    }

    fun setAlbumListener(onAlbumListener: OnAlbumListener): AlbumHelper {
        this.onAlbumListener = onAlbumListener
        return this
    }

    companion object {
        @JvmStatic
        fun with(activity: Activity?): AlbumHelper {
            return AlbumHelper(activity!!)
        }
    }

}