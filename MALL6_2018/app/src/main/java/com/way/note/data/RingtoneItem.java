package com.way.note.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * 铃声对象
 * 
 * @author way
 * 
 */
@SuppressLint("SimpleDateFormat")
public class RingtoneItem {
	private static final String TAG = "ClockItem";
	public static final String DEFAULT_RINGTONE = "defalut";

	public String ringtoneUrl;// 铃声url
	public String ringtoneName;// 铃声名称

	public boolean isVibrate;

	public String ringtoneDate;// 铃声路径
	public String ringtoneTime;// 铃声时长

	public RingtoneItem() {
		ringtoneUrl = DEFAULT_RINGTONE;
		ringtoneName = DEFAULT_RINGTONE;
		isVibrate = false;
		ringtoneDate = "";
		ringtoneTime = "";
	}

	public void checkDateAndTime() {
		if (ringtoneTime == null) {
			ringtoneTime = "";
		}
		if (ringtoneDate == null) {
			ringtoneDate = "";
		}
	}

	public long getClockLong() throws ParseException {
		checkDateAndTime();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = dateFormat.parse(ringtoneDate);

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		Date time = timeFormat.parse(ringtoneTime);

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		c.setTime(time);
		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		c.set(year, month, day, hourOfDay, minute);

		Log.i(TAG, "date -->" + ringtoneDate);
		Log.i(TAG, "time -->" + ringtoneTime);
		return c.getTimeInMillis();
	}

	public Date getClockDate() throws ParseException {
		checkDateAndTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = dateFormat.parse(ringtoneDate+" "+ringtoneTime);
		return date;
	}
	public Date getClockTime24() throws ParseException {
		checkDateAndTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse(ringtoneDate+" "+ringtoneTime);
		return date;
	}

	public Date getClockTime() throws ParseException {
		checkDateAndTime();
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		Date time = timeFormat.parse(ringtoneDate+" " +ringtoneTime);
		return time;
	}
}
