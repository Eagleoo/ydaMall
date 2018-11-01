package com.mall.yyrg;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.ProductDeatilFream;
import com.mall.view.R;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;
import com.mall.yyrg.model.HuoDong;

public class NewHuoDong extends Activity {
	private List<HuoDong> huoDongs = new ArrayList<HuoDong>();
	private int width;
	private NewHuoDongAdapter newHuoDongAdapter;
	@ViewInject(R.id.goods_list)
	private GridView goodslist;
	private int currentPageShop = 0;
	private PopupWindow distancePopup = null;
	private BitmapUtils bmUtil;
	private List<HuoDong> huodongType = new ArrayList<HuoDong>();
	@ViewInject(R.id.re1)
	private RelativeLayout re1;
	@ViewInject(R.id.hdzq)
	private TextView hdzq;
	private String startTime;
	@ViewInject(R.id.ss_text1)
	private TextView ss_text1;
	@ViewInject(R.id.ss_text2)
	private TextView ss_text2;
	@ViewInject(R.id.ss_text3)
	private TextView ss_text3;
	@ViewInject(R.id.ss_text4)
	private TextView ss_text4;
	@ViewInject(R.id.ss_text5)
	private TextView ss_text5;
	@ViewInject(R.id.ss_text6)
	private TextView ss_text6;
	@ViewInject(R.id.ss_text7)
	private TextView ss_text7;
	@ViewInject(R.id.lin1)
	private LinearLayout lin1;
	private long tozhouer;
	private int state=0;
	private Timer zw_now_timer = new Timer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_new_forum_activities);
		ViewUtils.inject(this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		getAllActivityTheme(re1);
		
		// getGoodProductListRecord("");
	}

	@OnClick({ R.id.top_back, R.id.fenlei })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;

		case R.id.fenlei:
			if (huodongType.size() > 0) {
				getPopupWindow();
				startPopupWindow(huodongType);
				distancePopup.showAsDropDown(re1);
			} else {
				
			}

			break;
		}
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(final List<HuoDong> list) {
		View pview = getLayoutInflater().inflate(
				R.layout.dialog_huodong_fenlei, null, false);
		ListView goods_type = (ListView) pview.findViewById(R.id.goods_type);
		goods_type.setAdapter(new NewHuoDongTypeAdapter(list));
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
		// distancePopup.setAnimationStyle(R.style.popupanimation);
	}

	/**
	 * 获得商品列表信息
	 */
	private void getGoodProductListRecord(final String categoryID) {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<HuoDong> list = new ArrayList<HuoDong>();
					list = ((HashMap<Integer, List<HuoDong>>) runData)
							.get(index++);
					if (list.size() > 0) {
						newHuoDongAdapter.setList(list,categoryID);
					} else {
						Toast.makeText(NewHuoDong.this, "没有更多商品",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(NewHuoDong.this, "网络不给力哦，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress,
						Web.Get_ActivityProductByPage, "pagesize=20"
								+ "&currentPage=" + (++currentPageShop)
								+ "&categoryID=" +"&activityId="+categoryID);
				List<HuoDong> list = web.getList(HuoDong.class);
				HashMap<Integer, List<HuoDong>> map = new HashMap<Integer, List<HuoDong>>();
				map.put(index++, list);
				return map;
			}
		});
	}
	public void daojishi(Date nowdate,Date startdate){
		try {
			tozhouer = getTimeDelta(startdate, nowdate);
			if (tozhouer <= 0) {// 时间小于0说明现在已经过了周五
				lin1.setVisibility(View.INVISIBLE);
			} else {// 开始计时
				/*MyCount myCount=new MyCount(tozhouwu*1000, 1000);
				myCount.start();*/
				zw_now_timer = new Timer();
				zw_now_timer.schedule(new TimerTask() {
					@Override
					public void run() {

						runOnUiThread(new Runnable() { // UI thread
							@Override
							public void run() {
								tozhouer--;
							
								if (tozhouer < 0) {
									zw_now_timer.cancel();
									// txtView.setVisibility(View.GONE);
									return;
								}
								long hour = (int) (tozhouer / (60 * 60));
								long fen = (tozhouer % (60 * 60)) / 60;
								long miao = ((tozhouer % (60 * 60)) % 60);
								if (hour >= 100) {
									ss_text1.setVisibility(View.VISIBLE);
									ss_text1.setText(hour / 100 + "");
									ss_text2.setText(((hour % 100) / 10) + "");
									ss_text3.setText(((hour % 100) % 10) + "");
									ss_text4.setText(fen / 10 + "");
									ss_text5.setText(fen % 10 + "");
									ss_text6.setText(miao / 10 + "");
									ss_text7.setText(miao % 10 + "");
								} else {
									ss_text1.setVisibility(View.INVISIBLE);
									// sd_text1.setText(hour/100+"");
									ss_text2.setText(((hour) / 10) + "");
									ss_text3.setText(((hour) % 10) + "");
									ss_text4.setText(fen / 10 + "");
									ss_text5.setText(fen % 10 + "");
									ss_text6.setText(miao / 10 + "");
									ss_text7.setText(miao % 10 + "");
								}

							}
						});
					}
				}, 1000, 1000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public long getTimeDelta(Date date1, Date date2) {
		long timeDelta = (date1.getTime() - date2.getTime()) / 1000;// 单位是秒
		return timeDelta;
	}
	/**
	 * 获得商品列表信息
	 */
	private void getAllActivityTheme(final View view) {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<HuoDong> list = new ArrayList<HuoDong>();
					list = ((HashMap<Integer, List<HuoDong>>) runData).get(1);
					if (list.size() > 0) {
						huodongType.addAll(list);
						
						/*getPopupWindow();
						startPopupWindow(huodongType);
						distancePopup.showAsDropDown(view);*/
						if (newHuoDongAdapter == null) {
							newHuoDongAdapter = new NewHuoDongAdapter();
							goodslist.setAdapter(newHuoDongAdapter);
						}
						state=1;
						firstpageshop(list.get(0).getId());
						scrollPageshop(list.get(0).getId());
						startTime=list.get(0).getStartTime();
						hdzq.setText("活动专区-" + list.get(0).getName());
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							daojishi(new Date(), sdf.parse(startTime));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
					/*	Toast.makeText(NewHuoDong.this, "没有商品分类",
								Toast.LENGTH_LONG).show();*/
						if (newHuoDongAdapter == null) {
							newHuoDongAdapter = new NewHuoDongAdapter();
							goodslist.setAdapter(newHuoDongAdapter);
						}
						state=2;
						firstpageshop("0");
						scrollPageshop("0");
					}
				} else {
					Toast.makeText(NewHuoDong.this, "网络不给力哦，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.getAllActivityTheme, "type=runing");
				List<HuoDong> list = web.getList(HuoDong.class);
				HashMap<Integer, List<HuoDong>> map = new HashMap<Integer, List<HuoDong>>();
				map.put(1, list);
				return map;
			}
		});
	}

	public void firstpageshop(String id) {
		getGoodProductListRecord(id);
	}

	public void scrollPageshop(final String id) {
		goodslist.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= newHuoDongAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getGoodProductListRecord(id);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	public class NewHuoDongAdapter extends BaseAdapter {
		private List<HuoDong> list = new ArrayList<HuoDong>();
		private LayoutInflater inflater;
		private AsyncImageLoader imageLoader;
		private String activity ="";
		public NewHuoDongAdapter() {
			 inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        ImageCacheManager cacheMgr = new ImageCacheManager(NewHuoDong.this);
		        imageLoader = new AsyncImageLoader(NewHuoDong.this, cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		}

		public void setList(List<HuoDong> list,String activity) {
			this.list.addAll(list);
			this.activity = activity;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View view, ViewGroup arg2) {
			if (view == null) {
				view = inflater.inflate(R.layout.yyrg_new_forum_activitys_item,
						null);
			}
			DecimalFormat df = new DecimalFormat("#.00");
			final ImageView goods_image = (ImageView) view
					.findViewById(R.id.goods_image);
			TextView goods_name = (TextView) view.findViewById(R.id.goods_name);
			TextView now_price = (TextView) view.findViewById(R.id.now_price);
			TextView old_price = (TextView) view.findViewById(R.id.old_price);
			TextView sb_price = (TextView) view.findViewById(R.id.sb_price);
			TextView zhekou = (TextView) view.findViewById(R.id.zhekou);
			TextView reviewNum = (TextView) view.findViewById(R.id.reviewNum);
			LinearLayout lin1 = (LinearLayout) view.findViewById(R.id.lin1);
			setImageWidthAndHeight(goods_image, 1, width * 2 / 5, width * 2 / 5);
			Bitmap bmp = imageLoader.loadBitmap(goods_image, list.get(position).getProductThumb(), true, width * 2 / 5, width * 2 / 5);
	        if(bmp == null) {
	        	goods_image.setImageResource(R.drawable.new_yda__top_zanwu);
	        } else {
	        	goods_image.setImageBitmap(bmp);
	        }
			goods_name.setText(list.get(position).getShortTitle());
			now_price.setText("￥"
					+ df.format(Double.parseDouble(list.get(position)
							.getPrice())));
			old_price.setText("￥"
					+ df.format(Double.parseDouble(list.get(position)
							.getPriceMarket())));
			DecimalFormat df1 = new DecimalFormat("#.0");
			Double zhek=Double.parseDouble(list.get(position)
					.getPrice())/Double.parseDouble(list.get(position)
							.getPriceMarket())*10;
			old_price.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
			if (!TextUtils.isEmpty(list.get(position)
					.getSbPrice())) {
				sb_price.setText("商币兑换："+(int)Double.parseDouble(list.get(position)
						.getSbPrice()));
			}
			zhekou.setText(df1.format(zhek) + "折");
			reviewNum.setText(list.get(position).getReviewNum() + "条");
			lin1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try { 
						if (state==2) {
							Intent intent = new Intent(NewHuoDong.this,
									ProductDeatilFream.class);
							intent.putExtra("url", list.get(position).getProductID());
							startActivity(intent);
						}else {
							
						
						Date date=new Date();
						if (getTimeDelta(new Date(), sdf.parse(startTime))>=0) {
							Intent intent = new Intent(NewHuoDong.this,
									ProductDeatilFream.class);
							intent.putExtra("url", list.get(position).getProductID());
							intent.putExtra("activity", activity);
							startActivity(intent);
						}else {
							Toast.makeText(NewHuoDong.this, "活动尚未开始，敬请期待!", Toast.LENGTH_SHORT).show();
						}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
				}
			});

			return view;
		}
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

	public class NewHuoDongTypeAdapter extends BaseAdapter {
		private List<HuoDong> list;
		private LayoutInflater inflater;

		public NewHuoDongTypeAdapter(List<HuoDong> list) {
			this.list = list;
			inflater = LayoutInflater.from(NewHuoDong.this);
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
		public View getView(final int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = inflater.inflate(R.layout.new_huodong_type_item, null);
			}
			TextView goods_type = (TextView) view.findViewById(R.id.goods_type);
			goods_type.setText(list.get(position).getName());
			goods_type.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					currentPageShop = 0;
					newHuoDongAdapter = new NewHuoDongAdapter();
					goodslist.setAdapter(newHuoDongAdapter);
					hdzq.setText("活动专区-" + list.get(position).getName());
					firstpageshop(list.get(position).getId());
					scrollPageshop(list.get(position).getId());
					distancePopup.dismiss();
				}
			});
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					currentPageShop = 0;
					newHuoDongAdapter = new NewHuoDongAdapter();
					goodslist.setAdapter(newHuoDongAdapter);
					firstpageshop(list.get(position).getId());
					scrollPageshop(list.get(position).getId());
					distancePopup.dismiss();
				}
			});
			return view;
		}

	}
}
