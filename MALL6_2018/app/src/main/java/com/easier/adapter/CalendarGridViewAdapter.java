package com.easier.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.easier.util.CalendarUtil;
import com.lidroid.xutils.util.LogUtils;
import com.mall.view.R;

/**
 * 
 * TODO<GridViewAdapter>
 * 
 * @author ZhuZiQiang
 * @data: 2014-3-29 上午7:58:22
 * @version: V1.0
 */
public class CalendarGridViewAdapter extends BaseAdapter {
	
	private Calendar showMonthDate = Calendar.getInstance();
	private Calendar calStartDate = Calendar.getInstance();// 当前显示的日历      
	private Calendar calSelected = Calendar.getInstance(); // 选择的日历
	public void setSelectedDate(Calendar cal) {
		calSelected = cal;
	}

	private Calendar calToday = Calendar.getInstance(); // 今日
	private int iMonthViewCurrentMonth = 0; // 当前视图月
	private int firstOne=0;
	private boolean isShowOtherDay = false;
	// 根据改变的日期更新日历
	// 填充日历控件用
	private void UpdateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月

		// 星期一是2 星期天是1 填充剩余天数
		int iDay = 0;
		int iFirstDayOfWeek = Calendar.MONDAY;
		int iStartDay = iFirstDayOfWeek;
		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
			if (iDay < 0)
				iDay = 6;
		}
		if (iStartDay == Calendar.SUNDAY) {
			//get(Calendar.DAY_OF_WEEK)获得（现在是）这个星期的第几天
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

		calStartDate.add(Calendar.DAY_OF_MONTH, -1);// 周日第一位

	}

	ArrayList<java.util.Date> titles;

	private ArrayList<java.util.Date> getDates() {

		UpdateStartDateForMonth();

		ArrayList<java.util.Date> alArrayList = new ArrayList<java.util.Date>();

		for (int i = 1; i <= 42; i++) {
			alArrayList.add(calStartDate.getTime());
			calStartDate.add(Calendar.DAY_OF_MONTH, 1);//向后加一取整天
		}

		return alArrayList;
	}

	private Activity activity;
	Resources resources;

	// construct
	public CalendarGridViewAdapter(Activity a, Calendar cal,boolean isShowOtherDay) {
		calStartDate = cal;
		showMonthDate = Calendar.getInstance();
		showMonthDate.setTime(cal.getTime());
		activity = a;
		this.isShowOtherDay = isShowOtherDay;
		resources = activity.getResources();
		titles = getDates();
	}

	public CalendarGridViewAdapter(Activity a) {
		activity = a;
		resources = activity.getResources();
	}

	@Override
	public int getCount() {
		return titles.size();
	}

	@Override
	public Object getItem(int position) {
		return titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout iv = new LinearLayout(activity);
		iv.setId(position + 5000);
		LinearLayout imageLayout = new LinearLayout(activity);
		imageLayout.setOrientation(0);
		iv.setGravity(Gravity.CENTER);
		iv.setOrientation(1);
		iv.setBackgroundColor(resources.getColor(R.color.white));

		Date myDate = (Date) getItem(position);
		Calendar calCalendar = Calendar.getInstance();
		calCalendar.setTime(myDate);

		final int iMonth = calCalendar.get(Calendar.MONTH);
		final int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);
		// 判断周六周日
		iv.setBackgroundColor(resources.getColor(R.color.white));

		TextView txtToDay = new TextView(activity);// 日本老黄历
		txtToDay.setGravity(Gravity.CENTER_HORIZONTAL);
		txtToDay.setTextSize(12);
		CalendarUtil calendarUtil = new CalendarUtil(calCalendar);
		if( iMonth == showMonthDate.get(Calendar.MONTH) || isShowOtherDay){
			txtToDay.setText(calendarUtil.toString());
		}
			
		// 设置背景颜色结束
		
		// 日期开始
		TextView txtDay = new TextView(activity);// 日期
		txtDay.setGravity(Gravity.CENTER_HORIZONTAL);

		txtDay.setTextColor(resources.getColor(R.color.noMonthBlack));
		txtToDay.setTextColor(resources.getColor(R.color.noMonth));
		int day = myDate.getDate(); // 日期
		
		if(iMonth == showMonthDate.get(Calendar.MONTH) || isShowOtherDay){
			txtDay.setText(String.valueOf(day));				
		}
		txtDay.setId(position + 500);
		iv.setTag(myDate);

		// 这里用于比对是不是比当前日期小，如果比当前日期小就高亮
//		if (!CalendarUtil.compare(myDate, calToday.getTime())) {
//			iv.setBackgroundColor(resources.getColor(R.color.frame));
//		} else {
			// 设置背景颜色
			if (equalsDate(calSelected.getTime(), myDate)) {
				// 选择的
				txtDay.setBackground(resources.getDrawable(R.drawable.conrner_gray_5dp_shape2));
				txtDay.setTextColor(resources.getColor(R.color.white));
			} else {
				if (equalsDate(calToday.getTime(), myDate)) {
					// 当前日期
					txtDay.setBackground(resources.getDrawable(R.drawable.conrner_red_5dp_shape));
					txtDay.setTextColor(resources.getColor(R.color.white));
				}
			}
//		}
		if (equalsDate(calToday.getTime(), myDate)) {
			//如果是当前日期    阳历红色圆形背景
			txtDay.setBackground(resources.getDrawable(R.drawable.conrner_red_5dp_shape));
			txtDay.setTextColor(resources.getColor(R.color.white));

		}
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 30, 0, 0);
		txtDay.setPadding(9, 5, 10, 5);
		iv.addView(txtDay, lp);

		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp1.setMargins(0, 0, 0, 15);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 1);
		View line = new View(activity);
		line.setBackgroundColor(resources.getColor(R.color.gray_backgroud));
		line.setLayoutParams(lp2);

		iv.addView(txtToDay, lp1);
		iv.addView(line);
		return iv;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private Boolean equalsDate(Date date1, Date date2) {

		if (date1.getYear() == date2.getYear()
				&& date1.getMonth() == date2.getMonth()
				&& date1.getDate() == date2.getDate()) {
			return true;
		} else {
			return false;
		}

	}

	public void setShowOtherDay(boolean isShowOtherDay) {
		this.isShowOtherDay = isShowOtherDay;
	}
	

}
