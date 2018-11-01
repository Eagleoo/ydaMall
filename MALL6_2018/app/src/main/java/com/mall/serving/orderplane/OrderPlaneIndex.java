package com.mall.serving.orderplane;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.CardMainFrame;
import com.mall.model.LocationModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.yyrg.YYRGFrame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderPlaneIndex extends Activity {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;

	private ViewPager banner;
	private List<View> bannersView;
	private TextView[] dots = null;
	private AtomicInteger what = new AtomicInteger();
	private boolean isContinue = true;
	private LinearLayout dotcontainer;
	private String cabintype = "经济舱", year = "";
	private TextView start_city, end_city, start_date, economy, reach_date;
	@ViewInject(R.id.reach_date_layout)
	private LinearLayout reach_date_layout;
	private String take_off_date;
	private ImageView switch_start_and_end;
	String[] week = new String[] { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
	private Button submit;
	String months = "", days = "", dayofweeks = "";
	private List<ImageView> cabinTypes = new ArrayList<ImageView>();
	private SharedPreferences sp;
	private LinearLayout voice_search;
	private String plantype = "单程";
	private String cangwei = "";
	@ViewInject(R.id.signal_way)
	private TextView signal_way;
	@ViewInject(R.id.two_way)
	private TextView two_way;
	@ViewInject(R.id.sanyuenei)
	private LinearLayout sanyuenei;
	@ViewInject(R.id.sanyueqian)
	private LinearLayout sanyueqian;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orderplanceindex);
		sp = this.getSharedPreferences("PlaneCities", 0);
		ViewUtils.inject(this);
		init();
		if (!Util.isNull(LocationModel.getLocationModel().getLocationType())) {
			String cityName = LocationModel.getLocationModel().getCity();
			if (!TextUtils.isEmpty(cityName) && cityName.endsWith("市")) {
				cityName = cityName.replace("市", "");
			}
			start_city.setText(cityName);
		}
	}

	private void init() {
		// Util.initTop(OrderPlaneIndex.this, "订机票", 0, new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Util.showIntent(OrderPlaneIndex.this, Lin_MainFrame.class);
		// }
		// }, null);
		top_left.setVisibility(View.VISIBLE);
		top_center.setText("订机票");

		initView();
		initBanner();
		checkLocationCity();
	}

	@OnClick({ R.id.fi1, R.id.fi2 })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.fi1:
			signal_way.setTextColor(getResources().getColor(R.color.blue));
			signal_way.setBackgroundColor(getResources().getColor(R.color.bg));
			two_way.setBackgroundColor(getResources().getColor(R.color.transparent));
			sanyuenei.setVisibility(View.VISIBLE);
			sanyueqian.setVisibility(View.INVISIBLE);
			two_way.setTextColor(getResources().getColor(R.color.bg));
			reach_date_layout.setVisibility(View.GONE);
			plantype = "单程";
			break;
		case R.id.fi2:
			// 双程
			signal_way.setTextColor(getResources().getColor(R.color.bg));
			sanyuenei.setVisibility(View.INVISIBLE);
			sanyueqian.setVisibility(View.VISIBLE);
			two_way.setTextColor(getResources().getColor(R.color.blue));
			signal_way.setBackgroundColor(getResources().getColor(R.color.transparent));
			two_way.setBackgroundColor(getResources().getColor(R.color.bg));
			plantype = "双程";
			reach_date_layout.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void initView() {
		banner = (ViewPager) this.findViewById(R.id.resturant_banner);
		dotcontainer = (LinearLayout) this.findViewById(R.id.dot_container);
		start_city = (TextView) this.findViewById(R.id.start_city);
		start_city.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OrderPlaneIndex.this, PlaneCities.class);
				OrderPlaneIndex.this.startActivityForResult(intent, 212);
			}
		});
		end_city = (TextView) this.findViewById(R.id.destination_city);
		end_city.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OrderPlaneIndex.this, PlaneCities.class);
				OrderPlaneIndex.this.startActivityForResult(intent, 213);
			}
		});
		switch_start_and_end = (ImageView) this.findViewById(R.id.switch_start_and_end);
		switch_start_and_end.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String endcity = end_city.getText().toString();
				String startcity = start_city.getText().toString();
				String tagendcity = end_city.getTag().toString();
				String tagstartcity = start_city.getTag().toString();
				start_city.setText(endcity);
				end_city.setText(startcity);
				start_city.setTag(tagendcity);
				end_city.setTag(tagstartcity);
			}
		});
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		year = c.get(Calendar.YEAR) + "";
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int day_off_week = c.get(Calendar.DAY_OF_WEEK);
		months = month + "";
		days = day + "";
		dayofweeks = week[day_off_week - 1];
		start_date = (TextView) this.findViewById(R.id.start_date);
		start_date.setText(month + "月" + day + "日" + "     " + week[day_off_week - 1]);
		start_date.setTag(year + "-" + month + "-" + day);
		start_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OrderPlaneIndex.this, PlaneDateWidget.class);
				intent.putExtra("title", "起飞");
				OrderPlaneIndex.this.startActivityForResult(intent, 210);
			}
		});
		c.add(Calendar.DAY_OF_MONTH, 4);
		reach_date = (TextView) this.findViewById(R.id.reach_date);
		reach_date.setText((c.get(Calendar.MONTH) + 1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日" + "     " + week[c.get(Calendar.DAY_OF_WEEK) - 1]);
		reach_date.setTag((c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
		reach_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OrderPlaneIndex.this, PlaneDateWidget.class);
				intent.putExtra("title", "返程");
				OrderPlaneIndex.this.startActivityForResult(intent, 211);
			}
		});
		economy = (TextView) this.findViewById(R.id.economy);
		economy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cabinTypes.clear();
				AlertDialog.Builder builder = new AlertDialog.Builder(OrderPlaneIndex.this);
				LayoutInflater inflater = LayoutInflater.from(OrderPlaneIndex.this);
				View view = inflater.inflate(R.layout.plane_cabin_type, null);
				ImageView one, two, three;
				one = (ImageView) view.findViewById(R.id.one);
				two = (ImageView) view.findViewById(R.id.two);
				three = (ImageView) view.findViewById(R.id.three);
				cabinTypes.add(one);
				cabinTypes.add(two);
				cabinTypes.add(three);
				one.setOnClickListener(new RadioButtonOnClick(0));
				two.setOnClickListener(new RadioButtonOnClick(1));
				three.setOnClickListener(new RadioButtonOnClick(2));
				Button submit = (Button) view.findViewById(R.id.submit);
				builder.setCancelable(false);
				final Dialog dialog;
				dialog = builder.show();
				Window window = dialog.getWindow();
				WindowManager.LayoutParams pa = window.getAttributes();
				pa.width = Util.dpToPx(OrderPlaneIndex.this, 300);
				pa.height = Util.dpToPx(OrderPlaneIndex.this, 280);
				dialog.setContentView(view, pa);
				submit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
						economy.setText(cabintype);
					}
				});
			}
		});
		submit = (Button) this.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isNull(start_city.getTag()) || Util.isNull(start_city.getText().toString())) {
					Toast.makeText(OrderPlaneIndex.this, "出发城市不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if (Util.isNull(end_city.getTag()) || Util.isNull(end_city.getText().toString())) {
					Toast.makeText(OrderPlaneIndex.this, "目的城市不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				if (Util.isNull(start_date.getTag()) || Util.isNull(start_date.getText().toString())) {
					Toast.makeText(OrderPlaneIndex.this, "日期不能为空", Toast.LENGTH_LONG).show();
					return;
				}
				String st = start_city.getText().toString().trim();
				String ed = end_city.getText().toString().trim();
				if (st.contains(ed) || ed.contains(st)) {
					Toast.makeText(OrderPlaneIndex.this, "出发城市不能与目的城市相同", Toast.LENGTH_LONG).show();
					return;
				}
				if (plantype.equals("双程")) {
					int start = 0;
					int end = 0;
					try {
						start = Integer.parseInt(months) * 30 + Integer.parseInt(days);
						end = Integer.parseInt(reach_date.getTag().toString().split("-")[0]) * 30 + Integer.parseInt(reach_date.getTag().toString().split("-")[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (start > end) {
						Toast.makeText(OrderPlaneIndex.this, "您选择的返程日期必须大于等于出发日期", Toast.LENGTH_LONG).show();
						return;
					}
				}
				if (plantype.equals("双程")) {
					Intent intent = new Intent(OrderPlaneIndex.this, PlaneListTwoWay.class);
					intent.putExtra("fromcity", start_city.getTag().toString());
					intent.putExtra("fromcityname", start_city.getText().toString());
					intent.putExtra("cityto", end_city.getTag().toString());
					intent.putExtra("citytoname", end_city.getText().toString());
					intent.putExtra("time", start_date.getTag().toString());
					intent.putExtra("date", start_date.getText().toString());
					intent.putExtra("backtime", reach_date.getTag().toString());
					intent.putExtra("backdate", reach_date.getText().toString());
					intent.putExtra("planetype", plantype);// 传递是否是单程票
					intent.putExtra("cangwei", cangwei);
					OrderPlaneIndex.this.startActivity(intent);
				} else if (plantype.equals("单程")) {
					Intent intent = new Intent(OrderPlaneIndex.this, PlaneList.class);
					String code = CityUtils.getAirportCode(start_city.getText().toString());
					if (null == code) {
						code = CityUtils.getAirportCode(start_city.getText().toString() + "市");
						if (null == code) {
							code = CityUtils.getAirportCode(start_city.getText().toString().replaceFirst("市", ""));
						}
					}
					if (null == code) {
						Util.show("获取机场三字码出错，请手动选择您的出发城市！", OrderPlaneIndex.this);
						return;
					}
					intent.putExtra("fromcity", code);
					intent.putExtra("fromcityname", start_city.getText().toString());
					intent.putExtra("cityto", end_city.getTag().toString());
					intent.putExtra("citytoname", end_city.getText().toString());
					LogUtils.e("出发城市：" + start_city.getText().toString() + "=" + code + "  到大城市=" + end_city.getText().toString() + "=" + end_city.getTag().toString());
					intent.putExtra("time", start_date.getTag().toString());
					intent.putExtra("date", start_date.getText().toString());
					intent.putExtra("planetype", plantype);// 传递是否是单程票
					intent.putExtra("date", start_date.getText().toString());
					intent.putExtra("year", year);
					intent.putExtra("month", months);
					intent.putExtra("day", days);
					intent.putExtra("dayofweek", dayofweeks);
					intent.putExtra("cangwei", cangwei);
					OrderPlaneIndex.this.startActivity(intent);
				}
			}
		});
		voice_search = (LinearLayout) this.findViewById(R.id.voice_search);
		voice_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请开始说话!");
					startActivityForResult(intent, 225);
				} catch (ActivityNotFoundException e) {
					AlertDialog.Builder builder = new AlertDialog.Builder(OrderPlaneIndex.this);
					builder.setTitle("语音识别");
					builder.setMessage("您的手机暂不支持语音搜索功能，点击确定下载安装Google语音搜索软件");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Util.openWeb(OrderPlaneIndex.this, "http://" + Web.voiceApk + "/voice.apk");
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}

			}
		});
	}

	private void initBanner() {
		initBannersView();
		initDotContainer();
		banner.setAdapter(new BannerAdapter());
		banner.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				banner.setCurrentItem(arg0);
				for (int i = 0; i < dots.length; i++) {
					if (arg0 == i) {
						dots[i].setBackgroundColor(Color.parseColor("#f72828"));
					} else {
						dots[i].setBackgroundColor(Color.parseColor("#cac8c8"));
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (isContinue) {
						bannerHandler.sendEmptyMessage(what.get());
						whatOption();
					}
				}
			}
		}).start();
	}

	private Handler bannerHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			banner.setCurrentItem(msg.what);
			super.handleMessage(msg);
		};
	};

	private void whatOption() {
		what.incrementAndGet();
		if (what.get() > bannersView.size() - 1) {
			what.getAndAdd(-4);
		}
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initBannersView() {
		bannersView = new ArrayList<View>();
		int _120dp = Util.pxToDp(this, 120);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, _120dp);

		ImageView banner1 = new ImageView(this);
		banner1.setImageResource(R.drawable.jp_yyrg);
		banner1.setLayoutParams(lp);

		// ImageView banner2 = new ImageView(this);
		// banner2.setImageResource(R.drawable.jp_hdsq);
		// banner2.setLayoutParams(lp);

		banner1.setScaleType(ScaleType.CENTER_CROP);
		// banner2.setScaleType(ScaleType.CENTER_CROP);

		ImageView banner3 = new ImageView(this);
		banner3.setImageResource(R.drawable.jp_mpt);
		banner3.setLayoutParams(lp);
		banner3.setScaleType(ScaleType.CENTER_CROP);

		bannersView.add(banner1);
		// bannersView.add(banner2);
		bannersView.add(banner3);
	}

	private void initDotContainer() {
		dots = new TextView[bannersView.size()];
		for (int i = 0; i < bannersView.size(); i++) {
			TextView dot;
			if (i == 0) {
				dot = createDot();
				dot.setBackgroundColor(Color.parseColor("#f72828"));
				dotcontainer.addView(dot);
				dots[i] = dot;
			} else {
				dot = createDot();
				dot.setBackgroundColor(Color.parseColor("#cac8c8"));
				dotcontainer.addView(dot);
				dots[i] = dot;
			}
		}
	}

	private TextView createDot() {
		TextView te = new TextView(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 5);
		params.setMargins(5, 0, 5, 0);
		te.setLayoutParams(params);
		return te;
	}

	private void checkLocationCity() {
		if (!Util.isNull(sp.getString("planecities", ""))) {
			// 如果城市数据保存下来了，则不用加载城市数据
			// 根据a-z进行排序源数据
			String s = sp.getString("planecities", "");
			parseCities(s);
		} else {
			// 如果城市数据未保存,则重新获取数据
			getCities();
		}
	}

	private void getCities() {
		Util.asynTask(OrderPlaneIndex.this, "", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				sp.edit().putString("planecities", result).commit();
				parseCities(result);
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.Ticket_getCity, "");
				String result = web.getPlanGb2312();
				return result;
			}
		});
	}

	private void parseCities(String result) {
		String[] results = result.split("@");
		for (int i = 0; i < results.length; i++) {
			String[] rs = results[i].split("\\|");
			if (!Util.isNull(start_city.getText().toString())) {
				if (rs[0].equals(start_city.getText())) {
					start_city.setTag(rs[2]);
				}
			}
		}
	}

	public class BannerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return bannersView.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(bannersView.get(position));
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			View view = bannersView.get(position);
			((ViewPager) container).addView(view);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (0 == position) {
						Util.showIntent(OrderPlaneIndex.this, YYRGFrame.class);
					} else {
						if (null == UserData.getUser()) {
							Util.showIntent(OrderPlaneIndex.this, LoginFrame.class);
							return;
						}
						Util.showIntent(OrderPlaneIndex.this, CardMainFrame.class);
					}
				}
			});
			return bannersView.get(position);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 210) {
			if (!Util.isNull(data)) {
				if (!Util.isNull(data.getStringExtra("takeoff")) && !Util.isNull(data.getStringExtra("year"))) {
					take_off_date = data.getStringExtra("takeoff");
					year = data.getStringExtra("year");
					String[] ts = take_off_date.split(",,,,");
					String tx = ts[0].replace(".", "-");
					String[] dates = tx.split("-");
					months = dates[0];
					days = dates[1];
					start_date.setTag(year + "-" + dates[0] + "-" + dates[1]);
					int start = 0;
					int end = 0;
					try {
						start = Integer.parseInt(months) * 30 + Integer.parseInt(days);
						end = Integer.parseInt(reach_date.getTag().toString().split("-")[0]) * 30 + Integer.parseInt(reach_date.getTag().toString().split("-")[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (plantype.equals("双程")) {
						if (start > end) {
							Toast.makeText(OrderPlaneIndex.this, "您选择的返程日期必须大于等于出发日期", Toast.LENGTH_LONG).show();
							return;
						} else {
							start_date.setTag(year + "-" + dates[0] + "-" + dates[1]);
							start_date.setText(dates[0] + "月" + dates[1] + "日" + "     " + ts[1]);
							dayofweeks = ts[1];
						}
					} else {
						start_date.setTag(year + "-" + dates[0] + "-" + dates[1]);
						start_date.setText(dates[0] + "月" + dates[1] + "日" + "     " + ts[1]);
						dayofweeks = ts[1];
					}
				}
			} else {
				System.out.println("传递过来的数据为空");
			}
		} else if (requestCode == 211) {
			if (!Util.isNull(data)) {
				if (!Util.isNull(data.getStringExtra("takeoff")) && !Util.isNull(data.getStringExtra("year"))) {
					take_off_date = data.getStringExtra("takeoff");
					year = data.getStringExtra("year");
					String[] ts = take_off_date.split(",,,,");
					String tx = ts[0].replace(".", "-");
					String[] dates = tx.split("-");
					reach_date.setTag(dates[0] + "-" + dates[1]);
					int start = 0;
					int end = 0;
					try {
						start = Integer.parseInt(months) * 30 + Integer.parseInt(days);
						end = Integer.parseInt(reach_date.getTag().toString().split("-")[0]) * 30 + Integer.parseInt(reach_date.getTag().toString().split("-")[1]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (start > end) {
						Toast.makeText(OrderPlaneIndex.this, "您选择的返程日期必须大于等于出发日期", Toast.LENGTH_LONG).show();
						return;
					} else {
						plantype = "双程";
						reach_date.setTag(dates[0] + "-" + dates[1]);
						reach_date.setText(dates[0] + "月" + dates[1] + "日" + "     " + ts[1]);
					}
				}
			} else {
				System.out.println("传递过来的数据为空");
			}
		} else if (requestCode == 212) {
			if (data != null) {
				if (!Util.isNull(data.getStringExtra("name"))) {
					start_city.setText(data.getStringExtra("name"));
				}
				if (!Util.isNull(data.getStringExtra("city"))) {
					start_city.setTag(data.getStringExtra("city"));
				}
			}
		} else if (requestCode == 213) {
			if (data != null) {
				if (!Util.isNull(data.getStringExtra("name"))) {
					end_city.setText(data.getStringExtra("name"));
				}
				if (!Util.isNull(data.getStringExtra("city"))) {
					end_city.setTag(data.getStringExtra("city"));
				}
			}
		} else if (requestCode == 225) {
			if (data != null) {
				ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				if (result.size() > 0) {
					start_city.setText(result.get(0));
				}
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class RadioButtonOnClick implements OnClickListener {
		private int pos;

		public RadioButtonOnClick(int pos) {
			this.pos = pos;
		}

		@Override
		public void onClick(View v) {
			ImageView view = (ImageView) v;
			if (view.getTag().equals("noselected")) {
				view.setImageResource(R.drawable.radiobutton_down);
				view.setTag("selected");
			}
			for (int i = 0; i < cabinTypes.size(); i++) {
				if (i != pos) {
					ImageView imageview = cabinTypes.get(i);
					imageview.setImageResource(R.drawable.radiobutton_up);
					imageview.setTag("noselected");
				}
			}
			if (pos == 0) {
				cabintype = "经济舱";
				cangwei = "1";
			} else if (pos == 1) {
				cabintype = "头等舱";
				cangwei = "2";
			} else if (pos == 2) {
				cabintype = "不限";
				cangwei = "";
			}
		}
	}
}
