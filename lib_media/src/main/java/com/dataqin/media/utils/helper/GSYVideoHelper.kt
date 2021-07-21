package com.dataqin.media.utils.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.dataqin.common.imageloader.ImageLoader
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
object GSYVideoHelper {
    private var retryTimes = 0
    private var videoType: VideoType = VideoType.MOBILE
    private var weakActivity: WeakReference<Activity>? = null
    private var imgCover: ImageView? = null
    private var player: StandardGSYVideoPlayer? = null
    private var orientationUtils: OrientationUtils? = null
    private val gSYSampleCallBack by lazy { object : GSYSampleCallBack() {
        override fun onQuitFullscreen(url: String, vararg objects: Any) {
            super.onQuitFullscreen(url, *objects)
            orientationUtils?.backToProtVideo()
        }

        override fun onPlayError(url: String?, vararg objects: Any?) {
            super.onPlayError(url, *objects)
            //播放失败切换内核，3次重试
            if (retryTimes != 3) {
                retryTimes++
                GSYVideoType.enableMediaCodecTexture()
                PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
                CacheFactory.setCacheManager(ProxyCacheManager::class.java)
                player?.startPlayLogic()
            }
        }
    }
    }

    enum class VideoType {
        MOBILE, PC
    }

    /**
     * activity-视频对应页面
     * player-视频对应播放器
     * fullScreen-是否全屏（默认不全屏）
     */
    @JvmStatic
    fun initialize(activity: Activity, standardGSYVideoPlayer: StandardGSYVideoPlayer, fullScreen: Boolean = false, videoType: VideoType = VideoType.MOBILE) {
//        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL)//是否平铺屏幕
//        GSYVideoType.enableMediaCodecTexture()
        //赋值
        this.weakActivity = WeakReference(activity)
        this.player = standardGSYVideoPlayer
        this.videoType = videoType
        this.imgCover = ImageView(weakActivity?.get())
        //屏幕展示效果
        GSYVideoType.setShowType(if (videoType == VideoType.MOBILE && fullScreen) GSYVideoType.SCREEN_MATCH_FULL else GSYVideoType.SCREEN_TYPE_DEFAULT)
        //底层渲染
        GSYVideoType.setRenderType(GSYVideoType.SUFRACE)
        //默认采用exo内核，播放报错则切内核
        GSYVideoType.disableMediaCodecTexture()
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
        imgCover?.scaleType = ImageView.ScaleType.FIT_CENTER
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
                if (videoType == VideoType.PC) orientationUtils?.resolveByClick()
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                player?.startWindowFullscreen(weakActivity?.get(), true, true)
            }
        }
    }

    /**
     * 设置播放路径
     */
    @JvmStatic
    fun setUrl(url: String) {
        retryTimes = 0
        //加载图片
        if (null != imgCover) ImageLoader.instance.displayCoverImage(imgCover!!, url)
        if (null != player) {
            if (videoType == VideoType.MOBILE) {
                GSYVideoOptionBuilder()
                    .setIsTouchWiget(false)
                    .setRotateViewAuto(true)
                    .setLockLand(true)
                    .setAutoFullWithSize(false)
                    .setShowFullAnimation(false)
                    .setNeedLockFull(true)
                    .setUrl(url)
                    .setCacheWithPlay(false)
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
    fun onPause() {
        player?.currentPlayer?.onVideoPause()
    }

    /**
     * 写在系统的onResume之前
     */
    @JvmStatic
    fun onResume() {
        player?.currentPlayer?.onVideoResume(false)
    }

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