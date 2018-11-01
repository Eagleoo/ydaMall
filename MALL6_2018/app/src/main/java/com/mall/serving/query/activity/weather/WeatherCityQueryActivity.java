package com.mall.serving.query.activity.weather;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.adapter.WeatherCityAdapter;
import com.mall.serving.query.model.CityString;
import com.mall.serving.query.model.PostcodeCityInfo;
import com.mall.serving.query.model.PostcodeCityInfo.City;
import com.mall.serving.query.model.PostcodeCityInfo.City.District;
import com.mall.serving.voip.view.alphabeticbar.QuickAlphabeticBar;
import com.mall.view.App;
import com.mall.view.R;

@ContentView(R.layout.query_trainticket_city_activity)
public class WeatherCityQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.et_city)
	private TextView et_city;
	@ViewInject(R.id.rb_rg_1)
	private RadioButton rb_rg_1;
	@ViewInject(R.id.rb_rg_2)
	private RadioButton rb_rg_2;
	@ViewInject(R.id.tv_search)
	private TextView tv_search;
	@ViewInject(R.id.lv_voip_contact)
	private ListView listview;
	@ViewInject(R.id.indexScrollerBar)
	private QuickAlphabeticBar mQuickAlphabeticBar;

	private WeatherCityAdapter adapter;

	private List list;
	private List listHot;

	private List listAll;
	private String[] hotCity = new String[] { "北京市", "上海市", "广州市", "深圳市", "武汉市", "天津市", "重庆市", "成都市", "厦门市", "昆明市",
			"杭州市", "西安市", "三亚市" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
		registerReceiverAtBase(new String[] { WeatherCity2DisQueryActivity.TAG });
		list = new ArrayList();
		listHot = new ArrayList();
		listAll = new ArrayList();
		mQuickAlphabeticBar.init(context);
		mQuickAlphabeticBar.setListView(listview);
		mQuickAlphabeticBar.setHight(mQuickAlphabeticBar.getHeight());
		mQuickAlphabeticBar.setVisibility(View.GONE);
		setListener();

		for (int i = 0; i < hotCity.length; i++) {
			listHot.add(hotCity[i]);

		}
		isSaveCity();
		list.addAll(listHot);
		adapter = new WeatherCityAdapter(context, list, 0);
		listview.setAdapter(adapter);

	}

	private void setView() {

		top_center.setText("选择城市");
		top_left.setVisibility(View.VISIBLE);
		rb_rg_1.setText("热门城市");
		rb_rg_2.setText("省份列表");
		et_city.setHint("城市/地区");
	}

	@OnClick({ R.id.tv_search })
	public void click(View v) {

		String city = et_city.getText().toString().trim().toLowerCase();
		if (TextUtils.isEmpty(city)) {
			Util.show("请输入城市或地区");
		} else {
			rb_rg_1.setChecked(true);
			List<String> searchList = new ArrayList<String>();
			for (int i = 0; i < listAll.size(); i++) {
				PostcodeCityInfo info = (PostcodeCityInfo) listAll.get(i);
				List<City> cityList = info.getCity();
				for (int j = 0; j < cityList.size(); j++) {
					City city2 = cityList.get(j);
					List<District> district = city2.getDistrict();
					String cityName = city2.getCity();
					if (!searchList.contains(cityName) && cityName.contains(city)) {
						searchList.add(cityName);
					}
					for (int k = 0; k < district.size(); k++) {

						District district2 = district.get(k);
						String districtName = district2.getDistrict();
						if (!searchList.contains(districtName) && cityName.contains(districtName)) {
							searchList.add(districtName);
						}
					}

				}
			}

			list.clear();
			list.addAll(searchList);
			adapter = new WeatherCityAdapter(context, list, 0);
			listview.setAdapter(adapter);

		}

	}

	private void setListener() {
		ViewTreeObserver vto = mQuickAlphabeticBar.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				mQuickAlphabeticBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mQuickAlphabeticBar.setHight(mQuickAlphabeticBar.getHeight());
			}
		});

	}

	@OnRadioGroupCheckedChange(R.id.rg)
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (checkedId) {
		case R.id.rb_rg_1:
			list.clear();
			list.addAll(listHot);
			adapter = new WeatherCityAdapter(context, list, 0);
			listview.setAdapter(adapter);

			break;
		case R.id.rb_rg_2:
			list.clear();
			list.addAll(listAll);
			adapter = new WeatherCityAdapter(context, list, 1);
			listview.setAdapter(adapter);
			break;

		}

	}

	private void isSaveCity() {

		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");

				String lists = map.get("list");
				if (TextUtils.isEmpty(lists)) {
					return;
				}
				listAll = JsonUtil.getPersons(lists, new TypeToken<List<PostcodeCityInfo>>() {
				});
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://v.juhe.cn/postcode/pcd?key=3287cd51e91a12cba3da80bd32704041");
				return web.getPlan();
			}

		});

	}

	@Override
	public void onReceiveBroadcast(Intent intent) {
		// TODO Auto-generated method stub
		super.onReceiveBroadcast(intent);

		if (intent != null && intent.getAction().equals(WeatherCity2DisQueryActivity.TAG)) {
			finish();
		}

	}
}
