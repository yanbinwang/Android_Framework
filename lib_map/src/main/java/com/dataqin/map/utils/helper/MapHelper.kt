package com.dataqin.map.utils.helper

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.amap.api.location.AMapLocation
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.PolygonOptions
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.analysis.GsonUtil
import com.dataqin.common.utils.helper.permission.PermissionHelper
import com.dataqin.map.utils.CoordinateTransUtil
import com.dataqin.map.utils.LocationFactory
import com.dataqin.map.utils.LocationSubscriber
import kotlin.math.roundToInt

/**
 *  Created by wangyanbin
 *  高德地图工具类
 */
object MapHelper {
    private var mapView: MapView? = null
    private var mapLatLng: LatLng? = null//默认地图经纬度
    var aMap: AMap? = null

    /**
     * 初始化
     * 如需要广播监听，需书写对应的rxjava
     */
    @JvmStatic
    fun initialize(savedInstanceState: Bundle?, mapView: MapView, initialize: Boolean = true) {
        this.mapView = mapView
        this.aMap = mapView.map
        //默认地图经纬度-杭州
        val json = Constants.LATLNG_JSON ?: "{latitude:30.2780010000,longitude:120.1680690000}"
        mapLatLng = GsonUtil.jsonToObj(json, LatLng::class.java)
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)创建地图
        mapView.onCreate(savedInstanceState)
        //更改地图view设置
        mapView.viewTreeObserver.addOnGlobalLayoutListener {
            val child = mapView.getChildAt(0) as ViewGroup //地图框架
            val logo = child.getChildAt(2)
            if (null != logo) logo.visibility = View.GONE //隐藏logo
        }
        aMap?.isTrafficEnabled = true //显示实时交通状况
        aMap?.uiSettings?.isRotateGesturesEnabled = false //屏蔽旋转
        aMap?.uiSettings?.isZoomControlsEnabled = false //隐藏缩放插件
        aMap?.uiSettings?.isTiltGesturesEnabled = false //屏蔽双手指上下滑动切换为3d地图
        aMap?.moveCamera(CameraUpdateFactory.zoomTo(18f))
        //是否需要在网络发生改变时，移动地图
        if (initialize) {
            //地图加载完成，定位一次，让地图移动到坐标点
            aMap?.setOnMapLoadedListener {
                //先移动到默认点再检测权限定位
                moveCamera()
                if (PermissionHelper.with(mapView.context).checkSelfLocation()) location(mapView.context)
            }
        }
    }

    /**
     * 加载
     */
    @JvmStatic
    fun resume() = mapView?.onResume()

    /**
     * 暂停
     */
    @JvmStatic
    fun pause() = mapView?.onPause()

    /**
     * 存储-保存地图当前的状态
     */
    @JvmStatic
    fun saveInstanceState(outState: Bundle) = mapView?.onSaveInstanceState(outState)

    /**
     * 销毁
     */
    @JvmStatic
    fun destroy() = mapView?.onDestroy()

    /**
     * 地图定位
     */
    @JvmStatic
    fun location(context: Context) {
        LocationFactory.instance.start(context, object : LocationSubscriber() {
            override fun onSuccess(model: AMapLocation) {
                super.onSuccess(model)
                moveCamera(LatLng(model.latitude, model.longitude))
            }

            override fun onFailed() {
                super.onFailed()
                moveCamera()
            }
        })
    }

    /**
     * 地图移动
     */
    @JvmStatic
    fun moveCamera(latLng: LatLng? = mapLatLng, zoom: Float = 18f, anim: Boolean = false) {
        if (anim) {
            aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        } else {
            aMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        }
    }

    /**
     * 移动到中心点
     */
    @JvmStatic
    fun moveCamera(latLngList: MutableList<LatLng>, zoom: Float = 18f, anim: Boolean = false) {
        moveCamera(CoordinateTransUtil.getCenterPoint(latLngList), zoom, anim)
    }

    /**
     * 需要移动的经纬度，需要移动的范围（米）
     */
    @JvmStatic
    fun adjustCamera(latLng: LatLng, range: Int) {
        //移动地图需要进行一定的换算
        val scale = aMap?.scalePerPixel!!
        //代表range（米）的像素数量
        val pixel = (range / scale).roundToInt()
        //小范围，小缩放级别（比例尺较大），有精度损失
        val projection = aMap?.projection!!
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
            .draggable(false) //设置手势拖拽
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

    /**
     * 判断经纬度是否在多边形范围内
     */
    @JvmStatic
    fun isPolygonContainsPoint(latLngList: MutableList<LatLng>, point: LatLng): Boolean {
        val options = PolygonOptions()
        for (index in latLngList.indices) {
            options.add(latLngList[index])
        }
        options.visible(false)//设置区域是否显示
        val polygon = aMap?.addPolygon(options)
        val contains = polygon?.contains(point)
        polygon?.remove()
        return contains ?: false
    }

}