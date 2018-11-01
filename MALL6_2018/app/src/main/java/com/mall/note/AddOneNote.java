package com.mall.note;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.YdAlainMall.util.time.MyDateTimePickerDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.model.UserInfo;
import com.mall.net.Web;
import com.mall.note.SwitchButton.OnChangeListener;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.way.note.NoteEditor;
import com.way.note.data.NoteItem;
import com.way.note.data.RingtoneItem;
import com.way.note.utils.ClockUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddOneNote extends Activity {
	private User user;
	private UserInfo userInfo;
	private String userId = "";
	private String md5Pwd = "";
	private Dialog dialog;
	@ViewInject(R.id.start_time)
	private TextView start_time;
	@ViewInject(R.id.jishi_title)
	private EditText jishi_title;
	@ViewInject(R.id.end_time)
	private TextView end_time;
	@ViewInject(R.id.add_tixing1)
	private TextView add_tixing1;
	@ViewInject(R.id.add_tixing2)
	private TextView add_tixing2;
	@ViewInject(R.id.add_tixing3)
	private TextView add_tixing3;
	@ViewInject(R.id.lin_add1)
	private LinearLayout lin_add1;
	@ViewInject(R.id.lin_add2)
	private LinearLayout lin_add2;
	@ViewInject(R.id.lin_add3)
	private LinearLayout lin_add3;
	@ViewInject(R.id.img_add1)
	private ImageView img_add1;
	@ViewInject(R.id.img_add2)
	private ImageView img_add2;
	private String startDate = "";
	private String endDate = "";
	private PopupWindow distancePopup = null;
	private TextView[] change;
	private TextView[] change1;
	@ViewInject(R.id.re2)
	private RelativeLayout re2;
	String changeTime = "";
	@ViewInject(R.id.jishi_message)
	private TextView jishi_message;
	String endTime = "";
	private String jishiId = "";
	@ViewInject(R.id.now_shijian)
	private TextView now_shijian;
	private String jishiXq;
	private int state = 0;// 等于0表示不添加提醒时间，1表示添加提醒时间
	// @ViewInject(R.id.quxiao_tixing)
	// private TextView quxiao_tixing;
	private Handler handler;
	@ViewInject(R.id.tixing_mesage)
	private LinearLayout tixing_mesage;
	@ViewInject(R.id.kaiqi_tixing)
	private TextView kaiqi_tixing;
	@ViewInject(R.id.guanbi_tixing)
	private TextView guanbi_tixing;
	@ViewInject(R.id.kaiqi_zhendong)
	private TextView kaiqi_zhendong;
	@ViewInject(R.id.guanbi_zhendong)
	private TextView guanbi_zhendong;
	private NoteItem noteItem = new NoteItem();
	@ViewInject(R.id.xuanze_lings)
	private TextView xuanze_lings;
	private Calendar cal = Calendar.getInstance();
	private SwitchButton wiperSwitch1;
	@ViewInject(R.id.lin_add4)
	private LinearLayout lin_add4;
	// 是否是编辑状态
	private int bianji = 0;
	@ViewInject(R.id.baocun)
	private TextView baocun;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_one_note);
		ViewUtils.inject(this);
		wiperSwitch1 = (SwitchButton) findViewById(R.id.wiperSwitch1);
		wiperSwitch1.setOnChangeListener(new OnChangeListener() {
			@Override
			public void onChange(SwitchButton sb, boolean states) {
				if (states) {
					state = 0;
					lin_add1.setVisibility(View.VISIBLE);
					tixing_mesage.setVisibility(View.VISIBLE);
				} else {
					lin_add1.setVisibility(View.GONE);
					add_tixing1.setText("添加提醒时间");
					tixing_mesage.setVisibility(View.GONE);
				}
			}
		});
		wiperSwitch1.setChecked(false);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 100:
					state = 1;
					tixing_mesage.setVisibility(View.VISIBLE);
					break;
				}
			}
		};
		noteItem.alarmEnable = true;
		noteItem.clockItem.isVibrate = true;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		now_shijian.setText(sdf.format(date));
		start_time.setText("开始时间：" + sdf1.format(date));
		changeTime = getIntent().getStringExtra("time");
		endTime = getIntent().getStringExtra("endtime");
		jishiId = getIntent().getStringExtra("jishiId");
		jishiXq = getIntent().getStringExtra("jishiXq");
		now_shijian.setText(changeTime);
		if (TextUtils.isEmpty(jishiId)) {

		} else {
			jishi_message.setText("记事详情");
			String[] timeString = endTime.split("--..--");
			baocun.setText("编辑");
			lin_add4.setVisibility(View.GONE);
			jishi_title.setEnabled(false);

			if (timeString.length > 1) {
				end_time.setText("结束时间：" + endTime.split("--..--")[1].trim());

			}
			if (timeString.length > 0) {
				start_time.setText("开始时间：" + endTime.split("--..--")[0].trim());

			}
			jishi_title.setText(jishiXq);
		}
		if (TextUtils.isEmpty(changeTime)) {

		} else {
			start_time.setText("开始时间：" + changeTime);
		}
		if (Util.checkLoginOrNot()) {
			userInfo = new UserInfo();
			user = UserData.getUser();
			userId = user.getUserId();
			md5Pwd = user.getMd5Pwd();
		} else {
			Util.showIntent(AddOneNote.this, LoginFrame.class);
		}

	}

	public void changeColor(TextView... textViews) {
		for (int i = 0; i < textViews.length; i++) {
			if (i == 0) {
				textViews[i].setTextColor(getResources().getColor(R.color.bg));
				textViews[i].setBackgroundColor(getResources().getColor(R.color.headertop));
			} else {
				textViews[i].setTextColor(getResources().getColor(R.color.yyrg_shouye_zi));
				textViews[i].setBackgroundColor(getResources().getColor(R.color.bg));
			}
		}
	}

	@OnClick({ R.id.start_time, R.id.baocun, R.id.end_time, R.id.topback, R.id.lin_add1, R.id.quxiao_tixing,
			R.id.xuanze_lings, R.id.kaiqi_tixing, R.id.guanbi_tixing, R.id.kaiqi_zhendong, R.id.guanbi_zhendong })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.kaiqi_tixing:
			changeColor(kaiqi_tixing, guanbi_tixing);
			noteItem.alarmEnable = true;
			break;
		case R.id.guanbi_tixing:
			changeColor(guanbi_tixing, kaiqi_tixing);
			noteItem.alarmEnable = false;
			break;
		case R.id.kaiqi_zhendong:
			noteItem.clockItem.isVibrate = true;
			changeColor(kaiqi_zhendong, guanbi_zhendong);
			break;
		case R.id.guanbi_zhendong:
			noteItem.clockItem.isVibrate = false;
			changeColor(guanbi_zhendong, kaiqi_zhendong);
			break;
		case R.id.xuanze_lings:
			setRings();
			break;
		case R.id.quxiao_tixing:
			state = 0;
			add_tixing1.setText("添加提醒时间");

			break;
		case R.id.lin_add1:
			getTime(add_tixing1, "提醒时间：");
			break;
		case R.id.topback:
			finish();
			break;
		case R.id.start_time:
			getTime(start_time, "开始时间：");
			break;

		case R.id.end_time:
			getTime(end_time, "结束时间：");
			break;
		case R.id.baocun:
			if (bianji == 0 && !TextUtils.isEmpty(jishiId)) {
				bianji = 1;
				baocun.setText("保存");
				// jishi_title.setClickable(false);
				jishi_title.setEnabled(true);
				lin_add4.setVisibility(View.VISIBLE);
			} else {
				startDate = start_time.getText().toString().trim().replace("开始时间：", "").toString().trim();
				endDate = end_time.getText().toString().trim().replace("结束时间：", "").toString().trim();
				if (TextUtils.isEmpty(jishi_title.getText().toString().trim())) {
					Util.show("请输入记事内容", this);
					return;
				}
				if (TextUtils.isEmpty(endTime)) {
					saveNote(startDate + "--..--" + endDate, jishi_title.getText().toString().trim(), "red");
				} else {
					updateNote(startDate + "--..--" + endDate, jishi_title.getText().toString());
				}
			}
			break;
		}
	}

	private void saveNote(final String content, final String title, final String color) {
		Util.asynTask(this, "正在保存记事信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {

				sendBroadcast(new Intent("com.mall.note.ImportantNoteList"));
				String result = (String) runData;
				if (result.contains("success")) {
					Toast.makeText(AddOneNote.this, "保存成功", Toast.LENGTH_LONG).show();
					if (state == 1) {
						String string = add_tixing1.getText().toString().trim().replace("提醒时间：", "");
						String datetime = string.trim().split(" ")[0];
						String time = string.trim().split(" ")[1];
						onSetDate(Integer.parseInt(datetime.split("-")[0]),
								Integer.parseInt(datetime.split("-")[1]) - 1, Integer.parseInt(datetime.split("-")[2]));
						onSetTime(Integer.parseInt(time.split(":")[0]), Integer.parseInt(time.split(":")[1]));
						ClockUtils.noteItem = noteItem;
						Intent intent = new Intent(AddOneNote.this, NoteEditor.class);
						intent.putExtra("count", title);
						intent.putExtra("title", content);
						intent.putExtra(NoteEditor.OPEN_TYPE, NoteEditor.TYPE_NEW_NOTE);
						startActivity(intent);
					} else if (state == 0) {
						// Util.showIntent(AddOneNote.this,
						// NoteMainFrame.class);
						// Util.showIntent(AddOneNote.this,Lin_MainFrame.class,new
						// String[]{"toTab"},new String[]{"note"});
					}
					finish();
				} else {
					Toast.makeText(AddOneNote.this, "保存失败", Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public Serializable run() {

				Date date = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
				String litiletitle = "";
				if (title.length() > 20) {
					litiletitle = title.substring(0, 20);
				} else {
					litiletitle = title;
				}

				Web web = new Web(Web.allianService, Web.addImportantNotes,
						"userId=" + Util.get(userId) + "&md5Pwd=" + md5Pwd + "&title=" + Util.get(content) + "&content="
								+ Util.get(title) + "&publishtime" + "&color=" + color + "&lmsj=mall");
				String s = web.getPlan();
				return s;
			}

		});
	}

	private void getTime(TextView textView, String message) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		MyDateTimePickerDialog dateTime = new MyDateTimePickerDialog(this, dialog, width, height, textView, 1, message,
				handler);
		dateTime.getTime();
	}

	class PopupOnClick implements OnClickListener {
		TextView textView;
		TextView textView2;
		String changeTime;

		public PopupOnClick(TextView textView, TextView textView2, String time) {
			// TODO Auto-generated constructor stub
			this.textView = textView;
			this.textView2 = textView2;
			this.changeTime = time;
		}

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			changeBackc(change1, textView2);
			changeTextc(change, textView);
			Util.showIntent(AddOneNote.this, com.way.note.NoteActivity.class);
		}

	}

	private void changeTextc(TextView[] textViews, TextView textView) {
		for (int i = 0; i < textViews.length; i++) {
			textViews[i].setTextColor(getResources().getColor(R.color.js_tx_zi1));
		}
		textView.setTextColor(getResources().getColor(R.color.js_tx_zi2));
	}

	private void changeBackc(TextView[] textViews, TextView textView) {
		for (int i = 0; i < textViews.length; i++) {
			textViews[i].setBackgroundColor(getResources().getColor(R.color.js_bcak1));
		}
		textView.setBackgroundColor(getResources().getColor(R.color.js_tx_zi2));
	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}

	/**
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view, android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimation1);
	}

	private void updateNote(final String content, final String title) {
		Util.asynTask(AddOneNote.this, "正在保存记事信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				sendBroadcast(new Intent("com.mall.note.ImportantNoteList"));
				String result = (String) runData;
				if (result.equals("success")) {
					Toast.makeText(AddOneNote.this, "修改成功", Toast.LENGTH_LONG).show();
					if (state == 1) {
						String string = add_tixing1.getText().toString().trim().replace("提醒时间：", "");
						String datetime = string.trim().split(" ")[0];
						String time = string.trim().split(" ")[1];
						onSetDate(Integer.parseInt(datetime.split("-")[0]),
								Integer.parseInt(datetime.split("-")[1]) - 1, Integer.parseInt(datetime.split("-")[2]));
						onSetTime(Integer.parseInt(time.split(":")[0]), Integer.parseInt(time.split(":")[1]));
						ClockUtils.noteItem = noteItem;
						Intent intent = new Intent(AddOneNote.this, NoteEditor.class);
						intent.putExtra("count", title);
						intent.putExtra("title", content);
						intent.putExtra(NoteEditor.OPEN_TYPE, NoteEditor.TYPE_NEW_NOTE);
						startActivity(intent);
					} else if (state == 0) {
						// Util.showIntent(AddOneNote.this,Lin_MainFrame.class,new
						// String[]{"toTab"},new String[]{"note"});
					}
					finish();
				} else {
					Toast.makeText(AddOneNote.this, "修改失败", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.allianService, Web.updateImportantNotes,
						"userId=" + Util.get(userId) + "&md5Pwd=" + md5Pwd + "&title=" + Util.get(content) + "&content="
								+ Util.get(title) + "&publishtime" + "&color=" + "" + "&id=" + jishiId + "&lmsj=mall");
				String s = web.getPlan();
				return s;
			}

		});
	}

	public void setRings() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
				// R.string.set_alarm_ringtone);
				getResources().getString(R.string.set_alarm_ringtone));

		// Allow user to pick 'Default'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		// show 'Silent'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);

		String urlStr = null;
		Uri ringtoneUri = null;
		if (urlStr == null) { // silent
			ringtoneUri = null;
		}
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);
		this.startActivityForResult(intent, 1);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == 1) {
			try {
				Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (uri != null) {
					noteItem.clockItem.ringtoneUrl = uri.toString();
					Log.v("you", "rings_uri= " + uri.toString() + " **********************************");
					if (RingtoneManager.isDefault(uri)) {
						noteItem.clockItem.ringtoneName = RingtoneItem.DEFAULT_RINGTONE;
					} else {
						Cursor cursor = this.getContentResolver().query(uri, ClockUtils.CURSOR_COLS, null, null, null);
						cursor.moveToFirst();
						String rings_name = cursor
								.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
						cursor.close();
						Log.v("you", "rings_name= " + rings_name);
						noteItem.clockItem.ringtoneName = rings_name.substring(0, rings_name.lastIndexOf("."));
					}
				} else {
					noteItem.clockItem.ringtoneName = getResources().getString(R.string.silent);
					noteItem.clockItem.ringtoneUrl = null;
				}
				xuanze_lings.setText("铃声：" + noteItem.clockItem.ringtoneName);
				/*
				 * saveChange(); updateUI();
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
				Toast.makeText(getApplicationContext(), R.string.alarm_time_error, Toast.LENGTH_LONG).show();
				return;
			}
		}

		String oldDate = noteItem.clockItem.ringtoneDate;
		noteItem.clockItem.ringtoneDate = year + "-" + format(monthOfYear + 1) + "-" + format(dayOfMonth);

		/*
		 * if (noteItem.alarmEnable) { if (setAlarm()) { updateUI();
		 * saveChange(); } else { noteItem.clockItem.ringtoneDate = oldDate; } }
		 * else { }
		 */
	}

	public String format(int i) {
		String s = "" + i;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}

	private Calendar praseTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		try {
			Date settingDate = dateFormat.parse(noteItem.clockItem.ringtoneTime);
			Calendar settingCal = Calendar.getInstance();
			settingCal.setTime(settingDate);
			return settingCal;
		} catch (ParseException e) {
			// Log.e(TAG, "setTime fail!!!");
		}

		return null;
	}

	public void onSetTime(int hourOfDay, int minute) {
		Calendar calendar = praseDate();
		if (calendar != null) {
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
				Toast.makeText(getApplicationContext(), R.string.alarm_time_error, Toast.LENGTH_LONG).show();
				return;
			}
		}

		String oldTime = noteItem.clockItem.ringtoneTime;
		noteItem.clockItem.ringtoneTime = format(hourOfDay) + ":" + format(minute);

		/*
		 * if (noteItem.alarmEnable) { if (setAlarm()) { updateUI();
		 * saveChange(); } else { noteItem.clockItem.ringtoneTime = oldTime; } }
		 * else { updateUI(); saveChange(); }
		 */
	}

	private Calendar praseDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date settingDate = dateFormat.parse(noteItem.clockItem.ringtoneDate);
			Calendar settingCal = Calendar.getInstance();
			settingCal.setTime(settingDate);
			return settingCal;
		} catch (ParseException e) {
			// Log.e(TAG, "setDate fail!!!");
		}

		return null;
	}
}
