package com.mall.order;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mall.model.HotelOrdersBean;
import com.mall.view.R;

public class HotelOrdersAdapter extends BaseAdapter {

	private List<HotelOrdersBean> list = new ArrayList<HotelOrdersBean>();
	private Context context;
	private LayoutInflater flater;
	private Dialog dialog;

	public HotelOrdersAdapter(Context context) {
		super();
		// this.list = list;
		this.context = context;
		flater = LayoutInflater.from(context);
	}

	public void setList(List<HotelOrdersBean> list) {
		this.list.addAll(list);
		this.notifyDataSetChanged();
	}

	public void clear() {
		this.list.clear();
	}

	public void updateUI() {
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = flater.inflate(R.layout.hotel_orders_item, null);
		}
		TextView order_number = (TextView) convertView
				.findViewById(R.id.order_number);
		TextView check_in_time = (TextView) convertView
				.findViewById(R.id.check_in_time_and_outtime);

		TextView hotelName = (TextView) convertView
				.findViewById(R.id.hotel_name);
		// TextView contacts_name=(TextView)
		// convertView.findViewById(R.id.contacts_name);
		TextView order_state = (TextView) convertView
				.findViewById(R.id.order_state);
		TextView money = (TextView) convertView.findViewById(R.id.money);
		money.setText("￥" + list.get(position).getMoney());
		order_number.setText("订单号:" + list.get(position).getOrderId());
		check_in_time.setText(list.get(position).getIntime()+" 至 "+ list.get(position).getOuttime());


		// if (TextUtils.isEmpty(list.get(position).getLinkman())) {
		// contacts_name.setText("无联系人");
		// }else {
		// contacts_name.setText(list.get(position).getLinkman());
		// }

		hotelName.setText(list.get(position).getHotelName());

		order_state.setText(list.get(position).getOrderState());
		if (list.get(position).getOrderState().equals("预订成功")) {
			order_state.setTextColor(context.getResources().getColor(
					R.color.headertop));
		} else {
			order_state.setTextColor(context.getResources().getColor(
					R.color.green_query));
		}
		return convertView;
	}
}
