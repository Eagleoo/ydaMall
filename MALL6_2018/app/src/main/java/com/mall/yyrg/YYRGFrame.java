package com.mall.yyrg;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lyc.pic.CategoryGallery;
import com.mall.net.Web;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.view.gridview.NoScrollGridView;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.util.WHD;
import com.mall.view.R;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;
import com.mall.yyrg.adapter.YYRGUtil;
import com.mall.yyrg.model.BaskSingle;
import com.mall.yyrg.model.NewAnnounce;

/**
 * 一元热购主页<br>
 * 
 * @author Administrator
 * 
 */
public class YYRGFrame extends Activity {
	// 最新揭晓
	@ViewInject(R.id.zx_jiexiao1)
	private LinearLayout zx_jiexiao1;
	@ViewInject(R.id.zx_jiexiao2)
	private LinearLayout zx_jiexiao2;
	@ViewInject(R.id.zx_jiexiao3)
	private LinearLayout zx_jiexiao3;
//	@ViewInject(R.id.zx_jiexiao4)
//	private LinearLayout zx_jiexiao4;


	// 第一组
	@ViewInject(R.id.img_zuixin1)
	private ImageView img_zuixin1;
	@ViewInject(R.id.name_zuixin1)
	private TextView name_zuixin1;
	@ViewInject(R.id.time_zuixin1)
	private TextView time_zuixin1;
	// 第二组
	@ViewInject(R.id.img_zuixin2)
	private ImageView img_zuixin2;
	@ViewInject(R.id.name_zuixin2)
	private TextView name_zuixin2;
	@ViewInject(R.id.name_zuixin21)
	private TextView name_zuixin21;
	@ViewInject(R.id.time_zuixin2)
	private TextView time_zuixin2;
	// 第三组
	@ViewInject(R.id.img_zuixin3)
	private ImageView img_zuixin3;
	@ViewInject(R.id.name_zuixin3)
	private TextView name_zuixin3;
	@ViewInject(R.id.time_zuixin3)
	private TextView time_zuixin3;
	@ViewInject(R.id.name_zuixin31)
	private TextView name_zuixin31;
	// 第四组
//	@ViewInject(R.id.img_zuixin4)
//	private ImageView img_zuixin4;
//	@ViewInject(R.id.name_zuixin4)
//	private TextView name_zuixin4;
//	@ViewInject(R.id.time_zuixin4)
//	private TextView time_zuixin4;

	// 第5组
	@ViewInject(R.id.img_zuixin5)
	private ImageView img_zuixin5;
	@ViewInject(R.id.name_zuixin5)
	private TextView name_zuixin5;
	// 第6组
	@ViewInject(R.id.img_zuixin6)
	private ImageView img_zuixin6;
	@ViewInject(R.id.name_zuixin6)
	private TextView name_zuixin6;
	// 第7组
	@ViewInject(R.id.img_zuixin7)
	private ImageView img_zuixin7;
	@ViewInject(R.id.name_zuixin7)
	private TextView name_zuixin7;
	@ViewInject(R.id.ll)
	private View ll;

	

	// 最人气商品
	private MyProgressView pro_renqi1;
	private MyProgressView pro_renqi2;
	private MyProgressView pro_renqi3;

	@ViewInject(R.id.zr_renqi1)
	private LinearLayout zr_renqi1;



	// 晒单分享
	// 第一组
	@ViewInject(R.id.img_shaidan1)
	private ImageView img_shaidan1;
	@ViewInject(R.id.name_shaidan1)
	private TextView name_shaidan1;
	// 第二组
	@ViewInject(R.id.img_shaidan2)
	private ImageView img_shaidan2;
	@ViewInject(R.id.name_shaidan2)
	private TextView name_shaidan2;
	// 第三组
	@ViewInject(R.id.img_shaidan3)
	private ImageView img_shaidan3;
	@ViewInject(R.id.name_shaidan3)
	private TextView name_shaidan3;

	@ViewInject(R.id.gv_1)
	private NoScrollGridView gv_1;
	@ViewInject(R.id.gv_2)
	private NoScrollGridView gv_2;

	private List list1;
	private List list2;

	private YyrgItemAdapter adapter1;

	private YyrgItemAdapter adapter2;

