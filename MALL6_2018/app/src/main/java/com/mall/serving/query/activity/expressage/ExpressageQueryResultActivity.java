package com.mall.serving.query.activity.expressage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.adapter.ExpressageAdapter;
import com.mall.serving.query.model.ExpressageCompanyInfo;
import com.mall.serving.query.model.ExpressageInfo;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.view.App;
import com.mall.view.R;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

@SuppressLint("NewApi")
@ContentView(R.layout.query_expressage_result_activity)
public class ExpressageQueryResultActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;
	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	@ViewInject(R.id.listview)
	private ListView listview;
	private List<ExpressageInfo> list;
	private ExpressageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(context);
		setView();

		list = new ArrayList<ExpressageInfo>();

		adapter = new ExpressageAdapter(context, list);

		listview.setAdapter(adapter);

		AnimeUtil.setAnimationEmptyView(context, listview, true);
		getIntentData();

	}

	private void setView() {

		top_left.setVisibility(View.VISIBLE);

		top_center.setText("查询结果");

	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent.hasExtra("list")) {
			List<ExpressageInfo> mlist = (List<ExpressageInfo>) intent
					.getSerializableExtra("list");

			for (ExpressageInfo info : mlist) {
				LogUtils.e("排序前 " + info.getDatetime());
			}
			Collections.sort(mlist, new Comparator<ExpressageInfo>() {
				@Override
				public int compare(ExpressageInfo lhs, ExpressageInfo rhs) {
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date date1 = null;
					try {
						date1 = sdf.parse(lhs.getDatetime() + ":00");
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Date date2 = null;
					try {
						date2 = sdf.parse(rhs.getDatetime() + ":00");
					} catch (ParseException e) {
						e.printStackTrace();
					}

					if (date1 == null || date2 == null)
						return -1;
					return (int) (date2.getTime() - date1.getTime());
				}
			});

			for (ExpressageInfo info : mlist) {
				LogUtils.e("排序后 " + info.getDatetime());
			}

			list.addAll(mlist);
			adapter.notifyDataSetChanged();
		}

		if (intent.hasExtra("com")) {
			String com = intent.getStringExtra("com");
			String no = intent.getStringExtra("no");

			try {
				List<ExpressageCompanyInfo> findAll = DbUtils.create(
						App.getContext()).findAll(ExpressageCompanyInfo.class);
				if (findAll != null) {
					for (ExpressageCompanyInfo eInfo : findAll) {
						if (com.contains(eInfo.getCom())
								|| eInfo.getCom().contains(com)) {

							expressageSearch(eInfo.getNo(), no);
							break;
						}

					}
				}

			} catch (DbException e) {

				e.printStackTrace();
			}

		}

	}

	@SuppressLint("NewApi")
	private void expressageSearch(String com, String no) {
		AnimeUtil.startImageAnimation(iv_center);
		Parameters params = new Parameters();
		params.add("com", com);
		params.add("no", no);
		JuheWeb.getJuheData(params, 43, "http://v.juhe.cn/exp/index",
				JuheData.GET, new JuheRequestCallBack() {
					@SuppressLint("NewApi")
					@Override
					public void success(int err, String reason, String result) {
						LogUtils.e(result);
						Map<String, String> map = JsonUtil
								.getNewApiJsonQuery(result.toString());
						String message = map.get("message");
						if (map.get("code").equals("200")) {
							String results = map.get("list");
							try {
								JSONObject jObject = new JSONObject(results);
								String lists = jObject.optString("list");
								List<ExpressageInfo> mlist = JsonUtil
										.getPersons(
												lists,
												new TypeToken<List<ExpressageInfo>>() {
												});

								list.addAll(mlist);

								adapter.notifyDataSetChanged();

							} catch (JSONException e) {
								Util.show("快递查询失败：接口返回数据格式错误！");
							}
						} else {

							iv_center.getAnimation().cancel();
							iv_center = null;
							if (!TextUtils.isEmpty(message)) {
								Util.show(message);
							}
						}
						AnimeUtil.cancelImageAnimation(iv_center);
					}

					@Override
					public void requestEnd() {
						AnimeUtil.setNoDataEmptyView("列表为空...",
								R.drawable.community_dynamic_empty, context,
								listview, true, null);
					}

					@Override
					public void fail(int err, String reason, String result) {

					}
				});

	}

}
