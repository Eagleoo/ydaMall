package com.mall.card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.card.adapter.MyScrollView;
import com.mall.card.adapter.PluginScrollView;
import com.mall.card.adapter.ViewPagerAdapter;
import com.mall.card.bean.CallBackListenerForViewPager;
import com.mall.card.bean.CardExchangeRequest;
import com.mall.card.bean.CardGrouping;
import com.mall.card.bean.CardLinkman;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;

/**
 * 名片夹
 * 
 * @author admin
 * 
 */
public class CardPhonekook extends Activity implements
		CallBackListenerForViewPager {
	// 管理
	@ViewInject(R.id.manage)
	private TextView manage;
	@ViewInject(R.id.guanli_lin)
	private TextView guanli_lin;
	// 全部
	@ViewInject(R.id.all)
	private TextView all;
	@ViewInject(R.id.quanbu_lin)
	private TextView quanbu_lin;
	// 联系人
	@ViewInject(R.id.contacts)
	private TextView contacts;
	@ViewInject(R.id.lianxi_lin)
	private TextView lianxi_lin;
	// 客户
	@ViewInject(R.id.clientele)
	private TextView clientele;
	@ViewInject(R.id.kehu_lin)
	private TextView kehu_lin;
	@ViewInject(R.id.xiaoxi_count)
	private TextView xiaoxi_count;
	private String[] arr;
	private MyScrollView myView;// 自定义的滑动view
	private LinearLayout sortliner;// 滑动条
	private List<View> sortTextViews = new ArrayList<View>();// 显示二级种类的TextView
	private PopupWindow distancePopup = null;
	private TextView[] textViews;
	private TextView[] textViews2;
	@ViewInject(R.id.listView1)
	private ListView listView1;
	@ViewInject(R.id.sousuo)
	private EditText sousuo;
	List<View> testList;
	private int width;
	// 所有的名片信息
	private List<CardLinkman> cardLinkmans = new ArrayList<CardLinkman>();
	// 所有的名片分组
	private List<CardGrouping> cardGroupings = new ArrayList<CardGrouping>();
	private Dialog dialog;
	@ViewInject(R.id.xiaoxi_re)
	private RelativeLayout xiaoxi_re;
	private int sousuoState = 0;
	private PluginScrollView mPluginScrollView;
	private ViewPager viewPager;
	private ViewPagerAdapter viewPagerAdapter;

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow() {
		View pview = getLayoutInflater().inflate(R.layout.card_manage, null,
				false);
		TextView listview = (TextView) pview.findViewById(R.id.listview);

		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) listview
				.getLayoutParams(); // 取控件textView当前的布局参数
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		linearParams.width = dm.widthPixels;
		linearParams.height = dm.heightPixels;
		listview.getLayoutParams();
		TextView add_new_card = (TextView) pview
				.findViewById(R.id.add_new_card);
		TextView fenzu = (TextView) pview.findViewById(R.id.fenzu);
		TextView wei_fenzu = (TextView) pview.findViewById(R.id.wei_fenzu);
		TextView no_queren = (TextView) pview.findViewById(R.id.no_queren);
		TextView daoru_card = (TextView) pview.findViewById(R.id.daoru_card);
		TextView close=(TextView) pview.findViewById(R.id.close);
		listview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				distancePopup.dismiss();
			}
		});
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				distancePopup.dismiss();
			}
		});
		daoru_card.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CardPhonekook.this,
						ContactsActivity.class);
				startActivityForResult(intent, 2);
				distancePopup.dismiss();
			}
		});
		no_queren.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Util.showIntent(CardPhonekook.this,
						CardExchangeCardRequest.class);
				distancePopup.dismiss();
			}
		});
		wei_fenzu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getMyCards("", "1");
				distancePopup.dismiss();
			}
		});
		fenzu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CardPhonekook.this,
						CardGroupingManagement.class);
				startActivity(intent);
				distancePopup.dismiss();
				// getMyCards("", "");

			}
		});
		add_new_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				changeAdd();
				distancePopup.dismiss();
			}
		});
		initpoputwindow(pview);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_page_main);
		ViewUtils.inject(this);
		// preInit();
		viewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		mPluginScrollView = (PluginScrollView) findViewById(R.id.horizontalScrollView);
		DisplayMetrics dm = new DisplayMetrics();
		xiaoxi_re.setVisibility(View.INVISIBLE);
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		myView = (MyScrollView) findViewById(R.id.myView);
		sortliner = (LinearLayout) findViewById(R.id.sortliner);
		
	}

	private void init() {
		sousuo.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				List<CardLinkman> list = cardLinkmans;
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(sousuo.getText().toString().trim())) {
					preInit(list);
				} else {
					List<CardLinkman> list1 = new ArrayList<CardLinkman>();
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getName()
								.indexOf(sousuo.getText().toString().trim()) != -1) {
							list1.add(list.get(i));
						}
					}
					list = list1;
					preInit(list);
				}
			}
		});
		arr = new String[2];
		arr[0] = "全部";
		arr[1] = "管理";
		getCardGrouping();
		// getMyCards("", "");
		getAllreceiveUserNameCardShare();
		getAllInitiateUserNameCardShare();
	}

	/**
	 * 准备数据
	 * 
	 * @param params
	 */
	private void initData(LayoutParams params) {
		textViews = new TextView[arr.length];
		textViews2 = new TextView[arr.length];
		for (int i = 0; i < arr.length; i++) {
			TextView textView = new TextView(CardPhonekook.this);
			TextView textView2 = new TextView(CardPhonekook.this);
			textViews[i] = textView;
			textViews2[i] = textView2;
		}
		for (int i = 0; i < arr.length; i++) {// 往二级分类中加载数据
			LayoutInflater inflater;
			inflater = LayoutInflater.from(CardPhonekook.this);
			View view = inflater.inflate(R.layout.card_s_view_item, null);
			LinearLayout lin = (LinearLayout) view.findViewById(R.id.lin1);
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) lin
					.getLayoutParams(); // 取控件textView当前的布局参数
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			linearParams.width = dm.widthPixels / 4;
			lin.getLayoutParams();
			textViews[i] = (TextView) view.findViewById(R.id.quanbu_lin);
			textViews[i].setTag("" + i);
			textViews2[i] = (TextView) view.findViewById(R.id.text);
			if (i == 0) {
				textViews[i].setVisibility(View.VISIBLE);
				textViews2[i].setTextColor(getResources().getColor(
						R.color.card_bu_zi));
			} else {
				textViews[i].setVisibility(View.INVISIBLE);
				textViews2[i].setTextColor(getResources().getColor(
						R.color.card_centre_zi));
			}
			textViews2[i].setTag("" + i);
			textViews2[i].setText(arr[i]);
			final int j = i;
			textViews2[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub
					changeColors(Integer.parseInt(view.getTag() + ""),
							textViews);
					if (textViews2.length == Integer.parseInt(view.getTag()
							+ "") + 1) {
						getPopupWindow();
						startPopupWindow();
						distancePopup.showAsDropDown(myView);
					} else {
						if ("0".equals(view.getTag() + "")) {
							getMyCards("", "");
						} else {
							getMyCards(
									cardGroupings
											.get(Integer.parseInt(view.getTag()
													+ "") - 1).getId(), "");
						}

					}
				}
			});
			sortliner.addView(view, i, params);
			sortTextViews.add(view);

		}
	}

	@OnClick({ R.id.all, R.id.manage, R.id.clientele, R.id.contacts,
			R.id.top_back, R.id.xiaoxi, R.id.goods_qishu })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.goods_qishu:
			if (sousuoState == 0) {
				Animation anim = AnimationUtils.loadAnimation(this,
						R.anim.push_bottom_in);
				sousuo.startAnimation(anim);
				sousuo.setVisibility(View.VISIBLE);
				sousuoState = 1;
			} else if (sousuoState == 1) {
				sousuo.setVisibility(View.GONE);
				sousuoState = 0;
			}

			break;
		case R.id.xiaoxi:
			Util.showIntent(this, CardAdvices.class);
			break;
		case R.id.top_back:
			CardUtil.isMe = "0";
			CardUtil.cardCount = 0;
			CardUtil.cardLinkman = null;
			CardUtil.myCardLinkman = null;
			CardUtil.faqiRe=null;
			CardUtil.shoudaoRe=null;
			CardUtil.exchangeUser.clear();
			finish();
			break;
		case R.id.manage:
			changeColor(3);
			getPopupWindow();
			startPopupWindow();
			distancePopup.showAsDropDown(guanli_lin);
			break;

		case R.id.all:
			changeColor(0);
			break;

		case R.id.contacts:
			changeColor(1);
			break;

		case R.id.clientele:
			changeColor(2);
			break;
		}
	}

	public void changeColor(int state) {
		TextView[] textZis = new TextView[] { all, contacts, clientele, manage };
		TextView[] bujus = new TextView[] { quanbu_lin, lianxi_lin, kehu_lin,
				guanli_lin };
		for (int i = 0; i < bujus.length; i++) {
			bujus[i].setVisibility(View.INVISIBLE);
			textZis[i].setTextColor(getResources().getColor(
					R.color.card_centre_zi));
		}
		textZis[state]
				.setTextColor(getResources().getColor(R.color.card_bu_zi));
		bujus[state].setVisibility(View.VISIBLE);
	}

	public void changeColors(int state, TextView[] textViews) {
		for (int i = 0; i < textViews.length; i++) {
			textViews[i].setVisibility(View.INVISIBLE);
			textViews2[i].setTextColor(getResources().getColor(
					R.color.card_centre_zi));
		}
		textViews2[state].setTextColor(getResources().getColor(
				R.color.card_bu_zi));
		textViews[state].setVisibility(View.VISIBLE);
	}

	/**
	 * 获得用户的名片的分组信息
	 */
	public void getCardGrouping() {
		final User user = UserData.getUser();
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				Map<String, String> map = new HashMap<String, String>();
				List<CardGrouping> list = new ArrayList<CardGrouping>();
				if (runData != null) {
					System.out.println(runData.toString().replace(" ", "")
							.replace(",}", "}"));
					try {
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(CardPhonekook.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					} else {

						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Gson gson = new Gson();
								list = gson.fromJson(map.get("list"),
										new TypeToken<List<CardGrouping>>() {
										}.getType());
							} else {
								Toast.makeText(CardPhonekook.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {

							Toast.makeText(CardPhonekook.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					}
					cardGroupings.clear();
					if (list.size() > 0) {

						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getIsdel().equals("0")) {
								cardGroupings.add(list.get(i));
							}

						}
						arr = new String[cardGroupings.size() + 2];
						arr[0] = "全部";
						for (int i = 0; i < cardGroupings.size(); i++) {
							arr[i + 1] = cardGroupings.get(i).getGroupname();
						}
						arr[cardGroupings.size() + 1] = "管理";
					} else {
						arr = new String[2];
						arr[0] = "全部";
						arr[1] = "管理";
						
					}
				} else {
					arr = new String[2];
					arr[0] = "全部";
					arr[1] = "管理";
				}
				getMyCards("", "");

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
	 * 获得我的所有的名片
	 */
	public void getMyCards(final String group, final String state) {
		final User user = UserData.getUser();
		Util.asynTask(this, "载入中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {

					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(CardPhonekook.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG)
								.show();
					} else {
						CardUtil.exchangeUser.clear();
						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {

								Gson gson = new Gson();

								cardLinkmans = gson.fromJson(map.get("list"),
										new TypeToken<List<CardLinkman>>() {
										}.getType());
								for (int i = 0; i < cardLinkmans.size(); i++) {
									if (!TextUtils.isEmpty(cardLinkmans.get(i)
											.getTouserid())) {
										CardUtil.exchangeUser.add(cardLinkmans
												.get(i));
									}
								}
								for (int j = 0; j < cardLinkmans.size(); j++) {

									if (cardLinkmans.get(j).getIsme()
											.equals("1")) {
										CardUtil.myCardLinkman = cardLinkmans
												.get(j);
										cardLinkmans.remove(j);
									}
								}
								if (TextUtils.isEmpty(group)) {
									CardUtil.cardCount = cardLinkmans.size();
								}
								if (!TextUtils.isEmpty(state)) {
									List<CardLinkman> list = new ArrayList<CardLinkman>();
									for (int i = 0; i < cardLinkmans.size(); i++) {

										if (TextUtils.isEmpty(cardLinkmans.get(
												i).getGroupname())) {
											list.add(cardLinkmans.get(i));
										}
									}
									cardLinkmans = list;

								}
								
								preInit();
								if (CardUtil.myCardLinkman==null&&cardLinkmans.size()<1) {
									CardUtil.xuznzeAdd(CardPhonekook.this.getParent(),width,"1");
								//	xuznzeAdd("0");
								}else if (cardLinkmans.size()<1&&CardUtil.myCardLinkman!=null) {
								//	xuznzeAdd("1");
									CardUtil.xuznzeAdd(CardPhonekook.this,width,"0");
								}
							} else {
								Toast.makeText(CardPhonekook.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {

							Toast.makeText(CardPhonekook.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(CardPhonekook.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(1, Web.bBusinessCard,
						Web.getAllUserBusinessCard, "getAllUserBusinessCard",
						"userId=" + user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&pagesize=99999999"
								+ "&curpage=1" + "&group=" + group);
				String s = web.getPlan();
				return s;
			}

		});
	}

	public class CardPhoneKookAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private AsyncImageLoader imageLoader;
		private List<CardLinkman> list;

		public CardPhoneKookAdapter(List<CardLinkman> list) {
			this.list = list;
			inflater = LayoutInflater.from(CardPhonekook.this);
			ImageCacheManager cacheMgr = new ImageCacheManager(
					CardPhonekook.this);
			imageLoader = new AsyncImageLoader(CardPhonekook.this,
					cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int postion, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = inflater.inflate(R.layout.card_lianxi_item, null);
			}
			ImageView userimg = (ImageView) view.findViewById(R.id.userimg);
			setImageWidthAndHeight(userimg, 1, width * 1 / 5, width * 1 / 5);
			ImageView imageView = new ImageView(CardPhonekook.this);
			Bitmap bmp=null;
			if (TextUtils.isEmpty(list.get(postion).getTouserid())) {
				bmp = imageLoader.loadBitmap(imageView, UserData.getUser().getUserFace(), true, width * 2 / 5, width * 2 / 5);
			}else {
				 bmp = imageLoader.loadBitmap(imageView, list.get(postion)
						.getUserFace(), true, width * 2 / 5, width * 2 / 5);
			}
			
			if (bmp == null) {
				userimg.setImageResource(R.drawable.addgroup_item_icon);
			} else {
				userimg.setImageBitmap(bmp);
			}
			Bitmap bitmap = ((BitmapDrawable) userimg.getDrawable())
					.getBitmap();
			bitmap = Util.toRoundCorner(bitmap, 10);
			userimg.setImageBitmap(bitmap);
			TextView name = (TextView) view.findViewById(R.id.name);
			TextView userMessage = (TextView) view
					.findViewById(R.id.userMessage);
			name.setText(list.get(postion).getName());
			userMessage.setText(list.get(postion).getDuty() + " "
					+ list.get(postion).getCompanyName());
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					CardUtil.cardLinkman = cardLinkmans.get(postion);
					Intent intent = new Intent(CardPhonekook.this,
							CardOneCardMessage.class);
					intent.putExtra("state", "chakan");
					startActivity(intent);
				}
			});
			view.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					cardCaozuo(list.get(postion).getId(), postion,
							list.get(postion).getTouserid(), list,list.get(postion).getState());
					return false;
				}
			});
			return view;
		}

		private void setImageWidthAndHeight(ImageView imageView, int state,
				int width, int height) {
			if (state == 1) {
				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) imageView
						.getLayoutParams(); // 取控件textView当前的布局参数
				linearParams.width = width;
				linearParams.height = height;
				imageView.getLayoutParams();
			}
			if (state == 2) {
				RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) imageView
						.getLayoutParams(); // 取控件textView当前的布局参数
				linearParams.width = width;
				linearParams.height = height;
				imageView.getLayoutParams();
			}

		}

	}

	/**
	 * 选择新增名片的方式
	 */
	private void changeAdd() {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_change_add_type_dialog, null);
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView shoujilianxi = (TextView) layout
				.findViewById(R.id.shoujilianxi);
		TextView shuru = (TextView) layout.findViewById(R.id.shuru);
		shoujilianxi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CardPhonekook.this,
						ContactsActivity.class);
				startActivityForResult(intent, 2);
				CardUtil.isMe = "0";
				dialog.dismiss();
			}
		});

		shuru.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CardPhonekook.this,
						CardAddNewCard.class);
				CardUtil.isMe = "0";
				startActivity(intent);
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		CardUtil.cardCount=0;
		init();
		
	}

	public void cardCaozuo(final String id, final int position,
			String touserid, final List<CardLinkman> lst,final String states) {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_caozuo_dialog, null);
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView yidong = (TextView) layout.findViewById(R.id.yidong);
		TextView xiugai = (TextView) layout.findViewById(R.id.xiugai);
		TextView shanchu = (TextView) layout.findViewById(R.id.delete);
		LinearLayout tuijian_lin = (LinearLayout) layout
				.findViewById(R.id.tuijian_lin);
		TextView tuijian = (TextView) layout.findViewById(R.id.tuijian);
		LinearLayout f_xinxi_lin = (LinearLayout) layout
				.findViewById(R.id.f_xinxi_lin);
		TextView f_xinxi = (TextView) layout.findViewById(R.id.f_xinxi);
		if (TextUtils.isEmpty(touserid)) {
			tuijian.setVisibility(View.GONE);
			tuijian_lin.setVisibility(View.GONE);
			f_xinxi.setVisibility(View.GONE);
			f_xinxi_lin.setVisibility(View.GONE);
		} else {
			f_xinxi.setVisibility(View.VISIBLE);
			f_xinxi_lin.setVisibility(View.VISIBLE);
			tuijian.setVisibility(View.VISIBLE);
			tuijian_lin.setVisibility(View.VISIBLE);
			if (states.equals("1")) {
				tuijian.setText("已推荐");
			}else {
				tuijian.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						tuijianCard(id);
						dialog.dismiss();
					}
				});
			}
		}
		f_xinxi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri smsToUri = Uri.parse("smsto:"
						+ lst.get(position).getPhone());
				Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
				intent.putExtra("sms_body", "");
				startActivity(intent);
				dialog.dismiss();
			}
		});
		
		yidong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent intent = new Intent(CardPhonekook.this,
						CardGroupingManagement.class);
				intent.putExtra("fenzu", "fenzu");
				intent.putExtra("cardid", id);
				startActivity(intent);
			}
		});
		xiugai.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		shanchu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				deleteTishi(id, position);

			}
		});
	}

	private void deleteTishi(final String id, final int postions) {
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
		TextView update_count = (TextView) layout
				.findViewById(R.id.update_count);
		TextView yihou_update = (TextView) layout
				.findViewById(R.id.yihou_update);
		TextView now_update = (TextView) layout.findViewById(R.id.now_update);
		update_count.setText("确定删除该名片？");
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
				deleteBusinessCard(id);
				dialog.dismiss();
			}
		});
	}

	/**
	 * 删除名片接口
	 */
	public void deleteBusinessCard(final String id) {
		final User user = UserData.getUser();
		Util.asynTask(this, "删除中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(CardPhonekook.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG)
								.show();
					} else {
						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Toast.makeText(CardPhonekook.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
								getMyCards("", "");
							} else {
								Toast.makeText(CardPhonekook.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {

							Toast.makeText(CardPhonekook.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(CardPhonekook.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard, Web.deleteBusinessCard,
						"deleteBusinessCard", "userId=" + user.getUserId()
								+ "&md5Pwd=" + user.getMd5Pwd() + "&id=" + id);
				String s = web.getPlan();
				return s;
			}
		});
	}


/**
	 * 推荐名片
	 */
	public void tuijianCard(final String id) {
		final User user = UserData.getUser();
		Util.asynTask(this, "推荐中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(CardPhonekook.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG)
								.show();
					} else {
						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Toast.makeText(CardPhonekook.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();

							} else {
								Toast.makeText(CardPhonekook.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {

							Toast.makeText(CardPhonekook.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(CardPhonekook.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard,
						Web.updateStateBusinessCard, "updateStateBusinessCard",
						"userId=" + user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&id=" + id);
				String s = web.getPlan();
				return s;
			}
		});
	}

	/**
	 * 获得用户收到的交换名片的请求
	 */
	private void getAllreceiveUserNameCardShare() {
		final User user = UserData.getUser();
		Util.asynTask(this, "加载中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(CardPhonekook.this,
								"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_LONG).show();
					} else {

						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Gson gson = new Gson();
								List<CardExchangeRequest> list = new ArrayList<CardExchangeRequest>();
								list = gson.fromJson(
										map.get("list"),
										new TypeToken<List<CardExchangeRequest>>() {
										}.getType());
								if (list.size() > 0) {
									List<CardExchangeRequest> list1 = new ArrayList<CardExchangeRequest>();
									for (int i = 0; i < list.size(); i++) {
										if (list.get(i).getState().equals("等待对方同意")) {
											list1.add(list.get(i));
										}
									}
									xiaoxi_count.setText(list1.size() + "");
									xiaoxi_re.setVisibility(View.VISIBLE);
									CardUtil.shoudaoRe=list;
								}else {
									xiaoxi_re.setVisibility(View.INVISIBLE);
								}
								if (xiaoxi_count.getText().toString().trim().equals("0")) {
									xiaoxi_re.setVisibility(View.INVISIBLE);
								}
							} else {
								Toast.makeText(CardPhonekook.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(CardPhonekook.this,
									"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_SHORT)
									.show();
						}
					}
				} else {
					Toast.makeText(CardPhonekook.this, "网络不给力哦，请检查网络后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = null;
				// 获取自己发送的
				// 获取接收到的
				web = new Web(1, Web.bBusinessCard,
						Web.getAllreceiveUserNameCardShare,
						"getAllreceiveUserNameCardShare", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&pagesize=999999999"
								+ "&curpage=" + (1));
				String s = web.getPlan();
				return s;
			}

		});
	}
	/**
	 * 获得用户收到的交换名片的请求
	 */
	private void getAllInitiateUserNameCardShare() {
		final User user = UserData.getUser();
		Util.asynTask(this, "加载中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(CardPhonekook.this,
								"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_LONG).show();
					} else {

						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Gson gson = new Gson();
								List<CardExchangeRequest> list = new ArrayList<CardExchangeRequest>();
								list = gson.fromJson(
										map.get("list"),
										new TypeToken<List<CardExchangeRequest>>() {
										}.getType());
								if (list.size() > 0) {
									CardUtil.faqiRe=list;
								}
							} else {
								Toast.makeText(CardPhonekook.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(CardPhonekook.this,
									"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_SHORT)
									.show();
						}
					}
				} else {
					Toast.makeText(CardPhonekook.this, "网络不给力哦，请检查网络后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = null;
				// 获取接收到的
				web = new Web(1, Web.bBusinessCard,
						Web.getAllInitiateUserNameCardShare,
						"getAllInitiateUserNameCardShare", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&pagesize=999999999"
								+ "&curpage=" + (1));
				String s = web.getPlan();
				return s;
			}

		});
	}

	private void postInit() {
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				Log.d("k", "onPageSelected - " + arg0);
				mPluginScrollView.buttonSelected(arg0);
				viewPager.setCurrentItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				Log.d("k", "onPageScrolled - " + arg0);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				Log.d("k", "onPageScrollStateChanged - " + arg0);
				// 状态有三个0空闲，1是增在滑行中，2目标加载完毕
			}
		});

	}

	private void preInit() {
		testList = new ArrayList<View>();
		for (int i = 0; i < arr.length; i++) {
			View view = getLayoutInflater().inflate(R.layout.card_list_item,
					null);
			ListView listView1 = (ListView) view.findViewById(R.id.listView1);
			
//			 * textView = new TextView(CardPhonekook.this);
//			 * textView.setText("ViewPager ==>" + i);
			 
			List<CardLinkman> list = new ArrayList<CardLinkman>();
			if (i == 0) {
				list = cardLinkmans;
			} else if (i == arr.length - 1) {

			} else {
				for (int j2 = 0; j2 < cardLinkmans.size(); j2++) {
					if (cardLinkmans.get(j2).getGroup()
							.equals(cardGroupings.get(i - 1).getId())) {
						list.add(cardLinkmans.get(j2));
					}
				}
			}
			listView1.setAdapter(new CardPhoneKookAdapter(list));
			testList.add(view);
		}
		viewPagerAdapter = new ViewPagerAdapter();
		viewPagerAdapter.setList(testList);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(0);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		// mPluginScrollView = new PluginScrollView(this, viewPager, testList);
		mPluginScrollView = (PluginScrollView) findViewById(R.id.horizontalScrollView);
		mPluginScrollView.setArr(arr);
		mPluginScrollView.setcallBack(this);
		mPluginScrollView.getWidth(width);
		mPluginScrollView.setTestList(testList);
		mPluginScrollView.setViewPager(viewPager);
		postInit();

	}

	private void preInit(List<CardLinkman> lists) {
		testList = new ArrayList<View>();
		for (int i = 0; i < arr.length; i++) {
			View view = getLayoutInflater().inflate(R.layout.card_list_item,
					null);
			ListView listView1 = (ListView) view.findViewById(R.id.listView1);
			
//			 * textView = new TextView(CardPhonekook.this);
//			 * textView.setText("ViewPager ==>" + i);
			 
			List<CardLinkman> list = new ArrayList<CardLinkman>();
			if (i == 0) {
				list = lists;
			} else if (i == arr.length - 1) {

			} else {
				for (int j2 = 0; j2 < lists.size(); j2++) {
					if (lists.get(j2).getGroup()
							.equals(cardGroupings.get(i - 1).getId())) {
						list.add(lists.get(j2));
					}
				}
			}
			listView1.setAdapter(new CardPhoneKookAdapter(list));
			testList.add(view);
		}
		viewPagerAdapter = new ViewPagerAdapter();
		viewPagerAdapter.setList(testList);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(0);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		// mPluginScrollView = new PluginScrollView(this, viewPager, testList);
		mPluginScrollView = (PluginScrollView) findViewById(R.id.horizontalScrollView);
		mPluginScrollView.setArr(arr);
		mPluginScrollView.setcallBack(this);
		mPluginScrollView.getWidth(width);
		mPluginScrollView.setTestList(testList);
		mPluginScrollView.setViewPager(viewPager);
		postInit();
	}

	@Override
	public void callBack() {
		// TODO Auto-generated method stub
		getPopupWindow();
		startPopupWindow();
		distancePopup.showAsDropDown(mPluginScrollView);
	}
//	@Override
//	public void callBack() {
//		// TODO Auto-generated method stub
//		getPopupWindow();
//		startPopupWindow();
//		distancePopup.showAsDropDown(mPluginScrollView);
//	}
}
