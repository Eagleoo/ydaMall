package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.Util;

public class ExceptionFrame extends Activity {

	@ViewInject(R.id.app_exception_listView)
	private ListView listView;
	private int page=1;
	private int size = 15;
	private NewWebAPI api = App.getNewWebAPI();
	private int status=0; // 0可以加载，1不能加载，2加载中
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.exception_frame);
		ViewUtils.inject(this);
		Util.initTop(this, "异常信息", Integer.MIN_VALUE, null);
		Util.showProgress("正在获取异常信息...", this);
		init();
	}
	
	
	private void init(){
		page();
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= listView.getAdapter().getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					synchronized(listView){
						if(0==status){
							status = 1;
							page();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
	
	private void page(){
		final CustomProgressDialog dialog = Util.showProgress("数据加载中...", this);
		status = 2;
		api.showException(page, size, new WebRequestCallBack(){
			@Override
			public void success(Object result) {
				JSONObject obj = JSON.parseObject(result.toString());
				if(200 != obj.getInteger("code").intValue()){
					Util.show(obj.getString("message"), ExceptionFrame.this);
					return ;
				}
				
				JSONArray array = obj.getJSONArray("list");
				JSONObject[] objs = array.toArray(new JSONObject[]{});
				
				BaseMallAdapter<JSONObject> adapter = (BaseMallAdapter<JSONObject>)listView.getAdapter();
				if(null == adapter){
					adapter = new BaseMallAdapter<JSONObject>(R.layout.exception_frame_item,ExceptionFrame.this,objs) {
						@Override
						public View getView(int position, View convertView, ViewGroup parent,
								final JSONObject t) {
							setText(R.id.app_e_borand, t.getString("brand"));
							setText(R.id.app_e_model, t.getString("model"));
							setText(R.id.app_e_ysversion, t.getString("version"));
							setText(R.id.app_e_msg, t.getString("message"));
							convertView.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									Util.alert(ExceptionFrame.this,t.getString("time"),t.getString("localizedMessage"));
								}
							});
							return convertView;
						}
					};
					listView.setAdapter(adapter);
				}else{
					adapter.add(objs);
					adapter.updateUI();
				}
				dialog.cancel();
				dialog.dismiss();
				page++;
			}
			@Override
			public void requestEnd() {
				status = 0;
				dialog.cancel();
				dialog.dismiss();
			}
		});
	}
}
