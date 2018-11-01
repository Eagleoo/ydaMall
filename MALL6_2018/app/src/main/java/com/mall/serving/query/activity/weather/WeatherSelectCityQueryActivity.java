package com.mall.serving.query.activity.weather;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.query.adapter.WeatherGridCityAdapter;
import com.mall.serving.query.model.WeatherCityInfo;
import com.mall.view.App;
import com.mall.view.R;

@ContentView(R.layout.query_weather_city_activity)
public class WeatherSelectCityQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;

	@ViewInject(R.id.gridview)
	private GridView gridview;

	private List list;
	private WeatherGridCityAdapter adapter;
	public static String TAG = "WeatherSelectCityQuery";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
		list = new ArrayList();

		adapter = new WeatherGridCityAdapter(context, list);
		gridview.setAdapter(adapter);
		refrenshList();
		registerReceiverAtBase(new String[] { TAG });
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	private void refrenshList() {
		try {
			List<WeatherCityInfo> findAll = DbUtils.create(App.getContext())
					.findAll(WeatherCityInfo.class);

			if (findAll != null && findAll.size() > 0) {
				list.clear();
				list.addAll(findAll);

				adapter.notifyDataSetChanged();
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setView() {

		top_center.setText("城市管理");
		top_left.setVisibility(View.VISIBLE);

	}

	@Override
	public void onReceiveBroadcast(Intent intent) {
		super.onReceiveBroadcast(intent);

		if (intent.getAction().equals(TAG)) {
			refrenshList();

		}

	}

	@OnClick(R.id.top_left)
	public void click(View v) {
		Intent intent = new Intent();
		Activity ac = (Activity) context;
		ac.setResult(100, intent);
		ac.finish();

	}

}
