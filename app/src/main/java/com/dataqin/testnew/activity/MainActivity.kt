package com.dataqin.testnew.activity

import android.graphics.Bitmap
import android.os.Environment
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.dataqin.base.utils.LogUtil
import com.dataqin.common.base.BaseActivity
import com.dataqin.common.constant.ARouterPath
import com.dataqin.common.constant.Constants
import com.dataqin.common.constant.Constants.CAMERA_FILE_PATH
import com.dataqin.testnew.R
import com.dataqin.testnew.databinding.ActivityMainBinding
import com.dataqin.testnew.utils.CertificateHelper
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by WangYanBin
 */
@Route(path = ARouterPath.MainActivity)
class MainActivity : BaseActivity<ActivityMainBinding>(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_create -> {
                CertificateHelper.create(
                    activity.get()!!,
                    "嗷嘮啊啦啦啦啦大師傅似的",
                    object : CertificateHelper.OnCertificateListener {
                        override fun onStart() {
                            showDialog()
                        }

                        override fun onResult(bitmap: Bitmap) {
                            val mediaStorageDir = File(
                                Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES
                                ), Constants.APPLICATION_NAME + "/" + CAMERA_FILE_PATH
                            )
                            val timeStamp =
                                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                                    Date()
                                )
                            val path =
                                File(mediaStorageDir.path + File.separator + timeStamp + ".jpg").path
                            saveBitmapToSd(bitmap, path, 100)
                            binding.ivCertificate.setImageBitmap(bitmap)
                        }

                        override fun onComplete() {
                            hideDialog()
                        }
                    })
            }
        }
    }

    fun saveBitmapToSd(bitmap: Bitmap, path: String?, quality: Int): Boolean {
        val f = File(path)
        if (f.exists()) {
            f.delete()
        }
        return try {
            val out = FileOutputStream(f)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.flush()
            out.close()
            true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}