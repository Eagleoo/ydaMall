package com.mall.serving.doubleball;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

@ContentView(R.layout.doubleball_my_lottery_order)
public class DoubleballMyLotteryOrderInfoActivity extends Activity {
	@ViewInject(R.id.topCenter)
	private TextView topCenter;

	@ViewInject(R.id.lv_MyLotteryOrder)
	private ListView lv_MyLotteryOrder;

	private List<MyLotteryOrderInfo> infolist;
	private List<MyLotteryOrder> orderlist;
	private MyLotteryOrderAdapter adapter;
	private int page = 1;
	private boolean isHave = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		infolist = new ArrayList<MyLotteryOrderInfo>();
		orderlist = new ArrayList<MyLotteryOrder>();
		setView();
		getOrderData();
	}

	/**
	 * 返回点击事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.topback)
	public void topback(View v) {
		finish();

	}

	/**
	 * 标题
	 */
	private void setView() {
		topCenter.setText("我的投注");

	}

	/**
	 * 获取每一个订单号
	 */
	private void getOrderData() {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}

		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData == null) {
					return;
				}
				HashMap<String, List<MyLotteryOrder>> map = (HashMap<String, List<MyLotteryOrder>>) runData;
				List<MyLotteryOrder> arrayList = map.get("list");
				if (null == arrayList || 0 == arrayList.size())
					Util.show("没有获取到信息！",
							DoubleballMyLotteryOrderInfoActivity.this);
				else {

					getOrderInfo();

				}

			}

			@Override
			public Serializable run() {

				Web web = new Web(Web.convience_service, Web.getMyLotteryOrder,
						"userId=" + UserData.getUser().getUserId() + "&md5Pwd="
								+ UserData.getUser().getMd5Pwd()
								+ "&type=SSQ&size=10&page=" + page);
				List<MyLotteryOrder> list = web.getList(MyLotteryOrder.class,
						"obj");
				if (list != null && list.size() < 10) {
					isHave = false;

				}
				orderlist.addAll(list);
				HashMap<String, List<MyLotteryOrder>> map = new HashMap<String, List<MyLotteryOrder>>();
				map.put("list", list);
				return map;
			}
		});

	}

	/**
	 * 获取每个订单里的每一个投注的号码
	 */
	private void getOrderInfo() {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}

		Util.asynTask(this, "正在获取我的投注信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData == null) {
					return;
				}
				HashMap<String, List<MyLotteryOrderInfo>> map = (HashMap<String, List<MyLotteryOrderInfo>>) runData;
				List<MyLotteryOrderInfo> arrayList = map.get("list");
				if (null == arrayList || 0 == arrayList.size())
					Util.show("没有获取到信息！",
							DoubleballMyLotteryOrderInfoActivity.this);
				else {

					if (adapter == null) {
						adapter = new MyLotteryOrderAdapter(
								DoubleballMyLotteryOrderInfoActivity.this,
								infolist);

						lv_MyLotteryOrder.setAdapter(adapter);
					} else {
						adapter.notifyDataSetChanged();
					}

				}

			}

			@Override
			public Serializable run() {
				for (int i = 0; i < orderlist.size(); i++) {
					MyLotteryOrder order = orderlist.get(i);
					String orderId = order.getOrderId();
					Web web = new Web(Web.convience_service,
							Web.getMyLotteryOrderInfo, "userId="
									+ UserData.getUser().getUserId()
									+ "&md5Pwd="
									+ UserData.getUser().getMd5Pwd()
									+ "&type=SSQ&size=10&page=1&orderId="
									+ orderId);
					List<MyLotteryOrderInfo> arrayList = web.getList(
							MyLotteryOrderInfo.class, "obj");
					infolist.addAll(arrayList);

				}

				HashMap<String, List<MyLotteryOrderInfo>> map = new HashMap<String, List<MyLotteryOrderInfo>>();
				map.put("list", infolist);
				return map;
			}
		});

	}

	/**
	 * 功能：我的投注列表的adapter <br>
	 * 时间： 2014-7-28<br>
	 * 备注： <br>
	 * 
	 * @author Lin.~
	 * 
	 */
	class MyLotteryOrderAdapter extends BaseAdapter {
		private Context context;
		private List<MyLotteryOrderInfo> list;

		public MyLotteryOrderAdapter(Context context,
				List<MyLotteryOrderInfo> list) {
			super();
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			if (list == null && list.size() == 0) {
				return 0;
			}
			return list.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {

			if (v == null) {
				ViewCache cache = new ViewCache();
				v = LayoutInflater
						.from(context)
						.inflate(
								R.layout.doubleball_my_lottery_order_info_content,
								null);
				cache.tv_order_info_term = (TextView) v
						.findViewById(R.id.tv_order_info_term);
				cache.tv_order_info_money = (TextView) v
						.findViewById(R.id.tv_order_info_money);
				cache.tv_order_info_betting = (TextView) v
						.findViewById(R.id.tv_order_info_betting);
				cache.tv_order_info_num = (TextView) v
						.findViewById(R.id.tv_order_info_num);
				cache.tv_order_info_playType = (TextView) v
						.findViewById(R.id.tv_order_info_playType);
				cache.tv_order_info_multiple = (TextView) v
						.findViewById(R.id.tv_order_info_multiple);
				cache.gv_order_info = (GridView) v
						.findViewById(R.id.gv_order_info);
				cache.tv_item_more = (TextView) v
						.findViewById(R.id.tv_item_more);
				cache.ll_item = v.findViewById(R.id.ll_item);
				v.setTag(cache);
			}
			ViewCache cache = (ViewCache) v.getTag();
			if (position == list.size()) {
				cache.tv_item_more.setVisibility(View.VISIBLE);
				cache.ll_item.setVisibility(View.GONE);

				cache.tv_item_more.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						page++;
						getOrderData();

					}
				});

				if (!isHave) {
					cache.tv_item_more.setVisibility(View.GONE);
				}

			} else {
				cache.tv_item_more.setVisibility(View.GONE);
				cache.ll_item.setVisibility(View.VISIBLE);
				MyLotteryOrderInfo info = list.get(position);
				cache.tv_order_info_term.setText(info.getIssueTime() + "期");

				String money = info.getMoney();
				String multiple = info.getMultiple();

				int num = (int) (Double.parseDouble(money) / (Double
						.parseDouble(multiple) * 2));
				String playType = info.getPlayType();

				if (playType.equals("DS")) {
					playType = "单式投注";
				} else if (playType.equals("FS")) {
					playType = "复式投注";
				} else if (playType.equals("DT")) {
					playType = "胆拖投注";
				}
//				"金额：" + "倍数：" + "注数：" +
				cache.tv_order_info_money.setText(money + "元");
				cache.tv_order_info_multiple.setText(multiple + "倍");
				cache.tv_order_info_num.setText( num + "注");

				cache.tv_order_info_playType.setText(playType);

				final Bettingdata data = getNumber(info);
				cache.tv_order_info_betting
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										DoubleballMyLotteryOrderInfoActivity.this,
										DoubleballBettingActivity.class);
								Bundle bundle = new Bundle();
								ArrayList<Bettingdata> basketArrayList = new ArrayList<Bettingdata>();
								basketArrayList.add(data);
								bundle.putSerializable("basketArrayList",
										(Serializable) basketArrayList);

								intent.putExtras(bundle);
								startActivity(intent);

							}
						});
				MyLotteryOrderGridAdapter adapter = new MyLotteryOrderGridAdapter(
						context, data);
				cache.gv_order_info.setAdapter(adapter);
			}
			return v;
		}

		class ViewCache {
			TextView tv_order_info_term;
			TextView tv_order_info_money;
			TextView tv_order_info_betting;

			TextView tv_order_info_num;
			TextView tv_order_info_playType;
			TextView tv_order_info_multiple;
			GridView gv_order_info;
			TextView tv_item_more;
			View ll_item;
		}

	}

	/**
	 * 功能：gridview的adapter <br>
	 * 时间： 2014-7-28<br>
	 * 备注： <br>
	 * 
	 * @author Lin.~
	 * 
	 */
	class MyLotteryOrderGridAdapter extends BaseAdapter {
		private Context context;
		private Bettingdata data;
		private int num;
		private boolean isStraight;
		private List<String> blueBallStraightList;
		private List<String> redBallStraightList;

		private List<String> blueBallDragList;
		private List<String> redBallCourageList;
		private List<String> redBallDragList;

		public MyLotteryOrderGridAdapter(Context context, Bettingdata data) {
			super();
			this.context = context;
			this.data = data;
			this.isStraight = data.isStraight();
			if (isStraight) {
				blueBallStraightList = data.getBlueBallStraightList();
				redBallStraightList = data.getRedBallStraightList();

				num = blueBallStraightList.size() + redBallStraightList.size();

			} else {

				blueBallDragList = data.getBlueBallDragList();
				redBallCourageList = data.getRedBallCourageList();
				redBallDragList = data.getRedBallDragList();
				num = blueBallDragList.size() + redBallCourageList.size()
						+ redBallDragList.size();
			}

		}

		@Override
		public int getCount() {
			if (data == null) {
				return 0;
			}
			return num;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {

			if (v == null) {
				ViewCache cache = new ViewCache();
				v = LayoutInflater
						.from(context)
						.inflate(
								R.layout.doubleball_my_lottery_order_info_content_gridviewitem,
								null);
				cache.cb_ball_number_red = (CheckBox) v
						.findViewById(R.id.cb_ball_number_red);
				cache.cb_ball_number_blue = (CheckBox) v
						.findViewById(R.id.cb_ball_number_blue);

				v.setTag(cache);
			}
			ViewCache cache = (ViewCache) v.getTag();

			if (isStraight) {
				int size = redBallStraightList.size();
				if (position < size) {
					cache.cb_ball_number_red.setVisibility(View.VISIBLE);
					cache.cb_ball_number_blue.setVisibility(View.GONE);
					String string = redBallStraightList.get(position);
					cache.cb_ball_number_red.setText(string);

				} else {
					cache.cb_ball_number_red.setVisibility(View.GONE);
					cache.cb_ball_number_blue.setVisibility(View.VISIBLE);

					String string = blueBallStraightList.get(position - size);
					cache.cb_ball_number_blue.setText(string);
				}

			} else {
				int sizeDrag = redBallDragList.size();
				int sizeCourage = redBallCourageList.size();
				if (position < sizeCourage) {

					String stringDrag = redBallCourageList.get(position);
					cache.cb_ball_number_red.setVisibility(View.VISIBLE);
					cache.cb_ball_number_blue.setVisibility(View.GONE);
					cache.cb_ball_number_red.setText(stringDrag);
					cache.cb_ball_number_red.setChecked(true);

				} else if (position < sizeDrag + sizeCourage) {
					String stringCourage = redBallDragList.get(position
							- sizeCourage);
					cache.cb_ball_number_red.setVisibility(View.VISIBLE);
					cache.cb_ball_number_blue.setVisibility(View.GONE);
					cache.cb_ball_number_red.setText(stringCourage);
					cache.cb_ball_number_red.setChecked(false);

				} else {

					cache.cb_ball_number_red.setVisibility(View.GONE);
					cache.cb_ball_number_blue.setVisibility(View.VISIBLE);

					String string = blueBallDragList.get(position
							- (sizeDrag + sizeCourage));

					cache.cb_ball_number_blue.setText(string);

				}

			}

			return v;
		}

		class ViewCache {
			CheckBox cb_ball_number_red;
			CheckBox cb_ball_number_blue;

		}

	}

	/**
	 * 得到订单里的投注号码
	 * 
	 * @param info
	 * @return
	 */
	private Bettingdata getNumber(MyLotteryOrderInfo info) {

		String betContent = info.getBetContent();

		String money = info.getMoney();
		String multiple = info.getMultiple();

		int num = (int) (Double.parseDouble(money) / (Double
				.parseDouble(multiple) * 2));
		String playType = info.getPlayType();

		Bettingdata data = new Bettingdata();
		data.setCount(num);
		data.setMoney(num * 2 + "元");
		if (playType.equals("FS") || playType.equals("DS")) {
			String[] split = betContent.split("\\|");
			if (split.length >= 2) {
				String[] reds = split[0].split(",");
				String[] blues = split[1].split(",");
				List<String> redBallStraightList = StringArrayToList(reds);
				List<String> blueBallStraightList = StringArrayToList(blues);
				data.setRedBallStraightList(redBallStraightList);
				data.setBlueBallStraightList(blueBallStraightList);
				data.setStraight(true);
			}

		} else if (playType.equals("DT")) {
			String[] split = betContent.split("\\|");
			if (split.length >= 2) {
				String red = split[0];
				String[] blues = split[1].split(",");
				int indexOf = red.indexOf(")");
				String redCourage= red.substring(1, indexOf);
				String  redDrag = red.substring(indexOf + 2);
				String[] redDrags = redDrag.split(",");
				String[] redCourages = redCourage.split(",");
				List<String> redBallDragList = StringArrayToList(redDrags);
				List<String> redBallCourageList = StringArrayToList(redCourages);
				List<String> blueBallDragList = StringArrayToList(blues);

				data.setBlueBallDragList(blueBallDragList);
				data.setRedBallCourageList(redBallCourageList);
				data.setRedBallDragList(redBallDragList);
				data.setStraight(false);
			}

		}
		return data;

	}

	/**
	 * 将字符串数组转成list
	 * 
	 * @param str
	 * @return
	 */
	private List<String> StringArrayToList(String[] str) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < str.length; i++) {
			list.add(str[i]);
		}

		return list;

	}

}
