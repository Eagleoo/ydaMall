package com.easier.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easier.adapter.CalendarGridView;
import com.easier.adapter.CalendarGridViewAdapter;
import com.easier.util.CalendarUtil;
import com.easier.util.NumberHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.model.NoteModel;
import com.mall.model.UserInfo;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.note.AddOneNote;
import com.mall.serving.community.view.listview.ListViewForScrollView;
import com.mall.serving.community.view.progresswheel.RoundProgressBar;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

/**
 * 
 * 
 * @author ZhuZiQiang
 * @data: 2014-3-29 下午2:59:00
 * @version: V1.0
 */
public class CalendarView extends Activity implements OnTouchListener {
	@ViewInject(R.id.roundProgressBar)
	private RoundProgressBar roundProgressBar;
	@ViewInject(R.id.pro_text)
	private TextView pro_text;
	/**
	 * 日历布局ID
	 */
	private static final int CAL_LAYOUT_ID = 55;
	// 判断手势用
	private static final int SWIPE_MIN_DISTANCE = 120;

	private static final int SWIPE_MAX_OFF_PATH = 250;

	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	/**
	 * 用于传递选中的日期
	 */
	private static final String MESSAGE = "msg";

	// 动画
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private ViewFlipper viewFlipper;
	GestureDetector mGesture = null;
	private String md5Pwd = "";
	private String userId = "";
	private CaNoteAdapter adapter;
	private ListViewForScrollView listView;
	/**
	 * 今天按钮
	 */
	private ImageView mTodayBtn;

	/**
	 * 上一个月按钮
	 */
	private ImageView mPreMonthImg;

	/**
	 * 下一个月按钮
	 */
	private ImageView mNextMonthImg;

	/**
	 * 用于显示今天的日期
	 */
	private TextView mDayMessage;

	/**
	 * 用于装截日历的View
	 */
	private RelativeLayout mCalendarMainLayout;

	// 基本变量
	private Context mContext = CalendarView.this;
	/**
	 * 上一个月View
	 */
	private GridView firstGridView;

	/**
	 * 当前月View
	 */
	private GridView currentGridView;

	/**
	 * 下一个月View
	 */
	private GridView lastGridView;

	/**
	 * 当前显示的日历
	 */
	private Calendar calStartDate = Calendar.getInstance();

	/**
	 * 选择的日历
	 */
	private Calendar calSelected = Calendar.getInstance();

	/**
	 * 今日
	 */
	private Calendar calToday = Calendar.getInstance();

	/**
	 * 当前界面展示的数据源
	 */
	private CalendarGridViewAdapter currentGridAdapter;

	/**
	 * 预装载上一个月展示的数据源
	 */
	private CalendarGridViewAdapter firstGridAdapter;

	/**
	 * 预装截下一个月展示的数据源
	 */
	private CalendarGridViewAdapter lastGridAdapter;

	//
	/**
	 * 当前视图月
	 */
	private int mMonthViewCurrentMonth = 0;

	/**
	 * 当前视图年
	 */
	private int mMonthViewCurrentYear = 0;

