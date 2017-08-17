package com.wyb.iocframe.util.net;

import android.content.Context;

import com.wyb.iocframe.util.log.LogUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.CacheMode;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectWebsiteUtil {
	//用来标志请求的what, 类似handler的what一样，这里用来区分请求
	private static final int NOHTTP_WHAT = 0x001;
	// 请求队列
    private RequestQueue requestQueue;
	// 返回string的值
    private Request<String> request;
	// 回调接口
    private NetworkCallback networkCallback = null;
	//持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载
	private static ConnectWebsiteUtil instance = null;
	//存储请求页面的上下文和队列
	private static Map<Context, List<Request>> excuteRequestsMap = new HashMap<>();

	//私有构造方法，防止被实例化
	private ConnectWebsiteUtil(){
		requestQueue = NoHttp.newRequestQueue();
	}

	//静态工程方法，创建实例
	public static synchronized ConnectWebsiteUtil getInstance(){
		instance = new ConnectWebsiteUtil();
		return instance;
	}

	public void toCallWebsite(Context context, RequestMethod method, String url, Map<String, String> headerMap, Map<String, String> paramsMap, NetworkCallback networkCallback){
		this.networkCallback = networkCallback;
		request = NoHttp.createStringRequest(url, method);
		request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
		WeakReference<Context> wContext = new WeakReference<>(context);
		//请求体头部
		if (headerMap != null) {
			Set<String> s = headerMap.keySet();
			Iterator<String> it = s.iterator();
			// 把key和value 循环的加入到params中
			while (it.hasNext()) {
				String key = it.next();
				String value = headerMap.get(key);
				request.addHeader(key, value);
			}
		}
		//请求参数
		if (paramsMap != null) {
			Set<String> s = paramsMap.keySet();
			Iterator<String> it = s.iterator();
			// 把key和value 循环的加入到params中
			while (it.hasNext()) {
				String key = it.next();
				String value = paramsMap.get(key);
				request.add(key, value);
			}
		}

		List<Request> requestList = excuteRequestsMap.get(context);
		if (null == requestList) {
			requestList = new ArrayList<>();
		}
		requestList.add(request);
		excuteRequestsMap.put(context, requestList);

		/*
         * what: 当多个请求同时使用同一个OnResponseListener时用来区分请求, 类似handler的what一样
         * request: 请求对象
         * onResponseListener 回调对象，接受请求结果
         */
		requestQueue.add(NOHTTP_WHAT, request, new OnCancelResponseListener(wContext, request));
	}

	//继承监听时传入弱引用的context和请求的request
	private class OnCancelResponseListener implements OnResponseListener<String> {
		private Request request = null;
		private WeakReference<Context> wContext = null;

		public OnCancelResponseListener(WeakReference<Context> context, Request request) {
			super();
			this.wContext = context;
			this.request = request;
		}

		// 开始请求
		public void onStart(int what) {}

		// 请求成功带返回
		public void onSucceed(int what, Response<String> response) {
			// 请求成功,响应结果
			String result = (String) response.get();
			if(networkCallback != null){
				networkCallback.onSuccessCallBack(result);
			}
			LogUtil.e("response", result);
		}

		// 请求失败
		public void onFailed(int what, Response<String> response) {
			if (networkCallback != null) {
				networkCallback.onFailureCallBack(response.getException());
			}
		}

		// 请求完成
		public void onFinish(int what) {
			//从map里面把自己移除掉
			if (null != wContext && null != wContext.get()) {
				Context context = wContext.get();
				List<Request> requestList = excuteRequestsMap.get(context);
				if (null != requestList) {
					requestList.remove(request);
				}
				if (null == requestList && 0 == requestList.size()) {
					excuteRequestsMap.remove(context);
				}
			}
		}
	}

	//页面结束后当前页面请求停止
	public static void cancelRequestByKey(Context context) {
		if (null != excuteRequestsMap) {
			List<Request> requestList = excuteRequestsMap.get(context);
			if (null != requestList) {
				for (Request request : requestList) {
					request.cancel();
				}
				requestList.clear();
			}
			excuteRequestsMap.remove(context);
		}
	}

	public interface NetworkCallback{
		void onSuccessCallBack(String result);

		void onFailureCallBack(Exception e);
	}
	
}