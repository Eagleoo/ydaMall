package com.mall.serving.query.activity.flight;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListView;
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
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.adapter.FlightCityAdapter;
import com.mall.serving.query.model.FlightCityInfo;
import com.mall.serving.voip.util.ToPinYin;
import com.mall.serving.voip.view.alphabeticbar.QuickAlphabeticBar;
import com.mall.view.App;
import com.mall.view.R;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ContentView(R.layout.query_trainticket_city_activity)
public class FlightCityQueryActivity extends BaseActivity {
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;

	@ViewInject(R.id.et_city)
	private TextView et_city;
	@ViewInject(R.id.rb_rg_1)
	private RadioButton rb_rg_1;
	@ViewInject(R.id.rb_rg_2)
	private RadioButton rb_rg_2;
	@ViewInject(R.id.tv_search)
	private TextView tv_search;
	@ViewInject(R.id.lv_voip_contact)
	private ListView listview;
	@ViewInject(R.id.indexScrollerBar)
	private QuickAlphabeticBar mQuickAlphabeticBar;

	private FlightCityAdapter adapter;

	private List list;

	private List listAll;

	private int rgIndex;
	boolean isEnd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();
		list = new ArrayList();
		listAll = new ArrayList();
		mQuickAlphabeticBar.init(context);
		mQuickAlphabeticBar.setListView(listview);
		mQuickAlphabeticBar.setHight(mQuickAlphabeticBar.getHeight());
		mQuickAlphabeticBar.setVisibility(View.VISIBLE);
		setListener();

		try {

			long count = DbUtils.create(App.getContext()).count(Selector.from(FlightCityInfo.class));
			if (count <= 0) {
				trainTicketCityQuery();
			} else {
				List<FlightCityInfo> mmlist = DbUtils.create(App.getContext())
						.findAll(Selector.from(FlightCityInfo.class).where("type", "=", 0).orderBy("pinyin", false));
				if (mmlist != null) {

					listAll.addAll(mmlist);

				}
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AnimeUtil.setAnimationEmptyView(context, listview, true);
		list.clear();
		Set<FlightCityInfo> setData = new HashSet<FlightCityInfo>();
		setData.addAll(listAll);
		list.addAll(listAll);
		listAll.clear();
		listAll.addAll(setData);
		adapter = new FlightCityAdapter(context, list, mQuickAlphabeticBar);
		listview.setAdapter(adapter);

	}

	private void setView() {

		top_center.setText("选择机场");
		top_left.setVisibility(View.VISIBLE);
		rb_rg_1.setText("机场列表");
		rb_rg_2.setText("最近常用");
		et_city.setHint("城市/机场");
	}

	@OnClick({ R.id.tv_search })
	public void click(View v) {

		String city = et_city.getText().toString().trim().toLowerCase();
		if (TextUtils.isEmpty(city)) {
			Util.show("请输入城市或机场");
		} else {
			rb_rg_1.setChecked(true);
			if (listAll.size() <= 0) {
				try {
					List mlist = DbUtils.create(App.getContext()).findAll(Selector.from(FlightCityInfo.class)
							.where("type", "=", 0).and("spell", "<>", "").orderBy("pinyin", false));
					if (mlist != null) {
						listAll.clear();
						listAll.addAll(mlist);
					}
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			list.clear();
			for (int i = 0; i < listAll.size(); i++) {
				FlightCityInfo info = (FlightCityInfo) listAll.get(i);

				if (info.getCity().contains(city) || info.getSpell().toLowerCase().contains(city)) {
					list.add(info);

				}

			}
			adapter = new FlightCityAdapter(context, list, mQuickAlphabeticBar);
			listview.setAdapter(adapter);

		}

	}

	private void setListener() {
		ViewTreeObserver vto = mQuickAlphabeticBar.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				mQuickAlphabeticBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mQuickAlphabeticBar.setHight(mQuickAlphabeticBar.getHeight());
			}
		});

	}

	@OnRadioGroupCheckedChange(R.id.rg)
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		AnimeUtil.setAnimationEmptyView(context, listview, true);
		try {

			List<FlightCityInfo> mlist = new ArrayList<FlightCityInfo>();
			switch (checkedId) {
			case R.id.rb_rg_2:
				rgIndex = 1;
				mlist = DbUtils.create(App.getContext()).findAll(Selector.from(FlightCityInfo.class)
						.where("type", "=", 1).and("spell", "<>", "").orderBy("pinyin", false));

				break;
			case R.id.rb_rg_1:
				rgIndex = 0;
				mlist.addAll(listAll);
				break;

			}
			if (mlist != null) {
				list.clear();
				list.addAll(mlist);
				adapter = new FlightCityAdapter(context, list, mQuickAlphabeticBar);
				listview.setAdapter(adapter);
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (rgIndex != 0 || isEnd) {
			AnimeUtil.setNoDataEmptyView("列表为空", R.drawable.community_dynamic_empty, context, listview, true, null);
		}

	}

	private void trainTicketCityQuery() {
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				isEnd = true;
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");

				String lists = map.get("list");
				if (TextUtils.isEmpty(lists)) {
					return;
				}
				final List<FlightCityInfo> mlist = JsonUtil.getPersons(lists, new TypeToken<List<FlightCityInfo>>() {
				});
				if (mlist != null) {
					listAll.addAll(mlist);
					if (rgIndex == 0) {
						list.clear();
						list.addAll(listAll);
						adapter = new FlightCityAdapter(context, list, mQuickAlphabeticBar);
						listview.setAdapter(adapter);
					}
					new  Thread() {

						public void run() {
							try {
								for (FlightCityInfo info : mlist) {
									String fullSpell = ToPinYin.getPinYin(info.getCity());
									info.setPinyin(fullSpell);
								}

							} catch (BadHanyuPinyinOutputFormatCombination e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							try {

								DbUtils.create(App.getContext()).dropTable(FlightCityInfo.class);
								DbUtils.create(App.getContext()).saveAll(mlist);

							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						};
					}.start();

				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web("http://apis.juhe.cn/plan/city?key=a8a3e8029ed043f166ecfbc35f36bfdb");
				return web.getPlan();
			}
		});
	}
}
