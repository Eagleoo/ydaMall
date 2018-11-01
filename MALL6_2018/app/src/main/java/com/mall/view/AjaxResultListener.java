package com.mall.view;

import java.io.InputStream;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.AjaxResult;
import com.mall.net.ListHandle;
import com.mall.net.Web;
import com.mall.util.Util;

/**
 * 
 * 功能： 手机防网页的自动匹配提示事件<br>
 * 时间： 2013-12-27<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class AjaxResultListener implements TextWatcher {

	private static HashMap<String, List<AjaxResult>> cache = new HashMap<String, List<AjaxResult>>();
	private RequestAjaxThread thread = null;
	private View line;
	private EditText serach;
	private ListView listView;
	private AjaxResultAdapter adapter;

	public AjaxResultListener(Activity context, EditText serach, int lineId,
			int listId) {
		super();
		line =  context.findViewById(lineId);
		this.serach = serach;
		listView = (ListView) context.findViewById(listId);
		LogUtils.e("-------------------------------------------------");
		listView.setAdapter(adapter = new AjaxResultAdapter(line,listView, context));
		thread = new RequestAjaxThread(line, listView, serach, cache);
		thread.start();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		synchronized (this) {
			String value = s.toString();
			if (Util.isNull(value) || "".equals(value.trim())) {
				line.setVisibility(LinearLayout.GONE);
				listView.setVisibility(ListView.GONE);
				adapter.clear();
				return;
			}
			if("noAjax".equals(serach.getTag()+""))
				return ;
		}
		String value = s.toString();
		if (Util.isNull(value) || "".equals(value.trim())) {
			line.setVisibility(LinearLayout.GONE);
			listView.setVisibility(ListView.GONE);
			return;
		}
		if (cache.containsKey(value)) {
			List<AjaxResult> data = cache.get(value);
			adapter.clear();
			adapter.addData(data);
			adapter.updateUI();
			View v = adapter.getView(0, null, listView);
			if(null == v) return ;
			v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int itemHeight = v.getMeasuredHeight();
			int _height = itemHeight * (data.size() + 1);
			line.getLayoutParams().height = _height;
			line.setVisibility(LinearLayout.VISIBLE);
			listView.setVisibility(ListView.VISIBLE);
		} else {
			synchronized (thread) {
				if (thread.getState() == State.WAITING
						|| thread.getState() == State.TIMED_WAITING)
					thread.notify();
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}
}

class RequestAjaxThread extends Thread {
	private static final int SUCCESS = 0;
	private View line;
	private ListView listView;
	private AjaxResultAdapter adapter;
	private EditText input;
	private HashMap<String, List<AjaxResult>> cache;
	private int itemHeight = -1;
	private Handler hanlder = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (SUCCESS == msg.what) {
				List<AjaxResult> list = (List<AjaxResult>) msg.obj;
				adapter.clear();
				adapter.addData(list);
				adapter.updateUI();
				cache.put(input.getText().toString(), list);
				if (0 == list.size()) {
					line.setVisibility(LinearLayout.GONE);
					listView.setVisibility(ListView.GONE);
				} else {
					if(input.getText().toString().length() >1){
						line.setVisibility(LinearLayout.VISIBLE);
						listView.setVisibility(ListView.VISIBLE);
	
						View v = adapter.getView(0, null, listView);
						v.measure(MeasureSpec.makeMeasureSpec(0,
								MeasureSpec.UNSPECIFIED), MeasureSpec
								.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
						itemHeight = v.getMeasuredHeight();
						int _height = itemHeight * (list.size() + 1);
						line.getLayoutParams().height = _height;
					}
				}
			}
			super.handleMessage(msg);
		}
	};

	public RequestAjaxThread(View line, ListView listView,
			EditText input, HashMap<String, List<AjaxResult>> cache) {
		super();
		this.line = line;
		this.listView = listView;
		this.input = input;
		this.cache = cache;
		adapter = (AjaxResultAdapter) listView.getAdapter();
	}

	public void run() {
		while (true) {
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			HttpUtils http = new HttpUtils(10000);
			String value = input.getText().toString();
			try {
				ResponseStream stream = http.sendSync(HttpMethod.GET,
						"http://"+Web.webServer+"Handler/ProductSearch.aspx?r="
								+ System.currentTimeMillis() + value.hashCode()
								+ "&name=" + Util.get(value));
				LogUtils.d("请求网络：http://"+Web.webServer+"Handler/ProductSearch.aspx?r="
								+ System.currentTimeMillis() + value.hashCode()
								+ "&name=" + Util.get(value));
				InputStream in = stream.getBaseStream();
				List<Object> list = new ListHandle(AjaxResult.class, "obj", in)
						.getList();
				List<AjaxResult> data = new ArrayList<AjaxResult>();
				for (Object obj : list)
					data.add((AjaxResult) obj);
				list.clear();
				LogUtils.d("消息内容："+data.size());
				Message message = new Message();
				message.what = SUCCESS;
				message.obj = data;
				hanlder.sendMessage(message);
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}
	}
}

class AjaxResultAdapter extends BaseAdapter {
	private View line;
	private ListView listView;
	private Context context;
	public List<AjaxResult> data = new ArrayList<AjaxResult>();
	private LayoutInflater flater;

	public AjaxResultAdapter(View line,ListView listView,Context context) {
		super();
		this.line = line;
		this.listView = listView;
		this.context = context;
		this.flater = LayoutInflater.from(context);
	}

	private class AjaxResultHolder {
		@ViewInject(R.id.ajax_result_list_item_line)
		public LinearLayout line;
		@ViewInject(R.id.ajax_result_list_item_name)
		public TextView name;
		@ViewInject(R.id.ajax_result_list_item_dataCount)
		public TextView count;
	}

	public void addData(List<AjaxResult> data) {
		this.data.addAll(data);
	}

	public void clear() {
		this.data.clear();
	}

	public void updateUI() {
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		synchronized (parent) {
			if (0 == data.size())
				return convertView;
		}
		final AjaxResult model = data.get(position);
		AjaxResultHolder holder = null;
		if (null == convertView) {
			convertView = flater.inflate(R.layout.ajax_result_list_item, null);
			holder = new AjaxResultHolder();
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
		} else
			holder = (AjaxResultHolder) convertView.getTag();
		holder.name.setText(model.getName());
		holder.count.setText("约有" + model.getNum() + "条数据");
		holder.line.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Util.isNull(model.getCate())) {
					line.setVisibility(LinearLayout.GONE);
					listView.setVisibility(ListView.GONE);
					Util.showIntent(context, ProductListFrame.class,
							new String[] { "catId", "catName","ajax" }, new String[] {
									model.getCate(), model.getName(),"ajax" });
				}
			}
		});
		return convertView;
	}
}
