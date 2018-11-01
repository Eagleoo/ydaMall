package com.bpj.lazyfragment;


import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

import com.bpj.lazyfragment.Fragment1.OnArticleSelectedListener;
import com.bpj.lazyfragment.Fragment2.OnArticleSeachListener;
import com.bpj.lazyfragment.Fragment3.OnArticleFragment3SeachListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.AlbumClassifyModel;
import com.mall.model.ShopOfficeArticleModel;
import com.mall.model.ShopOfficeInfo;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.officeonline.AlbumList;
import com.mall.officeonline.OfficeAddVedioOrArticle;
import com.mall.officeonline.ShopOfficeArticle;
import com.mall.officeonline.ShopOfficeConfig;
import com.mall.officeonline.ShopOfficeFrame;
import com.mall.officeonline.ShopOfficeOrder;
import com.mall.officeonline.ShopOfficeVedio;
import com.mall.officeonline.ShopOfficeFrame.ItemAdapter;


import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.BMFWFrane;
import com.mall.view.LoginFrame;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.StoreFrame;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;




public class MainActivity extends FragmentActivity 
implements OnClickListener,OnArticleSelectedListener,OnArticleSeachListener,OnArticleFragment3SeachListener{


	@ViewInject(R.id.rl)
	private RelativeLayout rl;
	private List<ShopOfficeArticleModel> original = new ArrayList<ShopOfficeArticleModel>();
	private FragmentAdapter mFragmentAdapter;
	private Fragment[] mFragments;
	private Context mContext;
	Fragment F1,F2,F3;

	//便民
	@ViewInject(R.id.bmLayout)
	private LinearLayout bmLayout;
	//相册
	@ViewInject(R.id.albumLayout)
	private LinearLayout albumLayout;
	//视频
	@ViewInject(R.id.vedioLayout)
	private LinearLayout vedioLayout;
	//日志
	@ViewInject(R.id.notesLayout)
	private LinearLayout notesLayout;
	//宝贝
	@ViewInject(R.id.baobei_layout)
	private LinearLayout baobei_layout;


	//头部布局
	@ViewInject(R.id.top1)
	private View top1;
	//返回
	@ViewInject(R.id.topback)
	private ImageView topback;
	//中间文字
	@ViewInject(R.id.topCenter)
	private TextView topCenter;
	//搜索
	@ViewInject(R.id.rightone)
	private ImageView rightone;
	//添加
	@ViewInject(R.id.righttwo)
	private ImageView righttwo;
	//三白点
	@ViewInject(R.id.morewhite)
	private ImageView morewhite;
	
	
	
	private BitmapUtils bmUtils;
	@ViewInject(R.id.layer)
	private LinearLayout layer;
	//头像
	@ViewInject(R.id.logo)
	private ImageView OfficeLogo;
	//用户名
	@ViewInject(R.id.name)
	private TextView userName;
	//访客
	@ViewInject(R.id.visitors)
	private TextView Visitors;
	//
	@ViewInject(R.id.gridview)
	private GridView Gridv;
	//宝贝数量
	@ViewInject(R.id.baobei)
	private TextView BaoBei;
	//日志数量
	@ViewInject(R.id.notes)
	private TextView Notes;
	//视频数量
	@ViewInject(R.id.vedios)
	private TextView Vedios;
	//相册数量
	@ViewInject(R.id.album)
	private TextView Albums;
	//广告
	@ViewInject(R.id.office_banner)
	private ImageView OfficeBanner;
	//心情排序
	@ViewInject(R.id.rank_container)
	private LinearLayout rank_container;
	
	@ViewInject(R.id.store)
	private TextView store;
	
	@ViewInject(R.id.connect)
	private TextView connect;
	
	private int page = 1;
	public static final int PAGE_SIZE = 10;
	public ItemAdapter adapter;
	private User user;
	public static ShopOfficeInfo shopoffice;
	private int _50dp = 50;
	private PopupWindow distancePopup = null;
	private String userNo = "";
	private String names = "";
	private int crown = 0, sun = 0, moon = 0, star = 0;
	@ViewInject(R.id.topright)
	private ImageView topright;
	private String officeid = "";
	private String from = "";
	private String officeUserNo = "";
	private String id = "";
	private String my = "";
	
	private String titletop="";
	
	
	@OnClick(R.id.store)
	public void Store(View v) {
		store(id);
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		mContext=MainActivity.this;
		init();
		F1=Fragment1.newInstance();
		F2=Fragment2.newInstance();
		F3=Fragment3.newInstance();
//		mFragments = new Fragment[]{new BabyFragment(),Fragment2.newInstance(),Fragment3.newInstance()};
		mFragments = new Fragment[]{F1,F2,F3};
//		mFragments = new Fragment[]{new BabyFragment(),new LogFragment(),new VideoFragment()};
		mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),mFragments);
	
		
		rl = (RelativeLayout) findViewById(R.id.rl);
	
		setFragment(rl,0);
	
	}
	
	private void store(final String officesId) {
		if (UserData.getUser() != null) {
			final User user = UserData.getUser();
			Util.asynTask(MainActivity.this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if (runData != null) {
						if ("success".equals(runData + "")) {
							Toast.makeText(MainActivity.this, "收藏成功",
									Toast.LENGTH_LONG).show();
						}
					} else {
					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.officeUrl, Web.favoriteOffices,
							"userId=" + user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&officesId="
									+ officesId);
					return web.getPlan();
				}
			});
		} else {
			Util.showIntent(this, LoginFrame.class);
		}
	}
	
	private void initop(){
		top1.setBackgroundColor(Color.RED);
		topback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
	
			}
		});

		if (titletop.equals("")) {
			titletop="我的空间";
		}
		topCenter.setText(titletop);
		rightone.setVisibility(View.GONE);
		righttwo.setVisibility(View.GONE);
	

		morewhite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.e("三点","点击");
				getPopupWindow();
		
				startPopupWindow();
				distancePopup.showAsDropDown(arg0);
			}
		});
	
	}
	
	private void init(){
	
		getIntentmethods();
		initListening();//设置监听
		initData();  //获取页面数据
	}
	
	private void getIntentmethods() {
		// TODO Auto-generated method stub
		userNo = this.getIntent().getStringExtra("userNo");
		my = this.getIntent().getStringExtra("my");
		if (TextUtils.isEmpty(my)) {
			crown = this.getIntent().getIntExtra("crown", 0);
			sun = this.getIntent().getIntExtra("sun", 0);
			moon = this.getIntent().getIntExtra("moon", 0);
			star = this.getIntent().getIntExtra("star", 0);
			names = this.getIntent().getStringExtra("name");
			officeid = this.getIntent().getStringExtra("offid");
			from = this.getIntent().getStringExtra("from");
			
			id = this.getIntent().getStringExtra("officeid");
		}
	
	}
	
	private void initData(){
		
	
		bmUtils = new BitmapUtils(this);
		

		
//		Util.initTop(this, names, Integer.MIN_VALUE, new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//
//		}, null);
		
		if (UserData.getUser() != null) {
			user = UserData.getUser();
		} else {
			// Util.showIntent(this, LoginFrame.class);
		}
		_50dp = Util.pxToDp(this, 150);
		layer.getBackground().setAlpha(150);
		if (!Util.isNull(names)) {
			if (names.length() > 6) {
				names = names.substring(0, 6) + "...";
			}
			userName.setText(names.replace("_p", ""));
		} else {
			userName.setText("");
		}
		if (!Util.isNull(from) && from.equals("list")) {
			if (UserData.getOfficeInfo() != null) {
				initOffice(UserData.getOfficeInfo());
			} else {
				getShopOfficeInfo();
			}
		} else {
			getShopOfficeInfo();
		}
	}
	
	private void getShopOfficeInfo() {
		Util.asynTask(this, "正在获取空间信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null&&shopoffice!=null) {
					UserData.setOfficeInfo(shopoffice);
					userNo=shopoffice.getUserNo();
					titletop=shopoffice.getOfficename();
					topCenter.setText(titletop);
					initOffice(shopoffice);
				} else {
					Toast.makeText(MainActivity.this, "未获取到空间数据",
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = null;
				if (Util.isNull(userNo)) {
					web = new Web(Web.officeUrl, Web.GetOfficeInfo, "unum="
							+ user.getUserNo());
				} else {
					web = new Web(Web.officeUrl, Web.GetOfficeInfo, "unum="
							+ userNo);
				}
				shopoffice = web.getObject(ShopOfficeInfo.class);
				return shopoffice;
			}
		});
	}
	
	@OnClick(R.id.connect)
	public void PhoneClick(final View v) {
		Util.doPhone(Util._400, this);
	}

