package com.mall.serving.redpocket.activity;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.Util;
import com.mall.serving.redpocket.util.ShareUtil;
import com.mall.view.R;

@ContentView(R.layout.redpocket_share_activity)
public class RedPocketShareActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.tv_redpocket)
	private TextView tv_redpocket;

	private int red;
	private int type;

	private String text;
	private String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
	}

	private void setView() {
		Intent intent = getIntent();
		if (intent.hasExtra("red")) {
			red = intent.getIntExtra("red", 0);
		}
		if (intent.hasExtra("type")) {
			type= intent.getIntExtra("type", 0);
		}
		if (intent.hasExtra("text")) {
			text = intent.getStringExtra("text");
		}
		if (intent.hasExtra("message")) {
			message = intent.getStringExtra("message");
		}
		if (intent.hasExtra("redpocket")) {
			String redpocket = intent.getStringExtra("redpocket");
			tv_redpocket.setText(redpocket);
		} else {
			tv_redpocket.setText("");
		}

		String title = "";
		if (red == 0) {
			title = "拼手气红包";
		} else {
			title = "普通红包";
		}
		top_center.setText(title);
		top_left.setVisibility(View.VISIBLE);

	}

	@OnClick({ R.id.tv_share, R.id.tv_record })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.tv_share:
			ShareUtil.showShareDialog(context, text,message);
			break;
		case R.id.tv_record:

			Util.showIntent(context, RedPocketIndexActivity.class,
					new String[] { "type" }, new Serializable[] { type});

			break;

		}
	}

	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
}
