package com.mall.serving.community.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mall.view.App;

public class SharedPreferencesUtils {

	public static String isRemind = "isRemind";
	public static String isSound = "isSound";
	public static String isVibrate = "isVibrate";
	public static String isDisturbing = "isDisturbing";
	public static String isStranger = "isStranger";
	public static String isDynamic = "isDynamic";
	public static String isMessage = "isMessage";
	public static String start_time = "start_time";
	public static String end_time = "end_time";
	public static String isList = "isList";
	public static String isGender = "isGender";
	public static String isCity = "isCity";
	public static String isRole= "isRole";
	public static String isListRandom = "isListRandom";
	public static String isGenderRandom = "isGenderRandom";
	public static String isCityRandom = "isCityRandom";
	public static String isRoleRandom= "isRoleRandom";
	
	public static String isCallSet= "isCallSet";
	public static String isCallVibrate= "isCallVibrate";
	public static String isCallSound= "isCallSound";
	public static String isSupplement= "isSupplement";
	
	
	

	public static int getSharedPreferencesInt(String name) {

		SharedPreferences sp = App.getContext().getSharedPreferences("yda",
				Context.MODE_PRIVATE);

		return sp.getInt(name, 0);
	}

	public static void setSharedPreferencesInt(String name, int num) {

		SharedPreferences sp = App.getContext().getSharedPreferences("yda",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(name, num);
		editor.commit();

	}

	public static String getSharedPreferencesString(String name) {

		SharedPreferences sp = App.getContext().getSharedPreferences("yda",
				Context.MODE_PRIVATE);

		return sp.getString(name, "");
	}

	public static void setSharedPreferencesString(String name, String str) {

		SharedPreferences sp = App.getContext().getSharedPreferences("yda",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(name, str);

		editor.commit();

	}

}