//	@OnClick(R.id.albumLayout)
//	public void AlbumClick(final View v) {
//		Intent intent = new Intent(this, AlbumList.class);
//		intent.putExtra("offid", officeid);
//		this.startActivity(intent);
//	}

	@OnClick(R.id.logo)
	public void LogoClick(final View v) {
		if (UserData.getUser() != null && UserData.getOfficeInfo() != null) {
			System.out.println("UserData.getUser().getUserIdNoEncodByUTF8()=="
					+ UserData.getUser().getUserIdNoEncodByUTF8()
					+ "UserData.getOfficeInfo().getUserid()=="
					+ UserData.getOfficeInfo().getUserid());
			if (UserData.getUser().getUserIdNoEncodByUTF8().equals(officeid)) {
				Util.showIntent(MainActivity.this, ShopOfficeConfig.class);
			}
		}
	}

//	@OnClick(R.id.notesLayout)
//	public void NotesClick(final View v) {
//		Intent intent = new Intent(this, ShopOfficeArticle.class);
//		intent.putExtra("offid", officeid);
//		intent.putExtra("crown", crown);
//		intent.putExtra("sun", sun);
//		intent.putExtra("moon", moon);
//		intent.putExtra("star", star);
//		intent.putExtra("Albums", Albums.getText().toString());
//		intent.putExtra("officeid", shopoffice.getOffice_id());
//		intent.putExtra("userNo", userNo);
//		this.startActivity(intent);
//	}

	@OnClick(R.id.vedioLayout)
	public void VedioClick(final View v) {
		Intent intent = new Intent(this, ShopOfficeVedio.class);
		intent.putExtra("offid", officeid);
		intent.putExtra("crown", crown);
		intent.putExtra("sun", sun);
		intent.putExtra("moon", moon);
		intent.putExtra("star", star);
		intent.putExtra("Albums", Albums.getText().toString());
		intent.putExtra("userNo", userNo);
		this.startActivity(intent);
	}

	@OnClick(R.id.bmLayout)
	public void BmClick(final View v) {
		Intent intent = new Intent();
		intent.setClass(this, BMFWFrane.class);
		intent.putExtra("unum", userNo);
		this.startActivity(intent);
	}
	
	private void initOffice(ShopOfficeInfo shopOffice) {
		initTop();
		
	
		
		if (Util.isNull(shopOffice)) {
			return;
		}
		
		if (!Util.isNull(shopOffice.getOfficename())) {
			Log.e("名字",shopOffice.getOfficename()+"");
			String s=shopOffice.getOfficename();
					if (shopOffice.getOfficename().length() > 6) {
						 s = s.substring(0, 6) + "...";
			}
					userName.setText(s);
		}else{
			names="我的空间";
			userName.setText(names);
		}
		
		if (!Util.isNull(shopoffice.getClicks())) {
			Visitors.setText("访客：" + shopoffice.getClicks());
		} else {
			Visitors.setText("");
		}
		if (!Util.isNull(shopoffice.getProductcount())) {
			BaoBei.setText(shopoffice.getProductcount());
		} else {
			BaoBei.setText("0");
		}
		officeUserNo = shopOffice.getUserNo();
		if (!Util.isNull(shopoffice.getVdieo_count())) {
			Vedios.setText(shopoffice.getVdieo_count());
		}
		if (!Util.isNull(shopoffice.getArt_count())) {
			Notes.setText(shopoffice.getArt_count());
		}
		if (!Util.isNull(shopoffice.getArt_count())) {
			Albums.setText(shopOffice.getArt_count());
		}
	

		if (!Util.isNull(shopoffice.getImgLogo())) {
			String logourl = "";
			if (shopoffice.getImgLogo1().contains("http")) {
				logourl = shopoffice.getImgLogo();
			} else {
				logourl = "http://" + Web.webImage + shopoffice.getImgLogo();
			}
			bmUtils.display(OfficeLogo, logourl,
					new DefaultBitmapLoadCallBack<View>() {
						@Override
						public void onLoadCompleted(View container, String uri,
								Bitmap bitmap, BitmapDisplayConfig config,
								BitmapLoadFrom from) {
							Bitmap zoomBm = Util.zoomBitmap(bitmap, _50dp,
									_50dp);
							super.onLoadCompleted(container, uri,
									Util.toRoundCorner(zoomBm, 5), config, from);
						}

						@Override
						public void onLoadFailed(View container, String uri,
								Drawable drawable) {
							Resources r = MainActivity.this.getResources();
							InputStream is = r
									.openRawResource(R.drawable.ic_launcher_black_white);
							BitmapDrawable bmpDraw = new BitmapDrawable(is);
							Bitmap zoomBm = Util.zoomBitmap(
									bmpDraw.getBitmap(), _50dp, _50dp);
							OfficeLogo.setImageBitmap(Util.toRoundCorner(
									zoomBm, 5));
						}
					});
		} else {
			Resources r = MainActivity.this.getResources();
			InputStream is = r
					.openRawResource(R.drawable.ic_launcher_black_white);
			BitmapDrawable bmpDraw = new BitmapDrawable(is);
			Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(), _50dp, _50dp);
			OfficeLogo.setImageBitmap(Util.toRoundCorner(zoomBm, 5));
		}
		String url = "";
		if (shopoffice.getImgLogo1().contains("http")) {
			url = shopoffice.getImgLogo1();
		} else {
			url = "http://" + Web.webImage + shopoffice.getImgLogo1();
		}
		bmUtils.display(OfficeBanner, url,
				new DefaultBitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View container, String uri,
							Bitmap bitmap, BitmapDisplayConfig config,
							BitmapLoadFrom from) {
						super.onLoadCompleted(container, uri, bitmap, config,
								from);
					}

					@Override
					public void onLoadFailed(View container, String uri,
							Drawable drawable) {
						OfficeBanner
								.setImageResource(R.drawable.shop_office_banner);
					}
				});
		if (crown == 0 && sun == 0 && moon == 0 && star == 0) {
			if (!Util.isNull(shopOffice.getCrown())) {
				crown = Integer.parseInt(shopOffice.getCrown());
			}
			if (!Util.isNull(shopOffice.getSun())) {
				sun = Integer.parseInt(shopOffice.getSun());
			}
			if (!Util.isNull(shopOffice.getMoon())) {
				moon = Integer.parseInt(shopOffice.getMoon());
			}
			if (!Util.isNull(shopOffice.getStar())) {
				star = Integer.parseInt(shopOffice.getStar());
			}

		}
		clacluate(rank_container, this, crown, sun, moon, star);
		getClassifyId();
		
		onArticleSelected((Fragment1) F1,shopOffice);
		
		

	}
	
	private void getClassifyId() {  //获取相册数量
		if (UserData.getOfficeInfo() != null) {
			Util.asynTask(this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if (runData != null) {
						HashMap<Integer, List<AlbumClassifyModel>> map = (HashMap<Integer, List<AlbumClassifyModel>>) runData;
						List<AlbumClassifyModel> list = map.get(1);
						if (list != null && list.size() > 0) {
							Albums.setText(list.size() + "");
						}
					} else {
					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.officeUrl, Web.GetOfficePhotoClass,
							"officeid="
									+ UserData.getOfficeInfo().getOffice_id());
					List<AlbumClassifyModel> list = web
							.getList(AlbumClassifyModel.class);
					HashMap<Integer, List<AlbumClassifyModel>> map = new HashMap<Integer, List<AlbumClassifyModel>>();
					map.put(1, list);
					return map;
				}
			});
		}
	}
	


	public static void clacluate(LinearLayout Layout, Context c, int crown,
			int sun, int moon, int star) {
		int size = crown + sun + moon + star;
		int[] ione = new int[size];
		for (int i = 0; i < crown; i++) {
			ione[i] = R.drawable.hg;
		}
		for (int i = 0; i < sun; i++) {
			ione[i + crown] = R.drawable.hg;
		}
		for (int i = 0; i < moon; i++) {
			ione[i + crown + sun] = R.drawable.diamond;
		}
		for (int i = 0; i < star; i++) {
			ione[i + crown + sun + moon] = R.drawable.red_heart;
		}
		addIndexImage(Layout, ione);
	}
	private static void addIndexImage(LinearLayout Layout, int[] res) {
		Layout.removeAllViews();
		LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		l.setMargins(0, 0, 0, 0);
		int[] rank = res;
		for (int i = 0; i < rank.length; i++) {
			ImageView img = new ImageView(App.getContext());
			img.setLayoutParams(l);
			img.setImageResource(rank[i]);
			Layout.addView(img);
		}
	}
	
	private void initTop() {
		String title = "";
		if (UserData.getUser() != null && UserData.getOfficeInfo() != null) {// 未登录
			if (!Util.isNull(UserData.getOfficeInfo().getOfficename())) {
				title = UserData.getOfficeInfo().getOfficename();
			} else {
				title = "创业空间";
			}
			if (shopoffice!=null) {
				officeid = shopoffice.getUserid();
			}
			
			if (!UserData.getUser().getUserIdNoEncodByUTF8().equals(officeid)) {
				
			} else {
				initTopTwo(title);
			}
		} 
	}
	
	private void initTopTwo(String title) {
//		Util.initTop(this, title, R.drawable.morewhite, new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (UserData.getUser() != null
//						&& UserData.getOfficeInfo() != null) {
//					if (UserData.getUser().getUserIdNoEncodByUTF8()
//							.equals(UserData.getOfficeInfo().getUserid())) {
//						getPopupWindow();
//						startPopupWindow();
//						distancePopup.showAsDropDown(v);
//					}
//				}
//			}
//		});
	}
	
	
	/*
	 * 搜索弹窗
	 */
	private void seachPopupWindow(final int typ) {// typ:1 视频，2日志
		View pview = getLayoutInflater().inflate(R.layout.search_dialog, null,
				false);
		final EditText search_e = (EditText) pview
				.findViewById(R.id.search_edit);
		TextView search_text = (TextView) pview.findViewById(R.id.search_text);
		search_text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				adapter.clear();
//				List<ShopOfficeArticleModel> l = new ArrayList<ShopOfficeArticleModel>();
//				for (int i = 0; i < original.size(); i++) {
//					if (original.get(i).getContent()
//							.contains(search_e.getText().toString())) {
//						l.add(original.get(i));
//					}
//				}
//				if (l.size() > 0) {
//					adapter.setList(l);
//					adapter.notifyDataSetChanged();
//				}
//				searched = true;
				if (typ==2) {
					onArticleSelected((Fragment2)F2,search_e.getText().toString());
				}else{
					onArticleFragment3Selected((Fragment3)F3,search_e.getText().toString());
				}
		
			
			
				distancePopup.dismiss();
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
		View pview = getLayoutInflater().inflate(
				R.layout.shop_office_configuration, null, false);
		TextView office_share = (TextView) pview
				.findViewById(R.id.office_share);
		TextView office_order = (TextView) pview
				.findViewById(R.id.office_order);
		TextView office_config = (TextView) pview
				.findViewById(R.id.office_config);
		TextView add_baobei = (TextView) pview.findViewById(R.id.add_baobei);

		office_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
				officeShare();
			}
		});
		add_baobei.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
				Util.showIntent(MainActivity.this, StoreFrame.class);
			}
		});
		office_order.setVisibility(View.GONE);
		office_order.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
				// 跳转到Order页面
				Util.showIntent(MainActivity.this, ShopOfficeOrder.class);
			}
		});
		office_config.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
				Util.showIntent(MainActivity.this, ShopOfficeConfig.class);
			}
		});
		initpoputwindow(pview);
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
	
	private void officeShare() {
		if (UserData.getUser() == null) {
			Util.showIntent(this, LoginFrame.class);
		}
		if (UserData.getOfficeInfo() == null) {
			return;
		}
		final String url;
		String s = Web.webServer_Image.replace("test", UserData.getOfficeInfo()
				.getDomainstr());
		s = s.replace("www", UserData.getOfficeInfo().getDomainstr());
		url = "http://" + s + "/user/office/myOffices.aspx?unum="
				+ UserData.getOfficeInfo().getUserNo();
		String logourl = "";
		if (!Util.isNull(shopoffice.getImgLogo1())) {
			if (shopoffice.getImgLogo1().contains("http")) {
				logourl = shopoffice.getImgLogo();
			} else {
				logourl = "http://" + Web.webServer_Image + shopoffice.getImgLogo();
			}
		} else {
			logourl = UserData.getUser().getUserFace();
		}
		User user = UserData.getUser();
		if (user != null) {
			if ("6".equals(user.getLevelId())) {
				Util.show("对不起，请登录您的城市总监账号在进行此操作！", this);
				return;
			}
			if (Util.getInt(user.getShopTypeId()) >= 3) {
				final OnekeyShare oks = new OnekeyShare();
				final String title = "创业空间分享-远大云商";
				oks.setTitle(title);
				oks.setTitleUrl(url);
				oks.setUrl(url);
				oks.setAddress("10086");
				oks.setComment("快来注册吧");
				oks.setText("hi，您想低成本创业赚钱吗？我在此向您郑重推荐中国首家移动电商第三方创业服务平台〔远大云商〕。点击本链接"
						+ "http://"
						+ Web.webServer_Image
						+ "/user/office/myOffices.aspx?unum="
						+ user.getUserNo()
						+ "。可直接进入我的创业空间了解项目、参观购物。中国已进入4G时代，让您的手机也变成获取财富的工具吧。咨询电话："
						+ Util._400);

				oks.setSite("远大云商");
				oks.setSiteUrl("http://" + Web.webServer_Image
						+ "/Shop/ShopSite.aspx?unum=" + user.getUserNo());
				oks.setSilent(false);
				oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
					@Override
					public void onShare(Platform platform,
							ShareParams paramsToShare) {
						if ("ShortMessage".equals(platform.getName())) {
							paramsToShare.setImageUrl(null);
							paramsToShare.setText(paramsToShare.getText()+"\n"+url.toString());
						}

					}
				});
				oks.show(this);
			} else {
				Util.showIntent("对不起，您还没有创业空间，您可以前去申请创业空间!", this, "去申请",
						"再逛逛", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Util.showIntent(MainActivity.this,
										ProxySiteFrame.class);
								dialog.cancel();
								dialog.dismiss();
							}
						}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								dialog.dismiss();
							}
						});
			}
		} else {
			Util.showIntent("您还没有登录，要先去登录吗？", this, LoginFrame.class);
		}
	}
	
	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}

	private void setFragment(FrameLayout fr2,int position ){
		Fragment fragment = (Fragment) mFragmentAdapter.instantiateItem(fr2, position);
		mFragmentAdapter.setPrimaryItem(fr2, 0, fragment);
		mFragmentAdapter.finishUpdate(fr2);
		
	}
	

	private void initListening(){
		initop();
		baobei_layout.setOnClickListener(this);
		notesLayout.setOnClickListener(this);
		vedioLayout.setOnClickListener(this);
		albumLayout.setOnClickListener(this);
		bmLayout.setOnClickListener(this);
	}
	

	private void setFragment(RelativeLayout rl,int position ){
		Fragment fragment = (Fragment) mFragmentAdapter.instantiateItem(rl, position);
		mFragmentAdapter.setPrimaryItem(rl, 0, fragment);
		mFragmentAdapter.finishUpdate(rl);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.baobei_layout:
		
			setFragment(rl,0);
			initop();
			store.setVisibility(View.INVISIBLE);
			connect.setVisibility(View.VISIBLE);
			break;
case R.id.notesLayout:

	
	store.setVisibility(View.INVISIBLE);
	top1.setBackgroundColor(Color.rgb(70, 168, 231));
	topback.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();

		}
	});

	topCenter.setText("日志");
	rightone.setVisibility(View.VISIBLE);
	rightone.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			getPopupWindow();
			seachPopupWindow(2);
			distancePopup.showAsDropDown(arg0);
		}
	});
	righttwo.setVisibility(View.VISIBLE);
	righttwo.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (UserData.getUser() != null
					&& UserData.getOfficeInfo() != null) {
				if (UserData.getUser().getUserIdNoEncodByUTF8()
						.equals(officeid)) {
					getPopupWindow();
					startAddClassPopupWindow();
					distancePopup.showAsDropDown(arg0);
				}
			}
		}
	});
	morewhite.setVisibility(View.GONE);



	
