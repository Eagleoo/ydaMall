package com.mall.serving.scenic.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.Util;
import com.mall.serving.scenic.adapter.ScenicSpotsGridAdapter;
import com.mall.view.R;

@ContentView(R.layout.scenic_spots_main_activity)
public class ScenicSpotsMainActivity extends BaseActivity {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.gv_1)
	private GridView gv_1;
	@ViewInject(R.id.gv_2)
	private GridView gv_2;
	@ViewInject(R.id.gv_3)
	private GridView gv_3;

	private List list1;
	private List list2;
	private List list3;

	private ScenicSpotsGridAdapter adapter1;
	private ScenicSpotsGridAdapter adapter2;
	private ScenicSpotsGridAdapter adapter3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		setView();

	}

	private void setView() {
		top_center.setText("景区门票");
		top_left.setVisibility(View.VISIBLE);
		top_right.setVisibility(View.VISIBLE);
		top_right.setText("订单");

		list1 = new ArrayList();
		list2 = new ArrayList();
		list3 = new ArrayList();

		for (int i = 0; i < 4; i++) {
			list1.add("");
		}
		for (int i = 0; i < 12; i++) {
			list2.add("");
		}
		for (int i = 0; i < 9; i++) {
			list3.add("");
		}

		adapter1 = new ScenicSpotsGridAdapter(context, list1, 0);
		adapter2 = new ScenicSpotsGridAdapter(context, list2, 1);
		adapter3 = new ScenicSpotsGridAdapter(context, list3, 2);
		gv_1.setAdapter(adapter1);
		gv_2.setAdapter(adapter2);
		gv_3.setAdapter(adapter3);

	}

	@OnClick({ R.id.top_right })
	public void click(View v) {

		switch (v.getId()) {
		case R.id.top_right:

			break;

		default:
			break;
		}

	}

	@OnItemClick({ R.id.gv_1 })
	public void onItemClick(AdapterView<?> av, View v, int p, long arg3) {
		Object itemAtPosition = av.getItemAtPosition(p);

		Util.showIntent(context, ScenicSpotsListActivity.class);

	}

}
