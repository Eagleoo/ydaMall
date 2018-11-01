package com.mall.card.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mall.card.CardAddNewCard;
import com.mall.card.bean.CardExchangeRequest;
import com.mall.card.bean.CardLinkman;
import com.mall.view.R;

public class CardUtil {
	public static String isMe = "0";
	public static String lat = "";
	public static String lng = "";
	public static int paiseCount=0;
	/**
	 * json to map
	 */
	public static Map<String, String> JsonToMap(String data) {
		GsonBuilder gb = new GsonBuilder();
		Gson g = gb.create();
		Map<String, String> map = g.fromJson(data,
				new TypeToken<Map<String, String>>() {
				}.getType());
		return map;
	}

	/**
	 * 将json字符串解析，并放置到map中
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static Map<String, String> getJosn(String jsonStr) throws Exception {
		Map<String, String> map = null;
		if (!TextUtils.isEmpty(jsonStr)) {
			map = new HashMap<String, String>();
			JSONObject json = new JSONObject(jsonStr);
			Iterator i = json.keys();
			while (i.hasNext()) {
				String key = (String) i.next();
				String value = json.getString(key);
				map.put(key, value);
			}
		}
		return map;
	}

	public static CardLinkman cardLinkman = new CardLinkman();
	/**
	 * 我的名片
	 */
	public static CardLinkman myCardLinkman;
	/**
	 * 当前的名片数量
	 */
	public static int cardCount = 0;
	/**
	 * 已经交换名片的会员
	 */
	public static List<CardLinkman> exchangeUser = new ArrayList<CardLinkman>();
	/**收到的交换请求*/
	public static List<CardExchangeRequest> shoudaoRe=new ArrayList<CardExchangeRequest>();
	/**发起的交换请求*/
	public static List<CardExchangeRequest> faqiRe=new ArrayList<CardExchangeRequest>();
	/**
	 * 验证只包含中文英文
	 */
	public static boolean checkZNAndEN(String string) {
		String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(string);
		return match.matches();
	}

	/**
	 * 验证网址
	 */
	public static boolean checkWeb(String string) {
		String regex = "http://(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(string);
		return match.matches();
	}
	/**
	 * 验证手机号码
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(14[5,7])|(17[0,6,7,8])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	/**
	 * 验证座机号码
	 */
	public static boolean checkZJ(String  string){
		Pattern p = Pattern
				.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");
		Matcher m = p.matcher(string);
		if (!m.matches()) {
			return  checkZJ1(string);
		}
		return m.matches();
		 
	}
	/**
	 * 验证座机号码
	 */
	public static boolean checkZJ1(String string){
		Pattern p = Pattern
				.compile("^[0][1-9]{2,3}[0-9]{5,10}$");
		Matcher m = p.matcher(string);
		return m.matches();
	}
	
	/**
	 * 1.以18/17/15/13/14开头的号码只能是11位，且以1开头后面不是数字3,4,5,7,8，那么就是错误号码
	 * 2.仅以一个0开头座机号规则10<=X<=12 3.以两个0开头的号码就是国际电话
	 * 4.以+86/12593/17951/17909/17911的结合规则1和2，其中+86后面的座机号码和手机号码均可以带0也可以不带0
	 * 
	 * @param num
	 * @return 格式后的电话 null标示号码不匹配
	 */
	public static String formatFreeCallNum(String num) {
		if (TextUtils.isEmpty(num)) {
			return null;
		}

		num = num.replace(" ", "");
		String oldString = num.replace("-", "");
		oldString = oldString.replace("+", "");

		if (oldString.length() < 3) {
			return null;
		}

		if (oldString.matches("^86.*"))
			oldString = oldString.substring("86".length());
		if (oldString.matches("^12593.*|17951.*|17909.*|17911.*")) {
			oldString = oldString.substring("12593".length());
		}

		if (oldString.matches("^(0){1}[1-9]*$")) {
			if (oldString.matches("[0-9]{8,12}")) {
				return oldString;
			} else {
				return null;
			}
		}
		if (oldString.startsWith("1")) {
			if (oldString.matches("^13.*|14.*|15.*|18.*|17.*")) {
				if (oldString.matches("[0-9]{11}")) {
					return oldString;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return oldString;

	}
	/**
	 * 验证邮箱
	 * @param string
	 * @return
	 */
	public static boolean checkEmail(String string){
		Pattern p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher m = p.matcher(string);
		return m.matches();
	}
	public static void xuznzeAdd(final Context context, int width,final String state) {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_no_card_tishi_dialog, null);
		final Dialog dialog = new Dialog(context, R.style.CustomDialogStyle);
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView shoujilianxi = (TextView) layout
				.findViewById(R.id.quanqu);
		TextView counts=(TextView)layout.findViewById(R.id.counts);
		TextView shuru = (TextView) layout.findViewById(R.id.yihou);
		ImageView delete=(ImageView) layout.findViewById(R.id.delete);
		if(state.equals("0")){
			counts.setText("您还没有添加客户的名片\n是否前去扫描或添加？");
		}
		
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CardUtil.isMe = "0";
				dialog.dismiss();
			}
		});
		shoujilianxi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		shuru.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(context, CardAddNewCard.class);
				context.startActivity(intent);
				CardUtil.isMe = state;
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}
	/**
	 * 过滤特殊字符以及空格
	 */
	public static String deleteZiFu(String string){
		Pattern matchsip = Pattern.compile("[^\\dA-Za-z\\u3007\\u3400-\\u4DB5\\u4E00-\\u9FCB\\uE815-\\uE864]");
		Matcher mp = matchsip.matcher(string);
		return mp.replaceAll("").toString().replaceAll(" ", "");
	}

}
