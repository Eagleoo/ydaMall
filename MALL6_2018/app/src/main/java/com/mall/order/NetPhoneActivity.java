package com.mall.order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.lin.component.BaseMallAdapter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mall.model.ChargeCarPhone;
import com.mall.model.NetPhone;
import com.mall.model.PhoneOrdersBean;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.QuitDialog;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

@ContentView(R.layout.net_phont_orders)
public class NetPhoneActivity extends Activity {
	@ViewInject(R.id.money_charge)
	private TextView money_charge;
	@ViewInject(R.id.card_charge)
	private TextView card_charge;
	@ViewInject(R.id.money_charge_list)
	private ListView money_charge_list;
	@ViewInject(R.id.card_charge_list)
	private ListView card_charge_list;
	@ViewInject(R.id.money_charge_line)
	private LinearLayout money_charge_line;
	@ViewInject(R.id.card_charge_line)
	private LinearLayout card_charge_line;
	@ViewInject(R.id.money_charge_topLine)
	private LinearLayout money_charge_topLine;
	@ViewInject(R.id.card_charge_topLine)
	private LinearLayout card_charge_topLine;
	@ViewInject(R.id.nocharge_hint)
	private RelativeLayout nocharge_hint;
	private User user;
	private PopupWindow distancePopup = null;
	private List<NetPhone> NetPhones=new ArrayList<NetPhone>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		nocharge_hint.setVisibility(View.VISIBLE);
		getMoneyOrders(0);
		getCardOrders();
	}
	private void getCardOrders() {
		user=UserData.getUser();
		Map< String, String> parm=new HashMap<String, String>();
		parm.put("userId", user.getUserId());
		parm.put("md5Pwd", user.getMd5Pwd());

		NewWebAPI.getNewInstance().getWebRequest("/Voip.aspx?call=getMyRechargeCards", parm,new WebRequestCallBack(){
			public void success(Object result) {
				if(null==result){
					Util.show("网络错误，请重试！", NetPhoneActivity.this);
					return;
				}
				JSONObject json = JSON.parseObject(result.toString());
				if(200!=json.getIntValue("code")){
					Util.show(json.getString("message"), NetPhoneActivity.this);
					return;
				}
				String arrList = json.getString("list");
				List<ChargeCarPhone> list = JSON.parseArray(arrList, ChargeCarPhone.class);
				if(Util.isNull(list))
					return;
				BaseMallAdapter<ChargeCarPhone> adapter = (BaseMallAdapter<ChargeCarPhone>)card_charge_list.getAdapter();
				if(null!=list&&0<list.size()){
					if(null==adapter){
						adapter=new BaseMallAdapter<ChargeCarPhone>(R.layout.chargecar_phone_orders_item3,NetPhoneActivity.this,list) {

							@Override
							public View getView(int position, View convertView, ViewGroup parent, ChargeCarPhone t) {
								LinearLayout item=(LinearLayout) convertView.findViewById(R.id.staff_manager_user_reward_item);
								TextView card_id=(TextView) convertView.findViewById(R.id.card_id);
								TextView order_money=(TextView) convertView.findViewById(R.id.order_money);
								TextView order_date=(TextView) convertView.findViewById(R.id.order_date);
								card_id.setText(list.get(position).cardId);
								order_money.setText(list.get(position).money);
								order_date.setText(list.get(position).date);
								if (position%2==0) {
									item.setBackgroundColor(context.getResources().getColor(R.color.blue_light_more));
								}else {
									item.setBackgroundColor(context.getResources().getColor(R.color.bg));
								}
								return convertView;
							}
						};
						card_charge_list.setAdapter(adapter);

					}else{
						adapter.add(list);
						adapter.updateUI();
					}

				}
			}

			@Override
			public void requestEnd() {

				super.requestEnd();
			};

		} );

	}
	private void getMoneyOrders(int month) {
		user=UserData.getUser();
		Map< String, String> parm=new HashMap<String, String>();
		parm.put("userId", user.getUserId());
		parm.put("md5Pwd", user.getMd5Pwd());
		parm.put("month", month+"");

		NewWebAPI.getNewInstance().getWebRequest("/ydaOrder.aspx?call=getYdPhoneOrder", parm,new WebRequestCallBack(){






			public void success(Object result) {
				if(null==result){
					Util.show("网络错误，请重试！", NetPhoneActivity.this);
					return;
				}
				JSONObject json = JSON.parseObject(result.toString());
				if(200!=json.getIntValue("code")){
					Util.show(json.getString("message"), NetPhoneActivity.this);
					return;
				}
				String arrList = json.getString("list");
				List<NetPhone> list = JSON.parseArray(arrList, NetPhone.class);
				if(Util.isNull(list))
					return;
				BaseMallAdapter<NetPhone> adapter = (BaseMallAdapter<NetPhone>)money_charge_list.getAdapter();
	
				if(null!=list&&0<list.size()){
					nocharge_hint.setVisibility(View.GONE);
					if(null==adapter){
						adapter=new BaseMallAdapter<NetPhone>(R.layout.netphone_orders_item2,NetPhoneActivity.this,list) {

							@Override
							public View getView(int position, View convertView, ViewGroup parent, NetPhone t) {
								LinearLayout item=(LinearLayout) convertView.findViewById(R.id.staff_manager_user_reward_item);
								TextView order_id=(TextView) convertView.findViewById(R.id.order_id);
								TextView order_money=(TextView) convertView.findViewById(R.id.order_money);
								TextView order_state=(TextView) convertView.findViewById(R.id.order_state);
								TextView order_date=(TextView) convertView.findViewById(R.id.order_date);
								order_id.setText(list.get(position).orderId);
								order_money.setText(list.get(position).money);
								order_date.setText(list.get(position).date);
								order_state.setText(list.get(position).status_s);
								if (position%2==0) {
									item.setBackgroundColor(context.getResources().getColor(R.color.blue_light_more));
								}else {
									item.setBackgroundColor(context.getResources().getColor(R.color.bg));
								}
								return convertView;
							}
						};
						money_charge_list.setAdapter(adapter);

					}else{
						adapter.add(list);
						adapter.updateUI();
					}

				}
				NetPhones.addAll(list);

			}

			@Override
			public void requestEnd() {

				super.requestEnd();
			};

		} );


	}

	@OnItemClick({R.id.money_charge_list})
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		getPopupWindow();
		startPopupWindow(NetPhones.get(position));
		distancePopup.showAtLocation(view, 0, 0, 0);
	}
	@OnClick({R.id.top_back,R.id.money,R.id.card,R.id.nocharge_hint})
	public void BackClick(View v){

		switch (v.getId()) {
		case R.id.top_back:
			finish();
			break;
		case R.id.nocharge_hint:
			if (Util.isInstall(this, "com.yda360.ydaphone")) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				ComponentName cn = new ComponentName("com.yda360.ydaphone", "com.yda360.ydaphone.acticity.Leading");
				intent.putExtra("action", "lin00123");
				intent.setComponent(cn);
				if (null != UserData.getUser()) {
					intent.putExtra("userId", UserData.getUser().getUserId());
					intent.putExtra("md5Pwd", UserData.getUser().getMd5Pwd());
					intent.putExtra("userNo", UserData.getUser().getUserNo());
					intent.putExtra("userFace", UserData.getUser().getUserFace());
				}
				//intent.putExtra("openClassName", "com.yda360.ydaphone.acticity.VoipIndexActivity");
				intent.putExtra("openClassName", "com.yda360.ydaphone.acticity.more.VoipMoreFavorableActivity");

				startActivity(intent);
			} else {
				QuitDialog dialog = new QuitDialog(this, "亲，首次拨打电话请安装【远大电话】！", "立即安装", "稍后下载", new OnClickListener() {
					@Override
					public void onClick(View v) {
						Util.openWeb(NetPhoneActivity.this, "http://" + Web.webServer + "/yuandaapp/plugs/YdaPhone.apk");
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
					}
				});
				dialog.show();
			}

			
			break;
		case R.id.money://现金

			money_charge.setTextColor(getResources().getColor(
					R.color.new_headertop));
			money_charge.setBackgroundColor(getResources().getColor(R.color.bg));
			card_charge.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			money_charge_line.setVisibility(View.VISIBLE);
			card_charge_line.setVisibility(View.INVISIBLE);
			card_charge.setTextColor(getResources().getColor(R.color.bg));

			card_charge_topLine.setVisibility(View.GONE);
			card_charge_list.setVisibility(View.GONE);
			money_charge_topLine.setVisibility(View.VISIBLE);
			money_charge_list.setVisibility(View.VISIBLE);
			if(money_charge_list.getAdapter()==null)
				nocharge_hint.setVisibility(View.VISIBLE);

			else
				nocharge_hint.setVisibility(View.GONE);


			break;
		case R.id.card://充值卡

			money_charge.setTextColor(getResources().getColor(R.color.bg));
			money_charge_line.setVisibility(View.INVISIBLE);
			card_charge_line.setVisibility(View.VISIBLE);
			card_charge.setTextColor(getResources().getColor(
					R.color.new_headertop));
			money_charge.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			card_charge.setBackgroundColor(getResources().getColor(R.color.bg));

			card_charge_topLine.setVisibility(View.VISIBLE);
			card_charge_list.setVisibility(View.VISIBLE);
			money_charge_topLine.setVisibility(View.GONE);
			money_charge_list.setVisibility(View.GONE);
			if(card_charge_list.getAdapter()==null)
				nocharge_hint.setVisibility(View.VISIBLE);

			else
				nocharge_hint.setVisibility(View.GONE);
			break;
		}
	}
	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(NetPhone netphone) {
		View pview = getLayoutInflater().inflate(R.layout.netphone_orders_dialog2,
				null, false);
		ViewUtils.inject(pview);
		TextView top_back=(TextView) pview.findViewById(R.id.top_back);
		TextView order_number=(TextView) pview.findViewById(R.id.order_number);
		TextView order_time=(TextView) pview.findViewById(R.id.order_time);
		TextView pay_money=(TextView) pview.findViewById(R.id.pay_money);
		TextView pay_state=(TextView) pview.findViewById(R.id.pay_state);
		TextView order_sstate=(TextView) pview.findViewById(R.id.order_sstate);
		order_number.setText(netphone.orderId);
		order_time.setText(netphone.date);
		pay_money.setText("￥"+netphone.money);
		pay_state.setText(netphone.status_s);
		if(netphone.status.equals("0"))
			order_sstate.setText("未处理");
		else
			order_sstate.setText("已处理");

		top_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
}
