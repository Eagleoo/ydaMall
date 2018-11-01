package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.baidu.android.common.logging.Log;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.MyAreaBusinessInfo;
import com.mall.model.MyAreaBusinessInfo.ListBean;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.util.ArrayList;

public class AreaBusinessFrame extends Activity {

	@ViewInject(R.id.area_bus_listView)
	private ListView listView;
	private int page = 1;
	private int maxPage = 0;
	private int lastItem=0;
	private int status=0; // 0可以加载，1不能加载，2加载中

	@ViewInject(R.id.huiyuannumber)
	private TextView huiyuannumber;

	@ViewInject(R.id.chuangkenumber)
	private TextView chuangkenumber;

	@ViewInject(R.id.shangjianumber)
	private TextView shangjianumber;

	private int number1=0;//创客数

	private int number2=0;//商家数

	ArrayList<ListBean> list=new ArrayList<MyAreaBusinessInfo.ListBean>();
	ArrayList<ListBean> listall=new ArrayList<MyAreaBusinessInfo.ListBean>();
	ArrayList<ListBean> list1=new ArrayList<MyAreaBusinessInfo.ListBean>();
	ArrayList<ListBean> list2=new ArrayList<MyAreaBusinessInfo.ListBean>();
	private BaseMallAdapter<ListBean> adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.area_bus_frame);
		Util.initTop(this, "区域内的业务", Integer.MIN_VALUE, null);
		ViewUtils.inject(this);
		initData();
	}

	@OnClick({R.id.huiyuannumber,R.id.chuangkenumber,R.id.shangjianumber})
			private void click(View view){
		switch (view.getId()){
			case R.id.huiyuannumber:
				adapter.clear();
				adapter.add(listall);
				adapter.updateUI();
				break;
			case R.id.chuangkenumber:
				adapter.clear();
				adapter.add(list1);
				adapter.updateUI();
				break;
			case R.id.shangjianumber:
				adapter.clear();
				adapter.add(list2);
				adapter.updateUI();
				break;
		}
		inittextstate(view);
	}

	private void inittextstate(View view){

		huiyuannumber.setTextColor(Color.parseColor("#000000"));
		chuangkenumber.setTextColor(Color.parseColor("#000000"));
		shangjianumber.setTextColor(Color.parseColor("#000000"));
		switch (view.getId()){
			case R.id.huiyuannumber:
				huiyuannumber.setTextColor(Color.parseColor("#FF2145"));
				break;
			case R.id.chuangkenumber:
				chuangkenumber.setTextColor(Color.parseColor("#FF2145"));
				break;
			case R.id.shangjianumber:
				shangjianumber.setTextColor(Color.parseColor("#FF2145"));
				break;
		}

	}


	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1234:

					Log.e("最后长度", "会员："+list.size()+"创客："+list1.size()+"商家："+list2.size());
					chuangkenumber.setText("创客："+number1);
					shangjianumber.setText("商家："+number2);

					break;

				default:
					break;
			}

		};

	};

	private void page(){
		User user = UserData.getUser();
		status = 2;
		final CustomProgressDialog cpd = CustomProgressDialog
				.showProgressDialog(this, "正在获取数据...");

		NewWebAPI.getNewInstance().myAreaBus(user.getUserId(),
				user.getMd5Pwd(), page + "", "20", new WebRequestCallBack() {
					@Override
					public void success(Object result) {
						if (Util.isNull(result)) {
							Util.show("网络异常，请稍后再试！", AreaBusinessFrame.this);
							return;
						}
						JSONObject json = JSONObject.parseObject(result
								.toString());


						if (200 != json.getIntValue("code")) {
							Util.show(json.getString("message"),
									AreaBusinessFrame.this);
							return;
						}

						Gson gson=new Gson();
						MyAreaBusinessInfo MyAreaBusinessInfo=gson.fromJson(result.toString(), MyAreaBusinessInfo.class);
						huiyuannumber.setText("会员："+MyAreaBusinessInfo.getMessage());
						list.clear();
						listall.clear();
						list.addAll(MyAreaBusinessInfo.getList());
						listall.addAll(MyAreaBusinessInfo.getList());



						new Thread(new Runnable() {
							@Override
							public void run() {
								try {

									for (int i = 0; i < list.size(); i++) {

										ListBean bean= list.get(i);
										String level =bean.getLevel()+"";

										if (level.equals("城市总监")||level.equals("城市经理")) {
											number1	++;  // 创客
											list1.add(bean);
										} else if(level.equals("联盟商家")){
											number2	++;  //商家
											list2.add(bean);
										}

									}

									handler.sendEmptyMessage(1234);

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();

						if(null == adapter)
							initAdapter(list);
						else{
							adapter.add(list);
							adapter.updateUI();
						}
					}

					@Override
					public void fail(Throwable e) {
						super.fail(e);
					}

					@Override
					public void requestEnd() {
						super.requestEnd();
						cpd.cancel();
						cpd.dismiss();
						status =0;
						page++;
					}
				});
	}

	public void initData() {
		page();
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= listView.getAdapter().getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					synchronized(listView){
						if(0==status){
							status = 1;
							if(maxPage != page)
								page();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	private void initAdapter(ArrayList<ListBean> list){
		adapter = new BaseMallAdapter<ListBean>(R.layout.business_info_item,this, list) {


			@Override
			public View getView(int position, View convertView,
								ViewGroup parent, ListBean t) {
				setText(R.id.bus_info_item_name, Util.getNo_pUserId(t.getUserId()));
				setText(R.id.bus_info_item_date, t.getDate());
				getCacheView(R.id.bus_info_item_state).setVisibility(View.GONE);
				setText(R.id.bus_info_item_type, t.getMobilePhone());
				bindClick(convertView,t);
				return convertView;
			}
		};
		listView.setAdapter(adapter);
	}

	private void bindClick(View item,final ListBean json){
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				String[] items = new String[] {
//						"会员账号："+Util.getNo_pUserId(json.getUserId())
//						, "会员姓名 ："+json.getName()
//						, "系统角色 ："+json.getLevel()
//						, "推荐人 ："+Util.getNo_pUserId(json.getInviter())
//						, "招商人 ："+Util.getNo_pUserId(json.getMerchants())
//						, "所属区域 ："+json.getZone() };

				DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				};


                final AlertDialog alertDialog=	new AlertDialog.Builder(AreaBusinessFrame.this,R.style.MyDialog).show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.areabusindialog);
				TextView close= (TextView) window.findViewById(R.id.close);
                TextView tv1 = (TextView) window.findViewById(R.id.tv1);
                TextView tv2 = (TextView) window.findViewById(R.id.tv2);
                TextView tv3 = (TextView) window.findViewById(R.id.tv3);
                TextView tv4 = (TextView) window.findViewById(R.id.tv4);
                TextView tv5 = (TextView) window.findViewById(R.id.tv5);
                TextView tv6 = (TextView) window.findViewById(R.id.tv6);
                tv1.setText("会员账号："+Util.getNo_pUserId(json.getUserId()));
                tv2.setText("联系电话："+Util.getNo_pUserId(json.getMobilePhone()));
                tv3.setText("辅导老师："+json.getMerchants());
                tv4.setText("系统角色："+json.getLevel());
                tv5.setText("所在城市："+json.getZone());
                tv6.setText("注册时间："+json.getDate());
				 close.setOnClickListener(new OnClickListener() {
					 @Override
					 public void onClick(View v) {
						 alertDialog.dismiss();
					 }
				 });
                alertDialog
						.setCanceledOnTouchOutside(true);
			}
		});

//		item.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				AlertDialog dialog = new AlertDialog.Builder(AreaBusinessFrame.this).create();
//				dialog.setTitle("开通业务");
//				TextView text = new TextView(AreaBusinessFrame.this);
//				text.setText("申请用户："+ json.getString("userId")+"\n"
//						+"用户手机："+json.getString("phone")+"\n"
//						+"申请状态："+json.getString("status")+"\n"
//						+"申请时间："+json.getString("date")+"\n"
//						+"招商单位："+json.getString("recomderId")+"\n"
//						+"申请省份："+json.getString("province")+"\n"
//						+"申请城市："+json.getString("city")+"\n"
//						);
//				dialog.addContentView(text, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//				final EditText twoPwd = new EditText(AreaBusinessFrame.this);
//				twoPwd.setHint("请输入您的交易密码");
//				dialog.addContentView(twoPwd, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//				dialog.setButton("开通", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						String twoPwd_ = twoPwd.getText().toString();
//						if(Util.isNull(twoPwd_)){
//							Util.show("请输入您的交易密码", AreaBusinessFrame.this);
//							return ;
//						}
//						User user = UserData.getUser();
//						CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(AreaBusinessFrame.this, "开通中...");
//						NewWebAPI.getNewInstance().doCreateY(user.getUserId(), user.getMd5Pwd(), twoPwd_, json.getString("id"), new WebRequestCallBack(){
//							@Override
//							public void success(Object result) {
//								super.success(result);
//								if(Util.isNull(result)){
//									Util.show("网络异常请稍后再试！", AreaBusinessFrame.this);
//									return ;
//								}
//								JSONObject obj = JSONObject.parseObject(result.toString());
//								if(200 == obj.getIntValue("code")){
//									Util.show("开通成功！", AreaBusinessFrame.this);
//									page=1;
//									page();
//								}else{
//									Util.show(json.getString("messge"), AreaBusinessFrame.this);
//								}
//							}
//
//							@Override
//							public void fail(Throwable e) {
//								super.fail(e);
//							}
//
//							@Override
//							public void requestEnd() {
//								super.requestEnd();
//							}
//							
//						});
//					}
//				});
//				dialog.setButton("关闭", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//						dialog.dismiss();
//					}
//				});
//			}
//		});
	}
}
