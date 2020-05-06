package com.example.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.R;
import com.example.common.base.callback.BasePresenter;
import com.example.common.base.callback.BaseView;
import com.example.common.utils.subscription.RxBus;
import com.example.common.utils.subscription.RxBusEvent;
import com.example.common.constant.Constants;
import com.example.common.constant.Extras;
import com.example.common.base.interceptor.ARouterParams;
import com.example.common.utils.TitleBuilder;
import com.example.common.utils.subscription.RxManager;
import com.example.common.utils.permission.AndPermissionUtil;
import com.example.common.widget.dialog.LoadingDialog;
import com.example.glide.utils.GlideUtil;
import com.example.framework.utils.LogUtil;
import com.example.framework.utils.SHPUtil;
import com.example.framework.utils.StatusBarUtil;
import com.example.framework.utils.ToastUtil;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;

/**
 * author: wyb
 * date: 2018/7/26.
 * 所有activity的基类，包含了一些方法，全局广播等
 */
@SuppressWarnings({"unchecked","SourceLockedOrientationActivity"})
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView {
    protected P presenter;//P层泛型
    protected RxManager rxManager;//事务管理器
    protected CountDownTimer countDownTimer;//计时器
    protected GlideUtil mGlide;//图片加载类
    protected StatusBarUtil statusBarUtil;//状态栏工具类
    protected TitleBuilder titleBuilder;//标题栏
    protected AndPermissionUtil andPermissionUtil;//获取权限类
    protected SHPUtil userInfoSHP, userConfigSHP, appInfoSHP, appConfigSHP;//用户和应用的一些暂存文件
    private Unbinder mUnbinder;//黄油刀绑定
    private LoadingDialog loadingDialog;//刷新球控件，相当于加载动画
    private final String TAG = getClass().getSimpleName().toLowerCase();//额外数据，查看log，观察当前activity是否被销毁

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResID());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        presenter = getPresenter();
        if (null != presenter) {
            presenter.attachView(this, this);
        }
        initBaseView();
        initBaseEvent();
        initView();
        initEvent();
        initData();
    }

    // <editor-fold defaultstate="collapsed" desc="初始化一些工具类和全局的订阅（顶号关闭等操作）">
    private <P> P getPresenter() {
        try {
            Type superClass = getClass().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type type = null;
            if (parameterizedType != null) {
                type = parameterizedType.getActualTypeArguments()[0];
            }
            Class<P> tClass = (Class<P>) type;
            if (tClass != null) {
                return tClass.newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initBaseView() {
        ARouter.getInstance().inject(this);
        userInfoSHP = new SHPUtil(this, getString(R.string.shp_user_info_fileName));
        userConfigSHP = new SHPUtil(this, getString(R.string.shp_user_configure_fileName));
        appInfoSHP = new SHPUtil(this, getString(R.string.shp_app_info_fileName));
        appConfigSHP = new SHPUtil(this, getString(R.string.shp_app_configure_fileName));
        loadingDialog = new LoadingDialog(this);
        statusBarUtil = new StatusBarUtil(this);
        andPermissionUtil = new AndPermissionUtil(this);
        mGlide = new GlideUtil(this);
        rxManager = new RxManager();
    }

    private void initBaseEvent() {
        addDisposable(RxBus.Companion.getInstance().toFlowable(RxBusEvent.class).subscribe(rxBusEvent -> {
            String action = rxBusEvent.getAction();
            switch (action) {
                //注销登出
                case Constants.APP_USER_LOGIN_OUT:
                    if (!"mainactivity".equals(TAG)) {
                        finish();
                    }
                    break;
                //切换语言
                case Constants.APP_SWITCH_LANGUAGE:
                    finish();
                    break;
            }
        }));
    }
    // </editor-fold>

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base);
        FrameLayout addMainContextFrame = findViewById(R.id.add_main_context_frame);
        addMainContextFrame.addView(getLayoutInflater().inflate(layoutResID, null));
        titleBuilder = new TitleBuilder(this);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    public void log(String content) {
        LogUtil.INSTANCE.e(TAG, content);
    }

    @Override
    public void showToast(String str) {
        ToastUtil.INSTANCE.mackToastSHORT(str, getApplicationContext());
    }

    @Override
    public void showDialog() {
        showDialog(false);
    }

    @Override
    public void showDialog(Boolean isClose) {
        loadingDialog.show(isClose);
    }

    @Override
    public void hideDialog() {
        loadingDialog.hide();
    }

    // <editor-fold defaultstate="collapsed" desc="页面覆写方法">
    //加载页面布局文件
    protected abstract int getLayoutResID();

    //加载页面控件view
    protected void initView() {
    }

    //加载页面监听
    protected void initEvent() {
    }

    //加载页面接口数据
    protected void initData() {
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="添加事务管理">
    protected void addDisposable(Disposable disposable) {
        if (null != disposable) {
            rxManager.add(disposable);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="虚拟键盘操作">
    protected void openDecor(View view) {
        closeDecor();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.showSoftInput(view, 2);
        }
    }

    protected void closeDecor() {
        View decorView = getWindow().peekDecorView();
        // 隐藏软键盘
        if (decorView != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="针对textview的一些赋值操作">
    protected void setText(int res, String str) {
        ((TextView) findViewById(res)).setText(str);
    }

    protected void setTextColor(int res, int color) {
        ((TextView) findViewById(res)).setTextColor(color);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="让一个view获得焦点">
    protected void setViewFocus(View view) {
        view.setFocusable(true);//设置输入框可聚集
        view.setFocusableInTouchMode(true);//设置触摸聚焦
        view.requestFocus();//请求焦点
        view.findFocus();//获取焦点
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="防止报空">
    protected String getProcessedString(String source, String defaultStr) {
        if (source == null) {
            return defaultStr;
        } else {
            if (source.trim().isEmpty()) {
                return defaultStr;
            } else {
                return source;
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="倒计时">
    protected void setDownTime(final TextView txt) {
        setDownTime(txt, ContextCompat.getColor(this, R.color.gray_9f9f9f), ContextCompat.getColor(this, R.color.gray_9f9f9f));
    }

    protected void setDownTime(final TextView txt, final int startColorId, final int endColorId) {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    txt.setText(MessageFormat.format("{0}s后重新获取", millisUntilFinished / 1000));// 剩余多少毫秒
                    txt.setTextColor(startColorId);
                    txt.setEnabled(false);
                }

                @Override
                public void onFinish() {
                    txt.setEnabled(true);
                    txt.setTextColor(endColorId);
                    txt.setText("重新发送");
                }
            };
        }
        countDownTimer.start();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="批量设置View隐藏显示状态">
    protected void VISIBLE(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void INVISIBLE(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void GONE(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="非空判断">
    protected static boolean isEmpty(Object... objs) {
        for (Object obj : objs) {
            if (obj == null) {
                return true;
            } else if (obj instanceof String && obj.equals("")) {
                return true;
            }
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="获取控件信息">
    protected String getViewValue(View view) {
        if (view instanceof EditText) {
            return ((EditText) view).getText().toString().trim();
        } else if (view instanceof TextView) {
            return ((TextView) view).getText().toString().trim();
        } else if (view instanceof CheckBox) {
            return ((CheckBox) view).getText().toString().trim();
        } else if (view instanceof RadioButton) {
            return ((RadioButton) view).getText().toString().trim();
        } else if (view instanceof Button) {
            return ((Button) view).getText().toString().trim();
        }
        return null;
    }
    // </editor-fold>

    //<editor-fold desc="路由跳转">
    protected void bannerNavigation(String link) {
        if (!TextUtils.isEmpty(link)) {
            if (link.startsWith("http")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            } else {
                ARouter.getInstance().build(Uri.parse(link.replace("/view/cloud", ""))).navigation();
            }
        }
    }

    protected Activity navigation(String path) {
        return navigation(path, null);
    }

    protected Activity navigation(String path, ARouterParams aRouterParams) {
        Postcard postcard = ARouter.getInstance().build(path);
        Integer code = null;
        if (aRouterParams != null) {
            Map<String, Object> map = aRouterParams.getParams();
            for (String key : map.keySet()) {
                Object value = map.get(key);
                Class<?> cls = value.getClass();
                if (key.equals(Extras.REQUEST_CODE)) {
                    code = (Integer) value;
                    continue;
                }
                if (cls == String.class) {
                    postcard.withString(key, (String) value);
                } else if (value instanceof Parcelable) {
                    postcard.withParcelable(key, (Parcelable) value);
                } else if (value instanceof Serializable) {
                    postcard.withSerializable(key, (Serializable) value);
                } else if (cls == int.class) {
                    postcard.withInt(key, (int) value);
                } else if (cls == long.class) {
                    postcard.withLong(key, (long) value);
                } else if (cls == boolean.class) {
                    postcard.withBoolean(key, (boolean) value);
                } else if (cls == float.class) {
                    postcard.withFloat(key, (float) value);
                } else if (cls == double.class) {
                    postcard.withDouble(key, (double) value);
                } else if (cls == char[].class) {
                    postcard.withCharArray(key, (char[]) value);
                } else if (cls == Bundle.class) {
                    postcard.withBundle(key, (Bundle) value);
                } else {
                    throw new RuntimeException("不支持参数类型" + ": " + cls.getSimpleName());
                }
            }
        }
        if (code == null) {
            postcard.navigation();
        } else {
            postcard.navigation(this, code);
        }
        return this;
    }
    // </editor-fold>

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rxManager.clear();
        if (presenter != null) {
            presenter.detachView();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        log("onDestroy...");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }
}
