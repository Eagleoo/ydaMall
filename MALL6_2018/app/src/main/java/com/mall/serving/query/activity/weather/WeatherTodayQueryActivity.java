package com.mall.serving.query.activity.weather;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.query.adapter.WeatherGridAdapter;
import com.mall.serving.query.configs.QueryConfigs;
import com.mall.serving.query.model.WeatherExpInfo;
import com.mall.serving.query.model.WeatherInfo;
import com.mall.serving.query.model.WeatherInfo.Data.Weather;
import com.mall.view.R;

@ContentView(R.layout.query_weather_today_activity)
public class WeatherTodayQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.tv_item1)
	private TextView tv_item1;
	@ViewInject(R.id.tv_item2)
	private TextView tv_item2;
	@ViewInject(R.id.iv_item1)
	private ImageView iv_item1;
	@ViewInject(R.id.tv_exp1)
	private TextView tv_exp1;
	@ViewInject(R.id.tv_exp2)
	private TextView tv_exp2;
	@ViewInject(R.id.iv_exp)
	private ImageView iv_exp;

	@ViewInject(R.id.gridview1)
	private GridView gridview1;

	private final String TEMP = "℃";
	private WeatherInfo info;
	private WeatherExpInfo expInfo;


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
		if (intent.hasExtra("exp")) {
			expInfo = (WeatherExpInfo) intent.getSerializableExtra("exp");
		}
		

	}

	private void setView() {
		top_center.setText(info.getData().getRealtime().getCity_name());
		top_left.setVisibility(View.VISIBLE);
		List<Weather> weather = info.getData().getWeather();
		int weatherRid = QueryConfigs.getWeatherRid(weather.get(0).getInfo()
				.getDay().get(0));
		iv_item1.setImageResource(weatherRid);

		tv_item1.setText(weather.get(0).getInfo().getDay().get(2) + "/"
				+ weather.get(0).getInfo().getNight().get(2) + TEMP);
		tv_item2.setText(weather.get(0).getDate() + " | 周"
				+ weather.get(0).getWeek());

		iv_exp.setImageResource(expInfo.getRid());
		tv_exp1.setText(expInfo.getTitle() + "   " + expInfo.getList().get(0));
		tv_exp2.setText(expInfo.getList().get(1));

		List<Weather> list = new ArrayList<Weather>();
		list.add(weather.get(1));
		list.add(weather.get(2));
		list.add(weather.get(3));
		list.add(weather.get(4));
		WeatherGridAdapter adapter1 = new WeatherGridAdapter(context, list, 2,info);
		gridview1.setAdapter(adapter1);

	}

}
