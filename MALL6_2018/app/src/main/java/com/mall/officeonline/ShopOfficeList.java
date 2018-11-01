package com.mall.officeonline;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.ShopOfficeListModel;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.mall.serving.community.view.pulltorefresh.PullToRefreshListView;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.Lin_MainFrame;
import com.mall.view.LoginFrame;
import com.mall.view.R;

public class ShopOfficeList extends Activity {
	@ViewInject(R.id.refreshListView)
	private PullToRefreshListView refreshListView;
	private ListView listview;
	private int page = 1;
	private ItemAdapter adapter;
	private PopupWindow distancePopup;
	private String order = "joinDate";
	private BitmapUtils bmUtils;
	@ViewInject(R.id.fi2)
	private FrameLayout fi2;
	private String orderty = "1";
	@ViewInject(R.id.order_indicator)
	private ImageView order_indicator;
	@ViewInject(R.id.search_edit)
	private EditText search_edit;
	private String KeyWord1 = "";
	private String sLevel = "";;
	private boolean search = false;
	private boolean mIsStart = true;

	@OnClick({ R.id.search, R.id.speak })
	public void Search(View v) {

		switch (v.getId()) {
		case R.id.search:
			search();
			break;
		case R.id.speak:

			Util.startVoiceRecognition(this, new DialogRecognitionListener() {
				@Override
				public void onResults(Bundle results) {
					ArrayList<String> rs = results != null ? results
							.getStringArrayList(RESULTS_RECOGNITION) : null;
					if (rs != null && rs.size() > 0) {
						String str = rs.get(0).replace("。", "")
								.replace("，", "");
						search_edit.setText(str);
						search();
					}
				}
			});

			break;

		default:
			break;
		}

	}

