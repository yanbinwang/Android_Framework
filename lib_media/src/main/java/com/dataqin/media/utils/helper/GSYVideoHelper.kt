package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dataqin.base.utils.TimerHelper
import com.dataqin.common.imageloader.ImageLoader
import com.dataqin.media.R
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.cache.ProxyCacheManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  视频播放器工具类
 *  默认-开锁可随屏幕翻转角度
 */
@SuppressLint("StaticFieldLeak")
object GSYVideoHelper : DefaultLifecycleObserver {
    private var retryWithPlay = false
    private var videoType: VideoType = VideoType.MOBILE
    private var weakActivity: WeakReference<Activity>? = null
    private var imgCover: ImageView? = null
    private var player: StandardGSYVideoPlayer? = null
    private var orientationUtils: OrientationUtils? = null
    private val gSYSampleCallBack by lazy { object : GSYSampleCallBack() {
        override fun onQuitFullscreen(url: String, vararg objects: Any) {
            super.onQuitFullscreen(url, *objects)
            if (videoType == VideoType.PC) orientationUtils?.backToProtVideo()
        }

        override fun onPlayError(url: String?, vararg objects: Any?) {
            super.onPlayError(url, *objects)
            if (!retryWithPlay) {
                retryWithPlay = true
                player?.isEnabled = false
                //允许硬件解码，装载IJK播放器内核
                GSYVideoType.enableMediaCodecTexture()
                PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
                CacheFactory.setCacheManager(ProxyCacheManager::class.java)
                TimerHelper.schedule(object : TimerHelper.OnTaskListener {
                    override fun run() {
                        player?.isEnabled = true
                        player?.startPlayLogic()
                    }
                })
            }
        }
    }}

    enum class VideoType {
        MOBILE, PC
    }

    /**
     * activity-视频对应页面
     * player-视频对应播放器
     * fullScreen-是否全屏（默认不全屏）
     */
    @JvmOverloads
    @JvmStatic
    fun initialize(activity: Activity, standardGSYVideoPlayer: StandardGSYVideoPlayer, fullScreen: Boolean = false, videoType: VideoType = VideoType.MOBILE) {
        //基础属性赋值
        this.weakActivity = WeakReference(activity)
        this.player = standardGSYVideoPlayer
        this.videoType = videoType
        //屏幕展示效果
        GSYVideoType.setShowType(if (videoType == VideoType.MOBILE && !fullScreen) GSYVideoType.SCREEN_MATCH_FULL else GSYVideoType.SCREEN_TYPE_DEFAULT)
        //设置底层渲染,关闭硬件解码
        GSYVideoType.setRenderType(GSYVideoType.GLSURFACE)
        GSYVideoType.disableMediaCodecTexture()
        //默认采用exo内核，播放报错则切ijk内核
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
        player?.titleTextView?.visibility = View.GONE
        player?.backButton?.visibility = View.GONE
        //设置播放器初始化的蒙层
        var thumbImageView: View = ImageView(weakActivity?.get())
        if (videoType != VideoType.PC || (videoType == VideoType.MOBILE && fullScreen)) {
            thumbImageView = LayoutInflater.from(standardGSYVideoPlayer.context).inflate(R.layout.view_video_cover, null)
            this.imgCover = thumbImageView.findViewById(R.id.iv_cover)
            this.imgCover?.scaleType = ImageView.ScaleType.FIT_XY
        } else {
            this.imgCover = thumbImageView as ImageView
            this.imgCover?.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        player?.thumbImageView = thumbImageView
        //设置按钮的一些显影
        if (fullScreen) {
            if (videoType == VideoType.PC) {
                //外部辅助的旋转，帮助全屏
                orientationUtils = OrientationUtils(weakActivity?.get(), player)
                //初始化不打开外部的旋转
                orientationUtils?.isEnable = false
            }
            //直接横屏
            player?.fullscreenButton?.setOnClickListener {
                orientationUtils?.resolveByClick()
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                player?.startWindowFullscreen(weakActivity?.get(), true, true)
            }
        } else player?.fullscreenButton?.visibility = View.GONE
    }

    /**
     * 设置播放路径
     */
    @JvmOverloads
    @JvmStatic
    fun setUrl(url: String, autoPlay: Boolean = false) {
        try {
            retryWithPlay = false
            if (null != imgCover) ImageLoader.instance.displayCoverImage(imgCover!!, url)
            if (videoType == VideoType.MOBILE) {
                GSYVideoOptionBuilder()
                    .setIsTouchWiget(false)
                    .setRotateViewAuto(false)
                    .setAutoFullWithSize(true)
                    .setShowFullAnimation(false)
                    .setNeedLockFull(false)
                    .setUrl(url)
                    .setCacheWithPlay(false)//禁用缓存，vivo手机出错
                    .setVideoAllCallBack(gSYSampleCallBack).build(player)
            } else {
                GSYVideoOptionBuilder()
                    .setIsTouchWiget(false)
                    .setRotateViewAuto(false)
                    .setAutoFullWithSize(true)
                    .setShowFullAnimation(false)
                    .setNeedLockFull(false)
                    .setUrl(url)
                    .setCacheWithPlay(false)
                    .setVideoAllCallBack(gSYSampleCallBack).build(player)
            }
            if (autoPlay) player?.startPlayLogic()
        } catch (e: Exception) {
        }
    }

    /**
     * 全屏时写，写在系统的onBackPressed之前
     */
    @JvmStatic
    fun onBackPressed(): Boolean {
        orientationUtils?.backToProtVideo()
        return GSYVideoManager.backFromWindowFull(weakActivity?.get())
    }

    /**
     * 写在系统的onPause之前
     */
    @JvmStatic
    fun onPause() = player?.currentPlayer?.onVideoPause()

    /**
     * 写在系统的onResume之前
     */
    @JvmStatic
    fun onResume() = player?.currentPlayer?.onVideoResume(false)

    /**
     * 写在系统的onDestroy之后
     */
    @JvmStatic
    fun onDestroy() {
        onPause()
        player?.currentPlayer?.release()
        player?.release()
        orientationUtils?.releaseListener()
    }

    /**
     * 绑定对应页面的生命周期-》对应回调重写对应方法
     * @param lifecycleOwner
     */
    fun addLifecycleObserver(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        onPause()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        onResume()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        onDestroy()
    }

}