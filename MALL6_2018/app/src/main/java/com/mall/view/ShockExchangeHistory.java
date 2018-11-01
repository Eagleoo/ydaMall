package com.mall.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.Stored;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

public class ShockExchangeHistory extends Activity {
	@ViewInject(R.id.listView)
	private ListView listView;
	@ViewInject(R.id.sb_exchange_history)
	private TextView sb_exchange_history;
	@ViewInject(R.id.czzh_exchange_history)
	private TextView czzh_exchange_history;
	@ViewInject(R.id.sanyuenei)
	private LinearLayout sanyuenei;
	@ViewInject(R.id.sanyueqian)
	private LinearLayout sanyueqian;
	private String listType = "sb";
	private String searchUser="";
	private int page = 1;
    private ShockExchangeHistoryAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shock_exchange_history);
		ViewUtils.inject(this);
		init();
	}
	private void init() {
		Util.initTop(this, "转账记录", Integer.MIN_VALUE, null);
		searchUser=this.getIntent().getStringExtra("searchUser");
		page();
		scrollPage();
	}
	@OnClick({ R.id.fi1, R.id.fi2 })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.fi1:
			sb_exchange_history.setTextColor(getResources().getColor(
					R.color.new_headertop));
			sb_exchange_history.setBackgroundColor(getResources().getColor(
					R.color.bg));
			czzh_exchange_history.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			sanyuenei.setVisibility(View.VISIBLE);
			sanyueqian.setVisibility(View.INVISIBLE);
			czzh_exchange_history.setTextColor(getResources().getColor(
					R.color.bg));
			listType = "sb";
			page=1;
			if(adapter!=null){
				adapter.clear();
			}
			page();
			break;
		case R.id.fi2:
			// 双程
			sb_exchange_history.setTextColor(getResources()
					.getColor(R.color.bg));
			sanyuenei.setVisibility(View.INVISIBLE);
			sanyueqian.setVisibility(View.VISIBLE);
			czzh_exchange_history.setTextColor(getResources().getColor(
					R.color.new_headertop));
			sb_exchange_history.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			czzh_exchange_history.setBackgroundColor(getResources().getColor(
					R.color.bg));
			listType = "rec";
			page=1;
			if(adapter!=null){
				adapter.clear();
			}
			page();
			break;
		}
	}
	private void page() {
		final String url = "userid=" + UserData.getUser().getUserId()
				+ "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&pageSize="
				+ 20 + "&page=" + (page++) ;
		Util.asynTask(this, "正在获取您的帐户信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (null == runData) {
					return;
				}
				HashMap<String, List<Stored>> map3 = (HashMap<String, List<Stored>>) runData;
				List<Stored> list = map3.get("list");
				if(list!=null&&list.size()>0){
					if(adapter==null){
						adapter=new ShockExchangeHistoryAdapter(ShockExchangeHistory.this);
						listView.setAdapter(adapter);
					}
					adapter.setList(list);
				}
			}
			@Override
			public Serializable run() {
				Web web = null;
				if ("sb".equals(listType)) {
					web = new Web(Web.getSBDetailList, url+"&enumType=9"+"&searchUser="+searchUser);
				} else if ("rec".equals(listType)) {
					web = new Web(Web.getRechargeAccount, url+"&enumType=40"+"&searchUser="+searchUser);
				} else {
					return null;
				}
				HashMap<String, List<Stored>> map=new HashMap<String, List<Stored>>();
				List<Stored> list = web.getList(Stored.class);
				map.put("list", list);
				return map;
			}
		});
	}
	private void scrollPage() {
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					page();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
    public class  ShockExchangeHistoryAdapter extends BaseAdapter{
		private LayoutInflater inflater;
        private Context c;
        private List<Stored> list=new ArrayList<Stored>();
		public ShockExchangeHistoryAdapter(Context c){
			this.c=c;
			inflater=LayoutInflater.from(this.c);
		}
		public void setList(List<Stored> list){
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}
		@Override
		public int getCount() {
			return this.list.size();
		}
		@Override
		public Object getItem(int arg0) {
			return this.list.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
        private void clear(){
        	this.list.clear();
        	this.notifyDataSetChanged();
        }
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder h=null;
			final Stored s=this.list.get(arg0);
			if(arg1==null){
				arg1=inflater.inflate(R.layout.list_item_of_styleone, null);
				h=new ViewHolder();
				h.t1=(TextView) arg1.findViewById(R.id.style_text_one);
				h.t2=(TextView) arg1.findViewById(R.id.style_text_two);
				h.t3=(TextView) arg1.findViewById(R.id.style_text_three);
				arg1.setTag(h);
			}else{
				h=(ViewHolder) arg1.getTag();
			}
			if(!Util.isNull(searchUser)){
				h.t1.setText(searchUser);
			}else{
				h.t1.setText(s.getComments());
			}
			if ("sb".equals(listType)) 
				h.t2.setText(s.getIncome()+"商币");
			else
				h.t2.setText(s.getIncome()+"元");
			h.t3.setText(Util.friendly_time(s.getDate()));
			DisplayMetrics dm=new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			final int width=dm.widthPixels;
			OnClickListener click = new View.OnClickListener() {
				@Override
				public void onClick(View v) {     
					if (Util.isNull(s)) {
						Util.detailInformation(c, "暂无详细内容!", "详细信息", width);
					} else {
						String message="时间：" + s.getDate()
								+ "\n金额：" + Util.deleteX(s.getIncome()+"")
								+ "\n描述：" + s.getDetail()+ "";
						Util.detailInformation(c, message, "详细信息", width);
					}
				}
			};
			arg1.setOnClickListener(click);
			return arg1;
		}
	}
    public class ViewHolder{
	   TextView t1;
	   TextView t2;
	   TextView t3;
   }
}
