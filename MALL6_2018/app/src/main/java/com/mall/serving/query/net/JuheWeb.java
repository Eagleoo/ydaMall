package com.mall.serving.query.net;

import com.lidroid.xutils.util.LogUtils;
import com.mall.serving.community.util.Util;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

public class JuheWeb {

	public static void getJuheData(Parameters params, int apiid, String api,
			String method, final JuheRequestCallBack callback) {

		params.add("dtype", "json");

		if (!Util.isNetworkConnected()) {

			callback.requestEnd();
			callback.fail(0, "", "");
			return;
		}
		StringBuffer p = new StringBuffer();
		for (int i = 0 ; i < params.size();i++) {
			p.append("&"+params.getKey(i)+"="+params.getValue(i));
		}
		LogUtils.e("http  "+method+"     "+api+"?"+p.toString());
		JuheData.executeWithAPI(apiid, api, method, params, new DataCallBack() {

			/**
			 * @param err
			 *            错误码,0为成功
			 * @param reason
			 *            原因
			 * @param result
			 *            数据
			 */
			@Override
			public void resultLoaded(int err, String reason, String result) {
				if (err == 0) {
					LogUtils.e("成功："+result);
					callback.success(err, reason, result);
				} else {
					LogUtils.e("else____________"+reason);
					callback.fail(err, reason, result);

				}
				callback.requestEnd();

			}
		});

	}

	public interface JuheRequestCallBack {

		void success(int err, String reason, String result);

		void requestEnd();

		void fail(int err, String reason, String result);

	}
}
