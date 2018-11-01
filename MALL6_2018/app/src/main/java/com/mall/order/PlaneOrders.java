package com.mall.order;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.model.PlaneOrderDetail;
import com.mall.model.PlaneOrdersBean;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.ListViewForScrollView;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * 功能：酒店订单信息 时间：2014年8月8日
 * 
 * @author Administrator
 * 
 */
public class PlaneOrders extends Activity {
	@ViewInject(R.id.phone_list)
	private ListView phone_list;
	private Dialog dialog;
	private PopupWindow distancePopup = null;
	private int currentPageShop = 0;
	private PlaneOrdersAdapter planeOrdersAdapter;
	private List<PlaneOrdersBean> planeOrdersBeans = new ArrayList<PlaneOrdersBean>();
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
		setContentView(R.layout.plane_orders);
		ViewUtils.inject(this);
		if (planeOrdersAdapter == null) {
			planeOrdersAdapter = new PlaneOrdersAdapter(this);
			phone_list.setAdapter(planeOrdersAdapter);
		}
		firstpageshop();
		scrollPageshop();
	}

	@OnClick({ R.id.top_back, R.id.fi1, R.id.fi2 })
	public void onclick(View view) {
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
			planeOrdersBeans.clear();
			currentPageShop = 0;
			planeOrdersAdapter = new PlaneOrdersAdapter(this);
			phone_list.setAdapter(planeOrdersAdapter);
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
			planeOrdersBeans.clear();
			currentPageShop = 0;
			planeOrdersAdapter = new PlaneOrdersAdapter(this);
			phone_list.setAdapter(planeOrdersAdapter);
			firstpageshop();
			scrollPageshop();
			break;
		}
	}

	@OnItemClick({ R.id.phone_list })
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		getPlaneMassage(planeOrdersBeans.get(position).getOrderId(), view);
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(PlaneOrderDetail orderDetail) {
		View pview = getLayoutInflater().inflate(R.layout.plane_orders_dialog,
				null, false);
		ViewUtils.inject(pview);
		TextView top_back = (TextView) pview.findViewById(R.id.top_back);
		// 订单状态
		TextView orderstate = (TextView) pview.findViewById(R.id.orderstate);
		orderstate.setText(orderDetail.getOrderState());
		// 订单时间
		TextView ordertime = (TextView) pview.findViewById(R.id.ordertime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ordertime.setText(orderDetail.getOrdertime().split(" ")[0]);
		try {
			ordertime.setText(ordertime.getText().toString() + "  "
					+ getWeek(sdf.parse(orderDetail.getOrdertime())));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 单程
		TextView dancheng = (TextView) pview.findViewById(R.id.dancheng);
		// 返程
		TextView fancheng = (TextView) pview.findViewById(R.id.fancheng);
		String[] fromFlys = orderDetail.getFromFly().split("\\|");
		String[] toFlys = orderDetail.getToFly().split("\\|");
		if (fromFlys.length > 1) {
			dancheng.setText(fromFlys[0] + " - " + toFlys[0]);
			fancheng.setVisibility(View.VISIBLE);
			fancheng.setText(fromFlys[1] + " - " + toFlys[1]);
		} else {
			dancheng.setText(fromFlys[0] + " - " + toFlys[0]);
			fancheng.setVisibility(View.GONE);
		}
		// 单程航班号
		TextView flightNo_qucheng = (TextView) pview
				.findViewById(R.id.flightNo_qucheng);
		// 返程航班号
		TextView flightNo_fancheng = (TextView) pview
				.findViewById(R.id.flightNo_fancheng);
		String[] flightNos = orderDetail.getFlightNo().split("\\|");
		if (flightNos.length > 1) {
			flightNo_qucheng.setText(flightNos[0]);
			flightNo_fancheng.setVisibility(View.VISIBLE);
			flightNo_fancheng.setText(flightNos[1] + "  (返回)");
		} else {
			flightNo_qucheng.setText(flightNos[0]);
			flightNo_fancheng.setVisibility(View.GONE);
		}
		// 单程出发时间地点
		TextView dancheng_departureDate = (TextView) pview
				.findViewById(R.id.dancheng_departureDate);
		// 单程到达时间地点
		TextView dancheng_arrivalDate = (TextView) pview
				.findViewById(R.id.dancheng_arrivalDate);
		// 是否是双程票
		ImageView fanchengimg = (ImageView) pview
				.findViewById(R.id.fanchengimg);
		// 返程出发时间地点
		TextView fancheng_departureDate = (TextView) pview
				.findViewById(R.id.fancheng_departureDate);
		// 返程到达时间地点
		TextView fancheng_arrivalDate = (TextView) pview
				.findViewById(R.id.fancheng_arrivalDate);
		String[] departureDates = orderDetail.getDepartureDate().split("\\|");
		String[] arrivalDates = orderDetail.getArrivalDate().split("\\|");
		if (departureDates.length > 1) {
			dancheng_departureDate.setText(departureDates[0].split(" ")[1]
					+ " " + fromFlys[0]);
			dancheng_arrivalDate.setText(arrivalDates[0] + " " + toFlys[0]);
			fanchengimg.setVisibility(View.VISIBLE);
			fancheng_arrivalDate.setVisibility(View.VISIBLE);
			fancheng_departureDate.setVisibility(View.VISIBLE);
			fancheng_departureDate.setText(departureDates[1].split(" ")[1]
					+ " " + toFlys[0]);
			fancheng_arrivalDate.setText(arrivalDates[1] + " " + fromFlys[1]);
		} else {
			dancheng_departureDate.setText(departureDates[0].split(" ")[1]
					+ " " + fromFlys[0]);
			dancheng_arrivalDate.setText(arrivalDates[0] + " " + toFlys[0]);
			fanchengimg.setVisibility(View.GONE);
			fancheng_arrivalDate.setVisibility(View.GONE);
			fancheng_departureDate.setVisibility(View.GONE);
		}
		// 订单金额
		TextView money = (TextView) pview.findViewById(R.id.money);
		money.setText("￥" + orderDetail.getMoney());
		// 订单编号
		TextView orderId = (TextView) pview.findViewById(R.id.orderId);
		orderId.setText(orderDetail.getOrderId());
		// 机票价格
		TextView price = (TextView) pview.findViewById(R.id.price);
		String[] prices = orderDetail.getPrice().split("\\|");
		if (prices.length > 1) {
			price.setText("￥" + prices[0] + "  ￥" + prices[1] + "(返程)");
		} else {
			price.setText("￥" + prices[0]);
		}

		// 保险费用
		TextView insuranceCount = (TextView) pview
				.findViewById(R.id.insuranceCount);
		insuranceCount.setText("￥" + orderDetail.getInsuranceCount());
		// 燃油费
		TextView airportTax = (TextView) pview.findViewById(R.id.airportTax);
		String[] airportTaxs = orderDetail.getAirportTax().split("\\|");
		String[] fuelSurTaxs = orderDetail.getFuelSurTax().split("\\|");
		if (airportTaxs.length > 1) {
			airportTax.setText(airportTaxs[0] + "/" + fuelSurTaxs[0] + "  "
					+ airportTaxs[1] + "/" + fuelSurTaxs[1] + "(返程)");
		} else {
			airportTax.setText(airportTaxs[0] + "/" + fuelSurTaxs[0]);
		}

		// 经停
		TextView viaport = (TextView) pview.findViewById(R.id.viaport);
		viaport.setText(orderDetail.getViaport());
		// 取票方式
		TextView obtaintype = (TextView) pview.findViewById(R.id.obtaintype);
		obtaintype.setText(orderDetail.getObtaintype());
		// 联系人行姓名
		TextView contactName = (TextView) pview.findViewById(R.id.contactName);
		contactName.setText(orderDetail.getContactName());
		// 手机号码
		TextView contactMobile = (TextView) pview
				.findViewById(R.id.contactMobile);
		contactMobile.setText(orderDetail.getContactMobile());
		// 联系电话
		TextView contactTel = (TextView) pview.findViewById(R.id.contactTel);
		contactTel.setText(orderDetail.getContactTel());
		// 传真
		TextView contactFax = (TextView) pview.findViewById(R.id.contactFax);
		contactFax.setText(orderDetail.getContactFax());
		// 电子邮箱
		TextView contactEmail = (TextView) pview
				.findViewById(R.id.contactEmail);
		contactEmail.setText(orderDetail.getContactEmail());
		// 乘客信息
		ListViewForScrollView list_chengke = (ListViewForScrollView) pview
				.findViewById(R.id.list_chengke);
		List<PlaneOrderDetail> orderDetails = new ArrayList<PlaneOrderDetail>();
		String[] namess = orderDetail.getNames().split("\\|");
		String[] ptypes = orderDetail.getPtype().split("\\|");
		String[] pcard_types = orderDetail.getPcard_type().split("\\|");
		String[] pcard_nos = orderDetail.getPcard_no().split("\\|");
		for (int i = 0; i < pcard_nos.length; i++) {
			PlaneOrderDetail orderDetail2 = new PlaneOrderDetail();
			if (namess.length > i) {
				orderDetail2.setNames(namess[i]);
			}
			if (ptypes.length > i) {
				orderDetail2.setPtype(ptypes[i]);
			}
			if (pcard_types.length > i) {
				orderDetail2.setPcard_type(pcard_types[i]);
			}
			orderDetail2.setPcard_no(pcard_nos[i]);
			orderDetails.add(orderDetail2);
		}
		list_chengke
				.setAdapter(new PlaneAdapter(PlaneOrders.this, orderDetails));
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

	private void getPlaneOrders() {
		Util.asynTask(this, "正在加载数据", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					int index = 0;
					List<PlaneOrdersBean> list = new ArrayList<PlaneOrdersBean>();
					list = ((HashMap<Integer, List<PlaneOrdersBean>>) runData)
							.get(index++);

					List<PlaneOrdersBean> nowlist = new ArrayList<PlaneOrdersBean>();
					if (state == 1) {// 近三月
						for (PlaneOrdersBean bean : list) {
							String orderYear=bean.getOrdertime().split(" ")[0]
									.split("-")[0];
							String orderMonth = bean.getOrdertime().split(" ")[0]
									.split("-")[1];
							int nowmonth = Calendar.getInstance().get(
									Calendar.MONTH) + 1;
							int nowyear=Calendar.getInstance().get(Calendar.YEAR);
							int orderM=(Integer.parseInt(orderYear)-nowyear)*12+Integer.parseInt(orderMonth);
							if (orderM + 3 >= (nowmonth)) {
								nowlist.add(bean);
							}
						}
					} else {// 三月前
						for (PlaneOrdersBean bean : list) {
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
					planeOrdersAdapter.setList(nowlist);
					planeOrdersBeans.addAll(nowlist);
				} else {
					Toast.makeText(PlaneOrders.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.getServiceTicketOrder,
						"userid=" + UserData.getUser().getUserId()
								+ "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&page="
								+ (++currentPageShop) + "&size=" + 40);
				List<PlaneOrdersBean> list = web.getList(PlaneOrdersBean.class);
				HashMap<Integer, List<PlaneOrdersBean>> map = new HashMap<Integer, List<PlaneOrdersBean>>();
				map.put(index++, list);
				return map;
			}

		});
	}

	public void firstpageshop() {
		getPlaneOrders();
	}

	public void scrollPageshop() {
		phone_list.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= planeOrdersAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getPlaneOrders();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	private void getPlaneMassage(final String orderId, final View v) {
		Util.asynTask(this, "正在加载数据", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<PlaneOrderDetail> list = new ArrayList<PlaneOrderDetail>();
					list = ((HashMap<Integer, List<PlaneOrderDetail>>) runData)
							.get(0);
					getPopupWindow();
					startPopupWindow(list.get(0));
					distancePopup.showAtLocation(v, 0, 0, 0);
				} else {
					Toast.makeText(PlaneOrders.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(
						Web.getServiceTicketOrderDetail, "userid="
								+ UserData.getUser().getUserId()
								+ "&md5Pwd=" + UserData.getUser().getMd5Pwd()
								+ "&orderId=" + orderId);
				List<PlaneOrderDetail> list = web
						.getList(PlaneOrderDetail.class);
				HashMap<Integer, List<PlaneOrderDetail>> map = new HashMap<Integer, List<PlaneOrderDetail>>();
				map.put(0, list);
				return map;
			}

		});
	}

	public static String getWeek(Date date) {
		String[] weeks = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week_index < 0) {
			week_index = 0;
		}
		return weeks[week_index];
	}
}

class PlaneAdapter extends BaseAdapter {

	private List<PlaneOrderDetail> list;
	private Context context;
	private LayoutInflater flater;

	public PlaneAdapter(Context context, List<PlaneOrderDetail> list) {
		super();
		this.context = context;
		this.list = list;
		flater = LayoutInflater.from(context);
	}

	public void clear() {
		this.list.clear();
	}

	public void updateUI() {
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = flater.inflate(R.layout.plane_orders_dialog_item,
					null);
		}
		TextView names = (TextView) convertView.findViewById(R.id.names);
		TextView ptype = (TextView) convertView.findViewById(R.id.ptype);
		TextView pcard_type = (TextView) convertView
				.findViewById(R.id.pcard_type);
		TextView pcard_no = (TextView) convertView.findViewById(R.id.pcard_no);
		names.setText(list.get(position).getNames());
		ptype.setText(list.get(position).getPtype());
		pcard_type.setText(list.get(position).getPcard_type());
		pcard_no.setText(list.get(position).getPcard_no());
		return convertView;
	}
}
