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
import com.mall.yyrg.adapter.AllRegouRecordleAdapter;
import com.mall.yyrg.model.HotShopRecord;

public class YYRGAllRegouRecord extends Activity {
	@ViewInject(R.id.listView1)
	private ListView listView1;
	private AllRegouRecordleAdapter adapter;
	private int currentPageShop = 0;
	private int width;
	private String yppid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_all_regou);
		ViewUtils.inject(this);
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width=dm.widthPixels;
		yppid=getIntent().getStringExtra("yppid");
		if (TextUtils.isEmpty(yppid)) {
			Toast.makeText(this, "网络连接失败，请稍候再试", Toast.LENGTH_SHORT).show();
		}else {
			if (adapter == null) {
				adapter = new AllRegouRecordleAdapter(this,width);
				listView1.setAdapter(adapter);
			}
			firstpageshop();
			scrollPageshop();
		}
		
	}
	@OnClick(R.id.top_back)
	public void onclick(View view){
		finish();
	}
	/**
	 * 获取所有晒单记录
	 */
	private void getAllHotShopRecord() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<HotShopRecord> list = new ArrayList<HotShopRecord>();
					list = ((HashMap<Integer, List<HotShopRecord>>) runData)
							.get(index++);
					if (list.size() > 0) {
						adapter.setList(list);
					} 

				} else {
					Toast.makeText(YYRGAllRegouRecord.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.getNewHotShopRecord,
						"pageNum=" + (++currentPageShop) + "&size=" + 20+"&yppid="+yppid);
				List<HotShopRecord> list = web.getList(HotShopRecord.class);
				HashMap<Integer, List<HotShopRecord>> map = new HashMap<Integer, List<HotShopRecord>>();
				map.put(index++, list);
				return map;
			}

		});
	}

	public void firstpageshop() {
		getAllHotShopRecord();
	}

	public void scrollPageshop() {
		listView1.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getAllHotShopRecord();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
}
