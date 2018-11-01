package com.mall.serving.query.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.textview.TextDrawable;
import com.mall.serving.query.activity.constellation.ConstellationActivity;
import com.mall.view.R;

public class ConstellationPopupWindow extends PopupWindow {

	public static String[] cons = { "白羊", "金牛", "双子", "巨蟹", "狮子", "处女", "天秤",
			"天蝎", "射手", "摩羯", "水瓶", "双鱼" };
	public static String[] times = { "3/21-4/19", "4/20-5/20", "5/21-6/21",
			"6/22-7/22", "7/23-8/22", "8/23-9/22", "9/23-10/23", "10/24-11/22",
			"11/23-12/21", "12/22-1/19", "1/20-2/18", "2/19-3/20" };
	public static String[] colors = { "a8dd2d", "ffaa16", "af77c4", "f9643c",
			"04beb1", "7d91ff", "f7bd2d", "04c0b2", "ae91e3", "607df5",
			"ded842", "17bff6" };

	public static int[] rids = { R.drawable.query_star_aries,
			R.drawable.query_star_taurus, R.drawable.query_star_gemini,
			R.drawable.query_star_cancer, R.drawable.query_star_leo,
			R.drawable.query_star_virgo, R.drawable.query_star_libra,
			R.drawable.query_star_scorpio, R.drawable.query_star_sagittarius,
			R.drawable.query_star_capricornus, R.drawable.query_star_aquarius,
			R.drawable.query_star_pisces };

	public ConstellationPopupWindow(final Context context) {
		super(context);

		final LinearLayout view = new LinearLayout(context);
		final ScrollView scroll = new ScrollView(context);

		view.setOrientation(LinearLayout.VERTICAL);

		view.setGravity(Gravity.CENTER);

		scroll.addView(view);

		view.setGravity(Gravity.RIGHT);
		for (int i = 0; i < colors.length; i++) {
			TextDrawable td = TextDrawable.builder().buildRound("",
					Color.parseColor("#" + colors[i]));

			TextView tv1 = new TextView(context);
			tv1.setBackgroundDrawable(td);
			int dp80 = Util.dpToPx(80);
			int dp10 = Util.dpToPx(10);
			LayoutParams params = new LayoutParams(dp80, dp80);
			tv1.setLayoutParams(params);
			tv1.setPadding(dp10, dp10, dp10, dp10);
			tv1.setGravity(Gravity.CENTER);
			params.setMargins(dp10, dp10, dp10, dp10);
			tv1.setText(cons[i]+"座");
			tv1.setTextColor(context.getResources().getColor(R.color.white));
			tv1.setTextSize(15);
			tv1.setCompoundDrawablesWithIntrinsicBounds(0, rids[i], 0, 0);
			final int num=i;
			tv1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new OnAnimEnd() {
						
						@Override
						public void start() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void repeat() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void end() {
							Intent intent = new Intent(ConstellationActivity.TAG);
							intent.putExtra("num", num);
							context.sendBroadcast(intent);
							dismiss();
							
						}
					});
					
				}
			});
			view.addView(tv1);
			TextView tv2 = new TextView(context);
			tv2.setText(times[i]);
			LayoutParams params2 = new LayoutParams(dp80, LayoutParams.WRAP_CONTENT);
			params2.setMargins(dp10, 0, dp10,0);
			tv2.setGravity(Gravity.CENTER);
			tv2.setLayoutParams(params2);
			tv2.setTextSize(14);
			tv2.setTextColor(context.getResources().getColor(R.color.white));
			view.addView(tv2);

		}

	    view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dismiss();
				
				
			}
		});
		setFocusable(true);
		setContentView(scroll);

		setOutsideTouchable(true);

		scroll.setBackgroundResource(R.color.half_transparent);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		showAtLocation(view, Gravity.CENTER, 0, 0);

	}

}
