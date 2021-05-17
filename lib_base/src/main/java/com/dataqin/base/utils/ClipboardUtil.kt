package com.dataqin.base.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * 粘贴板工具类
 */
object ClipboardUtil {

    @JvmStatic
    fun putTextIntoClip(context: Context, label: String, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //添加ClipData对象到剪切板中
        clipboardManager.primaryClip = ClipData.newPlainText(label, text)
        if (clipboardManager.hasPrimaryClip()) clipboardManager.primaryClip?.getItemAt(0)?.text
    }

    @JvmStatic
    fun getTextFromClip(context: Context): String {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //判断剪切版时候有内容
        if (!clipboardManager.hasPrimaryClip()) return ""
        //获取 text
        return clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
    }

}