	// 最新揭晓的商品
	private List<NewAnnounce> newAnnounces = new ArrayList<NewAnnounce>();
	// 最热人气的商品
	private List<NewAnnounce> renqiAnnounces = new ArrayList<NewAnnounce>();
	// 即将揭晓的商品
	private List<NewAnnounce> jiexiaoAnnounces = new ArrayList<NewAnnounce>();
	// 晒单分享
	private List<BaskSingle> baskSingles = new ArrayList<BaskSingle>();
	private int width;
	private int height;
	private BitmapUtils bmUtil;
	private Intent intent;
	private Handler handler;

	private CategoryGallery main_layout3_gridLine;
	private final int flag = 9;
	private ViewPager viewPager;
	private List<ImageView> imageViews = new ArrayList<ImageView>();
	private List<View> dots;
	private boolean isScroll = true;
	private final static float TARGET_HEAP_UTILIZATION = 0.75f;
	private Handler layout1BannerHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (isScroll && -1 == msg.what) {
				if (null != msg.obj) {
					viewPager.setCurrentItem(Integer.parseInt(msg.obj + ""));
				}
			}
		}
	};

	// 接受线程发出的消息，实现自动滚动的handler
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_frame);
		ViewUtils.inject(this);
		main_layout3_gridLine = (CategoryGallery) findViewById(R.id.main_layout3_gridLine);

		list1 = new ArrayList();
		list2 = new ArrayList();

		adapter1 = new YyrgItemAdapter(this, list1);
		adapter2 = new YyrgItemAdapter(this, list2);
		ll.setVisibility(View.GONE);
		gv_1.setAdapter(adapter1);
		gv_2.setAdapter(adapter2);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == flag) {
					int i = main_layout3_gridLine.getSelectedItemPosition();
					main_layout3_gridLine.setSelection(i + 1);
				} else {

					baskSingles.clear();
					jiexiaoAnnounces.clear();
					renqiAnnounces.clear();
					newAnnounces.clear();
					getNewAnnounce();
				}

			}
		};

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		showTopImg();

		getNewAnnounce();

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
		if (state == 3) {
			FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) imageView
					.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width;
			linearParams.height = height;
			imageView.getLayoutParams();
		}

	}

	private void showTopImg() {
		viewPager = (ViewPager) findViewById(R.id.vp);
		FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) viewPager
				.getLayoutParams(); // 取控件textView当前的布局参数
		linearParams.width = width;
		linearParams.height = (int) (width / 2.11);
		viewPager.getLayoutParams();
		// 头部滚动广告 start
		dots = new ArrayList<View>();
		dots.add((View) findViewById(R.id.v_dot0));
		dots.add((View) findViewById(R.id.v_dot1));
		dots.add((View) findViewById(R.id.v_dot2));
		dots.add((View) findViewById(R.id.v_dot3));
		dots.add((View) findViewById(R.id.v_dot4));
		dots.add((View) findViewById(R.id.v_dot5));
		dots.add((View) findViewById(R.id.v_dot6));
		dots.add((View) findViewById(R.id.v_dot7));
		dots.add((View) findViewById(R.id.v_dot8));
		dots.add((View) findViewById(R.id.v_dot9));
		for (int i = 0; i < dots.size(); i++) {
			dots.get(i).setVisibility(View.GONE);
		}
		int[] resId = new int[]{R.drawable.yyrg_b1,R.drawable.yyrg_b2};
		String[] resUrlId = new String[]{}; 
		for (int i = 0; i < resId.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setBackgroundResource(resId[i]);
			imageViews.add(imageView);
		}
		for (int i = 0; i < dots.size(); i++) {
			if (i < imageViews.size())
				dots.get(i).setVisibility(View.VISIBLE);
			else
				dots.get(i).setVisibility(View.GONE);
		}
		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					int count = viewPager.getAdapter().getCount();
					int index = viewPager.getCurrentItem();
					for (int i = 0; i < count; i++) {
						try {
							Thread.currentThread().sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Message msg = new Message();
						index = viewPager.getCurrentItem() + 1;
						if (index > count)
							index = 0;
						msg.obj = i;
						msg.what = -1;
						layout1BannerHandle.sendMessage(msg);
					}
				}
			}
		}).start();
	}

	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		public void onPageSelected(int position) {
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 最新揭晓商品的监听
	 * 
	 * @param view
	 */
	@OnClick({ R.id.zx_jiexiao1, R.id.zx_jiexiao2, R.id.zx_jiexiao3 })
	public void zuixinOnclick(View view) {
		String tag = (String) view.getTag();
		if (Integer.parseInt(tag) <= newAnnounces.size()) {
			intent = new Intent(this, YYRGHistoryGoodsMessage.class);
			intent.putExtra("goodsid",
					newAnnounces.get(Integer.parseInt(tag) - 1).getYppid());
			startActivity(intent);
		}

	}

	/**
	 * 最新晒单的监听
	 * 
	 * @param view
	 */
	@OnClick({ R.id.sd_fenxiang1, R.id.sd_fenxiang2, R.id.sd_fenxiang3 })
	public void shaidanOnclick(View view) {
		String tag = (String) view.getTag();
		YYRGUtil.baskSingle = baskSingles.get(Integer.parseInt(tag) - 1);
		Util.showIntent(this, YYRGBaskSingleMessage.class);
	}

	@OnClick({ R.id.yyrg_shuaxin, R.id.yyrg_sousuo, R.id.yyrg_xinpin,
			R.id.yyrg_sd_fanxiang, R.id.yyrg_rg_jilu, R.id.yyrg_xinshou,
			R.id.yyrg_zx_jiexiao, R.id.yyrg_renqi, R.id.yyrg_jj_jiexiao,
			R.id.yyrg_sd_fenxiang, R.id.yyrg_limit_time })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.yyrg_limit_time:
			Util.showIntent(this, YYRGLimitTimes.class);
			break;
		case R.id.yyrg_shuaxin:// 刷新当前页面
			finish();
			break;

		case R.id.yyrg_sousuo:// 搜索商品

			break;
		case R.id.yyrg_xinpin:// 显示最新商品
			Intent intent = new Intent(this, YYRGGoodsList.class);
			intent.putExtra("type", "zuixin");
			startActivity(intent);
			break;
		case R.id.yyrg_sd_fanxiang:// 晒单分享
			Util.showIntent(this, YYRGBaskSingle.class);
			break;
		case R.id.yyrg_rg_jilu:// 热购记录
			Util.showIntent(this, YYRGMyRegouFrame.class);
			break;
		case R.id.yyrg_xinshou:// 新手指南
			Util.showIntent(this, YYRGXinShouZhiNan.class);
			break;
		case R.id.yyrg_zx_jiexiao:// 转到最新揭晓的界面
			Intent intent3 = new Intent(this, YYRGRevealedFrame.class);
			intent3.putExtra("type", "jiexiao");
			startActivity(intent3);
			break;
		case R.id.yyrg_renqi:// 转到最人气商品的界面
			Intent intent2 = new Intent(this, YYRGGoodsList.class);
			intent2.putExtra("type", "renqi");
			startActivity(intent2);
			break;
		case R.id.yyrg_jj_jiexiao:// 转到即将揭晓商品的界面
			Intent intent1 = new Intent(this, YYRGGoodsList.class);
			intent1.putExtra("type", "jjjx");
			startActivity(intent1);
			break;
		case R.id.yyrg_sd_fenxiang:// 跳转到晒单分享的界面
			Util.showIntent(this, YYRGBaskSingle.class);
			break;
		}
	}

	/**
	 * 得到最新揭晓的产品
	 */
	private void getNewAnnounce() {
		Util.asynTask(this,"载入中...", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				getGoodProductListRecord();
				getMostPopularity();
				getAllBaskSingle();
				if (runData != null) {
					List<NewAnnounce> list = new ArrayList<NewAnnounce>();
					list = ((HashMap<Integer, List<NewAnnounce>>) runData)
							.get(1);
					if (list.size() > 0) {
						String href = "";
						String name = "";
						String path = "";
						newAnnounces = list;
						for (NewAnnounce newAnnounce : newAnnounces) {
							System.out.println(newAnnounce.getProductName());
						}
						if (newAnnounces.size() >= 1) {
							name_zuixin1.setText(newAnnounces.get(0)
									.getProductName());
							time_zuixin1.setText(newAnnounces.get(0)
									.getAnnTime());
							href = "http://" + Web.imgServer + "/"
									+ newAnnounces.get(0).getPhotoThumb();
							setImage(img_zuixin1, href, width * 2 / 5,
									width * 2 / 5 - 10);
							// yicang(new View[] { zanwu_jiexiao1, zx_jiexiao2,
							// zx_jiexiao2, zx_jiexiao3, zx_jiexiao4 });
							// xianshi(new View[] { zx_jiexiao1, zanwu_jiexiao2,
							// zanwu_jiexiao3, zanwu_jiexiao4 });
						}
						if (newAnnounces.size() >= 2) {
							name_zuixin2.setText(newAnnounces.get(1)
									.getProductName());
							time_zuixin2.setText(newAnnounces.get(1)
									.getAnnTime());
							name_zuixin21.setText(newAnnounces.get(1)
									.getAwardUserid() + " 获得");
							href = "http://" + Web.imgServer + "/"
									+ newAnnounces.get(1).getPhotoThumb();
							setImage(img_zuixin2, href, width / 4, width / 4);
							// yicang(new View[] { zanwu_jiexiao1,
							// zanwu_jiexiao2,
							// zx_jiexiao2, zx_jiexiao3, zx_jiexiao4 });
							// xianshi(new View[] { zx_jiexiao1, zx_jiexiao2,
							// zanwu_jiexiao3, zanwu_jiexiao4 });
						}
						if (newAnnounces.size() >= 3) {
							name_zuixin3.setText(newAnnounces.get(2)
									.getProductName());
							time_zuixin3.setText(newAnnounces.get(2)
									.getAnnTime());
							name_zuixin31.setText(newAnnounces.get(2)
									.getAwardUserid() + " 获得");
							href = "http://" + Web.imgServer + "/"
									+ newAnnounces.get(2).getPhotoThumb();
							System.out.println(href);
							setImage(img_zuixin3, href, width / 5, width / 5);
							// yicang(new View[] { zanwu_jiexiao1,
							// zanwu_jiexiao2,
							// zx_jiexiao2, zanwu_jiexiao3, zx_jiexiao4 });
							// xianshi(new View[] { zx_jiexiao1, zx_jiexiao2,
							// zx_jiexiao3, zanwu_jiexiao4 });
						}
//						if (newAnnounces.size() >= 4) {
//							name_zuixin4.setText(newAnnounces.get(3)
//									.getProductName());
//							time_zuixin4.setText(newAnnounces.get(3)
//									.getAwardUserid());
//							href = "http://" + Web.imgServer + "/"
//									+ newAnnounces.get(3).getPhotoThumb();
//
//							setImage(img_zuixin4, href, width / 5, width / 5);
//							// yicang(new View[] { zanwu_jiexiao1,
//							// zanwu_jiexiao2,
//							// zx_jiexiao2, zanwu_jiexiao3, zanwu_jiexiao4 });
//							// xianshi(new View[] { zx_jiexiao1, zx_jiexiao2,
//							// zx_jiexiao3, zx_jiexiao4 });
//						}
						ImageView[] ivs = { img_zuixin5, img_zuixin6, img_zuixin7 };
						TextView[] tvs = { name_zuixin5, name_zuixin6, name_zuixin7 };
						if (newAnnounces.size() >= 6) {
							ll.setVisibility(View.VISIBLE);
							for (int i = 0; i < 3; i++) {
								final NewAnnounce info = newAnnounces.get(i + 3);
								tvs[i].setText(info.getProductName());
								bmUtil.display(
										ivs[i],
										"http://" + Web.imgServer + "/"
												+ info.getPhotoThumb());
								
								
								ivs[i].setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View arg0) {
										Intent	intent = new Intent(YYRGFrame.this, YYRGHistoryGoodsMessage.class);
										intent.putExtra("goodsid",
												info.getYppid());
										startActivity(intent);
										
									}
								});
								
								

							}
						}

					} else {
						Toast.makeText(YYRGFrame.this, "暂无最新揭晓的产品",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(YYRGFrame.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.getNewAnnounce,
						"pageNum=1" + "&size=6");
				List<NewAnnounce> list = web.getList(NewAnnounce.class);
				HashMap<Integer, List<NewAnnounce>> map = new HashMap<Integer, List<NewAnnounce>>();
				map.put(1, list);
				return map;
			}

		});
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	private void setImage(final ImageView logo, String href, final int width,
			final int height) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				Bitmap bitmap = null;
				try {
					bitmap = Util.zoomBitmap(arg2, width, height);
				} catch (Exception e) {
					System.gc();
					bitmap = null;
					// TODO: handle exception
				}

				super.onLoadCompleted(arg0, arg1, bitmap, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}

	/**
	 * 得到最热人气商品
	 */
	private void getMostPopularity() {
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<NewAnnounce> list = new ArrayList<NewAnnounce>();
					list = ((HashMap<Integer, List<NewAnnounce>>) runData)
							.get(1);

					if (list != null && list.size() > 6) {
						list = list.subList(0, 6);
					}
					if (list != null) {
						list1.addAll(list);
						adapter1.notifyDataSetChanged();
					}

				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.getMostPopularity, "");
				List<NewAnnounce> list = web.getList(NewAnnounce.class);
				HashMap<Integer, List<NewAnnounce>> map = new HashMap<Integer, List<NewAnnounce>>();
				map.put(1, list);
				return map;
			}

		});
	}

	private void yicang(View[] views) {
		for (int i = 0; i < views.length; i++) {
			views[i].setVisibility(View.GONE);
		}
	}

	private void xianshi(View[] views) {
		for (int i = 0; i < views.length; i++) {
			views[i].setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获得所有的晒单分享
	 */
	private void getAllBaskSingle() {
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<BaskSingle> list = new ArrayList<BaskSingle>();
					list = ((HashMap<Integer, List<BaskSingle>>) runData)
							.get(1);
					if (null != list && list.size() > 0) {
						String href = "";
						baskSingles = list;
						main_layout3_gridLine.setAdapter(new GalleryAdapter(
								YYRGFrame.this, list));
						main_layout3_gridLine.setSelection(1000);

						// 自动滚动的线程。
						new Thread() {
							public void run() {
								while (true) {
									try {
										sleep(6000);
										Message msg = handler.obtainMessage(
												flag, 0, 0);
										handler.sendMessage(msg);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							};
						}.start();
					} else {
						Toast.makeText(YYRGFrame.this, "暂无最新揭晓的产品",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(YYRGFrame.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.getAllBaskSingle,
						"pageNum=1" + "&size=100");
				List<BaskSingle> list = web.getList(BaskSingle.class);
				HashMap<Integer, List<BaskSingle>> map = new HashMap<Integer, List<BaskSingle>>();
				map.put(1, list);
				return map;
			}

		});
	}

	/**
	 * 获得即将揭晓
	 */
	private void getGoodProductListRecord() {
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<NewAnnounce> list = new ArrayList<NewAnnounce>();
					list = ((HashMap<Integer, List<NewAnnounce>>) runData)
							.get(1);
					if (list != null && list.size() > 6) {
						list = list.subList(0, 6);
					}
					if (list != null) {
						list2.addAll(list);
						adapter2.notifyDataSetChanged();
					}
				} else {
					Toast.makeText(YYRGFrame.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress,
						Web.getGoodProductListRecord, "pagesize=6"
								+ "&pageindex=" + 1
								+ "&classid=&Brandid=&Operation=&ordertype=1");
				List<NewAnnounce> list = web.getList(NewAnnounce.class);
				HashMap<Integer, List<NewAnnounce>> map = new HashMap<Integer, List<NewAnnounce>>();
				map.put(1, list);
				return map;
			}
		});
	}

	/**
	 * 
	 * 功能： <br>
	 * galler对象的adapter 时间： 2014-7-17<br>
	 * 备注： <br>
	 * 给galler对象layout3GridLine添加数据，并实现循环滚动
	 * 
	 * @author
	 * 
	 */
	class GalleryAdapter extends BaseAdapter {
		Context context;
		List<BaskSingle> list;
		private AsyncImageLoader imageLoader;

		public GalleryAdapter(Context context, List<BaskSingle> list) {
			super();
			this.context = context;
			this.list = list;
			ImageCacheManager cacheMgr = new ImageCacheManager(YYRGFrame.this);
			imageLoader = new AsyncImageLoader(YYRGFrame.this,
					cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		}

		@Override
		public int getCount() {
			if (list == null || list.size() == 0) {
				return 0;
			}
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {

			return list.get(position % list.size());
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			synchronized (this) {
				if (0 == list.size())
					return convertView;
				int newPosition = position % list.size();
				if (list.size() < newPosition)
					return convertView;
				if (null == list.get(newPosition))
					return convertView;
			}
			WHD screenSize = Util.getScreenSize((Activity) context);
			float widths = screenSize.getWidth() / 3;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.yyrg_shaidan_item, null);

				convertView.setLayoutParams(new Gallery.LayoutParams(
						(int) widths, Gallery.LayoutParams.WRAP_CONTENT));
				GridHolder holder = new GridHolder();
				holder.logo = (ImageView) convertView
						.findViewById(R.id.img_shaidan1);
				holder.name = (TextView) convertView
						.findViewById(R.id.name_shaidan1);

				convertView.setTag(holder);

			}
			GridHolder holder = (GridHolder) convertView.getTag();
			setImageWidthAndHeight(holder.logo, 3, (int) (width * 0.3),
					(int) (width * 0.3));
			if (TextUtils.isEmpty(list.get(position % list.size())
					.getShareimgs())) {
				setImage(holder.logo, "www.yda360.com", (int) (width * 0.3),
						(int) (width * 0.3));
			} else {
				String href = "";
				href = Web.imgServer2
						+ "/"
						+ list.get(position % list.size())
								.getShareimgs()
								.substring(
										1,
										list.get(position % list.size())
												.getShareimgs().length())
								.replace(",", "Y").split("Y")[0];
				Bitmap bmp = imageLoader.loadBitmap(holder.logo, href, true,
						(int) (width * 0.3), (int) (width * 0.3));
				if (bmp == null) {
					holder.logo.setImageResource(R.drawable.new_yda__top_zanwu);
				} else {
					bmp = Util.zoomBitmap(bmp, (int) (width * 0.3),
							(int) (width * 0.3));
					holder.logo.setImageBitmap(bmp);
				}

			}
			holder.name.setText(Html.fromHtml(list.get(position % list.size())
					.getContent()));
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					YYRGUtil.baskSingle = list.get(position % list.size());
					Util.showIntent(YYRGFrame.this,
							YYRGBaskSingleMessage.class);
				}
			});
			return convertView;

		}

	}

	class GridHolder {
		private ImageView logo;
		private TextView name;
	}

	private class YyrgItemAdapter extends NewBaseAdapter {

		public YyrgItemAdapter(Context c, List list) {
			super(c, list);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int p, View v, ViewGroup arg2) {
			if (v == null) {

				ViewCache cache = new ViewCache();

				v = LayoutInflater.from(context).inflate(
						R.layout.yyrg_frame_item, null);

				cache.img_renqi1 = (ImageView) v.findViewById(R.id.img_renqi1);
				cache.price_renqi1 = (TextView) v
						.findViewById(R.id.price_renqi1);
				cache.yicanyu_renqi1 = (TextView) v
						.findViewById(R.id.yicanyu_renqi1);
				cache.shengyu_renqi1 = (TextView) v
						.findViewById(R.id.shengyu_renqi1);
				cache.zongxu_renqi1 = (TextView) v
						.findViewById(R.id.zongxu_renqi1);
				cache.pro_renqi1 = (MyProgressView) v
						.findViewById(R.id.pro_renqi1);

				v.setTag(cache);

			}

			ViewCache cache = (ViewCache) v.getTag();

			final NewAnnounce info = (NewAnnounce) list.get(p);
			DecimalFormat df = new DecimalFormat("#.00");
			cache.price_renqi1.setText("价值：￥"
					+ df.format(Double.parseDouble(info.getPrice())));
			cache.pro_renqi1.setMaxCount(Integer.parseInt(info
					.getTotalPersonTimes()) * 1.00f);
			cache.pro_renqi1.setCurrentCount(Integer.parseInt(info
					.getPersonTimes()) * 1.00f);
			cache.yicanyu_renqi1.setText(info.getPersonTimes());
			cache.shengyu_renqi1.setText(info.getLastPersonTimes());
			cache.zongxu_renqi1.setText(info.getTotalPersonTimes());
			
			String href = 
					 info.getPhotoThumb();
			if (TextUtils.isEmpty(href)||!href.contains("http")) {
				href="http://" + Web.imgServer + "/"+href;
			}

			AnimeUtil.getImageLoad().displayImage(href, cache.img_renqi1);
			
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent	intent = new Intent(context, YYRGGoodsMessage.class);
					intent.putExtra("goodsid",
							info.getYppid());
					intent.putExtra("ypid",
							info.getYpid());
					startActivity(intent);
					
				}
			});

			return v;
		}

		class ViewCache {

			ImageView img_renqi1;
			TextView price_renqi1;
			TextView yicanyu_renqi1;
			TextView zongxu_renqi1;
			TextView shengyu_renqi1;
			MyProgressView pro_renqi1;

		}

	}

}
