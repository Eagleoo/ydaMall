package com.mall.serving.query.fragment;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.bounce.BounceEaseOut;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.serving.community.fragment.BaseReceiverFragment;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.dialog.CustomDialog;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshScrollView;
import com.mall.serving.query.activity.weather.WeatherMainQueryActivity;
import com.mall.serving.query.activity.weather.WeatherSelectCityQueryActivity;
import com.mall.serving.query.adapter.WeatherGrid2Adapter;
import com.mall.serving.query.adapter.WeatherGridAdapter;
import com.mall.serving.query.configs.QueryConfigs;
import com.mall.serving.query.model.CityString;
import com.mall.serving.query.model.WeatherCityInfo;
import com.mall.serving.query.model.WeatherExpInfo;
import com.mall.serving.query.model.WeatherInfo;
import com.mall.serving.query.model.WeatherInfo.Data.Life.LifeInfo;
import com.mall.serving.query.model.WeatherInfo.Data.Weather;
import com.mall.serving.query.model.WeatherInfo.Data.Weather.Info;
import com.mall.util.MyLog;
import com.mall.view.App;
import com.mall.view.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressLint("ValidFragment")
public class WeatherQueryFragment extends BaseReceiverFragment {
	@ViewInject(R.id.linechart)
	private LineChartView mLineChart;
	@ViewInject(R.id.gridview1)
	private GridView gridview1;
	@ViewInject(R.id.gridview2)
	private GridView gridview2;
	@ViewInject(R.id.gridview3)
	private GridView gridview3;

	@ViewInject(R.id.tv_weather_main)
	private TextView tv_weather_main;
	@ViewInject(R.id.tv_temp)
	private TextView tv_temp;
	@ViewInject(R.id.tv_update)
	private TextView tv_update;
	@ViewInject(R.id.tv_wind)
	private TextView tv_wind;
	@ViewInject(R.id.tv_humidity)
	private TextView tv_humidity;
	@ViewInject(R.id.tv_moon)
	private TextView tv_moon;
	@ViewInject(R.id.tv_today)
	private TextView tv_today;
	@ViewInject(R.id.tv_today_weather)
	private TextView tv_today_weather;
	@ViewInject(R.id.tv_tomorrow)
	private TextView tv_tomorrow;
	@ViewInject(R.id.tv_tomorrow_weather)
	private TextView tv_tomorrow_weather;
	@ViewInject(R.id.tv_pm25)
	private TextView tv_pm25;

	private final String TEMP = "℃";
	private WeatherInfo info;
	private final TimeInterpolator enterInterpolator = new DecelerateInterpolator(1.5f);
	private final TimeInterpolator exitInterpolator = new AccelerateInterpolator();

	private String city = "北京";
	private boolean isFirst = true;
	private PullToRefreshScrollView refreshScrollView;

	public WeatherMainQueryActivity activity;
	
	public String uptime="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		info = new WeatherInfo();
		activity = (WeatherMainQueryActivity) getActivity();

	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("onCreateView" + city);
		View v = inflater.inflate(R.layout.query_weather_content, container, false);
		ViewUtils.inject(this, v);

		refreshScrollView = new PullToRefreshScrollView(context);
		refreshScrollView.setHeadTextColor(getResources().getColor(R.color.white));
		ScrollView scrollView = refreshScrollView.getRefreshableView();
		scrollView.addView(v);
		scrollView.setVerticalScrollBarEnabled(false);
		refreshScrollView.setPullLoadEnabled(false);
		refreshScrollView.setPullRefreshEnabled(true);

		setListener();
		getWeatherString();
		isFirst = false;

		return refreshScrollView;
	}

	/**
	 * Line
	 */
	private int LINE_MAX;
	private int LINE_MIN;
//	private String[] lineLabels = { "", "ANT", "GNU", "OWL", "APE","",""};
	private String[] lineLabels = {"ANT", "GNU", "OWL", "APE","JPE"};
