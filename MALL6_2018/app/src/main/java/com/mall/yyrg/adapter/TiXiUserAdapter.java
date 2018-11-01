package com.mall.yyrg.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mall.card.bean.CallBackListener;
import com.mall.view.R;
import com.mall.yyrg.model.TiXiUser;

public class TiXiUserAdapter extends BaseAdapter{
	private List<TiXiUser> list=new ArrayList<TiXiUser>();
	private Context context;
	private LayoutInflater inflater;
	private CallBackListener callBackListener;
	public TiXiUserAdapter(Context context,List<TiXiUser> list){
		this.context=context;
		this.list=list;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public void setCallBack(CallBackListener callBackListener){
		this.callBackListener=callBackListener;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder;
		if (convertView==null) {
			holder=new Holder();
			convertView=inflater.inflate(R.layout.tixi_user_list_item, null);
			holder.name=(TextView) convertView.findViewById(R.id.username);
			holder.login=(TextView) convertView.findViewById(R.id.userlogin);
			holder.phone=(TextView) convertView.findViewById(R.id.phone);
			holder.zhaoshang=(TextView) convertView.findViewById(R.id.zhaoshang);
			convertView.setTag(holder);
		}else {
			holder=(Holder) convertView.getTag();
		}
		holder.name.setText(list.get(position).getName());
		holder.login.setText(list.get(position).getUserId());
		holder.phone.setText(list.get(position).getMobilePhone());
		holder.zhaoshang.setText(list.get(position).getInviter());
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callBackListener.callBack(list.get(position));
			}
		});
		return convertView;
	}
	public class Holder{
		TextView login;
		TextView name;
		TextView phone;
		TextView zhaoshang;
	}
	
}
