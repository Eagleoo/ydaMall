package com.YdAlainMall.util.time;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mall.view.R;

/**
 * @auther:summer 时间： 2012-7-19 下午2:59:56
 */
public class MyDateTimePickerDialog {
	private static int START_YEAR = 1900, END_YEAR = 2100;
	private Calendar mCalendar;
	private Button queding, quxiao;
	private int curr_year, curr_month, curr_day, curr_hour, curr_minute;
	// 添加大小月月份并将其转换为list,方便之后的判断
	private String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	private String[] months_little = { "4", "6", "9", "11" };
	private WheelView wv_year, wv_month, wv_day, wv_hours, wv_mins;
	private List<String> list_big, list_little;
	private Context context;
	private Dialog dialog;
	private int width;
	private int height;
	private String time;
	private EditText editText;
	private TextView textView;
	private int state = 0;
	private String message;
	private Handler handler;
	public MyDateTimePickerDialog(Context context, Dialog dialog, int width,
			int height, EditText editText) {
		this.context = context;
		this.dialog = dialog;
		this.width = width;
		this.height = height;
		this.editText = editText;
	}

	public MyDateTimePickerDialog(Context context, Dialog dialog, int width,
			int height, TextView textView, int state, String message,Handler handler) {
		this.context = context;
		this.dialog = dialog;
		this.width = width;
		this.height = height;
		this.message = message;
		this.state = state;
		this.textView = textView;
		this.handler=handler;
	}

	public void getTime() {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(context);
		RelativeLayout view = (RelativeLayout) inflaterDl.inflate(
				R.layout.time_layout, null);
		dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		if (state==1) {
			params.width = width * 5 / 5;
		}
		
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
		mCalendar = Calendar.getInstance();
		int year = mCalendar.get(Calendar.YEAR);
		int month = mCalendar.get(Calendar.MONTH);
		int day = mCalendar.get(Calendar.DATE);
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);
		list_big = Arrays.asList(months_big);
		list_little = Arrays.asList(months_little);
		int textSize = 0;
		textSize = adjustFontSize(width, height);
		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// 日
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		// 时
		wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		wv_hours.setCyclic(true);
		wv_hours.setCurrentItem(hour);

		// 分
		wv_mins = (WheelView) view.findViewById(R.id.mins);
		if (state == 1) {
			wv_hours.setVisibility(View.VISIBLE);
			wv_mins.setVisibility(View.VISIBLE);
		}
		wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		wv_mins.setCyclic(true);
		wv_mins.setCurrentItem(minute);
		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
		wv_day.TEXT_SIZE = textSize;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
		queding = (Button) view.findViewById(R.id.queding);
		quxiao = (Button) view.findViewById(R.id.quxiao);
		queding.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				curr_year = wv_year.getCurrentItem() + START_YEAR;
				curr_month = wv_month.getCurrentItem() + 1;
				curr_day = wv_day.getCurrentItem() + 1;
				curr_hour = wv_hours.getCurrentItem();
				curr_minute = wv_mins.getCurrentItem();
				if (state == 1) {
					time = (curr_year + "-" + curr_month + "-" + curr_day + " "
							+ curr_hour + ":" + curr_minute).toString();
					textView.setText(message + " " + time);
					Message msg=new Message();
					msg.what=100;
					handler.sendMessage(msg);
				} else {
					time = (curr_year + "-" + curr_month + "-" + curr_day)
							.toString();
					editText.setText(time);
				}
				dialog.dismiss();

			}
		});
		quxiao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}

	public static int adjustFontSize(int width, int height) {
		int screenWidth = width;
		int screenHeight = height;
		return (int) (width / 20);
	}
}
