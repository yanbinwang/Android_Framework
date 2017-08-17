package com.wyb.iocframe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by android on 2017/7/11.
 */
public class XWebView extends WebView{
    public XWebView(Context context) {
        super(context);
        initWebSetting();
    }

    public XWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initWebSetting(){
        WebSettings webSettings = this.getSettings();
        //设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        //自动打开窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //设置WebView 可以加载更多格式页面
        webSettings.setLoadWithOverviewMode(true);
        //设置WebView使用广泛的视窗
        webSettings.setUseWideViewPort(true);
        // 设置webview页面自适应网页宽度
        webSettings.setUseWideViewPort(true);
        //启用或禁止WebView访问文件数据
        webSettings.setAllowFileAccess(true);
        //支持手势缩放
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        //告诉webview不启用应用程序缓存api---退出activity时全部清空
        webSettings.setAppCacheEnabled(false);
        webSettings.setDefaultTextEncodingName("utf-8");
    }

}
