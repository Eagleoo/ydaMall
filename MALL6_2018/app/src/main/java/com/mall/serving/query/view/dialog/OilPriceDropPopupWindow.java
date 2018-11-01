package com.mall.serving.query.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mall.serving.community.util.Util;
import com.mall.serving.query.activity.oilprice.OilPriceListActivity;
import com.mall.view.R;

public class OilPriceDropPopupWindow extends PopupWindow {

	public OilPriceDropPopupWindow(final Context context, final TextView tv,
			String[] strs,final int type) {
		super(context);

		final LinearLayout view = new LinearLayout(context);

		view.setOrientation(LinearLayout.VERTICAL);


		int dp = Util.dpToPx(10);
		for (int i = 0; i < strs.length; i++) {

			final String text = strs[i];

			TextView textView = new TextView(context);

			textView.setText(text);
			textView.setBackgroundResource(R.drawable.community_white_lightblue_selector);
			textView.setPadding(dp, dp, dp, dp);
			textView.setGravity(Gravity.CENTER);
			textView.setTextSize(15);
			

			final int position=i;
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					tv.setText(text);
					Intent intent = new Intent(OilPriceListActivity.TAG);
					intent.putExtra("type", type);
					intent.putExtra("position", position);
					Log.e("加油类型","type："+type+"position："+position);
					context.sendBroadcast(intent);
					dismiss();

				}
			});

			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			if (i == 0) {
				params.setMargins(1, 1, 1, 1);
			} else {
				params.setMargins(1, 0, 1, 1);
			}

			textView.setLayoutParams(params);
			view.addView(textView);
		}

		setFocusable(true);
		setContentView(view);

		setOutsideTouchable(true);

		view.setBackgroundResource(R.color.black_gray);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setBackgroundDrawable(new BitmapDrawable());
		showAsDropDown(tv, 0, 0);

	}

}
