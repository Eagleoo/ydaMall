package com.mall.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.model.IssueInfo;
import com.mall.model.LotteryOrdersBean;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.ListViewForScrollView;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * 功能：彩票订单信息 时间：2014年8月8日
 * 
 * @author Administrator
 * 
 */
public class LotteryOrders extends Activity {
	@ViewInject(R.id.phone_list)
	private ListView phone_list;
	private PopupWindow distancePopup = null;
	private int currentPageShop = 0;
	private LotteryOrdersAdapter lotteryOrdersAdapter;
	private List<LotteryOrdersBean> lotteryOrdersBeans = new ArrayList<LotteryOrdersBean>();
	private List<IssueInfo> issueInfos = new ArrayList<IssueInfo>();
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
		setContentView(R.layout.lottery_orders);
		ViewUtils.inject(this);
		if (lotteryOrdersAdapter == null) {
			lotteryOrdersAdapter = new LotteryOrdersAdapter(this);
			phone_list.setAdapter(lotteryOrdersAdapter);
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
			jinsanyue.setTextColor(getResources().getColor(
					R.color.new_headertop));
			jinsanyue.setBackgroundColor(getResources().getColor(R.color.bg));
			sanyue_qian.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			sanyuenei.setVisibility(View.VISIBLE);
			sanyueqian.setVisibility(View.INVISIBLE);
			sanyue_qian.setTextColor(getResources().getColor(R.color.bg));
			lotteryOrdersBeans.clear();
			currentPageShop = 0;
			lotteryOrdersAdapter = new LotteryOrdersAdapter(this);
			phone_list.setAdapter(lotteryOrdersAdapter);
			firstpageshop();
			scrollPageshop();
			break;
		case R.id.fi2:
			state = 2;
			jinsanyue.setTextColor(getResources().getColor(R.color.bg));
			sanyuenei.setVisibility(View.INVISIBLE);
			sanyueqian.setVisibility(View.VISIBLE);
			sanyue_qian.setTextColor(getResources().getColor(
					R.color.new_headertop));
			jinsanyue.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			sanyue_qian.setBackgroundColor(getResources().getColor(R.color.bg));
			lotteryOrdersBeans.clear();
			currentPageShop = 0;
			lotteryOrdersAdapter = new LotteryOrdersAdapter(this);
			phone_list.setAdapter(lotteryOrdersAdapter);
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
		getOneLottery(lotteryOrdersBeans.get(position).getOrderId(),
				lotteryOrdersBeans.get(position).getType(), view,
				lotteryOrdersBeans.get(position));
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(final List<LotteryOrdersBean> list,
			LotteryOrdersBean lottery, List<Map<String, String>> maps) {
		View pview = getLayoutInflater().inflate(
				R.layout.lottery_orders_dialog, null, false);
		ViewUtils.inject(pview);
		TextView top_back = (TextView) pview.findViewById(R.id.top_back);
		ScrollView scrollview = (ScrollView) pview
				.findViewById(R.id.scrollview);
		scrollview.smoothScrollTo(0, 0);
		// 投注列表
		ListViewForScrollView list_touzhu = (ListViewForScrollView) pview
				.findViewById(R.id.list_touzhu);
		list_touzhu.setAdapter(new LotteryAdapter(LotteryOrders.this, list));
		// 彩票期数
		TextView lottery_number = (TextView) pview
				.findViewById(R.id.lottery_number);
		// 支付时间
		TextView zhifu_time = (TextView) pview.findViewById(R.id.zhifu_time);
		// 投注金额
		TextView touzhu = (TextView) pview.findViewById(R.id.touzhu);
		// 中奖金额
		TextView zhongjiang = (TextView) pview.findViewById(R.id.zhongjiang);
		// 订单状态
		TextView order_state = (TextView) pview.findViewById(R.id.order_state);
		// 开奖号码
		TextView kaijiangnumber = (TextView) pview
				.findViewById(R.id.kaijiangnumber);
		// 订单编号
		TextView order_name = (TextView) pview.findViewById(R.id.order_name);
		LinearLayout allqiu = (LinearLayout) pview.findViewById(R.id.allqiu);
		TextView hong1 = (TextView) pview.findViewById(R.id.hong1);
		TextView hong2 = (TextView) pview.findViewById(R.id.hong2);
		TextView hong3 = (TextView) pview.findViewById(R.id.hong3);
		TextView hong4 = (TextView) pview.findViewById(R.id.hong4);
		TextView hong5 = (TextView) pview.findViewById(R.id.hong5);
		TextView hong6 = (TextView) pview.findViewById(R.id.hong6);
		TextView lanqiu = (TextView) pview.findViewById(R.id.lan1);
		order_name.setText(lottery.getOrderId());
		lottery_number.setText("彩票信息(第" + list.get(0).getIssueTime() + "期)");
		zhifu_time.setText(lottery.getDate());
		touzhu.setText(lottery.getMoney());
		if (maps.size() > 0) {
			String string = "";
			zhongjiang.setText(string);
		} else {
			zhongjiang.setText("");
		}

		order_state.setText(list.get(0).getPay());
		String string = list.get(0).getIssueTime();
		if (string.length() == 7) {
			string = string.substring(0, string.length() - 1) + "0"
					+ string.substring(string.length() - 1, string.length());

		}
		if (issueInfos.size() < 1) {
			kaijiangnumber.setText("等待开奖");
			zhongjiang.setText("等待开奖");
			allqiu.setVisibility(View.GONE);
		} else {
			if (TextUtils.isEmpty(issueInfos.get(0).getBonusCode())) {
				kaijiangnumber.setText("等待开奖");
				zhongjiang.setText("等待开奖");
				allqiu.setVisibility(View.GONE);
			} else {
				kaijiangnumber.setVisibility(View.GONE);
				String[] kaijiangnumbers = issueInfos.get(0).getBonusCode()
						.split("\\|");
				String[] hongqius = kaijiangnumbers[0].split(",");
				lanqiu.setText(kaijiangnumbers[1]);
				hong1.setText(hongqius[0]);
				hong2.setText(hongqius[1]);
				hong3.setText(hongqius[2]);
				hong4.setText(hongqius[3]);
				hong5.setText(hongqius[4]);
				hong6.setText(hongqius[5]);
			}

		}

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
					List<LotteryOrdersBean> list = new ArrayList<LotteryOrdersBean>();
					list = ((HashMap<Integer, List<LotteryOrdersBean>>) runData)
							.get(index++);
					List<LotteryOrdersBean> nowlist = new ArrayList<LotteryOrdersBean>();
					if (state == 1) {// 近三月
						for (LotteryOrdersBean bean : list) {
							String orderYear=bean.getDate().split(" ")[0]
									.split("-")[0];
							String orderMonth = bean.getDate().split(" ")[0]
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
						for (LotteryOrdersBean bean : list) {
							String orderYear=bean.getDate().split(" ")[0]
									.split("-")[0];
							String orderMonth = bean.getDate().split(" ")[0]
									.split("-")[1];
							int nowmonth = Calendar.getInstance().get(
									Calendar.MONTH) + 1;
							int nowyear=Calendar.getInstance().get(Calendar.YEAR);
							int orderM=(Integer.parseInt(orderYear)-nowyear)*12+Integer.parseInt(orderMonth);
							if (orderM+ 3 < nowmonth) {
								nowlist.add(bean);
							}
						}
					}
					lotteryOrdersBeans.addAll(nowlist);
					lotteryOrdersAdapter.setList(nowlist);
					for (int i = 0; i < list.size(); i++) {
						System.out.println(list.get(i).getOrderId());
					}
				} else {
					Toast.makeText(LotteryOrders.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.convience_service, Web.getMyLotteryOrder,
						"userid=" + UserData.getUser().getUserId() + "&md5Pwd="
								+ UserData.getUser().getMd5Pwd() + "&page="
								+ (++currentPageShop) + "&size=" + 20
								+ "&type=");
				List<LotteryOrdersBean> list = web
						.getList(LotteryOrdersBean.class);
				HashMap<Integer, List<LotteryOrdersBean>> map = new HashMap<Integer, List<LotteryOrdersBean>>();
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
				if (lastItem >= lotteryOrdersAdapter.getCount()
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

	/**
	 * 获得单个订单的详细信息
	 */
	private void getOneLottery(final String orderId, final String type,
			final View view, final LotteryOrdersBean lotteryOrdersBean) {
		Util.asynTask(this, "正在加载数据", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					int index = 0;
					List<LotteryOrdersBean> list = new ArrayList<LotteryOrdersBean>();
					list = ((HashMap<Integer, List<LotteryOrdersBean>>) runData)
							.get(1);
					getLottery(list.get(0).getIssueTime(), type, view,
							lotteryOrdersBean, list);
				} else {
					Toast.makeText(LotteryOrders.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.convience_service,
						Web.getMyLotteryOrderInfo, "userid="
								+ UserData.getUser().getUserId() + "&md5Pwd="
								+ UserData.getUser().getMd5Pwd() + "&page=1"
								+ "&size=" + 999999999 + "&type=" + type
								+ "&orderId=" + orderId);
				List<LotteryOrdersBean> list = web
						.getList(LotteryOrdersBean.class);
				HashMap<Integer, List<LotteryOrdersBean>> map = new HashMap<Integer, List<LotteryOrdersBean>>();
				map.put(1, list);
				return map;
			}

		});
	}

	/**
	 * 获得中奖的信息
	 */
	private void getLottery(final String issueNumber, final String type,
			final View view, final LotteryOrdersBean lotteryOrdersBean,
			final List<LotteryOrdersBean> lotteryOrdersBeans) {
		Util.asynTask(this, "正在加载数据", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<Map<String, String>> zhongjiang = new ArrayList<Map<String, String>>();
					List<IssueInfo> list = new ArrayList<IssueInfo>();
					list = ((HashMap<Integer, List<IssueInfo>>) runData).get(1);
					if (list.size() < 1) {

					} else {
						issueInfos = list;
					}
					getPopupWindow();
					startPopupWindow(lotteryOrdersBeans, lotteryOrdersBean,
							zhongjiang);
					distancePopup.showAtLocation(view, 0, 0, 0);
				} else {
					Toast.makeText(LotteryOrders.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.convience_service, Web.getLotteryInfo,
						"type=" + type + "&issueNumber=" + issueNumber);
				List<IssueInfo> list = web
						.getList(IssueInfo.class, "issueinfo");
				HashMap<Integer, List<IssueInfo>> map = new HashMap<Integer, List<IssueInfo>>();
				map.put(1, list);
				return map;
			}
		});
	}

