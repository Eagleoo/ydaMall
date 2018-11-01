package com.mall.serving.community.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {

	public static String createJsonString(Object value) {
		Gson gson = new Gson();
		String str = gson.toJson(value);
		return str;
	}

	public static <T> T getPerson(String jsonString, Class<T> cls) {
		T t = null;
		try {
			if (isGoodJson(jsonString)) {
				Gson gson = new Gson();
				t = gson.fromJson(jsonString, cls);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public static <T> List<T> getPersons(String jsonString, TypeToken tt) {
		List<T> list = new ArrayList<T>();
		try {
			if (isGoodJson(jsonString)) {
				Gson gson = new Gson();

				list = gson.fromJson(jsonString, tt.getType());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static Map<String, String> getNewApiJson(String json) {

		Map<String, String> map = new HashMap<String, String>();

		try {
			String code = "";
			String message = "";
			String list = "";
			if (isGoodJson(json)) {
				JSONObject jObject = new JSONObject(json);
				if (jObject.has("code")) {
					code = jObject.optString("code");
				}
				if (jObject.has("message")) {
					message = jObject.optString("message");
				}

				if (jObject.has("list")) {
					list = jObject.optString("list");
				}
			}

			map.put("code", code);
			map.put("message", message);
			map.put("list", list);

		} catch (Exception e) {
			e.printStackTrace();

		}

		return map;

	}

	public static Map<String, String> getNewApiJsonQuery(String json) {
		Map<String, String> map = new HashMap<String, String>();

		try {
			String code = "";
			String message = "";
			String list = "";
			int error_code = -1;

			if (isGoodJson(json)) {

				JSONObject jObject = new JSONObject(json);

				if (jObject.has("resultcode")) {
					code = jObject.optString("resultcode");
				}
				if (jObject.has("reason")) {
					message = jObject.optString("reason");
				}

				if (jObject.has("result")) {
					list = jObject.optString("result");

				}
				if (jObject.has("error_code")) {
					error_code = jObject.optInt("error_code");
					if (error_code > 0) {
						if (Util.isNull(message))
							Util.show("查询失败，请稍后重试");
						else if (message.equals("Empty")) {
							Util.show("查询失败，请检查关键字");
						} else if (error_code == 10014) {
							// Util.show("车票系统异常，请重试！");
						} else {
							Util.show(message);
						}
					}
				}

			}
			map.put("code", code);
			map.put("message", message);
			map.put("list", list);
			map.put("error_code", error_code + "");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}

	public static boolean isBadJson(String json) {
		return !isGoodJson(json);
	}

	public static boolean isGoodJson(String json) {
		if (TextUtils.isEmpty(json)) {
			return false;
		}
		try {
			new JsonParser().parse(json);
			return true;
		} catch (JsonParseException e) {

			return false;
		}
	}
}
