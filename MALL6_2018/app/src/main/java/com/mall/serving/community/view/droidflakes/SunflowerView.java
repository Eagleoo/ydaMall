package com.mall.serving.community.view.droidflakes;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.view.R;

public class SunflowerView extends LinearLayout {
	private TextView tv_flower_text;

	public SunflowerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View view = LayoutInflater.from(context).inflate(
				R.layout.community_custom_sunflowerview, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);

		tv_flower_text = (TextView) view.findViewById(R.id.tv_flower_text);

		addView(view);
	}

	public void setText(String text) {
		tv_flower_text.setText(text);

	}

}
