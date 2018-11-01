package com.mall.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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
import com.mall.model.FlimOrdersBean;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * 功能：电影票订单信息 时间：2014年8月8日
 * 
 * @author Administrator
 * 
 */
public class FlimOrders extends Activity {
	@ViewInject(R.id.phone_list)
	private ListView phone_list;
	private PopupWindow distancePopup = null;
	private int currentPageShop = 0;
	private FlimOrdersAdapter flimOrdersAdapter;
	private List<FlimOrdersBean> flimOrdersBeans=new ArrayList<FlimOrdersBean>();
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
		setContentView(R.layout.flim_orders);
		ViewUtils.inject(this);
		if (flimOrdersAdapter == null) {
			flimOrdersAdapter = new FlimOrdersAdapter(this);
			phone_list.setAdapter(flimOrdersAdapter);
		}
		firstpageshop();
		scrollPageshop();
	}

	@OnClick({ R.id.top_back , R.id.fi1, R.id.fi2})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;
		case R.id.fi1:
			state = 1;
			jinsanyue.setTextColor(getResources().getColor(R.color.headertop));
			jinsanyue.setBackgroundColor(getResources().getColor(R.color.bg));
			sanyue_qian.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			sanyuenei.setVisibility(View.VISIBLE);
			sanyueqian.setVisibility(View.INVISIBLE);
			sanyue_qian.setTextColor(getResources().getColor(R.color.bg));
			flimOrdersBeans.clear();
			currentPageShop = 0;
			flimOrdersAdapter = new FlimOrdersAdapter(this);
			phone_list.setAdapter(flimOrdersAdapter);
			firstpageshop();
			scrollPageshop();
			break;
		case R.id.fi2:
			state = 2;
			jinsanyue.setTextColor(getResources().getColor(R.color.bg));
			sanyuenei.setVisibility(View.INVISIBLE);
			sanyueqian.setVisibility(View.VISIBLE);
			sanyue_qian
					.setTextColor(getResources().getColor(R.color.headertop));
			jinsanyue.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			sanyue_qian.setBackgroundColor(getResources().getColor(R.color.bg));
			flimOrdersBeans.clear();
			currentPageShop = 0;
			flimOrdersAdapter = new FlimOrdersAdapter(this);
			phone_list.setAdapter(flimOrdersAdapter);
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
		getPopupWindow();
		startPopupWindow(flimOrdersBeans.get(position));
		distancePopup.showAtLocation(view, 0, 0, 0);

	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(FlimOrdersBean ordersBean) {
		View pview = getLayoutInflater().inflate(R.layout.flim_orders_dialog,
				null, false);
		ViewUtils.inject(pview);
		TextView top_back = (TextView) pview.findViewById(R.id.top_back);
		TextView orderid=(TextView) pview.findViewById(R.id.orderId);
		orderid.setText(ordersBean.getOrderId());
		TextView ordertime=(TextView) pview.findViewById(R.id.ordertime);
		ordertime.setText(ordersBean.getOrdertime());
		TextView filmName=(TextView) pview.findViewById(R.id.filmName);
		filmName.setText(ordersBean.getFilmName());
		TextView unitPrice=(TextView) pview.findViewById(R.id.unitPrice);
		unitPrice.setText("￥"+ordersBean.getUnitPrice());
		TextView count=(TextView) pview.findViewById(R.id.count);
		count.setText(ordersBean.getCount());
		TextView money=(TextView) pview.findViewById(R.id.money);
		money.setText("￥"+ordersBean.getMoney());
		
		TextView payType=(TextView) pview.findViewById(R.id.payType);
		payType.setText(ordersBean.getPayType());
		TextView orderState=(TextView) pview.findViewById(R.id.orderState);
		orderState.setText(ordersBean.getOrderState());
		TextView payState=(TextView) pview.findViewById(R.id.payState);
		payState.setText(ordersBean.getPayState());
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

	private void getFlimOrders() {
		Util.asynTask(this, "正在加载数据", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					int index = 0;
					List<FlimOrdersBean> list = new ArrayList<FlimOrdersBean>();
					list = ((HashMap<Integer, List<FlimOrdersBean>>) runData)
							.get(index++);

					List<FlimOrdersBean> nowlist = new ArrayList<FlimOrdersBean>();
					if (state == 1) {// 近三月
						for (FlimOrdersBean bean : list) {
							String orderYear=bean.getOrdertime().split(" ")[0]
									.split("-")[0];
							String orderMonth = bean.getOrdertime().split(" ")[0]
									.split("-")[1];
							int nowmonth = Calendar.getInstance().get(
									Calendar.MONTH) + 1;
							int nowyear=Calendar.getInstance().get(Calendar.YEAR);
							int orderM=(Integer.parseInt(orderYear)-nowyear)*12+Integer.parseInt(orderMonth);
							if (orderM + 3 >= nowmonth) {
								nowlist.add(bean);
							}
						}
					} else {// 三月前
						for (FlimOrdersBean bean : list) {
							String orderYear=bean.getOrdertime().split(" ")[0]
									.split("-")[0];
							String orderMonth = bean.getOrdertime().split(" ")[0]
									.split("-")[1];
							int nowmonth = Calendar.getInstance().get(
									Calendar.MONTH) + 1;
							int nowyear=Calendar.getInstance().get(Calendar.YEAR);
							int orderM=(Integer.parseInt(orderYear)-nowyear)*12+Integer.parseInt(orderMonth);
							if (orderM + 3 < nowmonth) {
								nowlist.add(bean);
							}
						}
					}
					flimOrdersAdapter.setList(nowlist);
					flimOrdersBeans.addAll(nowlist);
				} else {
					Toast.makeText(FlimOrders.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}
			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web( Web.getServiceFilmOrder,
						"userid=" + UserData.getUser().getUserId()
								+ "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&page="
								+ (++currentPageShop) + "&size=" + 20);
				List<FlimOrdersBean> list = web.getList(FlimOrdersBean.class);
				HashMap<Integer, List<FlimOrdersBean>> map = new HashMap<Integer, List<FlimOrdersBean>>();
				map.put(index++, list);
				return map;
			}

			
		});
	}

	public void firstpageshop() {
		getFlimOrders();
	}

	public void scrollPageshop() {
		phone_list.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= flimOrdersAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getFlimOrders();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
}
