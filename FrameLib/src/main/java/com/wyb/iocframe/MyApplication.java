package com.wyb.iocframe;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.wyb.iocframe.config.CommonConfig;
import com.wyb.iocframe.util.log.AppException;
import com.yanzhenjie.nohttp.NoHttp;


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
 * @author wyb
 */
public class MyApplication extends Application {
	public static MyApplication instance;

	public static MyApplication getInstance(){
		return instance;
	}

	public void onCreate() {
		super.onCreate();
		//单列返回值（能让程序在任意地方取到context）
		instance = this;
		//初始化必须写---否则会报异常
		NoHttp.initialize(this);
		//抓包文件实例化
		AppException.getInstance();
		//在程序运行时取值，保证长宽静态变量不丢失
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager mWindowManager  = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.getDefaultDisplay().getMetrics(metric);
		// 屏幕宽度（像素）
		CommonConfig.screenW = metric.widthPixels;
		// 屏幕高度（像素）
		CommonConfig.screenH = metric.heightPixels;
	}

}
