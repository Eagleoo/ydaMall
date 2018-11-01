package com.mall.yyrg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.BaskSingleAdapter;
import com.mall.yyrg.model.BaskSingle;

public class YYRGBaskSingle extends Activity {
	@ViewInject(R.id.listview)
	private ListView listview;
	private BaskSingleAdapter baskSingleAdapter;
	private int currentPageShop = 0;
	private int width;
	private String yppid;
	private List<BaskSingle> baskSingles=new ArrayList<BaskSingle>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_goods_shaidan);
		ViewUtils.inject(this);
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width=dm.widthPixels;
		yppid=getIntent().getStringExtra("yppid");
		if (TextUtils.isEmpty(yppid)) {
			currentPageShop = 0;
				baskSingleAdapter = new BaskSingleAdapter(this,width);
				listview.setAdapter(baskSingleAdapter);
			firstpageshop();
			scrollPageshop();
		}else {
			currentPageShop = 0;
				baskSingleAdapter = new BaskSingleAdapter(this,width);
				listview.setAdapter(baskSingleAdapter);
			firstpageshop1();
			scrollPageshop1();
		}
		
	}

	/**
	 * 获取所有晒单记录
	 */
	private void getAllBaskSingle() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<BaskSingle> list = new ArrayList<BaskSingle>();
					list = ((HashMap<Integer, List<BaskSingle>>) runData)
							.get(index++);
					if (list!=null&&list.size() > 0) {
						baskSingles=list;
						baskSingleAdapter.setList(list);
					} else {
//						Toast.makeText(YYRGBaskSingle.this, "没有更多的分享信息",
//								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(YYRGBaskSingle.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.getAllBaskSingle,
						"pageNum=" + (++currentPageShop) + "&size=" + 20);
				List<BaskSingle> list = web.getList(BaskSingle.class);
				HashMap<Integer, List<BaskSingle>> map = new HashMap<Integer, List<BaskSingle>>();
				map.put(index++, list);
				return map;
			}

		});
	}

	public void firstpageshop() {
		getAllBaskSingle();
	}

	public void scrollPageshop() {
		listview.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= baskSingleAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getAllBaskSingle();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
	/**
	 * 获取所有晒单记录
	 */
	private void getBaskSingle() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<BaskSingle> list = new ArrayList<BaskSingle>();
					list = ((HashMap<Integer, List<BaskSingle>>) runData)
							.get(index++);
					if (list.size() > 0) {
						baskSingleAdapter.setList(list);
					} else {
						Toast.makeText(YYRGBaskSingle.this, "暂无分享信息",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(YYRGBaskSingle.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.getBaskSingle,
						"pageNum=" + (++currentPageShop) + "&size=" + 20+"&yppid="+yppid);
				List<BaskSingle> list = web.getList(BaskSingle.class);
				HashMap<Integer, List<BaskSingle>> map = new HashMap<Integer, List<BaskSingle>>();
				map.put(index++, list);
				return map;
			}

		});
	}
	@OnClick(R.id.top_back)
	public void onclick(View view){
		finish();
	}
	public void firstpageshop1() {
		getBaskSingle();
	}

	public void scrollPageshop1() {
		listview.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= baskSingleAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getBaskSingle();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

/*	@OnItemClick(R.id.listview)
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg2 > baskSingles.size()){
			return ;
		}
		YYRGUtil.baskSingle=baskSingles.get(arg2);
		Intent intent=new Intent(this, YYRGBaskSingleMessage.class);
		this.startActivity(intent);
	}*/

}
