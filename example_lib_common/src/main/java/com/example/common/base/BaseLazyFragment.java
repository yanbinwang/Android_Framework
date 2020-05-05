package com.example.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.R;
import com.example.common.base.callback.BasePresenter;
import com.example.common.base.callback.BaseView;
import com.example.common.constant.Extras;
import com.example.common.base.interceptor.ARouterParams;
import com.example.common.utils.subscription.RxManager;
import com.example.common.utils.permission.AndPermissionUtil;
import com.example.common.widget.dialog.LoadingDialog;
import com.example.framework.glide.GlideUtil;
import com.example.framework.utils.LogUtil;
import com.example.framework.utils.SHPUtil;
import com.example.framework.utils.StatusBarUtil;
import com.example.framework.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;


/**
 * Created by wyb on 2016/3/26.
 * 懒加载的fragment的基类
 * 适用于viewpage+fragment的形式
 */
@SuppressWarnings("unchecked")
public abstract class BaseLazyFragment<P extends BasePresenter> extends Fragment implements BaseView {
    protected P presenter;//P层泛型
    protected View convertView;//传入的View
    protected RxManager rxManager;//事务管理器
    protected GlideUtil mGlide;//图片加载类
    protected StatusBarUtil statusBarUtil;//状态栏工具类
    protected AndPermissionUtil andPermissionUtil;//获取权限类
    protected SHPUtil userInfoSHP, userConfigSHP, appInfoSHP, appConfigSHP;//用户和应用的一些暂存文件
    protected WeakReference<Activity> mActivity;//基类activity弱引用
    private Unbinder mUnbinder;//黄油刀绑定
    private LoadingDialog loadingDialog;//刷新球控件，相当于加载动画
    private boolean isVisible, isInitView, isFirstLoad = true;//当前Fragment是否可见,是否与View建立起映射关系,是否是第一次加载数据
    private final String TAG = getClass().getSimpleName().toLowerCase();//额外数据，查看log，观察当前activity是否被销毁

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log(TAG);
        presenter = getPresenter();
        if (null != presenter) {
            presenter.attachView(requireContext(), this);
        }
        convertView = inflater.inflate(getLayoutResID(), container, false);
        mUnbinder = ButterKnife.bind(this, convertView);
        initBaseView();
        initView();
        initEvent();
        lazyLoadData();
        return convertView;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        log(TAG);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        log("context" + "   " + TAG);
    }

    // <editor-fold defaultstate="collapsed" desc="初始化懒加载以及一些工具类">
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

    public void setUserVisibleHint(boolean isVisibleToUser) {
        log("isVisibleToUser " + isVisibleToUser + "   " + TAG);
        if (isVisibleToUser) {
            isVisible = true;
            lazyLoadData();
        } else {
            isVisible = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void lazyLoadData() {
        if (isFirstLoad) {
            log("第一次加载 " + " isInitView  " + isInitView + "  isVisible  " + isVisible + "   " + TAG);
        } else {
            log("不是第一次加载" + " isInitView  " + isInitView + "  isVisible  " + isVisible + "   " + TAG);
        }
        if (!isFirstLoad || !isVisible || !isInitView) {
            log("不加载" + "   " + TAG);
            return;
        }

        log("完成数据第一次加载" + "   " + TAG);
        initData();
        isFirstLoad = false;
    }

    private void initBaseView() {
        isInitView = true;
        ARouter.getInstance().inject(this);
        userInfoSHP = new SHPUtil(getContext(), getString(R.string.shp_user_info_fileName));
        userConfigSHP = new SHPUtil(getContext(), getString(R.string.shp_user_configure_fileName));
        appInfoSHP = new SHPUtil(getContext(), getString(R.string.shp_app_info_fileName));
        appConfigSHP = new SHPUtil(getContext(), getString(R.string.shp_app_configure_fileName));
        mGlide = new GlideUtil(getContext());
        andPermissionUtil = new AndPermissionUtil(getContext());
        loadingDialog = new LoadingDialog(getContext());
        mActivity = new WeakReference<>(getActivity());
        statusBarUtil = new StatusBarUtil(mActivity.get());
        rxManager = new RxManager();
    }
    // </editor-fold>

    @Override
    public void log(String content) {
        LogUtil.INSTANCE.e(TAG, content);
    }

    @Override
    public void showToast(String str) {
        ToastUtil.INSTANCE.mackToastSHORT(str, requireContext().getApplicationContext());
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

    //让布局中的view与fragment中的变量建立起映射
    protected void initView() {
    }

    //加载页面监听
    protected void initEvent() {
    }

    //加载要显示的数据(已经做了懒加载)
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
                ((InputMethodManager) mActivity.get().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.get().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, 2);
        }
    }

    protected void closeDecor() {
        View decorView = mActivity.get().getWindow().peekDecorView();
        // 隐藏软键盘
        if (decorView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity.get().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="针对textview的一些赋值操作">
    protected void setText(int res, String str) {
        ((TextView) convertView.findViewById(res)).setText(str);
    }

    protected void setTextColor(int res, int color) {
        ((TextView) convertView.findViewById(res)).setTextColor(color);
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
            postcard.navigation(mActivity.get(), code);
        }
        return mActivity.get();
    }
    //</editor-fold>

    @Override
    public void onDestroy() {
        super.onDestroy();
        rxManager.clear();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

}
