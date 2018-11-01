package com.mall.serving.query.adapter;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.dialog.CustomDialog;
import com.mall.serving.query.activity.car.CarMainActivity;
import com.mall.serving.query.activity.car.CarResultActivity;
import com.mall.serving.query.model.CarResultInfo;
import com.mall.serving.query.model.CarResultInfo.CarWZInfo;
import com.mall.serving.query.model.CarString;
import com.mall.serving.voip.adapter.NewBaseAdapter;
import com.mall.view.R;

public class CarMainAdapter extends NewBaseAdapter {

	public CarMainAdapter(Context c, final List list) {
		super(c, list);

	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {

		if (v == null) {

			ViewCache cache = new ViewCache();
			v = inflater.inflate(R.layout.query_car_main_item, null);

			cache.tv_name = (TextView) v.findViewById(R.id.tv_name);

			cache.tv_message = (TextView) v.findViewById(R.id.tv_message);
			cache.tv_money = (TextView) v.findViewById(R.id.tv_money);
			cache.tv_fen = (TextView) v.findViewById(R.id.tv_fen);

			v.setTag(cache);
		}
		ViewCache cache = (ViewCache) v.getTag();

		final CarResultInfo info = (CarResultInfo) list.get(position);

		cache.tv_name.setText(info.getHphm());
		List<CarWZInfo> lists = info.getLists();

		int times = 0;
		int times2 = 0;
		int fen = 0;
		int money = 0;
		for (CarWZInfo cInfo : lists) {

			if (cInfo.getHandled().equals("0")) {
				times++;
				
			}else {
				times2++;
			}
			fen += Util.getInt(cInfo.getFen());
			money += Util.getInt(cInfo.getMoney());
		}
		cache.tv_message.setText("未处理违章" + times + "次，已处理违章"+times2+"次");

		cache.tv_money.setText(money + " ");
		cache.tv_fen.setText(fen + " ");

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Util.showIntent(context, CarResultActivity.class,
						new String[] { "info" ,"from"}, new Serializable[] { info ,1});

			}
		});

		v.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {

				new CustomDialog("删除", "是否删除本条记录？", context, "取消", "确定", null,
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								try {
									DbUtils.create(context).delete(
											CarString.class,
											WhereBuilder.b("key", "=",
													info.getHphm()));
									context.sendBroadcast(new Intent(CarMainActivity.TAG));
									
								} catch (DbException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).show();
				return false;
			}
		});

		return v;

	}

	class ViewCache {

		TextView tv_name;

		TextView tv_message;
		TextView tv_money;
		TextView tv_fen;

	}

}
