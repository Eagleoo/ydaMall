package com.mall.serving.query.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mall.serving.query.model.FlightInfo;
import com.mall.serving.voip.adapter.NewBaseAdapter;
import com.mall.view.R;

public class FlightListAdapter extends NewBaseAdapter {

	public FlightListAdapter(Context c, List list) {
		super(c, list);
	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {

		if (v == null) {

			ViewCache cache = new ViewCache();
			v = inflater.inflate(R.layout.query_trainticket_list_item, null);

			cache.tv_type_name = (TextView) v.findViewById(R.id.tv_type_name);
			cache.tv_type = (TextView) v.findViewById(R.id.tv_type);
			cache.tv_start_time = (TextView) v.findViewById(R.id.tv_start_time);
			cache.tv_from_station = (TextView) v
					.findViewById(R.id.tv_from_station);
			cache.tv_arrive_time = (TextView) v
					.findViewById(R.id.tv_arrive_time);
			cache.tv_to_station = (TextView) v.findViewById(R.id.tv_to_station);
			cache.tv_lishi = (TextView) v.findViewById(R.id.tv_lishi);
			v.setTag(cache);
		}
		ViewCache cache = (ViewCache) v.getTag();

		final FlightInfo info = (FlightInfo) list.get(position);
		cache.tv_type.setText(info.getFlightNum());
		cache.tv_type_name.setText(info.getAirline());
		cache.tv_start_time.setText(info.getDepTime());
		cache.tv_from_station.setText(info.getDepCity());
		cache.tv_arrive_time.setText(info.getArrTime());
		cache.tv_to_station.setText(info.getArrCity());
		return v;

	}

	class ViewCache {
		TextView tv_type_name;
		TextView tv_type;
		TextView tv_start_time;
		TextView tv_from_station;
		TextView tv_arrive_time;
		TextView tv_to_station;
		TextView tv_lishi;

	}

}
