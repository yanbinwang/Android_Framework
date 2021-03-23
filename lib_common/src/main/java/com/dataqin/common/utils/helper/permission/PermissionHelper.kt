package com.dataqin.common.utils.helper.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
    private val weakContext = WeakReference(context)
    private val permissionGroup = arrayOf(
        Permission.Group.LOCATION,//定位
        Permission.Group.CAMERA,//拍摄照片，录制视频
        Permission.Group.MICROPHONE,//录制音频(腾讯x5)
        Permission.Group.STORAGE//访问照片。媒体。内容和文件
    )
    private var onPermissionCallBack: OnPermissionCallBack? = null

    //检测权限(默认拿全部，可单独拿某个权限组)
    fun getPermissions(): PermissionHelper {
        return getPermissions(*permissionGroup)
    }

    fun getPermissions(vararg groups: Array<String>): PermissionHelper {
        //6.0+系统做特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndPermission.with(weakContext.get())
                .runtime()
                .permission(*groups)
                .onGranted {
                    //权限申请成功回调
                    onPermissionCallBack?.onPermissionListener(true)
                }
                .onDenied { permissions ->
                    //权限申请失败回调
                    onPermissionCallBack?.onPermissionListener(false)
                    //提示参数
                    var result: String? = null
                    if (permissions.isNotEmpty()) {
                        var permissionIndex = 0
                        for (i in permissionGroup.indices) {
                            if (listOf(*permissionGroup[i]).contains(permissions[0])) {
                                permissionIndex = i
                                break
                            }
                        }
                        when (permissionIndex) {
                            0 -> result = weakContext.get()?.getString(R.string.label_permissions_location)
                            1 -> result = weakContext.get()?.getString(R.string.label_permissions_camera)
                            2 -> result = weakContext.get()?.getString(R.string.label_permissions_microphone)
                            3 -> result = weakContext.get()?.getString(R.string.label_permissions_storage)
                        }
                    }

                    //如果用户拒绝了开启权限
                    if (AndPermission.hasAlwaysDeniedPermission(weakContext.get(), permissions)) {
                        AndDialog.with(weakContext.get())
                            .setParams(weakContext.get()?.getString(R.string.label_dialog_title), MessageFormat.format(weakContext.get()?.getString(R.string.label_dialog_permission), result), weakContext.get()?.getString(R.string.label_dialog_sure), weakContext.get()?.getString(R.string.label_dialog_cancel))
                            .setOnDialogListener(object : OnDialogListener {
                                override fun onDialogConfirm() {
                                    val packageURI = Uri.parse("package:" + weakContext.get()?.packageName)
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                                    weakContext.get()?.startActivity(intent)
                                }

                                override fun onDialogCancel() {}
                            }).show()
                    }
                }.start()
        } else {
            onPermissionCallBack?.onPermissionListener(true)
        }
        return this
    }

    fun setPermissionCallBack(onPermissionCallBack: OnPermissionCallBack): PermissionHelper {
        this.onPermissionCallBack = onPermissionCallBack
        return this
    }

    companion object {
        @JvmStatic
        fun with(context: Context?): PermissionHelper {
            return PermissionHelper(context!!)
        }
    }

}
