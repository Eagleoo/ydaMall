package com.mall.serving.scenic.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.scenic.adapter.AdapterHelper;
import com.mall.serving.scenic.adapter.CanBaseAdapter;
import com.mall.view.R;

@ContentView(R.layout.scenic_spots_list_activity)
public class ScenicSpotsListActivity extends BaseActivity {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.listView)
	private ListView listview;

	private List list;

	private CanBaseAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		setView();

	}

	@SuppressWarnings("unchecked")
	private void setView() {
		top_center.setText("景点列表");
		top_left.setVisibility(View.VISIBLE);
		top_right.setVisibility(View.VISIBLE);
		top_right.setText("地图");
		list = new ArrayList();
		for (int i = 0; i < 9; i++) {
			list.add("");
		}

		adapter = new CanBaseAdapter(context, R.layout.scenic_spots_item2, list) {

			@Override
			protected void initViewItem(AdapterHelper helper, int position,View v) {

				System.out.println("ccccccccc"+position);
			}

		};

		listview.setAdapter(adapter);

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

}
