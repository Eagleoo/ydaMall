package com.mall.net;

public interface NewWebAPIRequestCallback {
	
	
	public void success(Object result);

	public void fail(Throwable e);
	
	public void timeout();
	
	public void requestEnd();
	
}
