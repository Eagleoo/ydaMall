package com.mall.serving.resturant;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.util.Util;
import com.mall.view.R;


/*
 * 日历控件
 */
public class MyDateWidget extends Activity {
	private LinearLayout container;
	private int currentYear, currentMonth, currentDay;
	private String[] week = new String[] { "周日", "周一", "周二", "周三", "周四", "周五",
			"周六" };
	private int currentMonthCurrentDays, nextMonthCurrentDays,
			nextTwoMonthCurrentDays;
	private LayoutInflater inflater;
	private TextView in_out;
	private SharedPreferences sp;
	private String in;
	private String out;
	private String index = "0";
	private LinearLayout dialog_layout;
	private TextView now_time;
	private Button cancel, submit;
	private String weekin = "", weekout = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydatewidget);
		init();
	}

	private void init() {
		container = (LinearLayout) this.findViewById(R.id.date_container);
		Calendar ca = Calendar.getInstance();
		currentYear = ca.get(Calendar.YEAR);
		currentMonth = ca.get(Calendar.MONTH) + 1;
		currentDay = ca.get(Calendar.DAY_OF_MONTH);
		int days = 0;
		currentMonthCurrentDays = Util.getMonthLastDay(currentYear,
				currentMonth);
		nextMonthCurrentDays = Util.getMonthLastDay(currentYear,
				currentMonth + 1);
		nextTwoMonthCurrentDays = Util.getMonthLastDay(currentYear,
				currentMonth + 2);
		days += currentMonthCurrentDays;
		days += nextMonthCurrentDays;
		days += nextTwoMonthCurrentDays;
		inflater = LayoutInflater.from(this);
		sp = this.getSharedPreferences("mydatewidget", 0);
		if (sp.getString("in", "0") != null && sp.getString("out", "0") != null) {
			in = sp.getString("in", "0");
			out = sp.getString("out", "0");
		} else {
			in = currentMonth + "." + currentDay;
			out = currentMonth + "." + (currentDay + 1);
			index = "0";
		}
		cancel = (Button) this.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sp.edit().putString("index", "0");
				MyDateWidget.this.finish();
			}
		});
		submit = (Button) this.findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sp.edit().putString("index", "0").commit();
				Intent i = new Intent(MyDateWidget.this, ResturantIndex.class);
				System.out.println("in===="+sp.getString("in", "")+"     out==="+sp.getString("out", ""));
				i.putExtra("in", currentYear + "-" + in.replace(".", "-"));
				i.putExtra("out", currentYear + "-" + out.replace(".", "-"));
				i.putExtra("inweek", weekin);
				i.putExtra("outweek", weekout);
				setResult(200, i);
				MyDateWidget.this.finish();
			}
		});
		dialog_layout = (LinearLayout) this.findViewById(R.id.dialog_layout);
		dialog_layout.getBackground().setAlpha(150);
		now_time = (TextView) this.findViewById(R.id.now_time);
		now_time.setText("今年" + currentMonth + "月");
		in_out = (TextView) this.findViewById(R.id.in_out);
