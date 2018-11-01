package com.mall.serving.query.activity.car;

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

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.adapter.CarResultAdapter;
import com.mall.serving.query.configs.QueryConfigs;
import com.mall.serving.query.model.CarResultInfo;
import com.mall.serving.query.model.CarResultInfo.CarWZInfo;
import com.mall.serving.query.model.WeatherInfo.Data.Weather;
import com.mall.serving.query.model.CarString;
import com.mall.serving.query.model.WeatherInfo;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.view.App;
import com.mall.view.R;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

@ContentView(R.layout.community_find_listview2)
public class CarResultActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	private List list;

	@ViewInject(R.id.listview)
	private ListView listview;

	private CarResultAdapter adapter;

	private CarResultInfo info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		list = new ArrayList();
		setView();

		getIntentData();

	}

	private void setView() {

		top_left.setVisibility(View.VISIBLE);

	}

	private void getIntentData() {

		info = new CarResultInfo();

		Intent intent = getIntent();
		if (intent.hasExtra("info")) {
			info = (CarResultInfo) intent.getSerializableExtra("info");
		}
		top_center.setText(info.getHphm());
		adapter = new CarResultAdapter(context, list);
		listview.setAdapter(adapter);
		AnimeUtil.setAnimationEmptyView(context, listview, true);
		if (intent.hasExtra("from")) {
			top_right.setText("刷新");
			top_right.setVisibility(View.VISIBLE);
			List<CarWZInfo> lists = info.getLists();
			if (lists != null) {
				list.addAll(lists);
			}

			adapter.notifyDataSetChanged();

		} else {
			carResultQuery(info);

		}

		// carResultQuery(city_code, "川A0RW50", hpzl, "371654", "060453");

	}

	private void carResultQuery(final CarResultInfo info2) {
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String lists = map.get("list");
				CarResultInfo info = JsonUtil.getPerson(lists, CarResultInfo.class);

				if (info != null) {
					List<CarWZInfo> mlist = info.getLists();
					list.clear();
					list.addAll(mlist);
					adapter.notifyDataSetChanged();
					try {
						CarString cityString = DbUtils.create(App.getContext())
								.findFirst(Selector.from(CarString.class).where("key", "=", info.getHphm()));
						if (cityString == null) {
							cityString = new CarString();
							cityString.setCity(lists);
							cityString.setKey(info.getHphm());
							cityString.setMes(info2.getClassno() + "," + info2.getEngineno());
							final CarString cityS = cityString;
							Util.asynTask(new IAsynTask() {

								@Override
								public void updateUI(Serializable runData) {

								}

								@Override
								public Serializable run() {
									try {
										DbUtils.create(App.getContext()).saveBindingId(cityS);
									} catch (DbException e) {
										e.printStackTrace();
									}
									return null;
								}
							});

						} else {
							cityString.setCity(lists);
							cityString.setKey(info.getHphm());
							cityString.setMes(info2.getClassno() + "," + info2.getEngineno());
							DbUtils.create(App.getContext()).update(cityString);
						}

					} catch (DbException e) {
						e.printStackTrace();
					}

					context.sendBroadcast(new Intent(CarMainActivity.TAG));

				} else {
					iv_center.setVisibility(View.GONE);
					AnimeUtil.setNoDataEmptyView("该车辆还没有违章记录...", R.drawable.community_dynamic_empty, context, listview,
							true, null);
				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://v.juhe.cn/wz/query?city=" + info2.getCity() + "&hphm=" + info2.getHphm()
						+ "&engineno=" + info2.getEngineno() + "&key=e602c65dd84079924807d5b248d7e1b4" + "&classno="
						+ info2.getClassno() + "&hpzl=" + info2.getHpzl());
				return web.getPlan();
			}
		});

	}

	@OnClick(R.id.top_right)
	public void click(View v) {

		AnimeUtil.startImageAnimation(iv_center);
		carResultQuery(info);

	}

}
