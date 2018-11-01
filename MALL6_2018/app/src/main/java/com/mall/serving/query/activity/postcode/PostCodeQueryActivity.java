package com.mall.serving.query.activity.postcode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.model.CityString;
import com.mall.serving.query.model.IDCardInfo;
import com.mall.serving.query.model.PostcodeCityInfo;
import com.mall.serving.query.model.PostcodeCityInfo.City;
import com.mall.serving.query.model.PostcodeCityInfo.City.District;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.view.App;
import com.mall.view.R;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

@ContentView(R.layout.query_postcode_activity)
public class PostCodeQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;
	@ViewInject(R.id.iv_center)
	private ImageView iv_center;

	@ViewInject(R.id.ll_main)
	private View ll_main;
	@ViewInject(R.id.tl_city2post)
	private View tl_city2post;
	@ViewInject(R.id.ll_post2city)
	private View ll_post2city;

	@ViewInject(R.id.tv_province)
	private TextView tv_province;

	@ViewInject(R.id.tv_city)
	private TextView tv_city;

	@ViewInject(R.id.tv_district)
	private TextView tv_district;
	@ViewInject(R.id.tv_query)
	private TextView tv_query;

	@ViewInject(R.id.et_street)
	private EditText et_street;
	@ViewInject(R.id.et_post)
	private EditText et_post;

	@ViewInject(R.id.rb_rg_1)
	private RadioButton rb_rg_1;
	@ViewInject(R.id.rb_rg_2)
	private RadioButton rb_rg_2;

	private List<PostcodeCityInfo> list1;
	private List<PostcodeCityInfo.City> list2;
	private List<PostcodeCityInfo.City.District> list3;

	private String pid;
	private String cid;
	private String did;

	private String KEY = "PostCode";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		list1 = new ArrayList<PostcodeCityInfo>();
		list2 = new ArrayList<PostcodeCityInfo.City>();
		list3 = new ArrayList<PostcodeCityInfo.City.District>();

		setView();
		isSaveCity();
	}

	private void setView() {
		top_center.setText("邮编查询");
		top_left.setVisibility(View.VISIBLE);

		rb_rg_1.setText("地址-邮编");
		rb_rg_2.setText("邮编-地址");
	}

	@OnRadioGroupCheckedChange(R.id.rg)
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_rg_1:
			ll_main.setBackgroundResource(R.color.white);
			ll_post2city.setVisibility(View.GONE);
			tl_city2post.setVisibility(View.VISIBLE);
			tv_query.setVisibility(View.VISIBLE);
			break;
		case R.id.rb_rg_2:
			ll_main.setBackgroundResource(R.color.main_deep_bg);
			ll_post2city.setVisibility(View.VISIBLE);
			tl_city2post.setVisibility(View.GONE);
			tv_query.setVisibility(View.GONE);
			break;

		}
	}

	@OnClick({ R.id.tv_query, R.id.tv_search, R.id.tv_province, R.id.tv_city, R.id.tv_district })
	public void click(View v) {
		switch (v.getId()) {
		case R.id.tv_query:
			String province = tv_province.getText().toString();
			String city = tv_city.getText().toString();
			String district = tv_district.getText().toString();
			if (TextUtils.isEmpty(province)) {
				Util.show("请选择省份");
				return;
			}
			if (TextUtils.isEmpty(city)) {
				Util.show("请选择城市");
				return;
			}
			if (TextUtils.isEmpty(district)) {
				Util.show("请选择区县");
				return;
			}
			Util.showIntent(context, PostCodeResultQueryActivity.class, new String[] { "pid", "cid", "did", "q" },
					new Serializable[] { pid, cid, did, et_street.getText().toString().trim() });
			break;
		case R.id.tv_search:
			String post = et_post.getText().toString().trim();
			if (TextUtils.isEmpty(post)) {
				Util.show("请输入邮编");
				return;
			}
			Util.showIntent(context, PostCodeResultQueryActivity.class, new String[] { "postcode" },
					new Serializable[] { post });

			break;
		case R.id.tv_province:
			if (list1.size() > 0) {

				Util.showIntentForResult(context, PostCodeCityQueryActivity.class, new String[] { "type", "list" },
						new Serializable[] { 0, (Serializable) list1 }, 100);
			}

			break;
		case R.id.tv_city:
			if (list2.size() > 0) {
				Util.showIntentForResult(context, PostCodeCityQueryActivity.class, new String[] { "type", "list" },
						new Serializable[] { 1, (Serializable) list2 }, 100);
			} else {
				Util.show("请先选择省份");
			}
			break;
		case R.id.tv_district:
			if (list3.size() > 0) {
				Util.showIntentForResult(context, PostCodeCityQueryActivity.class, new String[] { "type", "list" },
						new Serializable[] { 2, (Serializable) list3 }, 100);
			} else {
				Util.show("请先选择城市");
			}
			break;

		}

	}

	private void isSaveCity() {

		// try {
		// final CityString findFirst = DbUtils.create(App.getContext())
		// .findFirst(Selector.from(CityString.class).where("key", "=", KEY));
		//
		// if (findFirst != null) {
		//
		// Util.asynTask(new IAsynTask() {
		//
		// @Override
		// public void updateUI(Serializable runData) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public Serializable run() {
		// List<PostcodeCityInfo> mlist =
		// JsonUtil.getPersons(findFirst.getCity(),
		// new TypeToken<List<PostcodeCityInfo>>() {
		// });
		// list1.clear();
		// list1.addAll(mlist);
		//
		// return null;
		// }
		// });
		//
		// } else {

		postCodeCityQuery();

		// }

		// } catch (DbException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	private void postCodeCityQuery() {
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");

				String lists = map.get("list");
				if (TextUtils.isEmpty(lists)) {
					return;
				}
				List<PostcodeCityInfo> mlist = JsonUtil.getPersons(lists, new TypeToken<List<PostcodeCityInfo>>() {
				});
				if (mlist != null) {

					list1 = mlist;

					try {
						long count = DbUtils.create(App.getContext())
								.count(Selector.from(CityString.class).where("key", "=", "PostCode"));
						if (count < 1) {
							final CityString cityString = new CityString();
							cityString.setCity(lists);
							cityString.setKey(KEY);

							Util.asynTask(new IAsynTask() {

								@Override
								public void updateUI(Serializable runData) {
									// TODO Auto-generated method stub

								}

								@Override
								public Serializable run() {
									try {
										DbUtils.create(App.getContext()).saveBindingId(cityString);
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return null;
								}
							});

						}

					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Util.show(message);
				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://v.juhe.cn/postcode/pcd?key=3287cd51e91a12cba3da80bd32704041");
				return web.getPlan();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("onActivityResult");
		if (data != null && resultCode == 100) {

			int type = 0;
			String text = "";
			String id = "";

			int position = 0;
			if (data.hasExtra("type")) {
				type = data.getIntExtra("type", 0);

			}
			if (data.hasExtra("position")) {
				position = data.getIntExtra("position", 0);
			}
			if (data.hasExtra("text")) {
				text = data.getStringExtra("text");
			}
			if (data.hasExtra("id")) {
				id = data.getStringExtra("id");
			}

			switch (type) {
			case 0:
				tv_province.setText(text);
				pid = id;
				System.out.println("position" + position);
				List<City> city2 = list1.get(position).getCity();
				list2.clear();
				list2.addAll(city2);
				if (list2.size() > 0) {
					City city = list2.get(0);
					tv_city.setText(city.getCity());
					cid = city.getId();
					list3.clear();
					list3.addAll(city.getDistrict());
					if (city.getDistrict().size() > 0) {
						District district = city.getDistrict().get(0);
						tv_district.setText(district.getDistrict());
						did = district.getId();

					}
				}

				break;
			case 1:
				tv_city.setText(text);
				cid = id;
				list3.clear();
				list3.addAll(list2.get(position).getDistrict());
				if (list3.size() > 0) {
					District district = list3.get(0);
					tv_district.setText(district.getDistrict());
					did = district.getId();
				}
				break;
			case 2:
				tv_district.setText(text);
				did = id;

				break;

			}

		}

	}

}