//		in_out.setText("入住" + in);
		if (currentDay > 28) {
			initMonthView(days + 21, currentMonth, currentDay);
		} else {
			initMonthView(days + 10, currentMonth, currentDay);
		}
	}
	private void clearDateWidget() {
		int end = container.getChildCount();
		for (int l = 0; l < end; l++) {
			LinearLayout linear = (LinearLayout) container.getChildAt(l);
			int c = linear.getChildCount();
			for (int t = 0; t < c; t++) {
				TextView date = (TextView) linear.getChildAt(t);
				if (date.getTag(-7) != null) {
					date.setTag(-7, null);
					date.setBackgroundResource(R.drawable.table_textview);
					date.setTextColor(Color.BLACK);
					date.setText(date.getTag(-8) + "");
				}
			}
		}
	}
	private void initMonthView(int maxDays, int currentMonth, int currentDay) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		int currentday = now.get(Calendar.DAY_OF_MONTH);
		now.set(Calendar.DATE, 1);// 从第一天开始
		int rows = maxDays / 7;
		if (maxDays % 7 != 0) {
			rows += 1;
		}
		int day = 0;
		boolean max = false;
		int dayofweek=now.get(Calendar.DAY_OF_WEEK);
		for (int i = 0; i < rows; i++) {
			LinearLayout l = (LinearLayout) inflater.inflate(R.layout.date_layout_item, null);
			for (int col = 0; col < 7; col++) {
				TextView t = (TextView) l.getChildAt(col);
				Calendar newDate = Calendar.getInstance();
				newDate.setTimeInMillis(System.currentTimeMillis());
				newDate.set(Calendar.DATE, 1);// 从当前月的第一天开始
				newDate.add(Calendar.DAY_OF_MONTH, day);
				if((col+(i*7))<(dayofweek-1))
					continue;
				day++;
				int m = newDate.get(Calendar.MONTH) + 1;
				int d = newDate.get(Calendar.DAY_OF_MONTH);
				if (currentMonth == (newDate.get(Calendar.MONTH) + 1)) {// 如果是本月
					// 如果是当月的前几天
					if (newDate.get(Calendar.DAY_OF_MONTH) < currentDay) {
						t.setBackgroundResource(R.drawable.date_textview);
						t.setOnClickListener(null);
						t.setTextColor(Color.WHITE);
						if (newDate.get(Calendar.DAY_OF_MONTH) == 1) {
							t.setText((newDate.get(Calendar.MONTH) + 1) + "月");
							t.setTag(m + "."+ newDate.get(Calendar.DAY_OF_MONTH));
							t.setTag(-8, t.getText().toString());

						} else {
							t.setText(newDate.get(Calendar.DAY_OF_MONTH) + "");
							t.setTag(-8, t.getText().toString());
						}
					} else if (currentMonth == (newDate.get(Calendar.MONTH) + 1)
							&& currentDay == newDate.get(Calendar.DAY_OF_MONTH)
							&& !(currentMonth + "." + newDate.get(Calendar.DAY_OF_MONTH)).equals(in)) {
						// 如果是今天
						t.setBackgroundResource(R.drawable.resturant_button_selector);
						t.setTextColor(Color.WHITE);
						t.setText(newDate.get(Calendar.DAY_OF_MONTH) + "  今天");
						t.setTag(m + "." + newDate.get(Calendar.DAY_OF_MONTH));
						t.setTag(-7, "today");
						t.setTag(-8, t.getText().toString());
						t.setOnClickListener(new DateOnClickListener(t.getTag()
								.toString(), week[col]));
					} else {
						t.setText(newDate.get(Calendar.DAY_OF_MONTH) + "");
						t.setBackgroundResource(R.drawable.table_textview);
						t.setTextColor(Color.BLACK);
						t.setTag(m + "." + newDate.get(Calendar.DAY_OF_MONTH));
						t.setOnClickListener(new DateOnClickListener(t.getTag()
								.toString(), week[col]));
					}
					// month++;
				} else {// 不是本月
					// 如果是每个月第一天
					if (newDate.get(Calendar.DAY_OF_MONTH) == 1) {
						t.setBackgroundResource(R.drawable.table_textview);
						t.setTextColor(Color.BLACK);
						t.setText((newDate.get(Calendar.MONTH) + 1) + "月");
						t.setTag(m + "." + newDate.get(Calendar.DAY_OF_MONTH));
						t.setTag(-8, t.getText().toString());
						t.setOnClickListener(new DateOnClickListener(t.getTag()
								.toString(), week[col]));
						continue;
					} else {
						int dayn = newDate.get(Calendar.DAY_OF_MONTH);
						t.setBackgroundResource(R.drawable.table_textview);
						t.setTextColor(Color.BLACK);
						t.setText(dayn + "");
						t.setTag(m + "." + dayn);
						t.setOnClickListener(new DateOnClickListener(t.getTag()
								.toString(), week[col]));
					}
				}
				t.setTag(-8, t.getText().toString());
				if (day > maxDays) {
					max = true;
					break;
				}
			}
			if (max) {
				break;
			}
			container.addView(l);
		}
		if(currentday>7&&currentday<=14){
			container.removeViewAt(0);
		}
		if (currentday > 14 && currentday <= 21) {
			container.removeViewAt(0);
			container.removeViewAt(0);
		}
		if (currentday >21) {
			container.removeViewAt(0);
			container.removeViewAt(0);
			container.removeViewAt(0);
		}
	}
	public class DateOnClickListener implements OnClickListener {
		private String week;
		public DateOnClickListener(String tag, String week) {
			this.week = week;
		}
		@Override
		public void onClick(View arg0) {
			TextView tv = (TextView) arg0;
			index = sp.getString("index", "0");
			if (index.equals("0")) {
				clearDateWidget();
				tv.setBackgroundResource(R.drawable.resturant_button_selector);
				tv.setTextColor(Color.WHITE);
				tv.setText("入住");
				index = "1";
				weekin = week;
				tv.setTag(-7, "in");
				in = tv.getTag().toString();
				sp.edit().putString("in", tv.getTag().toString()).commit();
				sp.edit().putString("index", index).commit();
				sp.edit().putString("inweek", week).commit();
				Toast.makeText(MyDateWidget.this, "请选择离店日期", 0).show();
			} else if (index.equals("1")) {
				weekout = week;
				String inx = sp.getString("in", "0");
				out = tv.getTag().toString();
				int monthin = Integer.parseInt(inx.substring(0,inx.indexOf(".")));
				int monthout = Integer.parseInt(out.substring(0,out.indexOf(".")));
				int dayin = Integer.parseInt(inx.substring(inx.indexOf(".") + 1));
				int dayout = Integer.parseInt(out.substring(out.indexOf(".") + 1));
				index = "0";
				tv.setTag(-7, "out");
				
				//计算入住时间
				int days = 0;
				if (monthin != monthout) {
					days = Util.getMonthLastDay(currentYear, monthin)- dayin + dayout;
				} else {
					days = dayout - dayin;
				}
				Calendar cl=Calendar.getInstance();
				cl.setTimeInMillis(System.currentTimeMillis());
				cl.set(Calendar.MONTH, monthin-1);
				cl.set(Calendar.DAY_OF_MONTH, dayin);
				cl.add(Calendar.DAY_OF_MONTH, 2);
				if (monthin > monthout) {
					submit.setVisibility(View.GONE);
					sp.edit().putString("index", index).commit();
					sp.edit().putString("out", (cl.get(Calendar.MONTH)+1)+"."+cl.get(Calendar.DAY_OF_MONTH)).commit();
					Toast.makeText(MyDateWidget.this, "退房日期不能小于入住日期", Toast.LENGTH_LONG).show();
					return;
				} else  if(monthin==monthout){
					if (dayin >= dayout) {
						submit.setVisibility(View.INVISIBLE);
						in_out.setText("");
						sp.edit().putString("index", index).commit();
						sp.edit().putString("out", (cl.get(Calendar.MONTH)+1)+"."+cl.get(Calendar.DAY_OF_MONTH)).commit();
						Toast.makeText(MyDateWidget.this, "退房日期不能小于入住日期", Toast.LENGTH_LONG).show();
						return;
					}else{ 
						submit.setVisibility(View.VISIBLE);
						in_out.setText("");
						in_out.setText("入住" + inx + "共" + days + "晚");
						sp.edit().putString("index", "0").commit();
						sp.edit().putString("out", tv.getTag().toString()).commit();
						sp.edit().putString("index", index).commit();
						sp.edit().putString("outweek", week).commit();
					}
				}else{
					if (days > 25) {
						submit.setVisibility(View.INVISIBLE);
						sp.edit().putString("index", index).commit();
						sp.edit().putString("out", (cl.get(Calendar.MONTH)+1)+"."+cl.get(Calendar.DAY_OF_MONTH)).commit();
						Toast.makeText(MyDateWidget.this, "最大支持预订25晚",Toast.LENGTH_LONG).show();
						return;
					} else {
						submit.setVisibility(View.VISIBLE);
						in_out.setText("");
						in_out.setText("入住" + inx + "共" + days + "晚");
						sp.edit().putString("index", "0").commit();
						sp.edit().putString("out", tv.getTag().toString()).commit();
						sp.edit().putString("index", index).commit();
						sp.edit().putString("outweek", week).commit();
					}
				}
				tv.setBackgroundResource(R.drawable.resturant_button_selector);
				tv.setTextColor(Color.WHITE);
				tv.setText("离店");
			}
		}
	}
	@Override
	protected void onStop() {
		sp.edit().putString("index", "0");
		super.onStop();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			MyDateWidget.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}