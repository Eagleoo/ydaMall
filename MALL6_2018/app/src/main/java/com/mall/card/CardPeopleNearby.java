package com.mall.card;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.card.bean.CardExchangeRequest;
import com.mall.card.bean.CardLinkman;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.PositionService;
import com.mall.view.R;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 附近的人
 * 
 * @author admin
 * 
 */
public class CardPeopleNearby extends Activity implements AMapLocationListener {
	@ViewInject(R.id.listView1)
	private ListView listView1;
	private CardPeopleNearbyAdapter cardPeopleNearbyAdapter;
	private Dialog dialog;
	private PositionService locationService;
	private String lat = "30.66833";
	private String lng = "104.072212";
	private int width;
//	private LocationManagerProxy mAMapLocManager = null;
	@ViewInject(R.id.juli)
	private TextView juli;
	private PopupWindow distancePopup = null;

	private ServiceConnection locationServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {



			Log.e("LocationMarkerActivity", "bind服务成功！");
			PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
			locationService = locationBinder.getService();
			locationService.startLocation();
			locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
				@Override
				public void onProgress(AMapLocation progress) {
					lat = progress.getLatitude() + "";
					lng =  progress.getLongitude() + "";
					selectPeopleNearby("5000");
				}

				@Override
				public void onError(AMapLocation error) {

				}
			});


		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_fujin_people);
		ViewUtils.inject(this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		showDialog();



			Intent intent = new Intent();
			intent.setAction(PositionService.TAG);
			intent.setPackage(getPackageName());
			getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);


		/*
		 * if
		 * (!TextUtils.isEmpty(CardUtil.lat)&&!TextUtils.isEmpty(CardUtil.lng))
		 * { selectPeopleNearby("5000"); }else { dingwei(); }
		 */
		// selectPeopleNearby("5000");

	}

	public void firstpageshop() {
		selectPeopleNearby("5000");
	}



	public void scrollPageshop() {
		listView1.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= cardPeopleNearbyAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					selectPeopleNearby("");
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	@OnClick({ R.id.top_back, R.id.juli })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;

		case R.id.juli:
			getPopupWindow();
			startPopupWindow();
			distancePopup.showAsDropDown(view);
			break;
		}
	}

	/**
	 * 查询附近的人
	 */
	public void selectPeopleNearby(final String type) {

		final User user = UserData.getUser();
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				dialog.dismiss();
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(null == map) return ;
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Gson gson = new Gson();
							List<CardLinkman> list = new ArrayList<CardLinkman>();
							list = gson.fromJson(map.get("list"),
									new TypeToken<List<CardLinkman>>() {
									}.getType());
							if (list==null) {
								return;
							}
							for (int i = 0; i < list.size(); i++) {
								if (list.get(i).getUserId().equals(user.getUserId())) {
									list.remove(i);
								}
							}
							
							Collections.sort(list,
									new Comparator<CardLinkman>() {

										@Override
										public int compare(
												CardLinkman info1,
												CardLinkman info2) {
											String distance1 = info1
													.getDistance();
											String distance2 = info2
													.getDistance();

											double dis1 = Util
													.getDouble(distance1);

											double dis2 = Util
													.getDouble(distance2);

											return (int) (dis1 - dis2);
										}
									});
							
							
							
							
							
							
							cardPeopleNearbyAdapter = new CardPeopleNearbyAdapter();
							listView1.setAdapter(cardPeopleNearbyAdapter);
							cardPeopleNearbyAdapter.setList(list);

						} else {
							Toast.makeText(CardPeopleNearby.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(CardPeopleNearby.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(CardPeopleNearby.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard, Web.nearbyNameCard,
						"nearbyNameCard", "userId=" + user.getUserId()
								+ "&md5Pwd=" + user.getMd5Pwd() + "&lat=" + lat
								+ "&lon=" + lng + "&type=" + type);
				String s = web.getPlan();
				return s;
			}

		});
	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {

	}

	public class CardPeopleNearbyAdapter extends BaseAdapter {
		private List<CardLinkman> list = new ArrayList<CardLinkman>();
		private LayoutInflater inflater;
		private AsyncImageLoader imageLoader;

		public CardPeopleNearbyAdapter() {
			inflater = LayoutInflater.from(CardPeopleNearby.this);
			ImageCacheManager cacheMgr = new ImageCacheManager(
					CardPeopleNearby.this);
			imageLoader = new AsyncImageLoader(CardPeopleNearby.this,
					cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		}

		public void setList(List<CardLinkman> list) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.card_exchange_card_request_item, null);
			}
			ImageView user_img = (ImageView) convertView
					.findViewById(R.id.user_img);
			TextView city = (TextView) convertView.findViewById(R.id.city);
			TextView username = (TextView) convertView
					.findViewById(R.id.username);
			TextView exchange = (TextView) convertView
					.findViewById(R.id.exchange);
			TextView message = (TextView) convertView
					.findViewById(R.id.message);
			TextView exchange1 = (TextView) convertView
					.findViewById(R.id.exchange1);
			TextView tv_distance = (TextView) convertView
					.findViewById(R.id.tv_distance);
			TextView tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			CardLinkman info = list.get(position);
			city.setText(list.get(position).getCity());
			username.setText(list.get(position).getName());
			message.setText(list.get(position).getDuty());
			exchange.setText("交换名片");
			exchange.setTag("0");
			exchange1.setVisibility(View.GONE);
			exchange.setVisibility(View.VISIBLE);
			
			String time=info.getLoginTime();
			if (!TextUtils.isEmpty(time)) {
				
			 time = Util.formatDateTime("yyyy/MM/dd HH:mm:ss",
						"yyyy-MM-dd HH:mm:ss", time);
			}
			
			tv_time.setText(Util.friendly_time(time));
			
			double dou = Util.getDouble(info.getDistance())/1000;
			String fdou = String.format("%.3f", (double) dou);
			tv_distance.setText(fdou+"KM");
			if (CardUtil.exchangeUser!=null) {
				
			for (int i = 0; i < CardUtil.exchangeUser.size(); i++) {

				if (list.get(position).getUserId()
						.equals(CardUtil.exchangeUser.get(i).getTouserid())) {
					System.out.println("已交换");
					exchange1.setVisibility(View.VISIBLE);
					exchange.setVisibility(View.GONE);
				}
			}
			}
			if (CardUtil.faqiRe!=null) {
				
			for (int i = 0; i < CardUtil.faqiRe.size(); i++) {

				if (list.get(position).getUserId()
						.equals(CardUtil.faqiRe.get(i).getTouserid())) {
					System.out.println("已发起交换");
					exchange1.setVisibility(View.VISIBLE);
					exchange1.setText("已发起交换");
					exchange.setVisibility(View.GONE);
				}
			}
			}
			if (CardUtil.shoudaoRe!=null) {
			for (int i = 0; i < CardUtil.shoudaoRe.size(); i++) {

				if (list.get(position).getUserId()
						.equals(CardUtil.shoudaoRe.get(i).getTouserid())) {
					System.out.println("收到请求");
					exchange1.setVisibility(View.VISIBLE);
					exchange1.setText("收到请求");
					exchange.setVisibility(View.GONE);
				}
			}
			}
			setImageWidthAndHeight(user_img, 1, width * 1 / 5, width * 1 / 5);
			ImageView imageView = new ImageView(CardPeopleNearby.this);
			Bitmap bmp = imageLoader.loadBitmap(imageView, list.get(position)
					.getUserFace(), true, width * 2 / 5, width * 2 / 5);
			if (bmp == null) {
				user_img.setImageResource(R.drawable.addgroup_item_icon);

			} else {
				user_img.setImageBitmap(bmp);
			}
			Bitmap bitmap = ((BitmapDrawable) user_img.getDrawable())
					.getBitmap();
			bitmap = Util.toRoundCorner(bitmap, 10);
			user_img.setImageBitmap(bitmap);
			exchange.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (CardUtil.myCardLinkman == null) {
						CardUtil.xuznzeAdd(CardPeopleNearby.this,width,"1");
//						Toast.makeText(this, "您还没有拍自己的名片，请前去扫描", Toast.LENGTH_SHORT)
//								.show();
					} else {
						if (CardUtil.myCardLinkman.getIsme().equals("1")) {
							addNameCardShare(list.get(position).getUserId());
						} else {
							CardUtil.xuznzeAdd(CardPeopleNearby.this,width,"1");
//							Toast.makeText(this, "您好没有拍自己的名片，请前去扫描", Toast.LENGTH_SHORT)
//									.show();
						}
					}
					
				}
			});
			return convertView;
		}

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
		TextView yihou_update = (TextView) layout
				.findViewById(R.id.yihou_update);
		TextView now_update = (TextView) layout.findViewById(R.id.now_update);
		TextView update_count = (TextView) layout
				.findViewById(R.id.update_count);
		update_count.setText("确定要推荐该会员？");
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
				updateStateBusinessCard(id);
				dialog.dismiss();
			}
		});
	}

	private void showDialog() {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_select_nearby_dialog, null);
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 1 / 2;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		ImageView im_scan = (ImageView) layout.findViewById(R.id.im_scan);
		ImageView im_dian = (ImageView) layout.findViewById(R.id.im_dian);
		RotateAnimation animation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(2000);
		animation.setRepeatCount(Animation.INFINITE);
		im_scan.startAnimation(animation);

		AlphaAnimation animation2 = new AlphaAnimation(0.0f, 1.0f);
		animation2.setDuration(3000);
		animation2.setRepeatCount(Animation.INFINITE);
		im_dian.startAnimation(animation2);
		dialog.setCanceledOnTouchOutside(false);
	}

