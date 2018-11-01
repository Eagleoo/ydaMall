package com.mall.card;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.card.bean.CardPush;
import com.mall.model.User;
import com.mall.net.WebRequestCallBack;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.PushMessageAndroidFrame;
import com.mall.view.R;

/**
 * 消息
 * 
 * @author admin
 * 
 */
public class CardAdvices extends Activity {
	@ViewInject(R.id.card_qingqiu)
	private TextView card_qingqiu;
	private List<CardPush> cardPushs = new ArrayList<CardPush>();
	@ViewInject(R.id.xiaoxi_count)
	private TextView xiaoxi_count;
	@ViewInject(R.id.xiaoxi_re)
	private RelativeLayout xiaoxi_re;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_advices);
		ViewUtils.inject(this);
		SharedPreferences sp=getSharedPreferences("lastDianji", Context.MODE_PRIVATE);
		String time=sp.getString("time", null);
		if (TextUtils.isEmpty(time)) {
	    	Editor editor=sp.edit();
	    	Date date=new Date();
	    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    	editor.putString("time", sdf.format(date));
	    	editor.commit();
		}
		xiaoxi_re.setVisibility(View.GONE);
		page();
	}
	@OnClick({ R.id.card_qingqiu, R.id.xiaoxi, R.id.kefu,R.id.top_back })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;
		case R.id.card_qingqiu:
			Util.showIntent(this, CardExchangeCardRequest.class);
			break;
		case R.id.xiaoxi:
			Util.showIntent(this, PushMessageAndroidFrame.class);
			SharedPreferences sp=getSharedPreferences("lastDianji", Context.MODE_PRIVATE);
	    	Editor editor=sp.edit();
	    	Date date=new Date();
	    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    	editor.putString("time", sdf.format(date));
	    	editor.commit();
			break;
		case R.id.kefu:
			Util.doPhone(Util._400, this);
			
			break;
		}
	}

	private void page() {
		User user = UserData.getUser();
		if (null == user)
			user = new User();
		App.getNewWebAPI().getAllPush(1, 99999999, user.getUserId(),
				user.getMd5Pwd(), new WebRequestCallBack() {
					@Override
					public void success(Object result) {
						Map<String, String> map = new HashMap<String, String>();

						try {
							System.out.println(result.toString()
									.replace(" ", "").replace(",}", "}"));
							map = CardUtil.getJosn(result.toString()
									.replace(" ", "").replace(",}", "}"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Gson gson = new Gson();
								cardPushs = gson.fromJson(map.get("list"),
										new TypeToken<List<CardPush>>() {
										}.getType());
								SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
								SharedPreferences sp=getSharedPreferences("lastDianji", Context.MODE_PRIVATE);
								String time=sp.getString("time", null);
								List<CardPush> list=new ArrayList<CardPush>();
								for (int i = 0; i < cardPushs.size(); i++) {
									try {
										if (!TextUtils.isEmpty(cardPushs.get(i).getSenderTime())) {
											if (sdf2.parse(cardPushs.get(i).getSenderTime()).getTime()-(sdf.parse(time).getTime())>=0) {
												list.add(cardPushs.get(i));
											}
										}else {
											list.add(cardPushs.get(i));
										}
										
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								if (list.size()>0) {
									xiaoxi_count.setText(list.size()+"");
									xiaoxi_re.setVisibility(View.VISIBLE);
								}
								//username.setText(sp.getString("username", null));
							}
						}
					}

					@Override
					public void requestEnd() {

					}
				});
	}
}
