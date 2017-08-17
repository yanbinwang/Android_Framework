package com.wyb.iocframe.view;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.wyb.iocframe.R;


/**
 * 类似苹果的弹出窗口类
 * @author wyb
 *
 */
public class MyDialog {

	public static void getDialogAndshow(Context context, final DialogCallBack callback, String content , String sureStr, String cancleStr, String tipStr) {
		AlertDialog.Builder builder = new AlertDialog.Builder (context, R.style.dialogStyle);
		builder.setTitle(tipStr);
		builder.setMessage(content);
		builder.setNegativeButton(sureStr, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if(null != callback){
					callback.dialogSure();
				}
			}
		});
		builder.setPositiveButton(cancleStr, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				if(null != callback){
					callback.dialogCancle();
				}
			}
		});
		builder.show();
	}
	
	public interface DialogCallBack{
		void dialogSure();
		void dialogCancle();
	}

}
