package com.dataqin.common.utils.helper.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.dataqin.common.R
import com.dataqin.common.widget.dialog.AndDialog
import com.dataqin.common.widget.dialog.callback.OnDialogListener
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import java.lang.ref.WeakReference
import java.text.MessageFormat

/**
 * author: wyb
 * date: 2018/6/11.
 * 获取选项工具类
 * 根据项目需求哪取需要的权限组
 */
class PermissionHelper(context: Context) {
    private var denied = true
    private var onPermissionCallBack: OnPermissionCallBack? = null
    private val weakContext = WeakReference(context)
    private val permissionGroup = arrayOf(
        Permission.Group.LOCATION,//定位
        Permission.Group.CAMERA,//拍摄照片，录制视频
        Permission.Group.MICROPHONE,//录制音频(腾讯x5)
        Permission.Group.STORAGE)//访问照片。媒体。内容和文件

    //检测权限(默认拿全部，可单独拿某个权限组)
    fun requestPermissions(): PermissionHelper {
        return requestPermissions(*permissionGroup)
    }

    fun requestPermissions(vararg groups: Array<String>): PermissionHelper {
        //6.0+系统做特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission()) {
                onPermissionCallBack?.onPermission(true)
            } else {
                AndPermission.with(weakContext.get())
                    .runtime()
                    .permission(*groups)
                    .onGranted {
                        //权限申请成功回调
                        onPermissionCallBack?.onPermission(true)
                    }
                    .onDenied {
                        //权限申请失败回调-安卓Q定位为使用期间允许，此处做一次检测
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if (checkSelfLocation() && it.size == 1 && listOf(*Permission.Group.LOCATION).contains(it[0])) {
                                onPermissionCallBack?.onPermission(true)
                                return@onDenied
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (checkSelfStorage() && it.size == 1 && listOf(*Permission.Group.STORAGE).contains(it[0])) {
                                onPermissionCallBack?.onPermission(true)
                                return@onDenied
                            }
                        }
                        onPermissionCallBack?.onPermission(false)
                        if (denied) {
                            var permissionIndex = 0
                            for (i in permissionGroup.indices) {
                                if (listOf(*permissionGroup[i]).contains(it[0])) {
                                    permissionIndex = i
                                    break
                                }
                            }
                            //提示参数
                            val result = when (permissionIndex) {
                                0 -> weakContext.get()?.getString(R.string.label_permissions_location)
                                1 -> weakContext.get()?.getString(R.string.label_permissions_camera)
                                2 -> weakContext.get()?.getString(R.string.label_permissions_microphone)
                                3 -> weakContext.get()?.getString(R.string.label_permissions_storage)
                                else -> null
                            }
                            //如果用户拒绝了开启权限
                            if (AndPermission.hasAlwaysDeniedPermission(weakContext.get(), it)) {
                                AndDialog.with(weakContext.get()).setOnDialogListener(object : OnDialogListener {
                                    override fun onConfirm() {
                                        weakContext.get()?.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + weakContext.get()?.packageName)))
                                    }

                                    override fun onCancel() {}
                                }).setParams(weakContext.get()?.getString(R.string.label_window_title), MessageFormat.format(weakContext.get()?.getString(R.string.label_window_permission), result), weakContext.get()?.getString(R.string.label_window_sure), weakContext.get()?.getString(R.string.label_window_cancel)).show()
                            }
                        }
                    }.start()
            }
        } else onPermissionCallBack?.onPermission(true)
        return this
    }

    fun setPermissionCallBack(onPermissionCallBack: OnPermissionCallBack, denied: Boolean = true): PermissionHelper {
        this.onPermissionCallBack = onPermissionCallBack
        this.denied = denied
        return this
    }

    private fun checkSelfPermission(): Boolean {
        var granted = true
        for (groupIndex in permissionGroup.indices) {
            if (0 == groupIndex) {
                granted = checkSelfLocation()
            } else {
                for (index in permissionGroup[groupIndex].indices) {
                    if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakContext.get()!!, permissionGroup[groupIndex][index])) granted = false
                }
            }
        }
        return granted
    }

    /**
     * 定位权限在安卓10开始有些许变化
     * 1.允许-前后台皆可定位
     * 2.仅在使用中允许-前台可定位，后台被拒绝
     * 3.拒绝-前后台都拒绝
     */
    fun checkSelfLocation(): Boolean {
        var granted = true
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakContext.get()!!, Permission.ACCESS_FINE_LOCATION)) granted = false
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakContext.get()!!, Permission.ACCESS_COARSE_LOCATION)) granted = false
        return granted
    }

    fun checkSelfStorage(): Boolean {
        var granted = true
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakContext.get()!!, Permission.READ_EXTERNAL_STORAGE)) granted = false
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(weakContext.get()!!, Permission.WRITE_EXTERNAL_STORAGE)) granted = false
        return granted
    }

    companion object {
        @JvmStatic
        fun with(context: Context?): PermissionHelper {
            return PermissionHelper(context!!)
        }
    }

}