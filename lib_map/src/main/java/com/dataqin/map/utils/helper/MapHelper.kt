package com.dataqin.map.utils.helper

import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.PolygonOptions
import com.dataqin.common.utils.analysis.GsonUtil
import com.dataqin.map.service.MapReceiver
import kotlin.math.roundToInt


/**
 *  Created by wangyanbin
 *  高德地图工具类
 */
object MapHelper {
    private val defaultLatLng by lazy { GsonUtil.jsonToObj("{latitude:30.2780010000,longitude:120.1680690000}", LatLng::class.java) }//默认地图经纬度
    private val aMapReceiver by lazy { MapReceiver() }
    private var mapView: MapView? = null
    var aMap: AMap? = null

    /**
     * 初始化
     * 如需要广播监听，需书写对应的rxjava
     */
    @JvmStatic
    fun initialize(savedInstanceState: Bundle, mapView: MapView, receiver: Boolean = false) {
        MapHelper.mapView = mapView
        aMap = mapView.map
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)创建地图
        mapView.onCreate(savedInstanceState)
        //更改地图view设置
        mapView.viewTreeObserver.addOnGlobalLayoutListener {
            val child = mapView.getChildAt(0) as ViewGroup //地图框架
            val logo = child.getChildAt(2)
            if (null != logo) {
                logo.visibility = View.GONE //隐藏logo
            }
        }
        aMap?.isTrafficEnabled = true //显示实时交通状况
        aMap?.uiSettings?.isRotateGesturesEnabled = false //屏蔽旋转
        aMap?.uiSettings?.isZoomControlsEnabled = false //隐藏缩放插件
        aMap?.uiSettings?.isTiltGesturesEnabled = false //屏蔽双手指上下滑动切换为3d地图
        aMap?.moveCamera(CameraUpdateFactory.zoomTo(18f))
        if (receiver) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            mapView.context.registerReceiver(aMapReceiver, intentFilter)
        }
    }

    /**
     * 加载
     */
    @JvmStatic
    fun resume() {
        mapView?.onResume()
    }

    /**
     * 暂停
     */
    @JvmStatic
    fun pause() {
        mapView?.onPause()
    }

    /**
     * 存储
     */
    @JvmStatic
    fun saveInstanceState(outState: Bundle) {
        //保存地图当前的状态
        mapView?.onSaveInstanceState(outState)
    }

    /**
     * 销毁
     */
    @JvmStatic
    fun destroy() {
        aMap = null
        mapView?.context?.unregisterReceiver(aMapReceiver)
        mapView?.onDestroy()
    }

    /**
     * 地图移动
     */
    @JvmStatic
    fun moveCamera(latLng: LatLng? = defaultLatLng, zoom: Float = 18f, anim: Boolean = false) {
        if (anim) {
            aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        } else {
            aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }

    /**
     * 需要移动的经纬度，需要移动的范围（米）
     */
    @JvmStatic
    fun adjustCamera(latLng: LatLng, range: Int) {
        //移动地图需要进行一定的换算
        val scale = aMap!!.scalePerPixel
        //代表range（米）的像素数量
        val pixel = (range / scale).roundToInt()
        //小范围，小缩放级别（比例尺较大），有精度损失
        val projection = aMap!!.projection
        //将地图的中心点，转换为屏幕上的点
        val center = projection.toScreenLocation(latLng)
        //获取距离中心点为pixel像素的左、右两点（屏幕上的点
        val top = Point(center.x, center.y + pixel)
        //将屏幕上的点转换为地图上的点
        moveCamera(projection.fromScreenLocation(top), 16f, true)
    }

    /**
     * 添加覆盖物
     */
    @JvmStatic
    fun addMarker(latLng: LatLng, view: View, json: String = "") {
        //将标识绘制在地图上
        val markerOptions = MarkerOptions()
        val bitmap = BitmapDescriptorFactory.fromView(view)
        markerOptions.position(latLng) //设置位置
            .icon(bitmap) //设置图标样式
            .anchor(0.5f, 0.5f)
            .zIndex(9f) //设置marker所在层级
            .draggable(false) //设置手势拖拽;
        //给地图覆盖物加上额外的集合数据（点击时候取）
        val marker = aMap?.addMarker(markerOptions)
        marker?.title = json
        marker?.setFixingPointEnable(false) //去除拉近动画
        marker?.isInfoWindowEnable = false //禁止高德地图自己的弹出窗口
    }

    /**
     * 绘制多边形
     */
    @JvmStatic
    fun addPolygon(latLngList: MutableList<LatLng>) {
        // 声明多边形参数对象
        val polygonOptions = PolygonOptions()
        for (latLng in latLngList) {
            polygonOptions.add(latLng)
        }
        polygonOptions.strokeWidth(15f) // 多边形的边框
            .strokeColor(Color.argb(50, 1, 1, 1))// 边框颜色
            .fillColor(Color.argb(1, 1, 1, 1))// 多边形的填充色
        aMap?.addPolygon(polygonOptions)
    }

}