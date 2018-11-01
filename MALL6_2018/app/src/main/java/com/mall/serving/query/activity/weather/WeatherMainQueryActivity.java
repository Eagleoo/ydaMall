package com.mall.serving.query.activity.weather;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.LocationModel;
import com.mall.net.Web;
import com.mall.serving.community.adapter.SupportFragmentAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.fragment.WeatherQueryFragment;
import com.mall.serving.query.model.CityString;
import com.mall.serving.query.model.WeatherCityInfo;
import com.mall.util.MyLog;
import com.mall.view.App;
import com.mall.view.PositionService;
import com.mall.view.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.query_weather_activity)
public class WeatherMainQueryActivity extends FragmentActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;

	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	@ViewInject(R.id.pager)
	public ViewPager pager;
	@ViewInject(R.id.rg_point)
	private RadioGroup rg_point;
	private FragmentManager supportManager;

	private SupportFragmentAdapter adapter;
	private List<WeatherCityInfo> findAll;

	private WeatherMainQueryActivity context;
	private PositionService locationService;
	private String cityName = "北京";
	private ServiceConnection locationServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
			locationService = locationBinder.getService();
			locationService.startLocation();
			locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
				@Override
				public void onProgress(AMapLocation progress) {
					if (!Util.isNull(progress.getLocationType())){
						cityName = progress.getCity().replaceFirst("市", "");
						Log.e("定位城市","cityName"+cityName);
						setViewPager();
						isSave();
					}
				}

				@Override
				public void onError(AMapLocation error) {

				}
			});



		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		context = this;

		Intent intent = new Intent();
		intent.setAction(PositionService.TAG);
		intent.setPackage(getPackageName());
		LocationModel locationModel=LocationModel.getLocationModel();
		if(!Util.isNull(locationModel.getLatitude())){
			cityName = locationModel.getCity();
			Log.e("当前chens1","cityName"+cityName);
			setViewPager();
			isSave();


		}else {
			getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
		}


	}

	private void setViewPager() {
		Log.e("setViewPager","11");
		final List<WeatherQueryFragment> list = buidData("setViewPager");
		top_center.setText(list.get(0).getCity());
		supportManager = getSupportFragmentManager();

		adapter = new SupportFragmentAdapter(supportManager, list);
		pager.setOffscreenPageLimit(list.size());
		pager.setAdapter(adapter);
		if (pager.getTag() == null) {
			pager.setCurrentItem(0);
			pager.setTag("");
		}
		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				RadioButton rb = (RadioButton) rg_point.getChildAt(position);
				rb.setChecked(true);
				WeatherQueryFragment cityInfo = (WeatherQueryFragment) list.get(position);
				top_center.setText(cityInfo.getCity());
			}

			@Override
			public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int paramInt) {
				// TODO Auto-generated method stub

			}
		});

	}

	private List<WeatherQueryFragment> buidData(String k) {
		Log.e("setViewPager","12"+k);
		List<WeatherQueryFragment> list = new ArrayList<WeatherQueryFragment>();
		boolean isWeatherMainQuery= getSharedPreferences("WeatherMainQueryActivity", MODE_PRIVATE).getBoolean("WeatherMainQueryActivity", false);


		try {
			if (isWeatherMainQuery){

				try {
					findAll = DbUtils.create(App.getContext()).findAll(WeatherCityInfo.class);
					if (findAll != null && findAll.size() > 0) {

						for (int i = 0; i < findAll.size(); i++) {
							WeatherCityInfo cityInfo = findAll.get(i);
							WeatherQueryFragment fragment = new WeatherQueryFragment();
							fragment.setCity(cityInfo.getCity());
							list.add(fragment);
						}

					}
				}catch (DbException e){

				}

			}else {
				DbUtils.create(App.getContext()).deleteAll(WeatherCityInfo.class);
				WeatherQueryFragment fragment = new WeatherQueryFragment();
				fragment.setCity(cityName);
				list.add(fragment);
				getSharedPreferences("WeatherMainQueryActivity", MODE_PRIVATE).edit().putBoolean("WeatherMainQueryActivity", true).commit();
			}

			if (list.size() == 1) {
				WeatherQueryFragment cityInfo = (WeatherQueryFragment) list.get(0);
				if (cityInfo != null && !TextUtils.isEmpty(cityInfo.getCity())) {
					top_center.setText(cityInfo.getCity());
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rg_point.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			RadioButton view = (RadioButton) LayoutInflater.from(this).inflate(R.layout.banner_point, null);
			android.widget.RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(Util.dpToPx(7), Util.dpToPx(7));
			params.setMargins(3, 0, 3, 0);
			view.setLayoutParams(params);

			rg_point.addView(view);

		}
		return list;

	}

	@OnClick({ R.id.ll_center })
	public void click(final View v) {

		AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new OnAnimEnd() {

			@Override
			public void start() {
				// TODO Auto-generated method stub

			}

			@Override
			public void repeat() {
				// TODO Auto-generated method stub

			}

			@Override
			public void end() {

				switch (v.getId()) {

				case R.id.ll_center:

					Util.showIntentForResult(context, WeatherSelectCityQueryActivity.class, new String[] {},
							new Serializable[] {}, 100);

					break;

				}

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.e("sd","onActivityResult");

		if (data != null && resultCode == 100) {

			List<WeatherQueryFragment> list = buidData("onActivityResult");
			adapter.setFragments(list);
			MyLog.e("城市列表长度"+pager.getAdapter().getCount());
			if (data.hasExtra("num")) {
				int intExtra = data.getIntExtra("num", 0);
				MyLog.e("城市列表长度"+pager.getAdapter().getCount()+"position"+intExtra);
				pager.setCurrentItem(intExtra);
			} else {
				pager.setCurrentItem(0);
			}
		}

	}

	private void postCodeCityQuery() {
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

				try {
					long count = DbUtils.create(App.getContext())
							.count(Selector.from(CityString.class).where("key", "=", "PostCode"));
					if (count < 1) {
						final CityString cityString = new CityString();
						cityString.setCity(lists);
						cityString.setKey("PostCode");
						Util.asynTask(new IAsynTask() {

							@Override
							public void updateUI(Serializable runData) {
								// TODO Auto-generated method stub

							}

							@Override
							public Serializable run() {
								try {
									DbUtils.create(App.getContext()).saveBindingId(cityString);
								} catch (DbException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}
						});

					}

				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://v.juhe.cn/postcode/pcd?key=3287cd51e91a12cba3da80bd32704041");
				return web.getPlan();
			}
		});
	}

	private void isSave() {
		try {
			long count = DbUtils.create(App.getContext())
					.count(Selector.from(CityString.class).where("key", "=", "PostCode"));
			if (count < 1) {
				postCodeCityQuery();
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		try {
			context.getApplicationContext().unbindService(locationServiceConnection);
		}catch (Exception e){

		}

	}
}
