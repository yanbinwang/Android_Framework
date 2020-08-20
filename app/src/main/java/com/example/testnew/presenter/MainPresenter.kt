package com.example.testnew.presenter

import com.example.common.http.HttpParams
import com.example.common.http.callback.HttpSubscriber
import com.example.common.subscribe.BaseSubscribe.getSendVerification
import com.example.testnew.presenter.contract.MainContract
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Created by WangYanBin on 2020/6/30.
 */
class MainPresenter : MainContract.Presenter() {

    override fun getUserInfo() {
//        getView().showDialog()
//        getView().getUserInfoSuccess(Any())

        addDisposable(
            getSendVerification("dsfdsfds", HttpParams().getParams())
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : HttpSubscriber<Any>() {

                    override fun onStart() {
                        super.onStart()
                        getView().log("开始")
                    }

                    override fun onSuccess(data: Any?) {}

                    override fun onFailed(e: Throwable?, msg: String?) {}

                    override fun onComplete() {
                        super.onComplete()
                        getView().log("结束")
                    }

                })
        )

//                addDisposable(BaseSubscribe.INSTANCE.download("dsfdsfds")
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new ResourceSubscriber<ResponseBody>() {
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable t) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                }));

//        addDisposable(BaseSubscribe.INSTANCE.getVerification("dsfsd",new HttpParams().append("dsadsa","sddas").getParams())
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new HttpSubscriber<Object>() {
//                    @Override
//                    protected void onSuccess(Object data) {
//
//                    }
//
//                    @Override
//                    protected void onFailed(Throwable e, String msg) {
//
//                    }
//
//                    @Override
//                    protected void onFinish() {
//
//                    }
//                }));
//        view?.get()?.hideDialog()
    }

}