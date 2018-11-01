package com.mall.serving.query.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.activity.weather.WeatherTodayQueryActivity;
import com.mall.serving.query.model.WeatherExpInfo;
import com.mall.serving.query.model.WeatherInfo;
import com.mall.view.R;

public class WeatherGrid2Adapter extends NewBaseAdapter {

	private WeatherInfo info;

	public WeatherGrid2Adapter(Context c, List list, WeatherInfo info) {
		super(c, list);
		this.info = info;
	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {

		if (v == null) {
			ViewCache cache = new ViewCache();
			v = LayoutInflater.from(context).inflate(
					R.layout.query_weather_grid_item2, null);
			cache.tv1 = (TextView) v.findViewById(R.id.tv_item1);
			cache.tv2 = (TextView) v.findViewById(R.id.tv_item2);
			cache.iv1 = (ImageView) v.findViewById(R.id.iv_item1);
			cache.iv2 = (ImageView) v.findViewById(R.id.iv_item2);

			v.setTag(cache);

		}
		ViewCache cache = (ViewCache) v.getTag();
		final WeatherExpInfo winfo = (WeatherExpInfo) list.get(position);
		cache.iv1.setImageResource(winfo.getRid());
		cache.tv1.setText(winfo.getTitle());
		List<String> list2 = winfo.getList();
		if (list2 != null && list2.size() >= 2) {
			cache.tv2.setText(list2.get(0));
		}

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

								Util.showIntent(context,
										WeatherTodayQueryActivity.class,
										new String[] { "info", "exp" },
										new Serializable[] { info, winfo });

							}
						});

			}
		});
		return v;
	}

	class ViewCache {
		TextView tv1;
		TextView tv2;
		ImageView iv1;
		ImageView iv2;

	}

}