	/**
	 * 
	 * 
	 * @param a
	 *            数据数组
	 * @param num
	 *            M选N中 N的个数
	 * @return
	 */
	private List<String> combine(String[] a, int num) {
		List<String> list = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		String[] b = new String[a.length];
		for (int i = 0; i < b.length; i++) {
			if (i < num) {
				b[i] = "1";
			} else
				b[i] = "0";
		}

		int point = 0;
		int nextPoint = 0;
		int count = 0;
		int sum = 0;
		String temp = "1";
		while (true) {
			// 判断是否全部移位完毕
			for (int i = b.length - 1; i >= b.length - num; i--) {
				if (b[i].equals("1"))
					sum += 1;
			}
			// 根据移位生成数据
			for (int i = 0; i < b.length; i++) {
				if (b[i].equals("1")) {
					point = i;
					sb.append(a[point]);
					sb.append(",");
					count++;
					if (count == num)
						break;
				}
			}
			String s = sb.substring(0, sb.length() - 1);
			// 往返回值列表添加数据
			list.add(s);

			// 当数组的最后num位全部为1 退出
			if (sum == num) {
				break;
			}
			sum = 0;

			// 修改从左往右第一个10变成01
			for (int i = 0; i < b.length - 1; i++) {
				if (b[i].equals("1") && b[i + 1].equals("0")) {
					point = i;
					nextPoint = i + 1;
					b[point] = "0";
					b[nextPoint] = "1";
					break;
				}
			}
			// 将 i-point个元素的1往前移动 0往后移动
			for (int i = 0; i < point - 1; i++)
				for (int j = i; j < point - 1; j++) {
					if (b[i].equals("0")) {
						temp = b[i];
						b[i] = b[j + 1];
						b[j + 1] = temp;
					}
				}
			// 清空 StringBuffer
			sb.setLength(0);
			count = 0;
		}
		//
		System.out.println("数据长度 " + list.size());
		return list;

	}

