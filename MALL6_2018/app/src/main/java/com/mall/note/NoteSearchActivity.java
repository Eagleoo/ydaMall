package com.mall.note;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.model.NoteModel;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

@ContentView(R.layout.note_search_activity)
public class NoteSearchActivity extends Activity{
	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.norecord)
	private TextView norecord;
	@ViewInject(R.id.search)
	private EditText search;
	@ViewInject(R.id.listview)
	private ListView listView;
	private NoteAdapter adapter;
	private Context context;
	
	String userId="";
	String md5Pwd="";
	int currentPage;
	
	
	private List<NoteModel> models = new ArrayList<NoteModel>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		context=this;
		setView();
		adapter = new NoteAdapter(context);
		listView.setAdapter(adapter);
		setListener();
		scrollPage();
		asyncloadData();
	}
	
	
	
	private void setView() {
		top_center.setText("搜索");
		top_left.setVisibility(View.VISIBLE);

	}
	
	
	private void setListener() {
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, int start,
					int before, int count) {
				String trim = search.getText().toString().trim();
				if (TextUtils.isEmpty(trim)) {
					adapter.clear();
					adapter.setList(models);
				}else {
//					List<NoteModel> noteModels = new ArrayList<NoteModel>();
//					for (int i = 0; i < models.size(); i++) {
//						
//						NoteModel info = models.get(i);
//						if (info.getTitle()
//								.contains(trim)||info.getContent().contains(trim)||info.getPublishTime().contains(trim)) {
//							noteModels.add(models.get(i));
//						}
//					}
//					adapter.clear();
//					adapter.setList(noteModels);
					
					searchImportantNotes(trim);
				}
				
			
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				

			}
		});

	}
	
	public void scrollPage() {
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (TextUtils.isEmpty(search.getText().toString().trim())) {
						asyncloadData();
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
	
	public void asyncloadData() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); // 获取东八区时间
		int year = c.get(Calendar.YEAR); // 获取年
		int month = c.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
		String mon = "";
		if (month < 10) {
			mon = "0" + month;
		} else {
			mon = month + "";
		}
		int day = c.get(Calendar.DAY_OF_MONTH); // 获取当前天数
		final String time = year + "-" + mon + "-" + day;
		User user = UserData.getUser();
		
		if (user!=null) {
			userId=user.getUserId();
			md5Pwd=user.getMd5Pwd();
			
		}
		
		final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "正在获取记事信息...");
		Map<String,String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("md5Pwd", md5Pwd);
		params.put("yearMonthDay", "");
		params.put("page", (++currentPage)+"");
		params.put("size", "50");
		NewWebAPI.getNewInstance().getWebRequest("/Note.aspx?call=getImportantNotes",params, new WebRequestCallBack(){
			@Override
			public void success(Object result) {
				super.success(result);
				if(Util.isNull(result)){
					Util.show("网络异常！", NoteSearchActivity.this);
					return ;
				}
				JSONObject json = JSON.parseObject(result.toString());
				if(200 != json.getIntValue("code")){
					Util.show(json.getString("message"), NoteSearchActivity.this);
					return ;
				}
				List<NoteModel> list = JSON.parseArray(json.getString("list"), NoteModel.class);
				if (list.size() != 0) {
					adapter.setList(list);
					models.addAll(list);
				}
			}

			@Override
			public void requestEnd() {
				super.requestEnd();
				cpd.cancel();
				cpd.dismiss();
			}
			
		});

//		Util.asynTask(context, "正在获取记事信息", new IAsynTask() {
//
//			@Override
//			public void updateUI(Serializable runData) {
//				int index = 0;
//				HashMap<Integer, List<NoteModel>> map = (HashMap<Integer, List<NoteModel>>) runData;
//				List<NoteModel> list = map.get(index++);
//				
//				if (list.size() != 0) {
//					adapter.setList(list);
//					models.addAll(list);
//				} else {
//
//				}
//			}
//
//			@Override
//			public Serializable run() {
//				int index = 0;
//				
//				
//				Web web = new Web(Web.allianService, Web.getImportantNotes,
//						"userId=" + Util.get(userId) + "&md5Pwd=" + md5Pwd
//								+ "&yearMonthDay=" + "&page=" + (++currentPage)
//								+ "&size=50" + "&lmsj=mall");
//				List<NoteModel> list = web.getList(NoteModel.class);
//				HashMap<Integer, List<NoteModel>> map = new HashMap<Integer, List<NoteModel>>();
//				map.put(index++, list);
//				return map;
//			}
//
//		});
	}

	void deleteData(final String id) {
		Util.asynTask(this, "正在删除记事", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				if (runData.equals("success")) {
					Toast.makeText(context, "删除成功！",
							Toast.LENGTH_LONG).show();
					currentPage = 0;
					adapter = new NoteAdapter(context);
					listView.setAdapter(adapter);
					asyncloadData();
					scrollPage();

				} else {
					Toast.makeText(context, "删除失败！",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.allianService, Web.deleteImportantNotes,
						"userId=" + Util.get(userId) + "&md5Pwd=" + md5Pwd
								+ "&id=" + id + "&lmsj=mall");
				String result = web.getPlan();
				return result;
			}

		});
	}
	void searchImportantNotes(final String content) {
		
		Map<String,String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("md5Pwd", md5Pwd);
		params.put("content", content);
		final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "正在查看记事信息...");
		NewWebAPI.getNewInstance().getWebRequest("/Note.aspx?call=searchImportantNotes",params, new WebRequestCallBack(){
			@Override
			public void success(Object result) {
				super.success(result);
				if(Util.isNull(result)){
					Util.show("网络异常！", NoteSearchActivity.this);
					return ;
				}
				JSONObject json = JSON.parseObject(result.toString());
				if(200 != json.getIntValue("code")){
					Util.show(json.getString("message"), NoteSearchActivity.this);
					return ;
				}
				List<NoteModel> list = JSON.parseArray(json.getString("list"), NoteModel.class);
				adapter.clear();
				if (list.size() != 0) {
					norecord.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
					adapter.setList(list);
				}else{
					listView.setVisibility(View.GONE);
					norecord.setVisibility(View.VISIBLE);	
				}
			
			}

			@Override
			public void requestEnd() {
				super.requestEnd();
				cpd.cancel();
				cpd.dismiss();
			}
			
		});
		
//		Util.asynTask(new IAsynTask() {
//			@Override
//			public void updateUI(Serializable runData) {
//				HashMap<Integer, List<NoteModel>> map = (HashMap<Integer, List<NoteModel>>) runData;
//				List<NoteModel> list = map.get(0);
//				
//				if (list.size() != 0) {
//					adapter.setList(list);
//					
//					
//				} else {
//
//				}
//				
//			}
//			
//			@Override
//			public Serializable run() {
//				
//				
//				Web web = new Web(Web.allianService, Web.searchImportantNotes,
//						"userId=" + Util.get(userId) + "&md5Pwd=" + md5Pwd
//								+ "&content="+ content+"&lmsj=mall");
//				List<NoteModel> list = web.getList(NoteModel.class);
//				HashMap<Integer, List<NoteModel>> map = new HashMap<Integer, List<NoteModel>>();
//				map.put(0, list);
//				return map;
//			}
//			
//		});
	}
}
