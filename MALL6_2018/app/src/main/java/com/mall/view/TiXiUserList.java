package com.mall.view;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.YdAlainMall.util.time.MyDateTimePickerDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.card.bean.CallBackListener;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.yyrg.adapter.TiXiUserAdapter;
import com.mall.yyrg.model.TiXiUser;

public class TiXiUserList extends Activity	implements CallBackListener{
	@ViewInject(R.id.start_time)
	private EditText start_time;
	@ViewInject(R.id.end_time)
	private EditText end_time;
	@ViewInject(R.id.listView1)
	private ListView listView1;
	private PopupWindow distancePopup = null;
	private List<TiXiUser> tiXiUsers=new ArrayList<TiXiUser>();
	@ViewInject(R.id.lins1)
	private LinearLayout lins1;
	private String huiyuanMes="";
	private String zhaoshangMes="";
	private String tuijianMes="";
	private String jueseMes="全部";
	private String juseId="-1";
	private int state = 0;
	private Dialog dialog;
	private String tixiOrquyu;
	@ViewInject(R.id.title)
	private TextView title;
	
	private String[][] allRoles=new String[][]{{"-1","全部"},{"1","普通会员"},{"2","VIP会员"},{"6","城市运营中心"}
	,{"5","城市总监"},{"7","创业大使"},{"14","创业空间"},{"8","基础"},{"9","标准"}
	,{"10","形象"},{"11","蓝钻"},{"12","红钻"},{"13","金钻"},{"18","联盟商家"}};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tixi_user_list);
		ViewUtils.inject(this);
		tixiOrquyu=getIntent().getStringExtra("state");
		if (tixiOrquyu.equals("1")) {
			title.setText("体系内会员");
		}else if (tixiOrquyu.equals("2")) {
			title.setText("区域内会员");
		}
		getTiXiUser("", "", "", "", "", "");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		start_time.setText("");
		end_time.setText("");
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
	@OnClick({R.id.top_back,R.id.chaxun,R.id.shaixuan})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;

		case R.id.chaxun:
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
				 getTiXiUser(juseId, huiyuanMes, tuijianMes, zhaoshangMes, start_time.getText().toString().trim(), 
						 end_time.getText().toString().trim());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		
		case R.id.shaixuan:
			getPopupWindow();
			startPopupWindow(null,2);
			distancePopup.showAsDropDown(view);
			break;
			
		}
	}
	/**
	 * 获得体系内的会员
	 */
	public void getTiXiUser(final String ddlevel,final String txtUserID
			,final String txtIn,final String txtMe,final String txtStartDate
			,final String txtEndDate){
		final User user = UserData.getUser();
		Util.asynTask(this, "载入中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(TiXiUserList.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG)
								.show();
					} else {
						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Gson gson = new Gson();
								tiXiUsers = gson.fromJson(map.get("list"),
										new TypeToken<List<TiXiUser>>() {
										}.getType());
								if (tiXiUsers.size()>0) {
									TiXiUserAdapter adapter=new TiXiUserAdapter(TiXiUserList.this, tiXiUsers);
									adapter.setCallBack(TiXiUserList.this);
									listView1.setAdapter(adapter);
									
								}else {
									Util.show("没有数据源", TiXiUserList.this);
								}
							} else {
								tiXiUsers.clear();
								TiXiUserAdapter adapter=new TiXiUserAdapter(TiXiUserList.this, tiXiUsers);
								adapter.setCallBack(TiXiUserList.this);
								listView1.setAdapter(adapter);
								Toast.makeText(TiXiUserList.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(TiXiUserList.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(TiXiUserList.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}
			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web=null;
				if (tixiOrquyu.equals("1")) {
					web = new Web(1, Web.newAPI,
							Web.gettixiuser, "gettixiuser",
							"userId=" + user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&pagesize=20"
									+ "&pageindex=1"
									+"&ddlevel="+ddlevel
									+"&txtUserID="+txtUserID
									+"&txtIn="+txtIn
									+"&txtMe="+txtMe
									+"&txtStartDate="+txtStartDate
									+"&txtEndDate="+txtEndDate);
				}else if (tixiOrquyu.equals("2")) {
					web = new Web(1, Web.newAPI,
							Web.getquyuuser, "getquyuuser",
							"userId=" + user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&pagesize=20"
									+ "&pageindex=1"
									+"&ddlevel="+ddlevel
									+"&txtUserID="+txtUserID
									+"&txtIn="+txtIn
									+"&txtMe="+txtMe
									+"&txtStartDate="+txtStartDate
									+"&txtEndDate="+txtEndDate);
				}
			
				String s = web.getPlan();
				return s;
			}
		});
	}
	@Override
	public void callBack(Object object) {
		// TODO Auto-generated method stub
		TiXiUser user=(TiXiUser) object;
		getPopupWindow();
		startPopupWindow(user,1);
		distancePopup.showAsDropDown(lins1);
	}
	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(TiXiUser user,int state) {
		if (state==1) {
			View pview = getLayoutInflater().inflate(R.layout.tixi_user_message,
					null, false);
			LinearLayout lin=(LinearLayout) pview.findViewById(R.id.lin);
			DisplayMetrics dm=new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int width=dm.widthPixels;
			int height=dm.heightPixels;
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) lin
					.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width;
			linearParams.height = height;
			lin.getLayoutParams();
			TextView zhanghao=(TextView) pview.findViewById(R.id.zhanghao);
			TextView juese=(TextView) pview.findViewById(R.id.juese);
			TextView xingming=(TextView) pview.findViewById(R.id.xingming);
			TextView dianhua=(TextView) pview.findViewById(R.id.dianhua);
			TextView tuijian=(TextView) pview.findViewById(R.id.tuijian);
			TextView zhaoshang=(TextView) pview.findViewById(R.id.zhaoshang);
			TextView quyu=(TextView) pview.findViewById(R.id.quyu);
			TextView zhuce=(TextView) pview.findViewById(R.id.zhuce);
			zhanghao.setText("会员帐号："+user.getUserId());
			juese.setText("会员角色："+user.getLevel());
			xingming.setText("会员姓名："+user.getName());
			dianhua.setText("联系方式："+user.getMobilePhone());
			tuijian.setText("推荐人："+user.getInviter());
			zhaoshang.setText("招商人："+user.getMerchants());
			quyu.setText("所在区域："+user.getZone());
			zhuce.setText("注册时间："+user.getDate());
			TextView top_back=(TextView) pview.findViewById(R.id.top_back);
			top_back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					distancePopup.dismiss();
				}
			});
			initpoputwindow(pview);
		}else {
			View pview = getLayoutInflater().inflate(R.layout.tixi_user_popupwindow,
					null, false);
			final EditText huiyuanid=(EditText) pview.findViewById(R.id.huiyuanid);
			final EditText zhaoshang=(EditText) pview.findViewById(R.id.zhaoshang);
			final EditText tuijian=(EditText) pview.findViewById(R.id.tuijian);
			huiyuanid.setText(huiyuanMes);
			zhaoshang.setText(zhaoshangMes);
			tuijian.setText(tuijianMes);
			final ListView listView=(ListView) pview.findViewById(R.id.listview);
			TextView text1=(TextView) pview.findViewById(R.id.text1);
			text1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					huiyuanMes=huiyuanid.getText().toString().trim();
					zhaoshangMes=zhaoshang.getText().toString().trim();
					tuijianMes=tuijian.getText().toString().trim();
					distancePopup.dismiss();
					getTiXiUser(juseId, huiyuanMes, tuijianMes, zhaoshangMes, "", "");
				}
			});
			listView.setAdapter(new SelectRoleAdapter(jueseMes));
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					jueseMes=allRoles[position][1];
					juseId=allRoles[position][0];
					listView.setAdapter(new SelectRoleAdapter(jueseMes));
					listView.setSelection(position);
				}
			});
			initpoputwindow(pview);
		}
	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}
	private void chanegeDrawable(int imageid, TextView view) {
		Drawable icon = this.getResources().getDrawable(imageid);
		icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
		view.setCompoundDrawables(null, null, icon, null);
		
	}
	/**
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimation);
	}
	private class SelectRoleAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private String message;
		private SelectRoleAdapter(String message){
			this.message=message;
			inflater=LayoutInflater.from(TiXiUserList.this);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allRoles.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allRoles[position][1];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView==null) {
				convertView=inflater.inflate(R.layout.tixi_user_popupwindow_item, null);
			}
			TextView name=(TextView) convertView.findViewById(R.id.name);
			name.setText(allRoles[position][1]);
			if (allRoles[position][1].equals(message)) {
				chanegeDrawable(R.drawable.gwcxdh, name);
			}else {
				chanegeDrawable(R.drawable.gwdxk, name);
			}
			return convertView;
		}
		
	}
}
