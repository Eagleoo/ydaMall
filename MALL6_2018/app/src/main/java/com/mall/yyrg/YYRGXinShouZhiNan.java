package com.mall.yyrg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.view.R;

public class YYRGXinShouZhiNan extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_xinshou);
		ViewUtils.inject(this);
	}
	@OnClick({R.id.top_back,R.id.goumai})
	public void onclcik(View view){
		finish();
	}
}
