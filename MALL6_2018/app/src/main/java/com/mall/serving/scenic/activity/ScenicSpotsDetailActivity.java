package com.mall.serving.scenic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.Util;
import com.mall.view.R;

@ContentView(R.layout.scenic_spots_detail_activity)
public class ScenicSpotsDetailActivity extends BaseActivity {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		setView();

	}

	private void setView() {
		top_center.setText("景点列表");
		top_left.setVisibility(View.VISIBLE);
		top_right.setVisibility(View.VISIBLE);
		top_right.setText("地图");

	

	
	}

	@OnClick({ R.id.top_right })
	public void click(View v) {

		switch (v.getId()) {
		case R.id.top_right:

			Util.showIntent(context, ScenicSpotsWriteActivity.class);
			break;

	
		default:
			break;
		}

	}

}
