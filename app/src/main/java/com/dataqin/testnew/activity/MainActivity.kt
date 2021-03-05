package com.dataqin.testnew.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding

/**
 * Created by WangYanBin
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_create -> {
//                CertificateHelper.create(
//                    activity.get()!!,
//                    "嗷嘮啊啦啦啦啦大師傅似的",
//                    object : CertificateHelper.OnCertificateListener {
//                        override fun onStart() {
//                            showDialog()
//                        }
//
//                        override fun onResult(bitmap: Bitmap) {
////                        val mediaStorageDir = File(
////                            Environment.getExternalStoragePublicDirectory(
////                                Environment.DIRECTORY_PICTURES
////                            ), Constants.APPLICATION_NAME + "/" + CAMERA_FILE_PATH
////                        )
////                        val timeStamp =
////                            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
////                                Date()
////                            )
////                        val path =
////                            File(mediaStorageDir.path + File.separator + timeStamp + ".jpg").path
//                            val path = MediaFileHelper.getOutputMediaFile(MEDIA_TYPE_IMAGE, Constants.APPLICATION_NAME + "/" + CAMERA_FILE_PATH)?.path
//                            MediaFileHelper.saveBitmapToSd(bitmap, path, 100)
//                            binding.ivCertificate.setImageBitmap(bitmap)
//                        }
//
//                        override fun onComplete() {
//                            hideDialog()
//                        }
//                    })
            }
        }
    }

}