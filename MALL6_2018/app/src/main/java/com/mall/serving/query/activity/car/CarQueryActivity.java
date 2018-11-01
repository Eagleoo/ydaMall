package com.mall.serving.query.activity.car;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.LocationModel;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.model.CarCityInfo;
import com.mall.serving.query.model.CarCityInfo.CityInfo;
import com.mall.serving.query.model.CarIdInfo;
import com.mall.serving.query.model.CarResultInfo;
import com.mall.view.R;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.query_car_query_activity)
public class CarQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	private String city_name = "BJ";
	private String abbr = "京";
	private String hpzl = "02";// 号牌类型
	private String city_code = "SC_CD";
	private String car_ty = "小型车";

	@ViewInject(R.id.query_city)
	private TextView query_city;
	@ViewInject(R.id.car_type)
	private TextView car_type;
	@ViewInject(R.id.cphm_prefix)
	private TextView cphm_prefix;
	@ViewInject(R.id.car_cphm)
	private EditText car_cphm;
	@ViewInject(R.id.car_cjhm)
	private EditText car_cjhm;
	@ViewInject(R.id.car_engineno)
	private EditText car_engineno;
	@ViewInject(R.id.tv_engineno)
	private TextView tv_engineno;
	@ViewInject(R.id.tv_classno)
	private TextView tv_classno;

	private CityInfo info;

	private boolean no_need_engine;
	private boolean no_need_class;

	public static final String TAG = "com.mall.serving.query.activity.car.CarQueryActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();

		registerReceiverAtBase(new String[] { TAG });
	}

	private void setView() {
		top_center.setText("添加车辆");
		top_left.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		if (intent.hasExtra("city")) {
			String city = intent.getStringExtra("city");
			cityQuery(city);
			System.out.println(city);
		}

	}

	@OnClick({ R.id.submit, R.id.query_city, R.id.car_type })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.submit:

			String hphm = car_cphm.getText().toString();
			String classno = car_cjhm.getText().toString();
			String engineno = car_engineno.getText().toString();
			if (TextUtils.isEmpty(hphm)) {
				Util.show("车牌号码不能为空！");
				return;
			}

			if (!no_need_engine && TextUtils.isEmpty(engineno)) {
				Util.show("发动机号不能为空！");
				return;
			}
			if (!no_need_class && TextUtils.isEmpty(classno)) {
				Util.show("车架号码不能为空！");
				return;
			}

			hphm = abbr + hphm;

			CarResultInfo info2 = new CarResultInfo();
			info2.setClassno(classno);
			info2.setEngineno(engineno);
			info2.setHphm(hphm);
			info2.setHpzl(hpzl);
			info2.setCity(city_code);

			Util.showIntent(context, CarResultActivity.class, new String[] { "info" }, new Serializable[] { info2 });

			break;
		case R.id.query_city:
			if (!Util.isNetworkConnected()){
				Toast.makeText(context,"请检查你的网络！",Toast.LENGTH_SHORT).show();
				return;
			}

			LocationModel locationModel= LocationModel.getLocationModel();

			Log.e("当前定位的城市是","kkk"+locationModel.toString());

			Util.showIntent(context, CarCityActivity.class, new String[] { "province", "city_" },
					new String[] { locationModel.getProvince(), locationModel.getCity() });
			break;
		case R.id.car_type:
			Util.showIntent(context, CarCityActivity.class, new String[] { "carType" },
					new Serializable[] { "carType" });
			break;

		default:
			break;
		}

	}

	@Override
	public void onReceiveBroadcast(Intent intent) {
		super.onReceiveBroadcast(intent);
		if (intent.getAction().equals(TAG)) {
			if (intent.hasExtra("city")) {
				info = (CityInfo) intent.getSerializableExtra("city");
				setCityView();

			} else if (intent.hasExtra("type")) {
				CarIdInfo cinfo = (CarIdInfo) intent.getSerializableExtra("type");
				hpzl = cinfo.getId();
				car_type.setText(cinfo.getCar());
			}
		}
	}

	private void setCityView() {
		abbr = info.getAbbr();
		city_code = info.getCity_code();
		query_city.setText(info.getCity_name());
		cphm_prefix.setText(info.getAbbr());
		if (info.getEngine().equals("0")) {
			tv_engineno.setText("发动机号（选）");
			car_engineno.setHint("请输入完整的发动机号");
			no_need_engine = true;
		} else {
			tv_engineno.setText("发动机号（必）");
			no_need_engine = false;
			int eint = Util.getInt(info.getEngineno());
			if (eint == 0) {
				car_engineno.setHint("请输入完整的发动机号");
			} else {
				car_engineno.setHint("请输入发动机号后" + eint + "位");
			}

		}
		if (info.getClassa().equals("0")) {
			no_need_class = true;
			tv_classno.setText("车架号码（选）");
			car_cjhm.setHint("请输入完整的车架号码");
		} else {
			no_need_class = false;
			tv_classno.setText("车架号码（必）");
			int eint = Util.getInt(info.getClassno());
			if (eint == 0) {
				car_cjhm.setHint("请输入完整的车架号码");
			} else {
				car_cjhm.setHint("请输入车架号码后" + eint + "位");
			}

		}

	}

	private void findCityView(List<CarCityInfo> findAll, String city) {
		for (CarCityInfo carCityInfo : findAll) {

			List<CityInfo> citys = carCityInfo.getCitys();
			System.out.println(carCityInfo.getProvince());
			for (CityInfo cityInfo : citys) {
				System.out.println(cityInfo.getCity_name());
				if (city.contains(cityInfo.getCity_name())) {
					info = cityInfo;
					setCityView();

					return;

				}
			}

		}

		for (CarCityInfo carCityInfo : findAll) {

			List<CityInfo> citys = carCityInfo.getCitys();
			System.out.println(carCityInfo.getProvince());
			for (CityInfo cityInfo : citys) {
				System.out.println(cityInfo.getCity_name());
				if ("北京".contains(cityInfo.getCity_name())) {
					info = cityInfo;
					setCityView();

					break;
				}
			}

		}

	}

	private void cityQuery(final String city) {
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");

				String lists = map.get("list");

				final List<CarCityInfo> mlist = JsonUtil.getPersons(lists, new TypeToken<List<CarCityInfo>>() {
				});

				if (mlist != null) {

					findCityView(mlist, city);

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

}
