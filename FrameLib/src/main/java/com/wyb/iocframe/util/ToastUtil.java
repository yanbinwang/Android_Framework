package com.wyb.iocframe.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	
	
	public static void mackToastSHORT(String str,Context context){
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}
	public static void mackToastLONG(String str,Context context){
		Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	}

}
