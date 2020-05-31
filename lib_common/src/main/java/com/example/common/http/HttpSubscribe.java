package com.example.common.http;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.bus.RxBus;
import com.example.common.model.BusModel;
import com.example.common.constant.ARouterPath;
import com.example.common.constant.Constants;
import com.example.common.utils.AccountUtil;
import com.example.common.model.BaseModel;
import com.example.common.utils.GsonUtil;

import io.reactivex.subscribers.ResourceSubscriber;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * author: wyb
 * date: 2019/7/30.
 * rxjava处理回调
 */
@SuppressWarnings("unchecked")
public abstract class HttpSubscribe<T> extends ResourceSubscriber<BaseModel<T>> {

    @Override
    public void onNext(BaseModel<T> baseModel) {
        doResult(baseModel, null);
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            ResponseBody responseBody = ((HttpException) throwable).response().errorBody();
            if (null != responseBody) {
                BaseModel baseModel = GsonUtil.INSTANCE.jsonToObj(responseBody.string(), BaseModel.class);
                doResult(baseModel, throwable);
            } else {
                doResult(null, throwable);
            }
        } catch (Exception e) {
            doResult(null, e);
        }
    }

    //处理请求
    private void doResult(BaseModel<T> baseModel, Throwable throwable) {
        if (null != baseModel) {
            String msg = baseModel.getMsg();
            int e = baseModel.getE();
            if (0 == e) {
                onSuccess(baseModel.getData());
            } else {
                //账号还没有登录，解密失败，重新获取
                if (100005 == e || 100008 == e) {
                    AccountUtil.signOut();
                    RxBus.Companion.getInstance().post(new BusModel(Constants.APP_USER_LOGIN_OUT));
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
