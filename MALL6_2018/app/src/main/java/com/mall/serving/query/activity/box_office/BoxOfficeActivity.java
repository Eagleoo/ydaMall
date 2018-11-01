package com.mall.serving.query.activity.box_office;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.picviewpager.ViewPagerAdapter;
import com.mall.serving.query.adapter.BoxOfficeAdapter;
import com.mall.serving.query.model.BoxOfficeInfo;
import com.mall.serving.query.model.FlightInfo;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.view.R;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

@ContentView(R.layout.query_box_office_activity)
public class BoxOfficeActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;

	@ViewInject(R.id.rg)
	private RadioGroup rg;
	@ViewInject(R.id.pager)
	private ViewPager pager;
	private ViewPagerAdapter pagerAdapter;

	public String[] offices = { "CN", "US", "HK" };
	private List<View> vList;
	private List<View> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		ViewUtils.inject(this);
		vList = new ArrayList<View>();
		list = new ArrayList<View>();
		setView();
		initView();
	}

	private void setView() {

		top_left.setVisibility(View.VISIBLE);

		top_center.setText("电影票房");

	}

	private void getListData() {

		for (int i = 0; i < offices.length; i++) {
			FrameLayout fl = new FrameLayout(context);
			ListView lv = new ListView(context);
			lv.setDividerHeight(1);
			lv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			fl.addView(lv);
			AnimeUtil.setAnimationEmptyView(context, lv, true);
			vList.add(lv);
			list.add(fl);
		}

	}

	private void initView() {
		getListData();
		pagerAdapter = new ViewPagerAdapter(list);
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int p) {
				RadioButton childAt = (RadioButton) rg.getChildAt(p);
				childAt.setChecked(true);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup rg, int rid) {
				int num = 0;
				switch (rid) {
				case R.id.rb_1:
					num = 0;
					break;
				case R.id.rb_2:
					num = 1;
					break;
				case R.id.rb_3:
					num = 2;
					break;

				}

				pager.setCurrentItem(num);

			}
		});

		for (int i = 0; i < offices.length; i++) {
			box_officeQuery(offices[i], vList.get(i));
		}

	}

	private void box_officeQuery(final String area, final View view) {
		final ListView lv = (ListView) view;
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");
				if (map.get("code").equals("200")) {
					String lists = map.get("list");

					List<BoxOfficeInfo> mlist = JsonUtil.getPersons(lists, new TypeToken<List<BoxOfficeInfo>>() {
					});
					BoxOfficeAdapter adapter = new BoxOfficeAdapter(context, mlist);
					lv.setAdapter(adapter);
				} else {
					AnimeUtil.setNoDataEmptyView("列表为空...", R.drawable.community_dynamic_empty, context, lv, true,
							null);
				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web(
						"http://v.juhe.cn/boxoffice/rank.php?&area=" + area + "&key=4df242ac5d8d1e143cea0e364c807d63");
				return web.getPlan();
			}
		});
	}

}
