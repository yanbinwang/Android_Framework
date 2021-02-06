package com.dataqin.testnew.activity

import android.graphics.Bitmap
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.imageloader.ImageLoader
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding
import com.dataqin.testnew.utils.CertificateHelper
import kotlinx.android.synthetic.main.activity_main.view.*

/**
 * Created by WangYanBin
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_create -> {
                CertificateHelper.create(
                    this,
                    "嗷嘮啊啦啦啦啦大師傅似的",
                    object : CertificateHelper.OnCertificateListener {
                        override fun onStart() {
                            showDialog()
                        }

                        override fun onResult(bitmap: Bitmap) {
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