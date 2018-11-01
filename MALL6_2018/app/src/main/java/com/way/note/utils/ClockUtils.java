package com.way.note.utils;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;

import com.way.note.CallAlarm;
import com.way.note.data.DBOpenHelper;
import com.way.note.data.NoteItem;

/**
 * 时锟接癸拷锟斤拷
 * 
 * @author way
 * 
 */
public class ClockUtils {
	private static final String TAG = "ClockUtils";
	public static NoteItem noteItem=new NoteItem();
	private static File ALARM_FLAG_FILE1 = new File("/productinfo/alarm_flag");

	/**
	 * set alarm clock for one noteItem.
	 */
	public static final void setAlarmClock(Context context, NoteItem noteItem) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent1 = new Intent(context, CallAlarm.class);
		intent1.putExtra(DBOpenHelper.ID, noteItem._id);
		PendingIntent pi1 = PendingIntent.getBroadcast(context, noteItem._id,
				intent1, 0);
		long alarmTime = 0L;
		try {
			alarmTime = noteItem.clockItem.getClockLong();
			Log.i(TAG,
					DateFormat.getDateFormat(context).format(
							new Date(alarmTime)));
			Log.i(TAG,
					DateFormat.getTimeFormat(context).format(
							new Date(alarmTime)));
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		if ((alarmTime - System.currentTimeMillis() > 0)) {
			am.cancel(pi1);
			am.set(AlarmManager.RTC_WAKEUP, alarmTime, pi1);
			Log.i(TAG, "openAlarm");
			alarm_flag_setup(alarmTime);
		}
	}

	/**
	 * cancle alarm clock for one noteItem.<br>
	 * if you want to delte one noteItem ,must notice to cancle the alarm clock.
	 */
	public static final void cancleAlarmClock(Context context, NoteItem noteItem) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, CallAlarm.class);
		intent.putExtra(DBOpenHelper.ID, noteItem._id);
		PendingIntent pi = PendingIntent.getBroadcast(context, noteItem._id,
				intent, 0);
		am.cancel(pi);
	}

	/**
	 * do what ?
	 */
	private static void alarm_flag_setup(final long alarmTimeInMillis) {
		Calendar c = Calendar.getInstance();
		c.set(2011, 0, 1, 0, 0, 0);
		Calendar to = Calendar.getInstance();
		to.setTimeInMillis(alarmTimeInMillis);
		TimeZone zone = c.getTimeZone();
		long dstOffset = zone.getOffset(alarmTimeInMillis);
		long startTimeInMillis = c.getTimeInMillis();
		long dstAlarmTimeInMillis = alarmTimeInMillis - dstOffset;
		long timeDiffInMillis = dstAlarmTimeInMillis - startTimeInMillis;
		long timeDiffInSecs = timeDiffInMillis / 1000;
		if (ALARM_FLAG_FILE1.exists()) {
			try {
				ALARM_FLAG_FILE1.delete();
			} catch (Exception e) {
				Log.i(TAG, ALARM_FLAG_FILE1 + " delete before write failed");
			}
		}

		try {
			FileWriter command = new FileWriter(ALARM_FLAG_FILE1);
			try {
				command.write(String.valueOf(timeDiffInSecs));
				command.write("\n");
				command.write(String.valueOf(alarmTimeInMillis / 1000));
				command.write("\n");
			} finally {
				command.close();
				Log.i(TAG, ALARM_FLAG_FILE1 + " write done");
			}
		} catch (Exception e) {
			Log.i(TAG, ALARM_FLAG_FILE1 + "write error");
		}
	}

	public static String[] CURSOR_COLS = new String[] {
			MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.TRACK };

	public static boolean isSongExist(Context context, String name) {
		boolean isSongExist = false;
		Cursor c = context.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, CURSOR_COLS, null,
				null, null);
		if (c != null) {
			while (c.moveToNext()) {
				String songname = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				String dispalyname = songname.substring(0,
						songname.lastIndexOf(".")).toLowerCase();

				if (name.equals(dispalyname)) {
					isSongExist = true;
					if (c != null)
						c.close();
					return isSongExist;

				} else {
					isSongExist = false;
				}
			}
		}
		if (c != null)
			c.close();
		return isSongExist;
	}
}
