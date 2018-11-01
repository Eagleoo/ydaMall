package com.mall.serving.query.activity.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.view.picviewpager.ViewPagerAdapter;
import com.mall.serving.query.configs.QueryConfigs;
import com.mall.serving.query.model.WeatherInfo;
import com.mall.serving.query.model.WeatherInfo.Data.Weather;
import com.mall.view.R;

@ContentView(R.layout.query_weather_detail_activity)
public class WeatherDetailQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.rg)
	private RadioGroup rg;
	@ViewInject(R.id.pager)
	private ViewPager pager;

	private WeatherInfo info;
	private String date;
	private final String TEMP = "℃";

	@ViewInject(R.id.iv_day)
	private ImageView iv_day;
	@ViewInject(R.id.tv_day1)
	private TextView tv_day1;
	@ViewInject(R.id.tv_day2)
	private TextView tv_day2;
	@ViewInject(R.id.tv_day3)
	private TextView tv_day3;
	@ViewInject(R.id.iv_night)
	private ImageView iv_night;
	@ViewInject(R.id.tv_night1)
	private TextView tv_night1;
	@ViewInject(R.id.tv_night2)
	private TextView tv_night2;
	@ViewInject(R.id.tv_night3)
	private TextView tv_night3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		getIntentData();
		setView();
	}

	private void getIntentData() {
		info = new WeatherInfo();
		Intent intent = getIntent();
		if (intent.hasExtra("info")) {
			info = (WeatherInfo) intent.getSerializableExtra("info");
		}

		if (intent.hasExtra("date")) {
			date = intent.getStringExtra("date");
		}

	}

	private void setView() {
		top_center.setText(info.getData().getRealtime().getCity_name());
		top_left.setVisibility(View.VISIBLE);

		initView();
	}

	private void initView() {

		final List<Weather> weather = info.getData().getWeather();

		List<View> vList = new ArrayList<View>();

		for (int i = 0; i < weather.size(); i++) {
			Weather weather2 = weather.get(i);
			RadioButton rb = (RadioButton) rg.getChildAt(i);

			if (i == 0) {
				rb.setText("今天");
				rb.setChecked(true);
			} else {
				rb.setText("周" + weather2.getWeek());
				rb.setChecked(false);
			}

			final int m = i;
			rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(
						CompoundButton paramCompoundButton, boolean flag) {
					if (flag) {
						pager.setCurrentItem(m);
					}

				}
			});

			LinearLayout ll = new LinearLayout(context);
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.setGravity(Gravity.CENTER);
			ll.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			TextView tv1 = new TextView(context);
			TextView tv2 = new TextView(context);
			TextView tv3 = new TextView(context);
			tv1.setGravity(Gravity.CENTER);
			tv2.setGravity(Gravity.CENTER);
			tv3.setGravity(Gravity.CENTER);
			tv1.setText(weather2.getInfo().getDay().get(2) + "/"
					+ weather2.getInfo().getNight().get(2) + TEMP);
			tv1.setTextColor(getResources().getColor(R.color.white));
			tv1.setTextSize(50);
			tv2.setText(weather2.getInfo().getDay().get(1));
			tv2.setTextColor(getResources().getColor(R.color.white));
			tv2.setTextSize(30);
			tv3.setText(weather2.getDate());
			tv3.setTextColor(getResources().getColor(R.color.white));
			tv3.setTextSize(20);
			ll.addView(tv1);
			
			ll.addView(tv3);
			ll.addView(tv2);
			vList.add(ll);
		}

		ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(vList);
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int p) {
				RadioButton childAt = (RadioButton) rg.getChildAt(p);
				childAt.setChecked(true);
				Weather weather2 = weather.get(p);
				initDetail(weather2);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		for (int i = 0; i < weather.size(); i++) {
			Weather w = weather.get(i);
			if (w.getDate().equals(date)) {
				pager.setCurrentItem(i);
				break;
			}
		}

	}

	private void initDetail(Weather we) {
		int weatherRid = QueryConfigs.getWeatherRid(we.getInfo().getDay()
				.get(0));
		iv_day.setImageResource(weatherRid);

		tv_day1.setText(we.getInfo().getDay().get(1));
		tv_day2.setText(we.getInfo().getDay().get(3)
				+ we.getInfo().getDay().get(4));
		tv_day3.setText("日出" + we.getInfo().getDay().get(5));
		int nightRid = QueryConfigs.getWeatherRid(we.getInfo().getNight()
				.get(0));
		iv_night.setImageResource(nightRid);

		tv_night1.setText(we.getInfo().getNight().get(1));
		tv_night2.setText(we.getInfo().getNight().get(3)
				+ we.getInfo().getNight().get(4));
		tv_night3.setText("日落" + we.getInfo().getNight().get(5));
	}

}