	/**
	 * 双色球中奖判断
	 */
	public static String shuangseqiu(Map<String, String[]> changeNumber,
			Map<String, String[]> winningNumber) {
		String[] strings = new String[] { "未中奖", "六等奖", "五等奖", "四等奖", "三等奖",
				"二等奖", "一等奖" };
		// 正常投注
		if (changeNumber.size() == 2) {
			String[] changeLanqiu = changeNumber.get("lanqiu");
			String[] changeHongqiu = changeNumber.get("hongqiu");
			String[] winningLanqiu = winningNumber.get("lanqiu");
			String[] winningHongqiu = winningNumber.get("hongqiu");
			Set<String> lanqiuNumber = new HashSet<String>();
			Set<String> hongqiuNumber = new HashSet<String>();
			// 匹配出相同的球
			for (int i = 0; i < changeLanqiu.length; i++) {
				for (int j = 0; j < winningLanqiu.length; j++) {
					if (changeLanqiu[i].equals(winningLanqiu[j])) {
						lanqiuNumber.add(changeLanqiu[i]);
					}
				}
			}
			for (int i = 0; i < changeHongqiu.length; i++) {
				for (int j = 0; j < winningHongqiu.length; j++) {
					if (changeHongqiu[i].equals(winningHongqiu[j])) {
						hongqiuNumber.add(changeHongqiu[i]);
					}
				}
			}
			if (lanqiuNumber.size() == 6 && hongqiuNumber.size() == 1) {
				return "一等奖";
			}
			if (lanqiuNumber.size() == 6 && hongqiuNumber.size() == 0) {
				return "二等奖";
			}
			if (lanqiuNumber.size() == 5 && hongqiuNumber.size() == 1) {
				return "三等奖";
			}
			if ((lanqiuNumber.size() == 5 && hongqiuNumber.size() == 0)
					|| (lanqiuNumber.size() == 4 && hongqiuNumber.size() == 1)) {
				return "四等奖";
			}
			if ((lanqiuNumber.size() == 4 && hongqiuNumber.size() == 0)
					|| (lanqiuNumber.size() == 3 && hongqiuNumber.size() == 1)) {
				return "五等奖";
			}
			if ((lanqiuNumber.size() == 2 && hongqiuNumber.size() == 1)
					|| (lanqiuNumber.size() == 1 && hongqiuNumber.size() == 1)
					|| (lanqiuNumber.size() == 0 && hongqiuNumber.size() == 1)) {
				return "六等奖";
			}
		}
		return "未中奖";
	}

