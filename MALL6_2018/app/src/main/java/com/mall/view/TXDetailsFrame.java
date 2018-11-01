package com.mall.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.Stored;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

public class TXDetailsFrame extends Activity {

	@ViewInject(R.id.txListView)
	private ListView listView;
	private int page = 1;
	private Intent intent = null;
	private MyAdapter1 adapter = null;
	boolean isFoot = false; // 用来判断是否已滑动到底部
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.txdetail_frame);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		intent = this.getIntent();
		if (null != UserData.getUser()) {
			bind(page);
			listView.setOnScrollListener(new OnScrollListener() {
				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					// 滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						page++;
						bind(page);
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					if (firstVisibleItem + visibleItemCount == totalItemCount) {
						isFoot = true;
					}
				}
			});
		} else {
			Util.doLogin("请先登录！", this);
		}
	}

	private void bind(int page) {
		final String url = "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
				+ UserData.getUser().getMd5Pwd() + "&pageSize=" + 20 + "&page="
				+ page + "&enumType=0";

		final String parentValue = intent.getStringExtra("parentName");
		final String userKey = intent.getStringExtra("userKey");
		Util.initTop(this, parentValue + "明细", Integer.MIN_VALUE, null);
		
		
		Util.asynTask(this, "正在获取您的帐户信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if(null == runData){
					Util.show("无效的帐户类型...", TXDetailsFrame.this);
					return ;
				}
				
				HashMap<String,List<Stored>> map3= (HashMap<String, List<Stored>>)runData;
				List<Stored> list = map3.get("list");

				if (null == list || 0 == list.size()) {
					Util.show("您没有更多的" + parentValue + "明细", TXDetailsFrame.this);
					return ;
				}
				List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map2 = new HashMap<String, Object>();
					map2.put("date", list.get(i).getDate());
					map2.put("type", list.get(i).getType());
					map2.put("money", list.get(i).getIncome());
					map2.put("desc", list.get(i).getDetail());
					map2.put("bann", list.get(i).getBalance());
					dataList.add(map2);
				}
				if (null == adapter){
					int width =0;
					DisplayMetrics dm=new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					width=dm.widthPixels;
					listView.setAdapter(adapter = new MyAdapter1(TXDetailsFrame.this, dataList,width));
				}else{
					adapter.addChildren(dataList);
				}
			}
			
			@Override
			public Serializable run() {
				Web web = null;
				if ("tixian".equals(userKey))
					web = new Web(Web.txming, url);
				if ("cptixian".equals(userKey))
					web = new Web(Web.cptxming, url);
				else {
					return null;
				}
				HashMap<String,List<Stored>> map = new HashMap<String, List<Stored>>();
				List<Stored> list = web.getList(Stored.class);
				map.put("list", list);
				return map;
			}
		});
	}
}
