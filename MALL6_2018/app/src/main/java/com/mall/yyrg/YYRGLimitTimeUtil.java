package com.mall.yyrg;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.util.WHD;
import com.mall.view.R;
import com.mall.yyrg.adapter.TimeListRecord;

public class YYRGLimitTimeUtil {
	public static boolean isRunThread = true;

	public static List<TimeListRecord> allList=new ArrayList<TimeListRecord>();

	public static void addLimitTimeGroup(final Context context,
			LinearLayout ll,  boolean isNext) {
		boolean isStart = false;
		
		final String[] strs = { "周二10：00", "周五14：00" };

		for (int i = 0; i < strs.length; i++) {
			View v = LayoutInflater.from(context).inflate(
					R.layout.yyrg_limit_time_group, null);

			View ll_yyrg_group = v.findViewById(R.id.ll_limit_group);
			final View re7 = v.findViewById(R.id.re7);
			final View ll_limit_time = v.findViewById(R.id.ll_limit_time);
			final TextView tv_limit_time = (TextView) v
					.findViewById(R.id.tv_limit_time);
			final TextView shisi_text = (TextView) v
					.findViewById(R.id.shisi_text);
			final TextView tv_isfinish = (TextView) v
					.findViewById(R.id.tv_isfinish);
			final TextView ss_text1 = (TextView) v.findViewById(R.id.ss_text1);
			final TextView ss_text2 = (TextView) v.findViewById(R.id.ss_text2);
			final TextView ss_text3 = (TextView) v.findViewById(R.id.ss_text3);
			final TextView ss_text4 = (TextView) v.findViewById(R.id.ss_text4);
			final TextView ss_text5 = (TextView) v.findViewById(R.id.ss_text5);
			final TextView ss_text6 = (TextView) v.findViewById(R.id.ss_text6);
			final TextView ss_text7 = (TextView) v.findViewById(R.id.ss_text7);
			final CustomNoScrollGridView goods_list = (CustomNoScrollGridView) v
					.findViewById(R.id.goods_list);
			goods_list .setNumColumns(2);
			if (i % 2 == 0) {
				ll_yyrg_group.setBackgroundResource(R.drawable.yyrg_shijian_2);
			} else {
				ll_yyrg_group.setBackgroundResource(R.drawable.yyrg_shijian);
			}
			tv_limit_time.setText(strs[i]);
			Calendar c1 = Calendar.getInstance();

			Date date = null;
			switch (i) {
			case 0:
				if (isNext) {
					date = getNextTuesday().getTime();
				} else {
					date = getTuesdayOFWeek().getTime();
				}
				date.setHours(10);
				break;
			case 1:
				if (isNext) {
					date = getNextFriday().getTime();
				} else {
					date = getFridayOFWeek().getTime();
				}
				date.setHours(14);
				break;
			default:
				break;
			}
			
			date.setMinutes(0);
			date.setSeconds(0);

			if (date.getTime() > c1.getTimeInMillis()) {
				ll_limit_time.setVisibility(View.VISIBLE);
				isStart = true;
			} else {
				ll_limit_time.setVisibility(View.GONE);
				shisi_text.setText("已揭晓");
				tv_isfinish.setText("已  揭  晓");
				isStart = false;
			}

			final long longtime = date.getTime();

			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (200 == msg.what) {
						Calendar c1 = Calendar.getInstance();
						long long1 = longtime - c1.getTimeInMillis();
						if (long1 < 0) {
							ll_limit_time.setVisibility(View.GONE);
							shisi_text.setText("已揭晓");
							tv_isfinish.setText("已  揭  晓");
						} else {
							long1 = long1 / 1000;
							long hour = (int) (long1 / (60 * 60));
							long fen = (long1 % (60 * 60)) / 60;
							long miao = ((long1 % (60 * 60)) % 60);
							if (hour/10>=10) {
								ss_text1.setVisibility(View.VISIBLE);
								ss_text1.setText(hour/100+"");
								ss_text2.setText((hour %100)/10 + "");
							}else {
								ss_text1.setVisibility(View.GONE);
								ss_text2.setText((hour)/10 + "");
							}
							ss_text3.setText(((hour) % 10) + "");
							ss_text4.setText(fen / 10 + "");
							ss_text5.setText(fen % 10 + "");
							ss_text6.setText(miao / 10 + "");
							ss_text7.setText(miao % 10 + "");
						}

					}
				}
			};