	/**
	 * 将号码拆分成单注的号码
	 */
	private List<String[]> getNumber(LotteryOrdersBean lottery) {
		if (lottery.getlType().equalsIgnoreCase("SSQ")) {
			/*
			 * if (lottery.getPlayType().equalsIgnoreCase("DS")) {
			 * List<String[]> nowList = new ArrayList<String[]>(); // 蓝球
			 * String[] lan =
			 * lottery.getBetContent().split("\\|")[1].split(","); // 红球 String
			 * hongs = lottery.getBetContent().split("\\|")[0]; hongs = hongs +
			 * "," + lan; String[] qius = hongs.split(","); nowList.add(qius);
			 * return nowList; } else
			 */if (lottery.getPlayType().equalsIgnoreCase("FS")
					|| lottery.getPlayType().equalsIgnoreCase("DS")) {
				// 蓝球
				String[] lan = lottery.getBetContent().split("\\|")[1]
						.split(",");
				// 红球
				String[] hongs = lottery.getBetContent().split("\\|")[0]
						.split(",");
				List<String> list = combine(hongs, 6);
				List<String[]> nowList = new ArrayList<String[]>();
				for (int i = 0; i < lan.length; i++) {
					for (String string : list) {
						string = string + "," + lan[i];
						String[] strings = string.split(",");
						nowList.add(strings);
					}
				}

				return nowList;
			} else if (lottery.getPlayType().equalsIgnoreCase("DT")) {
				// 蓝球
				String[] lan = lottery.getBetContent().split("\\|")[1]
						.split(",");
				// 红球
				String[] hongs = lottery.getBetContent().split("\\|")[0]
						.split(",");
				String danma = lottery.getBetContent().split("\\|")[0]
						.substring(
								1,
								lottery.getBetContent().split("\\|")[0]
										.length()).split("\\),")[0];
				String tuoma = lottery.getBetContent().split("\\|")[0]
						.substring(
								1,
								lottery.getBetContent().split("\\|")[0]
										.length()).split("\\),")[1];
				List<String> list = combine(tuoma.split(","),
						6 - danma.split(",").length);
				List<String[]> nowList = new ArrayList<String[]>();
				for (int i = 0; i < lan.length; i++) {
					for (String string : list) {
						string = danma + "," + string + "," + lan[i];
						String[] strings = string.split(",");
						nowList.add(strings);
					}
				}

				return nowList;

			}
		}
		return null;
	}
}

