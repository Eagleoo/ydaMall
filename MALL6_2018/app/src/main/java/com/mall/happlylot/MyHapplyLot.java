package com.mall.happlylot;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

public class MyHapplyLot extends Activity{
	@ViewInject(R.id.user_img)
	private ImageView user_img;
	@ViewInject(R.id.user_name)
	private TextView user_name;
	@ViewInject(R.id.ruzhi_time)
	private TextView ruzhi_time;
	@ViewInject(R.id.start_dep)
	private TextView start_dep;
	@ViewInject(R.id.now_dep)
	private TextView now_dep;
	@ViewInject(R.id.start_role)
	private TextView start_role;
	@ViewInject(R.id.job_time)
	private TextView job_time;
	@ViewInject(R.id.in_job)
	private TextView in_job;
	@ViewInject(R.id.now_fufen)
	private TextView now_fufen;
	@ViewInject(R.id.b_jiangli)
	private TextView b_jiangli;
	@ViewInject(R.id.lj_jiangli)
	private TextView lj_jiangli;
	@ViewInject(R.id.now_leiji)
	private TextView now_leiji;
	@ViewInject(R.id.role_tiaoz)
	private TextView role_tiaoz;
	@ViewInject(R.id.dep_paiming)
	private TextView dep_paiming;
	@ViewInject(R.id.gs_paiming)
	private TextView gs_paiming;
	private BitmapUtils bmUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_happly_lot_message);
		ViewUtils.inject(this);
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		userMessage();
		getFufen("");
		getUserBlessing();
	}
	private void setImage(final ImageView logo, String href, final int width,
			final int height) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				arg2 = Util.zoomBitmap(arg2, width, height);
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}
	@OnClick({R.id.sousuo,R.id.top_back,R.id.what_is_fufen})
	public void onclick(View view){
		switch (view.getId()) {
		case R.id.sousuo:
			Util.showIntent(this, UserHapplyLot.class);
			break;

		case R.id.top_back:
			finish();
			break;
		case R.id.what_is_fufen:
			showdWhatFufen();
			break;
		}
	}

	public void userMessage(){
		final User user =UserData.getUser();
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<HapplyUser> list = new ArrayList<HapplyUser>();
					list = ((HashMap<Integer, List<HapplyUser>>) runData)
							.get(1);
					if (list.size() > 0) {
						HapplyUser happlyUser=list.get(0);
						user_name.setText(user_name.getText().toString().trim()+happlyUser.getRealName());
						String[] a = happlyUser.getInWorkDate().split(" ");
						ruzhi_time.setText(ruzhi_time.getText().toString().trim()+a[0]);
						now_dep.setText(now_dep.getText().toString().trim()+happlyUser.getJob());
						Date date=new Date();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
						int nowYear=Integer.parseInt(sdf.format(date).split("-")[0]);
						int inYear=Integer.parseInt(happlyUser.getInWorkDate().split(" ")[0].split("/")[0]);
						String time=""; 
						if (nowYear>inYear) {
							time=nowYear-inYear+"年";
						}else {
							time="不足1年";
						}
						DisplayMetrics dm=new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dm);
						setImage(user_img, happlyUser.getUserPhoto().replace("%3A", ":").replace("%2F", "/"), dm.widthPixels/4, dm.heightPixels/5);
						job_time.setText(job_time.getText().toString().trim()+time);
					} else {
						Toast.makeText(MyHapplyLot.this, "没有更多福分信息",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(MyHapplyLot.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.staffManager_service,
						Web.getStaffUserByUserid,  "userId="
								+ Util.get(user.getUserId()) + "&md5Pwd="
								+ user.getMd5Pwd());
				List<HapplyUser> list = web.getList(HapplyUser.class);
				HashMap<Integer, List<HapplyUser>> map = new HashMap<Integer, List<HapplyUser>>();
				map.put(1, list);
				return map;
			}

		});
	}

	/*
	 * 获得福分记录
	 */
	private void getFufen(final String typeid) {
		final User user = UserData.getUser();
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<HapplyLot> list = new ArrayList<HapplyLot>();
					list = ((HashMap<Integer, List<HapplyLot>>) runData)
							.get(1);
					if (list.size() > 0) {
						Date date=new Date();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
						String thisYear=sdf.format(date);
						double jiangli=0.0d;
						double leiji=0.0d;
						//本年度福分奖励
						for (int i = 0; i < list.size(); i++) {
							String time=list.get(i).getDate().split("/")[0];
							leiji=leiji+Double.parseDouble(list.get(i).getIncome());
							if (time.equals(thisYear)) {
								jiangli=jiangli+Double.parseDouble(list.get(i).getIncome());
							}
						}
						b_jiangli.setText((int)jiangli+"");
						lj_jiangli.setText(""+(int)leiji);
						now_leiji.setText((int)Double.parseDouble(list.get(list.size()-1).getBalance())+"");
					} else {
						Toast.makeText(MyHapplyLot.this, "没有更多福分信息",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(MyHapplyLot.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.staffManager_service,
						Web.getStaffUserBlessingByUserId,  "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd()+"&pagesize=99999999" + "&curpage=1"
								+"&typeid="+typeid);
				List<HapplyLot> list = web.getList(HapplyLot.class);
				HashMap<Integer, List<HapplyLot>> map = new HashMap<Integer, List<HapplyLot>>();
				map.put(1, list);
				return map;
			}

		});
	}
	public void getUserBlessing(){
		final User user = UserData.getUser();
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<UserBlessingTop> list = new ArrayList<UserBlessingTop>();
					list = ((HashMap<Integer, List<UserBlessingTop>>) runData)
							.get(1);
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getJoinuserid().equals(user.getUserId())) {
								dep_paiming.setText(list.get(i).getGrouptop());
								gs_paiming.setText(list.get(i).getRownum());
								now_fufen.setText((int)Double.parseDouble(list.get(i).getBlessing())+"");
								in_job.setText((int)Double.parseDouble(list.get(i).getBlessing())+"");
								break;
							}
						}
						//						Date date=new Date();
						//						SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
						//						String thisYear=sdf.format(date);
						//						double jiangli=0.0d;
						//						double leiji=0.0d;
						//						//本年度福分奖励
						//						for (int i = 0; i < list.size(); i++) {
						//							String time=list.get(i).getDate().split("/")[0];
						//							leiji=leiji+Double.parseDouble(list.get(i).getIncome());
						//							if (time.equals(thisYear)) {
						//								jiangli=jiangli+Double.parseDouble(list.get(i).getIncome());
						//							}
						//						}
						//						b_jiangli.setText((int)jiangli+"");
						//						lj_jiangli.setText(""+leiji);
						//						now_leiji.setText((int)Double.parseDouble(list.get(list.size()-1).getBalance())+"");
					} else {
						Toast.makeText(MyHapplyLot.this, "没有更多福分信息",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(MyHapplyLot.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.staffManager_service,
						Web.getStaffUserBlessingTop,  "zyuserid="
								+ Util.get(user.getUserId()) + "&md5Pwd="
								+ user.getMd5Pwd() + "&typeid="
								+"&userId="+"");
				List<UserBlessingTop> list = web.getList(UserBlessingTop.class);
				HashMap<Integer, List<UserBlessingTop>> map = new HashMap<Integer, List<UserBlessingTop>>();
				map.put(1, list);
				return map;
			}

		});
	}
	@SuppressLint("NewApi")
	private void showdWhatFufen() {
		final AlertDialog alert = new  AlertDialog.Builder(this,R.style.dialog).create();
		View view = LayoutInflater.from(this).inflate(R.layout.what_is_fufen,null);
		view.findViewById(R.id.connot_yzm_but).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alert.dismiss();

			}
		});
		alert.show();
		WindowManager.LayoutParams parms = alert.getWindow()
				.getAttributes();
		int width = alert.getWindow().getWindowManager()
				.getDefaultDisplay().getWidth() * 9 / 10;
		int height = width / 4 * 7;
		parms.height = height;
		parms.width = width;
		alert.getWindow().setAttributes(parms);
		alert.getWindow().setContentView(view);
	}
}
