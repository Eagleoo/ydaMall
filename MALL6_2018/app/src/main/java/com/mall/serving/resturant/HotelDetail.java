package com.mall.serving.resturant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.util.Util;
import com.mall.view.R;

public class HotelDetail extends Activity {
	@ViewInject(R.id.hotel_description)
	private TextView hotel_desc;
	private String intro="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_detail);
		ViewUtils.inject(this);
		intro=this.getIntent().getStringExtra("intro");
		hotel_desc.setText("\t\t"+intro);
		Util.initTop(this, "酒店详情", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				HotelDetail.this.finish();
			}
		});
	}

}
