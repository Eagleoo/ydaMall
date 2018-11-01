package com.mall.serving.query.adapter;

import java.io.Serializable;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mall.serving.community.util.Util;
import com.mall.serving.query.activity.car.CarCityActivity;
import com.mall.serving.query.activity.car.CarQueryActivity;
import com.mall.serving.query.model.CarCityInfo;
import com.mall.serving.query.model.CarCityInfo.CityInfo;
import com.mall.serving.query.model.CarIdInfo;
import com.mall.serving.voip.adapter.NewBaseAdapter;
import com.mall.view.R;

public class CarCityAdapter extends NewBaseAdapter {

	private int type;

	public CarCityAdapter(Context c, final List list, int type) {
		super(c, list);

		this.type = type;

	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {
		if (v == null) {
			ViewCache cache = new ViewCache();
			v = inflater.inflate(R.layout.community_location_city_item, null);
			cache.tv_city = (TextView) v.findViewById(R.id.tv_city);
			cache.sort_key = (TextView) v.findViewById(R.id.alpha);

			v.setTag(cache);
		}
		ViewCache cache = (ViewCache) v.getTag();
		cache.sort_key.setVisibility(View.GONE);

		switch (type) {
		case 0:
			final CarCityInfo info = (CarCityInfo) list.get(position);
			String province = info.getProvince();
			cache.tv_city.setText(province);
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					Util.showIntent(context, CarCityActivity.class,
							new String[] { "city" },
							new Serializable[] { info });

				}
			});
			break;
		case 1:
			final CityInfo info1 = (CityInfo) list.get(position);
			String name = info1.getCity_name();
			cache.tv_city.setText(name);

			
			
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					
					Activity ac = (Activity) context;
					Intent intent = new Intent();

					intent.putExtra("city", info1);
					intent.setAction(CarQueryActivity.TAG);
		
					context.sendBroadcast(intent);
					ac.finish();
					context.sendBroadcast(new Intent(CarCityActivity.TAG));
				}
			});

			break;
		case 2:
			final CarIdInfo info2 = (CarIdInfo) list.get(position);

			cache.tv_city.setText(info2.getCar());

			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Activity ac1 = (Activity) context;
					Intent intent1 = new Intent();

					intent1.putExtra("type", info2);
					intent1.setAction(CarQueryActivity.TAG);
					context.sendBroadcast(intent1);
					ac1.finish();
					
				}
			});
			
			break;

		}

		return v;

	}

	class ViewCache {

		TextView tv_city;
		TextView sort_key;
	}

}
