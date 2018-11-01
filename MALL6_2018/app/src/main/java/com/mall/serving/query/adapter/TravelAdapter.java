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
import com.mall.serving.community.util.Util;
import com.mall.serving.query.activity.travel.TravelDetailActivity;
import com.mall.serving.query.model.TravelInfo.Hotel;
import com.mall.serving.query.model.TravelInfo.Scenery;
import com.mall.view.R;

public class TravelAdapter extends NewBaseAdapter {

	private boolean isHotel;

	public TravelAdapter(Context c, List list, boolean isHotel) {
		super(c, list);
		this.isHotel = isHotel;
	}

	@Override
	public View getView(int p, View v, ViewGroup arg2) {

		if (v == null) {
			ViewCache cache = new ViewCache();
			v = LayoutInflater.from(context).inflate(
					R.layout.query_travel_list_item, null);
			cache.iv_1 = (ImageView) v.findViewById(R.id.iv_1);
			cache.tv_1 = (TextView) v.findViewById(R.id.tv_1);
			cache.tv_2 = (TextView) v.findViewById(R.id.tv_2);
			cache.tv_3 = (TextView) v.findViewById(R.id.tv_3);

			v.setTag(cache);
		}
		ViewCache cache = (ViewCache) v.getTag();

		String title = "";
		String price = "";
		String imgurl = "";
		String unit = "";
		final Serializable info = (Serializable) list.get(p);

		if (isHotel) {

			Hotel sinfo = (Hotel) info;
			title = sinfo.getTitle();

			price = sinfo.getPrice_min();

			imgurl = sinfo.getImgurl();
			unit="起/间";

		} else {

			Scenery sinfo = (Scenery) info;
			title = sinfo.getTitle();

			price = sinfo.getPrice_min();

			imgurl = sinfo.getImgurl();
			unit="起/人";
		}
		cache.tv_1.setText(title);
		cache.tv_2.setText("￥" + price);
		cache.tv_3.setText(unit);
		
		AnimeUtil.getImageLoad().displayImage(imgurl, cache.iv_1,
				AnimeUtil.getImageOption());

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Util.showIntent(context, TravelDetailActivity.class,
						new String[] { "info", "isHotel" }, new Serializable[] {
								info, isHotel });

			}
		});

		return v;
	}

	class ViewCache {

		ImageView iv_1;
		TextView tv_1;
		TextView tv_2;
		TextView tv_3;

	}

}
