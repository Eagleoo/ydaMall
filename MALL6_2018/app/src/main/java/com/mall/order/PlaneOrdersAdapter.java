package com.mall.order;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mall.model.PlaneOrdersBean;
import com.mall.view.R;

public class PlaneOrdersAdapter extends BaseAdapter {
	
	
	private List<PlaneOrdersBean> list=new ArrayList<PlaneOrdersBean>();
	private Context context;
	private LayoutInflater flater;
	public PlaneOrdersAdapter( Context context) {
		super();
		this.context = context;
		flater = LayoutInflater.from(context);
	}
	 public void setList(List<PlaneOrdersBean> list){
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
			convertView = flater.inflate(R.layout.plane_orders_item, null);
		}
		TextView dan_from=(TextView) convertView.findViewById(R.id.dan_from);
		TextView dan_to=(TextView) convertView.findViewById(R.id.dan_to);
		TextView fan_from=(TextView) convertView.findViewById(R.id.fan_from);
		TextView fan_to=(TextView) convertView.findViewById(R.id.fan_to);
		TextView departureDate=(TextView) convertView.findViewById(R.id.departureDate);
		TextView hangban=(TextView) convertView.findViewById(R.id.hangban);
		TextView departureDate1=(TextView) convertView.findViewById(R.id.departureDate1);
		TextView arriveDate=(TextView) convertView.findViewById(R.id.arriveDate);
		TextView arriveDate1=(TextView) convertView.findViewById(R.id.arriveDate1);
		TextView hangban1=(TextView) convertView.findViewById(R.id.hangban1);
		TextView jine=(TextView) convertView.findViewById(R.id.jine);
		TextView order_state=(TextView) convertView.findViewById(R.id.order_state);
		LinearLayout linn1=(LinearLayout) convertView.findViewById(R.id.linn1);
		RelativeLayout re2=(RelativeLayout) convertView.findViewById(R.id.re2);
		order_state.setText(list.get(position).getOrderState());
		order_state.setBackgroundColor(context.getResources().getColor(R.color.headertop));
		jine.setText("￥"+list.get(position).getMoney());
		hangban.setText(list.get(position).getFlightNo());
		String[] qifei=list.get(position).getFromFly().split("\\|");
		String[] jiangluo=list.get(position).getToFly().split("\\|");
		String[] departureDates=list.get(position).getDepartureDate().split("\\|");
		String[] hangbans=list.get(position).getFlightNo().split("\\|");
		if (qifei.length>1) {//双城
			re2.setVisibility(View.VISIBLE);
			linn1.setVisibility(View.VISIBLE);
			dan_from.setText(qifei[0]);
			dan_to.setText(jiangluo[0]);
			fan_from.setText(jiangluo[1]);
			hangban.setText(hangbans[0]);
			hangban1.setText(hangbans[1]);
			fan_to.setText(qifei[1]);
			departureDate.setText(departureDates[0]);
			departureDate1.setText(departureDates[1]);
			/*danorfan.setText("(返程)");
			from_to.setText(qifei[0]+" - "+jiangluo[0]+"\n"+qifei[1]+" - "+jiangluo[1]);
			System.out.println(qifei[0]+" - "+jiangluo[0]+"\n"+jiangluo[1]+" - "+qifei[1]);*/
		}else {//单程
			re2.setVisibility(View.GONE);
			linn1.setVisibility(View.GONE);
			dan_from.setText(qifei[0]);
			dan_to.setText(jiangluo[0]);
			hangban.setText(hangbans[0]);
			hangban1.setText("");
			fan_from.setText("");
			fan_to.setText("");
			departureDate.setText(departureDates[0]);
			departureDate1.setText("");
		}
		return convertView;
	}
}
