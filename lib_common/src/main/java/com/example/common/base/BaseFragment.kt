package com.example.common.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.example.base.utils.LogUtil
import com.example.base.utils.ToastUtil
import com.example.common.base.bridge.BaseImpl
import com.example.common.base.bridge.BasePresenter
import com.example.common.base.bridge.BaseView
import com.example.common.base.page.PageParams
import com.example.common.base.proxy.SimpleTextWatcher
import com.example.common.bus.RxManager
import com.example.common.constant.Extras
import com.example.common.utils.builder.StatusBarBuilder
import com.example.common.widget.dialog.LoadingDialog
import io.reactivex.rxjava3.disposables.Disposable
import java.io.Serializable
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.util.*

/**
 * Created by wyb on 2016/3/26.
 * 基础fragment的基类
 * 适用于fragmentmanager管理显示隐藏fragment
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment(), BaseImpl, BaseView {
    protected lateinit var binding: VB
    protected val activity by lazy { WeakReference<Activity>(getActivity()) } //基类activity弱引用
    protected val context by lazy { WeakReference<Context>(getContext()) }//基类context弱引用
    protected val statusBarBuilder by lazy { StatusBarBuilder(activity.get()!!) }//状态栏工具类
    private var presenter: BasePresenter<*>? = null//P层
    private val rxManager by lazy { RxManager() } //事务管理器
    private val loadingDialog by lazy { LoadingDialog(context.get()) }//刷新球控件，相当于加载动画
    private val TAG = javaClass.simpleName.toLowerCase(Locale.getDefault()) //额外数据，查看log，观察当前activity是否被销毁

    // <editor-fold defaultstate="collapsed" desc="基类方法">
    protected fun addDisposable(disposable: Disposable?) {
        if (null != disposable) {
            rxManager.add(disposable)
        }
    }

    protected fun <P : BasePresenter<*>> createPresenter(pClass: Class<P>): P {
        if (presenter == null) {
            try {
                presenter = pClass.newInstance()
                presenter?.initialize(activity.get(), context.get(), this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return presenter as P
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log(TAG)
        val superclass = javaClass.genericSuperclass
        val aClass = (superclass as ParameterizedType).actualTypeArguments[0] as Class<*>
        try {
            val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.javaPrimitiveType)
            binding = method.invoke(null, layoutInflater, container, false) as VB
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initEvent()
        initData()
    }

    override fun initView() {
        ARouter.getInstance().inject(this)
    }

    override fun initEvent() {
    }

    override fun initData() {
    }

    override fun isEmpty(vararg objs: Any?): Boolean {
        for (obj in objs) {
            if (obj == null) {
                return true
            } else if (obj is String && obj == "") {
                return true
            }
        }
        return false
    }

    override fun openDecor(view: View?) {
        closeDecor(view)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                (activity.get()?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }, 200)
        val inputMethodManager = activity.get()?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, 2)
    }

    override fun closeDecor(view: View?) {
        val inputMethodManager = activity.get()?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun getFocus(view: View?) {
        view?.isFocusable = true //设置输入框可聚集
        view?.isFocusableInTouchMode = true //设置触摸聚焦
        view?.requestFocus() //请求焦点
        view?.findFocus() //获取焦点
    }

    override fun getParameters(view: View?): String? {
        return when (view) {
            is EditText -> view.text.toString().trim { it <= ' ' }
            is TextView -> view.text.toString().trim { it <= ' ' }
            is CheckBox -> view.text.toString().trim { it <= ' ' }
            is RadioButton -> view.text.toString().trim { it <= ' ' }
            is Button -> view.text.toString().trim { it <= ' ' }
            else -> null
        }
    }

    override fun onTextChanged(simpleTextWatcher: SimpleTextWatcher?, vararg views: View?) {
        for (view in views) {
            if (view is EditText) {
                view.addTextChangedListener(simpleTextWatcher)
            }
        }
    }

    override fun onClick(onClickListener: View.OnClickListener?, vararg views: View?) {
        for (view in views) {
            view?.setOnClickListener(onClickListener)
        }
    }

    override fun VISIBLE(vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.visibility = View.VISIBLE
            }
        }
    }

    override fun INVISIBLE(vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.visibility = View.INVISIBLE
            }
        }
    }

    override fun GONE(vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rxManager.clear()
        if (presenter != null) {
            presenter?.detachView()
        }
        log("onDestroy...")
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="BaseView实现方法-初始化一些工具类和全局的订阅">
    override fun log(msg: String) {
        LogUtil.e(TAG, msg)
    }

    override fun showToast(msg: String) {
        ToastUtil.mackToastSHORT(msg, requireContext().applicationContext)
    }

    override fun showDialog(flag: Boolean) {
        loadingDialog.show(flag)
    }

    override fun hideDialog() {
        loadingDialog.hide()
    }

    override fun navigation(path: String, params: PageParams?): Activity {
        val postcard = ARouter.getInstance().build(path)
        var code: Int? = null
        if (params != null) {
            val map: Map<String, Any> = params.getParams()
            for (key in map.keys) {
                val value = map[key]
                val cls: Class<*> = value?.javaClass!!
                if (key == Extras.REQUEST_CODE) {
                    code = value as Int?
                    continue
                }
                when {
                    cls == String::class.java -> postcard.withString(key, value as String?)
                    value is Parcelable -> postcard.withParcelable(key, value as Parcelable?)
                    value is Serializable -> postcard.withSerializable(key, value as Serializable?)
                    cls == Int::class.javaPrimitiveType -> postcard.withInt(key, value as Int)
                    cls == Long::class.javaPrimitiveType -> postcard.withLong(key, value as Long)
                    cls == Boolean::class.javaPrimitiveType -> postcard.withBoolean(
                        key,
                        value as Boolean
                    )
                    cls == Float::class.javaPrimitiveType -> postcard.withFloat(key, value as Float)
                    cls == Double::class.javaPrimitiveType -> postcard.withDouble(
                        key,
                        value as Double
                    )
                    cls == CharArray::class.java -> postcard.withCharArray(key, value as CharArray?)
                    cls == Bundle::class.java -> postcard.withBundle(key, value as Bundle?)
                    else -> throw RuntimeException("不支持参数类型" + ": " + cls.simpleName)
                }
            }
        }
        if (code == null) {
            postcard.navigation()
        } else {
            postcard.navigation(activity.get(), code)
        }
        return activity.get()!!
    }
    // </editor-fold>

}