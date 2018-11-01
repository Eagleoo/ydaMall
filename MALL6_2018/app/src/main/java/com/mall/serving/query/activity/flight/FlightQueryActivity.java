package com.mall.serving.query.activity.flight;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.GetLastDateTime;
import com.mall.serving.community.util.SharedPreferencesUtils;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.customdatapicker.CustomDatePickerDialog;
import com.mall.serving.community.view.customdatapicker.CustomDatePickerDialog.onDateListener;
import com.mall.view.PositionService;
import com.mall.view.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@ContentView(R.layout.query_trainticket_activity)
public class FlightQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.tv_city_from)
	private TextView tv_city_from;
	@ViewInject(R.id.tv_city_to)
	private TextView tv_city_to;
	@ViewInject(R.id.tv_time_from)
	private TextView tv_time_from;
	@ViewInject(R.id.tv_time_to)
	private TextView tv_time_to;
	@ViewInject(R.id.tv_type)
	private TextView tv_type;
	@ViewInject(R.id.rb_rg_1)
	private RadioButton rb_rg_1;
	@ViewInject(R.id.rb_rg_2)
	private RadioButton rb_rg_2;
	@ViewInject(R.id.tr_time_to)
	private View tr_time_to;
	@ViewInject(R.id.tr_type)
	private View tr_type;

	

	private int rbIndex;
	
	private Calendar c1=Calendar.getInstance();
	private Calendar c2=Calendar.getInstance();
	
	private PositionService locationService;
	private String cityName="北京";
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
						tv_city_from.setText(cityName);
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
		setView();

		Intent intent = new Intent();
		intent.setAction(PositionService.TAG);
		intent.setPackage(getPackageName());
		getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

		String city_from = SharedPreferencesUtils
				.getSharedPreferencesString("flight_from");
		String city_to = SharedPreferencesUtils
				.getSharedPreferencesString("flight_to");
		
		if (!TextUtils.isEmpty(city_from)) {
			tv_city_from.setText(city_from);
		}
		
		tv_city_to.setText(city_to);
		
		String dayByDate = GetLastDateTime.getInstance().getDayByDate(new Date(), 0);
		String dayByDate1 = GetLastDateTime.getInstance().getDayByDate(new Date(), 1);
		
		c2.add(Calendar.DATE, 1);
		tv_time_from.setText(Util.formatDateTime("yyyy-MM-dd", "yyyy-MM-dd E", dayByDate));
		tv_time_to.setText(Util.formatDateTime("yyyy-MM-dd", "yyyy-MM-dd E", dayByDate1));
		tr_type.setVisibility(View.GONE);
		
		
		

		
	}

	private void setView() {
		rb_rg_1.setText("单程");
		rb_rg_2.setText("往返");
		top_center.setText("航班查询");
		top_left.setVisibility(View.VISIBLE);
		tr_time_to.setVisibility(View.GONE);
	}

	@OnClick({ R.id.tv_city_from, R.id.tv_city_to, R.id.tv_time_from,
			R.id.tv_time_to, R.id.tv_query })
	public void click(View v) {

		switch (v.getId()) {
		case R.id.tv_city_from:
			Intent intent = new Intent(this, FlightCityQueryActivity.class);

			startActivityForResult(intent, 101);

			break;
		case R.id.tv_city_to:
			Intent intent2 = new Intent(this,
					FlightCityQueryActivity.class);

			startActivityForResult(intent2, 102);
			break;
		case R.id.tv_time_from:
			buildDatePicker(tv_time_from,true);
			break;
		case R.id.tv_time_to:
			buildDatePicker(tv_time_to,false);
			break;
		
		case R.id.tv_query:

			String from = tv_city_from.getText().toString().trim();
			String to = tv_city_to.getText().toString().trim();
			String date = tv_time_from.getText().toString().trim();
			String date2 = tv_time_to.getText().toString().trim();
			if (TextUtils.isEmpty(from)) {
				Util.show("请选择出发城市");
				return;
			}
			if (TextUtils.isEmpty(to)) {
				Util.show("请选择到达城市");
				return;
			}
			if (TextUtils.isEmpty(date)) {
				Util.show("请选择出发时间");
				return;
			}

			if (rbIndex == 1) {
				if (TextUtils.isEmpty(date2)) {
					Util.show("请选择返回时间");
					return;
				} else {
					date2 = Util.formatDateTime("yyyy-MM-dd E", "yyyy-MM-dd",
							date2);
				}
			} else {
				date2 = "";
			}

			date = Util.formatDateTime("yyyy-MM-dd E", "yyyy-MM-dd", date);

			Util.showIntent(context, FlightQueryListActivity.class,
					new String[] { "from", "to", "date", "date2",  },
					new Serializable[] { from, to, date, date2});
			

			break;

		}
	}

	@OnClick({ R.id.iv_refresh })
	public void click2(View v) {
		AnimeUtil.startAnimation(context, v, R.anim.small_2_big,
				new OnAnimEnd() {

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
						String city_from = tv_city_from.getText().toString();
						String city_to = tv_city_to.getText().toString();
						String temp = city_from;
						tv_city_from.setText(city_to);
						tv_city_to.setText(temp);

					}
				});

	}

	@OnRadioGroupCheckedChange(R.id.rg)
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_rg_1:
			rbIndex = 0;
			tr_time_to.setVisibility(View.GONE);
			break;
		case R.id.rb_rg_2:
			rbIndex = 1;
			tr_time_to.setVisibility(View.VISIBLE);
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			String city = "";
			if (data != null && data.hasExtra("city")) {
				city = data.getStringExtra("city");
			}

			if (requestCode == 101) {
				tv_city_from.setText(city);
				SharedPreferencesUtils.setSharedPreferencesString("flight_from",
						city);
			} else if (requestCode == 102) {
				tv_city_to.setText(city);
				SharedPreferencesUtils.setSharedPreferencesString("flight_to",
						city);
			}
		}

	}

	private void buildDatePicker(final TextView tv,final boolean isFirst) {


		Calendar c = Calendar.getInstance();
		if (isFirst) {
			c=c1;
		}else {
			c=c2;
		}
		CustomDatePickerDialog dialog = new CustomDatePickerDialog(context, c);
		
		
		dialog.addDateListener(new onDateListener() {

			@Override
			public void dateFinish(Calendar ca) {

				if (isFirst) {
					c1=ca;
				}else {
					c2=ca;
				}
				Date time = ca.getTime();

				SimpleDateFormat sdfFrom = new SimpleDateFormat("yyyy-MM-dd E");
				String string = sdfFrom.format(time);

				tv.setText(string);
				if (isFirst&&rbIndex==1) {
					if (c2.compareTo(c1)<0) {
						c2=(Calendar) c1.clone();
						
						c2.add(Calendar.DATE, 2);
						String dayByDate = GetLastDateTime.getInstance().getDayByDate(c2.getTime(), 0);
						tv_time_to.setText(Util.formatDateTime("yyyy-MM-dd", "yyyy-MM-dd E", dayByDate));
					}
					
				}
			}
		});
		dialog.show();
		
		if (isFirst) {
			dialog.setNowData(Calendar.getInstance());
		}else {
			dialog.setNowData(c1);
		}

	}

	
}
