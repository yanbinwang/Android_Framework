package com.example.common.utils.subscription;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.constant.ARouterPath;
import com.example.common.constant.Constants;
import com.example.common.utils.UserUtil;
import com.example.common.bean.BaseBean;
import com.example.framework.utils.GsonUtil;

import io.reactivex.subscribers.ResourceSubscriber;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * author: wyb
 * date: 2019/7/30.
 * rxjava处理回调
 */
@SuppressWarnings("unchecked")
public abstract class RxSubscribe<T> extends ResourceSubscriber<BaseBean<T>> {

    @Override
    public void onNext(BaseBean<T> baseBean) {
        doResult(baseBean, null);
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            ResponseBody responseBody = ((HttpException) throwable).response().errorBody();
            if (null != responseBody) {
                BaseBean baseBean = GsonUtil.INSTANCE.jsonToObj(responseBody.string(), BaseBean.class);
                doResult(baseBean, throwable);
            } else {
                doResult(null, throwable);
            }
        } catch (Exception e) {
            doResult(null, e);
        }
    }

    //处理请求
    private void doResult(BaseBean<T> baseBean, Throwable throwable) {
        if (null != baseBean) {
            String msg = baseBean.getMsg();
            int e = baseBean.getE();
            if (0 == e) {
                onSuccess(baseBean.getData());
            } else {
                //账号还没有登录，解密失败，重新获取
                if (100005 == e || 100008 == e) {
                    UserUtil.signOut();
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
