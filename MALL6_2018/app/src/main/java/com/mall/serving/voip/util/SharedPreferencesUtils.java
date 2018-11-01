package com.mall.serving.voip.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mall.view.App;

public class SharedPreferencesUtils {

	public static String isCallbackWiFI = "isCallbackWiFI";
	public static String isCallback3G = "isCallback3G=";
	public static String isCallerID = "isCallerID";
	public static String isCallBackAuto = "isCallBackAuto";
	public static String isMessageFirst = "isMessageFirst";
	public static String isGuide = "isGuide";
	public static String isHint = "isHint";
	public static String isSound = "isSound";
	public static String isVibrate = "isVibrate";
	public static String sign = "sign";
	public static String isSignIn = "isSignIn";
	public static String isCallbackHint = "isCallbackHint";
	public static String JKEY_TOKEN = "kc_token";
	public static String versiont= "version";
	/**
	 * 是否已经设置兼容模式
	 */
	public final static String JKey_DIALTESTMODELINCALL = "jkey_dialtestmodelincall";
	
	/**
	 * 云之讯拨打电话服务器地址
	 */
	public static String JKEY_UCPASS_URL = "jkey_ucpass_url";
	
	/**
	 * 云之讯拨打电话服务器端口
	 */
	public static String JKEY_UCPASS_PORT = "jkey_ucpass_port";

	public static int getSharedPreferencesInt(String name) {

		SharedPreferences sp = App.getContext().getSharedPreferences("ydaVoip",
				App.getContext().MODE_PRIVATE);

		return sp.getInt(name, 0);
	}

	public static void setSharedPreferencesInt(String name, int num) {

		SharedPreferences sp = App.getContext().getSharedPreferences("ydaVoip",
				App.getContext().MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(name, num);
		editor.commit();

	}

	public static String getSharedPreferencesString(String name) {

		SharedPreferences sp = App.getContext().getSharedPreferences("ydaVoip",
				App.getContext().MODE_PRIVATE);

		return sp.getString(name, "");
	}

	public static void setSharedPreferencesString(String name, String str) {

		SharedPreferences sp = App.getContext().getSharedPreferences("ydaVoip",
				App.getContext().MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(name, str);
		editor.commit();

	}

}
