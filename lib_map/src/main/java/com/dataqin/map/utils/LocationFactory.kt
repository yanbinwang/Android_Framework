package com.dataqin.map.utils

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.model.LatLng
import com.dataqin.common.BaseApplication
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.RequestCode
import com.dataqin.common.utils.analysis.GsonUtil
import com.dataqin.common.utils.helper.permission.OnPermissionCallBack
import com.dataqin.common.utils.helper.permission.PermissionHelper
import com.dataqin.common.widget.dialog.AppDialog
import com.dataqin.common.widget.dialog.callback.OnDialogListener
import com.dataqin.map.R
import com.yanzhenjie.permission.Permission
import java.lang.ref.WeakReference

/**
 *  Created by wangyanbin
 *  定位-必须要有定位权限，否则定位失败，可以不开gps会走网络定位
 *  定位工具类写成class避免每次init都要初始化
 *  1.先实现回调
 *  2.key文件一定要校准
 */
class LocationFactory : AMapLocationListener {
    private val context by lazy { BaseApplication.instance?.applicationContext }
    private var locationClient: AMapLocationClient? = null
    private var locationSubscriber: LocationSubscriber? = null

    companion object {
        @JvmStatic
        val instance by lazy { LocationFactory() }
    }

    init {
        //初始化定位
        locationClient = AMapLocationClient(context)
        //初始化定位参数
        val aMapLocationClientOption = AMapLocationClientOption()
        //设置定位监听
        locationClient?.setLocationListener(this)
        aMapLocationClientOption.apply {
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //设置是否gps优先，只在高精度模式下有效
            isGpsFirst = true
            //设置定位场景为出行
            locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
            //设置定位间隔,单位毫秒,默认为2000ms
            interval = 1000
            //true表示允许外界在定位SDK通过GPS定位时模拟位置，false表示不允许模拟GPS位置
            isMockEnable = true
            //设置是否返回方向角(取值范围：【0，360】，其中0度表示正北方向，90度表示正东，180度表示正南，270度表示正西)
            isSensorEnable = true
            //启动定位时SDK会返回最近3s内精度最高的一次定位结果（+）
            isOnceLocationLatest = true
            //请求超时时间
            httpTimeOut = 3000
        }
        //设置定位参数
        locationClient?.setLocationOption(aMapLocationClientOption)
        //启动后台定位，第一个参数为通知栏ID，建议整个APP使用一个
        locationClient?.enableBackgroundLocation(2001, buildNotification())
    }

    private fun buildNotification(): Notification {
        val builder: Notification.Builder?
        //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            val notificationChannel = NotificationChannel(Constants.PUSH_CHANNEL_ID, Constants.PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.apply {
                enableLights(true) //是否在桌面icon右上角展示小圆点
                lightColor = Color.BLUE //小圆点颜色
                setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
            }
            notificationManager?.createNotificationChannel(notificationChannel)
            builder = Notification.Builder(context, Constants.PUSH_CHANNEL_ID)
        } else {
            builder = Notification.Builder(context)
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(Constants.APPLICATION_NAME)
            .setContentText("正在定位...")
            .setWhen(System.currentTimeMillis())
        return builder.build()
    }

    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation != null && aMapLocation.errorCode == AMapLocation.LOCATION_SUCCESS) {
            Constants.LATLNG_JSON = GsonUtil.objToJson(LatLng(aMapLocation.latitude, aMapLocation.longitude))
            locationSubscriber?.onSuccess(aMapLocation)
        } else {
            locationSubscriber?.onFailed()
        }
        stop()
    }

    /**
     * 开始定位-启动页定位
     */
    fun start(locationSubscriber: LocationSubscriber) {
        this.locationSubscriber = locationSubscriber
        if (PermissionHelper.with(context).checkSelfLocation()) {
            locationClient?.startLocation()
        } else {
            locationSubscriber.onFailed()
        }
    }

    /**
     * 开始定位(高德的isStart取到的不是实时的值,直接调取开始或停止内部api会做判断)
     * 必须具备定位权限,不区分安卓版本！用于打卡，签到，地图矫正
     */
    fun start(context: Context, locationSubscriber: LocationSubscriber? = null) {
        this.locationSubscriber = locationSubscriber
        PermissionHelper.with(context).setPermissionCallBack(object : OnPermissionCallBack {
            override fun onPermission(isGranted: Boolean) {
                if (isGranted) {
                    locationClient?.startLocation()
                } else {
                    locationSubscriber?.onFailed()
                }
            }
        }).requestPermissions(Permission.Group.LOCATION)
    }

    /**
     * 停止定位，在页面OnDestroy调用
     */
    fun stop() {
        //关闭后台定位，参数为true时会移除通知栏，为false时不会移除通知栏，但是可以手动移除
        locationClient?.disableBackgroundLocation(true)
        //结束定位(高德的isStart取到的不是实时的值,直接调取开始或停止内部api会做判断)
        locationClient?.stopLocation()
    }

    /**
     * 释放，销毁定位客户端调用
     */
    fun destroy() {
        stop()
        locationClient?.unRegisterLocationListener(this)
        locationClient?.onDestroy()
        locationClient = null
    }

    /**
     * 跳转设置gps
     */
    fun settingGps(activity: Activity?) {
        val weakActivity = WeakReference(activity)
        val locationManager = weakActivity.get()?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AppDialog.with(weakActivity.get()).setOnDialogListener(object : OnDialogListener {
                override fun onConfirm() {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    weakActivity.get()?.startActivityForResult(intent, RequestCode.LOCATION_REQUEST)
                }

                override fun onCancel() {
                }
            }).setParams(weakActivity.get()?.getString(R.string.label_window_title), weakActivity.get()?.getString(R.string.label_window_gps), weakActivity.get()?.getString(R.string.label_window_sure), weakActivity.get()?.getString(R.string.label_window_cancel)).show()
        }
    }

}