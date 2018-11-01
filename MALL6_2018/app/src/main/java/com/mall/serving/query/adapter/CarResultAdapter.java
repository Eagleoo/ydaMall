package com.mall.serving.query.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.serving.query.model.CarResultInfo.CarWZInfo;
import com.mall.serving.voip.adapter.NewBaseAdapter;
import com.mall.view.R;

public class CarResultAdapter extends NewBaseAdapter {

	public CarResultAdapter(Context c, final List list) {
		super(c, list);

	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {

		if (v == null) {

			ViewCache cache = new ViewCache();
			v = inflater.inflate(R.layout.query_car_result_item, null);

			cache.iv = (ImageView) v.findViewById(R.id.iv);

			cache.tv_name = (TextView) v.findViewById(R.id.tv_name);
			cache.tv_time = (TextView) v.findViewById(R.id.tv_time);
			cache.tv_message_1 = (TextView) v.findViewById(R.id.tv_message_1);
			cache.tv_message_2 = (TextView) v.findViewById(R.id.tv_message_2);

			v.setTag(cache);
		}
		ViewCache cache = (ViewCache) v.getTag();

		final CarWZInfo info = (CarWZInfo) list.get(position);

		cache.tv_name.setText(info.getArea());

		cache.tv_time.setText(info.getDate());

		String handled = info.getHandled();
		if (handled.equals("0")) {
			handled="未处理";
		}else {
			handled="已处理";
		}
		cache.tv_message_1.setText("扣分：" + info.getFen() + "  罚款："
				+ info.getMoney() + "  是否处理：" + handled);

		cache.tv_message_2.setText(info.getAct());
		
		

		return v;

	}

	class ViewCache {

		ImageView iv;
		TextView tv_name;
		TextView tv_time;
		TextView tv_message_1;
		TextView tv_message_2;
	}

}
