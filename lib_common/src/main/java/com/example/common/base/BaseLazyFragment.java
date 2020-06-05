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

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.base.bridge.BasePresenter;
import com.example.common.base.bridge.BaseView;
import com.example.common.base.page.PageParams;
import com.example.common.bus.RxManager;
import com.example.common.constant.Extras;
import com.example.common.utils.LogUtil;
import com.example.common.utils.permission.AndPermissionUtil;
import com.example.common.widget.dialog.LoadingDialog;
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
    protected WeakReference<Activity> activity;//基类activity弱引用
    protected WeakReference<Context> context;//基类context弱引用
    protected P presenter;//P层泛型
    protected View convertView;//传入的View
    protected RxManager rxManager;//事务管理器
    protected StatusBarUtil statusBarUtil;//状态栏工具类
    protected AndPermissionUtil andPermissionUtil;//获取权限类
    private Unbinder mUnbinder;//黄油刀绑定
    private LoadingDialog loadingDialog;//刷新球控件，相当于加载动画
    private boolean isVisible, isInitView, isFirstLoad = true;//当前Fragment是否可见,是否与View建立起映射关系,是否是第一次加载数据
    private final String TAG = getClass().getSimpleName().toLowerCase();//额外数据，查看log，观察当前activity是否被销毁

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log(TAG);
        convertView = inflater.inflate(getLayoutResID(), container, false);
        mUnbinder = ButterKnife.bind(this, convertView);
        initView();
        initEvent();
        lazyLoadData();
        return convertView;
    }

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
                ((InputMethodManager) activity.get().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 200);
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.get().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, 2);
        }
    }

    protected void closeDecor() {
        View decorView = activity.get().getWindow().peekDecorView();
        // 隐藏软键盘
        if (decorView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.get().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="赋值操作">
    protected boolean isEmpty(Object... objs) {
        for (Object obj : objs) {
            if (obj == null) {
                return true;
            } else if (obj instanceof String && obj.equals("")) {
                return true;
            }
        }
        return false;
    }

    protected String processedString(String source, String defaultStr) {
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

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    protected abstract int getLayoutResID();

    protected void initView() {
        ARouter.getInstance().inject(this);
        isInitView = true;
        activity = new WeakReference<>(getActivity());
        context = new WeakReference<>(getContext());
        presenter = getPresenter();
        if (null != presenter) {
            presenter.attachView(activity.get(), this);
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

    protected void initEvent() {

    }

    protected void initData() {

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

    @Override
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
    // </editor-fold>

}
