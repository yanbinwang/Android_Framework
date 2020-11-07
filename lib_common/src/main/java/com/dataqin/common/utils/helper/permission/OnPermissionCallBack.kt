package com.dataqin.common.utils.helper.permission

/**
 * author: wyb
 * date: 2018/6/11.
 * 权限回调
 */
interface OnPermissionCallBack {

    fun onPermissionListener(isGranted: Boolean = false)

}
