package com.mall.note;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.android.common.logging.Log;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.NoteModel;
import com.mall.model.UserInfo;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ImportantNoteList extends BaseActivity {
	// @ViewInject(R.id.listview)
	private ListView listView;
	private int currentPage = 0;
	private String md5Pwd = "";
	private String userId = "";
	private NoteAdapter adapter;
	private DbUtils db = null;
	private List<NoteModel> models = new ArrayList<NoteModel>();
	// @ViewInject(R.id.xiala_lin)

	 public static final String TAG = "com.mall.note.ImportantNoteList";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("生命周期", "ImportantNoteList onCreate" );
		setContentView(R.layout.all_note_list);
		ViewUtils.inject(this);
		listView = (ListView) this.findViewById(R.id.listview_);
		db = DbUtils.create(this);
		addHeaderView();
		
		
		adapter = new NoteAdapter(ImportantNoteList.this);

		adapter.setList(models);
		listView.setAdapter(adapter);
		// 验证用户是否登录
		if (Util.checkLoginOrNot()) {
			UserInfo userInfo = new UserInfo();
			md5Pwd = UserData.getUser().getMd5Pwd();
			userId = UserData.getUser().getUserId();
			try {
				List<NoteModel> data = db.findAll(NoteModel.class);
				if (null != data && 0 != data.size()) {
					adapter.setList(data);
				}
			} catch (DbException e) {
				e.printStackTrace();
				Log.e("e--", e.toString());
				Util.show("数据操作权限异常！", ImportantNoteList.this);
			}
		} else {
			Util.show("请先登录", ImportantNoteList.this);
			Util.showIntent(ImportantNoteList.this, LoginFrame.class);
			return;
		}
		scrollPage();
		adapter.clear();
		currentPage = 0;
		firsPage();
		registerReceiverAtBase(new String[] { "com.mall.note.ImportantNoteList" });
	}

	private void addHeaderView() {
		View view = LayoutInflater.from(context).inflate(R.layout.my_note_header_view, null);
		TextView my_note_year = (TextView) view.findViewById(R.id.my_note_year);
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00")); // 获取东八区时间
		int year = c.get(Calendar.YEAR); // 获取年
		my_note_year.setText("" + year);
		listView.addHeaderView(view);
	}

	@OnClick(R.id.top_back)
	public void onclick(View view) {
		finish();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("生命周期", "ImportantNoteList 停止" );
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("生命周期", "ImportantNoteList 结束" );
	}
	
	

	private void firsPage() {
		asyncloadData();
	}

	public void scrollPage() {
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount() && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					asyncloadData();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
		try {
			AnimeUtil.setAnimationEmptyView(ImportantNoteList.this, listView, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "正在获取记事信息...");
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", userId);
		params.put("md5Pwd", md5Pwd);
		params.put("yearMonthDay", "");
		params.put("page", (++currentPage) + "");
		params.put("size", "30");
		NewWebAPI.getNewInstance().getWebRequest("/Note.aspx?call=getImportantNotes", params, new WebRequestCallBack() {
			@Override
			public void success(Object result) {
				super.success(result);
				cpd.cancel();
				cpd.dismiss();
				if (Util.isNull(result)) {
					Util.show("网络异常,请重试！", ImportantNoteList.this);
					return;
				}
				JSONObject json = null;
				try {
					json = JSON.parseObject(result.toString());
				} catch (Exception e) {
					Util.show("数据异常，请联系远大技术人员！", ImportantNoteList.this);
					return;
				}
				if (null != json && 200 != json.getIntValue("code")) {
					Util.show(json.getString("message"), ImportantNoteList.this);
					return;
				}

				List<NoteModel> list = JSON.parseArray(json.getString("list"), NoteModel.class);

				if (1 == currentPage) {
					try {
						db.deleteAll(NoteModel.class);
						db.saveAll(list);
					} catch (DbException e) {
						e.printStackTrace();
						Util.show("数据操作异常或权限被禁止，请重试！", ImportantNoteList.this);
						return;
					}
				}
				if (null != list && list.size() != 0) {
					adapter.setList(list);
					models.addAll(list);
				} else {
					try {
						showEmityData();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}

			@Override
			public void requestEnd() {
				super.requestEnd();
				cpd.cancel();
				cpd.dismiss();
			}
		});
		// Util.asynTask(ImportantNoteList.this, "正在获取记事信息", new IAsynTask() {
		// @Override
		// public void updateUI(Serializable runData) {
		// AnimeUtil.setNoDataEmptyView("暂无记事",
		// R.drawable.community_dynamic_empty, ImportantNoteList.this, listView,
		// true, null);
		// int index = 0;
		// HashMap<Integer, List<NoteModel>> map = (HashMap<Integer,
		// List<NoteModel>>) runData;
		// List<NoteModel> list = map.get(index++);
		// if(1 == currentPage){
		// try {
		// db.deleteAll(NoteModel.class);
		// db.saveAll(list);
		// } catch (DbException e) {
		// e.printStackTrace();
		// }
		// }
		// if (list.size() != 0) {
		// adapter.setList(list);
		// models.addAll(list);
		// } else {
		//
		// }
		// }
		//
		// @Override
		// public Serializable run() {
		// int index = 0;
		// Web web = new Web(Web.allianService, Web.getImportantNotes,
		// "userId=" + Util.get(userId) + "&md5Pwd=" + md5Pwd
		// + "&yearMonthDay=" + "&page=" + (++currentPage)
		// + "&size=50" + "&lmsj=mall");
		// List<NoteModel> list = web.getList(NoteModel.class);
		// HashMap<Integer, List<NoteModel>> map = new HashMap<Integer,
		// List<NoteModel>>();
		// map.put(index++, list);
		// return map;
		// }
		//
		// });
	}

	public void showEmityData() {
		AnimeUtil.setNoDataEmptyView("您还没有发表记事，点击发表记事。", R.drawable.community_dynamic_empty, context, listView, true,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new OnAnimEnd() {
							@Override
							public void start() {

							}

							@Override
							public void repeat() {

							}

							@Override
							public void end() {
								Util.showIntent(ImportantNoteList.this, AddOneNote.class);
							}
						});
					}
				});
	}

	void deleteData(final String id) {
		Util.asynTask(this, "正在删除记事", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				String result = (String) runData;
				if (runData.equals("success")) {
					Toast.makeText(ImportantNoteList.this, "删除成功！", Toast.LENGTH_LONG).show();
					currentPage = 0;
					adapter = new NoteAdapter(ImportantNoteList.this);
					listView.setAdapter(adapter);
					try {
						db.deleteById(NoteModel.class, id);
					} catch (DbException e) {
						e.printStackTrace();
					}
					firsPage();
					scrollPage();
				} else {
					Toast.makeText(ImportantNoteList.this, "删除失败！", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.allianService, Web.deleteImportantNotes,
						"userId=" + Util.get(userId) + "&md5Pwd=" + md5Pwd + "&id=" + id + "&lmsj=mall");
				String result = web.getPlan();
				return result;
			}

		});
	}

	@Override
	public void onReceiveBroadcast(Intent intent) {
		super.onReceiveBroadcast(intent);
		Log.e("广播接受","成功");
		adapter.clear();
		currentPage = 0;
		firsPage();
	}

}
