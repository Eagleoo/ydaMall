package com.mall.view.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lidroid.xutils.util.LogUtils;
import com.mall.util.Data;
import com.mall.util.Util;


public class StartServer extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			String make = Util.getMake();
			String model = Util.getModel();
			// 2014-06-10 远大也要做流氓了。法克鱿唉。。。
			if (!"OPPO".equalsIgnoreCase(make) && !"X909".equalsIgnoreCase(model)) {
				boolean isNet = Util.isNetworkConnected(context);
				Intent service = new Intent(context, MessageService.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// 3.1以后的版本直接设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES的value：32
				if (android.os.Build.VERSION.SDK_INT >= 12) {
				    intent.setFlags(32);
				}
				context.startService(service);
				LogUtils.v("消息推送开机启动");
				if (isNet) {
					Data.getShen();
				}
			}
		}
	}
}
