package com.mall.order;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mall.model.LotteryOrdersBean;
import com.mall.view.R;

public class LotteryOrdersAdapter extends BaseAdapter {
	
	
	private List<LotteryOrdersBean> list=new ArrayList<LotteryOrdersBean>();
	private Context context;
	private LayoutInflater flater;
	public LotteryOrdersAdapter( Context context) {
		super();
		this.context = context;
		flater = LayoutInflater.from(context);
	}
	
	public void clear(){
		this.list.clear();
	}
	
	public void updateUI(){
		this.notifyDataSetChanged();
	}
	public void setList(List<LotteryOrdersBean> list){
    	this.list.addAll(list);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = flater.inflate(R.layout.lottery_orders_item, null);
		}
		RelativeLayout item=(RelativeLayout) convertView.findViewById(R.id.staff_manager_user_reward_item);
		TextView lottery_type=(TextView) convertView.findViewById(R.id.lottery_type);
		TextView lottey_number=(TextView) convertView.findViewById(R.id.lottey_number);
		TextView lottey_amount=(TextView) convertView.findViewById(R.id.lottey_amount);
		TextView lottery_time=(TextView) convertView.findViewById(R.id.lottery_time);
		if (list.get(position).getType().equalsIgnoreCase("SSQ")) {
			lottery_type.setText("双色球");
			
		}
		lottey_number.setText(list.get(position).getOrderId());
		lottey_amount.setText("购买金额：￥"+list.get(position).getMoney());
		lottery_time.setText("下注时间："+list.get(position).getDate());
		if (position%2==0) {
			item.setBackgroundColor(context.getResources().getColor(R.color.gray));
		}else {
			item.setBackgroundColor(context.getResources().getColor(R.color.bg));
		}
		return convertView;
	}
}
