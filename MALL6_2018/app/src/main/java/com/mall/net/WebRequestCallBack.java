package com.mall.net;

import com.lidroid.xutils.util.LogUtils;
import com.mall.util.Util;
import com.mall.view.App;

public class WebRequestCallBack implements NewWebAPIRequestCallback {

	@Override
	public void success(Object result) {
		
	}

	@Override
	public void fail(Throwable e) {
		LogUtils.e("网络请求错误：",e);
	}

	@Override
	public void timeout() {
		LogUtils.e("网络请求超时！");
		Util.show("小二很忙，系统很累，请稍候...", App.getContext());
	}
	
	public void requestEnd(){
		
	}
}
