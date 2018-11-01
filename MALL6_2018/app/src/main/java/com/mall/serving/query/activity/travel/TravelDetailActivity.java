package com.mall.serving.query.activity.travel;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.model.TravelInfo.Hotel;
import com.mall.serving.query.model.TravelInfo.Scenery;
import com.mall.view.R;

@ContentView(R.layout.query_travel_detail_activity)
public class TravelDetailActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.iv_1)
	private ImageView iv_1;
	@ViewInject(R.id.tv_1)
	private TextView tv_1;
	@ViewInject(R.id.tv_2)
	private TextView tv_2;
	@ViewInject(R.id.tv_3)
	private TextView tv_3;
	@ViewInject(R.id.tv_4)
	private TextView tv_4;
	@ViewInject(R.id.tv_5)
	private TextView tv_5;
	@ViewInject(R.id.tv_6)
	private TextView tv_6;
	@ViewInject(R.id.tv_7)
	private TextView tv_7;
	@ViewInject(R.id.tv_8)
	private TextView tv_8;
	@ViewInject(R.id.tv_9)
	private TextView tv_9;

	private Serializable sinfo;
	private String url;

	private boolean isHotel;

	private String mStr = "";
	private String mStr2 = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
		getIntentData();
		tv_8.setVisibility(View.GONE);
	}

	private void setView() {

		top_left.setVisibility(View.VISIBLE);

	}

	private void getIntentData() {

		String title = "";
		String grade = "";

		String price = "";
		String address = "";
		String imgurl = "";
		String intro = "";
		String manyidu = "";

		Intent intent = getIntent();
		if (intent.hasExtra("info")) {
			sinfo = intent.getSerializableExtra("info");

		}

		if (intent.hasExtra("isHotel")) {
			isHotel = intent.getBooleanExtra("isHotel", false);
		}
		if (isHotel) {
			mStr = "酒店";
			mStr2 = "房价：";
			//tv_8.setVisibility(View.GONE);

			Hotel info = (Hotel) sinfo;
			title = info.getTitle();
			grade = info.getGrade();
			price = info.getPrice_min();
			address = info.getAddress();
			imgurl = info.getImgurl();
			intro = info.getIntro();
			url = info.getUrl();
			manyidu = info.getManyidu();
		} else {
			mStr = "景点";
			mStr2 = "门票：";
			tv_6.setVisibility(View.GONE);

			Scenery info = (Scenery) sinfo;
			title = info.getTitle();
			grade = info.getGrade();
			price = info.getPrice_min();
			address = info.getAddress();
			imgurl = info.getImgurl();
			intro = info.getIntro();
			url = info.getUrl();
		}

		top_center.setText(mStr + "详情");

		tv_1.setText(title);
		tv_9.setText(mStr +"介绍：");
       top_center.setText(title);
		int int1 = Util.getInt(grade);
		String str = "";
		for (int i = 0; i < int1; i++) {
			str = "A" + str;
		}
		Spanned html1 = Html.fromHtml(mStr + "网址：<font color='#2498e2'>" + url
				+ "</font>");
		Spanned html2 = Html.fromHtml(mStr2+"<font color='#fe9920'>" + price
				+ "元</font>");
		if (isHotel) {
			tv_2.setText(grade + "星级" + mStr);
		} else {
			tv_2.setText(str + "级" + mStr);
		}

		tv_3.setText("地址：" + address);
		tv_4.setText(html1);
		tv_5.setText(html2);
		tv_6.setText("满意度：" + manyidu);
		tv_7.setText( intro);

		AnimeUtil.getImageLoad().displayImage(imgurl, iv_1,
				AnimeUtil.getImageOption());

	}

	@OnClick({ R.id.tv_4, R.id.tv_8 })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.tv_4:
			Util.openWeb(context, url);
			break;
		case R.id.tv_8:

			Util.showIntent(context, TravelMainActivity.class,
					new String[] { "info" }, new Serializable[] { sinfo });
			break;

		}

	}

}
