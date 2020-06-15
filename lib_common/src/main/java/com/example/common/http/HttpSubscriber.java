package com.example.common.http;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.bus.RxBus;
import com.example.common.bus.RxBusEvent;
import com.example.common.constant.ARouterPath;
import com.example.common.constant.Constants;
import com.example.common.utils.analysis.GsonUtil;
import com.example.common.utils.helper.AccountHelper;

import io.reactivex.subscribers.ResourceSubscriber;
import retrofit2.HttpException;

/**
 * author: wyb
 * date: 2019/7/30.
 * rxjava处理回调
 */
@SuppressWarnings("unchecked")
public abstract class HttpSubscriber<T> extends ResourceSubscriber<ResponseBody<T>> {

    @Override
    public void onNext(ResponseBody<T> responseBody) {
        doResult(responseBody, null);
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            okhttp3.ResponseBody responseBody = ((HttpException) throwable).response().errorBody();
            if (null != responseBody) {
                ResponseBody baseModel = GsonUtil.INSTANCE.jsonToObj(responseBody.string(), ResponseBody.class);
                doResult(baseModel, throwable);
            } else {
                doResult(null, throwable);
            }
        } catch (Exception e) {
            doResult(null, e);
        }
    }

    //处理请求
    private void doResult(ResponseBody<T> responseBody, Throwable throwable) {
        if (null != responseBody) {
            String msg = responseBody.getMsg();
            int e = responseBody.getE();
            if (0 == e) {
                onSuccess(responseBody.getData());
            } else {
                //账号还没有登录，解密失败，重新获取
                if (100005 == e || 100008 == e) {
                    AccountHelper.signOut();
                    RxBus.Companion.getInstance().post(new RxBusEvent(Constants.APP_USER_LOGIN_OUT));
                    ARouter.getInstance().build(ARouterPath.LoginActivity).navigation();
                }
                //账号被锁定--进入账号锁定页（其余页面不关闭）
                if (100002 == e) {
                    ARouter.getInstance().build(ARouterPath.UnlockIPActivity).navigation();
                }
                onFailed(throwable, msg);
            }
        } else {
            onFailed(throwable, "");
        }
        onFinish();
    }

    @Override
    public void onComplete() {
        if (!isDisposed()) {
            dispose();
        }
    }

    protected abstract void onSuccess(T data);

    protected abstract void onFailed(Throwable e, String msg);

    protected abstract void onFinish();

}
