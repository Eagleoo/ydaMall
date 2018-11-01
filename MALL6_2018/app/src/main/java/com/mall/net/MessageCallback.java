package com.mall.net;

import com.alibaba.fastjson.JSONObject;

public interface MessageCallback {
	
	public void onAcceptMessage(JSONObject json);
	
}
