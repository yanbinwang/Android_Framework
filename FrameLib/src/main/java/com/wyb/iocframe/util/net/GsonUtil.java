package com.wyb.iocframe.util.net;

import com.google.gson.Gson;

public class GsonUtil {
	private static Gson gson = new Gson();
	
	public static <T> T jsonToObj(String json,Class<T> className){
		T ret = null;
		try{
			ret = gson.fromJson(json, className);
		}catch(Exception e){
//			e.printStackTrace();
		}
		return ret;
	}
	
	public static String objToJson(Object obj){
		String ret = null;
		try{
			ret = gson.toJson(obj);
		}catch(Exception e){
//			e.printStackTrace();
		}
		return ret;
	}
	
}