//	Util.initTopWithTwoButton(this, "日志", R.drawable.vedio_search,
//			R.drawable.add_article, new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			// 搜索功能
//			getPopupWindow();
//			startPopupWindow();
//			distancePopup.showAsDropDown(v);
//		}
//	}, new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			if (UserData.getUser() != null
//					&& UserData.getOfficeInfo() != null) {
//				if (UserData.getUser().getUserIdNoEncodByUTF8()
//						.equals(officeid)) {
//					getPopupWindow();
//					startAddClassPopupWindow();
//					distancePopup.showAsDropDown(v);
//				}
//			}
//		}
//	}, new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			MainActivity.this.finish();
//		}
//	});
	setFragment(rl,1);
			break;
case R.id.vedioLayout:

	setFragment(rl,2);
	store.setVisibility(View.INVISIBLE);
	top1.setBackgroundColor(Color.rgb(70, 168, 231));
	topback.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
			Toast.makeText(MainActivity.this, "返回", Toast.LENGTH_SHORT).show();
		}
	});

	topCenter.setText("视频");
	rightone.setVisibility(View.VISIBLE);
	rightone.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			getPopupWindow();
			seachPopupWindow(1);
			distancePopup.showAsDropDown(arg0);
		}
	});
	righttwo.setVisibility(View.VISIBLE);
	righttwo.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (UserData.getUser() != null
					&& UserData.getOfficeInfo() != null) {
				if (UserData.getUser().getUserIdNoEncodByUTF8()
						.equals(officeid)) {
					getPopupWindow();
					startAddClassPopupWindow();
					distancePopup.showAsDropDown(arg0);
				}
			}
		}
	});
	morewhite.setVisibility(View.GONE);
	break;
