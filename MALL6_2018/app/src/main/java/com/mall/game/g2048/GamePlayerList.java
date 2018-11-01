package com.mall.game.g2048;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

public class GamePlayerList extends Activity {
	@ViewInject(R.id.list)
	private ListView listView;
	private User user;
	private GamePlayerListAdapter adapter;
	private int page=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_player_list);
		ViewUtils.inject(this);
		if(UserData.getUser()!=null){
			user=UserData.getUser();
		}else{
			user=new User();
		}
		initTop();
		page();    
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= listView.getAdapter().getCount()
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
	private void initTop(){
		Util.initTop(this,"玩家足迹", Integer.MIN_VALUE, null);
	}
	private void page(){
		NewWebAPI.getNewInstance().getGameRanking(page++, 10, new WebRequestCallBack(){
			@Override
			public void success(Object result) {
				if(Util.isNull(result)){
					return;
				}
				JSONObject obj = null;
                 try{ 
                	 obj= JSON.parseObject(result.toString());
				}catch (Exception e) {
					e.printStackTrace();  
					return;
				}
				if(200 != obj.getInteger("code").intValue()){
					Util.show(obj.getString("message"), GamePlayerList.this);
					return ;
				}
				JSONArray array = obj.getJSONArray("list"); 
				List<GamePlayerModel> list=new ArrayList<GamePlayerList.GamePlayerModel>();
				for(int i=0;i<array.size();i++){ 
					JSONObject o=array.getJSONObject(i);
					GamePlayerModel m=new GamePlayerModel();
					m.setUserid(o.getString("userid"));
					m.setFraction(o.getString("Fraction"));
					m.setTimes(o.getString("times"));
					m.setDate(o.getString("enddate"));
					list.add(m);  
				}               
				if(list!=null&&list.size()>0){
					if(adapter==null){
						adapter=new GamePlayerListAdapter(GamePlayerList.this);
						listView.setAdapter(adapter);
					}
					adapter.setList(list);
				}
			}

			@Override
			public void fail(Throwable e) {
				super.fail(e);
			}
		});
	}
	class GamePlayerModel{
		private String userid="";
		private String gametype="";
		private String Fraction="";
		private String times="";
		private String date="";
		private String enddate="";
		public String getUserid() {
			return userid;
		}
		public void setUserid(String userid) {
			this.userid = userid;
		}
		public String getGametype() {
			return gametype;
		}
		public void setGametype(String gametype) {
			this.gametype = gametype;
		}
		public String getFraction() {
			return Fraction;
		}
		public void setFraction(String fraction) {
			Fraction = fraction;
		}
		public String getTimes() {
			return times;
		}
		public void setTimes(String times) {
			this.times = times;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getEnddate() {
			return enddate;
		}
		public void setEnddate(String enddate) {
			this.enddate = enddate;
		}
		
	}
	class ViewHolder{
		ImageView face;
		TextView userid;
		TextView score;  
		TextView time;
		TextView count;  
	}  
	public class GamePlayerListAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private Context c;
        private List<GamePlayerModel> list=new ArrayList<GamePlayerList.GamePlayerModel>();
		public GamePlayerListAdapter(Context c){
        	this.c=c;
        	inflater=LayoutInflater.from(c);
        }
		public void setList(List<GamePlayerModel> list){
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
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder h=null;
			GamePlayerModel m=this.list.get(arg0);
			if(arg1==null){
				arg1=inflater.inflate(R.layout.game_log_item, null);
				h=new ViewHolder();
				h.face=(ImageView) arg1.findViewById(R.id.face);
				h.userid=(TextView) arg1.findViewById(R.id.userid);
				h.score=(TextView) arg1.findViewById(R.id.score);
				h.count=(TextView) arg1.findViewById(R.id.counts);
				h.time=(TextView) arg1.findViewById(R.id.time);
				arg1.setTag(h);
			}else{
				h=(ViewHolder) arg1.getTag();
			}
			h.userid.setText(m.getUserid());
			h.score.setText("游戏得分："+m.getFraction());
			h.count.setText("游戏次数："+m.getTimes());
			h.time.setText("游戏时间："+Util.friendly_time(m.getDate()));
			return arg1;
		}
	}
}
