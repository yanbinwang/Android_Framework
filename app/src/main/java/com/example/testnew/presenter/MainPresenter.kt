package com.example.testnew.presenter

import com.example.testnew.presenter.contract.MainContract

/**
 * Created by WangYanBin on 2020/6/30.
 */
class MainPresenter : MainContract.Presenter() {

    override fun getUserInfo() {
        getView().showDialog()
        getView().getUserInfoSuccess(Any())

        //        addDisposable(BaseSubscribe.INSTANCE.download("dsfdsfds")
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