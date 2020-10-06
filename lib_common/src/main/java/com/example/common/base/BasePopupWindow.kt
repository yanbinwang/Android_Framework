package com.example.common.base

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.viewbinding.ViewBinding
import com.example.common.R
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 * Created by WangYanBin on 2020/7/13.
 * 所有弹出窗口的基类，弹框本身操作并不应该复杂，只拿取对应的binding即可
 * 如果具有复杂的交互则直接写成activity加入对应的透明动效
 */
abstract class BasePopupWindow<VB : ViewBinding> : PopupWindow {
    protected lateinit var binding: VB
    protected var weakActivity: WeakReference<Activity>? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var dark = false

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    constructor(activity: Activity) {
        init(activity)
    }

    constructor(activity: Activity, dark: Boolean = false) {
        init(activity, dark)
    }

    private fun init(activity: Activity, dark: Boolean = false) {
        this.weakActivity = WeakReference(activity)
        this.layoutParams = weakActivity?.get()?.window?.attributes
        this.dark = dark
    }

    protected open fun initialize() {
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            try {
                val vbClass = (type as ParameterizedType).actualTypeArguments[0] as Class<VB>
                val method = vbClass.getMethod("inflate", LayoutInflater::class.java)
                binding = method.invoke(null, weakActivity!!.get()!!.layoutInflater) as VB
            } catch (e: Exception) {
                e.printStackTrace()
            }
            contentView = binding.root
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            animationStyle = R.style.pushBottomAnimStyle //默认底部弹出，可重写
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            setDismissAttributes()
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="重写方法">
    override fun showAsDropDown(anchor: View?) {
        setShowAttributes()
        super.showAsDropDown(anchor)
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int) {
        setShowAttributes()
        super.showAsDropDown(anchor, xoff, yoff)
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        setShowAttributes()
        super.showAsDropDown(anchor, xoff, yoff, gravity)
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        setShowAttributes()
        super.showAtLocation(parent, gravity, x, y)
    }

    private fun setShowAttributes() {
        if (dark) {
            layoutParams?.alpha = 0.7f
            weakActivity?.get()?.window?.attributes = layoutParams
        }
    }

    private fun setDismissAttributes() {
        if (dark) {
            setOnDismissListener {
                layoutParams?.alpha = 1f
                weakActivity?.get()?.window?.attributes = layoutParams
            }
        }
    }
    // </editor-fold>

}