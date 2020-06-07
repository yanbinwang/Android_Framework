package com.example.common.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.base.bridge.BaseImpl;
import com.example.common.base.bridge.BasePresenter;
import com.example.common.base.bridge.BaseView;
import com.example.common.base.page.PageParams;
import com.example.common.bus.RxManager;
import com.example.common.constant.Extras;
import com.example.common.utils.permission.AndPermissionUtil;
import com.example.common.widget.dialog.LoadingDialog;
import com.example.framework.utils.LogUtil;
import com.example.framework.utils.StatusBarUtil;
import com.example.framework.utils.ToastUtil;

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
 * 基础fragment的基类
 * 适用于fragmentmanager管理显示隐藏fragment
 */
@SuppressWarnings("unchecked")
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseImpl, BaseView {
    protected P presenter;//P层泛型
    protected WeakReference<Activity> activity;//基类activity弱引用
    protected WeakReference<Context> context;//基类context弱引用
    protected View convertView;//传入的View
    protected RxManager rxManager;//事务管理器
    protected StatusBarUtil statusBarUtil;//状态栏工具类
    protected AndPermissionUtil andPermissionUtil;//获取权限类
    private Unbinder unBinder;//黄油刀绑定
    private LoadingDialog loadingDialog;//刷新球控件，相当于加载动画
    private final String TAG = getClass().getSimpleName().toLowerCase();//额外数据，查看log，观察当前activity是否被销毁

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    protected abstract int getLayoutResID();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log(TAG);
        convertView = inflater.inflate(getLayoutResID(), container, false);
        unBinder = ButterKnife.bind(this, convertView);
        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void initView() {
        ARouter.getInstance().inject(this);
        activity = new WeakReference<>(getActivity());
        context = new WeakReference<>(getContext());
        presenter = getPresenter();
        if (null != presenter) {
            presenter.attachView(activity.get(), context.get(), this);
        }
        rxManager = new RxManager();
        andPermissionUtil = new AndPermissionUtil(activity.get());
        loadingDialog = new LoadingDialog(activity.get());
        statusBarUtil = new StatusBarUtil(activity.get());
    }

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

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void addDisposable(Disposable disposable) {
        if (null != disposable) {
            rxManager.add(disposable);
        }
    }

    @Override
    public void openDecor(View view) {
        closeDecor();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((InputMethodManager) activity.get().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.get().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, 2);
        }
    }

    @Override
    public void closeDecor() {
        View decorView = activity.get().getWindow().peekDecorView();
        // 隐藏软键盘
        if (decorView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.get().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
    }

    @Override
    public String getViewValue(View view) {
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

    @Override
    public void setViewFocus(View view) {
        view.setFocusable(true);//设置输入框可聚集
        view.setFocusableInTouchMode(true);//设置触摸聚焦
        view.requestFocus();//请求焦点
        view.findFocus();//获取焦点
    }

    @Override
    public void VISIBLE(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void INVISIBLE(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void GONE(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean isEmpty(Object... objs) {
        for (Object obj : objs) {
            if (obj == null) {
                return true;
            } else if (obj instanceof String && obj.equals("")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String processedString(String source, String defaultStr) {
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

    @Override
    public void setText(int res, String str) {
        ((TextView) convertView.findViewById(res)).setText(str);
    }

    @Override
    public void setTextColor(int res, int color) {
        ((TextView) convertView.findViewById(res)).setTextColor(color);
    }

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
        if (null != unBinder) {
            unBinder.unbind();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="BaseView实现方法-初始化一些工具类和全局的订阅">
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

    @Override
    public Activity navigation(String path) {
        return navigation(path, null);
    }

    @Override
    public Activity navigation(String path, PageParams pageParams) {
        Postcard postcard = ARouter.getInstance().build(path);
        Integer code = null;
        if (pageParams != null) {
            Map<String, Object> map = pageParams.getParams();
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
            postcard.navigation(activity.get(), code);
        }
        return activity.get();
    }
    // </editor-fold>

}