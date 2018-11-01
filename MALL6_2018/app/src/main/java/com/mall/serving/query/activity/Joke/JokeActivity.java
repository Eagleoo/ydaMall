package com.mall.serving.query.activity.Joke;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.textview.TextViewBack;
import com.mall.serving.query.activity.Joke.JokeBean.ResultBean.DataBean;
import com.mall.view.R;
import com.pulltorefresh.PullToRefreshListView;
import com.pulltorefresh.PullToRefreshListView.PullToRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


@ContentView(R.layout.activity_joke)
public class JokeActivity extends  BaseActivity{
	
	@ViewInject(R.id.refreshable_view)
	private PullToRefreshListView refreshable_view;
	@ViewInject(R.id.joke_list)
	private ListView listview;
	@ViewInject(R.id.top_left)
	private TextViewBack back;
	private int page=1;
	private int pagesize=20;
	private String sort="desc"; //desc:指定时间之前发布的，asc:指定时间之后发布的
	
	private JokeAdapter jokeAdapter;

private Context context;
private ArrayList<JokeBean.ResultBean.DataBean> myList=new ArrayList<JokeBean.ResultBean.DataBean>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		ViewUtils.inject(this);
		
		context=this;
		initListen();
		jokeQuery();
	}
	
	private void initListen() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		jokeAdapter =new JokeAdapter(context, myList);
		
		listview.setAdapter(jokeAdapter);
		

		refreshable_view.setOnRefreshListener(new PullToRefreshListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				page=1;
				jokeQuery();
			}
			
		},12);
		
		listview.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= jokeAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					page++;
					jokeQuery();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	private void jokeQuery() {
	
final String time=		getSecondTimestamp(new Date());
Log.e("时间戳",time);
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				if (runData==null) {
					Toast.makeText(context, "未获取到数据，请稍后重试", Toast.LENGTH_SHORT).show();
					return;
				}
				Gson gson=new Gson();
				JokeBean jokeBean= gson.fromJson(runData.toString(), JokeBean.class);
				if(jokeBean==null){
					com.mall.util.Util.show("网络异常,请检查网络！", context);
					return;
				}
				String reason=jokeBean.getReason()+"";
				if (reason.equals("Success")) {
					if (jokeBean.getResult().getData()==null) {
						Toast.makeText(context, "未获取到数据，请稍后重试", Toast.LENGTH_SHORT).show();
						return;
					}
					
					ArrayList<JokeBean.ResultBean.DataBean> list=(ArrayList<DataBean>) jokeBean.getResult().getData();
					if (page==1) {
						myList.clear();
					}
					myList.addAll(list);
					jokeAdapter.notifyDataSetChanged();
				}else{
					Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
				}
			
				refreshable_view.finishRefreshing(); 
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web(
						"http://japi.juhe.cn/joke/content/list.from?key="+"c161e23c29ffcd1fc761aa4d0d0586d9"+"&page="+page+"&pagesize="+pagesize+"&sort="+sort+"&time="+time);
				return web.getPlan();
			}
		});
	}
	
	/** 
	 * 获取精确到秒的时间戳 
	 * @return 
	 */  
	public static String getSecondTimestamp(Date date){  
	    if (null == date) {  
	        return 0+"";  
	    }  
	    String timestamp = String.valueOf(date.getTime());  
	    int length = timestamp.length();  
	    if (length > 3) {  
	        return timestamp.substring(0,length-3);  
	    } else {  
	        return 0+"";  
	    }  
	}
	

	

	

}
