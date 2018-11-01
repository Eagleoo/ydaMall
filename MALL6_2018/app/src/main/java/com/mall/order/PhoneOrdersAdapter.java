package com.mall.order;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.model.PhoneOrdersBean;
import com.mall.view.R;

public class PhoneOrdersAdapter extends BaseAdapter {
	
	
	private List<PhoneOrdersBean> list=new ArrayList<PhoneOrdersBean>();
	private Context context;
	private LayoutInflater flater;
	private Dialog dialog;
		

	public PhoneOrdersAdapter(Context context) {
		super();
		this.list = list;
		this.context = context;
		flater = LayoutInflater.from(context);
	}
	 public void setList(List<PhoneOrdersBean> list){
	    	this.list.addAll(list);
			this.notifyDataSetChanged();
	    }
	public void clear(){
		this.list.clear();
	}
	
	public void updateUI(){
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
		if(convertView == null){
			convertView = flater.inflate(R.layout.phone_orders_item, null);
		}
		LinearLayout item=(LinearLayout) convertView.findViewById(R.id.staff_manager_user_reward_item);
		TextView order_number=(TextView) convertView.findViewById(R.id.order_number);
		TextView order_amount=(TextView) convertView.findViewById(R.id.order_amount);
		TextView order_state=(TextView) convertView.findViewById(R.id.order_state);
		TextView order_date=(TextView) convertView.findViewById(R.id.order_date);
		order_number.setText(list.get(position).getOrderId());
		order_amount.setText(list.get(position).getMianzhi());
		order_state.setText(list.get(position).getOrderState());
		order_date.setText(list.get(position).getOrderTime());
		if (position%2==0) {
			item.setBackgroundColor(context.getResources().getColor(R.color.blue_light_more));
		}else {
			item.setBackgroundColor(context.getResources().getColor(R.color.bg));
		}
		return convertView;
	}
}
