package com.wyb.iocframe.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 沉浸后内容被挤上去，设置android:fitsSystemWindows="true"即可
 * 
 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			StatusBarUtil.setStatusBarColorDeep(this,Color.WHITE);
			StatusBarUtil.StatusBarLightMode(this);//不是6.0系统或者小米和魅族不会执行
		} else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			StatusBarUtil.setStatusBarColor(this,Color.WHITE);
			StatusBarUtil.StatusBarLightMode(this);
		}
 * @author wyb
 *
 */
public class StatusBarUtil {
	
	/**
	 * 全屏完全隐藏通知栏
	 * @param activity
	 */
	public static void hideStatus(Activity activity){
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	}
	
	public static void setStatusBarColorDeep(Activity activity, int color) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// 设置状态栏透明
			activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 生成一个状态栏大小的矩形
			View statusView = createStatusView(activity, color);
			// 添加 statusView 到布局中
			ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
			decorView.addView(statusView);
			// 设置根布局的参数
			ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
			rootView.setFitsSystemWindows(true);
			rootView.setClipToPadding(true);
		}
	}

	/**
	 * 生成一个和状态栏大小相同的矩形条 
	 * @param activity
	 * @param color
	 * @return
	 */
	private static View createStatusView(Activity activity, int color) {
		// 获得状态栏高度
		int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
		// 绘制一个和状态栏一样高的矩形
		View statusView = new View(activity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
		statusView.setLayoutParams(params);
		statusView.setBackgroundColor(color);
		return statusView;
	}
	
	/**
	 * 修改状态栏为全透明
	 * 
	 * @param activity
	 */
	public static void transparencyBar(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = activity.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
			window.setNavigationBarColor(Color.TRANSPARENT);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = activity.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	/**
	 * 修改状态栏颜色，支持4.4以上版本
	 * 
	 * @param activity
	 * @param colorId
	 */
	@SuppressWarnings("unchecked")
	public static void setStatusBarColor(Activity activity, int colorId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window window = activity.getWindow();
    		window.setStatusBarColor(colorId);
//    		window.setNavigationBarColor(colorId);
        }
	}

	/**
	 * 设置状态栏黑色字体图标， 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
	 * 
	 * @param activity
	 * @return 1:MIUUI 2:Flyme 3:android6.0
	 */
	public static int StatusBarLightMode(Activity activity) {
		int result = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (MIUISetStatusBarLightMode(activity.getWindow(), true)) {
				result = 1;
			} else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
				result = 2;
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
				result = 3;
			}
		}
		return result;
	}

	/**
	 * 已知系统类型时，设置状态栏黑色字体图标。 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
	 * 
	 * @param activity
	 * @param type
	 *            1:MIUUI 2:Flyme 3:android6.0
	 */
	public static void StatusBarLightMode(Activity activity, int type) {
		if (type == 1) {
			MIUISetStatusBarLightMode(activity.getWindow(), true);
		} else if (type == 2) {
			FlymeSetStatusBarLightMode(activity.getWindow(), true);
		} else if (type == 3) {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
	}

	/**
	 * 清除MIUI或flyme或6.0以上版本状态栏黑色字体
	 */
	public static void StatusBarDarkMode(Activity activity, int type) {
		if (type == 1) {
			MIUISetStatusBarLightMode(activity.getWindow(), false);
		} else if (type == 2) {
			FlymeSetStatusBarLightMode(activity.getWindow(), false);
		} else if (type == 3) {
			activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		}

	}

	/**
	 * 设置状态栏图标为深色和魅族特定的文字风格 可以用来判断是否为Flyme用户
	 * 
	 * @param window
	 *            需要设置的窗口
	 * @param dark
	 *            是否把状态栏字体及图标颜色设置为深色
	 * @return boolean 成功执行返回true
	 * 
	 */
	public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			try {
				WindowManager.LayoutParams lp = window.getAttributes();
				Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
				Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
				darkFlag.setAccessible(true);
				meizuFlags.setAccessible(true);
				int bit = darkFlag.getInt(null);
				int value = meizuFlags.getInt(lp);
				if (dark) {
					value |= bit;
				} else {
					value &= ~bit;
				}
				meizuFlags.setInt(lp, value);
				window.setAttributes(lp);
				result = true;
			} catch (Exception e) {}
		}
		return result;
	}

	/**
	 * 设置状态栏字体图标为深色，需要MIUIV6以上
	 * 
	 * @param window
	 *            需要设置的窗口
	 * @param dark
	 *            是否把状态栏字体及图标颜色设置为深色
	 * @return boolean 成功执行返回true
	 * 
	 */
	public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
		boolean result = false;
		if (window != null) {
			Class clazz = window.getClass();
			try {
				int darkModeFlag = 0;
				Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
				Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
				darkModeFlag = field.getInt(layoutParams);
				Method extraFlagField = clazz.getMethod("setExtraFlags",int.class, int.class);
				if (dark) {
					extraFlagField.invoke(window, darkModeFlag, darkModeFlag);// 状态栏透明且黑色字体
				} else {
					extraFlagField.invoke(window, 0, darkModeFlag);// 清除黑色字体
				}
				result = true;
			} catch (Exception e) {}
		}
		return result;
	}

}