package com.mall.yyrg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.yyrg.adapter.MyAllRegouAdapter;
import com.mall.yyrg.model.MyHotShopReward;

/**
 * 我的热购记录
 * 
 * @author Administrator
 * 
 */
public class YYRGMyRegouFrame extends Activity {
	@ViewInject(R.id.all_regou)
	private TextView all_regou;
	@ViewInject(R.id.regou_jinxing)
	private TextView regou_jinxing;
	@ViewInject(R.id.regou_jiexiao)
	private TextView regou_jiexiao;
	@ViewInject(R.id.listView1)
	private ListView listView1;
	private int currentPageShop = 0;
	private MyAllRegouAdapter myAllRegouAdapter;
	private List<MyHotShopReward> myHotShopRewards = new ArrayList<MyHotShopReward>();
	private int width;
	private int state = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_my_regoulist);
		ViewUtils.inject(this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		if (UserData.getUser() == null) {
			Intent intent = new Intent(this, LoginFrame.class);
			startActivity(intent);
		} else {
			if (myAllRegouAdapter == null) {
				myAllRegouAdapter = new MyAllRegouAdapter(this, width);
				listView1.setAdapter(myAllRegouAdapter);
			}
			firstpageshop();
			scrollPageshop();
		}
	}
	@OnClick(R.id.top_back)
	public void topback(View view){
		finish();
	}
	@OnClick({ R.id.all_regou, R.id.regou_jinxing, R.id.regou_jiexiao })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.all_regou:
			changeColor(all_regou);
			state = 0;
			myHotShopRewards.clear();
			currentPageShop = 0;
			myAllRegouAdapter = new MyAllRegouAdapter(this, width);
			listView1.setAdapter(myAllRegouAdapter);
			firstpageshop();
			scrollPageshop();
			break;

		case R.id.regou_jinxing:
			state = 1;
			changeColor(regou_jinxing);
			myHotShopRewards.clear();
			currentPageShop = 0;
			myAllRegouAdapter = new MyAllRegouAdapter(this, width);
			listView1.setAdapter(myAllRegouAdapter);
			firstpageshop();
			scrollPageshop();
			break;
		case R.id.regou_jiexiao:
			state = 2;
			changeColor(regou_jiexiao);
			myHotShopRewards.clear();
			currentPageShop = 0;
			myAllRegouAdapter = new MyAllRegouAdapter(this, width);
			listView1.setAdapter(myAllRegouAdapter);
			firstpageshop();
			scrollPageshop();
			break;
		}
	}

	private void changeColor(TextView view) {
		all_regou.setTextColor(getResources().getColor(R.color.yyrg_topcolor));
		regou_jiexiao.setTextColor(getResources().getColor(
				R.color.yyrg_topcolor));
		regou_jinxing.setTextColor(getResources().getColor(
				R.color.yyrg_topcolor));
		view.setTextColor(getResources().getColor(R.color.bg));
		all_regou.setBackgroundColor(getResources().getColor(R.color.bg));
		regou_jiexiao.setBackgroundColor(getResources().getColor(R.color.bg));
		regou_jinxing.setBackgroundColor(getResources().getColor(R.color.bg));
		view.setBackgroundColor(getResources().getColor(R.color.yyrg_topcolor));
	}

	/**
	 * 获得我的所有热购记录
	 */
	private void getMyAllHotShopRecord() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<MyHotShopReward> list = new ArrayList<MyHotShopReward>();
					list = ((HashMap<Integer, List<MyHotShopReward>>) runData)
							.get(index++);
					if (list.size() > 0) {
						List<MyHotShopReward> hotShopRewards = new ArrayList<MyHotShopReward>();
						if (state == 0) {
							myHotShopRewards.addAll(list);
							myAllRegouAdapter.setList(list);
						} else if (state == 1) {
							hotShopRewards.clear();
							for (MyHotShopReward hotShopReward : list) {
								if (hotShopReward.getStatus().equals("1")) {
									hotShopRewards.add(hotShopReward);
								}
							}
							myHotShopRewards.addAll(hotShopRewards);
							myAllRegouAdapter.setList(hotShopRewards);
						} else if (state == 2) {
							hotShopRewards.clear();
							for (MyHotShopReward hotShopReward : list) {
								if (hotShopReward.getStatus().equals("2")
										|| hotShopReward.getStatus()
												.equals("3")) {
									hotShopRewards.add(hotShopReward);
								}

							}
							myHotShopRewards.addAll(hotShopRewards);
							myAllRegouAdapter.setList(hotShopRewards);
						}

					}
				} else {
					Toast.makeText(YYRGMyRegouFrame.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				int typeid = state;
				if(2 == typeid)
					typeid = 3;
				Web web = new Web(Web.yyrgAddress, Web.GetMyAllHotShopRecordByType,
						"userID=" + Util.get(UserData.getUser().getUserId())
								+ "&userPaw=" + UserData.getUser().getMd5Pwd()
								+ "&pagesize=15" + "&pageindex="
								+ (++currentPageShop)+"&typeid="+typeid);
				List<MyHotShopReward> list = web.getList(MyHotShopReward.class);
				HashMap<Integer, List<MyHotShopReward>> map = new HashMap<Integer, List<MyHotShopReward>>();
				map.put(index++, list);
				return map;
			}
		});
	}

	public void firstpageshop() {
		getMyAllHotShopRecord();
	}

	public void scrollPageshop() {
		listView1.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= myAllRegouAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getMyAllHotShopRecord();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	@OnItemClick(R.id.listView1)
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent;
		if (myHotShopRewards.get(arg2).getStatus().equals("1")) {
			intent = new Intent(this, YYRGGoodsMessage.class);
			intent.putExtra("goodsid", myHotShopRewards.get(arg2).getYppid());
			// intent.putExtra("ypid", myHotShopRewards.get(arg2).getYpid());
			startActivity(intent);
		} else if (myHotShopRewards.get(arg2).getStatus().equals("2")
				|| myHotShopRewards.get(arg2).getStatus().equals("3")) {
			intent = new Intent(this, YYRGHistoryGoodsMessage.class);
			intent.putExtra("goodsid", myHotShopRewards.get(arg2).getYppid());
			startActivity(intent);
		}

	}
}
