package com.example.common.base.proxy;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.example.base.utils.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by WangYanBin on 2020/8/3.
 * 防误触点击
 */
public class ApplicationActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(() -> proxyOnClick(activity.getWindow().getDecorView(), 5));
    }

    private void proxyOnClick(View view, int recycledContainerDeep) {
        if (view.getVisibility() == View.VISIBLE) {
            if (view instanceof ViewGroup) {
                boolean existAncestorRecycle = recycledContainerDeep > 0;
                ViewGroup p = (ViewGroup) view;
                if (!(p instanceof AbsListView) || existAncestorRecycle) {
                    getClickListenerForView(view);
                    if (existAncestorRecycle) {
                        recycledContainerDeep++;
                    }
                } else {
                    recycledContainerDeep = 1;
                }
                int childCount = p.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = p.getChildAt(i);
                    proxyOnClick(child, recycledContainerDeep);
                }
            } else {
                getClickListenerForView(view);
            }
        }
    }

    private void getClickListenerForView(View view) {
        try {
            Class viewClazz = Class.forName("android.view.View");
            //事件监听器都是这个实例保存的
            Method listenerInfoMethod = viewClazz.getDeclaredMethod("getListenerInfo");
            if (!listenerInfoMethod.isAccessible()) {
                listenerInfoMethod.setAccessible(true);
            }
            Object listenerInfoObj = listenerInfoMethod.invoke(view);
            Class listenerInfoClazz = Class.forName("android.view.View$ListenerInfo");
            Field onClickListenerField = listenerInfoClazz.getDeclaredField("mOnClickListener");

            if (!onClickListenerField.isAccessible()) {
                onClickListenerField.setAccessible(true);
            }
            View.OnClickListener mOnClickListener = (View.OnClickListener) onClickListenerField.get(listenerInfoObj);
            if (!(mOnClickListener instanceof ProxyOnclickListener)) {
                //自定义代理事件监听器
                View.OnClickListener onClickListenerProxy = new ProxyOnclickListener(mOnClickListener);
                //更换
                onClickListenerField.set(listenerInfoObj, onClickListenerProxy);
            } else {
                LogUtil.e("OnClickListenerProxy", "setted proxy listener ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ProxyOnclickListener implements View.OnClickListener {
        private View.OnClickListener onclick;
        private long lastClickTime = 0;

        ProxyOnclickListener(View.OnClickListener onclick) {
            this.onclick = onclick;
        }

        @Override
        public void onClick(View v) {
            //点击时间控制
            long currentTime = System.currentTimeMillis();
            int MIN_CLICK_DELAY_TIME = 500;
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                if (onclick != null) onclick.onClick(v);
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

}
