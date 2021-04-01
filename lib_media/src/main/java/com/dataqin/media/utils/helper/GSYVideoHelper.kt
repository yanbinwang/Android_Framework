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
object GSYVideoHelper{
    private var retryNum = 0
    private var weakActivity: WeakReference<Activity>? = null
    private var imgCover: ImageView? = null
    private var player: StandardGSYVideoPlayer? = null
    private var orientationUtils: OrientationUtils? = null

    /**
     * activity-视频对应页面
     * player-视频对应播放器
     * fullScreen-是否全屏（默认不全屏）
     */
    @JvmStatic
    fun initialize(activity: Activity, standardGSYVideoPlayer: StandardGSYVideoPlayer, fullScreen: Boolean = false) {
//        GSYVideoType.setShowType(GSYVideoType.SCREEN_MATCH_FULL)//是否平铺屏幕
//        GSYVideoType.enableMediaCodecTexture()
        GSYVideoType.setRenderType(GSYVideoType.SUFRACE)//底层渲染
        //默认采用exo内核，报错了切内核
        GSYVideoType.disableMediaCodecTexture()
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
        weakActivity = WeakReference(activity)
        player = standardGSYVideoPlayer
        imgCover = ImageView(weakActivity?.get())
        imgCover?.scaleType = ImageView.ScaleType.CENTER_CROP
        player?.titleTextView?.visibility = View.GONE
        player?.backButton?.visibility = View.GONE
        player?.thumbImageView = imgCover
        if (!fullScreen) {
            player?.fullscreenButton?.visibility = View.GONE
        } else {
            //外部辅助的旋转，帮助全屏
            orientationUtils = OrientationUtils(weakActivity?.get(), player)
            //初始化不打开外部的旋转
            orientationUtils?.isEnable = false
            player?.fullscreenButton?.setOnClickListener { //直接横屏
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
    fun setUrl(url: String) {
        retryNum = 0
        //加载图片
        if (null != imgCover) {
            ImageLoader.instance.displayCoverImage(imgCover!!, url)
        }
        if (null != player) {
            GSYVideoOptionBuilder()
                .setIsTouchWiget(false)
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setAutoFullWithSize(false)
                .setShowFullAnimation(false)
                .setNeedLockFull(true)
                .setUrl(url)
                .setCacheWithPlay(false)
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onQuitFullscreen(url: String, vararg objects: Any) {
                        super.onQuitFullscreen(url, *objects)
                        orientationUtils?.backToProtVideo()
                    }

                    override fun onPlayError(url: String?, vararg objects: Any?) {
                        super.onPlayError(url, *objects)
                        if (retryNum != 3) {
                            //播放失败切换内核
                            GSYVideoType.enableMediaCodecTexture()
                            PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
                            CacheFactory.setCacheManager(ProxyCacheManager::class.java)
                            player?.startPlayLogic()
                            retryNum++
                        }
                    }
                }).build(player)
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