package com.wyb.iocframe.util.net;

import android.content.Context;

import com.wyb.iocframe.config.CommonConfig;
import com.wyb.iocframe.util.log.LogUtil;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.CacheMode;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectServerUtil<T>  {
	//请求服务器返回码成功
	private static final int HTTP_SUCCESS = 200;
	//用来标志请求的what, 类似handler的what一样，这里用来区分请求
	private static final int NOHTTP_WHAT = 0x001;
	// 请求队列
    private RequestQueue requestQueue;
	// 返回string的值
    private Request<String> request;
	// 回调接口
    private NetworkCallback<T> networkCallback = null;
	//传入用于返回的class
	private Class<T> clazz;
	//持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载
	private static ConnectServerUtil instance  = null;
	//存储请求页面的上下文和队列
	private static Map<Context, List<Request>> excuteRequestsMap = new HashMap<>();

	//私有构造方法，防止被实例化
	private ConnectServerUtil(){
		requestQueue = NoHttp.newRequestQueue();
	}

	//静态工程方法，创建实例(nohttp本身类似handler通过发送不同的what来返回不同参数，如果是单例模式，每次需要重新初始化)
	public static synchronized ConnectServerUtil getInstance() {
		instance = new ConnectServerUtil();
		return instance;
	}

	//取得接口中的泛型T的类型
	public void getCallBackClass(NetworkCallback<T> networkCallback){
		if(networkCallback != null){
			try {
				//获取接口中的泛型类
				Type[] interfacesTypes = networkCallback.getClass().getGenericInterfaces();
				for (Type t : interfacesTypes) {
					Type[] genericType = ((ParameterizedType) t).getActualTypeArguments();
					for (Type t2 : genericType) {
						this.clazz = (Class<T>)t2;
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void toConntectServer(Context context, RequestMethod method, String url, Map<String, String> paramsMap , NetworkCallback<T> networkCallback){
		this.networkCallback = networkCallback;
		request = NoHttp.createStringRequest(CommonConfig.MAIN_PATH + url, method);
		request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
		WeakReference<Context> wContext = new WeakReference<>(context);
		getCallBackClass(networkCallback);
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
	
	//上传文件---放在文件体内上传
	public void toPostFile(Context context, RequestMethod method, String url, Map<String, String> paramsMap, ArrayList<String> path, NetworkCallback<T> networkCallback){
		this.networkCallback = networkCallback;
		request = NoHttp.createStringRequest(CommonConfig.MAIN_PATH + url,method);
		request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
		WeakReference<Context> wContext = new WeakReference<>(context);
		getCallBackClass(networkCallback);
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
		//将传过来的图片路径转存在文件集合里
		List<File> files= new ArrayList<File>();
		for (int i = 0; i < path.size(); i++) {
			File file = new File(path.get(i));
			files.add(file);
		}
		if (files != null) {
			for (int i = 0; i < files.size(); i++) {
				File file = files.get(i);
				// 压缩
				File fileUpload = CompressImageUtil.scal(file);
				request.add("file", new FileBinary(fileUpload));
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
			LogUtil.e("response", result);
			try {
				JSONObject json = new JSONObject(result);
				String code = json.optString("code");
				if (String.valueOf(HTTP_SUCCESS).equals(code)) {
					//得到body对象
					Object	body = json.get("body");
					String bodyStr = null;
					if (body instanceof JSONObject) {
						bodyStr = ((JSONObject) body).toString();
					} else if (body instanceof JSONArray) {
						bodyStr = ((JSONArray) body).toString();
					}
					T entity = null;
					if (clazz.getSimpleName().toLowerCase().equals("string") || clazz.getSimpleName().toLowerCase().equals("integer")) {
						entity = (T) body;
					} else {
						entity = GsonUtil.jsonToObj(bodyStr, clazz);
					}
					if (null != networkCallback) {
						networkCallback.onSuccessCallBack(entity);
					}
				} else {
					String message = null;
					message = (String) json.get("msg");
					if (null == message) {
						message = "数据返回异常";
					}
					if(networkCallback != null){
						networkCallback.onFailureCallBack(null, message);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 请求失败
		public void onFailed(int what, Response<String> response) {
			if (networkCallback != null) {
				networkCallback.onFailureCallBack(response.getException(), null);
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

	public interface NetworkCallback<T>{
		void onSuccessCallBack(T data);

		void onFailureCallBack(Exception e, String response);
	}
	
}