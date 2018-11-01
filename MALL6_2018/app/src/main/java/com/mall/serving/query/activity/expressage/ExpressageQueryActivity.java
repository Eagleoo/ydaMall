package com.mall.serving.query.activity.expressage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.dialog.CustomListDialog;
import com.mall.serving.query.model.ExpressageCompanyInfo;
import com.mall.serving.query.model.ExpressageHistroty;
import com.mall.serving.query.model.ExpressageInfo;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.serving.query.view.dialog.ExpressageDropPopupWindow;
import com.mall.view.App;
import com.mall.view.R;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

@ContentView(R.layout.query_expressage_activity)
public class ExpressageQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;
	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	@ViewInject(R.id.et_query_number)
	private EditText et_query_number;
	@ViewInject(R.id.tv_query_company)
	private TextView tv_query_company;
	@ViewInject(R.id.tv_query)
	private TextView tv_query;

	private String strs[];
	private List<ExpressageCompanyInfo> list;

	private String com;

	public void setCom(String com) {
		this.com = com;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(context);
		setView();
		list = new ArrayList<ExpressageCompanyInfo>();
		saveData();
	}

	private void setView() {

		String postNum = this.getIntent().getStringExtra("postNumber");
		String postCpy = this.getIntent().getStringExtra("postCompany");
		if (!Util.isNull(postNum)) {
			et_query_number.setText(postNum);
		}
		if (!Util.isNull(postCpy)) {
			tv_query_company.setText(postCpy);
		}
		top_left.setVisibility(View.VISIBLE);
		
		top_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		top_center.setText("物流查询");

	}

	@OnClick({ R.id.tv_query, R.id.tv_query_company, R.id.iv_query_hsitory })
	public void click(View v) {
		switch (v.getId()) {

		case R.id.tv_query:
			String no = et_query_number.getText().toString();
			if (TextUtils.isEmpty(no)) {

				Util.show("请输入快递单号！");
				return;
			}
			if (TextUtils.isEmpty(com)) {

				Util.show("请选择快递公司！");
				return;
			}

			expressageSearch(v, com, no);

			break;
		case R.id.tv_query_company:

			showDialog();

			break;

		case R.id.iv_query_hsitory:

			try {
				List<ExpressageHistroty> findAll = DbUtils.create(App.getContext())
						.findAll(Selector.from(ExpressageHistroty.class));
				if (findAll != null && findAll.size() > 0) {

					new ExpressageDropPopupWindow(context, et_query_number, tv_query_company, findAll);
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		}
	}

	private void expressageCompany() {
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");
				if (map.get("code").equals("200")) {
					String lists = map.get("list");
					if (TextUtils.isEmpty(lists)) {
						return;
					}
					final List<ExpressageCompanyInfo> mlist = JsonUtil.getPersons(lists,
							new TypeToken<List<ExpressageCompanyInfo>>() {
							});
					if (mlist != null) {
						Util.asynTask(new IAsynTask() {

							@Override
							public void updateUI(Serializable runData) {
								// TODO Auto-generated method stub

							}

							@Override
							public Serializable run() {
								try {
									DbUtils.create(App.getContext()).saveBindingIdAll(mlist);
								} catch (DbException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return null;
							}
						});

						list.clear();
						list.addAll(mlist);

					}

				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://v.juhe.cn/exp/com?key=df230bfc449b7e4b2013cbfbe5523eae");
				return web.getPlan();
			}
		});
	}

	private void saveData() {
		try {
			List<ExpressageCompanyInfo> findAll = DbUtils.create(App.getContext()).findAll(ExpressageCompanyInfo.class);

			if (findAll != null && findAll.size() > 0) {
				list.clear();
				list.addAll(findAll);

			} else {
				expressageCompany();
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void showDialog() {
		if (list.size() == 0) {
			return;
		}
		strs = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			strs[i] = list.get(i).getCom();
		}

		new CustomListDialog(context, "请选择快递公司", strs, new CustomListDialog.OnItemClick() {

			@Override
			public void itemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				String text = strs[position];

				tv_query_company.setText(text);
				com = list.get(position).getNo();
				Log.i("tag", "------------" + com);

			}
		}).show();

	}

	private void expressageSearch(final View view, final String com, final String no) {
		if (!Util.isNetworkConnected()) {
			Util.show("请检查网络连接～");
			return;
		}
		AnimeUtil.startImageAnimation(iv_center);
		view.setEnabled(false);

		Parameters params = new Parameters();
		params.add("com", com);
		params.add("no", no);

		try {
			ExpressageHistroty findFirst = DbUtils.create(App.getContext())
					.findFirst(Selector.from(ExpressageHistroty.class).where("num", "=", no));

			if (findFirst == null) {

				final ExpressageHistroty histroty = new ExpressageHistroty(no, tv_query_company.getText().toString(),
						com);
				Util.asynTask(new IAsynTask() {

					@Override
					public void updateUI(Serializable runData) {
						// TODO Auto-generated method stub

					}

					@Override
					public Serializable run() {
						try {
							DbUtils.create(App.getContext()).saveBindingId(histroty);
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}
				});

			}

		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");
				if (map.get("code").equals("200")) {
					String results = map.get("list");
					try {
						JSONObject jObject = new JSONObject(results);
						String lists = jObject.optString("list");

						List<ExpressageInfo> mlist = JsonUtil.getPersons(lists, new TypeToken<List<ExpressageInfo>>() {
						});
						if (mlist != null) {

							if (!isFinishing()) {
								Util.showIntent(context, ExpressageQueryResultActivity.class, new String[] { "list" },
										new Serializable[] { (Serializable) mlist });
							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					LogUtils.e("map.get(\"error_code\")   " + map.get("error_code"));
					if (map.get("error_code").equals("204304"))
						Util.show("查询失败");
					else if (map.get("error_code").equals("204302"))
						Util.show("请填写正确的运单号");
					else if (map.get("error_code").equals("204301"))
						Util.show("未被识别的快递公司");
					else
						Util.show("查询失败，错误码：" + map.get("error_code") + "。错误消息：" + message);
				}
				view.setEnabled(true);
				iv_center.setVisibility(View.GONE);
				AnimeUtil.cancelImageAnimation(iv_center);
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web(
						"http://v.juhe.cn/exp/index?key=df230bfc449b7e4b2013cbfbe5523eae&com=" + com + "&no=" + no);
				return web.getPlan();
			}
		});

		// NewWebAPI.getNewInstance().expressageSearch(com, no,
		// new WebRequestCallBack() {
		// @Override
		// public void success(Object result) {
		// super.success(result);
		//
		// System.out.println(result.toString());
		//
		// Map<String, String> map = JsonUtil
		// .getNewApiJsonQuery(result.toString());
		// String message = map.get("message");
		// if (map.get("code").equals("200")) {
		// String results = map.get("list");
		//
		// try {
		// JSONObject jObject = new JSONObject(results);
		// String lists = jObject.optString("list");
		//
		// List<ExpressageInfo> mlist = JsonUtil
		// .getPersons(
		// lists,
		// new TypeToken<List<ExpressageInfo>>() {
		// });
		// if (mlist != null) {
		//
		// Util.showIntent(
		// context,
		// ExpressageQueryResultActivity.class,
		// new String[] { "list" },
		// new Serializable[] { (Serializable) mlist });
		//
		// }
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// } else {
		// if (!TextUtils.isEmpty(message)) {
		// Util.show(message);
		// }
		// }
		//
		// }
		//
		// @Override
		// public void requestEnd() {
		// // TODO Auto-generated method stub
		// super.requestEnd();
		// view.setEnabled(true);
		// iv_center.setVisibility(View.GONE);
		// }
		// });

	}

}
