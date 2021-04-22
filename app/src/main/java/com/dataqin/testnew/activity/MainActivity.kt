package com.dataqin.testnew.activity

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.RequestCode
import com.dataqin.map.utils.helper.fadeIn
import com.dataqin.map.utils.helper.fadeOut
import com.dataqin.map.utils.helper.hidden
import com.dataqin.map.utils.helper.shown
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding
import com.dataqin.testnew.presenter.contract.MainContract
import com.dataqin.testnew.widget.popup.AddressPopup

/**
 * Created by WangYanBin
 * 地图库采用高德地图，获取经纬度必须具备定位权限
 * 如果进应用就是地图，则在进首页前先给个软提示页面，列出所有权限问用户索要，如果还不接受，则直接进应用，在地图onload生命周期结束后，先移动到给定的默认位置，
 * 再进首页前弹出拦截的权限按钮进行权限的索要
 * cameraview_tts
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseTitleActivity<ActivityMainBinding>(), View.OnClickListener, MainContract.View {
//    private val presenter by lazy { createPresenter(MainPresenter::class.java) }
    private val addressPopup by lazy { AddressPopup(this) }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        MapHelper.initialize(savedInstanceState, binding.map)
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        MapHelper.saveInstanceState(outState)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        MapHelper.resume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        MapHelper.pause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        MapHelper.destroy()
//    }

    override fun initView() {
        super.initView()
        titleBuilder.setTitle("控制台").hideBack()
//        presenter.setEmptyView(baseBinding.flBaseContainer)
//        PopupHelper.initialize(this)
//        //不需要更新传一个Any，需要的传VersionModel
//        PopupHelper.addPopup(0,Any())
//        PopupHelper.addPopup(1,Any())
//        PopupHelper.addPopup(2,Any())
//        PopupHelper.addPopup(3,Any())
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnTest, binding.btnTest2, binding.btnTest3, binding.btnTest4, binding.btnTest5)

//        addDisposable(RxBus.instance.toFlowable {
//            when (it.getAction()) {
//                Constants.APP_MAP_CONNECTIVITY -> MapHelper.location(this)
//            }
//        })

//        presenter.getEmptyView()?.setOnEmptyRefreshListener(object : OnEmptyRefreshListener {
//            override fun onRefreshListener() {
//                presenter.getOperation()
//            }
//        })
//
//        LocationFactory.instance.start(this)
//        LocationFactory.instance.locationSubscriber = object : LocationSubscriber() {
//            override fun onSuccess(model: AMapLocation) {
//                super.onSuccess(model)
//                if (normal) {
//                    MapHelper.moveCamera(LatLng(model.latitude, model.longitude))
//                } else {
//                    //执行打卡
//                }
//            }
//
//            override fun onFailed() {
//                super.onFailed()
//                if (normal) {
//                    MapHelper.moveCamera()
//                } else {
//                    LocationFactory.instance.settingGps(activity.get()!!)
//                }
//            }
//        }
//        LocationFactory.instance.start(this)
    }

//    //获取本地省市区文件
//    private fun getAddress() {
//        val stringBuilder = StringBuilder()
//        try {
//            val bufferedReader = BufferedReader(InputStreamReader(assets.open("pcas-code.json")))
//            var str = ""
//            while (null != bufferedReader.readLine().also { str = it }) {
//                stringBuilder.append(str)
//            }
//        } catch (e: IOException) {
//            stringBuilder.delete(0, stringBuilder.length)
//        } finally {
//            val result = stringBuilder.toString()
//            if (!TextUtils.isEmpty(result)) {
//                val addressList = Gson().fromJson<List<AddressModel>>(
//                    result,
//                    object : TypeToken<List<AddressModel>>() {}.type
//                )
//            }
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_test -> binding.tvView.fadeIn()
            R.id.btn_test2 -> binding.tvView.fadeOut()
            R.id.btn_test3 -> binding.tvView.shown()
            R.id.btn_test4 -> binding.tvView.hidden()
//            R.id.btn_test5 -> LocationFactory.instance.settingGps(activity.get()!!)
            R.id.btn_test5 -> {
                addressPopup.showPopup()
//                navigation(ARouterPath.TransActivity)
//                navigation(
//                    ARouterPath.ScaleActivity,
//                    PageParams().append(
//                        Extras.FILE_PATH,
//                        listOf(
//                            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-12-06%2F5a2795b48ab8c.jpg%3Fdown&refer=http%3A%2F%2Fpic1.win4000.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1619340383&t=d6165e069cd6c28c2296496b074784d4",
//                            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg1.3lian.com%2F2015%2Fa1%2F144%2Fd%2F83.jpg&refer=http%3A%2F%2Fimg1.3lian.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1619341141&t=a25aa81f9e0e7cbf611281bfc7f7a486",
//                            "https://ss1.baidu.com/-4o3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/ca1349540923dd54ea2076a4d309b3de9d8248af.jpg"
//                        )
//                    )
//                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                showToast("开启")
            } else {
                showToast("没开")
            }
        }
    }

    override fun getOperation() {
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        LocationFactory.instance.stop()
//    }

}