	private void search() {
		search = true;
		if (!Util.isNull(search_edit.getText().toString().trim())) {
			if (adapter != null) {
				adapter.clear();
				adapter.notifyDataSetChanged();
			}
			sLevel = "";
			KeyWord1 = search_edit.getText().toString();
			page = 1;
			fisrtpage();
			// scrollpage();
		} else {
			Toast.makeText(this, "搜索关键词不能为空", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop_office_list);
		ViewUtils.inject(this);
		listview = refreshListView.getRefreshableView();
		listview.setDividerHeight(1);
		refreshListView.setPullRefreshEnabled(false);
		refreshListView.setPullLoadEnabled(true);

		setListener();
		bmUtils = new BitmapUtils(this);
		page = 1;
		init();
	}

	private void setListener() {
		OnRefreshListener refreshListener = new OnRefreshListener() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				mIsStart = true;

				getOffice();

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				mIsStart = false;
				getOffice();
			}
		};
		refreshListView.setOnRefreshListener(refreshListener);

	}

	@OnClick(R.id.office_order)
	public void orderList(View v) {
		KeyWord1 = "";
		TextView t = (TextView) v;
		if (orderty.equals("1")) {
			orderty = "0";
			t.setText("由低到高");
			order_indicator.setImageResource(R.drawable.office_up);
		} else if (orderty.equals("0")) {
			orderty = "1";
			t.setText("由高到低");
			order_indicator.setImageResource(R.drawable.office_down);
		}
		if (adapter != null) {
			adapter.clear();
		}
		fisrtpage();
		page = 1;
	}

	@Override
	protected void onStart() {
		UserData.setOfficeInfo(null);
		KeyWord1 = "";
		super.onStart();
	}

	@OnClick({ R.id.fi1, R.id.fi2, R.id.fi3 })
	public void TopOnClick(final View v) {
		topSegment(v.getId());
	}

	public void topSegment(int id) {
		FrameLayout fi1 = (FrameLayout) this.findViewById(R.id.fi1);
		FrameLayout fi2 = (FrameLayout) this.findViewById(R.id.fi2);
		FrameLayout fi3 = (FrameLayout) this.findViewById(R.id.fi3);
		LinearLayout fi1_1 = (LinearLayout) fi1.getChildAt(0);
		TextView fi1_2 = (TextView) fi1.getChildAt(1);
		TextView fi2_1 = (TextView) fi2.getChildAt(0);
		LinearLayout fi3_1 = (LinearLayout) fi3.getChildAt(0);
		TextView fi3_2 = (TextView) fi3.getChildAt(1);
		switch (id) {
		case R.id.fi1:
			fi1_1.setBackgroundResource(R.drawable.order_biankuang1);
			fi1_2.setTextColor(getResources().getColor(R.color.headertop));
			fi1_2.setBackgroundColor(Color.WHITE);
			fi2_1.setTextColor(Color.WHITE);
			fi2_1.setBackgroundColor(Color.TRANSPARENT);

			fi3_1.setBackgroundResource(R.drawable.order_biankuang);
			fi3_2.setBackgroundColor(getResources().getColor(R.color.headertop));
			fi3_2.setTextColor(Color.WHITE);
			order = "mCount";
			page = 1;
			if (adapter != null) {
				adapter.clear();
			}
			fisrtpage();
			break;
		case R.id.fi2:
			fi1_1.setBackgroundResource(R.drawable.order_biankuang);
			fi1_2.setTextColor(Color.WHITE);
			fi1_2.setBackgroundColor(getResources().getColor(R.color.headertop));

			fi2_1.setTextColor(getResources().getColor(R.color.headertop));
			fi2_1.setBackgroundColor(Color.WHITE);

			fi3_1.setBackgroundResource(R.drawable.order_biankuang);
			fi3_2.setBackgroundColor(getResources().getColor(R.color.headertop));
			fi3_2.setTextColor(Color.WHITE);
			order = "vipMcount";
			if (adapter != null) {
				adapter.clear();
			}
			page = 1;
			fisrtpage();
			break;
		case R.id.fi3:
			fi3_1.setBackgroundResource(R.drawable.order_biankuang1);
			fi3_2.setTextColor(getResources().getColor(R.color.headertop));
			fi3_2.setBackgroundColor(Color.WHITE);
			fi2_1.setTextColor(Color.WHITE);
			fi2_1.setBackgroundColor(Color.TRANSPARENT);

			fi1_1.setBackgroundResource(R.drawable.order_biankuang);
			fi1_2.setBackgroundColor(getResources().getColor(R.color.headertop));
			fi1_2.setTextColor(Color.WHITE);
			order = "joinDate";
			if (adapter != null) {
				adapter.clear();
			}
			page = 1;
			fisrtpage();
			break;

		default:
			break;
		}
	}

	public void init() {
		Util.initTitle(this, "创业榜单",
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Util.showIntent(ShopOfficeList.this,
								Lin_MainFrame.class);
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						getPopupWindow();
						startOfficeTopPopupWindow();
						distancePopup.showAsDropDown(v);
					}
				}, R.drawable.morewhite);
		fisrtpage();

		search_edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				
				if (TextUtils.isEmpty(cs.toString())) {
				
					KeyWord1="";
					page=1;
					if (adapter != null) {
						adapter.clear();
						adapter.notifyDataSetChanged();
					}
					getOffice();
				}
				
				
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
	}

	private void fisrtpage() {
		if (adapter != null) {
			adapter.clear();
			adapter.notifyDataSetChanged();
		}
		getOffice();
	}

	@Override
	protected void onStop() {
		super.onStop();
		search_edit.clearFocus();
	}

	// private void scrollpage() {
	// listview.setOnScrollListener(new OnScrollListener() {
	// int lastItem;
	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	// if (lastItem >= adapter.getCount()
	// && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
	// getOffice();
	// }
	// }
	//
	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem,
	// int visibleItemCount, int totalItemCount) {
	// lastItem = firstVisibleItem + visibleItemCount;
	// }
	// });
	// }

	private void getOffice() {
		AnimeUtil.setAnimationEmptyView(this, listview, true);
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				refreshListView.onPullDownRefreshComplete();
				refreshListView.onPullUpRefreshComplete();
				refreshListView.setHasMoreData(true);
				AnimeUtil.setNoDataEmptyView("加载失败，轻触重试",
						R.drawable.community_dynamic_empty,
						ShopOfficeList.this, listview, true,
						new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								page = 1;
								getOffice();

							}
						});

				if (runData != null) {
					HashMap<Integer, List<ShopOfficeListModel>> map = (HashMap<Integer, List<ShopOfficeListModel>>) runData;
					List<ShopOfficeListModel> list = map.get(page);
					if (list != null && list.size() > 0) {
						if (adapter == null) {
							adapter = new ItemAdapter(ShopOfficeList.this);
							listview.setAdapter(adapter);
						}
						adapter.setList(list);
						adapter.notifyDataSetChanged();
					}
				} else {
				}
			}

			@Override
			public Serializable run() {
				String userid = "";
				if (UserData.getUser() != null) {
					userid = UserData.getUser().getUserId();
				}
				Web web = new Web(Web.officeUrl, Web.GetOfficeListPage3,
						"loginUserId=" + userid + "&KeyWord1=" + KeyWord1
								+ "&sLevel" + sLevel + "=&curpage=" + (page++)
								+ "&PageSize=20" + "&order_=" + order
								+ "&ordertype=" + orderty);
				List<ShopOfficeListModel> list = web
						.getList(ShopOfficeListModel.class);
				HashMap<Integer, List<ShopOfficeListModel>> map = new HashMap<Integer, List<ShopOfficeListModel>>();
				map.put(page, list);
				return map;
			}
		});
	}

	public class ItemAdapter extends BaseAdapter {
		public Context c;
		private int _100dp = 100;
		private List<ShopOfficeListModel> list = new ArrayList<ShopOfficeListModel>();
		private LayoutInflater inflater;
		

		public ItemAdapter(Context c) {
			this.c = c;
			inflater = LayoutInflater.from(c);
			_100dp = Util.dpToPx(c, 100);
		}

		public void setList(List<ShopOfficeListModel> list) {
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
			
			final ShopOfficeListModel s = this.list.get(position);
			if (convertView == null) {
				ViewHolder h = new ViewHolder();
				convertView = inflater.inflate(R.layout.shop_list_item, null);
				h.office_logo = (ImageView) convertView
						.findViewById(R.id.office_logo);
				h.name = (TextView) convertView.findViewById(R.id.name);
				h.item_rank = (LinearLayout) convertView
						.findViewById(R.id.item_rank_container);
				h.member_number = (TextView) convertView
						.findViewById(R.id.member_number);
				h.vip_number = (TextView) convertView
						.findViewById(R.id.vip_number);
				h.sj_number = (TextView) convertView
						.findViewById(R.id.sj_number);
				h.kj_number = (TextView) convertView
						.findViewById(R.id.kj_number);
				h.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				h.tv_sign = (TextView) convertView
						.findViewById(R.id.tv_sign);
				h.container = (LinearLayout) convertView
						.findViewById(R.id.container);
				h.more_oper = (ImageView) convertView
						.findViewById(R.id.more_oper);
				h.ll_detail =  convertView
						.findViewById(R.id.ll_detail);
				convertView.setTag(h);
//				map.put(position, convertView);
			}
//				convertView = map.get(position);
				final ViewHolder	h = (ViewHolder) convertView.getTag();
			
			h.container.removeAllViews();
			if (!Util.isNull(s.getOfName())) {
				String names = s.getOfName();
				if (names.length() > 6) {
					names = names.substring(0, 6) + "...";
				}
				h.name.setText(names.replace("_p", ""));
			} else {
				h.name.setText("");
			}
			String mobilePhone = s.getMobilePhone();
			if (TextUtils.isEmpty(mobilePhone)) {
				mobilePhone="暂无手机号码";
			}
			h.tv_phone.setText(mobilePhone);
			String sign = s.getSignature();
			if (TextUtils.isEmpty(sign)) {
				sign="这家伙很懒，还没有签名";
			}
			h.tv_sign.setText(sign);
			
			if (s.isShow()) {
				h.ll_detail.setVisibility(View.VISIBLE);
				
				h.more_oper.setImageResource(R.drawable.office_show_up);
			}else {
				h.ll_detail.setVisibility(View.GONE);
				
				h.more_oper.setImageResource(R.drawable.office_show_down);
			}
			
			h.more_oper.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if (h.ll_detail.getVisibility()==View.GONE) {
						if(!"lin00123".equals(s.getClicks())){
							Util.asynTask(ShopOfficeList.this, "正在获取会员数...", new IAsynTask() {
								@Override
								public void updateUI(Serializable runData) {
									ShopOfficeListModel newS = (ShopOfficeListModel)runData;
									if (!Util.isNull(newS.getmCount())) {
										h.member_number.setText(Util.spanGreenWithString("会员数量：",
												newS.getmCount()));
									} else {
										h.member_number.setText(Util.spanGreenWithString("会员数量：", ""));
									}
									if (!Util.isNull(newS.getVipMcount())) {
										h.vip_number.setText(Util.spanGreenWithString("创客数量：",
												newS.getVipMcount()));
									} else {
										h.vip_number.setText(Util.spanGreenWithString("创客 数量：", ""));
									}
									if (!Util.isNull(newS.getLmsjMerCount())) {
										h.sj_number.setText(Util.spanGreenWithString("商家数量：",
												newS.getLmsjMerCount()));
									} else {
										h.sj_number.setText(Util.spanGreenWithString("商家数量：", ""));
									}
									if (!Util.isNull(newS.getShopMerCount())) {
										h.kj_number.setText(Util.spanGreenWithString("空间数量：",
												newS.getShopMerCount()));
									} else {
										h.kj_number.setText(Util.spanGreenWithString("空间数量：", ""));
									}
									s.setClicks("lin00123");
									h.ll_detail.setVisibility(View.VISIBLE);
									h.more_oper.setImageResource(R.drawable.office_show_up);
									s.setShow(true);
								}
								
								@Override
								public Serializable run() {
									Web web = new Web(Web.officeUrl, Web.getUserCountById,
											"id=" + s.getId() );
									return web.getObject(ShopOfficeListModel.class);
								}
							});
						}else{
							h.ll_detail.setVisibility(View.VISIBLE);
							h.more_oper.setImageResource(R.drawable.office_show_up);
							s.setShow(true);
						}
					}else {
						h.ll_detail.setVisibility(View.GONE);
					
						h.more_oper.setImageResource(R.drawable.office_show_down);
						s.setShow(false);
					}
				}
			});
			ShopOfficeFrame.clacluate(h.item_rank, c,
					Util.getInt(s.getCrown()), Util.getInt(s.getSun()),
					Util.getInt(s.getMonth()), Util.getInt(s.getStar()));
			String url = "";
			final ImageView img = h.office_logo;
			Resources r = ShopOfficeList.this.getResources();
			InputStream is = r.openRawResource(R.drawable.no_get_image);
			BitmapDrawable bmpDraw = new BitmapDrawable(is);
			Bitmap zoomBm = Util
					.zoomBitmap(bmpDraw.getBitmap(), _100dp, _100dp);
			String logo = s.getLogo();
			if (TextUtils.isEmpty(logo) || logo.equals("/")) {
				logo = "/user/office/offlogo.jpg";
			}
			logo = logo.replace("/http", "http");
			if (logo.contains("http://")) {
				url = logo;
			} else {
				url = "http://" + Web.webServer_Image + logo;
			}
			Log.e("图片地址",url);
			bmUtils.configDefaultLoadingImage(Util.toRoundCorner(zoomBm, 10));
			bmUtils.display(img, url, new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View arg0, String arg1,
						Bitmap arg2, BitmapDisplayConfig arg3,
						BitmapLoadFrom arg4) {
					img.setImageBitmap(Util.toRoundCorner(
							Util.zoomBitmap(arg2, _100dp, _100dp), 10));
				}

				@Override
				public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
					Resources r = ShopOfficeList.this.getResources();
					InputStream is = r.openRawResource(R.drawable.no_get_image);
					BitmapDrawable bmpDraw = new BitmapDrawable(is);
					Bitmap zoomBm = Util.zoomBitmap(bmpDraw.getBitmap(),
							_100dp, _100dp);
					img.setImageBitmap(Util.toRoundCorner(zoomBm, 10));
				}
			});

			int crown = 0;
			int sun = 0;
			int moon = 0;
			int star = 0;
			if (!Util.isNull(s.getCrown())) {
				crown = Integer.parseInt(s.getCrown());
			}
			if (!Util.isNull(s.getSun())) {
				sun = Integer.parseInt(s.getSun());
			}
			if (!Util.isNull(s.getMonth())) {
				moon = Integer.parseInt(s.getMonth());
			}
			if (!Util.isNull(s.getStar())) {
				star = Integer.parseInt(s.getStar());
			}
			final int cr = crown;
			final int su = sun;
			final int moo = moon;
			final int sta = star;
			clacluate(h.container, c, crown, sun, moon, star);
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ShopOfficeList.this,
							ShopOfficeFrame.class);
					intent.putExtra("userNo", s.getUnum());
					intent.putExtra("name", s.getOfName());
					intent.putExtra("crown", cr);
					intent.putExtra("sun", su);
					intent.putExtra("moon", moo);
					intent.putExtra("star", sta);
					intent.putExtra("offid", s.getUserid());
					intent.putExtra("from", "list");
					intent.putExtra("officeid", s.getId());
					ShopOfficeList.this.startActivity(intent);
				}
			});
			convertView.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
					getPopupWindow();
					startOfficeItemPopupWindow(s.getUnum(), s.getId(), s);
					distancePopup.showAtLocation(arg0,Gravity.CENTER, 0, 0);// 但是因为缓存，所以View的显示位置会错乱
					
					
					
					
					
					
					
					
					return false;
				}
			});
			return convertView;
		}

		private void clacluate(LinearLayout Layout, Context c, int crown,
				int sun, int moon, int star) {
			// 如果小于5个心。则有灰色的心，如果有钻石，则无灰色心
			int size = crown + sun + moon + star;
			int[] ione = new int[size];
			for (int i = 0; i < crown; i++) {
				ione[i] = R.drawable.hg;// ione[0]=hg
			}
			for (int i = 0; i < sun; i++) {
				ione[i + crown] = R.drawable.hg;// ione[1]=sun,ion[2]=sun
			}
			for (int i = 0; i < moon; i++) {
				ione[i + crown + sun] = R.drawable.diamond;
			}
			for (int i = 0; i < star; i++) {
				ione[i + crown + sun + moon] = R.drawable.red_heart;
			}
			addIndexImage(Layout, ione);
		}

		private void addIndexImage(LinearLayout Layout, int[] res) {
			LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			l.setMargins(0, 0, 0, 0);
			int[] rank = res;
			for (int i = 0; i < rank.length; i++) {
				ImageView img = new ImageView(c);
				img.setLayoutParams(l);
				img.setImageResource(rank[i]);
				Layout.addView(img);
			}
		}
	}

	public class ViewHolder {
		ImageView office_logo;
		TextView name;
		LinearLayout item_rank;
		TextView member_number;
		TextView vip_number;
		TextView sj_number;
		TextView kj_number;
		TextView tv_phone;
		TextView tv_sign;
		LinearLayout container;
		ImageView more_oper;
		View ll_detail;

	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}

	private void store(final String officesId, final ShopOfficeListModel m) {
		if (UserData.getUser() != null) {
			final User user = UserData.getUser();
			Util.asynTask(ShopOfficeList.this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if (runData != null) {
						if ("success".equals(runData + "")) {
							Toast.makeText(ShopOfficeList.this, "收藏成功",
									Toast.LENGTH_LONG).show();
							m.setSc("1");
							adapter.notifyDataSetChanged();
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
		}
	}

	public void deleteStore(final String officesId, final ShopOfficeListModel m) {
		if (UserData.getUser() != null) {
			final User user = UserData.getUser();
			Util.asynTask(ShopOfficeList.this, "", new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if (runData != null) {
						if ("success".equals(runData + "")) {
							Toast.makeText(ShopOfficeList.this, "取消收藏成功",
									Toast.LENGTH_LONG).show();
							m.setSc("0");
							adapter.notifyDataSetChanged();
						}
					} else {

					}
				}

				@Override
				public Serializable run() {
					Web web = new Web(Web.officeUrl, Web.deletefavoriteOffices,
							"userId=" + user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&officesId="
									+ officesId);
					return web.getPlan();
				}
			});
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
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	private void initOfficeItempoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.FILL_PARENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimationupanddown);
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startOfficeItemPopupWindow(final String userNo,
			final String office_id, final ShopOfficeListModel m) {
		View pview = getLayoutInflater().inflate(
				R.layout.shop_office_list_item_popup, null, false);
		LinearLayout pop_layout = (LinearLayout) pview
				.findViewById(R.id.pop_layout);
		// Util.drawLayoutDropShadow(ShopOfficeList.this,Util.dpToPx(ShopOfficeList.this,
		// 165), Util.dpToPx(ShopOfficeList.this, 130),pop_layout);
		TextView store = (TextView) pview.findViewById(R.id.store);
		TextView message = (TextView) pview.findViewById(R.id.message);
		TextView in_office = (TextView) pview.findViewById(R.id.in_office);
		if (!Util.isNull(m.getSc()) && Integer.parseInt(m.getSc()) > 0) {
			store.setText("已收藏");
			store.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					distancePopup.dismiss();
					deleteStore(office_id, m);
				}
			});
			store.setTextColor(Color.parseColor("#49afef"));
			store.setBackgroundColor(Color.parseColor("#ddf0fe"));
		} else {
			store.setTextColor(Color.BLACK);
			store.setBackgroundColor(Color.TRANSPARENT);
			store.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					distancePopup.dismiss();
					store(office_id, m);
				}
			});
		}
		message.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		in_office.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int crown = 0;
				int sun = 0;
				int moon = 0;
				int star = 0;
				if (!Util.isNull(m.getCrown())) {
					crown = Integer.parseInt(m.getCrown());
				}
				if (!Util.isNull(m.getSun())) {
					sun = Integer.parseInt(m.getSun());
				}
				if (!Util.isNull(m.getMonth())) {
					moon = Integer.parseInt(m.getMonth());
				}
				if (!Util.isNull(m.getStar())) {
					star = Integer.parseInt(m.getStar());
				}
				distancePopup.dismiss();
				Intent intent = new Intent(ShopOfficeList.this,
						ShopOfficeFrame.class);
				intent.putExtra("userNo", userNo);
				intent.putExtra("name", m.getOfName());
				intent.putExtra("crown", crown);
				intent.putExtra("sun", sun);
				intent.putExtra("moon", moon);
				intent.putExtra("star", star);
				intent.putExtra("offid", m.getUserid());
				intent.putExtra("from", "list");
				ShopOfficeList.this.startActivity(intent);
				ShopOfficeList.this.startActivity(intent);
			}
		});
		initOfficeItempoputwindow(pview);
	}

	/*
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startOfficeTopPopupWindow() {
		View pview = getLayoutInflater().inflate(
				R.layout.shop_office_list_top_popup, null, false);
		TextView mystore = (TextView) pview.findViewById(R.id.mystore);
		TextView myrank = (TextView) pview.findViewById(R.id.myrank);
		TextView in_office = (TextView) pview.findViewById(R.id.in_office);
		final LinearLayout myrank_layout = (LinearLayout) pview
				.findViewById(R.id.myrank_layout);
		mystore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
				Util.showIntent(ShopOfficeList.this, MyStoreOffice.class);
			}
		});
		if (UserData.getUser() != null) {
			// 如果isSite不等于0都是有创业空间
			if (UserData.getUser().getIsSite().equals("0")) {
				myrank_layout.setVisibility(View.GONE);
			}
		}
		myrank.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
				TextView t = (TextView) v;
				if (UserData.getUser() != null) {
					// 如果isSite不等于0都是有创业空间
					if (!UserData.getUser().getIsSite().equals("0")) {
						Intent intent = new Intent(ShopOfficeList.this,
								ShopOfficeFrame.class);
						intent.putExtra("userNo", UserData.getUser()
								.getUserNo());
						intent.putExtra("my", "my");
						ShopOfficeList.this.startActivity(intent);
					} else {
						myrank_layout.setVisibility(View.GONE);
					}
				} else {
					Util.showIntent(ShopOfficeList.this, LoginFrame.class);
				}
			}
		});
		initpoputwindow(pview);
	}
}
