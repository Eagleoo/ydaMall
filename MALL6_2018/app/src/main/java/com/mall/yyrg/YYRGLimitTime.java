package com.mall.yyrg;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.TimeListRecord;

/**
 * 限时抢购的功能
 * 
 * @author Administrator
 * 
 */
public class YYRGLimitTime extends Activity {
	@ViewInject(R.id.thisweek)
	private TextView thisweek;
	@ViewInject(R.id.nextweek)
	private TextView nextweek;
	@ViewInject(R.id.lin_thisweek)
	private LinearLayout lin_thisweek;
	@ViewInject(R.id.lin_nextweek)
	private LinearLayout lin_nextweek;
	private int state = 1;
	// 当前系统的时间
	private String nowtime;
	// 当前的星期
	private int nowweek;
	@ViewInject(R.id.qianggouing)
	private TextView qianggouing;
	@ViewInject(R.id.wangqi)
	private TextView wangqi;
	private int width;
	private BitmapUtils bmUtil;
	// 限时揭晓的商品
	private List<TimeListRecord> timeListRecords = new ArrayList<TimeListRecord>();
	// 本周周二的日期
	private String tuesDate;
	// 本周周五的日期
	private String friDate;
	// 下周周二的日期
	private String nexttuesDate;
	// 下周周五的日期
	private String nextfriDate;
	@ViewInject(R.id.goods_list)
	private GridView goods_list;
	@ViewInject(R.id.top)
	private LinearLayout top;
	@ViewInject(R.id.lin7)
	private LinearLayout lin7;
	@ViewInject(R.id.re7)
	private RelativeLayout re7;
	@ViewInject(R.id.jinxing_ing)
	private RelativeLayout jinxing_ing;
	private PopupWindow distancePopup = null;
	// 倒计时的布局
	@ViewInject(R.id.shidian_sj)
	private LinearLayout shidian_sj;
	@ViewInject(R.id.shisi_sj)
	private LinearLayout shisi_sj;
	// 倒计时的提示文字
	@ViewInject(R.id.shidian_text)
	private TextView shidian_text;
	@ViewInject(R.id.shisi_text)
	private TextView shisi_text;
	// 显示倒计时的textview
	@ViewInject(R.id.sd_text1)
	private TextView sd_text1;
	@ViewInject(R.id.sd_text2)
	private TextView sd_text2;
	@ViewInject(R.id.sd_text3)
	private TextView sd_text3;
	@ViewInject(R.id.sd_text4)
	private TextView sd_text4;
	@ViewInject(R.id.sd_text5)
	private TextView sd_text5;
	@ViewInject(R.id.sd_text6)
	private TextView sd_text6;
	@ViewInject(R.id.sd_text7)
	private TextView sd_text7;

