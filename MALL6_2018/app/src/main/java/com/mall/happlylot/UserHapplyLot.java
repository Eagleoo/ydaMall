package com.mall.happlylot;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
 * 我的福分列表
 * @author admin
 *
 */
public class UserHapplyLot extends Activity {
	@ViewInject(R.id.listView1)
	private ListView listView;
	@ViewInject(R.id.heji_fen)
	private TextView heji_fen;
	private List<HapplyLot> happlyLots=new ArrayList<HapplyLot>();
	private UserHapplyAdapter userHapplyAdapter;
	private int currentPageShop = 0;
	@ViewInject(R.id.start_time)
	private EditText start_time;
	@ViewInject(R.id.end_time)
	private EditText end_time;
	@ViewInject(R.id.lin2)
	private LinearLayout lin2;
	private int state = 0;
	private Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.happly_lot_one_people);
		ViewUtils.inject(this);
		getFufen("");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		start_time.setText(sdf.format(date));
		end_time.setText(sdf.format(date));
	}
	public void donghua(){
		Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.gallery_in);
		lin2.startAnimation(anim);
	}
	@OnClick(R.id.top_back)
	public void onclick(View view){
		finish();
	}
	@OnClick(R.id.sousuo)
	public void sousuo(View view){
		Util.showIntent(this, ReferHapplyLot.class);
	}
	public void firstpageshop() {
		getFufen("");
	}
	@OnClick({R.id.start_time, R.id.end_time})
	public void shijian(View view){
		switch (view.getId()) {
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
	private void getTime(EditText editText) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		MyDateTimePickerDialog dateTime = new MyDateTimePickerDialog(this,
				dialog, width, height, editText);
		dateTime.getTime();

	}
	@OnClick( R.id.chaxun)
	public void onclick1(View view){
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
	}
	@OnClick(R.id.chaxun1)
	public void chaxun(View view){
		lin2.setVisibility(View.VISIBLE);
		donghua();
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
			if (list.size()>0) {
				heji_fen.setText((int)Double.parseDouble(list.get(list.size()-1).getBalance())+"");
			}else {
				heji_fen.setText(0+"");
			}
			
			userHapplyAdapter = new UserHapplyAdapter(list);
			listView.setAdapter(userHapplyAdapter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public void scrollPageshop() {
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= userHapplyAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
				getFufen("");
			}
		});
	}
	/*
	 * 获得福分记录
	 */
	private void getFufen(final String typeid) {
		final User user = UserData.getUser();
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
						listView.setAdapter(new UserHapplyAdapter(list));
						heji_fen.setText((int)Double.parseDouble(list.get(list.size()-1).getBalance())+"");
					} else {
						Toast.makeText(UserHapplyLot.this, "没有更多福分信息",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(UserHapplyLot.this,
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
								+ user.getMd5Pwd()+"&pagesize=99999999" + "&curpage=1"
						+"&typeid="+typeid);
				List<HapplyLot> list = web.getList(HapplyLot.class);
				HashMap<Integer, List<HapplyLot>> map = new HashMap<Integer, List<HapplyLot>>();
				map.put(1, list);
				return map;
			}

		});
	}
	public class UserHapplyAdapter extends BaseAdapter{
		private  LayoutInflater inflater;
		private List<HapplyLot> list=new ArrayList<HapplyLot>();
		public UserHapplyAdapter(){
			inflater=LayoutInflater.from(UserHapplyLot.this);
		}
		public UserHapplyAdapter(List<HapplyLot> list){
			this.list=list;
			inflater=LayoutInflater.from(UserHapplyLot.this);
		}
		public void setList(List<HapplyLot> list){
			this.notifyDataSetChanged();
			this.list.addAll(list);
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
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int ponsition, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (view==null) {
				view=inflater.inflate(R.layout.happly_lot_item, null);
			}
			TextView name=(TextView) view.findViewById(R.id.name);
			TextView jiangli_fen=(TextView) view.findViewById(R.id.jiangli_fen);
			TextView now_fen=(TextView) view.findViewById(R.id.now_fen);
			TextView shijian=(TextView) view.findViewById(R.id.shijian);
			jiangli_fen.setText((int)Double.parseDouble(list.get(ponsition).getIncome())+"");
			now_fen.setText((int)Double.parseDouble(list.get(ponsition).getBalance())+"");
			shijian.setText(list.get(ponsition).getDate().split(" ")[0]);
			/*if (TextUtils.isEmpty(list.get(ponsition).getDetail())) {
				name.setText("");
			}else {
				String[] names=list.get(ponsition).getDetail().split("..--..");
				if (names.length>1) {
					name.setText(names[1]);
				}else {
					name.setText("");
				}
			}*/
			name.setText(list.get(ponsition).getTypename());
			return view;
			
		}
		
	}
}
