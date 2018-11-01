package com.alipay.android.appDemo4;

/**
 * 
 * 功能： 手机支付宝支付回调接口<br>
 * 时间： 2013-12-19<br>
 * 备注： <br>
 * @author Lin.~
 *
 */
public interface AliPayCallBack {
	
	public void doSuccess(String aliResultCode);
	
	public void doFailure(String aliResultCode);
	
}
