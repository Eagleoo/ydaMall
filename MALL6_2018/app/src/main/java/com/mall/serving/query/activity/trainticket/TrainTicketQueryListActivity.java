package com.mall.serving.query.activity.trainticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.GetLastDateTime;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.adapter.TrainTicketListAdapter;
import com.mall.serving.query.model.TrainTicketInfo;
import com.mall.view.R;

@ContentView(R.layout.query_trainticket_list_activity)
public class TrainTicketQueryListActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;
	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	@ViewInject(R.id.tv_date)
	private TextView tv_date;

	@ViewInject(R.id.listview)
	private ListView listview;
	@ViewInject(R.id.ll_rg)
	private View ll_rg;
	@ViewInject(R.id.rb_rg_1)
	private RadioButton rb_rg_1;
	@ViewInject(R.id.rb_rg_2)
	private RadioButton rb_rg_2;
	@ViewInject(R.id.rg_most)
	private RadioGroup rg_most;

	@SuppressWarnings("rawtypes")
	private List list;
	@SuppressWarnings("rawtypes")
	private List list1;
	@SuppressWarnings("rawtypes")
	private List list2;
	private TrainTicketListAdapter adapter;
	private int rbIndex;
	private String from = "";
	private String to = "";
	private String date = "";
	private String date2 = "";
	private String tt = "";

	private int lastRb;
	private int nextRb;

	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		list = new ArrayList();
		list1 = new ArrayList();
		list2 = new ArrayList();
		getIntentData();
		setView();
		adapter = new TrainTicketListAdapter(context, list);
		listview.setAdapter(adapter);
		AnimeUtil.setAnimationEmptyView(context, listview, true);
	}

	private void setView() {
		rb_rg_1.setText("出发");
		rb_rg_2.setText("返回");

		top_center.setText(from + "-" + to);
		top_left.setVisibility(View.VISIBLE);
		setTvData(date);

	}

	private void setTvData(String date) {
		String dateS = Util.formatDateTime("yyyy-MM-dd", "yyyy-MM-dd E", date);
		tv_date.setText(dateS);

	}

	private void getIntentData() {

		Intent intent = getIntent();
		if (intent.hasExtra("from")) {
			from = intent.getStringExtra("from");

		}
		if (intent.hasExtra("to")) {
			to = intent.getStringExtra("to");
		}
		if (intent.hasExtra("date")) {
			date = intent.getStringExtra("date");
		}
		if (intent.hasExtra("date2")) {
			date2 = intent.getStringExtra("date2");
		}
		if (intent.hasExtra("tt")) {
			tt = intent.getStringExtra("tt");
		}
		System.out.println(from + to + date + date2 + tt);
		trainTicketQuery(from, to, date, tt, false, false);
		if (TextUtils.isEmpty(date2)) {
			ll_rg.setVisibility(View.GONE);
		} else {

			ll_rg.setVisibility(View.VISIBLE);
		}

	}

	@OnClick({ R.id.tv_down, R.id.tv_up })
	public void click(final View v) {

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

				switch (v.getId()) {
				case R.id.tv_down:
					getNextQueryList(-1);

					break;
				case R.id.tv_up:
					getNextQueryList(1);
					break;

				}

			}
		});
	}

	private void getNextQueryList(int next) {

		String dateS = "";
		boolean isindex = isIndex();
		if (rbIndex == 0) {
			date = GetLastDateTime.getInstance().getDayByDate(GetLastDateTime.getInstance().getDate(date), next);
			dateS = date;
			trainTicketQuery(from, to, dateS, tt, isindex, true);
		} else {
			date2 = GetLastDateTime.getInstance().getDayByDate(GetLastDateTime.getInstance().getDate(date2), next);
			dateS = date2;
			trainTicketQuery(to, from, dateS, tt, isindex, true);
		}

		setTvData(dateS);

	}

	private boolean isIndex() {
		if (rbIndex == 0) {
			return false;
		} else {
			return true;
		}

	}

	@SuppressWarnings("unchecked")
	private void sortList(final int type) {

		Collections.sort(list, new Comparator<TrainTicketInfo>() {

			@Override
			public int compare(TrainTicketInfo info1, TrainTicketInfo info2) {

				int compare = 0;
				switch (type) {
				case 0:
					compare = info1.getStart_time().compareToIgnoreCase(info2.getStart_time());
					break;
				case 1:
					compare = info1.getRun_time().compareToIgnoreCase(info2.getRun_time());
					break;
				case 2:
					compare = info1.getEnd_time().compareToIgnoreCase(info2.getEnd_time());
					break;

				}
				return compare;
			}

		});

		adapter.notifyDataSetChanged();

	}

	@SuppressWarnings("unchecked")
	@OnRadioGroupCheckedChange(R.id.rg)
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_rg_1:
			rbIndex = 0;
			if (list1.size() == 0) {
				trainTicketQuery(from, to, date, tt, false, true);
			} else {
				list.clear();
				list.addAll(list1);
				adapter.notifyDataSetChanged();
			}
			setTvData(date);
			top_center.setText(from + "-" + to);
			break;
		case R.id.rb_rg_2:
			rbIndex = 1;
			if (list2.size() == 0) {
				trainTicketQuery(to, from, date2, tt, true, true);
			} else {
				list.clear();
				list.addAll(list2);
				adapter.notifyDataSetChanged();
			}

			setTvData(date2);
			top_center.setText(to + "-" + from);
			break;

		}
		int at = 0;
		if (rbIndex == 0) {
			at = lastRb;
		} else {
			at = nextRb;
		}
		RadioButton childAt = (RadioButton) rg_most.getChildAt(at);
		childAt.setChecked(true);
	}

	@OnRadioGroupCheckedChange(R.id.rg_most)
	public void onCheckedChanged2(RadioGroup group, int checkedId) {
		int type = 0;
		switch (checkedId) {
		case R.id.rb_most_from:
			type = 0;
			break;
		case R.id.rb_most_time:
			type = 1;
			break;
		case R.id.rb_most_to:
			type = 2;
			break;

		}

		sortList(type);
		if (rbIndex == 0) {
			lastRb = type;
		} else {
			nextRb = type;
		}
	}

	private void trainTicketQuery(final String from, final String to, final String date, String tt,
			final boolean isIndex, boolean isiv) {
		if (isiv) {
			AnimeUtil.startImageAnimation(iv_center);
		}
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(runData.toString());
					String message = jsonObject.optString("reason");
					if (jsonObject.has("error_code")) {
						int error_code = jsonObject.optInt("error_code");
						if (error_code > 0) {
							if (Util.isNull(message))
								Util.show("查询失败，请稍后重试");
							else if (message.equals("Empty")) {
								Util.show("查询失败，请检查关键字");
							} else if (error_code == 10014) {
								// Util.show("车票系统异常，请重试！");
							} else {
								Util.show(message);
							}
						}
					}
					String lists = jsonObject.optJSONObject("result").optString("list");

					if (isIndex) {
						list2.clear();
					} else {
						list1.clear();

					}
					if (TextUtils.isEmpty(lists)) {
						if (isIndex == isIndex()) {
							list.clear();
							adapter.notifyDataSetChanged();
						}
						return;
					}
					List<TrainTicketInfo> mlist = JsonUtil.getPersons(lists, new TypeToken<List<TrainTicketInfo>>() {
					});
					if (null != mlist && mlist.size() > 0) {
						if (isIndex) {
							list2.addAll(mlist);
						} else {
							list1.addAll(mlist);
						}

						if (isIndex == isIndex()) {
							list.clear();
							list.addAll(mlist);
							adapter.notifyDataSetChanged();
							iv_center.setVisibility(View.GONE);
						}

					} else {
						AnimeUtil.setNoDataEmptyView("未查询到信息...", R.drawable.community_dynamic_empty, context, listview,
								true, null);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://apis.juhe.cn/train/s2swithprice?start=" + from + "&end=" + to + "&date="
						+ date + "&key=f06bceda8f535f42473b6f95642d4e5e");
				return web.getPlan();
			}
		});

	}

}
