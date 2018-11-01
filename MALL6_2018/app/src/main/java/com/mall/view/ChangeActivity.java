package com.mall.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mall.model.Product;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.AutoListView.OnLoadListener;
import com.mall.view.AutoListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ChangeActivity extends Activity {

	Button back, advance;
	AutoListView listView;
	List<Product> list = new ArrayList<Product>();
	ListViewAdapter adapter;
	int i = 1;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			List<Product> result = (List<Product>) msg.obj;
			switch (msg.what) {
			case AutoListView.REFRESH:
				listView.onRefreshComplete();
				list.clear();
				list.addAll(result);
				break;
			case AutoListView.LOAD:
				listView.onLoadComplete();
				list.addAll(result);
				break;
			}
			listView.setResultSize(result.size());
			adapter.notifyDataSetChanged();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change);
		findView();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		advance.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ChangeActivity.this,
						StoreMainFrame.class);
				startActivity(intent);
			}
		});
		adapter = new ListViewAdapter(this, list);
		listView.setAdapter(adapter);
		listView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				i = 1;
				loadData(AutoListView.REFRESH, 1);
			}
		});
		listView.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				i += 1;
				loadData(AutoListView.LOAD, i);
			}
		});
		loadData(AutoListView.REFRESH, 1);
	}

	private void loadData(final int what, final int page) {
		Util.asynTask(this, "正在获取商品...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<Product>> map = (HashMap<String, List<Product>>) runData;
				List<Product> result = map.get("list");
				if (null == result || 0 == result.size()) {
					Util.show("没有更多微商商品信息！", ChangeActivity.this);
				}
				Message msg = handler.obtainMessage();
				msg.what = what;
				msg.obj = result;
				handler.sendMessage(msg);
			}

			@Override
			public Serializable run() {
				Web web = new Web(
						Web.yyrgAddress,
						Web.get_RelatedProductList,
						"categoryID_=-1&aOrDesc_=&orderField_=&KeyWord_=&priceFrom_=&priceTo_=&tradeid_=&price_=&PageSize_=10"
								+ "&page_=" + page);
				List<Product> list = web.getList(Product.class);
				HashMap<String, List<Product>> map = new HashMap<String, List<Product>>();
				map.put("list", list);
				return map;
			}
		});
	}

	private void findView() {
		// TODO Auto-generated method stub
		back = (Button) findViewById(R.id.back);
		advance = (Button) findViewById(R.id.advance);
		listView = (AutoListView) findViewById(R.id.listView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
