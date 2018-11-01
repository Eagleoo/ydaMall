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
import android.text.SpannableString;
import android.util.Log;
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
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.ShopOfficeCommentMoel;
import com.mall.model.ShopOfficeInfo;
import com.mall.model.ShopOfficeVedioModel;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.BMFWFrane;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.FaceConversionUtil;

public class ShopOfficeVedio extends Activity {
	@ViewInject(R.id.vedio_list)
	private ListView listView;
	private int page = 1;
	private BitmapUtils bmUtils = null;           
	private VedioAdapter adapter;
	@ViewInject(R.id.comment_layout)   
	private LinearLayout comment_layout;
	private PopupWindow distancePopup = null;
	private List<ShopOfficeVedioModel> original = new ArrayList<ShopOfficeVedioModel>();
	private boolean searched = false;  
	@ViewInject(R.id.righttwo)
	private ImageView righttwo;
	private String officeuserid="";
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
	private int _50dp = 50;
	private User user;
	private String albums = "";
	private String userNo="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_office_vedio);
		ViewUtils.inject(this);
		bmUtils = new BitmapUtils(this);
		_50dp = Util.pxToDp(this, 150);
		crown = this.getIntent().getIntExtra("crown", 0);
		sun = this.getIntent().getIntExtra("sun", 0);
		moon = this.getIntent().getIntExtra("moon", 0);
		star = this.getIntent().getIntExtra("star", 0);
		albums = this.getIntent().getStringExtra("Albums");
		userNo = this.getIntent().getStringExtra("userNo");
		if (!Util.isNull(albums)) {
			Albums.setText(albums);
		}
		init();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick(R.id.connect)
	public void PhoneClick(final View v) {
		Util.doPhone(Util._400, this);
	}
	 @OnClick(R.id.baobei_layout)
	 public void BaoBeiLayout(final View v){
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
		this.startActivity(intent);
	}
	@OnClick(R.id.notesLayout)
	public void NotesClick(final View v) {
		Intent intent = new Intent(this, ShopOfficeArticle.class);
		intent.putExtra("offid", officeuserid);
		intent.putExtra("crown", crown);
		intent.putExtra("sun", sun);
		intent.putExtra("moon", moon);
		intent.putExtra("star", star);
		intent.putExtra("Albums", Albums.getText().toString());
		intent.putExtra("officeid", officeuserid);
		this.startActivity(intent);
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
	private void init() {
		officeuserid=this.getIntent().getStringExtra("offid");
		Util.initTopWithTwoButton(this, "视频", R.drawable.vedio_search,
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
						if(UserData.getUser()!=null&&UserData.getOfficeInfo()!=null){
							if(UserData.getUser().getUserIdNoEncodByUTF8().equals(officeuserid)){
								getPopupWindow();
								startAddClassPopupWindow();
								distancePopup.showAsDropDown(v);
							}
						}
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						ShopOfficeVedio.this.finish();
					}
				});
		if(UserData.getUser()!=null&&UserData.getOfficeInfo()!=null){
			if(!UserData.getUser().getUserIdNoEncodByUTF8().equals(UserData.getOfficeInfo().getUserid())){
				righttwo.setVisibility(View.GONE);
			}
		}else{
			righttwo.setVisibility(View.GONE);
		}
		firstPage();
		scrollPage();
		layer.getBackground().setAlpha(150);
		initOfficeInfo();
		clacluate(rank_container, this, crown, sun, moon, star);
	}
	private void initOfficeInfo() {
		if (UserData.getOfficeInfo() != null) {
			ShopOfficeInfo shopoffice = UserData.getOfficeInfo();
			if(!Util.isNull(shopoffice.getClicks())){
	    		Visitors.setText("访客：" + shopoffice.getClicks());
	    	}
	    	if(!Util.isNull(shopoffice.getProductcount())){
	    		BaoBei.setText(shopoffice.getProductcount());
	    	}else{
	    		BaoBei.setText("0");
	    	}
	    	if(!Util.isNull(shopoffice.getVdieo_count())){
	    		Vedios.setText(shopoffice.getVdieo_count());
	    	}
	    	if(!Util.isNull(shopoffice.getArt_count())){
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
				bmUtils.display(OfficeLogo, logourl,
						new DefaultBitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View container,
							String uri, Bitmap bitmap,
							BitmapDisplayConfig config,
							BitmapLoadFrom from) {
						Bitmap zoomBm = Util.zoomBitmap(bitmap,
								_50dp, _50dp);
						super.onLoadCompleted(container, uri,
								Util.toRoundCorner(zoomBm, 5),
								config, from);
					} 

							@Override
							public void onLoadFailed(View container,
									String uri, Drawable drawable) {
								Resources r = ShopOfficeVedio.this.getResources();
								InputStream is = r.openRawResource(R.drawable.ic_launcher_black_white);
								BitmapDrawable bmpDraw = new BitmapDrawable(is);
								Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),_50dp, _50dp);
								OfficeLogo.setImageBitmap(Util.toRoundCorner(zoomBm, 5));
							}
						});
			} else {
				Resources r = ShopOfficeVedio.this.getResources();
				InputStream is = r.openRawResource(R.drawable.ic_launcher_black_white);
				BitmapDrawable bmpDraw = new BitmapDrawable(is);
				Bitmap b = Util.convertToBlackWhite(bmpDraw.getBitmap());
				Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),_50dp, _50dp);
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
			}else{
				OfficeBanner
				.setImageResource(R.drawable.shop_office_banner);
			}
		}
	}
	private void addclass(){
		LayoutInflater infl=LayoutInflater.from(this);
		View view=infl.inflate(R.layout.add_album_classify, null);
		final EditText pwd=(EditText) view.findViewById(R.id.second_pwd);
		pwd.setFocusable(true);
		Button submit=(Button) view.findViewById(R.id.submit);
		Button cancel=(Button) view.findViewById(R.id.cancel);
		final Dialog ad = new Dialog(this,R.style.CustomDialogStyle);
		ad.show();
		Window window = ad.getWindow();
		WindowManager.LayoutParams pa=window.getAttributes();
		pa.width=Util.dpToPx(this, 250);
		window.setBackgroundDrawable(new ColorDrawable(0));  
		window.setContentView(view,pa);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Util.isNull(pwd.getText().toString())){
					Toast.makeText(ShopOfficeVedio.this, "请输入分类名称", Toast.LENGTH_LONG).show();
				}
				String id="";
				if(UserData.getOfficeInfo()!=null){
					id=UserData.getOfficeInfo().getOffice_id();
				}
				addArticleClassify(id, pwd.getText().toString(),"2");
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
	public void addArticleClassify(final String officeid,final String typename,final String parent){
		if(UserData.getUser()!=null){
			final User user=UserData.getUser();
			Util.asynTask(this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if(runData!=null){
						if("ok".equals(runData+"")){
						   Toast.makeText(ShopOfficeVedio.this, "添加分类成功", Toast.LENGTH_LONG).show();
						}
					}else{
						   Toast.makeText(ShopOfficeVedio.this, "添加分类失败", Toast.LENGTH_LONG).show();
					}
				}
				@Override
				public Serializable run() {
					Web web=new Web(Web.officeUrl, Web.AddOfficeArticleClass,"typename="+typename+"&userID="+user.getUserId()+"&userPaw="+user.getMd5Pwd()+"&parent="+parent);
					return web.getPlan();
				}
			});
		}else{
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
		View pview = getLayoutInflater().inflate(R.layout.shop_office_add_class_or_image,null, false);
		TextView add_class = (TextView) pview.findViewById(R.id.add_class);
		TextView add_article = (TextView) pview.findViewById(R.id.add_image);
		add_article.setText("添加文章视频");
		add_class.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//添加分类
				addclass();
				distancePopup.dismiss();
			}
		});
		add_article.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
				Util.showIntent(ShopOfficeVedio.this, OfficeAddVedioOrArticle.class);
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
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				adapter.clear();
				List<ShopOfficeVedioModel> l = new ArrayList<ShopOfficeVedioModel>();
				for (int i = 0; i < original.size(); i++) {
					if (original.get(i).getTitle()
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

	private void firstPage() {
		getVedios();
	}

	private void scrollPage() {
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
					getVedios();
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

	public void getVedios() {
		if (UserData.getOfficeInfo() != null) {
			Util.asynTask(this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if (runData != null) {
						HashMap<Integer, List<ShopOfficeVedioModel>> map = (HashMap<Integer, List<ShopOfficeVedioModel>>) runData;
						List<ShopOfficeVedioModel> list = map.get(page);
						if (list != null && list.size() > 0) {
							if (adapter == null) {
								adapter = new VedioAdapter(ShopOfficeVedio.this);
								adapter.setLayout(comment_layout);
								listView.setAdapter(adapter);
							}
							adapter.setList(list);
							original.addAll(list);
							adapter.notifyDataSetChanged();
						}
					} else {

					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.officeUrl, Web.GetVideoListPage,
							"officeid="
									+ UserData.getOfficeInfo().getOffice_id()
									+ "&cPage=" + (page++)
									+ "&flag=1&typeid=2&sec=1");
					List<ShopOfficeVedioModel> list = web
							.getList(ShopOfficeVedioModel.class);
					HashMap<Integer, List<ShopOfficeVedioModel>> map = new HashMap<Integer, List<ShopOfficeVedioModel>>();
					map.put(page, list);
					return map;
				}
			});
		}
	}
	private void initContainer(List<ShopOfficeCommentMoel> list,final LinearLayout container, final String artid) {
		container.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			if (i >= 10) {
				break;
			}
			LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.office_comment_message_item, null);
			TextView name = (TextView) layout.findViewById(R.id.username);
			TextView content = (TextView) layout.findViewById(R.id.content);
			SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(this, list.get(i).getContent());
			if (!Util.isNull(list.get(i).getContent())) {
				content.setText(spannableString);
			} else {
				content.setText("");
			}
			if (!Util.isNull(list.get(i).getUserid())) {
				name.setText(list.get(i).getUserid() + ":");
			} else {
				name.setText("");
			}
			final ShopOfficeCommentMoel ss = list.get(i);
			layout.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Util.showChoosedDialog(ShopOfficeVedio.this, "是否要删除该评论？",
							"点错了", "确定删除", new OnClickListener() {
								@Override
								public void onClick(View v) {
									deleteComment(ss.getId(), artid, container);
								}
							});
					return true;
				}
			});
			container.addView(layout);
		}
	}

	private void deleteComment(final String id, final String articleId,
			final LinearLayout container) {
		if (UserData.getUser() == null) {
			Util.showIntent(this, LoginFrame.class);
		}
		if (UserData.getOfficeInfo() != null) {
			if (UserData.getOfficeInfo().getUserid()
					.equals(UserData.getUser().getUserIdNoEncodByUTF8())) {
				Util.asynTask(this, "正在删除评论", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						String result = (String) runData;
						if (!Util.isNull(result)) {
							if (result.equals("ok")) {
								Toast.makeText(ShopOfficeVedio.this, "删除成功",
										Toast.LENGTH_LONG).show();
								getCommentById(articleId, container);
							}
						} else {
							Toast.makeText(ShopOfficeVedio.this, "删除失败",
									Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.officeUrl, Web.DelComment, "id="
								+ id + "&userID="
								+ UserData.getUser().getUserId() + "&userPaw="
								+ UserData.getUser().getMd5Pwd());
						return web.getPlan();
					}
				});
			}
		}
	}
	private void getCommentById(final String articleId,final LinearLayout container) {
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					HashMap<Integer, List<ShopOfficeCommentMoel>> map = (HashMap<Integer, List<ShopOfficeCommentMoel>>) runData;
					List<ShopOfficeCommentMoel> list = map.get(1);
					if (list != null && list.size() > 0) {
						System.out.println("-----------------http------getCommentById-------------"+list.size()+"articleId===="+articleId);
						initContainer(list, container, articleId);
					}
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.officeUrl, Web.GetCommentList,"cPage=1&articleid=" + articleId);
				List<ShopOfficeCommentMoel> list = web
						.getList(ShopOfficeCommentMoel.class);
				HashMap<Integer, List<ShopOfficeCommentMoel>> map = new HashMap<Integer, List<ShopOfficeCommentMoel>>();
				map.put(1, list);
				return map;
			}
		});
	}

	public class VedioAdapter extends BaseAdapter {
		private Context c;
		private List<ShopOfficeVedioModel> list = new ArrayList<ShopOfficeVedioModel>();
		private LayoutInflater inflater;
		private LinearLayout lay;
		private EditText message;
		private Button btn_send;
        private HashMap<Integer, View> map=new HashMap<Integer, View>();
		public VedioAdapter(Context c) {
			this.c = c;
			inflater = LayoutInflater.from(c);
		}

		public void setLayout(LinearLayout lay) {
			this.lay = lay;
			message = (EditText) lay.findViewById(R.id.et_sendmessage1);
			btn_send = (Button) lay.findViewById(R.id.btn_send);
		}

		public void setList(List<ShopOfficeVedioModel> list) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}

		private void clear() {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ShopOfficeVedioModel sp = this.list.get(position);
			ViewHolder h = null;
			if (map.get(position)==null) {
				convertView = inflater.inflate(R.layout.vedio_item, null);
				h = new ViewHolder();
				h.name = (TextView) convertView.findViewById(R.id.name);
				h.createtime = (TextView) convertView
						.findViewById(R.id.createtime);
				h.vedio_face = (ImageView) convertView
						.findViewById(R.id.vedio_face);
				h.office_favor = (TextView) convertView
						.findViewById(R.id.office_favor);
				h.office_comment = (TextView) convertView
						.findViewById(R.id.office_comment);
				h.office_share = (TextView) convertView
						.findViewById(R.id.office_share);
				h.container = (LinearLayout) convertView
						.findViewById(R.id.vedio_comment_contain);
				convertView.setTag(h);
				map.put(position, convertView);
			} else {
				convertView=map.get(position);
				h = (ViewHolder) convertView.getTag();  
			}    
			final ShopOfficeVedioModel smm = sp;
			h.office_favor.setText(sp.getGoodclicks() + "  ");
			h.name.setText("《" + sp.getTitle() + "》");
			final ImageView im = h.vedio_face;
			String urlimage = "";
			if (sp.getVdeioimgurl().contains("http://")) {
				urlimage = sp.getVdeioimgurl();
			} else {
				if(sp.getVdeioimgurl().contains("s_phone_"))
					urlimage = "http://" + Web.webImage + sp.getVdeioimgurl();
				else
					urlimage = "http://" + Web.webServer_Image + sp.getVdeioimgurl();
			}
			h.createtime.setText(Util.friendly_time(sp.getCreateTime()));
			bmUtils.display(h.vedio_face, urlimage,
					new BitmapLoadCallBack<View>() {
						@Override
						public void onLoadCompleted(View arg0, String arg1,
								Bitmap arg2, BitmapDisplayConfig arg3,
								BitmapLoadFrom arg4) {
							im.setImageBitmap(arg2);
						}
						@Override
						public void onLoadFailed(View arg0, String arg1,
								Drawable arg2) {
							im.setImageResource(R.drawable.no_get_image);
						}
			});
			h.container.removeAllViews();
			getCommentById(sp.getArticleid(), h.container);
			h.office_comment.setText(sp.getCOUNT_COMMENT() + "  ");
			final TextView t = h.office_comment;
			t.setTag(position + "");
			h.office_comment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final String artId = list.get(position).getArticleid();
					lay.setVisibility(View.VISIBLE);
					btn_send.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							addComment(artId);
						}
					});
				}
			});
			convertView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Util.showChoosedDialog(ShopOfficeVedio.this, "是否要删除该视频？",
							"点错了", "确定删除", new OnClickListener() {
								@Override
								public void onClick(View v) {
									deleteVedio(smm.getOfficeid(),
											smm.getArticleid());
								}
							});
					return true;
				}
			});
			final String content = sp.getContent();
			h.vedio_face.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (content.contains("src")) {
						Intent intent = new Intent(c, VedioWeb.class);
						intent.putExtra("src", getVedioSrc(content));
						c.startActivity(intent);
					}else{
						Toast.makeText(c, "没有视频连接", Toast.LENGTH_LONG).show();
					}

				}
			});
			h.office_favor.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					TextView t = (TextView) v;
					int i = Integer.parseInt(t.getText().toString().trim());
					GoodClick(smm.getArticleid(), t, i);
				}
			});
			final ShopOfficeVedioModel ssp=sp;
			h.office_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.e("分享点击成功","点击");
					TextView t = (TextView) v;
					String url="";
					Log.e("分享信息",Web.webImage);
					if(Web.webImage.contains("test")){
						url="http://"+UserData.getOfficeInfo().getDomainstr()+".mall666.com/user/office/myArtdetail.aspx?unum="+UserData.getOfficeInfo().getUserNo()+"&articleid="+ssp.getArticleid()+"&typeid=2";
					}else if(Web.webImage.contains("appgd.yda360.com")){
						url="http://"+UserData.getOfficeInfo().getDomainstr()+".yda360.com/user/office/myArtdetail.aspx?unum="+UserData.getOfficeInfo().getUserNo()+"&articleid="+ssp.getArticleid()+"&typeid=2";
					}
					Share(c, smm.getTitle(), url, "http://"+ Web.webImage + smm.getVdeioimgurl(), t);
				}
			});
			return convertView;
		}

		private void addComment(final String articleId) {
			if (UserData.getUser() == null) {
				Util.showIntent(c, LoginFrame.class);
			}
			if (!Util.isNull(message.getText().toString())) {
				Util.asynTask(c, "", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						String result = (String) runData;
						if (!Util.isNull(result)) {
							if (result.equals("ok")) {
								Toast.makeText(c, "评论成功", Toast.LENGTH_LONG).show();
								lay.setVisibility(View.GONE);
								clear();
								notifyDataSetChanged();
								firstPage();
							}
						} else {
							Toast.makeText(c, "评论提交失败", Toast.LENGTH_LONG)
									.show();
						}
					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.officeUrl, Web.AddComment,
								"articleid=" + articleId + "&text="
										+ message.getText().toString()
										+ "&userID="
										+ UserData.getUser().getUserId()
										+ "&userPaw="
										+ UserData.getUser().getMd5Pwd());
						return web.getPlan();
					}
				});
			} else {
				Toast.makeText(c, "评论内容不能为空", Toast.LENGTH_LONG).show();
			}
		}

		private void GoodClick(final String id, final TextView t,
				final int previous) {
			Util.asynTaskTwo(ShopOfficeVedio.this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if ("ok".equals(runData + "")) {
						int num = previous + 1;
						t.setText(num + " ");
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

		private void Share(Context c, String content,  final String url,
				String imageurl, final TextView t) {
//			String url1="http://a.app.qq.com/o/simple.jsp?pkgname=com.mall.view";
			LogUtils.e("url="+url+"          imageurl="+imageurl);
			OnekeyShare oks = new OnekeyShare();
			oks.setTitle("网上空间视频分享");
			oks.setAddress("400-666-3838");
			oks.setTitleUrl(url);
			oks.setText(content + "...");
			oks.setUrl(url);
			oks.setImageUrl("http://app.yda360.com/phone/images/ic_launcher1.png?r=2");
			oks.setComment(c.getString(R.string.share));
			oks.setSite(c.getString(R.string.app_name));
			oks.setSiteUrl(url);
			oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
				@Override
				public void onShare(Platform platform,
						Platform.ShareParams paramsToShare) {
					if ("ShortMessage".equals(platform.getName())) {
						paramsToShare.setImageUrl(null);
						paramsToShare.setText(paramsToShare.getText()+"\n"+url.toString());
					}
				}
			});
			Platform[] plant = ShareSDK.getPlatformList();
			for (int i = 0; i < plant.length; i++) {
				Platform p = plant[i];
				p.setPlatformActionListener(new PlatformActionListener() {
					@Override
					public void onError(Platform arg0, int arg1, Throwable arg2) {

					}

					@Override
					public void onComplete(Platform arg0, int arg1,
							HashMap<String, Object> arg2) {
						int i = Integer.parseInt(t.getText().toString());
						t.setText((i + 1) + "");
					}

					@Override
					public void onCancel(Platform arg0, int arg1) {

					}
				});
			}
			oks.show(c);

		}

		private String getVedioSrc(String content) {
			int start = 0;
			int end = 1;
			try {
				String src = "";
				start = content.indexOf("src=\"") + 5;
				end = content.indexOf("width") - 2;
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
			return content.substring(start, end);
		}

		private void deleteVedio(String officeId, final String artId) {
			if (UserData.getUser() == null) {
				Util.showIntent(c, LoginFrame.class);
			}
			if (UserData.getOfficeInfo() != null) {
				System.out
						.println("-------------deleteVedio-------officeId---------------"
								+ UserData.getOfficeInfo().getOffice_id()
								+ officeId);
				// if(UserData.getOfficeInfo().getOffice_id().equals(officeId)){
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
								firstPage();
							}
						} else {
							Toast.makeText(c, "删除失败", Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.officeUrl,
								Web.DeleteOfficeUserArticle, "userID="
										+ UserData.getUser().getUserId()
										+ "&userPaw="
										+ new UserData().getUser().getMd5Pwd()
										+ "&articleid=" + artId);
						return web.getPlan();
					}
				});
				// }
			}
		}
	}

	public class ViewHolder {
		TextView name;
		TextView createtime;
		ImageView vedio_face;
		TextView office_favor;
		TextView office_comment;
		TextView office_share;
		LinearLayout container;
	}
}
