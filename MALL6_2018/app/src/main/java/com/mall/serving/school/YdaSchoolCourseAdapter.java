package com.mall.serving.school;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.view.R;

import java.util.List;

public class YdaSchoolCourseAdapter extends NewBaseAdapter{

	public YdaSchoolCourseAdapter(Context c, List list) {
		super(c, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int arg0, View v, ViewGroup arg2) {
		if (v==null) {
			v=new TextView(context);
		}
		TextView tv=(TextView) v;
		tv.setText("《远大课堂》20150227商战之天龙八部");
		tv.setPadding(40, 20, 40, 20);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow, 0);
		tv.setBackgroundResource(R.drawable.community_white_lightblue_selector);
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return v;
	}

	
	
}
