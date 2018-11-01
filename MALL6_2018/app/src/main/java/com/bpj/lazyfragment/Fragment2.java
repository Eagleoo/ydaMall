package com.bpj.lazyfragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;

import com.mall.model.ShopOfficeArticleModel;
import com.mall.model.ShopOfficeInfo;
import com.mall.net.Web;
import com.mall.officeonline.ShopOfficeArticle;
import com.mall.officeonline.ShopOfficeArticleComment;
import com.mall.officeonline.ShopOfficeArticle.ShopOfficeArticleAdapter;
import com.mall.officeonline.ShopOfficeArticle.ViewHolder;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


public class Fragment2  extends LazyFragment {
	private int page = 1;
	private String officeId = "";
	private Context mContext;
	private ShopOfficeArticleAdapter  adapter;
	private ListView listView;
	private boolean searched = false;  
	private List<ShopOfficeArticleModel> original = new ArrayList<ShopOfficeArticleModel>();
	
	
	public static Fragment2 newInstance(){
		Fragment2 fragment1 = new Fragment2();
		return fragment1;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.e("mContext","Fragment2 mContext创建");
		View view = inflater.inflate(R.layout.fragment2,null);
		mContext=getActivity();
		listView=(ListView) view.findViewById(R.id.listview);
		firstpage();
		scrollPage();
		return view;
	}
	
