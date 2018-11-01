package com.mall.serving.query.activity.car;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.view.textview.TextViewBack;
import com.mall.serving.query.adapter.CarCityAdapter;
import com.mall.serving.query.model.CarCityInfo;
import com.mall.serving.query.model.CarCityInfo.CityInfo;
import com.mall.serving.query.model.CarIdInfo;
import com.mall.util.Util;
import com.mall.view.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.community_find_listview2)
public class CarCityActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextViewBack top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	private List list;

	@ViewInject(R.id.listview)
	private ListView listview;

	private CarCityAdapter adapter;
	public static final String TAG = "com.mall.serving.query.activity.car.CarCityActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();

		getIntentData();

	}

	private void setView() {
		if (null != top_left)
			top_left.setVisibility(View.VISIBLE);

	}

	private void getIntentData() {

		list = new ArrayList();
		int type = 0;
		Intent intent = getIntent();
		if (intent.hasExtra("city")) {

			CarCityInfo info = (CarCityInfo) intent.getSerializableExtra("city");
			type = 1;

			List<CityInfo> citys = info.getCitys();
			list.clear();
			list.addAll(citys);
		} else if (intent.hasExtra("carType")) {
			type = 2;
		}
		adapter = new CarCityAdapter(context, list, type);
		listview.setAdapter(adapter);
		listview.setDividerHeight(1);
		listview.setBackgroundResource(R.color.main_light_bg);
		AnimeUtil.setAnimationEmptyView(context, listview, true);
		switch (type) {
		case 0:
			top_center.setText("选择省份");
			registerReceiverAtBase(new String[] { TAG });
			cityQuery();
			break;
		case 1:
			top_center.setText("选择城市");
			break;
		case 2:
			top_center.setText("选择车辆类型");
			carTypeQuery();
			break;

		}

	}

	private void cityQuery() {
		Util.asynTask(new com.mall.util.IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");

				String lists = map.get("list");

				List<CarCityInfo> mlist = JSON.parseArray(lists, CarCityInfo.class);
				Intent intent = CarCityActivity.this.getIntent();
				String province = intent.getStringExtra("province");
				if (!Util.isNull(province)) {
					for (CarCityInfo c : mlist) {
						if (c.getProvince().startsWith(province) || province.startsWith(c.getProvince())) {
							Util.showIntent(context, CarCityActivity.class, new String[] { "city" },
									new Serializable[] { c });
							finish();
							return;
						}
					}
				}
				// JsonUtil.getPersons(lists,
				// new TypeToken<List<CarCityInfo>>() {
				// });
				list.clear();
				if (mlist != null && mlist.size() != 0) {
					list.addAll(mlist);
					adapter.notifyDataSetChanged();
				} else {
					AnimeUtil.setNoDataEmptyView("列表为空...", R.drawable.community_dynamic_empty, context, listview, true,
							null);
				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://v.juhe.cn/wz/citys?key=e602c65dd84079924807d5b248d7e1b4&format=2");
				return web.getPlan();
			}
		});
	}

	private void carTypeQuery() {
		Util.asynTask(new com.mall.util.IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");

				String lists = map.get("list");

				List<CarIdInfo> mlist = JsonUtil.getPersons(lists, new TypeToken<List<CarIdInfo>>() {
				});

				list.clear();
				if (mlist != null & mlist.size() != 0) {
					list.addAll(mlist);
					adapter.notifyDataSetChanged();
				} else {
					AnimeUtil.setNoDataEmptyView("列表为空...", R.drawable.community_dynamic_empty, context, listview, true,
							null);
				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://v.juhe.cn/wz/hpzl?key=e602c65dd84079924807d5b248d7e1b4&format=2");
				return web.getPlan();
			}
		});
	}

	@Override
	public void onReceiveBroadcast(Intent intent) {
		// TODO Auto-generated method stub
		super.onReceiveBroadcast(intent);
		if (intent.getAction().equals(TAG)) {
			finish();
		}
	}

}
