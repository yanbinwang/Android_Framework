package com.example.testnew.activity;

import com.example.common.base.BaseActivity;
import com.example.common.subscribe.BaseSubscribe;
import com.example.testnew.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
import okhttp3.ResponseBody;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        addDisposable(BaseSubscribe.INSTANCE.download("dsfdsfds")
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new ResourceSubscriber<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

}
