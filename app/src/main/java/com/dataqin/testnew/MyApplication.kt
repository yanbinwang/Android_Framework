package com.dataqin.testnew

import android.os.Looper
import android.util.Log
import com.dataqin.base.BuildConfig
import com.dataqin.common.BaseApplication
import com.dataqin.common.constant.Constants
import com.dataqin.common.utils.file.FileUtil.getApplicationId
import com.dataqin.common.utils.file.FileUtil.getApplicationName
import com.dataqin.testnew.activity.MainActivity
import com.zxy.recovery.core.Recovery

/**
 * 如果想在整个应用中使用全局变量，在java中一般是使用静态变量，public类型；
 * 而在android中如果使用这样的全局变量就不符合Android的框架架构，但是可以
 * 使用一种更优雅的方式就是使用Application context。 首先需要重写Application，
 * 主要重写里面的onCreate方法，就是创建的时候，初始化变量的值。然后在整个应用中
 * 的各个文件中就可以对该变量进行操作了。启动Application时，系统会创建一个PID，
 * 即进程ID，所有的Activity就会在此进程上运行。那么我们在Application创建的时候
 * 初始化全局变量，同一个应用的所有Activity都可以取到这些全局变量的值，换句话说，
 * 我们在某一个Activity中改变了这些全局变量的值，那么在同一个应用的其他Activity
 * 中值就会改变。
 *
 * @author wyb
 */
class MyApplication : BaseApplication() {

    companion object {
        @JvmField
        var instance: MyApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initialize()
    }

    //初始化一些第三方控件和单例工具类等
    private fun initialize() {
        if (BuildConfig.ISDEBUG) {
            //debug	是否开启debug模式
            //recoverInBackgroud 当应用在后台时发生Crash，是否需要进行恢复
            //recoverStack	是否恢复整个Activity Stack，否则将恢复栈顶Activity
            //mainPage	回退的界面
            //callback	发生Crash时的回调
            //silent	SilentMode	是否使用静默恢复，如果设置为true的情况下，那么在发生Crash时将不显示RecoveryActivity界面来进行恢复，而是自动的恢复Activity的堆栈和数据，也就是无界面恢复
            Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(MainActivity::class.java)
                .recoverEnabled(true) //发布版本不跳转
                //                .callback(new MyCrashCallback())
                .silent(
                    false,
                    Recovery.SilentMode.RECOVER_ACTIVITY_STACK
                ) //                .skip(TestActivity.class)
                .init(this)
        } else {
            //当前若是发布包，接管系统loop，让用户感知不到程序闪退
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    val stackTraceString = Log.getStackTraceString(e)
                    if (e is NullPointerException) {
                        println("AppCatch -$stackTraceString")
                    } else if (e is IllegalStateException) {
                        println("AppCatch -$stackTraceString")
                    } else if (e is ArrayIndexOutOfBoundsException) {
                        println("AppCatch -$stackTraceString")
                    } else if (e is IndexOutOfBoundsException) {
                        println("AppCatch -$stackTraceString")
                    } else if (e is OutOfMemoryError) {
                        println("AppCatch -$stackTraceString")
                    } else if (e is NumberFormatException) {
                        println("AppCatch -$stackTraceString")
                    } else {
                        throw e
                    }
                }
            }
        }
//        //初始化推送
//        NotificationUtil.getInstance();
//        PushManager.getInstance().initialize(this, GetuiPushService.class);
//        PushManager.getInstance().registerPushIntentService(this, GetuiIntentService.class);
        //获取应用名。包名。默认保存文件路径
        Constants.APPLICATION_ID = getApplicationId(this)
        Constants.APPLICATION_NAME = getApplicationName(this)
        Constants.APPLICATION_FILE_PATH = Constants.SDCARD_PATH + "/" + Constants.APPLICATION_NAME
        Constants.VERSION_CODE = com.dataqin.testnew.BuildConfig.VERSION_CODE
        Constants.VERSION_NAME = com.dataqin.testnew.BuildConfig.VERSION_NAME
    }

}