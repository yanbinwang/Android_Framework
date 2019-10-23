package com.ow.basemodule;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ow.basemodule.constant.Constants;
import com.ow.basemodule.utils.CrashHandler;
import com.ow.basemodule.utils.UserUtil;
import com.ow.basemodule.utils.album.GlideAlbumLoader;
import com.ow.basemodule.utils.http.download.DownloadFactory;
import com.ow.basemodule.utils.http.encryption.DeviceUuidFactory;
import com.ow.basemodule.utils.http.encryption.RSAKeyFactory;
import com.ow.framework.net.OkHttpFactory;
import com.ow.framework.net.RetrofitFactory;
import com.ow.framework.utils.LogUtil;
import com.tencent.smtt.sdk.QbSdk;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

@SuppressLint("MissingPermission")
public class BaseApplication extends Application {
    public static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    public void onCreate() {
        super.onCreate();
        //单列返回值（能让程序在任意地方取到context）
        instance = this;
        init();
    }

    //初始化一些第三方控件和单例工具类等
    private void init() {
        //布局初始化
        AutoSizeConfig.getInstance().getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
//        //阿里路由跳转初始化
//        if (BuildConfig.DEBUG) {
//            ARouter.openLog();     // 打印日志
//            ARouter.openDebug();
//        }
        // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        ARouter.init(this);
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtil.INSTANCE.d(" onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        });
        //初始化图片库类
        Album.initialize(
                AlbumConfig.newBuilder(this)
                        .setAlbumLoader(new GlideAlbumLoader())//设置Album加载器。
                        .setLocale(Locale.CHINA)//强制设置在任何语言下都用中文显示。
                        .build()
        );
        //用户类初始化
        UserUtil.init(this);
        //网络请求类初始化
        OkHttpFactory.Companion.getInstance();
        RetrofitFactory.Companion.getInstance();
        DownloadFactory.Companion.getInstance();
        RSAKeyFactory.getInstance();
        DeviceUuidFactory.getInstance();
        //实例化抓包文件
        CrashHandler.Companion.getInstance();
        //在程序运行时取值，保证长宽静态变量不丢失
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager mWindowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getMetrics(metric);
        //屏幕宽度（像素）
        Constants.SCREEN_WIDTH = metric.widthPixels;
        //屏幕高度（像素）
        Constants.SCREEN_HEIGHT = metric.heightPixels;
        //获取手机的导航栏高度
        Constants.STATUS_BAR_HEIGHT = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
        //获取手机的网络ip
        Constants.IP = getIp();
        //获取手机的Mac地址
        Constants.MAC = getMac();
        //获取手机的DeviceId
        Constants.DEVICE_ID = getDeviceId();
    }

    //获取当前设备ip地址
    private String getIp() {
        NetworkInfo info = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    return null;
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //得到IPV4地址
                return initIp(wifiInfo.getIpAddress());
            }
        }
        return null;
    }

    //将ip的整数形式转换成ip形式
    private String initIp(int ipInt) {
        return (ipInt & 0xFF) + "." +
                ((ipInt >> 8) & 0xFF) + "." +
                ((ipInt >> 16) & 0xFF) + "." +
                (ipInt >> 24 & 0xFF);
    }

    //获取当前设备的mac地址
    private String getMac() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //获取当前设备的id
    @SuppressLint("HardwareIds")
    private String getDeviceId() {
        try {
            return ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (SecurityException e) {
            return null;
        }
    }

    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (null != activityManager) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(100);
            String packageName = getPackageName();
            //100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
            for (ActivityManager.RunningTaskInfo info : runningTaskInfos) {
                if (info.topActivity.getPackageName().equals(packageName) || info.baseActivity.getPackageName().equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

}