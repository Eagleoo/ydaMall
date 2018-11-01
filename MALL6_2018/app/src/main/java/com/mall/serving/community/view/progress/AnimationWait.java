package com.mall.serving.community.view.progress;




import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mall.serving.community.util.Util;
import com.mall.view.R;

public class AnimationWait extends LinearLayout {

	private  TextView tv;
	private ImageView iv;
	private ProgressBar pb;
	
	
	

	public AnimationWait(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, false);
	}

	public AnimationWait(Context context,boolean is) {
		super(context);
		initView(context, is);
		

	}
	
	private void initView(Context context,boolean is) {
		setGravity(Gravity.CENTER);
		View view = LayoutInflater.from(context).inflate(R.layout.community_custom_progress_animation_wait,
				null);
		addView(view);

		tv = (TextView) view.findViewById(R.id.tv_animation_text);
		iv =  (ImageView) view.findViewById(R.id.iv_animation);
		pb =  (ProgressBar) view.findViewById(R.id.pb_animation);
        if (is) {
			if (!Util.isNetworkConnected()) {
				pb.setVisibility(View.GONE);
				iv.setVisibility(View.VISIBLE);
				tv.setText("请检查网络连接...");
			}else {
				pb.setVisibility(View.VISIBLE);
				iv.setVisibility(View.GONE);
				tv.setText("正在努力加载中...");
				
				
			}
		}

	}

	public AnimationWait setText(CharSequence str) {
		tv.setText(str);
		return this;
	}

	

}
