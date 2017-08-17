package com.wyb.iocframe.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.wyb.iocframe.config.CommonConfig;
import com.wyb.iocframe.util.log.LogUtil;
import com.wyb.iocframe.util.ToastUtil;


/**
 * Created by asus on 2016/3/26.
 */
public abstract class BaseFragment extends Fragment {
	// 当前Fragment是否可见
	private boolean isVisible = false; 
	// 是否与View建立起映射关系
	private boolean isInitView = false; 
	// 是否是第一次加载数据
	private boolean isFirstLoad = true;
	// 传入的View
	private View convertView;
	//软键盘的View
	private View decorView = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		LogUtil.m("   " + this.getClass().getSimpleName());
		convertView = inflater.inflate(getLayoutId(), container, false);
		FinalActivity.initInjectedView(this, convertView);
		initView();
		isInitView = true;
		lazyLoadData();
		return convertView;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LogUtil.m("   " + this.getClass().getSimpleName());
	}

	public void onAttach(Context context) {
		super.onAttach(context);
		LogUtil.m("context" + "   " + this.getClass().getSimpleName());
	}

	public void setUserVisibleHint(boolean isVisibleToUser) {
		LogUtil.m("isVisibleToUser " + isVisibleToUser + "   " + this.getClass().getSimpleName());
		if (isVisibleToUser) {
			isVisible = true;
			lazyLoadData();
		} else {
			isVisible = false;
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void lazyLoadData() {
		if (isFirstLoad) {
			LogUtil.m("第一次加载 " + " isInitView  " + isInitView + "  isVisible  " + isVisible + "   " + this.getClass().getSimpleName());
		} else {
			LogUtil.m("不是第一次加载" + " isInitView  " + isInitView + "  isVisible  " + isVisible + "   " + this.getClass().getSimpleName());
		}
		if (!isFirstLoad || !isVisible || !isInitView) {
			LogUtil.m("不加载" + "   " + this.getClass().getSimpleName());
			return;
		}

		LogUtil.m("完成数据第一次加载" + "   " + this.getClass().getSimpleName());
		initData();
		isFirstLoad = false;
	}

	// 加载页面布局文件
	protected abstract int getLayoutId();

	// 让布局中的view与fragment中的变量建立起映射
	protected abstract void initView();

	// 加载要显示的数据(已经做了懒加载)
	protected abstract void initData();

	// Toast 显示
	public void showToast(String str) {
		ToastUtil.mackToastSHORT(str, getContext().getApplicationContext());
	}
	
	// 关闭软键盘
	public void closeDecor() {
		decorView = getActivity().getWindow().peekDecorView();
		// 隐藏软键盘
		if (decorView != null) {
			InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
		}
	}
	
	// 获取手机版本
	protected String getVERSION() {
		String VERSION = android.os.Build.VERSION.RELEASE;
		return VERSION;
	}

	// 比例换算公式（手机宽度*转换长度/切图宽度---所占百分比）
	public int lengthConvert(int length) {
		return (CommonConfig.screenW * length / 640);
	}

	// 给控件换算比例后赋值长宽
	public void setParams(View view, int width, int height) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.width = width > 0 ? lengthConvert(width) : width;
		params.height = height > 0 ? lengthConvert(height) : height;
		view.setLayoutParams(params);
	}
	
	// 防止报空
	public String getStr(String source,String defaultStr){
		if(source == null){
			return defaultStr;
		}else {
			if(source.trim().isEmpty()){
				return defaultStr;
			}else {
				return source;
			}
		}
	}

}
