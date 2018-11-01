package com.mall.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.model.HotelOrdersBean;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * 功能：酒店订单信息 时间：2014年8月8日
 * 
 * @author Administrator
 * 
 */
public class HotelOrders extends Activity {
	@ViewInject(R.id.phone_list)
	private ListView phone_list;
	private Dialog dialog;
	private PopupWindow distancePopup = null;
	private int currentPageShop = 0;
	private HotelOrdersAdapter hotelOrdersAdapter;
	private List<HotelOrdersBean> hotelOrdersBeans = new ArrayList<HotelOrdersBean>();
	@ViewInject(R.id.jinsanyue)
	private TextView jinsanyue;
	@ViewInject(R.id.sanyue_qian)
	private TextView sanyue_qian;
	@ViewInject(R.id.sanyuenei)
	private LinearLayout sanyuenei;
	@ViewInject(R.id.sanyueqian)
	private LinearLayout sanyueqian;
	private int state = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_orders);
		ViewUtils.inject(this);
		// getHotelOrders(method, userId, md5Pwd);
		if (hotelOrdersAdapter == null) {
			hotelOrdersAdapter = new HotelOrdersAdapter(this);
			phone_list.setAdapter(hotelOrdersAdapter);
		}
		firstpageshop();
		scrollPageshop();
	}

	@OnClick({ R.id.top_back, R.id.fi1, R.id.fi2 })
	public void onclick(View view) {
		Drawable drawable = getResources().getDrawable(
				R.drawable.order_biankuang1);
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;
		case R.id.fi1:
			state = 1;
			jinsanyue.setTextColor(getResources().getColor(R.color.new_headertop));
			jinsanyue.setBackgroundColor(getResources().getColor(R.color.bg));
			sanyue_qian.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			sanyuenei.setVisibility(View.VISIBLE);
			sanyueqian.setVisibility(View.INVISIBLE);
			sanyue_qian.setTextColor(getResources().getColor(R.color.bg));
			hotelOrdersBeans.clear();
			currentPageShop = 0;
			hotelOrdersAdapter = new HotelOrdersAdapter(this);
			phone_list.setAdapter(hotelOrdersAdapter);
			firstpageshop();
			scrollPageshop();
			break;
		case R.id.fi2:
			state = 2;
			jinsanyue.setTextColor(getResources().getColor(R.color.bg));
			sanyuenei.setVisibility(View.INVISIBLE);
			sanyueqian.setVisibility(View.VISIBLE);
			sanyue_qian
					.setTextColor(getResources().getColor(R.color.new_headertop));
			jinsanyue.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			sanyue_qian.setBackgroundColor(getResources().getColor(R.color.bg));
			hotelOrdersBeans.clear();
			currentPageShop = 0;
			hotelOrdersAdapter = new HotelOrdersAdapter(this);
			phone_list.setAdapter(hotelOrdersAdapter);
			firstpageshop();
			scrollPageshop();
			break;
		}
	}

	@OnItemClick({ R.id.phone_list })
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// showOrderMessage();
//		getHotelMeaasge(hotelOrdersBeans.get(position).getOrderId(), view,
//				hotelOrdersBeans.get(position));
		Intent intent = new Intent(HotelOrders.this,HotelOrdersMsg.class);
		intent.putExtra("orderId", hotelOrdersBeans.get(position).getOrderId());
		intent.putExtra("data",(Serializable)hotelOrdersBeans.get(position));
		startActivity(intent);
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(List<HotelOrdersBean> list,
			HotelOrdersBean ordersBean) {
//		View pview = getLayoutInflater().inflate(R.layout.hotel_orders_dialog,
//				null, false);
		View pview = getLayoutInflater().inflate(R.layout.dialog_orders_hotels,
			null,false);
		TextView top_back = (TextView) pview.findViewById(R.id.top_back);
//		TextView order_id = (TextView) pview.findViewById(R.id.order_id);
//		order_id.setText(ordersBean.getOrderId());
//		TextView ordertime = (TextView) pview.findViewById(R.id.ordertime);
//		ordertime.setText(ordersBean.getOrdertime());
		TextView intime = (TextView) pview.findViewById(R.id.intime);
		intime.setText(ordersBean.getIntime());
		TextView outtime = (TextView) pview.findViewById(R.id.outtime);
		outtime.setText(ordersBean.getOuttime());
		TextView lasttime = (TextView) pview.findViewById(R.id.lasttime);
		lasttime.setText(ordersBean.getLasttime());
//		TextView money = (TextView) pview.findViewById(R.id.order_money);
//		money.setText("￥" + ordersBean.getMoney());
//		TextView orderstate = (TextView) pview.findViewById(R.id.orderState);
//		orderstate.setText(ordersBean.getOrderState());
		TextView phones = (TextView) pview.findViewById(R.id.user_phones);
		phones.setText(list.get(0).getPhone());
		TextView mail = (TextView) pview.findViewById(R.id.mail);
		mail.setText(list.get(0).getMail());
		TextView hotelName = (TextView) pview.findViewById(R.id.hotelName);
		hotelName.setText(list.get(0).getHotelName());
		TextView hoteladdress = (TextView) pview
				.findViewById(R.id.hoteladdress);
		hoteladdress.setText(list.get(0).getHotelAddress());
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
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimation);
	}

	private void getHotelOrders() {
		Util.asynTask(this, "正在加载数据", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					int index = 0;
					List<HotelOrdersBean> list = new ArrayList<HotelOrdersBean>();
					list = ((HashMap<Integer, List<HotelOrdersBean>>) runData)
							.get(index++);

					List<HotelOrdersBean> nowlist = new ArrayList<HotelOrdersBean>();
					int nowmonth = Calendar.getInstance().get(
							Calendar.MONTH) + 1;
					int nowyear=Calendar.getInstance().get(Calendar.YEAR);
					if (state == 1) {// 近三月
						for (HotelOrdersBean bean : list) {
							String orderYear=bean.getOrdertime().split(" ")[0]
									.split("-")[0];
							String orderMonth = bean.getOrdertime().split(" ")[0]
									.split("-")[1];
							int orderM=(Integer.parseInt(orderYear)-nowyear)*12+Integer.parseInt(orderMonth);
							if (orderM + 3 >= nowmonth) {
								nowlist.add(bean);
							}
						}
					} else {// 三月前
						for (HotelOrdersBean bean : list) {
							String orderYear=bean.getOrdertime().split(" ")[0]
									.split("-")[0];
							String orderMonth = bean.getOrdertime().split(" ")[0]
									.split("-")[1];
							int orderM=(Integer.parseInt(orderYear)-nowyear)*12+Integer.parseInt(orderMonth);
							if (orderM + 3 < nowmonth) {
								nowlist.add(bean);
							}
						}
					}
					hotelOrdersAdapter.setList(nowlist);
					hotelOrdersBeans.addAll(nowlist);
				} else {
					Toast.makeText(HotelOrders.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web( Web.getServiceHotelOrder,
						"userid=" + UserData.getUser().getUserId()
								+ "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&page="
								+ (++currentPageShop) + "&size=" + 40);
				List<HotelOrdersBean> list = web.getList(HotelOrdersBean.class);
				HashMap<Integer, List<HotelOrdersBean>> map = new HashMap<Integer, List<HotelOrdersBean>>();
				map.put(index++, list);
				return map;
			}

		});
	}

	public void firstpageshop() {
		getHotelOrders();
	}

	public void scrollPageshop() {
		phone_list.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= hotelOrdersAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getHotelOrders();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	/**
	 * 酒店详细信息
	 */
	private void getHotelMeaasge(final String orderId, final View view,
			final HotelOrdersBean ordersBean) {
		Util.asynTask(this, "正在加载数据", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<HotelOrdersBean> list = new ArrayList<HotelOrdersBean>();
					list = ((HashMap<Integer, List<HotelOrdersBean>>) runData)
							.get(0);
					
		
					
					getPopupWindow();
					startPopupWindow(list, ordersBean);
					distancePopup.showAtLocation(view, Gravity.TOP, 0, 0);
//					distancePopup.showAsDropDown(HotelOrders.this.findViewById(R.id.order_root));
				} else {
					Toast.makeText(HotelOrders.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(
						Web.getServiceHotelOrderDetail, "userid="
								+ UserData.getUser().getUserId()
								+ "&md5Pwd=" + UserData.getUser().getMd5Pwd()
								+ "&orderId=" + orderId);
				List<HotelOrdersBean> list = web.getList(HotelOrdersBean.class);
				HashMap<Integer, List<HotelOrdersBean>> map = new HashMap<Integer, List<HotelOrdersBean>>();
				map.put(0, list);
				return map;
			}

		});
	}

}
