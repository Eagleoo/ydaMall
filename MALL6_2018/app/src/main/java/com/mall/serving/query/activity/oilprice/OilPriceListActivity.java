package com.mall.serving.query.activity.oilprice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.adapter.OilPriceListAdapter;
import com.mall.serving.query.model.OilPriceInfo.Data;
import com.mall.serving.query.model.OilPriceInfo.Data.Gastprice;
import com.mall.serving.query.view.dialog.OilPriceDropPopupWindow;
import com.mall.view.R;

@ContentView(R.layout.query_oil_price_list_activity)
public class OilPriceListActivity extends BaseActivity {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.tv_1)
	private TextView tv_1;
	@ViewInject(R.id.tv_2)
	private TextView tv_2;
	@ViewInject(R.id.listview)
	private ListView listview;

	private OilPriceListAdapter adapter;
	private List<Data> list;
	private List<Data> listSel;

	public static String TAG = "com.mall.serving.query.activity.oilprice.OilPriceListActivity";
	private String[] oilTypes = new String[] { "0#车柴", "92#", "93#", "95#", "97#" };

	private String[] computes = new String[] { "智能排序", "按距离", "按价格" };

	private int oilType = 0;
	private int compute = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		list = new ArrayList<Data>();
		listSel = new ArrayList<Data>();
		adapter = new OilPriceListAdapter(context, listSel);
		listview.setAdapter(adapter);
		AnimeUtil.setAnimationEmptyView(context, listview, true);
		setView();
		getIntentData();
		registerReceiverAtBase(new String[] { TAG });
	}

	private void setView() {
		top_center.setText("加油站列表");
		top_left.setVisibility(View.VISIBLE);
		top_right.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.lm_fujinde, 0);
		top_right.setVisibility(View.VISIBLE);

	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent.hasExtra("info")) {
			List<Data> listData = (List<Data>) intent
					.getSerializableExtra("info");
			list.addAll(listData);
		}
		if (list.size() > 0) {
			oilType=2;
			getSelList(oilTypes[oilType]);
			tv_1.setText(oilTypes[oilType]);
			tv_2.setText(computes[compute]);
			getComputeList(compute);
			adapter.notifyDataSetChanged();
			AnimeUtil.setNoDataEmptyView("暂未查询到附近满足条件的结果",
					R.drawable.community_dynamic_empty, context, listview,
					true, null);
		}

	}

	@OnClick({ R.id.tv_1, R.id.tv_2, R.id.top_right })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.tv_1:

			new OilPriceDropPopupWindow(context, tv_1, oilTypes, 0);
			break;
		case R.id.tv_2:
			new OilPriceDropPopupWindow(context, tv_2, computes, 1);
			break;
		case R.id.top_right:
			if (listSel.size() <= 0) {
				Util.show("列表为空...");
				return;
			}

			Util.showIntent(context, OilPriceMainActivity.class, new String[] {
					"list", "from" }, new Serializable[] {
					(Serializable) listSel, 1 });
			break;

		}

	}

	private void getSelList(String tag) {
		listSel.clear();
		for (Data data : list) {
			List<Gastprice> list2 = data.getGastprice();
			if (list2 != null && list2.size() > 0) {
				for (Gastprice gp : list2) {
					if (gp.getName().equals(tag)) {
						data.setPriceTemp(gp.getPrice());
						listSel.add(data);
						break;
					}

				}
			}

		}

	}

	private void getComputeList(int m) {

		switch (m) {
		case 0:
			Collections.sort(listSel, new Comparator<Data>() {

				@Override
				public int compare(Data info1, Data info2) {
					String distance1 = info1.getDistance();
					String distance2 = info2.getDistance();
					Log.e("距离1","KK"+distance1);
					if (TextUtils.isEmpty(distance1)) {
						distance1 = "99999999";
					}
					if (TextUtils.isEmpty(distance2)) {
						distance2 = "99999999";
					}

					double dis1 = Util.getDouble(distance1);

					double dis2 = Util.getDouble(distance2);

					return (int) (dis1 - dis2);
				}
			});
			break;
		case 1:
			Collections.sort(listSel, new Comparator<Data>() {

				@Override
				public int compare(Data info1, Data info2) {
					String distance1 = info1.getDistance();
					String distance2 = info2.getDistance();
					Log.e("距离2","KK"+distance1);
					if (TextUtils.isEmpty(distance1)) {
						distance1 = "99999999";
					}
					if (TextUtils.isEmpty(distance2)) {
						distance2 = "99999999";
					}
					double dis1 = Util.getDouble(distance1);

					double dis2 = Util.getDouble(distance2);

					return (int) (dis1 - dis2);
				}
			});
			break;
		case 2:
			Collections.sort(listSel, new Comparator<Data>() {

				@Override
				public int compare(Data info1, Data info2) {
					String distance1 = info1.getPriceTemp();
					String distance2 = info2.getPriceTemp();

					double dis1 = Util.getDouble(distance1);

					double dis2 = Util.getDouble(distance2);

					return (int) (dis1 - dis2);
				}
			});

			break;

		}

		adapter.notifyDataSetChanged();

	}

	@Override
	public void onReceiveBroadcast(Intent intent) {
		// TODO Auto-generated method stub
		super.onReceiveBroadcast(intent);
		
		Log.e("广播接收","接收到广播"+intent.getAction());
		
		if (intent.getAction().equals(TAG)) {

			int type = 0;
			int position = 0;
			if (intent.hasExtra("type")) {
				type = intent.getIntExtra("type", 0);
			}
			if (intent.hasExtra("position")) {
				position = intent.getIntExtra("position", 0);
			}
			Log.e("加油类型","type："+type+"position："+position);
			switch (type) {
			case 0:
				oilType = position;
				break;
			case 1:
				compute = position;
				break;

			}
			
			getSelList(oilTypes[oilType]);
			tv_1.setText(oilTypes[oilType]);
			tv_2.setText(computes[compute]);
			getComputeList(compute);

		}
	}

}
