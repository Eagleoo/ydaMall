package com.mall.happlylot;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.YdAlainMall.util.time.MyDateTimePickerDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * 查询福分
 * 
 * @author admin
 * 
 */
public class ReferHapplyLot extends Activity {
	@ViewInject(R.id.start_time)
	private EditText start_time;
	@ViewInject(R.id.end_time)
	private EditText end_time;
	@ViewInject(R.id.listView1)
	private ListView listView;
	private Dialog dialog;
	@ViewInject(R.id.linn1)
	private LinearLayout linn1;
	private int state = 0;
	private int currentPageShop = 0;
	private HapplyLotAdapter happlyLotAdapter;
	private List<HapplyLot> happlyLots = new ArrayList<HapplyLot>();
	private List<TextView> textViews=new ArrayList<TextView>();
	private PopupWindow distancePopup = null;
	@ViewInject(R.id.lin2)
	private LinearLayout lin2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refer_happly_lot);
		ViewUtils.inject(this);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		start_time.setText(sdf.format(date));
		end_time.setText(sdf.format(date));
		getFufen("");
	}
public void donghua(){
	Animation anim = AnimationUtils.loadAnimation(this,
			R.anim.gallery_in);
	lin2.startAnimation(anim);
}
	private void chaxun( Date starttime, Date endtime) {
		try {
			List<HapplyLot> list = new ArrayList<HapplyLot>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < happlyLots.size(); i++) {
				Date thistime = sdf.parse(happlyLots.get(i).getDate()
						.replace("/", "-").split(" ")[0]);
					if (thistime.getTime() >= starttime.getTime()
							&& thistime.getTime() <= endtime.getTime()) {
						list.add(happlyLots.get(i));
					}
			}
			happlyLotAdapter = new HapplyLotAdapter(list);
			listView.setAdapter(happlyLotAdapter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void firstpageshop() {
		getFufen("");
	}
	public void scrollPageshop() {
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= happlyLotAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getFufen("");
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	@OnClick(R.id.top_back)
	public void topback(View view) {
		//finish();
		getPopupWindow();
		startPopupWindow();
		distancePopup.showAsDropDown(linn1);
	}

	@OnClick({ R.id.start_time, R.id.end_time, R.id.chaxun })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.chaxun:
			donghua();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date startdate = sdf.parse(start_time.getText().toString()
						.trim());
				Date enddate = sdf.parse(end_time.getText().toString().trim());
				if (enddate.getTime() >= startdate.getTime()) {

				} else {
					start_time.setText(end_time.getText().toString().trim());
				}
				startdate = sdf.parse(start_time.getText().toString()
						.trim());
				 enddate = sdf.parse(end_time.getText().toString().trim());
				chaxun( startdate, enddate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		case R.id.start_time:
			state = 0;
			getTime(start_time);
			break;
		case R.id.end_time:
			state = 1;
			getTime(end_time);
			break;
		}
	}
	/**
	 * 获得福分记录
	 * 
	 * @param i
	 */

	private void startPopupWindow() {
		View pview = getLayoutInflater().inflate(
				R.layout.my_happly_lot_message, null, false);
		ImageView user_img=(ImageView) pview.findViewById(R.id.user_img);
		TextView user_name=(TextView) pview.findViewById(R.id.user_name);
		TextView ruzhi_time=(TextView) pview.findViewById(R.id.ruzhi_time);
		TextView start_dep=(TextView) pview.findViewById(R.id.start_dep);
		TextView now_dep=(TextView) pview.findViewById(R.id.now_dep);
		TextView start_role=(TextView) pview.findViewById(R.id.start_role);
		TextView job_time=(TextView) pview.findViewById(R.id.job_time);
		TextView in_job=(TextView) pview.findViewById(R.id.in_job);
		TextView now_fufen=(TextView) pview.findViewById(R.id.now_fufen);
		
		TextView b_jiangli=(TextView) pview.findViewById(R.id.b_jiangli);
		TextView lj_jiangli=(TextView) pview.findViewById(R.id.lj_jiangli);
		TextView now_leiji=(TextView) pview.findViewById(R.id.now_leiji);
		TextView role_tiaoz=(TextView) pview.findViewById(R.id.role_tiaoz);
		
		TextView dep_paiming=(TextView) pview.findViewById(R.id.dep_paiming);
		TextView gs_paiming=(TextView) pview.findViewById(R.id.gs_paiming);
		TextView top_back=(TextView) pview.findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				distancePopup.dismiss();
			}
		});
		initpoputwindow(pview);
	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}

	/**
	 * 获得福分记录
	 * 
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		// distancePopup.setAnimationStyle(R.style.popupanimation);
	}
	/*
	 * 获得福分记录
	 */
	private void getFufen(final String typeid) {
		final User user =UserData.getUser();
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<HapplyLot> list = new ArrayList<HapplyLot>();
					list = ((HashMap<Integer, List<HapplyLot>>) runData)
							.get(1);
					if (list.size() > 0) {
						happlyLots.addAll(list);
						listView.setAdapter(new HapplyLotAdapter(list));
						
					} else {
						Toast.makeText(ReferHapplyLot.this, "没有更多福分信息",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(ReferHapplyLot.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.staffManager_service,
						Web.getStaffUserBlessingByUserId,  "userId="
								+ Util.get(user.getUserId()) + "&md5Pwd="
								+ user.getMd5Pwd()+"&pagesize=999999999" + "&curpage="
						+ (1)+"&typeid="+typeid);
				List<HapplyLot> list = web.getList(HapplyLot.class);
				HashMap<Integer, List<HapplyLot>> map = new HashMap<Integer, List<HapplyLot>>();
				map.put(1, list);
				return map;
			}
			
		});
	}

	private void getTime(EditText editText) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		MyDateTimePickerDialog dateTime = new MyDateTimePickerDialog(this,
				dialog, width, height, editText);
		dateTime.getTime();

	}

	public class HapplyLotAdapter extends BaseAdapter {
		private List<HapplyLot> list = new ArrayList<HapplyLot>();
		LayoutInflater inflater;

		public HapplyLotAdapter() {
			inflater = LayoutInflater.from(ReferHapplyLot.this);
		}

		public HapplyLotAdapter(List<HapplyLot> list) {
			this.list = list;
			inflater = LayoutInflater.from(ReferHapplyLot.this);
		}

		public void setList(List<HapplyLot> list) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			if (view == null) {
				view = inflater.inflate(R.layout.happly_lot_item, null);
			}
			final TextView name = (TextView) view.findViewById(R.id.name);
			TextView jiangli_fen = (TextView) view
					.findViewById(R.id.jiangli_fen);
			TextView now_fen = (TextView) view.findViewById(R.id.now_fen);
			TextView shijian = (TextView) view.findViewById(R.id.shijian);
			LinearLayout lin1 = (LinearLayout) view.findViewById(R.id.lin1);
			DecimalFormat df = new DecimalFormat("#.00");
			now_fen.setText(df.format(Double.parseDouble(list.get(position)
					.getBalance())));
			shijian.setText(list.get(position).getDate().replace("/", "-")
					.split(" ")[0]);
			jiangli_fen.setText(df.format(Double.parseDouble(list.get(position)
					.getIncome())));
			if (TextUtils.isEmpty(list.get(position).getDetail())) {
				name.setText("");
			}else {
				String[] names=list.get(position).getDetail().split("..--..");
				if (names.length>1) {
					name.setText(names[1]);
				}else {
					name.setText("");
				}
			}
			if (list.size() % 2 == 0) {
				lin1.setBackgroundColor(getResources().getColor(R.color.bg));
			} else {
				lin1.setBackgroundColor(getResources().getColor(
						R.color.fufen_beijing1));
			}
			return view;
		}
	}
}
