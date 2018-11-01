package com.mall.serving.school;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshListView;
import com.mall.view.R;

@ContentView(R.layout.ydaschool_activity)
public class YdaSchoolCourseActivity extends BaseActivity{
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;
	
	@ViewInject(R.id.refreshListView)
	private PullToRefreshListView refreshListView;
	private ListView lv;
	private List list;
	private YdaSchoolCourseAdapter adapter;
	
	private boolean mIsStart = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		list=new ArrayList();
		setListener() ;
		lv = refreshListView.getRefreshableView();
		lv.setDividerHeight(1);
		refreshListView.setPullRefreshEnabled(true);
		refreshListView.setPullLoadEnabled(true);
		for (int i = 0; i < 20; i++) {
			list.add("");
		}
	
		setView();
	   adapter = new YdaSchoolCourseAdapter(context, list);
	   lv.setAdapter(adapter);
	   
	}
	
	
	private void setView() {
		top_center.setText("创业课程");
		top_left.setVisibility(View.VISIBLE);
	
		
	}
	
	
	private void setListener() {
		OnRefreshListener refreshListener = new OnRefreshListener() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				mIsStart = true;
				getYdaSchoolList();
			 

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				mIsStart = false;
				getYdaSchoolList();
			}
		};
		refreshListView.setOnRefreshListener(refreshListener);

	}
	
	private void getYdaSchoolList() {
		AnimeUtil.setAnimationEmptyView(this, lv, true);
		for (int i = 0; i < 10; i++) {
			list.add("");
		}

		adapter.notifyDataSetChanged();
		refreshListView.onPullDownRefreshComplete();
		refreshListView.onPullUpRefreshComplete();
		refreshListView.setHasMoreData(true);
	}
	
	
	
	

}