	/**
	 * 起始周
	 */
	private int iFirstDayOfWeek = Calendar.MONDAY;
	private List<NoteModel> models = new ArrayList<NoteModel>();
	private String chageTime = "";
	String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGesture.onTouchEvent(event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("生命周期", "CalendarView 结束" );
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.js_calendar_main);
		initView();
		ViewUtils.inject(this);
		// 验证用户是否登录
		setTopTime();
		updateStartDateForMonth();
		ImageView add_js = (ImageView) findViewById(R.id.add_js);
		add_js.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CalendarView.this, AddOneNote.class);
				intent.putExtra("time", chageTime);
				startActivity(intent);
			}
		});
		generateContetView(mCalendarMainLayout);
		slideLeftIn = AnimationUtils.loadAnimation(this,
				R.anim.js_slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(this,
				R.anim.js_slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(this,
				R.anim.js_slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(this,
				R.anim.js_slide_right_out);

		slideLeftIn.setAnimationListener(animationListener);
		slideLeftOut.setAnimationListener(animationListener);
		slideRightIn.setAnimationListener(animationListener);
		slideRightOut.setAnimationListener(animationListener);

		mGesture = new GestureDetector(this, new GestureListener());
	}
	private void setTopTime() {
		int w = calStartDate.get(Calendar.DAY_OF_WEEK)-1;
		if (w < 0){
			w = 0;
		}
		int nian = calStartDate.get(Calendar.YEAR);
		int yue = calStartDate.get(Calendar.MONTH)+1;
		int ri = calStartDate.get(Calendar.DAY_OF_MONTH);
		pro_text.setText(""+weekDays[w]+"   "+nian+"-"+yue+"-"+ri);
		int year = calStartDate.get(Calendar.YEAR);

		if((year%4==0&&year%100!=0)||year%400==0){
			roundProgressBar.setProgress((int)((Integer.parseInt(NumberHelper.LeftPad_Tow_Zero(calStartDate.get(Calendar.MONTH)))*30.5+calStartDate.get(Calendar.DAY_OF_MONTH))/3.66));//设置进度
		} else{
			roundProgressBar.setProgress((int)((Integer.parseInt(NumberHelper.LeftPad_Tow_Zero(calStartDate.get(Calendar.MONTH)))*30.4166+calStartDate.get(Calendar.DAY_OF_MONTH))/3.65));//设置进度
		}
		
	}

	public void topbcak(View view) {
		finish();
	}

	/**
	 * 用于初始化控件
	 */
	private void initView() {
		mTodayBtn = (ImageView) findViewById(R.id.today_btn);
		mDayMessage = (TextView) findViewById(R.id.day_message);
		mCalendarMainLayout = (RelativeLayout) findViewById(R.id.calendar_main);
		mPreMonthImg = (ImageView) findViewById(R.id.left_img);
		mNextMonthImg = (ImageView) findViewById(R.id.right_img);
		listView = (ListViewForScrollView) findViewById(R.id.lv);
		mTodayBtn.setOnClickListener(onTodayClickListener);
		mPreMonthImg.setOnClickListener(onPreMonthClickListener);

		mNextMonthImg.setOnClickListener(onNextMonthClickListener);
		if (Util.checkLoginOrNot()) {
			UserInfo userInfo = new UserInfo();
			md5Pwd = UserData.getUser().getMd5Pwd();
			userId = UserData.getUser().getUserId();
		} else {
			Util.showIntent(this, LoginFrame.class);
		}
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		asyncloadData(simpleDateFormat.format(date));
		
	}

	/**
	 * 用于加载到当前的日期的事件
	 */
	private View.OnClickListener onTodayClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			calStartDate = Calendar.getInstance();
			calSelected = Calendar.getInstance();
			updateStartDateForMonth();
			generateContetView(mCalendarMainLayout);
		}
	};

	/**
	 * 用于加载上一个月日期的事件
	 */
	private View.OnClickListener onPreMonthClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			viewFlipper.setInAnimation(slideRightIn);
			viewFlipper.setOutAnimation(slideRightOut);
			viewFlipper.showPrevious();
			setPrevViewItem();
		}
	};

	/**
	 * 用于加载下一个月日期的事件
	 */
	private View.OnClickListener onNextMonthClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			viewFlipper.setInAnimation(slideLeftIn);
			viewFlipper.setOutAnimation(slideLeftOut);
			viewFlipper.showNext();
			setNextViewItem();
		}
	};

	/**
	 * 主要用于生成发前展示的日历View
	 * 
	 * @param layout
	 *            将要用于去加载的布局
	 */
	private void generateContetView(RelativeLayout layout) {
		// 创建一个垂直的线性布局（整体内容）
		viewFlipper = new ViewFlipper(this);
		viewFlipper.setId(CAL_LAYOUT_ID);
		calStartDate = getCalendarStartDate();
		CreateGirdView();
		RelativeLayout.LayoutParams params_cal = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layout.addView(viewFlipper, params_cal);

		LinearLayout br = new LinearLayout(this);
		RelativeLayout.LayoutParams params_br = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 1);
		params_br.addRule(RelativeLayout.BELOW, CAL_LAYOUT_ID);
		br.setBackgroundColor(getResources().getColor(
				R.color.bg));
		layout.addView(br, params_br);
	}

	/**
	 * 用于创建当前将要用于展示的View
	 */
	private void CreateGirdView() {

		Calendar firstCalendar = Calendar.getInstance(); // 临时  实例化对象
		Calendar currentCalendar = Calendar.getInstance(); // 临时
		Calendar lastCalendar = Calendar.getInstance(); // 临时
		firstCalendar.setTime(calStartDate.getTime());
		currentCalendar.setTime(calStartDate.getTime());
		lastCalendar.setTime(calStartDate.getTime());

		firstGridView = new CalendarGridView(mContext);
		firstCalendar.add(Calendar.MONTH, -1);
		firstGridAdapter = new CalendarGridViewAdapter(this, firstCalendar,false);
		firstGridView.setAdapter(firstGridAdapter);// 设置菜单Adapter
		firstGridView.setId(CAL_LAYOUT_ID);

		currentGridView = new CalendarGridView(mContext);
		currentGridAdapter = new CalendarGridViewAdapter(this, currentCalendar,false);
		currentGridView.setAdapter(currentGridAdapter);// 设置菜单Adapter
		currentGridView.setId(CAL_LAYOUT_ID);

		lastGridView = new CalendarGridView(mContext);
		lastCalendar.add(Calendar.MONTH, 1);
		lastGridAdapter = new CalendarGridViewAdapter(this, lastCalendar,false);
		lastGridView.setAdapter(lastGridAdapter);// 设置菜单Adapter
		lastGridView.setId(CAL_LAYOUT_ID);

		currentGridView.setOnTouchListener(this);
		firstGridView.setOnTouchListener(this);
		lastGridView.setOnTouchListener(this);

		if (viewFlipper.getChildCount() != 0) {
			viewFlipper.removeAllViews();
		}

		viewFlipper.addView(currentGridView);
		viewFlipper.addView(lastGridView);
		viewFlipper.addView(firstGridView);

		String s = calStartDate.get(Calendar.YEAR)
				+ "-"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate
						.get(Calendar.MONTH) + 1);
		mDayMessage.setText(s);

	}

	/**
	 * 上一个月
	 */
	private void setPrevViewItem() {
		mMonthViewCurrentMonth--;// 当前选择月--
		// 如果当前月为负数的话显示上一年
		if (mMonthViewCurrentMonth == -1) {
			mMonthViewCurrentMonth = 11;
			mMonthViewCurrentYear--;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1); // 设置日为当月1日
		calStartDate.set(Calendar.MONTH, mMonthViewCurrentMonth); // 设置月
		calStartDate.set(Calendar.YEAR, mMonthViewCurrentYear); // 设置年

	}

	/**
	 * 下一个月
	 */
	private void setNextViewItem() {
		mMonthViewCurrentMonth++;
		if (mMonthViewCurrentMonth == 12) {
			mMonthViewCurrentMonth = 0;
			mMonthViewCurrentYear++;
		}
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.MONTH, mMonthViewCurrentMonth);
		calStartDate.set(Calendar.YEAR, mMonthViewCurrentYear);

	}

	/**
	 * 根据改变的日期更新日历 填充日历控件用
	 */
	private void updateStartDateForMonth() {
		calStartDate.set(Calendar.DATE, 1); // 设置成当月第一天
		mMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);// 得到当前日历显示的月
		mMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);// 得到当前日历显示的年

		String s = calStartDate.get(Calendar.YEAR)
				+ "-"
				+ NumberHelper.LeftPad_Tow_Zero(calStartDate
						.get(Calendar.MONTH) + 1);
		mDayMessage.setText(s);
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
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

	}

	/**
	 * 用于获取当前显示月份的时间
	 * 
	 * @return 当前显示月份的时间
	 */
	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}

		return calStartDate;
	}

	AnimationListener animationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// 当动画完成后调用
			CreateGirdView();
		}
	};

	class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setInAnimation(slideLeftIn);
					viewFlipper.setOutAnimation(slideLeftOut);
					viewFlipper.showNext();
					setNextViewItem();
					return true;

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					viewFlipper.setInAnimation(slideRightIn);
					viewFlipper.setOutAnimation(slideRightOut);
					viewFlipper.showPrevious();
					setPrevViewItem();
					return true;

				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// 得到当前选中的是第几个单元格
			int pos = currentGridView.pointToPosition((int) e.getX(),
					(int) e.getY());
			LinearLayout txtDay = (LinearLayout) currentGridView
					.findViewById(pos + 5000);
			if (txtDay != null) {
				if (txtDay.getTag() != null) {
					Date date = (Date) txtDay.getTag();
					if (CalendarUtil.compare(date, Calendar.getInstance()
							.getTime())) {
						calSelected.setTime(date);
						currentGridAdapter.setSelectedDate(calSelected);
						currentGridAdapter.notifyDataSetChanged();
						firstGridAdapter.setSelectedDate(calSelected);
						firstGridAdapter.notifyDataSetChanged();

						lastGridAdapter.setSelectedDate(calSelected);
						lastGridAdapter.notifyDataSetChanged();
						String week = CalendarUtil.getWeek(calSelected);
						String message = CalendarUtil.getDay(calSelected)
								+ " 农历"
								+ new CalendarUtil(calSelected).getDay() + " "
								+ week;
						/*
						 * Toast.makeText(getApplicationContext(), "您选择的日期为:" +
						 * message, Toast.LENGTH_SHORT) .show();
						 */
						asyncloadData(CalendarUtil.getDay(calSelected));
						chageTime = CalendarUtil.getDay(calSelected);
					} else {
						/*
						 * Toast.makeText(getApplicationContext(),
						 * "选择的日期不能小于今天的日期", Toast.LENGTH_SHORT).show();
						 */
					}
				}
			}

			Log.i("TEST", "onSingleTapUp -  pos=" + pos);

			return false;
		}
	}

	public void asyncloadData(final String times) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); // 获取东八区时间
		int year = c.get(Calendar.YEAR); // 获取年
		int month = c.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
		String mon = "";
		if (month < 10) {
			mon = "0" + month;
		} else {
			mon = month + "";
		}
		int day = c.get(Calendar.DAY_OF_MONTH); // 获取当前天数
		final String time = year + "-" + mon + "-" + day;

		final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "正在获取记事信息...");
		Map<String,String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("md5Pwd", md5Pwd);
		params.put("yearMonthDay", times);
		params.put("page", "1");
		params.put("size", "5000");
		NewWebAPI.getNewInstance().getWebRequest("/Note.aspx?call=getImportantNotes",params, new WebRequestCallBack(){
			@Override
			public void success(Object result) {
				super.success(result);
				if(Util.isNull(result)){
					Util.show("网络异常！", CalendarView.this);
					return ;
				}
				JSONObject json = JSON.parseObject(result.toString());
				if(200 != json.getIntValue("code")){
					Util.show(json.getString("message"), CalendarView.this);
					return ;
				}
				List<NoteModel> list = JSON.parseArray(json.getString("list"), NoteModel.class);
				List<NoteModel> noteModels = new ArrayList<NoteModel>();
				for (int i = 0; i < list.size(); i++) {
					String[] countTime = list.get(i).getPublishTime()
							.split(" ");
					if (countTime.length > 0) {
						if (countTime[0].equals(times)) {
							noteModels.add(list.get(i));
						}
					}
				}
				adapter = new CaNoteAdapter(CalendarView.this, noteModels);
				listView.setAdapter(adapter);
				if (noteModels.size() == 0) {
					Toast.makeText(CalendarView.this, "当天无记事",
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void requestEnd() {
				super.requestEnd();
				cpd.cancel();
				cpd.dismiss();
			}

		});


		//		Util.asynTask(CalendarView.this, "正在获取记事信息", new IAsynTask() {
		//
		//			@Override
		//			public void updateUI(Serializable runData) {
		//				HashMap<Integer, List<NoteModel>> map = (HashMap<Integer, List<NoteModel>>) runData;
		//				List<NoteModel> list = map.get(1);
		//				List<NoteModel> noteModels = new ArrayList<NoteModel>();
		//				for (int i = 0; i < list.size(); i++) {
		//					String[] countTime = list.get(i).getPublishTime()
		//							.split(" ");
		//					if (countTime.length > 0) {
		//						if (countTime[0].equals(times)) {
		//							noteModels.add(list.get(i));
		//						}
		//					}
		//				}
		//				adapter = new CaNoteAdapter(CalendarView.this, noteModels);
		//				listView.setAdapter(adapter);
		//				if (noteModels.size() == 0) {
		//					Toast.makeText(CalendarView.this, "当天无记事",
		//							Toast.LENGTH_SHORT).show();
		//				}
		//			}
		//
		//			@Override
		//			public Serializable run() {
		//				Web web = new Web(Web.allianService, Web.getImportantNotes,
		//						"userId=" + Util.get(userId) + "&md5Pwd=" + md5Pwd
		//								+ "&yearMonthDay=" + "&page=" + 1
		//								+ "&size=9999999" + "&lmsj=mall");
		//				List<NoteModel> list = web.getList(NoteModel.class);
		//				HashMap<Integer, List<NoteModel>> map = new HashMap<Integer, List<NoteModel>>();
		//				map.put(1, list);
		//				return map;
		//			}
		//
		//		});
	}

	class CaNoteAdapter extends BaseAdapter {
		private Context c;
		private LayoutInflater layoutInflater;
		private List<NoteModel> list = new ArrayList<NoteModel>();

		public CaNoteAdapter(Context c, List<NoteModel> list) {
			this.c = c;
			this.list = list;
			layoutInflater = LayoutInflater.from(c);
		}

		public void setList(List<NoteModel> list) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			NoteModel n = this.list.get(position);
			final ViewHolder holder;
			if (convertView == null) {
				convertView = (LinearLayout) layoutInflater.inflate(
						R.layout.all_note_list_item, null);
				holder = new ViewHolder();
				holder.t1 = (TextView) convertView.findViewById(R.id.title);
				holder.t2 = (TextView) convertView.findViewById(R.id.context);
				holder.open_or_close_Liner = (LinearLayout) convertView.findViewById(R.id.open_or_close_Liner);
				holder.open_or_close = (ImageView) convertView.findViewById(R.id.open_or_close);
				holder.content_Liner = (LinearLayout) convertView.findViewById(R.id.content_Liner);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.t1.setText(n.getPublishTime());
			final String[] string=n.getTitle().split("--..--");
			if (n.getTitle().indexOf("--..--")!=-1) {
				holder.t2.setText(n.getContent());	
			}else {
				holder.t2.setText(n.getTitle());
			}
			holder.open_or_close_Liner.setOnClickListener(new OnClickListener() {
				int tag=0;	
				@Override
				public void onClick(View v) {
					switch(tag){
					case 0://关
						holder.content_Liner.setVisibility(View.GONE);
						holder.open_or_close.setImageResource(R.drawable.note_rili_close);
						tag=1;
						break;
					case 1://开
						holder.content_Liner.setVisibility(View.VISIBLE);
						holder.open_or_close.setImageResource(R.drawable.note_rili_open);
						tag=0;
						break;
					}

				}
			});
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					// 将布局文件转化成view对象
					LayoutInflater inflaterDl = LayoutInflater
							.from(CalendarView.this);
					LinearLayout layout = (LinearLayout) inflaterDl.inflate(
							R.layout.tuichu_lmsj_dialog, null);
					final Dialog dialog=new Dialog(CalendarView.this.getParent(), R.style.CustomDialogStyle);
					DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					int width = dm.widthPixels;
					dialog.show();
					WindowManager.LayoutParams params = dialog.getWindow()
							.getAttributes();
					params.width = width * 4 / 5;
					dialog.getWindow().setAttributes(params);
					dialog.getWindow().setContentView(layout);
					TextView update_count = (TextView) layout
							.findViewById(R.id.update_count);
					TextView yihou_update = (TextView) layout
							.findViewById(R.id.yihou_update);
					TextView now_update = (TextView) layout
							.findViewById(R.id.now_update);
					update_count.setText("确定要删除该条记事？");
					yihou_update.setText("取\t\t消");
					now_update.setText("确\t\t定");
					yihou_update.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					now_update.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// deleteData(list.get(position).getId());
							dialog.dismiss();
						}
					});
					dialog.setCanceledOnTouchOutside(false);
					return false;
				}
			});
			return convertView;
		}
	}

	public class ViewHolder {
		private TextView t1;
		private TextView t2;
		private LinearLayout content_Liner;
		private LinearLayout open_or_close_Liner;
		private ImageView open_or_close;

	}
}
