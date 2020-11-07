package com.dataqin.common.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.viewbinding.ViewBinding
import com.dataqin.base.utils.AnimationLoader.getInAnimation
import com.dataqin.base.utils.AnimationLoader.getOutAnimation
import com.dataqin.common.R
import java.lang.reflect.ParameterizedType

/**
 * Created by WangYanBin on 2020/7/13.
 * 所有弹框的基类
 */
abstract class BaseDialog<VB : ViewBinding> : Dialog {
    protected lateinit var binding: VB

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    constructor(context: Context) : super(context, R.style.appDialogStyle)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    protected open fun initialize() {
        initialize(anim = false, close = false)
    }

    /**
     * 设置布局
     *
     * @param anim  是否有进入动画
     * @param close 是否可以关闭
     */
    protected open fun initialize(anim: Boolean = false, close: Boolean = false) {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            try {
                val vbClass = type.actualTypeArguments[0] as? Class<VB>
                val method = vbClass?.getMethod("inflate", LayoutInflater::class.java)
                binding = method?.invoke(null, layoutInflater) as VB
            } catch (e: Exception) {
                e.printStackTrace()
            }
            setContentView(binding.root, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
            if (anim) {
                val mAnimIn = getInAnimation(context)
                val mAnimOut = getOutAnimation(context)
                //当布局show出来的时候执行开始动画
                setOnShowListener { dialog: DialogInterface? ->
                    binding.root.startAnimation(mAnimIn)
                }
                //当布局销毁时执行结束动画
                setOnDismissListener { dialog: DialogInterface? ->
                    binding.root.startAnimation(mAnimOut)
                }
            }
            if (close) {
                setOnKeyListener { _: DialogInterface?, _: Int, _: KeyEvent? -> true }
                setCancelable(true)
            }
        }
    }
    // </editor-fold>

}