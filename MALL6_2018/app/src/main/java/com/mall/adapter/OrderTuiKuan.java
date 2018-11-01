package com.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.mall.model.OrderOne;
import com.mall.model.OrderTwo;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.view.R;
import com.mall.yyrg.adapter.MyListView;

import java.util.List;

public class OrderTuiKuan extends NewBaseAdapter  {
	private List<OrderOne> list;
	public OrderTuiKuan(Context c, List<OrderOne> list) {
		super(c, list);
		this.list=list;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if(convertView==null){
			vh=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.new_order_tuikuan_item, null);
			vh.one_dingdan=(TextView)convertView.findViewById(R.id.one_dingdan);
			vh.time=(TextView)convertView.findViewById(R.id.time);
			vh.all_money=(TextView)convertView.findViewById(R.id.all_money);
			vh.one_daifukuan=(TextView)convertView.findViewById(R.id.one_daifukuan);
			vh.Listview_two_shouhuo=(MyListView)convertView.findViewById(R.id.Listview_two_shouhuo);
			convertView.setTag(vh);
		}else 
		vh=(ViewHolder)convertView.getTag();
		OrderOne one = list.get(position);
		vh.one_dingdan.setText("订单号:"+one.orderId);
		vh.all_money.setText("退款金额￥"+one.cost.replace(".00", ""));
//		if(one.orderStatus.equals("200")){
//			vh.one_daifukuan.setVisibility(View.VISIBLE);
//			vh.one_daifukuan.setText("退款审核中");
//		}else if(one.orderStatus.equals("300")){
//			vh.one_daifukuan.setVisibility(View.VISIBLE);
//			vh.one_daifukuan.setText("退款成功");
//		}else if(one.orderStatus.equals("400")){
//			vh.one_daifukuan.setVisibility(View.VISIBLE);
//			vh.one_daifukuan.setText("退款失败");
//		}
		String arrlist = one.secondOrder;
		String order = one.ordertype;
		List<OrderTwo> twos = JSON.parseArray(arrlist, OrderTwo.class);
		if(twos.size()>0){
			OrderTuiKuanTwo adapter = new OrderTuiKuanTwo(context,one.orderId, twos,order);
			vh.Listview_two_shouhuo.setAdapter(adapter);
			
		}else{
			vh.Listview_two_shouhuo.setVisibility(View.GONE);
		}
		return convertView;
	}
	class ViewHolder{
		private TextView one_dingdan;
		private TextView time;
		private TextView all_money;
		private TextView one_daifukuan;
		private MyListView Listview_two_shouhuo;
	}
}
