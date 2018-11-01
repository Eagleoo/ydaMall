package com.mall.serving.query.adapter;

import java.util.List;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mall.serving.community.util.Util;
import com.mall.serving.query.model.TrainTicketInfo;
import com.mall.serving.voip.adapter.NewBaseAdapter;
import com.mall.view.R;

public class TrainTicketListAdapter extends NewBaseAdapter {

	private String[] types = { "高软：", "软卧：", "软座：", "特等座：", "商务座：", "一等座：",
			"二等座：", "硬卧：", "硬座：", "无座：", "其他：", };

	private String[] tt = new String[] {  "高铁", "动车", "特快", "直达", "快速",
	"其他" };
private String[] ttCodes = new String[] { "G", "D", "T", "Z", "K", "Q" };
	public TrainTicketListAdapter(Context c, List list) {
		super(c, list);

	}

	@Override
	public View getView(int position, View v, ViewGroup arg2) {

		if (v == null) {

			ViewCache cache = new ViewCache();
			v = inflater.inflate(R.layout.query_trainticket_list_item,
					null);

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

		TrainTicketInfo info = (TrainTicketInfo) list.get(position);
		
		String train_no = info.getTrain_no();
		cache.tv_type.setText(train_no);
		
		String type_name="";
		for (int i = 0; i < ttCodes.length; i++) {
			if (train_no.contains(ttCodes[i])) {
				type_name=tt[i];
				break;
			}
		}
		cache.tv_type_name.setText(type_name);
		cache.tv_start_time.setText(info.getStart_time());
		cache.tv_from_station.setText(info.getStart_station());
		cache.tv_arrive_time.setText(info.getEnd_time());
		cache.tv_to_station.setText(info.getEnd_station());
		cache.tv_lishi.setText("耗时"+info.getRun_time());
		return v;

	}

	private void getTypeString(List list, TrainTicketInfo info, String... str) {

		for (int i = 0; i < str.length; i++) {
			String num = str[i];
			if (!TextUtils.isEmpty(num)&&!num.equals("--")) {
				String text = "";
				if (Util.isInt(num)) {
					text = "张";
				}
				list.add(types[i] + num + text);
			}
		}

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

	class GridAdapter extends NewBaseAdapter {

		public GridAdapter(Context c, List list) {
			super(c, list);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {

			TextView tv = new TextView(context);
//			tv.setGravity(Gravity.CENTER);
			tv.setPadding(25, 5, 5, 5);
			tv.setTextColor(context.getResources().getColor(R.color.black_gray));
			tv.setTextSize(12);
			String str = (String) list.get(position);

			SpannableStringBuilder builder = new SpannableStringBuilder(str);

			ForegroundColorSpan redSpan = new ForegroundColorSpan(context
					.getResources().getColor(R.color.voip_blue_top));
			int indexOf = str.indexOf("：")+1;

			builder.setSpan(redSpan, indexOf, str.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			tv.setText(builder);

			return tv;
		}

	}

}
