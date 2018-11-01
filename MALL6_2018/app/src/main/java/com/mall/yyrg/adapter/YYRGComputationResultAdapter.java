package com.mall.yyrg.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mall.view.R;
import com.mall.yyrg.model.TopHundredRecord;

public class YYRGComputationResultAdapter extends BaseAdapter{
	private List<TopHundredRecord> list;
	private Context context;
	private LayoutInflater inflater;
	private String message;
	public YYRGComputationResultAdapter(Context context,List<TopHundredRecord> list ,String message){
		this.list=list;
		this.context=context;
		this.message=message;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int postion, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (view==null) {
			view=inflater.inflate(R.layout.yyrg_computation_item, null);
		}
		TextView buy_date=(TextView) view.findViewById(R.id.buy_date);
		TextView buy_time=(TextView) view.findViewById(R.id.buy_time);
		TextView result=(TextView) view.findViewById(R.id.result);
		TextView user_id=(TextView) view.findViewById(R.id.user_id);
		TextView jisuan_message=(TextView) view.findViewById(R.id.jisuan_message);
		user_id.setText(list.get(postion).getUserid());
		buy_date.setText(list.get(postion).getBuyTime().split(" ")[0]);
		buy_time.setText(list.get(postion).getBuyTime().split(" ")[1]);
		result.setText(list.get(postion).getBuyTime().split(" ")[1].replace(":", "").replace(".", ""));
		if (list.get(postion).getState().equals("1")) {
			jisuan_message.setVisibility(View.GONE);
		}else if (list.get(postion).getState().equals("2")) {
			jisuan_message.setVisibility(View.VISIBLE);
			jisuan_message.setText(message);
		}
		return view;
	}

}
