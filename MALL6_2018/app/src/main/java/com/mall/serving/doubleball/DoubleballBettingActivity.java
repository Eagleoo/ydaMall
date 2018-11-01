package com.mall.serving.doubleball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.doubleball.addandsubview.AddAndSubView;
import com.mall.serving.doubleball.addandsubview.AddAndSubView.OnNumChangeListener;
import com.mall.util.Combination;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.doubleball_betting)
public class DoubleballBettingActivity extends Activity {

	@ViewInject(R.id.topCenter)
	private TextView topCenter;
	@ViewInject(R.id.iv_betting_clear)
	private ImageView iv_betting_clear;
	@ViewInject(R.id.lv_bettting)
	private ListView lv_bettting;
	@ViewInject(R.id.betting_times)
	private AddAndSubView betting_times;
	@ViewInject(R.id.betting_term)
	private AddAndSubView betting_term;

	@ViewInject(R.id.tv_betting_moneys)
	private TextView tv_betting_money;
	@ViewInject(R.id.cb_betting_agree)
	private CheckBox cb_betting_agree;

	private BettingListAdapter adapter;
	// 储存每一注号码的list
	private List<Bettingdata> basketArrayList;
	// 存储拼接好的号码
	private List<String> stringList;
	// 分开存储红蓝两种号码
	private List<BettingNum> list;

	// 最大投注数
	private final int bettingMax = 50;
	// 总投注数
	private int count;
	// 倍数
	private int multiple = 1;
	// 期数
	private int continuous = 1;

