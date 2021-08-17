package com.dataqin.testnew.activity

import android.content.Intent
import android.os.Build
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.base.utils.WeakHandler
import com.dataqin.common.base.BaseTitleActivity
import com.dataqin.common.base.page.PageParams
import com.dataqin.common.bus.RxBus
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Extras
import com.dataqin.common.constant.RequestCode
import com.dataqin.common.imageloader.ImageLoader
import com.dataqin.common.imageloader.glide.callback.progress.OnLoaderListener
import com.dataqin.common.utils.file.FileUtil
import com.dataqin.common.widget.advertising.callback.OnAdvertisingClickListener
import com.dataqin.map.utils.helper.fadeIn
import com.dataqin.map.utils.helper.fadeOut
import com.dataqin.map.utils.helper.hidden
import com.dataqin.map.utils.helper.shown
import com.dataqin.media.service.ScreenShotObserver
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding
import com.dataqin.testnew.presenter.contract.MainContract
import com.dataqin.testnew.widget.popup.AddressPopup
import com.dataqin.testnew.widget.popup.EditPopup
import java.io.File


/**
 * Created by WangYanBin
 * 地图库采用高德地图，获取经纬度必须具备定位权限
 * 如果进应用就是地图，则在进首页前先给个软提示页面，列出所有权限问用户索要，如果还不接受，则直接进应用，在地图onload生命周期结束后，先移动到给定的默认位置，
 * 再进首页前弹出拦截的权限按钮进行权限的索要
 * cameraview_tts
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseTitleActivity<ActivityMainBinding>(), View.OnClickListener, MainContract.View {
    private var srcPath = ""
    //    private val presenter by lazy { createPresenter(MainPresenter::class.java) }
    private val addressPopup by lazy { AddressPopup(this) }
    private val editPopup by lazy { EditPopup(this) }

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

        ImageLoader.instance.displayProgressImage(
            binding.ivLoading,
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fattach.bbs.miui.com%2Fforum%2F201312%2F03%2F165526ophx4l6c6ll3cnpl.jpg&refer=http%3A%2F%2Fattach.bbs.miui.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1626503458&t=0e2ad4aa991a1788dd91eed69ecf40b7",
            object : OnLoaderListener {
                override fun onStart() {
                }

                override fun onProgress(progress: Int) {
                }

                override fun onComplete() {
                }
            })

        val list = listOf(
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201708%2F04%2F20170804135156_metTN.thumb.400_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628070617&t=d90a27b306f2d7ce4c0d18a6744bca86",
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fphoto.tuchong.com%2F1336313%2Ff%2F977802912.jpg&refer=http%3A%2F%2Fphoto.tuchong.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1628070617&t=82da0c88197102345a628bc2239bcf50",
            "https://gimg2.baidu.com/image_search/src=http%3A%2…sec=1628070617&t=f8d42102c61a51f746676b2ea2dbdd30"
        )
        binding.adGallery.onStart(list, binding.llPoint)
        binding.adGallery.setOnAdvertisingClickListener(object :OnAdvertisingClickListener{
            override fun onItemClick(index: Int) {
                showToast("当前选中了${index}")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.adGallery.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.adGallery.onPause()
    }

    override fun initEvent() {
        super.initEvent()
        onClick(this, binding.btnTest, binding.btnTest2, binding.btnTest3, binding.btnTest4, binding.btnTest5)
        ScreenShotObserver.instance.register()

        addDisposable(RxBus.instance.toFlowable {
            when (it.getAction()) {
//                Constants.APP_MAP_CONNECTIVITY -> MapHelper.location(this)
                Constants.APP_SHOT_PATH -> srcPath = it.getStringExtra()
            }
        })

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

    private fun startZip() {
        if (!TextUtils.isEmpty(srcPath)) {
            showDialog()
            Thread {
                val fileDir = File(srcPath)
                val zipFile = File("${Constants.SDCARD_PATH}/10086.zip")
                try {
                    if (fileDir.exists()) FileUtil.zipFolder(
                        fileDir.absolutePath,
                        zipFile.absolutePath
                    )
                } catch (e: Exception) {
                    log("打包图片生成压缩文件异常: $e")
                } finally {
                    WeakHandler(Looper.getMainLooper()).post { hideDialog() }
                }
            }.start()
        } else showToast("先截图！")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_test -> binding.tvView.fadeIn()
            R.id.btn_test2 -> binding.tvView.fadeOut()
            R.id.btn_test3 -> binding.tvView.shown()
            R.id.btn_test4 -> binding.tvView.hidden()
//            R.id.btn_test5 -> LocationFactory.instance.settingGps(activity.get()!!)
            R.id.btn_test5 -> {
//                val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 10086);
//                editPopup.showPopup(v)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (Environment.isExternalStorageManager()) {
//                        startZip()
//                    } else {
//                        val intent = Intent()
//                        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
//                        intent.data = Uri.parse("package:" + Constants.APPLICATION_ID)
//                        startActivity(intent)
//                    }
//                } else {
//                    PermissionHelper.with(this).setPermissionCallBack(object : OnPermissionCallBack {
//                        override fun onPermission(isGranted: Boolean) {
//                            if (isGranted) startZip()
//                        }
//                    }).getPermissions(Permission.Group.STORAGE)
//                }
//                log(NetWorkUtil.getWifiSecurity())
//                navigation(ARouterPath.CameraActivity)
//                PermissionHelper.with(this)
//                    .setPermissionCallBack(object :OnPermissionCallBack{
//                        override fun onPermission(isGranted: Boolean) {
//                            if(isGranted){
//                                showToast("已经授权")
//                            }else{
//                                showToast("未授权")
//                            }
//                        }
//                    }).getPermissions()
//                addressPopup.showPopup(v)
//                navigation(ARouterPath.TransActivity)
                navigation(
                    ARouterPath.ScaleActivity,
                    PageParams().append(
                        Extras.FILE_PATH,
                        listOf(
                            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F2017-12-06%2F5a2795b48ab8c.jpg%3Fdown&refer=http%3A%2F%2Fpic1.win4000.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1619340383&t=d6165e069cd6c28c2296496b074784d4",
                            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg1.3lian.com%2F2015%2Fa1%2F144%2Fd%2F83.jpg&refer=http%3A%2F%2Fimg1.3lian.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1619341141&t=a25aa81f9e0e7cbf611281bfc7f7a486",
                            "https://ss1.baidu.com/-4o3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/ca1349540923dd54ea2076a4d309b3de9d8248af.jpg"
                        )
                    )
                )
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

    override fun onDestroy() {
        super.onDestroy()
        ScreenShotObserver.instance.unregister()
//        LocationFactory.instance.stop()
    }

}