class LotteryAdapter extends BaseAdapter {

	private List<LotteryOrdersBean> list;
	private Context context;
	private LayoutInflater flater;

	public LotteryAdapter(Context context, List<LotteryOrdersBean> list) {
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

	public void setList(List<LotteryOrdersBean> list) {
		this.list.addAll(list);
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
			convertView = flater.inflate(R.layout.lottery_orders_dialog_item,
					null);
		}
		LinearLayout item = (LinearLayout) convertView
				.findViewById(R.id.staff_manager_user_reward_item);
		TextView lottery_name = (TextView) convertView
				.findViewById(R.id.lottery_name);
		TextView zhushu = (TextView) convertView.findViewById(R.id.zhushu);
		System.out.println(list.get(position).getBetContent());
		// lottery_name.setText(list.get(position).getBetContent());
		if (list.get(position).getPlayType().equalsIgnoreCase("DT")) {
			lottery_name.setText(list.get(position).getBetContent() + "  胆拖"
					+ "  " + list.get(position).getMultiple() + "注");
		} else if (list.get(position).getPlayType().equalsIgnoreCase("DS")) {
			lottery_name.setText(list.get(position).getBetContent() + "  单式"
					+ "  " + list.get(position).getMultiple() + "注");
		} else if (list.get(position).getPlayType().equalsIgnoreCase("FS")) {
			lottery_name.setText(list.get(position).getBetContent() + "  复式"
					+ "  " + list.get(position).getMultiple() + "注");
		}
		zhushu.setText(list.get(position).getMultiple() + "注");
		if (position % 2 == 0) {
			item.setBackgroundColor(context.getResources().getColor(
					R.color.gray));
		} else {
			item.setBackgroundColor(context.getResources().getColor(R.color.bg));
		}
		return convertView;
	}
}