//	@Override
//	public void onLocationChanged(Location location) {
//		// AmapV2版本中定位不会回调此方法啦
//	}



//	@Override
//	public void onLocationChanged(AMapLocation location) {
//		if (location != null) {
//			Double geoLat = location.getLatitude();
//			Double geoLng = location.getLongitude();
//			lat = "" + geoLat;
//			lng = "" + geoLng;
//			selectPeopleNearby("5000");
//			if (mAMapLocManager != null) {
//				mAMapLocManager.removeUpdates(this);
//			}
//		}
//	}

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

	/**
	 * 发起交换
	 */
	public void addNameCardShare(final String touserid) {

		final User user = UserData.getUser();
		Util.asynTask(CardPeopleNearby.this, "处理中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				dialog.dismiss();
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
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(CardPeopleNearby.this, "发起交换成功",
									Toast.LENGTH_SHORT).show();
							getAllInitiateUserNameCardShare();
							
						} else {
							Toast.makeText(CardPeopleNearby.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(CardPeopleNearby.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(CardPeopleNearby.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard, Web.addNameCardShare,
						"addNameCardShare", "userId=" + user.getUserId()
								+ "&md5Pwd=" + user.getMd5Pwd() + "&touserid="
								+ touserid);
				String s = web.getPlan();
				return s;
			}

		});
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param
	 */
	private void startPopupWindow() {
		View pview = getLayoutInflater().inflate(
				R.layout.card_distance_popupwindow, null, false);
		TextView all_distance = (TextView) pview
				.findViewById(R.id.all_distance);
		TextView meter500 = (TextView) pview.findViewById(R.id.meter500);
		TextView meter1000 = (TextView) pview.findViewById(R.id.meter1000);
		TextView meter2000 = (TextView) pview.findViewById(R.id.meter2000);
		TextView meter3000 = (TextView) pview.findViewById(R.id.meter3000);
		TextView meter5000 = (TextView) pview.findViewById(R.id.meter5000);
		all_distance.setOnClickListener(new WindowOnclick(5001));
		meter500.setOnClickListener(new WindowOnclick(500));
		meter1000.setOnClickListener(new WindowOnclick(1000));
		meter2000.setOnClickListener(new WindowOnclick(2000));
		meter3000.setOnClickListener(new WindowOnclick(3000));
		meter5000.setOnClickListener(new WindowOnclick(5000));
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

	public class WindowOnclick implements OnClickListener {
		private int meter;

		public WindowOnclick(int meter) {
			this.meter = meter;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (meter > 5000) {
				juli.setText("全部距离");
			} else {
				juli.setText(meter + "米");
			}
			selectPeopleNearby(meter + "");
			distancePopup.dismiss();
			showDialog();
		}

	}

	/**
	 * 推荐的人
	 */
	public void updateStateBusinessCard(final String id) {
		final User user = UserData.getUser();
		Util.asynTask(this, "推荐中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
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
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(CardPeopleNearby.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						} else {
							Toast.makeText(CardPeopleNearby.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(CardPeopleNearby.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(CardPeopleNearby.this,
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
				selectPeopleNearby("500");
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
						Toast.makeText(CardPeopleNearby.this,
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
									CardUtil.shoudaoRe=list;
								}
							} else {
								Toast.makeText(CardPeopleNearby.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(CardPeopleNearby.this,
									"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_SHORT)
									.show();
						}
					}
				} else {
					Toast.makeText(CardPeopleNearby.this, "网络不给力哦，请检查网络后，再试一下",
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
				getAllreceiveUserNameCardShare();
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
						Toast.makeText(CardPeopleNearby.this,
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
								Toast.makeText(CardPeopleNearby.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(CardPeopleNearby.this,
									"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_SHORT)
									.show();
						}
					}
				} else {
					Toast.makeText(CardPeopleNearby.this, "网络不给力哦，请检查网络后，再试一下",
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
}
