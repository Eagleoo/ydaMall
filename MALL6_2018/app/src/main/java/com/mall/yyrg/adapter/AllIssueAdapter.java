package com.mall.yyrg.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mall.view.R;
import com.mall.yyrg.model.ShopPeriods;
/**
 * 产品的期次
 * @author Administrator
 *
 */
public class AllIssueAdapter extends BaseAdapter{
	private List<ShopPeriods> list;
	private Context context;
	private LayoutInflater inflater;
	public AllIssueAdapter(List<ShopPeriods> list,Context context){
		this.list=list;
		this.context=context;
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
			view=inflater.inflate(R.layout.yyrg_goods_fenlei_item, null);
			
		}
		TextView fenlei_name=(TextView) view.findViewById(R.id.fenlei_name);
		fenlei_name.setText("第"+list.get(postion).getPeriodName()+"期");
		if (postion==list.size()-1) {
			fenlei_name.setText("第"+list.get(postion).getPeriodName()+"期     ");
		}
		return view;
	}
	
}