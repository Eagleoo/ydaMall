package com.way.note;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.way.note.data.DBOpenHelper;
import com.way.note.data.NoteDataManager;
import com.way.note.data.NoteDataManagerImpl;
import com.way.note.data.NoteItem;

/**
 * 
 * @author way
 * 
 */
public class CallAlarm extends BroadcastReceiver {
	private static final String TAG = "CallAlarm";

	private Context mContext;
	private static final String DEFAULT_SNOOZE = "10";
	int mNoteID;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		Log.v(TAG, "onCreate()  intent-->" + intent + "  data-->"
				+ (intent == null ? "" : intent.getExtras()));
		// 接受其他闹钟事件，电话事件，短信事件等，进行交互处理
		String action = intent.getAction();
		if (action != null
				&& action.equals("android.intent.action.PHONE_STATE")) {
			Log.v(TAG, "onReceive:action.PHONE_STATE");
			snooze();
		} else if (action != null
				&& action.equals("android.provider.Telephony.SMS_RECEIVED")) {
			Log.v(TAG, "onReceive:Telephony.SMS_RECEIVED");
			snooze();
		} else {
			mNoteID = intent.getIntExtra(DBOpenHelper.ID, -1);
			Log.v(TAG, "noteID-->" + mNoteID);
			if (mNoteID == -1) {
				return;
			}

			NoteDataManager dateManager = NoteDataManagerImpl
					.getNoteDataManger(context);
			NoteItem noteItem = dateManager.getNoteItem(mNoteID);
			if (noteItem == null) {
				Log.e(TAG, "NoteItem is null");
				return;
			}

			Log.i(TAG, noteItem.toString());

			Intent i = new Intent(context, AlarmAlertActivity.class);
			i.putExtra(DBOpenHelper.ID, noteItem._id);
			i.putExtra(DBOpenHelper.ISVIBRATE, noteItem.clockItem.isVibrate);
			i.putExtra(DBOpenHelper.RINGTONE_URI,
					noteItem.clockItem.ringtoneUrl);
			i.putExtra(DBOpenHelper.RINGTONE_TIME,
					noteItem.clockItem.ringtoneTime);
			i.putExtra(DBOpenHelper.RINGTONE_NAME,
					noteItem.clockItem.ringtoneName);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}

	// Attempt to snooze this alert.
	private void snooze() {
		// Do not snooze if the snooze button is disabled.
//		final String snooze = DEFAULT_SNOOZE;
//		int snoozeMinutes = Integer.parseInt(snooze);
//		final long snoozeTime = System.currentTimeMillis()
//				+ (1000 * 60 * snoozeMinutes);
//
//		// Get the display time for the snooze and update the notification.
//		final Calendar c = Calendar.getInstance();
//		c.setTimeInMillis(snoozeTime);
//		// Append (snoozed) to the label.
//		String label = mContext.getResources()
//				.getString(R.string.default_label);
//		label = mContext.getResources().getString(
//				R.string.alarm_notify_snooze_label, label);
//
//		// Notify the user that the alarm has been snoozed.
//		Intent cancelSnooze = new Intent();
//		cancelSnooze.setAction("com.way.note.STOP_ALARM");
//		cancelSnooze.putExtra(DBOpenHelper.ID, mNoteID);
//		mContext.sendBroadcast(cancelSnooze);
//
//		PendingIntent broadcast = PendingIntent.getBroadcast(mContext, mNoteID,
//				cancelSnooze, 0);
//		Notification n = new Notification(R.drawable.stat_notify_alarm, label,
//				0);
//		n.setLatestEventInfo(
//				mContext,
//				label,
//				mContext.getResources().getString(
//						R.string.alarm_notify_snooze_text,
//						(String) DateFormat.format("kk:mm", c)), broadcast);
//		n.flags |= Notification.FLAG_AUTO_CANCEL
//				| Notification.FLAG_ONGOING_EVENT;
	}
}