	@ViewInject(R.id.ss_text1)
	private TextView ss_text1;
	@ViewInject(R.id.ss_text2)
	private TextView ss_text2;
	@ViewInject(R.id.ss_text3)
	private TextView ss_text3;
	@ViewInject(R.id.ss_text4)
	private TextView ss_text4;
	@ViewInject(R.id.ss_text5)
	private TextView ss_text5;
	@ViewInject(R.id.ss_text6)
	private TextView ss_text6;
	@ViewInject(R.id.ss_text7)
	private TextView ss_text7;
	private long tozhouer;
	private long tozhouwu;
	private long nextzhouer;
	private long nextzhouwu;
	private Timer zr_now_timer = new Timer();
	private Timer zw_now_timer = new Timer();
	private Timer zr_next_timer = new Timer();
	private Timer zw_next_timer = new Timer();
	// 停止now系列的timer
	private int endNowTimter = 1;
	// 停止next系列的timer
	private int endNextTimter = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_limit_time_announce);
		ViewUtils.inject(this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		bmUtil = new BitmapUtils(this);
		judgenow();
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		getGoodProductTimeListRecord();
	}

	/***
	 * 两个日期相差多少秒
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public long getTimeDelta(Date date1, Date date2) {
		long timeDelta = (date1.getTime() - date2.getTime()) / 1000;// 单位是秒
		return timeDelta;
	}

	/**
	 * 根据当前的时间，判断在本周距离下一次揭晓还剩余的时间
	 * 
	 * @param view
	 */
	private void judgenow() {
		// 当前系统的时间
		Date date = new Date();
		Calendar calendar=Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		nowtime = sdf.format(calendar.getTime());
		
		//nowtime = "2014-10-06 09:18:10";
		// 获得当前是周几
		Calendar cal = Calendar.getInstance();
		nowweek = cal.get(Calendar.DAY_OF_WEEK - 1);

		// 获得本周二、周五的日期
		// 获得下周二、周五的日期
		tuesDate = getTuesdayOFWeek() + " 10:00:00";
		friDate = getCurrentFriday() + " 14:00:00";
		nexttuesDate = getNextTuesday() + " 10:00:00";
		nextfriDate = getNextFriday() + " 14:00:00";

		// 当前时间距离周二的秒数
		try {
			tozhouer = getTimeDelta(sdf.parse(tuesDate), sdf.parse(nowtime));
			if (tozhouer <= 0) {// 时间小于0说明现在已经过了周二
				shidian_sj.setVisibility(View.INVISIBLE);
			} else {// 开始计时
				shidian_sj.setVisibility(View.VISIBLE);
				zr_now_timer = new Timer();
				zr_now_timer.schedule(new TimerTask() {
					@Override
					public void run() {

						runOnUiThread(new Runnable() { // UI thread
							@Override
							public void run() {
								tozhouer--;
								if (endNowTimter == 2) {
									tozhouer = -1;
								}
								if (tozhouer < 0) {
									zr_now_timer.cancel();
									return;
									// txtView.setVisibility(View.GONE);
								}
								long hour = (int) (tozhouer / (60 * 60));
								long fen = (tozhouer % (60 * 60)) / 60;
								long miao = ((tozhouer % (60 * 60)) % 60);
								if (hour >= 100) {
									sd_text1.setVisibility(View.VISIBLE);
									sd_text1.setText(hour / 100 + "");
									sd_text2.setText(((hour % 100) / 10) + "");
									sd_text3.setText(((hour % 100) % 10) + "");
									sd_text4.setText(fen / 10 + "");
									sd_text5.setText(fen % 10 + "");
									sd_text6.setText(miao / 10 + "");
									sd_text7.setText(miao % 10 + "");
								} else {
									sd_text1.setVisibility(View.INVISIBLE);
									// sd_text1.setText(hour/100+"");
									sd_text2.setText(((hour) / 10) + "");
									sd_text3.setText(((hour) % 10) + "");
									sd_text4.setText(fen / 10 + "");
									sd_text5.setText(fen % 10 + "");
									sd_text6.setText(miao / 10 + "");
									sd_text7.setText(miao % 10 + "");
								}

							}
						});
					}
				}, 1000, 1000);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 当前时间距离周五的秒数
		try {
			tozhouwu = getTimeDelta(sdf.parse(friDate), sdf.parse(nowtime));
			if (tozhouwu <= 0) {// 时间小于0说明现在已经过了周五
				shidian_sj.setVisibility(View.INVISIBLE);
			} else {// 开始计时
				
				/*MyCount myCount=new MyCount(tozhouwu*1000, 1000);
				myCount.start();*/
				zw_now_timer = new Timer();
				zw_now_timer.schedule(new TimerTask() {
					@Override
					public void run() {

						runOnUiThread(new Runnable() { // UI thread
							@Override
							public void run() {
								tozhouwu--;
								if (endNowTimter == 2) {
									tozhouwu = -1;
								}
								if (tozhouwu < 0) {
									zw_now_timer.cancel();
									// txtView.setVisibility(View.GONE);
									return;
								}
								long hour = (int) (tozhouwu / (60 * 60));
								long fen = (tozhouwu % (60 * 60)) / 60;
								long miao = ((tozhouwu % (60 * 60)) % 60);
								if (hour >= 100) {
									ss_text1.setVisibility(View.VISIBLE);
									ss_text1.setText(hour / 100 + "");
									ss_text2.setText(((hour % 100) / 10) + "");
									ss_text3.setText(((hour % 100) % 10) + "");
									ss_text4.setText(fen / 10 + "");
									ss_text5.setText(fen % 10 + "");
									ss_text6.setText(miao / 10 + "");
									ss_text7.setText(miao % 10 + "");
								} else {
									ss_text1.setVisibility(View.INVISIBLE);
									// sd_text1.setText(hour/100+"");
									ss_text2.setText(((hour) / 10) + "");
									ss_text3.setText(((hour) % 10) + "");
									ss_text4.setText(fen / 10 + "");
									ss_text5.setText(fen % 10 + "");
									ss_text6.setText(miao / 10 + "");
									ss_text7.setText(miao % 10 + "");
								}

							}
						});
					}
				}, 1000, 1000);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 根据当前的时间，判断在下周距离下一次揭晓还剩余的时间
	 * 
	 * @param view
	 */
	private void judgeNext() {
		// 当前系统的时间
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		nowtime = sdf.format(date);
		nowtime = "2014-10-06 09:18:10";
		// 获得当前是周几
		Calendar cal = Calendar.getInstance();
		nowweek = cal.get(Calendar.DAY_OF_WEEK - 1);

		// 获得本周二、周五的日期
		// 获得下周二、周五的日期
		tuesDate = getTuesdayOFWeek() + " 10:00:00";
		friDate = getCurrentFriday() + " 14:00:00";
		nexttuesDate = getNextTuesday() + " 10:00:00";
		nextfriDate = getNextFriday() + " 14:00:00";

		// 当前时间距离周二的秒数
		try {
			nextzhouer = getTimeDelta(sdf.parse(nexttuesDate),
					sdf.parse(nowtime));
			if (nextzhouer <= 0) {// 时间小于0说明现在已经过了周二
				shidian_sj.setVisibility(View.INVISIBLE);
			} else {// 开始计时
				zr_next_timer = new Timer();
				zr_next_timer.schedule(new TimerTask() {
					@Override
					public void run() {

						runOnUiThread(new Runnable() { // UI thread
							@Override
							public void run() {
								nextzhouer--;
								if (endNextTimter == 2) {
									nextzhouer = -1;
								}
								if (nextzhouer < 0) {
									zr_next_timer.cancel();
									// txtView.setVisibility(View.GONE);
									return;
								}

								long hour = (int) (nextzhouer / (60 * 60));
								long fen = (nextzhouer % (60 * 60)) / 60;
								long miao = ((nextzhouer % (60 * 60)) % 60);
								if (hour >= 100) {
									sd_text1.setVisibility(View.VISIBLE);
									sd_text1.setText(hour / 100 + "");
									sd_text2.setText(((hour % 100) / 10) + "");
									sd_text3.setText(((hour % 100) % 10) + "");
									sd_text4.setText(fen / 10 + "");
									sd_text5.setText(fen % 10 + "");
									sd_text6.setText(miao / 10 + "");
									sd_text7.setText(miao % 10 + "");
								} else {
									sd_text1.setVisibility(View.INVISIBLE);
									// sd_text1.setText(hour/100+"");
									sd_text2.setText(((hour) / 10) + "");
									sd_text3.setText(((hour) % 10) + "");
									sd_text4.setText(fen / 10 + "");
									sd_text5.setText(fen % 10 + "");
									sd_text6.setText(miao / 10 + "");
									sd_text7.setText(miao % 10 + "");
								}

							}
						});
					}
				}, 1000, 1000);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 当前时间距离周五的秒数
		try {
			nextzhouwu = getTimeDelta(sdf.parse(nextfriDate),
					sdf.parse(nowtime));
			if (nextzhouwu <= 0) {// 时间小于0说明现在已经过了周五
				shidian_sj.setVisibility(View.INVISIBLE);
			} else {// 开始计时
				zw_next_timer = new Timer();
				zw_next_timer.schedule(new TimerTask() {
					@Override
					public void run() {

						runOnUiThread(new Runnable() { // UI thread
							@Override
							public void run() {
								nextzhouwu--;
								if (endNextTimter == 2) {
									nextzhouwu = -1;
								}
								if (nextzhouwu < 0) {
									zw_next_timer.cancel();
									return;
									// txtView.setVisibility(View.GONE);
								}
								long hour = (int) (nextzhouwu / (60 * 60));
								long fen = (nextzhouwu % (60 * 60)) / 60;
								long miao = ((nextzhouwu % (60 * 60)) % 60);
								if (hour >= 100) {
									ss_text1.setVisibility(View.VISIBLE);
									ss_text1.setText(hour / 100 + "");
									ss_text2.setText(((hour % 100) / 10) + "");
									ss_text3.setText(((hour % 100) % 10) + "");
									ss_text4.setText(fen / 10 + "");
									ss_text5.setText(fen % 10 + "");
									ss_text6.setText(miao / 10 + "");
									ss_text7.setText(miao % 10 + "");
								} else {
									ss_text1.setVisibility(View.INVISIBLE);
									// sd_text1.setText(hour/100+"");
									ss_text2.setText(((hour) / 10) + "");
									ss_text3.setText(((hour) % 10) + "");
									ss_text4.setText(fen / 10 + "");
									ss_text5.setText(fen % 10 + "");
									ss_text6.setText(miao / 10 + "");
									ss_text7.setText(miao % 10 + "");
								}

								if (nextzhouwu < 0) {
									zw_next_timer.cancel();
									// txtView.setVisibility(View.GONE);
								}
							}
						});
					}
				}, 1000, 1000);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@OnClick({ R.id.top_back, R.id.fi1, R.id.fi2, R.id.qianggouing,
			R.id.wangqi, R.id.jinxing_ing, R.id.re7 })
	public void onclick(View view) {
		List<TimeListRecord> timeLists;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		switch (view.getId()) {
		case R.id.jinxing_ing:
			if ("1".equals(jinxing_ing.getTag() + "")) {
				lin7.setVisibility(View.VISIBLE);
				re7.setVisibility(View.VISIBLE);
				jinxing_ing.setTag("2");
			} else if ("2".equals(jinxing_ing.getTag() + "")) {
				lin7.setVisibility(View.GONE);
				re7.setVisibility(View.GONE);
				jinxing_ing.setTag("1");
			}

			break;
		case R.id.re7:

			break;
		case R.id.wangqi:
			timeLists = new ArrayList<TimeListRecord>();
			wangqi.setTextColor(getResources().getColor(R.color.yyrg_shenhong));
			qianggouing.setTextColor(getResources().getColor(
					R.color.yyrg_shenhei));
			for (TimeListRecord timeListRecord : timeListRecords) {
				if (!TextUtils.isEmpty(timeListRecord.getAwardUserid())) {
					timeLists.add(timeListRecord);
				}
			}
			getPopupWindow();
			startPopupWindow(timeLists);
			distancePopup.showAsDropDown(top);
			// goods_list.setAdapter(new MyAdapter(timeLists));
			break;

		case R.id.qianggouing:
			timeLists = new ArrayList<TimeListRecord>();
			qianggouing.setTextColor(getResources().getColor(
					R.color.yyrg_shenhong));
			wangqi.setTextColor(getResources().getColor(R.color.yyrg_shenhei));
			for (TimeListRecord timeListRecord : timeListRecords) {
				if (TextUtils.isEmpty(timeListRecord.getAwardUserid())) {
					timeLists.add(timeListRecord);
				}
			}
			goods_list.setAdapter(new MyAdapter(timeLists));
			break;

		case R.id.top_back:
			finish();
			break;

		case R.id.fi1:
			state = 1;
			thisweek.setTextColor(getResources()
					.getColor(R.color.yyrg_topcolor));
			thisweek.setBackgroundColor(getResources().getColor(R.color.bg));
			nextweek.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			lin_thisweek.setVisibility(View.VISIBLE);
			lin_nextweek.setVisibility(View.INVISIBLE);
			nextweek.setTextColor(getResources().getColor(R.color.bg));
			/*
			 * 显示本周的限时揭晓的商品 判断的依据是没有中奖者，以及时间的区间在当前周内
			 */
			timeLists = new ArrayList<TimeListRecord>();
			for (TimeListRecord timeListRecord : timeListRecords) {
				String annTime = timeListRecord.getAnnTime();
				annTime = annTime.replace("/", "-");
				try {
					long temp = getTimeDelta(sdf.parse(friDate),
							sdf.parse(annTime));
					String nowMonDay = getMondayOFWeek() + " 00:00:00";
					long start = getTimeDelta(sdf.parse(nowMonDay),
							sdf.parse(annTime));
					if (temp >= 0 && start < 0) {
						timeLists.add(timeListRecord);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			goods_list.setAdapter(new MyAdapter(timeLists));
			if (timeLists.size() < 1) {
				Toast.makeText(this, "暂无限时揭晓商品信息", Toast.LENGTH_LONG).show();
			}
			judgenow();
			endNextTimter = 2;
			endNowTimter = 1;
			zr_next_timer.cancel();
			zw_next_timer.cancel();

			break;
		case R.id.fi2:
			state = 2;
			thisweek.setTextColor(getResources().getColor(R.color.bg));
			lin_thisweek.setVisibility(View.INVISIBLE);
			lin_nextweek.setVisibility(View.VISIBLE);
			nextweek.setTextColor(getResources()
					.getColor(R.color.yyrg_topcolor));
			thisweek.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			nextweek.setBackgroundColor(getResources().getColor(R.color.bg));
			/*
			 * 显示下周的限时揭晓的商品 判断的依据是没有中奖者，以及时间的区间在当前周内
			 */
			timeLists = new ArrayList<TimeListRecord>();
			for (TimeListRecord timeListRecord : timeListRecords) {
				String annTime = timeListRecord.getAnnTime();
				annTime = annTime.replace("/", "-");
				try {
					long temp = getTimeDelta(sdf.parse(friDate),
							sdf.parse(annTime));
					if (temp <= 0) {
						timeLists.add(timeListRecord);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			goods_list.setAdapter(new MyAdapter(timeLists));
			if (timeLists.size() < 1) {
				Toast.makeText(this, "暂无限时揭晓商品信息", Toast.LENGTH_LONG).show();
			}
			judgeNext();
			endNowTimter = 2;
			endNextTimter = 1;
			zr_now_timer.cancel();
			zw_now_timer.cancel();

			break;
		}
	}

	private void getGoodProductTimeListRecord() {
		Util.asynTask(this, "加载中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<TimeListRecord> list = new ArrayList<TimeListRecord>();
					list = ((HashMap<Integer, List<TimeListRecord>>) runData)
							.get(1);
					if (list.size() > 0) {
						timeListRecords = list;
						if (state == 1) {
							List<TimeListRecord> timeLists = new ArrayList<TimeListRecord>();
							for (TimeListRecord timeListRecord : list) {
								if (TextUtils.isEmpty(timeListRecord
										.getAwardUserid())) {
									timeLists.add(timeListRecord);
								}
							}
							goods_list.setAdapter(new MyAdapter(timeLists));
						} else if (state == 2) {
							List<TimeListRecord> timeLists = new ArrayList<TimeListRecord>();
							for (TimeListRecord timeListRecord : list) {
								if (!TextUtils.isEmpty(timeListRecord
										.getAwardUserid())) {
									timeLists.add(timeListRecord);
								}
							}
							goods_list.setAdapter(new MyAdapter(timeLists));
						}
					}
				} else {
					Toast.makeText(YYRGLimitTime.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress,
						Web.getGoodProductTimeListRecord, "");
				List<TimeListRecord> list = web.getList(TimeListRecord.class);
				HashMap<Integer, List<TimeListRecord>> map = new HashMap<Integer, List<TimeListRecord>>();
				map.put(1, list);
				return map;
			}
		});
	}

	private void setImage(final ImageView logo, String href, final int width,
			final int height) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				arg2 = Util.zoomBitmap(arg2, width, height);
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}

	class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<TimeListRecord> list;

		MyAdapter(List<TimeListRecord> list) {
			this.list = list;
			inflater = LayoutInflater.from(YYRGLimitTime.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int postion, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = inflater.inflate(R.layout.yyrg_limit_item, null);
			}
			ImageView img_goods = (ImageView) view.findViewById(R.id.img_goods);
			TextView goods_name = (TextView) view.findViewById(R.id.goods_name);
			TextView goods_price = (TextView) view
					.findViewById(R.id.goods_price);
			MyProgressView pro_goods = (MyProgressView) view
					.findViewById(R.id.pro_goods);
			TextView yicanyu = (TextView) view.findViewById(R.id.yicanyu);
			TextView zongxu = (TextView) view.findViewById(R.id.zongxu);
			TextView shengyu = (TextView) view.findViewById(R.id.shengyu);
			final TextView to_buy = (TextView) view.findViewById(R.id.to_buy);
			setImage(img_goods, list.get(postion).getPhotoThumb(),
					width * 2 / 5, width * 2 / 5);
			goods_name.setText(list.get(postion).getProductName());
			DecimalFormat df = new DecimalFormat("#.00");
			goods_price.setText(df.format(Double.parseDouble(list.get(postion)
					.getPrice())));
			yicanyu.setText(list.get(postion).getPersonTimes());
			zongxu.setText(list.get(postion).getTotalPersonTimes());
			int sum=Integer.parseInt(list.get(postion)
					.getTotalPersonTimes())
					- Integer.parseInt(list.get(postion).getPersonTimes());
			shengyu.setText(sum + "");
			pro_goods.setMaxCount(Integer.parseInt(list.get(postion)
					.getTotalPersonTimes()) * 1.00f);
			pro_goods.setCurrentCount(Integer.parseInt(list.get(postion)
					.getPersonTimes()) * 1.00f);
			
			if (sum==0) {
				to_buy.setText("已满员");
			}else {
				to_buy.setText("立即参与");
			}
			
			
			
			
			
			if (!TextUtils.isEmpty(list.get(postion).getAwardUserid())) {
				to_buy.setBackgroundColor(getResources().getColor(
						R.color.yyrg_no_buy));
				to_buy.setTag("1");
				
			} else {
				to_buy.setBackgroundColor(getResources().getColor(
						R.color.yyrg_chengse));
				to_buy.setTag("2");
			}
			to_buy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if ("1".equals(to_buy.getTag() + "")) {
						/*Intent intent=new Intent(YYRGLimitTime.this, YYRGHistoryGoodsMessage.class);
						intent.putExtra("goodsid", list.get(postion).getYppid());
						startActivity(intent);*/
					} else if ("2".equals(to_buy.getTag() + "")) {
						Intent intent=new Intent(YYRGLimitTime.this, YYRGGoodsMessage.class);
						intent.putExtra("goodsid", list.get(postion).getYppid());
						intent.putExtra("ypid", list.get(postion).getYpid());
						startActivity(intent);
					}
				}
			});
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if ("1".equals(to_buy.getTag() + "")) {
						Intent intent=new Intent(YYRGLimitTime.this, YYRGHistoryGoodsMessage.class);
						intent.putExtra("goodsid", list.get(postion).getYppid());
						startActivity(intent);
					} else if ("2".equals(to_buy.getTag() + "")) {
						Intent intent=new Intent(YYRGLimitTime.this, YYRGGoodsMessage.class);
						intent.putExtra("goodsid", list.get(postion).getYppid());
						intent.putExtra("ypid", list.get(postion).getYpid());
						startActivity(intent);
					}
				}
			});
			return view;
		}

	}

	// 获得本周二的日期
	private String getTuesdayOFWeek() {
		int weeks = 0;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 1);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		preMonday.replace("年", "-");
		preMonday.replace("月", "-");
		preMonday.replace("日", "");
		return preMonday;
	}

	// 获得本周1的日期
	private String getMondayOFWeek() {
		int weeks = 0;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		preMonday=preMonday.replace("年", "-");
		preMonday=preMonday.replace("月", "-");
		preMonday=preMonday.replace("日", "");
		return preMonday;
	}

	// 获得当前日期与本周日相差的天数
	private int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一
		/*
		 * 作为第一 天所以这里减1
		 */
		if (dayOfWeek == 1) {
			return 0;
		} else {
			return 1 - dayOfWeek;
		}
	}

	// 获得本周星期日的日期
	private String getCurrentSunday() {
		int weeks = 0;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		preMonday=preMonday.replace("年", "-");
		preMonday=preMonday.replace("月", "-");
		preMonday=preMonday.replace("日", "");
		return preMonday;
	}

	// 获得本周星期五的日期
	private String getCurrentFriday() {
		int weeks = 0;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 4);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		preMonday=preMonday.replace("年", "-");
		preMonday=preMonday.replace("月", "-");
		preMonday=preMonday.replace("日", "");
		return preMonday;
	}

	// 获得下周星期二的日期
	public String getNextTuesday() {
		int weeks = 0;
		weeks++;
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 8);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		preMonday=preMonday.replace("年", "-");
		preMonday=preMonday.replace("月", "-");
		preMonday=preMonday.replace("日", "");
		return preMonday;
	}

	// 获得下周星期五的日期
	public String getNextFriday() {
		int mondayPlus = this.getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 + 4);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		preMonday=preMonday.replace("年", "-");
		preMonday=preMonday.replace("月", "-");
		preMonday=preMonday.replace("日", "");
		return preMonday;
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */

	private void startPopupWindow(List<TimeListRecord> list) {
		View pview = getLayoutInflater().inflate(R.layout.yyrg_passed_events,
				null, false);
		GridView goods_list1 = (GridView) pview.findViewById(R.id.goods_list1);
		LinearLayout lin3 = (LinearLayout) pview.findViewById(R.id.lin3);
		TextView top_back1 = (TextView) pview.findViewById(R.id.top_back1);
		goods_list1.setAdapter(new MyAdapter(list));
		top_back1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				qianggouing.setTextColor(getResources().getColor(
						R.color.yyrg_shenhong));
				wangqi.setTextColor(getResources().getColor(R.color.yyrg_shenhei));
				distancePopup.dismiss();
			}
		});
		lin3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				distancePopup.dismiss();
			}
		});
		initpoputwindow(pview);
	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}

	/**
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimation);
	}
	  /*定义一个倒计时的内部类*/  
    class MyCount extends CountDownTimer {     
        public MyCount(long millisInFuture, long countDownInterval) {     
            super(millisInFuture, countDownInterval);     
        }     
        @Override     
        public void onFinish() {     
          //  tv.setText("finish");        
        }     
        @Override     
        public void onTick(long millisUntilFinished) { 
        	long thistime=millisUntilFinished/1000;
        	long hour = (int) (thistime / (60 * 60));
			long fen = (thistime % (60 * 60)) / 60;
			long miao = ((thistime % (60 * 60)) % 60);
			if (hour >= 100) {
				ss_text1.setVisibility(View.VISIBLE);
				ss_text1.setText(hour / 100 + "");
				ss_text2.setText(((hour % 100) / 10) + "");
				ss_text3.setText(((hour % 100) % 10) + "");
				ss_text4.setText(fen / 10 + "");
				ss_text5.setText(fen % 10 + "");
				ss_text6.setText(miao / 10 + "");
				ss_text7.setText(miao % 10 + "");
			} else {
				ss_text1.setVisibility(View.INVISIBLE);
				// sd_text1.setText(hour/100+"");
				ss_text2.setText(((hour) / 10) + "");
				ss_text3.setText(((hour) % 10) + "");
				ss_text4.setText(fen / 10 + "");
				ss_text5.setText(fen % 10 + "");
				ss_text6.setText(miao / 10 + "");
				ss_text7.setText(miao % 10 + "");
			}

//            tv.setText("请等待30秒(" + millisUntilFinished / 1000 + ")...");     
//            Toast.makeText(NewActivity.this, millisUntilFinished / 1000 + "", Toast.LENGTH_LONG).show();//toast有显示时间延迟       
        }    
    }  

}
