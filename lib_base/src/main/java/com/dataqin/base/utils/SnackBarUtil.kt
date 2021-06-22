package com.dataqin.base.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

/**
 * author:wyb
 * 统一提示框
 */
object SnackBarUtil {

    @Synchronized
    @JvmStatic
    fun mackSHORT(view: View, content: String, label: String, onClickListener: View.OnClickListener?) {
        Snackbar.make(view, content, Snackbar.LENGTH_SHORT)
            .setAction(label, onClickListener)
            .setActionTextColor(ContextCompat.getColor(view.context, android.R.color.black))
            .show()
    }

    @Synchronized
    @JvmStatic
    fun mackLONG(view: View, content: String, label: String, onClickListener: View.OnClickListener?) {
        Snackbar.make(view, content, Snackbar.LENGTH_LONG)
            .setAction(label, onClickListener)
            .setActionTextColor(ContextCompat.getColor(view.context, android.R.color.black))
            .show()
    }

}