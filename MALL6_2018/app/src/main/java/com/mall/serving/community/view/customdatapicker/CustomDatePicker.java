package com.mall.serving.community.view.customdatapicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.wheel.AbstractWheel;
import com.mall.serving.community.view.wheel.AbstractWheelTextAdapter;
import com.mall.serving.community.view.wheel.ArrayWheelAdapter;
import com.mall.serving.community.view.wheel.OnWheelScrollListener;
import com.mall.serving.community.view.wheel.WheelVerticalView;
import com.mall.view.R;

public class CustomDatePicker<E> extends LinearLayout {
	private WheelVerticalView year;
	private WheelVerticalView month;
	private WheelVerticalView day;
	private ChangingListener listener;

	private ArrayWheelAdapter adapter3;
	private Context context;
	private Calendar select_date;
	private Calendar now_date;

	public CustomDatePicker(final Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		View v = LayoutInflater.from(context).inflate(
				R.layout.community_custom_datepicker, null);
		year = (WheelVerticalView) v.findViewById(R.id.year);
		month = (WheelVerticalView) v.findViewById(R.id.month);
		day = (WheelVerticalView) v.findViewById(R.id.day);

		ArrayList<String> yearlist = new ArrayList<String>();
		ArrayList<String> monthlist = new ArrayList<String>();

		for (int i = 1901; i < 2900; i++) {
			yearlist.add(i + "年");

		}

		for (int i = 1; i <= 12; i++) {
			monthlist.add(i + "月");
		}

		ArrayWheelAdapter adapter1 = new ArrayWheelAdapter(context,
				yearlist.toArray());

		ArrayWheelAdapter adapter2 = new ArrayWheelAdapter(context,
				monthlist.toArray());
		adapter3 = new ArrayWheelAdapter(context, getDaylist(31).toArray());
		setTextColor(adapter1);
		setTextColor(adapter2);
		setTextColor(adapter3);
		year.setViewAdapter(adapter1);
		month.setViewAdapter(adapter2);
		day.setViewAdapter(adapter3);
		year.setCyclic(true);
		month.setCyclic(true);
		day.setCyclic(true);
		setDate(Calendar.getInstance());

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(AbstractWheel wheel) {
				select_date = getDate();

			}

			@Override
			public void onScrollingFinished(AbstractWheel wheel) {
				
				if (now_date != null) {
					Calendar date = getDate();
					int compareTo = date.compareTo(
							now_date);

					if (compareTo < 0) {
						setDate(select_date);
						Util.show("不能选择此日期");

					}

				}

				setDayAdapter();

			}
		};

		OnWheelScrollListener scrollListener1 = new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(AbstractWheel wheel) {
				select_date = getDate();

			}

			@Override
			public void onScrollingFinished(AbstractWheel wheel) {
				
				if (now_date != null) {
					Calendar date = getDate();
					int compareTo = date.compareTo(
							now_date);

					if (compareTo < 0) {
						setDate(select_date);
						Util.show("不能选择此日期");
					}

				}
				doListener();

			}
		};
		month.addScrollingListener(scrollListener);
		year.addScrollingListener(scrollListener);
		day.addScrollingListener(scrollListener1);

		addView(v);

	}

	private List getDaylist(int num) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 1; i <= num; i++) {
			String d = i + "日";
			if (i < 10) {
				d = "\t" + i + "日";
			}
			list.add(d);
		}
		return list;

	}

	public void setDate(Calendar c) {
		int y = c.get(Calendar.YEAR);
		int index1 = y - 1901;
		year.setCurrentItem(index1);
		int m = c.get(Calendar.MONTH);

		month.setCurrentItem(m);
		int d = c.get(Calendar.DAY_OF_MONTH);
		setDayAdapter();
		day.setCurrentItem(d - 1);

	}

	public Calendar getDate() {
		int y = year.getCurrentItem();
		int index1 = y + 1901;
		int m = month.getCurrentItem();
		int d = day.getCurrentItem();
		Calendar c = Calendar.getInstance();
		c.set(index1, m, d + 1);

		return c;
	}

	public void addChangingListener(ChangingListener listener) {
		this.listener = listener;

	}

	private void doListener() {
		if (listener != null) {
			listener.onChange(getDate());
		}

	}

	public void setTextColor(AbstractWheelTextAdapter adapter) {
		adapter.setTextColor(context.getResources().getColor(R.color.blue));
	}

	interface ChangingListener {

		void onChange(Calendar c);
	}

	private void setDayAdapter() {
		int currentItem = year.getCurrentItem();
		int current = currentItem + 1901;
		int newValue = month.getCurrentItem() + 1;
		switch (newValue) {

		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:

			adapter3 = new ArrayWheelAdapter(context, getDaylist(31).toArray());

			break;
		case 2:
			if (current % 4 == 0) {
				adapter3 = new ArrayWheelAdapter(context, getDaylist(29)
						.toArray());
			} else {
				adapter3 = new ArrayWheelAdapter(context, getDaylist(28)
						.toArray());
			}

			break;

		case 4:
		case 6:
		case 9:
		case 11:
			adapter3 = new ArrayWheelAdapter(context, getDaylist(30).toArray());
			break;
		default:
			break;
		}
		day.setViewAdapter(adapter3);
		setTextColor(adapter3);
		doListener();

	}
	
	public void setNowData(Calendar c){
		now_date=c;
	}

}