	private void firstpage() {
		// TODO Auto-generated method stub
		getOfficeArticleList();
	}
	public void scrollPage() {
		listView.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (searched) {
						adapter.clear();
						adapter.setList(original);
					}
					getOfficeArticleList();
					searched = false;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;

			}
		});
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView()
					.setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
	
	@Override
	protected void onVisible() {
		System.out.println("Fragment2��ʼ���ء�����");
	}
	
	
	private void getOfficeArticleList() {
		Util.asynTaskTwo((Activity)mContext, "获取日志", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					HashMap<Integer, List<ShopOfficeArticleModel>> map = (HashMap<Integer, List<ShopOfficeArticleModel>>) runData;
					List<ShopOfficeArticleModel> list = map.get(page);
					if (adapter == null) {
						adapter = new ShopOfficeArticleAdapter(
								mContext);
						listView.setAdapter(adapter);
					}
					if (list != null && list.size() > 0) {
						adapter.setList(list);
						adapter.notifyDataSetChanged();
						original.addAll(list);
					}
				} else {
					Toast.makeText(mContext, "未获取到日志数据...",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.officeUrl, Web.GetArticlesListPage,
						"officeid=" + MainActivity.shopoffice.getOffice_id() + "&cPage=" + (page++)
						+ "&flag=1&typeid=1&sec=1");
				List<ShopOfficeArticleModel> list = web
						.getList(ShopOfficeArticleModel.class);
				HashMap<Integer, List<ShopOfficeArticleModel>> map = new HashMap<Integer, List<ShopOfficeArticleModel>>();
				map.put(page, list);
				return map;
			}
		});
	}
	
	public class ShopOfficeArticleAdapter extends BaseAdapter {
		private Context c;
		private List<ShopOfficeArticleModel> list = new ArrayList<ShopOfficeArticleModel>();
		private LayoutInflater inflater;
		private String content;

		public ShopOfficeArticleAdapter(Context c) {
			this.c = c;
			inflater = LayoutInflater.from(c);
		}

		public void setList(List<ShopOfficeArticleModel> list) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}

		public void clear() {
			this.list.clear();
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ShopOfficeArticleModel sm = this.list.get(position);
			ViewHolder h = null;
			if (convertView == null) {
				h = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.shop_office_article_item, null);
				h.message = (TextView) convertView.findViewById(R.id.message);
				h.comment = (TextView) convertView.findViewById(R.id.comment);
				h.time = (TextView) convertView.findViewById(R.id.time);
				h.read = (TextView) convertView.findViewById(R.id.read);
				h.share = (TextView) convertView.findViewById(R.id.share);
				convertView.setTag(h);
			} else {
				h = (ViewHolder) convertView.getTag();
			}
			if (!Util.isNull(sm.getCreateTime())) {
				h.time.setText(Util.friendly_time(sm.getCreateTime()));
			}
			if (!Util.isNull(sm.getContent())) {
				content = Util.Html2Text(sm.getContent());
				content = content.replace("&nbsp;", "");
				if (content.length() > 100) {
					h.message.setText(content.subSequence(0, 100) + "...");
				} else {
					h.message.setText(content);
				}
			}
			if (!Util.isNull(sm.getCommentCount())) {
				h.comment.setText(Util.spannBlueFromBegin("评论   ",
						"(" + sm.getCommentCount() + ")"));
			}
			if (!Util.isNull(sm.getClicks())) {
				h.read.setText(Util.spannBlueFromBegin("赞   ",
						"(" + sm.getGoodclicks() + ")"));
			}
			final ShopOfficeArticleModel smm = sm;
			h.read.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView t = (TextView) v;
					GoodClick(smm.getArticleid(), t,
							Integer.parseInt(smm.getGoodclicks()));
				}
			});
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext,
							ShopOfficeArticleComment.class);
					intent.putExtra("articleid", smm.getArticleid());
					Bundle bun = new Bundle();
					bun.putSerializable("article", smm);
					intent.putExtras(bun);
					intent.putExtra("title", smm.getTitle());
					mContext.startActivity(intent);
				}
			});
			final TextView me = h.message;  
			h.share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView t = (TextView) v;
					Share(c, t, content, smm.getArticleid());
				}
			});
			convertView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if(UserData.getUser().getUserIdNoEncodByUTF8().equals(UserData.getOfficeInfo().getUserid())){
						Util.showChoosedDialog(mContext, "是否要删除该日志？",
								"点错了", "确定删除", new OnClickListener() {
							@Override
							public void onClick(View v) {
								deleteVedio(smm.getUsername(),
										smm.getArticleid());
							}
						});
					}  
					return true;
				}
			});
			return convertView;
		}
		private void GoodClick(final String id, final TextView t,
				final int previous) {
			Util.asynTaskTwo((Activity)mContext, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if ("ok".equals(runData + "")) {
						int num = previous + 1;
						t.setText(Util.spannBlueFromBegin("赞   ", "(" + num
								+ ")"));
					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.officeUrl, Web.UpdateGoods,
							"articleid=" + id);
					String result = web.getPlan();
					return result;
				}
			});
		}

		private void Share(Context c, final TextView t, String content,
				String artid) {
			String url = "";
			String unum = "";
			if (UserData.getUser() != null) {
				unum = UserData.getUser().getUserNo();
			}
			url = "http://" + Web.webImage
					+ "/user/office/myArtdetail.aspx?unum=" + unum
					+ "&articleid=" + artid + "&typeid=1";
			OnekeyShare oks = new OnekeyShare();
			oks.setTitle("网上空间日志分享");
			oks.setAddress("");
			oks.setTitleUrl(url);
			oks.setText(content);
			oks.setUrl(url);
			oks.setComment(c.getString(R.string.share));
			oks.setSite(c.getString(R.string.app_name));
			oks.setSiteUrl("");
//			Platform[] plant = ShareSDK.getPlatformList(c);
//			for (int i = 0; i < plant.length; i++) {
//				Platform p = plant[i];
//				System.out.println("-----------------Platform-------------"
//						+ p.getName());
//				p.setPlatformActionListener(new PlatformActionListener() {
//					@Override
//					public void onError(Platform arg0, int arg1, Throwable arg2) {
//
//					}
//
//					@Override
//					public void onComplete(Platform arg0, int arg1,
//							HashMap<String, Object> arg2) {
//						int i = Integer.parseInt(t.getText().toString());
//						t.setText((i + 1) + "");
//					}
//
//					@Override
//					public void onCancel(Platform arg0, int arg1) {
//
//					}
//				});
//			}
			oks.show(c);

		}

		public void firstpage() {
			getOfficeArticleList();
		}
		private void deleteVedio(String userid, final String artId) {
			if (UserData.getUser() == null) {
				Util.showIntent(c, LoginFrame.class);
			}
			if (UserData.getOfficeInfo() != null) {
				if (UserData.getOfficeInfo().getUserid().equals(userid)) {
					Util.asynTask(c, "", new IAsynTask() {
						@Override
						public void updateUI(Serializable runData) {
							if (runData != null) {
								if ("ok".equals(runData + "")) {
									Toast.makeText(c, "删除成功", Toast.LENGTH_LONG)
									.show();
									page = 1;
									if (adapter != null) {
										adapter.clear();
										adapter.notifyDataSetChanged();
									}
									firstpage();
								}
							} else {
								Toast.makeText(c, "删除失败", Toast.LENGTH_LONG)
								.show();
							}
						}

						@Override
						public Serializable run() {
							Web web = new Web(Web.officeUrl,
									Web.DeleteOfficeUserArticle, "userID="
											+ UserData.getUser().getUserId()
											+ "&userPaw="
											+ new UserData().getUser()
											.getMd5Pwd()
											+ "&articleid=" + artId);
							return web.getPlan();
						}
					});
				}
			}
		}
	}
	public class ViewHolder {
		TextView message;
		TextView time;
		TextView comment;
		TextView read;
		TextView share;
	}
	
	public void seachadapter(String string){
		adapter.clear();
		List<ShopOfficeArticleModel> l = new ArrayList<ShopOfficeArticleModel>();
		for (int i = 0; i < original.size(); i++) {
			if (original.get(i).getContent()
					.contains(string)) {
				l.add(original.get(i));
			}
		}
		if (l.size() > 0) {
			adapter.setList(l);
			adapter.notifyDataSetChanged();
		}
		searched = true;
	}
	
	 public interface OnArticleSeachListener{  
	      public void onArticleSelected(Fragment2 fragment2, String string );  
	  } 
	
}
