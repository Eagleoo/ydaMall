package com.mall.card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.card.adapter.ListViewCompat;
import com.mall.card.adapter.SlideView;
import com.mall.card.adapter.SlideView.OnSlideListener;
import com.mall.card.bean.CardGrouping;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

public class CardGroupingManagement extends Activity implements
		OnItemClickListener, OnSlideListener {
	private static final String TAG = "MainActivity";
	private ListViewCompat mListView;
	private List<MessageItem> mMessageItems = new ArrayList<CardGroupingManagement.MessageItem>();
	private SlideView mLastSlideViewWithStatusOn;
	private SlideAdapter adapter;
	private Dialog dialog;
	private List<CardGrouping> cardGroupings=new ArrayList<CardGrouping>();
	private String fenzu;
	private String cardid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setView();
		initView();
	}

	@OnClick({ R.id.card_group_add, R.id.top_back })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.card_group_add:
			addGroup();
			break;

		case R.id.top_back:
			finish();
			break;
		}
	}
	private void deleteTishi(final String id ,final int postions){
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_delete_dialog, null);
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView yihou_update=(TextView) layout.findViewById(R.id.yihou_update);
		TextView now_update=(TextView) layout.findViewById(R.id.now_update);
		yihou_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		now_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteGrouping(id, postions);
				dialog.dismiss();
			}
		});
	}
	/**
	 * 删除分组
	 */
	private void deleteGrouping(final String id,final int postion){
		final User user = UserData.getUser();
		Util.asynTask(this,"删除中…",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {

				if (runData != null) {
					Map<String, String> map = CardUtil.JsonToMap(runData
							.toString());
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(CardGroupingManagement.this,
									"删除成功", Toast.LENGTH_SHORT)
									.show();
							mMessageItems.remove(postion);
							cardGroupings.remove(postion);
							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(CardGroupingManagement.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(CardGroupingManagement.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}
				} else {

					Toast.makeText(CardGroupingManagement.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
							.show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard, Web.delUserBusinessCardGroup,
						"delUserBusinessCardGroup", "userId="
								+ UserData.getUser().getUserId() + "&md5Pwd="
								+ UserData.getUser().getMd5Pwd() + "&id="+id+"&isdel=1");
				String s = web.getPlan();
				return s;
			}

		});
	}
	public void setView() {
		setContentView(R.layout.card_grouping_management);
		ViewUtils.inject(this);
		fenzu=getIntent().getStringExtra("fenzu");
		cardid=getIntent().getStringExtra("cardid");
	}

	private void addGroup() {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_add_fenzu, null);
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		final EditText group_name = (EditText) layout
				.findViewById(R.id.group_name);
		Button queding = (Button) layout.findViewById(R.id.queding);
		Button quxiao = (Button) layout.findViewById(R.id.quxiao);
		queding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(group_name.getText().toString().trim())) {
					Toast.makeText(CardGroupingManagement.this, "请输入分组名称",
							Toast.LENGTH_SHORT).show();
				} else {
					for (int i = 0; i < cardGroupings.size(); i++) {
						String string=cardGroupings.get(i).getGroupname();
						if (group_name.getText().toString().trim().equals(string)) {
							Util.show("该分组已经存在，请重新输入分组名", CardGroupingManagement.this);
							return;
						}
						
					}
					addGrouping("&groupName="
							+ group_name.getText().toString().trim()
							+ "&remark=");
					dialog.dismiss();
				}
			}
		});
		quxiao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}

	public void initView() {
		mListView = (ListViewCompat) findViewById(R.id.list);
		getCardGrouping();
		

	}

	private class SlideAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		SlideAdapter() {
			super();
			mInflater = getLayoutInflater();
		}

		@Override
		public int getCount() {
			return mMessageItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mMessageItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			SlideView slideView = (SlideView) convertView;
			if (slideView == null) {
				View itemView = mInflater.inflate(
						R.layout.item_listview_delete, null);

				slideView = new SlideView(CardGroupingManagement.this);
				slideView.setContentView(itemView);

				holder = new ViewHolder(slideView);
				slideView.setOnSlideListener(CardGroupingManagement.this);
				slideView.setTag(holder);
			} else {
				holder = (ViewHolder) slideView.getTag();
			}
			MessageItem item = mMessageItems.get(position);
			item.slideView = slideView;
			item.slideView.shrink();
			holder.title.setText(item.title);
			holder.deleteHolder.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					/*mMessageItems.remove(position);
					adapter.notifyDataSetChanged();*/
					deleteTishi(cardGroupings.get(position).getId(),position);
				}
			});
			holder.update.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					adapter = new SlideAdapter();
					mListView.setAdapter(adapter);
					mListView
							.setOnItemClickListener(CardGroupingManagement.this);

				}
			});
			/*slideView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (TextUtils.isEmpty(fenzu)) {
						
					}else {
						updateBusinessCard(cardid, cardGroupings.get(position).getId());
					}
				}
			});*/
			return slideView;
		}

	}

	public class MessageItem {
		public String title;
		public SlideView slideView;
	}

	private static class ViewHolder {
		public TextView title;
		public TextView deleteHolder;
		public TextView update;

		ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.title);
			deleteHolder = (TextView) view.findViewById(R.id.delete);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (TextUtils.isEmpty(fenzu)) {
			
		}else {
			updateBusinessCard(cardid, cardGroupings.get(position).getId());
		}
	}

	@Override
	public void onSlide(View view, int status) {
		if (mLastSlideViewWithStatusOn != null
				&& mLastSlideViewWithStatusOn != view) {
			mLastSlideViewWithStatusOn.shrink();
		}

		if (status == SLIDE_STATUS_ON) {
			mLastSlideViewWithStatusOn = (SlideView) view;
		}
	}

	public void addGrouping(final String path) {
		final User user = UserData.getUser();
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					Map<String, String> map = CardUtil.JsonToMap(runData
							.toString());
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(CardGroupingManagement.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
							getCardGrouping();
						} else {
							Toast.makeText(CardGroupingManagement.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						Toast.makeText(CardGroupingManagement.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(CardGroupingManagement.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
							.show();
				}
			}
			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard,
						Web.addUserBusinessCardGroup,
						"addUserBusinessCardGroup", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + path);
				String s = web.getPlan();
				return s;
			}

		});
	}
	/**
	 * 获得用户的名片的分组信息
	 */
	public void getCardGrouping() {
		final User user = UserData.getUser();
		Util.asynTask(this,"查询中",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				mMessageItems.clear();
				cardGroupings.clear();
				Map<String, String> map = new HashMap<String, String>();
				List<CardGrouping> list = new ArrayList<CardGrouping>();
				if (runData != null) {
					System.out.println(runData.toString().replace(" ", "").replace(",}","}"));
					try {
						map = CardUtil.getJosn(runData.toString().replace(" ", "").replace(",}","}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Gson gson = new Gson();
							list = gson.fromJson(map.get("list"),
									new TypeToken<List<CardGrouping>>() {
									}.getType());
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getIsdel().equals("0")) {
									cardGroupings.add(list.get(i));
								}
								
							}
							for (int i = 0; i < cardGroupings.size(); i++) {
								MessageItem item = new MessageItem();
								item.title=cardGroupings.get(i).getGroupname();
								mMessageItems.add(item);
							}
							
							adapter = new SlideAdapter();
							mListView.setAdapter(adapter);
							mListView.setOnItemClickListener(CardGroupingManagement.this);
						} else {
							Toast.makeText(CardGroupingManagement.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(CardGroupingManagement.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}
					if (list.size() > 0) {
						
					} else {
						
						Toast.makeText(CardGroupingManagement.this, "没有更多名片分组",
								Toast.LENGTH_LONG).show();
					}
				} else {
					
				}
				
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(1, Web.bBusinessCard,
						Web.getUserBusinessCardGroup,
						"getUserBusinessCardGroup", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd());
				String s = web.getPlan();
				return s;
			}

		});
	}
	/**
	 * 移动分组	
	 */
	public void updateBusinessCard(final String id,final String group){
		final User user = UserData.getUser();
		Util.asynTask(this,"移动中…",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					Map<String, String> map = CardUtil.JsonToMap(runData
							.toString());
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(CardGroupingManagement.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
							finish();
						} else {
							Toast.makeText(CardGroupingManagement.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {
						Toast.makeText(CardGroupingManagement.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(CardGroupingManagement.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
							.show();
				}
			}
			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard,
						Web.updateBusinessCard,
						"updateBusinessCard", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&id="+id+"&group="+group);
				String s = web.getPlan();
				return s;
			}

		});
	}
}
