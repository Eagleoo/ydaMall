package com.mall.serving.query.activity.postcode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshListView;
import com.mall.serving.query.adapter.PostCodeResultAdapter;
import com.mall.serving.query.model.CityString;
import com.mall.serving.query.model.PostcodeCityInfo;
import com.mall.serving.query.model.PostcodeInfo;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.view.App;
import com.mall.view.R;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

@ContentView(R.layout.query_postcode_result_activity)
public class PostCodeResultQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;
	@ViewInject(R.id.tv_sum)
	private TextView tv_sum;
	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	private List list;
	@ViewInject(R.id.refreshListView)
	private PullToRefreshListView refreshListView;
	private ListView listview;

	private PostCodeResultAdapter adapter;

	private int page = 1;
	private int size = 50;
	private boolean mIsStart = true;

	private boolean isCity2Post = false;

	String post;
	String pid = "";
	String cid = "";
	String did = "";
	String q = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		list = new ArrayList();

		getIntentData();
		setView();
		listview = refreshListView.getRefreshableView();
		refreshListView.setTimeId(203);
		refreshListView.setPullLoadEnabled(true);
		listview.setDividerHeight(1);
		// listview.setBackgroundResource(R.color.main_deep_bg);
		adapter = new PostCodeResultAdapter(context, list);
		listview.setAdapter(adapter);
		AnimeUtil.setAnimationEmptyView(context, listview, true);
		setListener();
	}

	private void setView() {

		top_center.setText("查询结果");

		top_left.setVisibility(View.VISIBLE);

	}

	private void getIntentData() {
		Intent intent = getIntent();

		if (intent.hasExtra("postcode")) {
			post = intent.getStringExtra("postcode");
			postCode2cityQuery(post);
			isCity2Post = false;
		} else {
			isCity2Post = true;

			if (intent.hasExtra("pid")) {
				pid = intent.getStringExtra("pid");
			}
			if (intent.hasExtra("cid")) {
				cid = intent.getStringExtra("cid");
			}
			if (intent.hasExtra("did")) {
				did = intent.getStringExtra("did");
			}
			if (intent.hasExtra("q")) {
				q = intent.getStringExtra("q");
			}

			city2postCodeQuery(pid, cid, did, q);

		}

	}

	private void setListener() {

		OnRefreshListener refreshListener = new OnRefreshListener() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				mIsStart = true;

				if (isCity2Post) {
					city2postCodeQuery(pid, cid, did, q);
				} else {
					postCode2cityQuery(post);
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				mIsStart = false;
				if (isCity2Post) {
					city2postCodeQuery(pid, cid, did, q);
				} else {
					postCode2cityQuery(post);
				}
			}
		};
		refreshListView.setOnRefreshListener(refreshListener);

	}

	private void city2postCodeQuery(final String pid, final String cid, final String did, final String q) {
		setPagesize();
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				thissuccess(runData.toString());
				thisrequestEnd();
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://v.juhe.cn/postcode/search.php?key=3287cd51e91a12cba3da80bd32704041&pid=" + pid
						+ "&cid=" + cid + "&did=" + did + "&q=" + q + "&page=" + page + "&size=" + size);
				return web.getPlan();
			}
		});
	}

	private void setPagesize() {
		if (mIsStart) {
			page = 1;
		} else {
			page++;
		}
	}

	private void postCode2cityQuery(final String post) {
		setPagesize();
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				thissuccess(runData.toString());
				thisrequestEnd();
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://v.juhe.cn/postcode/query?postcode=" + post
						+ "&key=3287cd51e91a12cba3da80bd32704041&page=" + page + "&size=" + size);
				return web.getPlan();
			}
		});
	}

	private void thissuccess(String result) {
		Map<String, String> map = JsonUtil.getNewApiJsonQuery(result.toString());
		String message = map.get("message");

		String lists = map.get("list");
		if (TextUtils.isEmpty(lists)) {
			if (!TextUtils.isEmpty(message)) {
				Util.show(message);
			}

			return;
		}
		PostcodeInfo info = JsonUtil.getPerson(lists, PostcodeInfo.class);
		if (info != null && info.getList() != null && info.getList().size() > 0) {
			if (page == 1) {
				list.clear();
			}
			list.addAll(info.getList());

			adapter.notifyDataSetChanged();
			refreshListView.setPullLoadEnabled(true);
			String totalcount = info.getTotalcount();
			String pagesize = info.getPagesize();
			String totalpage = info.getTotalpage();
			String currentpage = info.getCurrentpage();
			// tv_sum.setVisibility(View.VISIBLE);
			tv_sum.setText("总条数：" + totalcount + "        " + currentpage + "/" + totalpage);
			if (currentpage.equals(totalpage)) {
				refreshListView.setHasMoreData(false);
				refreshListView.setPullLoadEnabled(false);
			}
		} else {
			refreshListView.setHasMoreData(false);
			refreshListView.setPullLoadEnabled(false);
		}

	}

	private void thisrequestEnd() {
		refreshListView.onPullDownRefreshComplete();
		refreshListView.onPullUpRefreshComplete();
		refreshListView.setHasMoreData(false);
		AnimeUtil.setNoDataEmptyView("未能查询到数据...", R.drawable.community_dynamic_empty, context, listview, true, null);

	}

}
