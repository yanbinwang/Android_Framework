package com.wyb.iocframe.util;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * 缓存工具类
 * 
 * @author Liuj
 * 
 */
public class SHPUtil {
	// 程序是否是第一次安装
	public static final String IS_FRIST = "is_frist_use";
	// 用户登陆返回凭证
	public static final String TICKET = "ticket";
	// 判断用户登陆字段
	public static final String USER_ID = "USER_ID";
	// 当前经度
	public static final String LON = "lon";
	// 当前纬度
	public static final String LAT = "lat";
	// 当前城市
	public static final String CITY = "city";

	// 保存参数
	public static void saveParame(Context context, String key, String value) {
		SharedPreferences.Editor sharedata = context.getSharedPreferences("data", 0).edit();
		sharedata.putString(key, value);
		sharedata.commit();
	}

	//获取参数
	public static String getParame(Context context, String key) {
		SharedPreferences sharedata = context.getSharedPreferences("data", 0);
		return sharedata.getString(key, null);
	}

	//清除对应key值的值
	public static void removeKey(Context context, String key) {
		SharedPreferences.Editor sharedata = context.getSharedPreferences("data", 0).edit();
		sharedata.remove(key);
		sharedata.commit();
	}

	public static long getLongParame(Context context, String key) {
		SharedPreferences sharedata = context.getSharedPreferences("data", 0);
		return sharedata.getLong(key, 0);
	}

	public static void saveLongParame(Context context, String key, Long value) {
		SharedPreferences.Editor sharedata = context.getSharedPreferences("data", 0).edit();
		sharedata.putLong(key, value);
		sharedata.commit();
	}

}
