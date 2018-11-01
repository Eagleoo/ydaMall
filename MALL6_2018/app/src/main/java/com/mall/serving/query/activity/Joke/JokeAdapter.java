package com.mall.serving.query.activity.Joke;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

import com.mall.model.messageboard.UserMessageBoard;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.activity.Joke.JokeBean.ResultBean.DataBean;
import com.mall.view.R;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JokeAdapter  extends BaseAdapter{

	private Context mContext;
	private ArrayList<DataBean> myList;
	private LayoutInflater inflater;
	public JokeAdapter(Context context, ArrayList<DataBean> list){
		this.mContext=context;
		this.myList=list;
		this.inflater=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (myList==null) {
			return 0;
		}
		
		return myList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder =null;
		if (convertView==null) {
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.jokeitem, parent,false);
			viewHolder.content=(TextView) convertView.findViewById(R.id.contentjoke);
			viewHolder.shar=(ImageView) convertView.findViewById(R.id.sharejoke);
			viewHolder.time=(TextView) convertView.findViewById(R.id.timetv);
					convertView.setTag(viewHolder);   
		}else{
			viewHolder = (ViewHolder) convertView.getTag();   
		}
		final DataBean item= myList.get(position);
		viewHolder.content.setText(item.getContent());
		String time;
		try{
			String[] times=item.getUpdatetime().split(" ");
			time=times[0];
		}catch(Exception e){
			time="";
		}
		
		viewHolder.time.setText(time);
		viewHolder.shar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			Log.e("点击分享按钮","分享");	
			fenxiangClick(item);
			}
		});
		
		return convertView;
	}

	/**
	 * @param args
	 */
	
class ViewHolder{
	TextView content,time;
	ImageView shar;
}

public void fenxiangClick(DataBean dataBean) {


	OnekeyShare oks = new OnekeyShare();
	oks.setTitle("笑话");
	// titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
//	oks.setTitleUrl("http://sharesdk.cn");
	oks.setTitleUrl(dataBean.getContent());
	// text是分享文本，所有平台都需要这个字段
	oks.setText(dataBean.getContent());
	oks.show(mContext);
}
	
}