			if (isStart) {

				new Thread() {

					public void run() {
						while (isRunThread) {
							try {
								sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Message msg = new Message();
							msg.what = 200;
							handler.sendMessage(msg);
						}
					};
				}.start();
			}
			final ArrayList<TimeListRecord> list = new ArrayList<TimeListRecord>();
			for (TimeListRecord info : allList) {
				SimpleDateFormat formatDate = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");

				System.out.println(info.getAnnTime() + "this is my time");
				String format = formatDate.format(date);
				System.out.println(format);
				try {
					Date infodate = formatDate.parse(info.getAnnTime());
					if (date.getDay() == infodate.getDay()
							&& date.getHours() == infodate.getHours()&&date.getDate()==infodate.getDate()) {
						list.add(info);
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			MyAdapter adapter = new MyAdapter(context, list);
			goods_list.setAdapter(adapter);

			re7.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (list.size() > 0) {
						if (re7.getTag() == null) {
							goods_list.setVisibility(View.VISIBLE);
							re7.setTag(" ");

						} else {
							goods_list.setVisibility(View.GONE);
							re7.setTag(null);
						}
					} else {
						Util.show("本场暂无商品...", context);
						goods_list.setVisibility(View.GONE);
					}

				}
			});
			ll.addView(v);

		}

	}

	// 获得本周二的日期
	private static Calendar getTuesdayOFWeek() {

		int mondayPlus = getMondayPlus();
		Calendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 1);

		return currentDate;
	}

	// 获得本周星期五的日期
	private static Calendar getFridayOFWeek() {

		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 4);
		return currentDate;
	}
	// 获得下周星期二的日期
	private static Calendar getNextTuesday() {

		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 8);

		return currentDate;
	}

	// 获得下周星期五的日期
	private static Calendar getNextFriday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 + 4);

		return currentDate;
	}

	// 获得当前日期与本周日相差的天数
	private static int getMondayPlus() {
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

	public static class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<TimeListRecord> list;
		private Context context;
		

		MyAdapter(Context context, List<TimeListRecord> list) {
			this.list = list;
			this.context = context;
			inflater = LayoutInflater.from(context);
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
			WHD whd = Util.getScreenSize(context);
			int width = whd.getWidth();
			setImage(context, img_goods, list.get(postion).getPhotoThumb(),
					width * 2 / 5, width * 2 / 5);
			goods_name.setText(list.get(postion).getProductName());
			DecimalFormat df = new DecimalFormat("#.00");
			goods_price.setText(df.format(Double.parseDouble(list.get(postion)
					.getPrice())));
			yicanyu.setText(list.get(postion).getPersonTimes());
			zongxu.setText(list.get(postion).getTotalPersonTimes());
			shengyu.setText(Integer.parseInt(list.get(postion)
					.getTotalPersonTimes())
					- Integer.parseInt(list.get(postion).getPersonTimes()) + "");
			pro_goods.setMaxCount(Integer.parseInt(list.get(postion)
					.getTotalPersonTimes()) * 1.00f);
			pro_goods.setCurrentCount(Integer.parseInt(list.get(postion)
					.getPersonTimes()) * 1.00f);
			if (!TextUtils.isEmpty(list.get(postion).getAwardUserid())) {
				to_buy.setBackgroundColor(context.getResources().getColor(
						R.color.yyrg_no_buy));
				to_buy.setTag("1");
			} else {
				to_buy.setBackgroundColor(context.getResources().getColor(
						R.color.yyrg_chengse));
				to_buy.setTag("2");
			}
			to_buy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if ("1".equals(to_buy.getTag() + "")) {
					} else if ("2".equals(to_buy.getTag() + "")) {
						Intent intent = new Intent(context,
								YYRGGoodsMessage.class);
						intent.putExtra("goodsid", list.get(postion).getYppid());
						intent.putExtra("ypid", list.get(postion).getYpid());
						intent.putExtra("time",list.get(postion).getAnnTime() );
						context.startActivity(intent);
					}
				}
			});
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if ("1".equals(to_buy.getTag() + "")) {
						Intent intent = new Intent(context,
								YYRGHistoryGoodsMessage.class);
						intent.putExtra("goodsid", list.get(postion).getYppid());
						context.startActivity(intent);
					} else if ("2".equals(to_buy.getTag() + "")) {
						Intent intent = new Intent(context,
								YYRGGoodsMessage.class);
						intent.putExtra("goodsid", list.get(postion).getYppid());
						intent.putExtra("ypid", list.get(postion).getYpid());
						intent.putExtra("time",list.get(postion).getAnnTime() );
						context.startActivity(intent);
					}
				}
			});
			return view;
		}

	}

	private static void setImage(Context context, final ImageView logo,
			String href, final int width, final int height) {
		BitmapUtils bmUtil = new BitmapUtils(context);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
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

	public static void getGoodProductTimeListRecord(final Context context,
			final LinearLayout ll, final boolean isNext) {
		Util.asynTask(context, "加载中…", new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				List<TimeListRecord> listAll = new ArrayList<TimeListRecord>();
				HashMap<Integer, List<TimeListRecord>> map = (HashMap<Integer, List<TimeListRecord>>) runData;
				if (map != null) {
					List<TimeListRecord> list = map.get(1);
					
					if (list != null && list.size() > 0) {
						allList.clear();
						allList.addAll(list);
						
					}
				}

				addLimitTimeGroup(context, ll, isNext);
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

}
