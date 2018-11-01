package com.mall.serving.query.activity.travel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshGridView;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshListView;
import com.mall.serving.query.adapter.TravelAdapter;
import com.mall.serving.query.model.TravelInfo;
import com.mall.serving.query.model.TravelInfo.Hotel;
import com.mall.serving.query.model.TravelInfo.Scenery;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.view.R;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

@ContentView(R.layout.query_cookbook_search_activity)
public class TravelMainActivity extends BaseActivity {
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

//	@ViewInject(R.id.listview)
	private GridView listview;
	@ViewInject(R.id.refreshGridView)
	private PullToRefreshGridView refreshGridView;
	@ViewInject(R.id.refreshListView)
	private PullToRefreshListView refreshListView;
	private List list;

	private TravelAdapter adapter;
	private boolean isHotel = false;
	private int skip;
	
	private List tempList;
	
	private String cityId="";

	private String title="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		refreshGridView.setVisibility(View.VISIBLE);
		refreshListView.setVisibility(View.GONE);
		listview = refreshGridView.getRefreshableView();
		listview.setNumColumns(2);
		refreshGridView.setPullLoadEnabled(true);
		refreshGridView.setPullRefreshEnabled(false);
		top.setVisibility(View.GONE);
		ll.setVisibility(View.VISIBLE);
		setListener();
		list = new ArrayList();
		tempList=new ArrayList();
		
		AnimeUtil.setAnimationEmptyView(context, listview, true);
		getIntentData();
	}

	private void getIntentData() {

		Intent intent = getIntent();
		if (intent.hasExtra("info")) {
			isHotel = true;
			Scenery info = (Scenery) intent.getSerializableExtra("info");
			tv_hint.setText("请输入目的地、酒店名");
			cityId=info.getCityId();
			getTravelData(info.getCityId());
		} else {
			tv_hint.setText("请输入目的地、旅游景点");

			getTravelData("");
		}

		adapter = new TravelAdapter(context, list, isHotel);
		listview.setAdapter(adapter);

	}

	private void setListener() {
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {

				String str = paramCharSequence.toString();
				if (TextUtils.isEmpty(str)) {
					tv_hint.setVisibility(View.VISIBLE);
				} else {
					tv_hint.setVisibility(View.GONE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence paramCharSequence,
					int paramInt1, int paramInt2, int paramInt3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable paramEditable) {
				// TODO Auto-generated method stub

			}
		});
		
		
		OnRefreshListener refreshListener = new OnRefreshListener() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				  if (listview.getTag()!=null) {
					listview.setTag(null);
					for (int i = tempList.size()/2; i < tempList.size(); i++) {
						
						list.add(tempList.get(i));
						
					}
					
					adapter.notifyDataSetChanged();
					refreshGridView.onPullUpRefreshComplete();
					refreshGridView.setHasMoreData(true);
					System.out.println("tag");
				}else {
					skip+=50;
					getTravelData(cityId);
					System.out.println("null");
				}
				
			}
		};
		refreshGridView.setOnRefreshListener(refreshListener);

	}

	@OnClick({ R.id.iv_search })
	public void click(View v) {

		AnimeUtil.startAnimation(context, v, R.anim.small_2_big,
				new OnAnimEnd() {

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
						title= et_search.getText().toString().trim();
						if (TextUtils.isEmpty(title)) {
							Util.show("请输入目的地、旅游景点");
							return;
						}
						list.clear();
						tempList.clear();
						getTravelData("");

					}
				});

	}

	private void getTravelData(String cityId) {
		Parameters params = new Parameters();
		params.add("title", title);
		params.add("v", "1");
		params.add("pname", "com.mall.view");
		params.add("skip", skip);

		
		String url = "";
		if (isHotel) {
			params.add("cityId", cityId);
			url = "http://web.juhe.cn:8080/travel/hotel/hotelList.json";
		} else {
			url = "http://web.juhe.cn:8080/travel/scenery/sceneryList.json";
		}
		JuheWeb.getJuheData(params, 57, url, JuheData.GET,
				new JuheRequestCallBack() {

					@Override
					public void success(int err, String reason, String result) {
						Map<String, String> map = JsonUtil
								.getNewApiJsonQuery(result.toString());
						String message = map.get("message");

						String lists = map.get("list");

						TravelInfo info = JsonUtil.getPerson(lists,
								TravelInfo.class);

						if (info != null) {
							if (isHotel) {
								List<Hotel> hotelList = info.getHotelList();
								if (hotelList != null) {
									tempList=hotelList;
									

								}
							} else {
								List<Scenery> sceneryList = info
										.getSceneryList();
								if (sceneryList != null) {
									tempList=sceneryList;
								}
							}

						}
						
						for (int i = 0; i < tempList.size()/2; i++) {
							
							list.add(tempList.get(i));
						}
						listview.setTag("half");
						
						adapter.notifyDataSetChanged();
						refreshGridView.setHasMoreData(true);
					}

					@Override
					public void requestEnd() {
						refreshGridView.onPullDownRefreshComplete();
						refreshGridView.onPullUpRefreshComplete();
						
						AnimeUtil.setNoDataEmptyView("列表为空...",
								R.drawable.community_dynamic_empty, context,
								listview, true, null);

					}

					@Override
					public void fail(int err, String reason, String result) {
						// TODO Auto-generated method stub

					}
				});

	}

}
