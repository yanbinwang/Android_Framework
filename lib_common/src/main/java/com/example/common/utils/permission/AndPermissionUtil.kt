package com.example.common.utils.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.example.common.R
import com.example.common.widget.dialog.AndDialog
import com.example.common.widget.dialog.callback.OnDialogListener
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import java.text.MessageFormat

/**
 * author: wyb
 * date: 2018/6/11.
 * 获取选项工具类
 * 根据项目需求哪取需要的权限组
 */
class AndPermissionUtil(private val context: Context) {
    private val permissionGroup = arrayOf(Permission.Group.CAMERA, //拍摄照片，录制视频
            Permission.Group.MICROPHONE, //录制音频(腾讯x5)
            Permission.Group.STORAGE) //访问照片。媒体。内容和文件

    //检测权限(默认拿全部，可单独拿某个权限组)
    fun checkPermission(onAndPermissionListener: OnAndPermissionListener?) {
        checkPermission(onAndPermissionListener, *permissionGroup)
    }

    fun checkPermission(onAndPermissionListener: OnAndPermissionListener?, vararg groups: Array<String>) {
        //6.0+系统做特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndPermission.with(context).runtime().permission(*groups).onGranted {
                // 权限申请成功回调
                onAndPermissionListener?.onAndPermissionListener(true)
            }.onDenied { permissions ->
                // 权限申请失败回调
                onAndPermissionListener?.onAndPermissionListener(false)
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
                        0 -> result = context.getString(R.string.label_permissions_camera)
                        1 -> result = context.getString(R.string.label_permissions_microphone)
                        2 -> result = context.getString(R.string.label_permissions_storage)
                    }
                }

                //如果用户拒绝了开启权限
                if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                     AndDialog(context).show(context.getString(R.string.label_dialog_title), MessageFormat.format(context.getString(R.string.label_dialog_permission), result), context.getString(R.string.label_dialog_sure), context.getString(R.string.label_dialog_cancel), object : OnDialogListener {
                        override fun onDialogConfirm() {
                            val packageURI = Uri.parse("package:" + context.packageName)
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                            context.startActivity(intent)
                        }

                        override fun onDialogCancel() {}
                    })
                }
            }.start()
        } else {
            onAndPermissionListener?.onAndPermissionListener(true)
        }
    }

}
