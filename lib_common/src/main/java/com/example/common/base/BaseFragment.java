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
import androidx.viewbinding.ViewBinding;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.base.utils.LogUtil;
import com.example.base.utils.ToastUtil;
import com.example.common.base.bridge.BaseImpl;
import com.example.common.base.bridge.BasePresenter;
import com.example.common.base.bridge.BaseView;
import com.example.common.base.page.PageParams;
import com.example.common.base.proxy.SimpleTextWatcher;
import com.example.common.bus.RxManager;
import com.example.common.constant.Extras;
import com.example.common.utils.builder.StatusBarBuilder;
import com.example.common.widget.dialog.LoadingDialog;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.disposables.Disposable;


/**
 * Created by wyb on 2016/3/26.
 * 基础fragment的基类
 * 适用于fragmentmanager管理显示隐藏fragment
 */
@SuppressWarnings("unchecked")
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment implements BaseImpl, BaseView {
    protected VB binding;
    protected WeakReference<Activity> activity;//基类activity弱引用
    protected WeakReference<Context> context;//基类context弱引用
    protected StatusBarBuilder statusBarBuilder;//状态栏工具类
    private RxManager rxManager;//事务管理器
    private BasePresenter presenter;//P层
    private LoadingDialog loadingDialog;//刷新球控件，相当于加载动画
    private final String TAG = getClass().getSimpleName().toLowerCase();//额外数据，查看log，观察当前activity是否被销毁

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    protected void addDisposable(Disposable disposable) {
        if (null != disposable) {
            rxManager.add(disposable);
        }
    }

    protected <P extends BasePresenter> P createPresenter(Class<P> pClass) {
        if (presenter == null) {
            try {
                presenter = pClass.newInstance();
                presenter.attachView(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (P) presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log(TAG);
        Type superclass = getClass().getGenericSuperclass();
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];
        try {
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            binding = (VB) method.invoke(null, getLayoutInflater(), container, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return binding.getRoot();
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
        statusBarBuilder = new StatusBarBuilder(activity.get());
        loadingDialog = new LoadingDialog(activity.get());
        rxManager = new RxManager();
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void initData() {
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
    public void getFocus(View view) {
        view.setFocusable(true);//设置输入框可聚集
        view.setFocusableInTouchMode(true);//设置触摸聚焦
        view.requestFocus();//请求焦点
        view.findFocus();//获取焦点
    }

    @Override
    public String getParameters(@org.jetbrains.annotations.Nullable View view) {
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
    public void onTextChanged(SimpleTextWatcher simpleTextWatcher, View... views) {
        for (View view : views) {
            if (view instanceof EditText) {
                ((EditText) view).addTextChangedListener(simpleTextWatcher);
            }
        }
    }

    @Override
    public void onClick(View.OnClickListener onClickListener, View... views) {
        for (View view : views) {
            if (view != null) {
                view.setOnClickListener(onClickListener);
            }
        }
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
        if (binding != null) {
            binding = null;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="BaseView实现方法-初始化一些工具类和全局的订阅">
    @Override
    public void log(String content) {
        LogUtil.e(TAG, content);
    }

    @Override
    public void showToast(String str) {
        ToastUtil.mackToastSHORT(str, requireContext().getApplicationContext());
    }

    @Override
    public void showDialog() {
        showDialog(false);
    }

    @Override
    public void showDialog(boolean flag) {
        loadingDialog.show(flag);
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