package com.dataqin.testnew.activity

import android.graphics.Bitmap
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.app.hubert.guide.NewbieGuide
import com.app.hubert.guide.model.GuidePage
import com.app.hubert.guide.model.HighlightOptions
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Constants.CAMERA_FILE_PATH
import com.dataqin.common.utils.helper.GenerateHelper
import com.dataqin.media.utils.helper.MediaFileHelper
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding
import com.dataqin.testnew.databinding.ViewCertificateBinding


/**
 * Created by WangYanBin
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun initView() {
        super.initView()

        //https://github.com/huburt-Hu/NewbieGuide
        NewbieGuide.with(this)//传入activity
            .setLabel("guide1")//设置引导层标示，用于区分不同引导层，必传！否则报错
            .addGuidePage(GuidePage.newInstance()
                    .setLayoutRes(R.layout.view_guide_step_1)
                    .addHighLightWithOptions(
                        binding.btnCreate2, HighlightOptions.Builder()
                            .setOnClickListener {
                                showToast("highlight click")
                            }.build()))
            .alwaysShow(true)
            .show()//显示引导层
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_create -> {
                val certificateBinding = ViewCertificateBinding.inflate(LayoutInflater.from(this))
                certificateBinding.tvContext.text = "嗷嘮啊啦啦啦啦大師傅似的"
                GenerateHelper.create(
                    certificateBinding.root,
                    object : GenerateHelper.OnGenerateListener {
                        override fun onStart() {
                            showDialog()
                        }

                        override fun onResult(bitmap: Bitmap) {
                            val path = MediaFileHelper.getOutputMediaFile(
                                MEDIA_TYPE_IMAGE,
                                Constants.APPLICATION_NAME + "/" + CAMERA_FILE_PATH
                            )?.path
                            MediaFileHelper.saveBitmapToSd(bitmap, path, 100)
                            binding.ivCertificate.setImageBitmap(bitmap)
                        }

                        override fun onComplete() {
                            hideDialog()
                        }
                    })
            }
        }
    }

}