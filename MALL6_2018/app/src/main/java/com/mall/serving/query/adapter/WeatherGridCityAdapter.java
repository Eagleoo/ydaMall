package com.mall.serving.query.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.dialog.CustomDialog;
import com.mall.serving.query.activity.weather.WeatherCityQueryActivity;
import com.mall.serving.query.activity.weather.WeatherSelectCityQueryActivity;
import com.mall.serving.query.model.WeatherCityInfo;
import com.mall.view.App;
import com.mall.view.R;

public class WeatherGridCityAdapter extends NewBaseAdapter {

	public WeatherGridCityAdapter(Context c, List list) {
		super(c, list);

	}

	@Override
	public int getCount() {
		if (list == null) {
			return 1;
		}
		return list.size() + 1;
	}

	@Override
	public View getView(final int position, View v, ViewGroup arg2) {

		if (v == null) {
			ViewCache cache = new ViewCache();
			v = LayoutInflater.from(context).inflate(
					R.layout.query_weather_grid_item3, null);
			cache.tv1 = (TextView) v.findViewById(R.id.tv_item1);
			cache.tv2 = (TextView) v.findViewById(R.id.tv_item2);
			cache.tv3 = (TextView) v.findViewById(R.id.tv_item3);
			cache.iv1 = (ImageView) v.findViewById(R.id.iv_item1);

			v.setTag(cache);

		}
		ViewCache cache = (ViewCache) v.getTag();

		int p = position;
		if (position == getCount() - 1) {
			p = position - 1;
			cache.tv1.setVisibility(View.GONE);
			cache.tv2.setVisibility(View.GONE);
			cache.tv3.setVisibility(View.VISIBLE);
			cache.iv1.setVisibility(View.GONE);

		} else {
			p = position;
			cache.tv1.setVisibility(View.VISIBLE);
			cache.tv2.setVisibility(View.VISIBLE);
			cache.tv3.setVisibility(View.GONE);
			cache.iv1.setVisibility(View.VISIBLE);
		}
		final WeatherCityInfo winfo = (WeatherCityInfo) list.get(p);
		cache.iv1.setImageResource(winfo.getRid());
		cache.tv1.setText(winfo.getCity());
		cache.tv2.setText(winfo.getWeather());

		v.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				if (position != getCount() - 1&&list.size()>1) {
					new CustomDialog(winfo.getCity(), "是否删除？", context, "否",
							"是", null, new OnClickListener() {

								@Override
								public void onClick(View arg0) {

									try {
										DbUtils.create(App.getContext())
												.delete(winfo);

										context.sendBroadcast(new Intent(
												WeatherSelectCityQueryActivity.TAG));
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}).show();
				}

				return false;
			}
		});

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				AnimeUtil.startAnimation(context, view, R.anim.small_2_big,
						new OnAnimEnd() {

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

								if (position == getCount() - 1) {
									Util.showIntent(context,
											WeatherCityQueryActivity.class);

								} else {

									Intent intent = new Intent();
									intent.putExtra("num", position);
									Activity ac = (Activity) context;
									ac.setResult(100, intent);
									ac.finish();

								}

							}
						});

			}
		});
		return v;
	}

	class ViewCache {
		TextView tv1;
		TextView tv2;
		TextView tv3;
		ImageView iv1;

	}

}
