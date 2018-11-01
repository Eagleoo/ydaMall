package com.mall.serving.query.activity.postcode;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.query.adapter.PostCodeCityAdapter;
import com.mall.view.R;

@ContentView(R.layout.community_find_listview2)
public class PostCodeCityQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;
	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	private List list;

	@ViewInject(R.id.listview)
	private ListView listview;

	private PostCodeCityAdapter adapter;

	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		list=new ArrayList();
		listview.setBackgroundResource(R.color.main_deep_bg);
		listview.setDividerHeight(1);
		getIntentData();
		setView();

		
	}

	private void setView() {
		
		switch (type) {
		case 0:
			top_center.setText("选择省份");
			break;
		case 1:
			top_center.setText("选择城市");
			break;
		case 2:
			top_center.setText("选择区县");
			break;

		}

		top_left.setVisibility(View.VISIBLE);

	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent.hasExtra("type")) {
			type = intent.getIntExtra("type", 0);

		}
		
		if (intent.hasExtra("list")) {
			list=(List) intent.getSerializableExtra("list");
			
		}
		adapter = new PostCodeCityAdapter(context, list, type);
		listview.setAdapter(adapter);
	}

	
	

}
