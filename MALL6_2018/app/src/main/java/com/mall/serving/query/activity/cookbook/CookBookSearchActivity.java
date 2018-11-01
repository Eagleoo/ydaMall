package com.mall.serving.query.activity.cookbook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshListView;
import com.mall.serving.query.adapter.CookBookGridAdapter;
import com.mall.serving.query.adapter.CookBookSearchAdapter;
import com.mall.serving.query.model.CookBookIndexInfo;
import com.mall.serving.query.model.CookBookIndexInfo.ListInfo;
import com.mall.serving.query.model.CookBookInfo;
import com.mall.serving.query.model.CookBookInfo.Data;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.serving.voip.adapter.NewBaseAdapter;
import com.mall.view.R;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

@ContentView(R.layout.query_cookbook_search_activity)
public class CookBookSearchActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top)
	private View top;
	@ViewInject(R.id.ll)
	private View ll;

	@ViewInject(R.id.tv_hint)
	private TextView tv_hint;
	@ViewInject(R.id.et_search)
	private EditText et_search;

	@ViewInject(R.id.iv_search)
	private ImageView iv_search;

	// @ViewInject(R.id.listview)
	private ListView listview;

	@ViewInject(R.id.refreshListView)
	private PullToRefreshListView refreshListView;

	private List list;
	private CookBookSearchAdapter adapter;
	String url = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		listview = refreshListView.getRefreshableView();
		refreshListView.setPullLoadEnabled(false);
		refreshListView.setPullRefreshEnabled(false);
		getIntentData();

	}

	private void getIntentData() {

		top_left.setVisibility(View.VISIBLE);
		Intent intent = getIntent();
		if (intent.hasExtra("cook")) {
			String cook = intent.getStringExtra("cook");

			top.setVisibility(View.GONE);
			ll.setVisibility(View.VISIBLE);
			setListener();
			list = new ArrayList();
			adapter = new CookBookSearchAdapter(context, list);
			listview.setAdapter(adapter);
			AnimeUtil.setAnimationEmptyView(context, listview, true);
			if (!TextUtils.isEmpty(cook)) {
				et_search.setText(cook);
				getCookDetail(cook, 0 + "", 100 + "", false);

			}

		} else if (intent.hasExtra("indexData")) {
			top.setVisibility(View.VISIBLE);
			ll.setVisibility(View.GONE);

			CookBookIndexInfo info = (CookBookIndexInfo) intent.getSerializableExtra("indexData");
			top_center.setText(info.getName());
			IndexAdapter adapter2 = new IndexAdapter(context, info.getList());
			listview.setAdapter(adapter2);

		} else if (intent.hasExtra("index")) {
			top.setVisibility(View.VISIBLE);
			ll.setVisibility(View.GONE);
			ListInfo info = (ListInfo) intent.getSerializableExtra("index");
			top_center.setText(info.getName());
			list = new ArrayList();
			adapter = new CookBookSearchAdapter(context, list);
			listview.setAdapter(adapter);
			AnimeUtil.setAnimationEmptyView(context, listview, true);

			getCookDetail(info.getId(), 0 + "", 100 + "", true);

		}

	}

	private void setListener() {
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {

				String str = paramCharSequence.toString();
				if (TextUtils.isEmpty(str)) {
					tv_hint.setVisibility(View.VISIBLE);
				} else {
					tv_hint.setVisibility(View.GONE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable paramEditable) {
				// TODO Auto-generated method stub

			}
		});

	}

	@OnClick({ R.id.iv_search })
	public void click(View v) {

		AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new OnAnimEnd() {

			@Override
			public void start() {
				// TODO Auto-generated method stub

			}

			@Override
			public void repeat() {
				// TODO Auto-generated method stub

			}

			@Override
			public void end() {
				String trim = et_search.getText().toString().trim();
				if (TextUtils.isEmpty(trim)) {
					Util.show(" 请输入菜谱、食材");
					return;
				}

				getCookDetail(trim, 0 + "", 100 + "", false);
			}
		});

	}

	private void getCookDetail(String menu, String pn, String rn, boolean isIndex) {
		if (isIndex) {
			url = "http://apis.juhe.cn/cook/index?key=96877d4d3eeb6b8891dc32d6a9ea7a6e&cid=" + menu + "&pn=" + pn
					+ "&rn=" + rn;
		} else {
			url = "http://apis.juhe.cn/cook/query?key=96877d4d3eeb6b8891dc32d6a9ea7a6e&menu=" + menu + "&pn=" + pn
					+ "&rn=" + rn;
		}

		list.clear();
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");
				if (map.get("code").equals("200")) {
					String lists = map.get("list");

					CookBookInfo info = JsonUtil.getPerson(lists, CookBookInfo.class);
					List<Data> mlist = info.getData();
					list.addAll(mlist);
					adapter.notifyDataSetChanged();
				} else {
					AnimeUtil.setNoDataEmptyView("列表为空...", R.drawable.community_dynamic_empty, context, listview, true,
							null);
				}

			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web(url);
				return web.getPlan();
			}
		});

	}

	private class IndexAdapter extends NewBaseAdapter {

		public IndexAdapter(Context c, List list) {
			super(c, list);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int p, View paramView, ViewGroup paramViewGroup) {
			TextView tv = new TextView(context);
			final ListInfo info = (ListInfo) list.get(p);
			tv.setText(info.getName());
			int dp = Util.dpToPx(20);
			tv.setPadding(dp, dp, dp, dp);
			ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,
					ListView.LayoutParams.WRAP_CONTENT);
			tv.setLayoutParams(params);
			tv.setTextSize(15);

			tv.setBackgroundResource(R.drawable.community_white_lightblue_selector);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View paramView) {

					Util.showIntent(context, CookBookSearchActivity.class, new String[] { "index" },
							new Serializable[] { info });

				}
			});

			return tv;
		}

	}

}
