package com.dataqin.testnew.presenter

import com.dataqin.common.bus.RxSchedulers
import com.dataqin.common.constant.Constants
import com.dataqin.common.http.repository.HttpParams
import com.dataqin.common.http.repository.HttpSubscriber
import com.dataqin.common.subscribe.CommonSubscribe.getSendVerificationApi
import com.dataqin.common.utils.file.callback.OnDownloadListener
import com.dataqin.common.utils.file.factory.DownloadFactory
import com.dataqin.common.utils.helper.permission.OnPermissionCallBack
import com.dataqin.common.utils.helper.permission.PermissionHelper
import com.dataqin.testnew.presenter.contract.MainContract
import com.yanzhenjie.permission.runtime.Permission

/**
 * Created by WangYanBin on 2020/6/30.
 */
class MainPresenter : MainContract.Presenter() {

    override fun getUserInfo() {
//        getView().showDialog()
//        getView().getUserInfoSuccess(Any())

        addDisposable(
                getSendVerificationApi("dsfdsfds", HttpParams().signParams())
                        .compose(RxSchedulers.ioMain())
//                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : HttpSubscriber<Any>() {

                            override fun onStart() {
                                super.onStart()
                                getView()?.log("开始")
                            }

                            override fun onSuccess(data: Any?) {}

                            override fun onFailed(e: Throwable?, msg: String?) {}

                            override fun onComplete() {
                                super.onComplete()
                                getView()?.log("结束")
                            }

                        })
        )
    }

    override fun getDownload() {
        PermissionHelper.with(getContext())
                .setPermissionCallBack(object : OnPermissionCallBack {

                    override fun onPermissionListener(isGranted: Boolean) {
                        if (isGranted) {
                            val filePath = Constants.APPLICATION_FILE_PATH + "/安装包"
                            val fileName = Constants.APPLICATION_NAME + ".apk"
                            DownloadFactory.instance.download("https://ucan.25pp.com/Wandoujia_web_seo_baidu_homepage.apk", filePath, fileName, object :
                                    OnDownloadListener {

                                override fun onStart() {
                                    getView()?.showDialog()
                                }

                                override fun onSuccess(path: String?) {

                                }

                                override fun onLoading(progress: Int) {
                                }

                                override fun onFailed(e: Throwable?) {

                                }

                                override fun onComplete() {
                                    getView()?.hideDialog()
                                }

                            })
                        }
                    }
                }).getPermissions(Permission.Group.STORAGE)
    }

}