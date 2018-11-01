package com.mall.serving.orderplane;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.R;
/*
 * 日历控件
 */
public class PlaneDateWidget extends Activity {
	private LinearLayout container;
	private int currentYear, currentMonth, currentDay;
	private String dayOfWeek;
	private String[] week = new String[] { "周日", "周一", "周二", "周三", "周四", "周五",
			"周六" };
	private int currentMonthCurrentDays, nextMonthCurrentDays,
			nextTwoMonthCurrentDays;
	private int _40px;  
	private LayoutInflater inflater;
	private TextView in_out;
	private SharedPreferences sp;
	private String in;
	private String out;
	private String index = "0";
	private int roomdays;
	private LinearLayout dialog_layout;
	private TextView now_time;
	private Button cancel,submit;
	private String weekin="",weekout="";
	private String title="";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mydatewidget);
		getIntentData();
		init();
	}
	private void init() {
		container = (LinearLayout) this.findViewById(R.id.date_container);
		Calendar ca = Calendar.getInstance();
		currentYear = ca.get(Calendar.YEAR);
		currentMonth = ca.get(Calendar.MONTH) + 1;
		currentDay = ca.get(Calendar.DAY_OF_MONTH);
		int weekindex = currentDay - 1;
		int days = 0;
		currentMonthCurrentDays = Util.getMonthLastDay(currentYear,currentMonth);
		nextMonthCurrentDays = Util.getMonthLastDay(currentYear,currentMonth + 1);
		nextTwoMonthCurrentDays = Util.getMonthLastDay(currentYear,currentMonth + 2);
		days += currentMonthCurrentDays;
		days += nextMonthCurrentDays;
		days += nextTwoMonthCurrentDays;
		_40px = Util.dpToPx(this, 40);
		inflater = LayoutInflater.from(this);
		sp = this.getSharedPreferences("PlaneDateWidget", 0);
		if (sp.getString("in", "0") != null && sp.getString("out", "0") != null) {
			in = sp.getString("in", "0");
			out = sp.getString("out", "0");
		}else{
			in=currentMonth+"."+currentDay;
			out=currentMonth+"."+(currentDay+1);
			index="0";
		}
		cancel=(Button) this.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PlaneDateWidget.this.finish();
			}
		});
		submit=(Button) this.findViewById(R.id.submit);
		submit.setVisibility(View.GONE);
		dialog_layout=(LinearLayout) this.findViewById(R.id.dialog_layout);
		in_out=(TextView) this.findViewById(R.id.in_out);
		in_out.setVisibility(View.GONE);
		now_time=(TextView) this.findViewById(R.id.now_time);
		now_time.setText("今年"+currentMonth+"月");
		initMonthView(days+21, currentMonth, currentDay);
	}
   private void getIntentData(){
	   title=this.getIntent().getStringExtra("title");
   }
	private void clearDateWidget(){
		int end = container.getChildCount();
		for(int l=0 ; l < end;l++){
			LinearLayout linear = (LinearLayout)container.getChildAt(l);
			int c = linear.getChildCount();
			for(int t = 0 ; t < c;t++){
				TextView date = (TextView)linear.getChildAt(t);
				if(date.getTag(-7)!=null){
					date.setTag(-7, null);
					date.setBackgroundResource(R.drawable.table_textview);
					date.setTextColor(Color.BLACK);
					date.setText(date.getTag(-8)+"");
				}
			}
		}
	}
	private void initMonthView(int maxDays, int currentMonth, int currentDay) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());
		int currentday=now.get(Calendar.DAY_OF_MONTH);
		now.set(Calendar.DATE, 1);// 从第一天开始
		int rows = maxDays / 7;
		if (maxDays % 7 != 0) {
			rows += 1;
		}
		int day = 0;
		int month = currentMonth;
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
				t.setTag(-9,newDate.get(Calendar.YEAR));
				if (currentMonth == (newDate.get(Calendar.MONTH) + 1)) {//如果是本月
					// 如果是当月的前几天
					if (newDate.get(Calendar.DAY_OF_MONTH) < currentDay) {
						t.setBackgroundResource(R.drawable.date_textview);
						t.setOnClickListener(null);
						t.setTextColor(Color.WHITE);
						if (newDate.get(Calendar.DAY_OF_MONTH) == 1) {
							t.setText((newDate.get(Calendar.MONTH) + 1) + "月");
							t.setTag(m + "." + newDate.get(Calendar.DAY_OF_MONTH));
							t.setTag(-8,t.getText().toString());
						} else {
							t.setText(newDate.get(Calendar.DAY_OF_MONTH) + "");
							t.setTag(-8,t.getText().toString());
						}
					} else if (currentMonth == (newDate.get(Calendar.MONTH) + 1)&&currentDay==newDate.get(Calendar.DAY_OF_MONTH)&&!(currentMonth+"."+newDate.get(Calendar.DAY_OF_MONTH)).equals(in)) {
						//如果是今天
						t.setBackgroundResource(R.drawable.resturant_button_selector);
						t.setTextColor(Color.WHITE);
						t.setText(newDate.get(Calendar.DAY_OF_MONTH)+"  今天");
						t.setTag(m+"."+newDate.get(Calendar.DAY_OF_MONTH));
						t.setTag(-7,"today");
						t.setTag(-8,t.getText().toString());
						t.setOnClickListener(new DateOnClickListener(t.getTag().toString(), week[col]));
					} else {
						t.setText(newDate.get(Calendar.DAY_OF_MONTH)+"");
						t.setBackgroundResource(R.drawable.table_textview);
						t.setTextColor(Color.BLACK);
						t.setTag(m + "." + newDate.get(Calendar.DAY_OF_MONTH));
						t.setOnClickListener(new DateOnClickListener(t.getTag().toString(), week[col]));
					}
					//month++;
				} else {//不是本月
					//如果是每个月第一天
					if (newDate.get(Calendar.DAY_OF_MONTH) == 1) {
						t.setBackgroundResource(R.drawable.table_textview);
						t.setTextColor(Color.BLACK);
						t.setText((newDate.get(Calendar.MONTH) + 1) + "月");
						t.setTag(m + "." + newDate.get(Calendar.DAY_OF_MONTH));
						t.setTag(-8,t.getText().toString());
						t.setOnClickListener(new DateOnClickListener(t.getTag().toString(), week[col]));
						continue;
					}else{
					    int dayn = newDate.get(Calendar.DAY_OF_MONTH);
					    t.setBackgroundResource(R.drawable.table_textview);
					    t.setTextColor(Color.BLACK);
					    t.setText(dayn + "");
					    t.setTag(m + "." + dayn);
					    t.setOnClickListener(new DateOnClickListener(t.getTag().toString(), week[col]));
					}
				}
				t.setTag(-8,t.getText().toString());
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
		private String TextViewTag;
		private String week;

		public DateOnClickListener(String tag, String week) {
			this.TextViewTag = tag;
			this.week = week;
		}
		@Override
		public void onClick(View arg0) {
			TextView tv = (TextView) arg0;
			clearDateWidget();
			tv.setBackgroundResource(R.drawable.resturant_button_selector);
			tv.setTextColor(Color.WHITE);
			tv.setText(title);
			weekin=week;
			tv.setTag(-7,"in");
			in = tv.getTag().toString();
			Intent intent=new Intent(PlaneDateWidget.this,OrderPlaneIndex.class);
			intent.putExtra("takeoff", tv.getTag().toString()+",,,,"+week);
			intent.putExtra("year",tv.getTag(-9).toString());
			System.out.println("对应的年份===="+tv.getTag(-8).toString());
			PlaneDateWidget.this.setResult(100,intent);
			PlaneDateWidget.this.finish();
		}

	}
}