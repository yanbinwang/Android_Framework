package com.wyb.iocframe.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.wyb.iocframe.R;

/**
 * Created by android on 2017/6/28.
 */
public class LoadingUtil {
    private View view;
    private Context context;
    private Dialog progressDialog;

    public  LoadingUtil(Context context){
        this.context = context;
    }

    public void showProgressDialog() {
        LayoutInflater mInflater = LayoutInflater.from(context);
        if (view == null) {
            view = mInflater.inflate(R.layout.view_loading, null);
        }
        if (progressDialog == null) {
            progressDialog = new Dialog(context, R.style.LoadingStyle);
            progressDialog.setContentView(view, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            progressDialog.setCancelable(true);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