	private List<String> basketList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		basketArrayList = new ArrayList<Bettingdata>();
		list = new ArrayList<BettingNum>();
		stringList = new ArrayList<String>();
		basketList = new ArrayList<String>();
		setView();
		getIntentData(null);
		listSetAdapter();
		setListener();
	}

	/**
	 * 返回点击事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.topback)
	public void topback(View v) {
		if (basketArrayList.size() == 0) {
			finish();
		} else {
			new DoubleballDialog("返回将会清除数据，确定返回？", this, "确定", "取消",
					new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();

				}
			}, null).show();
		}

	}

	/**
	 * 投注事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_doubleball_betting)
	public void doubleballBetting(View v) {

		if (basketArrayList.size() == 0) {

			Util.show("投注数为0，不可投注", this);
			return;
		}
		if (!cb_betting_agree.isChecked()) {

			Util.show("请查看并同意《彩票投注规则》", this);
			return;
		}
		User user = UserData.getUser();
		if (user == null) {

		
			
			new DoubleballDialog("您还没登录，现在去登录吗？", this, "确定", "取消",
					new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(
									DoubleballBettingActivity.this,
									LoginFrame.class);

							startActivity(intent);

						}
					}, null).show();

		} else if ((Util.isNull(UserData.getUser().getIdNo())&&Util.isNull(UserData.getUser().getPassport()))
				|| Util.isNull(user.getMobilePhone())
				|| Util.isNull(user.getName())) {

			
			
			new DoubleballDialog("您还没完善身份信息，现在去完善吗？", this, "确定", "取消",
					new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					toActivity(DoubleballIdentityInformationActivity.class);

				}
			}, null).show();

		} else {

			toActivity(DoubleballOrderPayActivity.class);
		}
	}

	private void toActivity(Class cl) {

		List<LotteryOrderData> lotteryList = new ArrayList<LotteryOrderData>();
		int issueNumber = Web.issueNumber;
		for (int k = 0; k < continuous; k++) {

			for (int i = 0; i < basketArrayList.size(); i++) {
				Bettingdata bettingdata = basketArrayList.get(i);
				String betContent = stringList.get(i);

				LotteryOrderData data = new LotteryOrderData();
				boolean isStraight = bettingdata.isStraight();
				data.setBetContent(betContent);
				data.setIssueNumber(issueNumber + "");

				if (isStraight) {
					if (bettingdata.getRedBallStraightList().size() <= 6
							&& bettingdata.getBlueBallStraightList().size() == 1) {
						data.setPlayType("DS");
					} else {
						data.setPlayType("FS");
					}

				} else {
					data.setPlayType("DT");
				}
				int countItem = bettingdata.getCount();
				int money = countItem * 2 * multiple;

				if (money > 20000) {
					for (int j = 0; j < multiple; j++) {

						data.setMultiple(1 + "");

						data.setTotalmoney((countItem * 2) + "");

						lotteryList.add(data);
					}

				} else {
					data.setMultiple(multiple + "");

					data.setTotalmoney(money + "");
					lotteryList.add(data);
				}

			}
			issueNumber++;
		}

		if (lotteryList.size()>50) {
			Util.show("投注数不可大于50笔！", this);
			return;
		}
		Intent intent = new Intent(this, cl);
		Bundle bundle = new Bundle();

		bundle.putSerializable("lotteryList", (Serializable) lotteryList);
		bundle.putInt("money", count * 2 * multiple * continuous);

		intent.putExtras(bundle);
		startActivity(intent);

	}

	/**
	 * 彩票投注规则点击事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_betting_rule)
	public void bettingRule(View v) {
		new BetRulePopupWindow(this, v);

	}

	/**
	 * 功能：彩票投注规则窗口<br>
	 * 时间： 2014-7-21<br>
	 * 备注： <br>
	 * 
	 * @author Lin.~
	 * 
	 */
	class BetRulePopupWindow extends PopupWindow {
		public BetRulePopupWindow(final Context context, View parent) {
			super(context);
			View inflate = LayoutInflater.from(context).inflate(
					R.layout.doubleball_introduce_popupwindow, null);

			TextView tv_introduce_title = (TextView) inflate
					.findViewById(R.id.tv_introduce_title);
			TextView tv_introduce_content = (TextView) inflate
					.findViewById(R.id.tv_introduce_content);
			TextView tv_introduce_sure = (TextView) inflate
					.findViewById(R.id.tv_introduce_sure);

			View sv_introduce = inflate.findViewById(R.id.sv_introduce);
			WebView wv_introduce = (WebView) inflate
					.findViewById(R.id.wv_introduce);
			sv_introduce.setVisibility(View.GONE);
			wv_introduce.setVisibility(View.VISIBLE);
			wv_introduce.loadUrl("file:///android_asset/lotteryRule.html");
			tv_introduce_title.setText("彩票投注规则");

			tv_introduce_sure.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					cb_betting_agree.setChecked(true);
					dismiss();

				}
			});

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(inflate);

			showAtLocation(parent, Gravity.CENTER, 0, 0);
		}

	}

	/*
	 * 点击返回事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			if (basketArrayList.size() == 0) {
				finish();
			} else {
				new DoubleballDialog("返回将会清除数据，确定返回？", this, "确定", "取消",
						new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				}, null).show();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	

	private void setView() {
		topCenter.setText("双色球投注");
		cb_betting_agree.setChecked(true);

	}

	/**
	 * 给AddAndSubView设置监听
	 */
	private void setListener() {

		iv_betting_clear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (basketArrayList.size() > 0) {

					
					
					
					new DoubleballDialog("是否清空所有投注？", DoubleballBettingActivity.this,
							"确定", "取消", new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									basketArrayList.clear();
									list.clear();
									stringList.clear();
									adapter.notifyDataSetChanged();

									Util.show("清空成功",
											DoubleballBettingActivity.this);
									count = 0;
									setMoney();

								}
							}, null).show();
				}

			}
		});

		OnNumChangeListener numListener = new AddAndSubView.OnNumChangeListener() {

			@Override
			public void onNumChange(View view, int num) {
				AddAndSubView v = (AddAndSubView) view;
				if (view == betting_times) {

					if (num > 50) {
						num = 50;
						v.setNum(num);
						Util.show("最高投注50倍", DoubleballBettingActivity.this);

					}
					multiple = num;
					setMoney();
				} else if (view == betting_term) {
					if (num > 13) {
						num = 13;
						v.setNum(num);

						Util.show("最多连买13期", DoubleballBettingActivity.this);
					}
					continuous = num;
					setMoney();
				}

			}
		};

		betting_times.setOnNumChangeListener(numListener);
		betting_term.setOnNumChangeListener(numListener);
	}

	/**
	 * 给列表设置adapter
	 */
	private void listSetAdapter() {

		// listAddData(redNumList, blueNumList);
		adapter = new BettingListAdapter(this);
		lv_bettting.setAdapter(adapter);

	}

	/**
	 * 得到传过来的list
	 */
	private void getIntentData(Intent data) {
		Intent intent = null;
		if (data != null) {
			intent = data;
		} else {
			intent = getIntent();
		}
		if (intent != null) {
			Bundle bundle = intent.getExtras();

			ArrayList<Bettingdata> temp = (ArrayList<Bettingdata>) bundle
					.getSerializable("basketArrayList");
			if (temp == null || temp.size() == 0) {
				return;
			}
			basketArrayList.addAll(temp);

			listToBetString(temp);

		}

	}

	/**
	 * 将选号页面的数据转成需要的字符串等
	 * 
	 * @param betList
	 */
	private void listToBetString(List<Bettingdata> betList) {

		for (int i = 0; i < betList.size(); i++) {
			Bettingdata data = betList.get(i);
			boolean isStraight = data.isStraight();
			System.out.println(isStraight);
			if (isStraight) {
				List<String> blueBallStraightList = data
						.getBlueBallStraightList();
				List<String> redBallStraightList = data
						.getRedBallStraightList();

				String blueString = Combination.arrayToString(
						Combination.listToString(blueBallStraightList), ",");
				String redString = Combination.arrayToString(
						Combination.listToString(redBallStraightList), ",");

				BettingNum bettingNum = new BettingNum();
				bettingNum.setBlue(blueString);
				bettingNum.setRed(redString);
				list.add(bettingNum);

				String string = redString + "|" + blueString;
				stringList.add(string);

			} else {
				List<String> blueBallDragList = data.getBlueBallDragList();
				List<String> redBallCourageList = data.getRedBallCourageList();
				List<String> redBallDragList = data.getRedBallDragList();
				String blueString = Combination.arrayToString(
						Combination.listToString(blueBallDragList), ",");
				String redBallCourageString = Combination.arrayToString(
						Combination.listToString(redBallCourageList), ",");
				String redBallDragString = Combination.arrayToString(
						Combination.listToString(redBallDragList), ",");
				String redString = "(" + redBallCourageString + "),"
						+ redBallDragString;
				BettingNum bettingNum = new BettingNum();
				bettingNum.setBlue(blueString);
				bettingNum.setRed(redString);
				list.add(bettingNum);

				String string = redString + "|" + blueString;
				stringList.add(string);

			}

			count += data.getCount();

		}
		setMoney();

	}

	/**
	 * 设置总共的金额
	 */
	private void setMoney() {
		Spanned html = Html.fromHtml("<font color=\"#8d8d8d\">" + "总共"
				+ "</font><font color=\"#ff0000\">" + count + "注"
				+ "</font><font color=\"#8d8d8d\">" + "，合计"
				+ "</font><font color=\"#ff0000\">" + count * 2 * multiple
				* continuous + "元" + "</font>");
		tv_betting_money.setText(html);

	}

	/**
	 * 增加人工选号
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_betting_manpower)
	public void bettingManpower(View v) {
		Intent intent = new Intent(this,
				DoubleballNumericalSelectionActivity.class);
		intent.putExtra("fromBetting", true);
		intent.putExtra("bettingSum", basketArrayList.size());

		startActivityForResult(intent, 0);

	}

	/*
	 * 人工选号的回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			getIntentData(data);

			listSetAdapter();

		}

	}

	/**
	 * 添加一注机选
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_betting_machine)
	public void bettingMachine(View v) {
		if (basketArrayList.size() >= bettingMax) {

			Util.show("投注列表不能超过50", this);
			return;

		}
		List<String> redList = Combination.machineNumber(6, 33);
		List<String> blueList = Combination.machineNumber(1, 16);
		Bettingdata bettingdata = new Bettingdata();
		bettingdata.setStraight(true);
		bettingdata.setRedBallStraightList(redList);
		bettingdata.setBlueBallStraightList(blueList);
		bettingdata.setCount(1);
		bettingdata.setMoney("2元");
		basketArrayList.add(bettingdata);

		String red = Combination.arrayToString(
				Combination.listToString(redList), ",");

		BettingNum bettingNum = new BettingNum();
		bettingNum.setBlue(blueList.get(0));
		bettingNum.setRed(red);
		list.add(bettingNum);
		stringList.add(red + "|" + blueList.get(0));

		adapter.notifyDataSetChanged();
		count += 1;

		setMoney();

	}

	/**
	 * 功能：投注列表的adapter <br>
	 * 时间： 2014-7-19<br>
	 * 备注： <br>
	 * 
	 * @author Lin.~
	 * 
	 */
	class BettingListAdapter extends BaseAdapter {
		private Context context;

		public BettingListAdapter(Context context) {
			super();
			this.context = context;

		}

		@Override
		public int getCount() {
			if (list == null) {
				return 0;
			}
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
		public View getView(final int postion, View v, ViewGroup arg2) {
			if (v == null) {
				ViewCache cache = new ViewCache();
				v = LayoutInflater.from(context).inflate(
						R.layout.doubleball_betting_item, null);
				cache.tv_betting_item = (TextView) v
						.findViewById(R.id.tv_betting_item);
				cache.tv_bet_num = (TextView) v.findViewById(R.id.tv_bet_num);
				cache.tv_bet_money = (TextView) v
						.findViewById(R.id.tv_bet_money);

				cache.iv_doubleball_fork = (ImageView) v
						.findViewById(R.id.iv_doubleball_fork);
				v.setTag(cache);

			}

			final ViewCache cache = (ViewCache) v.getTag();
			final BettingNum bettingNum = list.get(postion);
			String blue = bettingNum.getBlue();
			String red = bettingNum.getRed();

			// Spanned html = Html.fromHtml("<font color=\"#ff0000\">" + red
			// + "</font><font color=\"#000000\">" + "|"
			// + "</font><font color=\"#0000ff\">" + blue + "</font>");

			List<String> betDetail = getBetDetail(basketArrayList.get(postion));
			String redstring = "";
			String bluestring = "";
			if (betDetail != null) {
				String string = betDetail.get(0);
				if (string.length() >= 20) {
					Log.i("reslut", string.length() + "");
					redstring = string.substring(0, 120);
					bluestring = string.substring(120);
				}

			}

			Spanned html = Html.fromHtml("<font color=\"#ff0000\">" + redstring
					+ "</font><font color=\"#0000ff\">" + bluestring
					+ "</font>");
			final Bettingdata data = basketArrayList.get(postion);
			cache.tv_betting_item.setText(html);
			cache.tv_bet_num.setText(data.getCount() + "注");
			cache.tv_bet_money.setText(data.getMoney());

			v.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,
							DoubleballBettingDetailsActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("data", (Serializable) data);
					intent.putExtras(bundle);
					startActivity(intent);

				}
			});
			v.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					
					
					new DoubleballDialog("确定删除此投注号码？",
							DoubleballBettingActivity.this, "确定", "取消",
							new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									basketArrayList.remove(data);
									list.remove(bettingNum);
									stringList.remove(postion);
									notifyDataSetChanged();

									Util.show("删除成功", context);
									count = count - data.getCount();
									setMoney();

								}
							}, null).show();
					

					return false;
				}
			});

			cache.iv_doubleball_fork
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							basketArrayList.remove(data);
							list.remove(bettingNum);
							stringList.remove(postion);
							notifyDataSetChanged();

							Util.show("删除成功", context);
							count = count - data.getCount();
							setMoney();

						}
					});

			return v;
		}

		class ViewCache {

			TextView tv_betting_item;
			TextView tv_bet_num;
			TextView tv_bet_money;
			ImageView iv_doubleball_fork;
		}
	}

	private List<String> getBetDetail(Bettingdata data) {
		List<String> redNumList = new ArrayList<String>();
		List<String> blueNumList = new ArrayList<String>();
		if (data.isStraight()) {

			List<String> blueBallStraightList = data.getBlueBallStraightList();
			List<String> redBallStraightList = data.getRedBallStraightList();
			int size = redBallStraightList.size();
			if (size < 6 || blueBallStraightList.size() < 1) {
				return null;
			} else if (size == 6) {
				String string = Combination.arrayToString(
						Combination.listToString(redBallStraightList),
						"&#160;&#160;&#160;");
				redNumList.add(string);
			} else {
				redNumList = Combination.combination(
						Combination.listToString(redBallStraightList), 6,
						"&#160;&#160;&#160;");
			}

			blueNumList = blueBallStraightList;

		} else {
			List<String> blueBallDragList = data.getBlueBallDragList();

			List<String> redBallCourageList = data.getRedBallCourageList();
			List<String> redBallDragList = data.getRedBallDragList();
			int redDragSize = redBallDragList.size();
			int redCourageSize = redBallCourageList.size();
			int num = 6 - redCourageSize;
			if ((redCourageSize + redDragSize) < 6
					|| blueBallDragList.size() < 1) {
				return null;
			} else if (num == redDragSize) {
				String string = Combination.arrayToString(Combination
						.combineStringArray(
								Combination.listToString(redBallCourageList),
								Combination.listToString(redBallDragList)),
						"&#160;&#160;&#160;");
				redNumList.add(string);
			} else {
				List<String[]> tempList = Combination.combinationStringArray(
						Combination.listToString(redBallDragList), num);
				String[] str1 = Combination.listToString(redBallCourageList);

				for (int i = 0; i < tempList.size(); i++) {
					String[] array = Combination.combineStringArray(str1,
							tempList.get(i));
					String toString = Combination.arrayToString(array,
							"&#160;&#160;&#160;");
					redNumList.add(toString);
				}

			}

			blueNumList = blueBallDragList;

		}

		List<String> listData = listAddData(redNumList, blueNumList);
		return listData;
	}

	/**
	 * 将两个list合并成一个list
	 * 
	 * @param list1
	 * @param list2
	 */
	private List<String> listAddData(List<String> list1, List<String> list2) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				list.add(list1.get(i) + "&#160;&#160;&#160;" + list2.get(j));

			}
		}
		return list;
	}

}
