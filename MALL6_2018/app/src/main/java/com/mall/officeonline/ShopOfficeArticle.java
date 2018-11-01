package com.mall.officeonline;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.ShopOfficeArticleModel;
import com.mall.model.ShopOfficeInfo;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.BMFWFrane;
import com.mall.view.LoginFrame;
import com.mall.view.R;

public class ShopOfficeArticle extends Activity {
	private String officeId = "";
	private int page = 1;
	private ShopOfficeArticleAdapter adapter;
	@ViewInject(R.id.listview)
	private ListView listView;
	private PopupWindow distancePopup;
	private List<ShopOfficeArticleModel> original = new ArrayList<ShopOfficeArticleModel>();
	private boolean searched = false;
	@ViewInject(R.id.righttwo)
	private ImageView righttwo;
	private String officeuserid = "";
	@ViewInject(R.id.layer)
	private LinearLayout layer;
	@ViewInject(R.id.logo)
	private ImageView OfficeLogo;
	@ViewInject(R.id.name)
	private TextView name;
	@ViewInject(R.id.visitors)
	private TextView Visitors;
	@ViewInject(R.id.baobei)
	private TextView BaoBei;
	@ViewInject(R.id.notes)
	private TextView Notes;
	@ViewInject(R.id.vedios)
	private TextView Vedios;
	@ViewInject(R.id.album)
	private TextView Albums;
	@ViewInject(R.id.office_banner)
	private ImageView OfficeBanner;
	@ViewInject(R.id.rank_container)
	private LinearLayout rank_container;
	private int crown = 0, sun = 0, moon = 0, star = 0;
	private BitmapUtils bmUtils;
	private int _50dp = 50;
	private User user;
	private String albums = "";
	private String userNo = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_office_article);
		ViewUtils.inject(this);
		bmUtils = new BitmapUtils(this);
		_50dp = Util.pxToDp(this, 150);
		officeuserid = this.getIntent().getStringExtra("offid");
		crown = this.getIntent().getIntExtra("crown", 0);
		sun = this.getIntent().getIntExtra("sun", 0);
		moon = this.getIntent().getIntExtra("moon", 0);
		star = this.getIntent().getIntExtra("star", 0);
		userNo = this.getIntent().getStringExtra("userNo");
		albums = this.getIntent().getStringExtra("Albums");
		if (!Util.isNull(albums)) {
			Albums.setText(albums);
		}
		init();
	}
	@OnClick(R.id.logo)
	public void LogoClick(final View v){ 
		if (UserData.getUser() != null && UserData.getOfficeInfo() != null) {
			System.out.println("UserData.getUser().getUserIdNoEncodByUTF8()=="+UserData.getUser().getUserIdNoEncodByUTF8()+"UserData.getOfficeInfo().getUserid()=="+UserData.getOfficeInfo().getUserid());
			if (UserData.getUser().getUserIdNoEncodByUTF8().equals(UserData.getOfficeInfo().getUserid())) {
				Util.showIntent(this, ShopOfficeConfig.class);
			}
		}
	}
	@OnClick(R.id.connect)
	public void PhoneClick(final View v) {
		Util.doPhone(Util._400, this);
	}

	@OnClick(R.id.baobei_layout)
	public void BaoBeiLayout(final View v) {
		Intent intent = new Intent(this, ShopOfficeFrame.class);
		intent.putExtra("offid", officeuserid);
		intent.putExtra("crown", crown);
		intent.putExtra("sun", sun);
		intent.putExtra("moon", moon);
		intent.putExtra("star", star);
		intent.putExtra("Albums", Albums.getText().toString());
		intent.putExtra("officeid", officeuserid);
		intent.putExtra("userNo", userNo);
		this.startActivity(intent);
	}

	@OnClick(R.id.albumLayout)
	public void AlbumClick(final View v) {
		Intent intent = new Intent(this, AlbumList.class);
		intent.putExtra("offid", officeuserid);
		intent.putExtra("crown", crown);
		intent.putExtra("sun", sun);
		intent.putExtra("moon", moon);
		intent.putExtra("star", star);
		intent.putExtra("Albums", Albums.getText().toString());
		this.startActivity(intent);
	}

	@OnClick(R.id.vedioLayout)
	public void VedioClick(final View v) {
		Intent intent = new Intent(this, ShopOfficeVedio.class);
		intent.putExtra("offid", officeuserid);
		intent.putExtra("crown", crown);
		intent.putExtra("sun", sun);
		intent.putExtra("moon", moon);
		intent.putExtra("star", star);
		intent.putExtra("Albums", Albums.getText().toString());
		this.startActivity(intent);
	}

	@OnClick(R.id.bmLayout)
	public void BmClick(final View v) {
		if (UserData.getUser() != null) {
			user = UserData.getUser();
		}
		Intent intent = new Intent();
		intent.setClass(this, BMFWFrane.class);
		intent.putExtra("unum", user.getUserNo());
		this.startActivity(intent);
	}

	private void clacluate(LinearLayout Layout, Context c, int crown, int sun,
			int moon, int star) {
		int size=crown+sun+moon+star;
		int[] ione=new int[size];
		for(int i=0;i<crown;i++){
			ione[i]=R.drawable.hg;//ione[0]=hg
		}
		for(int i=0;i<sun;i++){
			ione[i+crown]=R.drawable.hg;//ione[1]=sun,ion[2]=sun
		}       
		for(int i=0;i<moon;i++){
			ione[i+crown+sun]=R.drawable.diamond;
		}
		for(int i=0;i<star;i++){
			ione[i+crown+sun+moon]=R.drawable.red_heart;
		}
		addIndexImage(Layout,ione);
	}

	private void addIndexImage(LinearLayout Layout, int[] res) {
		Layout.removeAllViews();
		LinearLayout.LayoutParams l=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		l.setMargins(0, 0, 0, 0);
		int[] rank = res;
		for (int i = 0; i < rank.length; i++) {
			ImageView img = new ImageView(this);
			img.setLayoutParams(l);
			img.setImageResource(rank[i]);
			Layout.addView(img);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void init() {
		officeId = this.getIntent().getStringExtra("officeid");
		Util.initTopWithTwoButton(this, "日志", R.drawable.vedio_search,
				R.drawable.note_add, new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 搜索功能
				getPopupWindow();
				startPopupWindow();
				distancePopup.showAsDropDown(v);
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UserData.getUser() != null
						&& UserData.getOfficeInfo() != null) {
					if (UserData.getUser().getUserIdNoEncodByUTF8()
							.equals(officeuserid)) {
						getPopupWindow();
						startAddClassPopupWindow();
						distancePopup.showAsDropDown(v);
					}
				}
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShopOfficeArticle.this.finish();
			}
		});
		if (UserData.getUser() != null && UserData.getOfficeInfo() != null) {
			if (!UserData.getUser().getUserIdNoEncodByUTF8()
					.equals(UserData.getOfficeInfo().getUserid())) {
				righttwo.setVisibility(View.GONE);
			}
		} else {
			righttwo.setVisibility(View.GONE);
		}
		firstpage();
		scrollPage();
		layer.getBackground().setAlpha(150);
		initOfficeInfo();
		clacluate(rank_container, this, crown, sun, moon, star);
	}

	private void addclass() {
		LayoutInflater infl = LayoutInflater.from(this);
		View view = infl.inflate(R.layout.add_album_classify, null);
		final EditText pwd = (EditText) view.findViewById(R.id.second_pwd);
		pwd.setFocusable(true);
		Button submit = (Button) view.findViewById(R.id.submit);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		final Dialog ad = new Dialog(this, R.style.CustomDialogStyle);
		ad.show();
		Window window = ad.getWindow();
		WindowManager.LayoutParams pa = window.getAttributes();
		pa.width = Util.dpToPx(this, 250);
		window.setBackgroundDrawable(new ColorDrawable(0));
		window.setContentView(view, pa);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Util.isNull(pwd.getText().toString())) {
					Toast.makeText(ShopOfficeArticle.this, "请输入分类名称",
							Toast.LENGTH_LONG).show();
				}
				String id = "";
				if (UserData.getOfficeInfo() != null) {
					id = UserData.getOfficeInfo().getOffice_id();
				}
				addArticleClassify(id, pwd.getText().toString(), "1");
				ad.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
	}

	public void addArticleClassify(final String officeid,
			final String typename, final String parent) {
		if (UserData.getUser() != null) {
			final User user = UserData.getUser();
			Util.asynTask(this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if (runData != null) {
						if ("ok".equals(runData + "")) {
							Toast.makeText(ShopOfficeArticle.this, "添加分类成功",
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(ShopOfficeArticle.this, "添加分类失败",
								Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.officeUrl, Web.AddOfficeArticleClass,
							"typename=" + typename + "&userID="
									+ user.getUserId() + "&userPaw="
									+ user.getMd5Pwd() + "&parent=" + parent);
					return web.getPlan();
				}
			});
		} else {
			Util.showIntent(this, LoginFrame.class);
		}
	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}

	/**
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimation);
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startAddClassPopupWindow() {
		View pview = getLayoutInflater().inflate(
				R.layout.shop_office_add_class_or_image, null, false);
		TextView add_class = (TextView) pview.findViewById(R.id.add_class);
		TextView add_article = (TextView) pview.findViewById(R.id.add_image);
		add_article.setText("添加文章视频");
		add_class.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 添加分类
				addclass();
				distancePopup.dismiss();
			}
		});
		add_article.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
				Util.showIntent(ShopOfficeArticle.this,
						OfficeAddVedioOrArticle.class,new String[]{"article"},new String[]{"article"});
			}
		});
		initpoputwindow(pview);
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow() {
		View pview = getLayoutInflater().inflate(R.layout.search_dialog, null,
				false);
		final EditText search_e = (EditText) pview
				.findViewById(R.id.search_edit);
		TextView search_text = (TextView) pview.findViewById(R.id.search_text);
		search_text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.clear();
				List<ShopOfficeArticleModel> l = new ArrayList<ShopOfficeArticleModel>();
				for (int i = 0; i < original.size(); i++) {
					if (original.get(i).getContent()
							.contains(search_e.getText().toString())) {
						l.add(original.get(i));
					}
				}
				if (l.size() > 0) {
					adapter.setList(l);
					adapter.notifyDataSetChanged();
				}
				searched = true;
				distancePopup.dismiss();
			}
		});
		initpoputwindow(pview);
	}

	public void firstpage() {
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

	private void getOfficeArticleList() {
		Util.asynTaskTwo(this, "获取日志", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					HashMap<Integer, List<ShopOfficeArticleModel>> map = (HashMap<Integer, List<ShopOfficeArticleModel>>) runData;
					List<ShopOfficeArticleModel> list = map.get(page);
					if (adapter == null) {
						adapter = new ShopOfficeArticleAdapter(
								ShopOfficeArticle.this);
						listView.setAdapter(adapter);
					}
					if (list != null && list.size() > 0) {
						adapter.setList(list);
						adapter.notifyDataSetChanged();
						original.addAll(list);
					}
				} else {
					Toast.makeText(ShopOfficeArticle.this, "未获取到日志数据...",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.officeUrl, Web.GetArticlesListPage,
						"officeid=" + officeId + "&cPage=" + (page++)
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
					Intent intent = new Intent(ShopOfficeArticle.this,
							ShopOfficeArticleComment.class);
					intent.putExtra("articleid", smm.getArticleid());
					Bundle bun = new Bundle();
					bun.putSerializable("article", smm);
					intent.putExtras(bun);
					intent.putExtra("title", smm.getTitle());
					ShopOfficeArticle.this.startActivity(intent);
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
						Util.showChoosedDialog(ShopOfficeArticle.this, "是否要删除该日志？",
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
			Util.asynTaskTwo(ShopOfficeArticle.this, "", new IAsynTask() {
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

	private void initOfficeInfo() {
		if (UserData.getOfficeInfo() != null) {
			ShopOfficeInfo shopoffice = UserData.getOfficeInfo();
			if (!Util.isNull(shopoffice.getClicks())) {
				Visitors.setText("访客：" + shopoffice.getClicks());
			}
			if (!Util.isNull(shopoffice.getProductcount())) {
				BaoBei.setText(shopoffice.getProductcount());
			} else {
				BaoBei.setText("0");
			}
			if (!Util.isNull(shopoffice.getVdieo_count())) {
				Vedios.setText(shopoffice.getVdieo_count());
			}
			if (!Util.isNull(shopoffice.getArt_count())) {
				Notes.setText(shopoffice.getArt_count());
			}
			if(!Util.isNull(shopoffice.getOfficename())){
				if (!Util.isNull(shopoffice.getOfficename())) {
					name.setText(shopoffice.getOfficename());
				} else {
					name.setText("");
				}    
			}
			if (!Util.isNull(shopoffice.getImgLogo())) {
				String logourl = "";
				if (shopoffice.getImgLogo1().contains("http")) {
					logourl = shopoffice.getImgLogo();
				} else {
					logourl = "http://" + Web.webImage
							+ shopoffice.getImgLogo();
				}
				bmUtils.display(OfficeLogo, logourl,new BitmapLoadCallBack<View>() {

					@Override
					public void onLoadCompleted(View arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						Bitmap zoomBm = Util.zoomBitmap(arg2,_50dp, _50dp);
						OfficeLogo.setImageBitmap(Util.toRoundCorner(zoomBm, 5));
					}
					@Override
					public void onLoadFailed(View arg0, String arg1,
							Drawable arg2) {
						Resources r = ShopOfficeArticle.this
								.getResources();
						InputStream is = r
								.openRawResource(R.drawable.ic_launcher_black_white);
						BitmapDrawable bmpDraw = new BitmapDrawable(is);
						Bitmap b = Util.convertToBlackWhite(bmpDraw
								.getBitmap());
						Bitmap zoomBm = Util.zoomBitmap(
								bmpDraw.getBitmap(), _50dp, _50dp);
						OfficeLogo.setImageBitmap(Util.toRoundCorner(
								zoomBm, 5));

					}
				});
			} else {
				Resources r = ShopOfficeArticle.this.getResources();
				InputStream is = r
						.openRawResource(R.drawable.ic_launcher_black_white);
				BitmapDrawable bmpDraw = new BitmapDrawable(is);
				Bitmap b = Util.convertToBlackWhite(bmpDraw.getBitmap());
				Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(), _50dp,
						_50dp);
				OfficeLogo.setImageBitmap(Util.toRoundCorner(zoomBm, 5));
			}
			String url = "";
			if (shopoffice.getImgLogo1().contains("http")) {
				url = shopoffice.getImgLogo1();
			} else {
				url = "http://" + Web.webImage + shopoffice.getImgLogo1();
			}
			if (!Util.isNull(url)) {
				bmUtils.display(OfficeBanner, url,
						new DefaultBitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View container,
							String uri, Bitmap bitmap,
							BitmapDisplayConfig config,
							BitmapLoadFrom from) {
						OfficeBanner.setImageBitmap(bitmap); 
					}
					@Override
					public void onLoadFailed(View container,
							String uri, Drawable drawable) {
						OfficeBanner
						.setImageResource(R.drawable.shop_office_banner);
					}
				});
			} else {
				OfficeBanner.setImageResource(R.drawable.shop_office_banner);
			}
		}
	}
}