//	private float[][] lineValues = { { -5f, 6f, 2f, 9f, 0f ,2f,3f}, { -9f, -2f, -4f, -3f, -7f ,2f,3f} };
	private float[][] lineValues = { { -5f, 6f, 2f, 9f, 0f }, { -9f, -2f, -4f, -3f, -7f } };

	private Paint mLineGridPaint;
	private TextView mLineTooltip;

	private final OnEntryClickListener lineEntryListener = new OnEntryClickListener() {
		@Override
		public void onClick(int setIndex, int entryIndex, Rect rect) {
			System.out.println("OnEntryClickListener");
			if (mLineTooltip == null)
				showLineTooltip(setIndex, entryIndex, rect);
			else
				dismissLineTooltip(setIndex, entryIndex, rect);
		}
	};

	private final OnClickListener lineClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mLineTooltip != null)
				dismissLineTooltip(-1, -1, null);
		}
	};

	private void initLineChart() {

		int with =	Util.dpToPx(40);
		MyLog.e("int with"+with);
		LinearLayout.LayoutParams layoutParams1= (LinearLayout.LayoutParams) (mLineChart).getLayoutParams();
		layoutParams1.leftMargin=with;
		layoutParams1.rightMargin=with;
		mLineChart.setLayoutParams(layoutParams1);

		mLineChart.setOnEntryClickListener(lineEntryListener);

		mLineChart.setOnClickListener(lineClickListener);

		mLineGridPaint = new Paint();
		MyLog.e("isadd"+isAdded());
		if (isAdded()){
			mLineGridPaint.setColor(this.getResources().getColor(R.color.white));
			mLineGridPaint.setPathEffect(new DashPathEffect(new float[] { 7, 7 }, 0));
			mLineGridPaint.setStyle(Paint.Style.STROKE);
			mLineGridPaint.setAntiAlias(true);
			mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
		}

	}
	@Override
	public void onAttach(Context context) {
//		if (context instanceof OnCoverChangeListener) {
//			mListener = (ABC_Listener) context;
//		} else {
//			throw new RuntimeException(context.toString()
//					+ " must implement ABC_Listener");
//		}
		super.onAttach(context);
		MyLog.e(" onAttach context isadd"+isAdded());
	}

	//SDK API<23时，onAttach(Context)不执行，需要使用onAttach(Activity)。Fragment自身的Bug，v4的没有此问题
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			MyLog.e(" onAttach activity isadd"+isAdded());
//			if (activity instanceof OnCoverChangeListener) {
//				mListener = (ABC_Listener) activity;
//			} else {
//				throw new RuntimeException(activity.toString()
//						+ " must implement ABC_Listener");
//			}
		}

	}
	private void updateLineChart() {

		mLineChart.reset();
		MyLog.e("isadd"+isAdded());
		if (isAdded()){
			LineSet dataSet = new LineSet();
			MyLog.e("isadd"+isAdded()+"lineLabels.length"+lineLabels.length+"lineValues.length"+lineValues.length);
			dataSet.addPoints(lineLabels, lineValues[0]);
			dataSet.setDots(true).setDotsColor(this.getResources().getColor(R.color.yellow))
					.setDotsRadius(Tools.fromDpToPx(5)).setDotsStrokeThickness(Tools.fromDpToPx(2))
					.setDotsStrokeColor(this.getResources().getColor(R.color.yellow))
					.setLineColor(this.getResources().getColor(R.color.yellow)).setLineThickness(Tools.fromDpToPx(3));
			mLineChart.addData(dataSet);

			dataSet = new LineSet();
			dataSet.addPoints(lineLabels, lineValues[1]);
			dataSet.setDots(true).setDotsColor(this.getResources().getColor(R.color.white))
					.setDotsRadius(Tools.fromDpToPx(5)).setDotsStrokeThickness(Tools.fromDpToPx(2))
					.setDotsStrokeColor(this.getResources().getColor(R.color.white))
					.setLineColor(this.getResources().getColor(R.color.white)).setLineThickness(Tools.fromDpToPx(3));
			mLineChart.addData(dataSet);

			mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
					// .setHorizontalGrid(mLineGridPaint)
					.setXAxis(false).setXLabels(XController.LabelPosition.NONE).setYAxis(false)
					.setYLabels(YController.LabelPosition.NONE).setAxisBorderValues(LINE_MIN, LINE_MAX, 5)
					// .setLabelsMetric("u")
					.show(getAnimation(true).setEndAction(null));
		}

	}

	private Animation getAnimation(boolean newAnim) {

		int mCurrAlpha = -1;

		float mCurrStartX = 0f;
		float mCurrStartY = -1f;
		float mCurrOverlapFactor = .5f;
		int[] mCurrOverlapOrder = { 0, 1, 2, 3, 4,5,6};
		BaseEasingMethod mCurrEasing = new BounceEaseOut();

		return new Animation().setAlpha(mCurrAlpha).setEasing(mCurrEasing)
				.setOverlap(mCurrOverlapFactor, mCurrOverlapOrder).setStartPoint(mCurrStartX, mCurrStartY);

	}

	@SuppressLint("NewApi")
	private void showLineTooltip(int setIndex, int entryIndex, Rect rect) {

		TextView tv = new TextView(context);
		tv.setTextSize(12);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(getResources().getColor(R.color.blue));
		tv.setBackgroundResource(R.drawable.community_white_point_circle_shape);
		mLineTooltip = tv;
		mLineTooltip.setText(Integer.toString((int) lineValues[setIndex][entryIndex]) + "℃");

		LayoutParams layoutParams = new LayoutParams((int) Tools.fromDpToPx(35), (int) Tools.fromDpToPx(35));
		layoutParams.leftMargin = rect.centerX() - layoutParams.width / 2;
		layoutParams.topMargin = rect.centerY() - layoutParams.height / 2;
		mLineTooltip.setLayoutParams(layoutParams);

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			mLineTooltip.setPivotX(layoutParams.width / 2);
			mLineTooltip.setPivotY(layoutParams.height / 2);
			mLineTooltip.setAlpha(0);
			mLineTooltip.setScaleX(0);
			mLineTooltip.setScaleY(0);
			mLineTooltip.animate().setDuration(150).alpha(1).scaleX(1).scaleY(1).rotation(360)
					.setInterpolator(enterInterpolator);
		}

		mLineChart.showTooltip(mLineTooltip);
	}

	@SuppressLint("NewApi")
	private void dismissLineTooltip(final int setIndex, final int entryIndex, final Rect rect) {

		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mLineTooltip.animate().setDuration(100).scaleX(0).scaleY(0).alpha(0).setInterpolator(exitInterpolator)
					.withEndAction(new Runnable() {
						@Override
						public void run() {
							mLineChart.removeView(mLineTooltip);
							mLineTooltip = null;
							if (entryIndex != -1)
								showLineTooltip(setIndex, entryIndex, rect);
						}
					});
		} else {
			mLineChart.dismissTooltip(mLineTooltip);
			mLineTooltip = null;
			if (entryIndex != -1)
				showLineTooltip(setIndex, entryIndex, rect);
		}
	}

	private void getWeatherString() {

		try {

			System.out.println("getWeatherString()" + city);
			CityString findFirst = DbUtils.create(App.getContext())
					.findFirst(Selector.from(CityString.class).where("key", "=", city));
			if (findFirst != null) {
				
				getWeatherInfo(findFirst.getCity());

			} else {
				weatherQuery();
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void weatherQuery() {
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
					CityString cityString = DbUtils.create(App.getContext())
							.findFirst(Selector.from(CityString.class).where("key", "=", city));
					if (cityString == null) {
						cityString = new CityString();
						cityString.setCity(lists);
						cityString.setKey(city);

						final CityString cityS = cityString;
						Util.asynTask(new IAsynTask() {

							@Override
							public void updateUI(Serializable runData) {
								// TODO Auto-generated method stub

							}

							@Override
							public Serializable run() {
								try {
									DbUtils.create(App.getContext()).saveBindingId(cityS);
								} catch (DbException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}
						});

					} else {
						cityString.setCity(lists);
						cityString.setKey(city);

						final CityString cityS = cityString;
						Util.asynTask(new IAsynTask() {

							@Override
							public void updateUI(Serializable runData) {
								// TODO Auto-generated method stub

							}

							@Override
							public Serializable run() {
								try {
									DbUtils.create(App.getContext()).update(cityS);
								} catch (DbException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}
						});
					}
					refreshScrollView.onPullDownRefreshComplete();
					refreshScrollView.onPullUpRefreshComplete();
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("查询天气",lists);
				getWeatherInfo(lists);

			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://op.juhe.cn/onebox/weather/query?cityname=" + city
						+ "&key=0f176744e02379c77e43bdc8a1d1e4e4");
				return web.getPlan();
			}
		});

	}

	private void localWeather() {

		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				String str = (String) runData;
				if (!TextUtils.isEmpty(str)) {
					getWeatherInfo(str);
				}

			}

			@Override
			public Serializable run() {
				try {
					long count = DbUtils.create(App.getContext())
							.count(Selector.from(CityString.class).where("key", "=", city));
					System.out.println("localWeather()" + city);
					if (count <= 0) {
						return "";
					}

					CityString findFirst = DbUtils.create(App.getContext())
							.findFirst(Selector.from(CityString.class).where("key", "=", city));

					if (findFirst != null) {

						return findFirst.getCity();

					}
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});

	}

	private void getWeatherInfo(final String lists) {
		System.out.println("更新天气"+lists);
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {

				if (!context.isFinishing()) {
					System.out.println("更新天气"+city+"数据"+lists);
					initWeather();
				}

			}

			@Override
			public Serializable run() {
				info = JsonUtil.getPerson(lists, WeatherInfo.class);
				return null;
			}
		});

	}

	private void initWeather() {

		if (info != null) {
			uptime=info.getData().getRealtime().getTime();
			List<Weather> weather = info.getData().getWeather();
			WeatherGridAdapter adapter1 = new WeatherGridAdapter(context, weather, 0, info);
			gridview1.setAdapter(adapter1);
			WeatherGridAdapter adapter2 = new WeatherGridAdapter(context, weather, 1, info);
			gridview2.setAdapter(adapter2);

			float[][] values = new float[2][5];

			for (int i = 0; i < weather.size(); i++) {

				Weather wInfo = weather.get(i);
				Info info2 = wInfo.getInfo();

				List<String> day = info2.getDay();
				List<String> night = info2.getNight();
				if (day != null && day.size() >= 3) {
					int int1 = Util.getInt(day.get(2));
					values[0][i] = int1;
					if (i == 0) {
						LINE_MAX = int1;
						LINE_MIN = int1;
					}
					if (int1 > LINE_MAX) {
						LINE_MAX = int1;
					}
					if (int1 < LINE_MIN) {
						LINE_MIN = int1;
					}

				}
				if (night != null && night.size() >= 3) {
					int int1 = Util.getInt(night.get(2));
					values[1][i] = int1;
					if (int1 > LINE_MAX) {
						LINE_MAX = int1;
					}
					if (int1 < LINE_MIN) {
						LINE_MIN = int1;
					}
				}
			}

			int max = LINE_MAX / 5;

			if (LINE_MAX < 0) {
				LINE_MAX = max * 5;
			} else {
				LINE_MAX = (max + 1) * 5;
			}

			int min = LINE_MIN / 5;
			if (LINE_MIN < 0) {
				LINE_MIN = (min - 1) * 5;
			} else {
				LINE_MIN = min * 5;
			}

			lineValues = values;
			initLineChart();
			updateLineChart();

			LifeInfo lifeInfo = info.getData().getLife().getInfo();
			ArrayList<WeatherExpInfo> list = new ArrayList<WeatherExpInfo>();

			list.add(new WeatherExpInfo(R.drawable.query_weather_kt, "空调指数", lifeInfo.getKongtiao()));
			list.add(new WeatherExpInfo(R.drawable.query_weather_sport, "运动指数", lifeInfo.getYundong()));
			list.add(new WeatherExpInfo(R.drawable.query_weather_uvb, "紫外线指数", lifeInfo.getZiwaixian()));
			list.add(new WeatherExpInfo(R.drawable.query_weather_gm, "感冒指数", lifeInfo.getGanmao()));
			list.add(new WeatherExpInfo(R.drawable.query_weather_wash, "洗车指数", lifeInfo.getXiche()));
			//list.add(new WeatherExpInfo(R.drawable.query_weather_wr, "污染指数", lifeInfo.getWuran()));
			list.add(new WeatherExpInfo(R.drawable.query_weather_dress, "穿衣指数", lifeInfo.getChuanyi()));

			WeatherGrid2Adapter adapter3 = new WeatherGrid2Adapter(context, list, info);
			gridview3.setAdapter(adapter3);
			int weatherRid = QueryConfigs.getWeatherRid(weather.get(0).getInfo().getDay().get(0));
			tv_weather_main.setCompoundDrawablesWithIntrinsicBounds(0, weatherRid, 0, 0);
			tv_weather_main.setText(weather.get(0).getInfo().getDay().get(1));

			tv_temp.setText(info.getData().getRealtime().getWeather().getTemperature() + TEMP);
			//更新时间
			System.out.println("更新时间"+info.getData().getRealtime().getTime());
			tv_update.setText(uptime + "更新");
			tv_wind.setText(info.getData().getRealtime().getWind().getDirect()
					+ info.getData().getRealtime().getWind().getPower());

			tv_humidity.setText("湿度：" + info.getData().getRealtime().getWeather().getHumidity() + "%");

			tv_moon.setText("公历：" + info.getData().getRealtime().getDate() + "    农历："
					+ info.getData().getRealtime().getMoon());

			tv_today.setText(
					weather.get(0).getInfo().getDay().get(2) + "/" + weather.get(0).getInfo().getNight().get(2) + TEMP);
			tv_today_weather.setText(weather.get(0).getInfo().getDay().get(1));
			tv_tomorrow.setText(
					weather.get(1).getInfo().getDay().get(2) + "/" + weather.get(1).getInfo().getNight().get(2) + TEMP);
			tv_tomorrow_weather.setText(weather.get(1).getInfo().getDay().get(1));
			tv_pm25.setText(info.getData().getPm25().getPm25().getQuality());

			try {
				WeatherCityInfo cityInfo = DbUtils.create(App.getContext()).findFirst(Selector
						.from(WeatherCityInfo.class).where("city", "=", info.getData().getRealtime().getCity_name()));
				if (cityInfo != null) {
					cityInfo.setCity(info.getData().getRealtime().getCity_name());
					cityInfo.setRid(weatherRid);
					cityInfo.setWeather(weather.get(0).getInfo().getDay().get(1));

					final WeatherCityInfo cityI = cityInfo;
					Util.asynTask(new IAsynTask() {

						@Override
						public void updateUI(Serializable runData) {
							// TODO Auto-generated method stub

						}

						@Override
						public Serializable run() {
							try {
								DbUtils.create(App.getContext()).update(cityI);
							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return null;
						}
					});
				} else {
					cityInfo = new WeatherCityInfo();
					cityInfo.setRid(weatherRid);
					cityInfo.setWeather(weather.get(0).getInfo().getDay().get(1));
					cityInfo.setCity(info.getData().getRealtime().getCity_name());

					final WeatherCityInfo cityI = cityInfo;
					Util.asynTask(new IAsynTask() {

						@Override
						public void updateUI(Serializable runData) {
							// TODO Auto-generated method stub

						}

						@Override
						public Serializable run() {
							try {
								DbUtils.create(App.getContext()).save(cityI);
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

	}

	@OnClick({ R.id.ll_pm25, R.id.top_center })
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
				case R.id.ll_pm25:
					String des = info.getData().getPm25().getPm25().getDes();
					if (!TextUtils.isEmpty(des)) {
						new CustomDialog("PM2.5", des, context, "", "", null, null).show();
					}
					break;
				case R.id.top_center:
					Util.showIntent(context, WeatherSelectCityQueryActivity.class);

					break;

				}

			}
		});

	}

	private void setListener() {

		OnRefreshListener refreshListener = new OnRefreshListener() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				System.out.println("fragment:" + city);
				weatherQuery();

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				weatherQuery();
			}
		};
		refreshScrollView.setOnRefreshListener(refreshListener);

	}

}
