package com.way.note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.YdAlainMall.util.time.MyDateTimePickerDialog;
import com.mall.note.NoteMainFrame;
import com.mall.util.Util;
import com.mall.view.Lin_MainFrame;
import com.mall.view.R;
import com.way.note.data.NoteItem;
import com.way.note.data.RingtoneItem;
import com.way.note.utils.ClockUtils;

/**
 * 
 * @author way
 * 
 */
public class AlarmActivity extends BaseActivity implements OnItemClickListener {
	private static final String TAG = "AlarmActivity";

	public static final String NOTE_ID = "noteID";

	private ListView mListView;
	private List<String> mItems = null;
	private List<String> mValues = new ArrayList<String>();
	private NoteItem noteItem = null;
	private AlarmSettingAdapter adapter = null;

	private Calendar cal = Calendar.getInstance();
	private TextView top_bcak;
	String mExternal = "content://media/external/";
	private TextView jishi_neirong;
	private String times;
	private TextView changeTime;
	private Dialog dialog;
	private Handler handler;
	private String addNew;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.notealarm_layout);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 100:
					if (TextUtils.isEmpty(changeTime.getText().toString()
							.trim())) {

					} else {
						String datetime = changeTime.getText().toString()
								.trim().split(" ")[0];
						String time = changeTime.getText().toString().trim()
								.split(" ")[1];
						onSetDate(Integer.parseInt(datetime.split("-")[0]),
								Integer.parseInt(datetime.split("-")[1]) - 1,
								Integer.parseInt(datetime.split("-")[2]));
						onSetTime(Integer.parseInt(time.split(":")[0]),
								Integer.parseInt(time.split(":")[1]));
					}
					break;
				}
			}
		};
		top_bcak = (TextView) findViewById(R.id.top_bcak);
		jishi_neirong = (TextView) findViewById(R.id.jishi_neirong);
		final String count = getIntent().getStringExtra("count");
		times = getIntent().getStringExtra("time");
		addNew = getIntent().getStringExtra("addNew");
		changeTime = new TextView(this);
		if (!TextUtils.isEmpty(count)) {
			jishi_neirong.setText(count);
		}
		top_bcak.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(count)) {
					finish();
					Util.showIntent(AlarmActivity.this, NoteMainFrame.class);
				} else {
					finish();
				}
			}
		});
		initViews();
		if (!initDataSuccess()) {
			finish();
		}
	}

	private void initViews() {
		mListView = (ListView) findViewById(R.id.alarm_listview);
		mListView.setOnItemClickListener(this);
	}

	private boolean initDataSuccess() {
		int noteID = getIntent().getIntExtra(NOTE_ID, -1);
		Log.v(TAG, "initDataSuccess ID-->" + noteID);
		if (noteID == -1) {
			return false;
		}

		noteItem = getDataManager(this).getNoteItem(noteID);
		Log.v(TAG, "initDataSuccess noteItem-->" + noteItem);
		// fix for bug 74169 start
		if (noteItem == null) {
			return false;
		}
		// fix for bug 71469 end

		String ringtoneUrl = noteItem.clockItem.ringtoneUrl;

		if (ringtoneUrl != null && ringtoneUrl.startsWith(mExternal)) {
			if (!ClockUtils.isSongExist(this, ringtoneUrl)) {
				noteItem.clockItem.ringtoneUrl = RingtoneItem.DEFAULT_RINGTONE;
			}
		}

		String[] itemsStrArray = getResources().getStringArray(
				R.array.clock_items);
		mItems = Arrays.asList(itemsStrArray);
		return true;
	}

	@Override
	public void onResume() {
		Log.v(TAG, "onResume()");
		super.onResume();
		updateData();
		updateDisplay();
	}

	private void updateData() {
		mValues.clear();
		if (noteItem.alarmEnable) {
			mValues.add(getResources().getString(R.string.yes));
		} else {
			mValues.add(getResources().getString(R.string.no));
		}

		try {
			// if (!TextUtils.isEmpty(times)) {
			// SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			// Date date=new Date();
			// date=sdf.parse(times);
			// mValues.add(DateFormat.getDateFormat(this).format(date));
			// }else {
			Date date = noteItem.clockItem.getClockDate();
			mValues.add(DateFormat.getDateFormat(this).format(date));
			// }
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage());
			mValues.add("");
		}
		// mValues.add(noteItem.clockItem.ringtoneDate); // format: 2002-02-13

		try {
			// if (!TextUtils.isEmpty(times)) {
			// SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
			// Date date=new Date();
			// date=sdf.parse(times);
			// mValues.add(DateFormat.getTimeFormat(this).format(date));
			// }else {
			Date time = noteItem.clockItem.getClockTime();
			mValues.add(DateFormat.getTimeFormat(this).format(time));
			// }

		} catch (ParseException e) {
			Log.e(TAG, e.getMessage());
			mValues.add("");
		}

		if (noteItem.clockItem.isVibrate) {
			mValues.add(getResources().getString(R.string.yes));
		} else {
			mValues.add(getResources().getString(R.string.no));
		}

		if (noteItem.clockItem.ringtoneName
				.equals(RingtoneItem.DEFAULT_RINGTONE)) {
			mValues.add(getResources().getString(R.string.default_rings_name));
		} else {
			mValues.add(noteItem.clockItem.ringtoneName);
		}
		Log.v(TAG, "updateDate values-->" + mValues);

	}

	private void updateDisplay() {
		if (adapter == null) {
			adapter = new AlarmSettingAdapter(this, mItems, mValues);
			mListView.setAdapter(adapter);
		} else {
			adapter.updateItem(mValues);
			adapter.notifyDataSetChanged();
		}
		if (!TextUtils.isEmpty(addNew)) {
			noteItem.alarmEnable = ClockUtils.noteItem.alarmEnable;
			noteItem.clockItem.isVibrate = ClockUtils.noteItem.clockItem.isVibrate;
			noteItem.clockItem.ringtoneDate = ClockUtils.noteItem.clockItem.ringtoneDate;
			noteItem.clockItem.ringtoneTime = ClockUtils.noteItem.clockItem.ringtoneTime;
			noteItem.clockItem.ringtoneName = ClockUtils.noteItem.clockItem.ringtoneName;
			noteItem.clockItem.ringtoneUrl = ClockUtils.noteItem.clockItem.ringtoneUrl;
			setAlarm();
			Util.showIntent(AlarmActivity.this, Lin_MainFrame.class,new String[]{"toTab"},new String[]{"note"});
			finish();
		}
		/*
		 * if (!TextUtils.isEmpty(times)) { changeTime.setText(times); String
		 * datetime=changeTime.getText().toString().trim().split(" ")[0]; String
		 * time=changeTime.getText().toString().trim().split(" ")[1];
		 * onSetDate(Integer.parseInt(datetime.split("-")[0]),
		 * Integer.parseInt(datetime.split("-")[1])-1,
		 * Integer.parseInt(datetime.split("-")[2]));
		 * onSetTime(Integer.parseInt(time.split(":")[0]),
		 * Integer.parseInt(time.split(":")[1])); }
		 */
	}

	private void updateUI() {
		updateData();
		updateDisplay();
	}

	public void onPause() {
		super.onPause();
		Log.i(TAG, "OnPause");
	}

	public void setRings() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_ALARM);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
		// R.string.set_alarm_ringtone);
				getResources().getString(R.string.set_alarm_ringtone));

		// Allow user to pick 'Default'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		// show 'Silent'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);

		String urlStr = noteItem.clockItem.ringtoneUrl;
		Uri ringtoneUri = null;
		if (urlStr == null) { // silent
			ringtoneUri = null;
		} else if (!urlStr.equals(RingtoneItem.DEFAULT_RINGTONE)) { // default
			ringtoneUri = Uri.parse(urlStr);
		} else {
			// Otherwise pick default ringtone Uri so that something is
			// selected.
			ringtoneUri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		}
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
				ringtoneUri);
		Log.i(TAG,
				"setRings intent-->" + intent + "  data-->"
						+ intent.getExtras());
		this.startActivityForResult(intent, 1);
	}

	/*
	 * protected Uri onRestoreRingtone(int mRingtoneType) { Uri actualUri =
	 * RingtoneManager.getActualDefaultRingtoneUri(this, mRingtoneType);
	 * Log.i("URL", actualUri.getPath() + " "); return actualUri != null ?
	 * actualUri : null; }
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "onActivityResult requestCode-->" + requestCode
				+ "  resultCode-->" + resultCode);
		Log.i(TAG, "onActivityResult intent-->" + data + "  data-->"
				+ (data == null ? null : data.getExtras()));
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == 1) {
			try {
				// Uri uri = data!=null ? data.getData() : null;
				Uri uri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (uri != null) {
					noteItem.clockItem.ringtoneUrl = uri.toString();
					Log.v("you", "rings_uri= " + uri.toString()
							+ " **********************************");
					if (RingtoneManager.isDefault(uri)) {
						noteItem.clockItem.ringtoneName = RingtoneItem.DEFAULT_RINGTONE;
					} else {
						Cursor cursor = this.getContentResolver().query(uri,
								ClockUtils.CURSOR_COLS, null, null, null);
						cursor.moveToFirst();
						String rings_name = cursor
								.getString(cursor
										.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
						cursor.close();
						Log.v("you", "rings_name= " + rings_name);
						noteItem.clockItem.ringtoneName = rings_name.substring(
								0, rings_name.lastIndexOf("."))/*
																 * .toLowerCase()
																 */;
					}
				} else {
					noteItem.clockItem.ringtoneName = getResources().getString(
							R.string.silent);
					noteItem.clockItem.ringtoneUrl = null;
				}
				saveChange();
				updateUI();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void saveChange() {
		getDataManager(this).updateItem(noteItem);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		switch (position) {
		case 0:
			setAlarmState();
			break;
		case 1:
			setDate();
			getTime();
			break;
		case 2:
			/*setTime();
			getTime();*/
			break;
		case 3:
			setVibrate();
			break;
		case 4:
			setRings();
			break;
		}
	}

	private void setAlarmState() {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.tuichu_login, null);
		final Dialog dialog = new AlertDialog.Builder(this).create();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView update_number = (TextView) layout
				.findViewById(R.id.update_number);
		update_number.setText("是否开启提醒？");
		TextView no_tuichu = (TextView) layout.findViewById(R.id.no_tuichu);
		no_tuichu.setText("取消");
		TextView queding_tuichu = (TextView) layout
				.findViewById(R.id.queding_tuichu);
		queding_tuichu.setText("确定");
		no_tuichu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				closeAlarm();
				dialog.dismiss();
			}
		});
		queding_tuichu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setAlarm();
				updateUI();
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);

		// String[] choices = new String[] {
		// getResources().getString(R.string.yes),
		// getResources().getString(R.string.no) };
		//
		// AlertDialog.Builder builder = new AlertDialog.Builder(
		// AlarmActivity.this)
		// .setTitle(R.string.enableAlarm)
		// // .setItems(yesOrNo, listener)
		// .setSingleChoiceItems(choices, noteItem.alarmEnable ? 0 : 1,
		// openAlarmlistener)
		// .setNegativeButton(R.string.Cancel, cancelDialog);
		// builder.show();
	}

	// OnClickListener openAlarmlistener = new DialogInterface.OnClickListener()
	// {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// switch (which) {
	// case 0:
	// setAlarm();
	// updateUI();
	// break;
	// case 1:
	// closeAlarm();
	// break;
	// }
	// dialog.dismiss();
	// // saveChange();
	// }
	// };

	private boolean setAlarm() {
		if (isIllegelTime()) {
			return false;
		}
		if (isSameTimeClock()) {
			return false;
		}

		noteItem.alarmEnable = true;
		saveChange();
		ClockUtils.cancleAlarmClock(this, noteItem);
		ClockUtils.setAlarmClock(this, noteItem);
		return true;
	}

	private boolean isSameTimeClock() {
		String date_setted = noteItem.clockItem.ringtoneDate;
		String time_setted = noteItem.clockItem.ringtoneTime;

		List<NoteItem> colckAlarmItems = getDataManager(this)
				.getColckAlarmItems();
		colckAlarmItems.remove(noteItem); // remove itself if exist.
		for (NoteItem clockItems : colckAlarmItems) {
			String old_date = clockItems.clockItem.ringtoneDate;
			String old_time = clockItems.clockItem.ringtoneTime;
			if (old_date.equals(date_setted) && old_time.equals(time_setted)) {
				Toast.makeText(AlarmActivity.this, R.string.date_time_setted,
						Toast.LENGTH_LONG).show();
				return true;
			}
		}
		return false;
	}

	private boolean isIllegelTime() {
		if (isDateNull() && isTimeNull()) {
			Toast.makeText(this, R.string.set_date_and_time_frist, 1000).show();
			return true;
		} else if (isDateNull()) {
			Toast.makeText(this, R.string.set_date_frist, 1000).show();
			return true;
		} else if (isTimeNull()) {
			Toast.makeText(this, R.string.set_time_frist, 1000).show();
			return true;
		}

		String[] temp = noteItem.clockItem.ringtoneTime.split(":");
		Calendar dateCalendar = praseDate();

		dateCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
		dateCalendar.set(Calendar.MINUTE, Integer.parseInt(temp[1]));
		dateCalendar.set(Calendar.SECOND, 0);
		dateCalendar.set(Calendar.MILLISECOND, 0);

		/**
		 * add one getoff alarm,system raise alarm ; if it is timeout, system
		 * must not set alarm and notify user change time yu.li@spreadtrum.com
		 * 2012/04/16 CR00561254
		 */
		if (dateCalendar.getTime().getTime() - System.currentTimeMillis() < 0) {
			Toast.makeText(AlarmActivity.this, R.string.alarm_time_error,
					Toast.LENGTH_LONG).show();
			return true;
		}
		return false;
	}

	public boolean isDateNull() {
		return noteItem.clockItem.ringtoneDate == null
				|| noteItem.clockItem.ringtoneDate.length() == 0;
	}

	public boolean isTimeNull() {
		return noteItem.clockItem.ringtoneTime == null
				|| noteItem.clockItem.ringtoneTime.length() == 0;
	}

	private void closeAlarm() {
		// cancle alarm
		ClockUtils.cancleAlarmClock(this, noteItem);

		// update db and cache
		noteItem.alarmEnable = false;
		saveChange();

		// update displays
		updateUI();
	}

	private void setDate() {
		Calendar calendar = null;

		if (noteItem.clockItem.ringtoneDate.length() == 0) {
			calendar = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
		} else {
			calendar = praseDate();
		}

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		// new DatePickerDialog(AlarmActivity.this, this, year, month,
		// day).show();
	}

	private Calendar praseDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date settingDate = dateFormat
					.parse(noteItem.clockItem.ringtoneDate);
			Calendar settingCal = Calendar.getInstance();
			settingCal.setTime(settingDate);
			return settingCal;
		} catch (ParseException e) {
			Log.e(TAG, "setDate fail!!!");
		}

		return null;
	}

	private Calendar praseTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		try {
			Date settingDate = dateFormat
					.parse(noteItem.clockItem.ringtoneTime);
			Calendar settingCal = Calendar.getInstance();
			settingCal.setTime(settingDate);
			return settingCal;
		} catch (ParseException e) {
			Log.e(TAG, "setTime fail!!!");
		}

		return null;
	}

	private void getTime() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		MyDateTimePickerDialog dateTime = new MyDateTimePickerDialog(this,
				dialog, width, height, changeTime, 1, "", handler);
		dateTime.getTime();
	}

	public void onSetDate(int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = praseTime();
		if (calendar != null) {
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			/*
			 * if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
			 * Toast.makeText(getApplicationContext(),
			 * R.string.alarm_time_error, Toast.LENGTH_LONG).show(); return; }
			 */
		} else {
			cal.setTimeInMillis(System.currentTimeMillis());
			int cur_year = cal.get(Calendar.YEAR);
			int cur_month = cal.get(Calendar.MONTH);
			int cur_day = cal.get(Calendar.DAY_OF_MONTH);

			Calendar sys_current = Calendar.getInstance();
			sys_current.set(Calendar.YEAR, cur_year);
			sys_current.set(Calendar.MONTH, cur_month);
			sys_current.set(Calendar.DAY_OF_MONTH, cur_day);
			sys_current.set(Calendar.HOUR_OF_DAY, 0);
			sys_current.set(Calendar.MINUTE, 0);

			Calendar set_date = Calendar.getInstance();
			set_date.set(Calendar.YEAR, year);
			set_date.set(Calendar.MONTH, monthOfYear);
			set_date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			set_date.set(Calendar.HOUR_OF_DAY, 0);
			set_date.set(Calendar.MINUTE, 0);

			if (set_date.getTimeInMillis() < sys_current.getTimeInMillis()) {
				Toast.makeText(getApplicationContext(),
						R.string.alarm_time_error, Toast.LENGTH_LONG).show();
				return;
			}
		}

		String oldDate = noteItem.clockItem.ringtoneDate;
		noteItem.clockItem.ringtoneDate = year + "-" + format(monthOfYear + 1)
				+ "-" + format(dayOfMonth);

		if (noteItem.alarmEnable) {
			if (setAlarm()) {
				updateUI();
				saveChange();
			} else {
				noteItem.clockItem.ringtoneDate = oldDate;
			}
		} else {
			updateUI();
			saveChange();
		}
	}

	public String format(int i) {
		String s = "" + i;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}

	private void setTime() {
		int hour = 0;
		int minute = 0;
		cal.setTimeInMillis(System.currentTimeMillis());
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);

		if (noteItem.clockItem.ringtoneTime.length() == 0) {
			cal.setTimeInMillis(System.currentTimeMillis());
			hour = cal.get(Calendar.HOUR_OF_DAY);
			minute = cal.get(Calendar.MINUTE);
		} else {
			Calendar calendar = praseTime();
			if (calendar != null) {
				hour = calendar.get(Calendar.HOUR_OF_DAY);
				minute = calendar.get(Calendar.MINUTE);
			}
		}
		// new TimePickerDialog(AlarmActivity.this, this, hour, minute,
		// DateFormat.is24HourFormat(this)).show();
	}

	public void onSetTime(int hourOfDay, int minute) {
		Calendar calendar = praseDate();
		if (calendar != null) {
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
				Toast.makeText(getApplicationContext(),
						R.string.alarm_time_error, Toast.LENGTH_LONG).show();
				return;
			}
		}

		String oldTime = noteItem.clockItem.ringtoneTime;
		noteItem.clockItem.ringtoneTime = format(hourOfDay) + ":"
				+ format(minute);

		if (noteItem.alarmEnable) {
			if (setAlarm()) {
				updateUI();
				saveChange();
			} else {
				noteItem.clockItem.ringtoneTime = oldTime;
			}
		} else {
			updateUI();
			saveChange();
		}
	}

	public void setVibrate() {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.tuichu_login, null);
		final Dialog dialog = new AlertDialog.Builder(this).create();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView update_number = (TextView) layout
				.findViewById(R.id.update_number);
		update_number.setText("是否开启震动？");
		TextView no_tuichu = (TextView) layout.findViewById(R.id.no_tuichu);
		no_tuichu.setText("取消");
		TextView queding_tuichu = (TextView) layout
				.findViewById(R.id.queding_tuichu);
		queding_tuichu.setText("确定");
		no_tuichu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				noteItem.clockItem.isVibrate = false;
				saveChange();
				dialog.dismiss();
				updateUI();
			}
		});
		queding_tuichu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				noteItem.clockItem.isVibrate = true;
				saveChange();
				dialog.dismiss();
				updateUI();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		//
		// String[] choices = new String[] {
		// getResources().getString(R.string.yes),
		// getResources().getString(R.string.no) };
		//
		// int checkedVibrate = 0;
		// if (noteItem.clockItem.isVibrate) {
		// checkedVibrate = 0;
		// } else {
		// checkedVibrate = 1;
		// }
		// new AlertDialog.Builder(AlarmActivity.this)
		// .setTitle(R.string.setVibrate)
		// // .setItems(yesOrNo, listener)
		// .setSingleChoiceItems(choices, checkedVibrate, vibrateListener)
		// .setNegativeButton(R.string.Cancel, cancelDialog).show();
	}

	// OnClickListener vibrateListener = new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// // isVibrate = yesOrNo[which];
	// // saveChange();
	// // setListItem();
	// switch (which) {
	// case 0:
	// noteItem.clockItem.isVibrate = true;
	// break;
	// case 1:
	// noteItem.clockItem.isVibrate = false;
	// break;
	// }
	// saveChange();
	// dialog.dismiss();
	// updateUI();
	// }
	// };
}