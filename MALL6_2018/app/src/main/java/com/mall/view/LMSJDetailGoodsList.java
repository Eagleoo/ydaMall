package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.LMSJDetailAdapter;
import com.mall.yyrg.adapter.YYRGUtil;

public class LMSJDetailGoodsList extends Activity{
	private ListView goods_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lmsj_detial_goods_list);
		ViewUtils.inject(this);
		goods_list=(ListView) findViewById(R.id.goods_list);
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width=dm.widthPixels;
		LMSJDetailAdapter lmsjDetailAdapter=new LMSJDetailAdapter(YYRGUtil.allProducts, this, width);
		goods_list.setAdapter(lmsjDetailAdapter);
	}
	@OnClick(R.id.topback)
	public void topback(View view){
		finish();
	}
}
