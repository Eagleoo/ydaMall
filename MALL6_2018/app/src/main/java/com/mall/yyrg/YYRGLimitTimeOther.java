package com.mall.yyrg;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.view.R;
import com.mall.yyrg.YYRGLimitTimeUtil.MyAdapter;
import com.mall.yyrg.adapter.TimeListRecord;

/**
 * 回顾和抢购进行中
 * 
 * @author Administrator
 * 
 */
public class YYRGLimitTimeOther extends Activity {
	@ViewInject(R.id.goods_list1)
	private GridView goods_list1;
	@ViewInject(R.id.lin3)
	private LinearLayout lin3;
	@ViewInject(R.id.top_back1)
	private TextView top_back1;
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_passed_events);
		ViewUtils.inject(this);
		
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		List<TimeListRecord> list = (List<TimeListRecord>) intent.getSerializableExtra("list");
		
		
		
		
		
		
		tv_title.setText(title);
		MyAdapter adapter = new MyAdapter(this, list);
		goods_list1.setAdapter(adapter);
		top_back1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		lin3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	
}
