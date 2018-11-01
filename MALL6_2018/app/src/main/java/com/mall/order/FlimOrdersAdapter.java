package com.mall.order;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mall.model.FlimOrdersBean;
import com.mall.view.R;

public class FlimOrdersAdapter extends BaseAdapter {
	
	
	private List<FlimOrdersBean> list=new ArrayList<FlimOrdersBean>();
	private Context context;
	private LayoutInflater flater;
	private Dialog dialog;
		

	public FlimOrdersAdapter(Context context) {
		super();
		this.context = context;
		flater = LayoutInflater.from(context);
	}
	 public void setList(List<FlimOrdersBean> list){
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
			convertView = flater.inflate(R.layout.flim_orders_item, null);
		}
		TextView flim_name=(TextView) convertView.findViewById(R.id.flim_name);
		TextView flim_price=(TextView) convertView.findViewById(R.id.flim_price);
		TextView order_state=(TextView) convertView.findViewById(R.id.order_state);
		TextView order_time=(TextView) convertView.findViewById(R.id.order_time);
		flim_name.setText(list.get(position).getFilmName());
		flim_price.setText("￥"+list.get(position).getUnitPrice());
		order_state.setText(list.get(position).getPayState());
		order_time.setText(list.get(position).getOrdertime());
		if (list.get(position).getPayState().equals("已支付")) {
			order_state.setBackgroundColor(context.getResources().getColor(R.color.headertop));
		}else {
			order_state.setBackgroundColor(context.getResources().getColor(R.color.qianhui));
		}
		return convertView;
	}
}
