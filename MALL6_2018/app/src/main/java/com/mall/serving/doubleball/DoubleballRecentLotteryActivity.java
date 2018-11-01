package com.mall.serving.doubleball;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

@ContentView(R.layout.doubleball_recent_lottery)
public class DoubleballRecentLotteryActivity extends Activity {
	@ViewInject(R.id.topCenter)
	private TextView topCenter;

	@ViewInject(R.id.lv_recent)
	private ListView lv_recent;

	private List<LotteryInfo> list;
	private RecentAdapter adapter;
	private int lotteryDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		list = new ArrayList<LotteryInfo>();

		setView();

		if (Web.issueNumber != 0) {
			lotteryDate = Web.issueNumber - 1;
			getLotteryData();
		}else {
			Util.show("未获取到当前彩期，请刷新彩期！", this);
		}

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

	private void setView() {
		topCenter.setText("近期开奖");

		adapter = new RecentAdapter(this, list);

		lv_recent.setAdapter(adapter);

	}

	class RecentAdapter extends BaseAdapter {
		private Context context;
		private List<LotteryInfo> list;

		public RecentAdapter(Context context, List<LotteryInfo> list) {
			super();
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			if (list == null || list.size() == 0) {
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
				v = LayoutInflater.from(context).inflate(
						R.layout.doubleball_recent_list_item, null);
				cache.tv_recent_term = (TextView) v
						.findViewById(R.id.tv_recent_term);
				cache.tv_recent_pond = (TextView) v
						.findViewById(R.id.tv_recent_pond);
				cache.cb_ball_number_01 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_01);
				cache.cb_ball_number_02 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_02);
				cache.cb_ball_number_03 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_03);
				cache.cb_ball_number_04 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_04);
				cache.cb_ball_number_05 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_05);
				cache.cb_ball_number_06 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_06);
				cache.cb_ball_number_07 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_07);
				cache.cb_ball_number_08 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_08);
				cache.rl_number_08 =  v
						.findViewById(R.id.rl_number_08);

				cache.ll_item = v.findViewById(R.id.ll_item);
				cache.tv_item_more = (TextView) v
						.findViewById(R.id.tv_item_more);
				v.setTag(cache);
			}
			ViewCache cache = (ViewCache) v.getTag();

			if (position == list.size()) {
				cache.tv_item_more.setVisibility(View.VISIBLE);
				cache.ll_item.setVisibility(View.GONE);

				cache.tv_item_more.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						getLotteryData();

					}
				});

				if (lotteryDate == 1) {
					cache.tv_item_more.setVisibility(View.GONE);
				}

			} else {
				cache.tv_item_more.setVisibility(View.GONE);
				cache.ll_item.setVisibility(View.VISIBLE);
				if (position == 0) {
					cache.cb_ball_number_01.setChecked(true);
					cache.cb_ball_number_02.setChecked(true);
					cache.cb_ball_number_03.setChecked(true);
					cache.cb_ball_number_04.setChecked(true);
					cache.cb_ball_number_05.setChecked(true);
					cache.cb_ball_number_06.setChecked(true);
					cache.cb_ball_number_07.setChecked(true);
					cache.cb_ball_number_08.setChecked(true);

				} else {
					cache.cb_ball_number_01.setChecked(false);
					cache.cb_ball_number_02.setChecked(false);
					cache.cb_ball_number_03.setChecked(false);
					cache.cb_ball_number_04.setChecked(false);
					cache.cb_ball_number_05.setChecked(false);
					cache.cb_ball_number_06.setChecked(false);
					cache.cb_ball_number_07.setChecked(false);
					cache.cb_ball_number_08.setChecked(false);

				}
				LotteryInfo info = list.get(position);
				cache.tv_recent_term.setText(info.getIssueNumber() + "期");
				String bonusCode = info.getBonusCode();
				String[] split = bonusCode.split(" \\| ");
				String red="";
				String blue="";
				if (split.length>=2) {
					red=split[0];
					blue=split[1];
				}
				

				
				String[] reds = red.split(",");
				String[] blues = blue.split(",");
			
				if (reds.length >= 6) {
					cache.tv_recent_pond.setText("");
					cache.cb_ball_number_01.setText(reds[0]);
					cache.cb_ball_number_02.setText(reds[1]);
					cache.cb_ball_number_03.setText(reds[2]);
					cache.cb_ball_number_04.setText(reds[3]);
					cache.cb_ball_number_05.setText(reds[4]);
					cache.cb_ball_number_06.setText(reds[5]);
					
				}
				if (blues.length==1) {
					cache.cb_ball_number_07.setText(blues[0]);
					
				}
				cache.rl_number_08.setVisibility(View.GONE);
				if (blues.length==2) {
					cache.cb_ball_number_07.setText(blues[0]);
					cache.rl_number_08.setVisibility(View.VISIBLE);
					cache.cb_ball_number_08.setText(blues[1]);
				}

			}

			return v;
		}

		class ViewCache {

			TextView tv_recent_term;
			TextView tv_recent_pond;
			CheckBox cb_ball_number_01;
			CheckBox cb_ball_number_02;
			CheckBox cb_ball_number_03;
			CheckBox cb_ball_number_04;
			CheckBox cb_ball_number_05;
			CheckBox cb_ball_number_06;
			CheckBox cb_ball_number_07;
			CheckBox cb_ball_number_08;
			View rl_number_08;

			View ll_item;
			TextView tv_item_more;

		}

	}

	private void getLotteryData() {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}

		Util.asynTask(this, "正在获取近期彩票信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {

				HashMap<String, List<LotteryInfo>> map = (HashMap<String, List<LotteryInfo>>) runData;
				List<LotteryInfo> arrayList = map.get("list");
				if (null == arrayList || 0 == arrayList.size())
					Util.show("没有获取到近期彩票信息！",
							DoubleballRecentLotteryActivity.this);
				else {
					list.addAll(arrayList);
					if (null == adapter) {
						adapter = new RecentAdapter(
								DoubleballRecentLotteryActivity.this, list);
						lv_recent.setAdapter(adapter);
					} else {

						adapter.notifyDataSetChanged();
					}
				}

			}

			@Override
			public Serializable run() {
				List<LotteryInfo> arrayList = new ArrayList<LotteryInfo>();
				if (lotteryDate <= 0) {
					return null;
				}

				for (int i = 0; i < 15; i++) {
					Web web = new Web(Web.convience_service,
							Web.getLotteryInfo, "type=SSQ&issueNumber="
									+ lotteryDate);
					List<LotteryInfo> infolist = web.getList(LotteryInfo.class,
							"issueinfo");
					arrayList.addAll(infolist);
					lotteryDate--;
				}

				HashMap<String, List<LotteryInfo>> map = new HashMap<String, List<LotteryInfo>>();
				map.put("list", arrayList);
				return map;
			}
		});

	}

}
