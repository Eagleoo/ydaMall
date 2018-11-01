package com.way.note;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.view.R;
import com.way.note.data.DBOpenHelper;
import com.way.note.data.NoteItem;

import java.util.Date;

public class AlarmAlertActivity extends BaseActivity {
	private static final String TAG = "AlarmAlert";

	public static final String STOP_ALARM = "com.way.note.STOP_ALARM";
	public static final String ALARM_DONE = "com.android.deskclock.ALARM_DONE";
	int TIMEOUT = 55;// 闹铃响时长（s）
	private String action;

	private long mStartTime;
	private Handler mHandler = new Handler();
	private NotificationManager mNotificationManager;
	private static final int NOTIFICATION_ID = 100000010;
	private String mContent;
	private PowerManager.WakeLock mWakeLock;

	KeyguardManager mKeyguardManager = null;// 声明键盘管理器

	private KeyguardLock mKeyguardLock = null; // 声明键盘锁

	private TextView mContentTV;
	private NoteItem mNoteItem;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Log.v(TAG, "onCreate()  intent-->" + getIntent() + "  data-->"
				+ (getIntent() == null ? "" : getIntent().getExtras()));
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.note_dialog_alarm_alert);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.note_dialog_alarm_alert_title);

		initViews();
		initData();
		setWakeLock();
		registerStopReceiver();
		setNotication();
		setMuteOnTel();
		setAutoStop();
		stopWhenGoFM();
	}

	private void stopWhenGoFM() {
		/**
		 * add for bug : when play fm ,need to stop the alarm.^M Intent i = new
		 * Intent("com.android.fm.stopmusicservice"); i.putExtra("playingfm",
		 * true); this.sendBroadcast(i);
		 */
		IntentFilter stopFromFM = new IntentFilter(
				"com.android.fm.stopmusicservice");
		this.registerReceiver(stopFromFmReceiver, stopFromFM);
	}

	private BroadcastReceiver stopFromFmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Boolean isPlaying = intent.getExtras().getBoolean("playingfm");
			if (intent.getAction().equals("com.android.fm.stopmusicservice")) {
				if (isPlaying != null && isPlaying) {
					closeMediaPlayer();
				}
			}
		}
	};

	private void setAutoStop() {
		mHandler.postDelayed(stopTask, TIMEOUT * 1000);
	}

	private Runnable stopTask = new Runnable() {
		@Override
		public void run() {
			Log.i(TAG, "auto stop task run");
			closeMediaPlayer();
		}
	};

	private void initViews() {
		TextView alarmTimeTV = (TextView) findViewById(R.id.title_right);
		alarmTimeTV.setText(DateFormat.getTimeFormat(this).format(new Date()));
		mContentTV = (TextView) findViewById(R.id.tv_note_content);
		Button cancelBtn = (Button) findViewById(R.id.cancel_alarm);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeMediaPlayer();
				cancleNoticafation();
			}
		});
	}

	private void initData() {
		Intent intent = getIntent();
		int _id = intent.getIntExtra(DBOpenHelper.ID, -1);
		Log.v(TAG, " id-->" + _id);

		mNoteItem = getDataManager(this).getNoteItem(_id);

		mNoteItem.alarmEnable = false;
		getDataManager(this).updateItem(mNoteItem);

		mContent = mNoteItem.content;
		if (mContent != null && mContent.length() > 15) {// 如果内容太长,则采用如下方式显示
			mContent = mContent.substring(0, 15) + "...";
		}
		mContentTV.setText(mContent);
	}

	private void registerStopReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(STOP_ALARM);
		// 防止在灭屏时不能接收停止闹钟的广播，所以用静态注册。
		this.registerReceiver(this.receiver, filter);
	}

	private void setMuteOnTel() {
		// 通知背景播放音乐mp3暂停，这个action被mp3注册
		Intent alarm_alert = new Intent("com.android.deskclock.ALARM_ALERT");
		this.sendBroadcast(alarm_alert);

		// 获得来电状态，如果正在通话或者来电，闹铃时间到不响闹铃，只是通知。
		TelephonyManager mTelephonyMgr = (TelephonyManager) this
				.getSystemService(this.TELEPHONY_SERVICE);
		int callstate = mTelephonyMgr.getCallState();
		if (callstate == 0) {
			// 调用服务，播放闹铃
			Intent intent2 = new Intent(this, AlarmService.class);
			intent2.putExtra(DBOpenHelper.RINGTONE_URI,
					mNoteItem.clockItem.ringtoneUrl);
			intent2.putExtra(DBOpenHelper.ISVIBRATE,
					mNoteItem.clockItem.isVibrate);
			intent2.putExtra(DBOpenHelper.RINGTONE_NAME,
					mNoteItem.clockItem.ringtoneName);

			startService(intent2);
			// 音量控制
			setVolumeControlStream(AudioManager.STREAM_ALARM);
			mStartTime = System.currentTimeMillis();
			Log.v(TAG, "AlarmAlert:mStartTime=" + mStartTime);
		} else {
			String displayTime = AlarmAlertActivity.this.getResources()
					.getString(R.string.alarm_alert_snooze_set);
			Toast.makeText(AlarmAlertActivity.this, displayTime,
					Toast.LENGTH_LONG).show();
			this.finish();
			this.unregisterReceiver(this.receiver);
		}
	}

	private void setNotication() {
//		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
//		Notification n = new Notification();
//		// 设置显示图标，该图标会在状态栏显示
//		int icon = R.drawable.note_clock;
//		// 设置显示提示信息，该信息也会在状态栏显示
//		String tickerText = AlarmAlertActivity.this.getResources().getString(
//				R.string.notify_ticker);
//		// 显示时间
//		long when = System.currentTimeMillis();
//		String notifyTitle = mContent;
//
//		n.icon = icon;
//		n.tickerText = tickerText;
//		n.when = when;
//		n.defaults |= Notification.DEFAULT_VIBRATE;
//
//		final Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(System.currentTimeMillis() + (1000 * 60));
//		Intent intent1 = new Intent();
//		intent1.setAction(STOP_ALARM); // 如果在非AlarmAlert界面，则不能接受处理广播。
//
//		PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent1,
//				0);
//		n.setLatestEventInfo(
//				this,
//				notifyTitle,
//				getResources().getString(R.string.alarm_notify_snooze_text,
//						(String) DateFormat.format("kk:mm", cal)), broadcast);
//		n.flags |= Notification.FLAG_AUTO_CANCEL
//				| Notification.FLAG_ONGOING_EVENT;
//
//		Log.v(TAG, "notify()");
//		mNotificationManager.notify(NOTIFICATION_ID, n);
	}

	private void setWakeLock() {
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// 获取系统服务
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

		Log.i(TAG, "pm.isScreenOn()=" + pm.isScreenOn());
		Log.i(TAG, "mKeyguardManager.inKeyguardRestrictedInputMode()="
				+ mKeyguardManager.inKeyguardRestrictedInputMode());

		if (!pm.isScreenOn()) {
			// 点亮屏
			mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
			mWakeLock.acquire();
			if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
				// 初始化键盘锁，可以锁定或解开键盘锁
				mKeyguardLock = mKeyguardManager.newKeyguardLock("");
				// 禁用显示键盘锁定
				mKeyguardLock.disableKeyguard();
			}
		} else if (pm.isScreenOn()) {
			if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
				// 初始化键盘锁，可以锁定或解开键盘锁
				mKeyguardLock = mKeyguardManager.newKeyguardLock("");
				// 禁用显示键盘锁定
				mKeyguardLock.disableKeyguard();
			}
		}
	}

	@Override
	public void onBackPressed() {
		closeMediaPlayer();
		cancleNoticafation();
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "OnResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		if (mWakeLock != null) {
			mWakeLock.release();
		}
		if (mKeyguardLock != null) {
			mKeyguardLock.reenableKeyguard();
		}
		super.onStop();
	}

	// 用于接受广播，停止震动
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if (action != null && action.equals("com.way.note.STOP_ALARM")) {
				closeMediaPlayer();
			}
		}
	};

	public void closeMediaPlayer() {
		mHandler.removeCallbacks(stopTask);
		Intent intent3 = new Intent(this, AlarmService.class);
		Log.v(TAG, "closeMediaPlayer()");
		stopService(intent3);

		// 恢复音量控制状态
		setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

		// 通知背景播放音乐mp3再次启动
		Intent alarm_done = new Intent(ALARM_DONE);
		this.sendBroadcast(alarm_done);

		mHandler.post(mDisplayToast);
		// 修改鬧鈴数据库，使得“开启鬧鈴”状态为否

		this.unregisterReceiver(this.receiver);
		this.unregisterReceiver(stopFromFmReceiver);
		this.finish();
	}

	private void cancleNoticafation() {
		if (mNotificationManager != null) {
			mNotificationManager.cancel(NOTIFICATION_ID);
		}
	}

	Runnable mDisplayToast = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String displayTime = AlarmAlertActivity.this.getResources()
					.getString(R.string.alarm_alert_snooze_set);
			Toast.makeText(AlarmAlertActivity.this, displayTime,
					Toast.LENGTH_LONG).show();
		}
	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// mp.stop();
		}
		super.onConfigurationChanged(newConfig);
	}

}