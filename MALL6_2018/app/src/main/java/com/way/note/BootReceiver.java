package com.way.note;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.way.note.data.NoteDataManager;
import com.way.note.data.NoteDataManagerImpl;
import com.way.note.data.NoteItem;
import com.way.note.utils.ClockUtils;

/**
 * 开机广播接收者
 * 
 * @author way
 * 
 */
public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "BootReceiver";
	private Context mContext;
	int mClockId;
	public static final String SET_ALARM_INTENT = "com.way.note.SET_ALARM";

	private static final int SECOND = 1000000;

	private List<Long> alarmTimes = new ArrayList<Long>();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "onReceiver");
		mContext = context;

		String action = intent.getAction();
		if ("android.intent.action.BOOT_COMPLETED".equals(action)
				|| SET_ALARM_INTENT.equals(action)) {
			NoteDataManager dataManger = NoteDataManagerImpl
					.getNoteDataManger(context);
			List<NoteItem> clockAlarmItems = dataManger
					.getColckAlarmItemsFromDB();
			alarmTimes.clear();
			for (NoteItem item : clockAlarmItems) {
				long alarmClock = 0L;
				try {
					alarmClock = item.clockItem.getClockLong();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				Log.v(TAG, "alarmClock-->" + alarmClock + " currentTime-->"
						+ System.currentTimeMillis());
				if (isAboveNow(alarmClock) && isNotSameClock(alarmClock)) {
					Log.v(TAG, "setAlarm-->" + item);
					ClockUtils.setAlarmClock(mContext, item);
					alarmTimes.add(alarmClock);
				} else {
					Log.v(TAG, "cancle-->" + item);
					item.alarmEnable = false;
					dataManger.updateItem(item);
				}
			}
		}
	}

	public boolean isAboveNow(long alarmClock) {
		return (alarmClock - System.currentTimeMillis() > SECOND * 0);
	}

	public boolean isNotSameClock(long alarmClock) {
		for (Long alarmHaved : alarmTimes) {
			if (alarmHaved == alarmClock) {
				return false;
			}
		}
		return true;
	}
}