case R.id.albumLayout:
	Intent intent = new Intent(this, AlbumList.class);
	intent.putExtra("offid", officeid);
	Log.e("相册",officeid+"");
	this.startActivityForResult(intent, 100);
	break;
case R.id.bmLayout:

	Intent intent1 = new Intent();
	intent1.setClass(this, BMFWFrane.class);
	intent1.putExtra("unum", userNo);
	Log.e("便民",userNo+"");
	this.startActivity(intent1);
	break;

		default:
			break;
		}
	}



	@Override
	public void onArticleSelected(Fragment1 fragment1, ShopOfficeInfo shopoffice) {
		// TODO Auto-generated method stub
		fragment1.getProduct(shopoffice);
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
				Util.showIntent(MainActivity.this,
						OfficeAddVedioOrArticle.class,new String[]{"article"},new String[]{"article"});
			}
		});
		initpoputwindow(pview);
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
					Toast.makeText(MainActivity.this, "请输入分类名称",
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
							Toast.makeText(MainActivity.this, "添加分类成功",
									Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(MainActivity.this, "添加分类失败",
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

	@Override
	public void onArticleSelected(Fragment2 fragment2, String string) {
		// TODO Auto-generated method stub
		fragment2.seachadapter(string);
		
	}

	@Override
	public void onArticleFragment3Selected(Fragment3 fragment3, String string) {
		// TODO Auto-generated method stub
		fragment3.seachadapter(string);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		Log.e("调用","onActivityResult");
		switch(arg0){
		case 100:
			getClassifyId();
			break;
		
		}
	}


}
