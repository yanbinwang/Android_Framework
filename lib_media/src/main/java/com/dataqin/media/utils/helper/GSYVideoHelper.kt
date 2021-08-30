package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.dataqin.base.utils.TimerHelper
import com.dataqin.common.constant.Constants
import com.dataqin.common.imageloader.ImageLoader
import com.dataqin.common.utils.file.FileUtil
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
import java.io.File
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  视频播放器工具类
 *  默认-开锁可随屏幕翻转角度
 */
@SuppressLint("StaticFieldLeak")
object GSYVideoHelper {
    private var cacheWithPlay = false
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
                GSYVideoType.enableMediaCodec()
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
    @JvmStatic
    fun initialize(activity: Activity, standardGSYVideoPlayer: StandardGSYVideoPlayer, fullScreen: Boolean = false, cacheWithPlay: Boolean = false, videoType: VideoType = VideoType.MOBILE) {
//        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL)//是否平铺屏幕
//        GSYVideoType.enableMediaCodecTexture()
        //赋值
        this.weakActivity = WeakReference(activity)
        this.player = standardGSYVideoPlayer
        this.cacheWithPlay = cacheWithPlay
        this.videoType = videoType
        this.imgCover = ImageView(weakActivity?.get())
        //屏幕展示效果
        GSYVideoType.setShowType(if (videoType == VideoType.MOBILE && !fullScreen) GSYVideoType.SCREEN_MATCH_FULL else GSYVideoType.SCREEN_TYPE_DEFAULT)
        //设置底层渲染,关闭硬件解码
        GSYVideoType.setRenderType(GSYVideoType.GLSURFACE)
        GSYVideoType.disableMediaCodec()
        GSYVideoType.disableMediaCodecTexture()
        //默认采用exo内核，播放报错则切ijk内核
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
        imgCover?.scaleType = if (videoType == VideoType.MOBILE && !fullScreen) ImageView.ScaleType.FIT_XY else ImageView.ScaleType.CENTER_CROP
        player?.titleTextView?.visibility = View.GONE
        player?.backButton?.visibility = View.GONE
        player?.thumbImageView = imgCover
        if (!fullScreen) {
            player?.fullscreenButton?.visibility = View.GONE
        } else {
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
        }
    }

    /**
     * 设置播放路径
     */
    @JvmStatic
    fun setUrl(url: String, autoPlay: Boolean = false) {
        val root = "${Constants.APPLICATION_FILE_PATH}/视频缓存"
        FileUtil.deleteDir(root)
        val storeDir = File(root)
        retryWithPlay = false
        //加载图片
        if (null != imgCover) ImageLoader.instance.displayCoverImage(imgCover!!, url)
        if (null != player) {
            if (videoType == VideoType.MOBILE) {
                GSYVideoOptionBuilder()
                    .setIsTouchWiget(false)
                    .setRotateViewAuto(false)
                    .setAutoFullWithSize(true)
                    .setShowFullAnimation(false)
                    .setNeedLockFull(false)
                    .setUrl(url)
                    .setCacheWithPlay(cacheWithPlay)
                    .setCachePath(storeDir)
                    .setVideoAllCallBack(gSYSampleCallBack).build(player)
            } else {
                GSYVideoOptionBuilder()
                    .setIsTouchWiget(false)
                    .setRotateViewAuto(false)
                    .setAutoFullWithSize(true)
                    .setShowFullAnimation(false)
                    .setNeedLockFull(false)
                    .setUrl(url)
                    .setCacheWithPlay(cacheWithPlay)
                    .setCachePath(storeDir)
                    .setVideoAllCallBack(gSYSampleCallBack).build(player)
            }
        }
        if (autoPlay) player?.startPlayLogic()
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
        orientationUtils?.releaseListener()
    }

}