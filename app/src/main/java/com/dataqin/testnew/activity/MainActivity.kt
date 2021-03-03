package com.dataqin.testnew.activity

import android.graphics.Bitmap
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_create -> {
                val certificateBinding = ViewCertificateBinding.inflate(LayoutInflater.from(this))
                certificateBinding.tvContext.text = "嗷嘮啊啦啦啦啦大師傅似的"
                GenerateHelper.create(certificateBinding.root, object : GenerateHelper.OnGenerateListener {
                        override fun onStart() {
                            showDialog()
                        }

                        override fun onResult(bitmap: Bitmap) {
                            val path = MediaFileHelper.getOutputMediaFile(MEDIA_TYPE_IMAGE, Constants.APPLICATION_NAME + "/" + CAMERA_FILE_PATH)?.path
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