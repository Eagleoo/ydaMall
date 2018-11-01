package com.mall.serving.query.net;

import java.util.HashMap;
import java.util.Map;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.WebRequestCallBack;

public final class NewWebAPI {

	private static NewWebAPI api = new NewWebAPI();
	private static String webApi = "http://api.kuaidi100.com/api";
	

	private NewWebAPI() {
	}

	public static NewWebAPI getNewInstance() {
		return api;
	}

	// 没参数的请求
	public void getWebRequest(String url, NewWebAPIRequestCallback callback) {
		getWebRequest(url, null, callback);
	}

	// 有参数的请求
	public void getWebRequest(final String url, Map<String, String> map,
			NewWebAPIRequestCallback callback) {
		if (null == callback)
			callback = new WebRequestCallBack();
		final NewWebAPIRequestCallback callBack = callback;
		HttpUtils http = new HttpUtils(60000);
		RequestParams params = new RequestParams("UTF-8");

		

		StringBuilder p = new StringBuilder();
	
		if (null != map) {
			
			for (String key : map.keySet()) {
				params.addBodyParameter(key, map.get(key));
				p.append("&" + key + "=" + map.get(key));
			}
		}
		LogUtils.e("请求数据：" + url +"?"+ p.toString());
		http.send(HttpMethod.POST, url, params,
				new RequestCallBack<Object>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						arg0.printStackTrace();
						callBack.fail(arg0);
						callBack.requestEnd();
					}

					@Override
					public void onSuccess(ResponseInfo<Object> arg0) {
//					
						if (null == (arg0.result)) {
							LogUtils.e("接口繁忙            " + webApi + url);
						} else {
					
							callBack.success(arg0.result);
						}
						callBack.requestEnd();
					}

					@Override
					public void onCancelled() {
						super.onCancelled();
						callBack.fail(new Throwable("请求被取消"));
						callBack.requestEnd();
					}
				});
	}




	/**
	 * 快递查询
	 * 
	 * @param callback
	 */
	public void expressageSearch(String com, String no,
			WebRequestCallBack callback) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("com", com);
		param.put("nu", no);
		param.put("id", "20bbbb988f6aa6d9");
		param.put("valicode", "");
		param.put("show", "0");
		param.put("muti", "");
		param.put("order", "");

		getWebRequest(webApi, param, callback);
	}
//
//	/**
//	 * 得到快递公司
//	 * 
//	 * @param com
//	 * @param no
//	 * @param callback
//	 */
//	public void expressageCompany(WebRequestCallBack callback) {
//		Map<String, String> param = new HashMap<String, String>();
//		param.put("key", QueryConfigs.exp_key);
//		getWebRequest(webApi2,expressageCompany, param, callback);
//	}

}
