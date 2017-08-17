package com.wyb.iocframe.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.wyb.iocframe.config.CommonConfig;
import com.wyb.iocframe.util.LoadingUtil;
import com.wyb.iocframe.util.ToastUtil;
import com.wyb.iocframe.util.glide.GlideUtil;
import com.wyb.iocframe.util.net.ConnectServerUtil;
import com.wyb.iocframe.util.net.ConnectWebsiteUtil;

/**
 * Created by android on 2017/3/7.
 */
public class BaseActivity extends AppCompatActivity {
    // 额外数据，查看log，观察当前activity是否被销毁
    private final String TAG = getClass().getSimpleName().toLowerCase();
    // 软键盘的View
    private View decorView = null;
    //刷新球控件，相当于加载动画
    private LoadingUtil loadingUtil;
    // 图片加载类
    public GlideUtil mGlide;

    protected void log(String content){
        Log.e(TAG,content);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlide = new GlideUtil(this);
        loadingUtil = new LoadingUtil(this);
    }

    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        FinalActivity.initInjectedView(this);
    }

    //显示刷新球动画
    public void showDialog(){
        loadingUtil.showProgressDialog();
    }

    //隐藏刷新球控件
    public void hideDialog(){
        loadingUtil.hideProgressDialog();
    }

    // 给控件加点击事件
    protected void setOnClick(int res, View.OnClickListener listener) {
        findViewById(res).setOnClickListener(listener);
    }

    // 给textview赋值
    protected void setText(int res, String str) {
        ((TextView) findViewById(res)).setText(str);
    }

    // Toast 显示
    public void showToast(String str) {
        ToastUtil.mackToastSHORT(str, this);
    }

    // 关闭软键盘
    public void closeDecor() {
        decorView = getWindow().peekDecorView();
        // 隐藏软键盘
        if (decorView != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
    }

    // 获取APP版本
    protected String getAppVersion() {
        String verName = null;
        try {
            verName = verName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    // 获取手机版本
    protected String getVERSION() {
        String VERSION = android.os.Build.VERSION.RELEASE;
        return VERSION;
    }

    // 比例换算公式（手机宽度*转换长度/切图宽度---所占百分比）
    public int lengthConvert(int length) {
        return (CommonConfig.screenW * length / 640);
    }

    // 给控件换算比例后赋值长宽
    public void setParams(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width > 0 ? lengthConvert(width) : width;
        params.height = height > 0 ? lengthConvert(height) : height;
        view.setLayoutParams(params);
    }

    // 防止报空
    public String getStr(String source,String defaultStr){
        if(source == null){
            return defaultStr;
        }else {
            if(source.trim().isEmpty()){
                return defaultStr;
            }else {
                return source;
            }
        }
    }

    protected void onDestroy() {
        ConnectServerUtil.cancelRequestByKey(this);
        ConnectWebsiteUtil.cancelRequestByKey(this);
        super.onDestroy();
        Log.v(TAG, "onDestroy...");
    }

}
