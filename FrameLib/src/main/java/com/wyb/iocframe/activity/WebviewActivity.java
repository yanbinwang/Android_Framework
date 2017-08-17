package com.wyb.iocframe.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.wyb.iocframe.R;
import com.wyb.iocframe.base.BaseTitleActivity;
import com.wyb.iocframe.config.CommonConfig;
import com.wyb.iocframe.util.SHPUtil;
import com.wyb.iocframe.view.XWebView;


/**
 * 加载网页的activity
 *
 * @author wyb
 */
public class WebviewActivity extends BaseTitleActivity {
    //网页容器
    private FrameLayout webviewContainer;
    //网页通过代码new出添加进容器，方便回收
    private XWebView webView;
    // 传过来的标题，url和页面加载形式(0正常加载，1塞入cookies)
    private String appTitle, intentUrl, type = "0";
    public static final String APP_TITLE = "appTitle"; //显示的标题
    public static final String INTENT_URL = "intentUrl"; //跳转的链接
    public static final String WEB_TYPE = "webType"; //网页的类型

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        appTitle = getIntent().getStringExtra(APP_TITLE);
        intentUrl = getIntent().getStringExtra(INTENT_URL);
        type = getIntent().getStringExtra(WEB_TYPE);
        initViews();
    }

    protected void initViews() {
        setTitle(appTitle);
        webviewContainer = (FrameLayout) findViewById(R.id.webviewContainer);
        webView = new XWebView(getApplicationContext());
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webviewContainer.addView(webView);
        webView.setWebViewClient(new myClient());
        if ("0".equals(type)) {
            webView.loadUrl(intentUrl);
        } else {
            setCookies();
            webView.loadUrl(intentUrl);
        }
    }

    //网页塞入cookies
    @SuppressWarnings("deprecation")
    public void setCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        cookieManager.setAcceptCookie(true);
        String cook = SHPUtil.getParame(this, SHPUtil.TICKET);//登陆后返回的ticket
        cookieManager.setCookie(CommonConfig.MAIN_PATH + "luckyDrawH5/init.h5", "ticket=" + cook);
        CookieSyncManager.getInstance().sync();
        webView.loadUrl(CommonConfig.MAIN_PATH + "luckyDrawH5/init.h5");
        webView.requestFocus();
    }

    protected void onDestroy() {
        super.onDestroy();
        webviewContainer.removeAllViews();
        if (webView != null) {
            webView.clearHistory();
            webView.clearCache(true);
            webView.loadUrl("about:blank");
            webView.destroy();
            webView = null;
        }
    }

    @SuppressWarnings("unused")
    private class myClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            //调用拨号程序
//			if (url.startsWith("mailto:") || url.startsWith("geo:") ||url.startsWith("tel:")) {  
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));  
//				startActivity(intent);  
//				webView.loadUrl(url);
//			}else{
//				webView.loadUrl(url);
//			}  
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showDialog();
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideDialog();
        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            hideDialog();
        }

    }

}
