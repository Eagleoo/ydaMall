package com.mall.view;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.Category;
import com.mall.model.Product;
import com.mall.net.Web;
import com.mall.util.Data;
import com.mall.util.IAsynTask;
import com.mall.util.Util;

public class SBQFrame extends Activity {

	@ViewInject(R.id.sbq_frame_listview)
	private ListView listView;
	@ViewInject(R.id.sbq_frame_rehuan)
	private RadioButton hot;
	@ViewInject(R.id.sbq_frame_fenlei)
	private RadioButton fenlei;
	@ViewInject(R.id.sbq_frame_jiage)
	private RadioButton jia;
	private List<Category> rootCate;
	@ViewInject(R.id.sbq_select_line)
	private ScrollView dataLine;
	@ViewInject(R.id.sbq_selected_dataLine)
	private LinearLayout dataLineList;
	private SBQFrameListAdapter adapter;
	private int defaultColor = Color.parseColor("#cccccc");
	private int page = 1;
	private boolean isFoot = false; // 用来判断是否已滑动到底部
	private String cid = "-1";
	private int backCateId = -1;
	private BitmapUtils bmUtil;
	private String ascOrDesc = "";
	private int[] images = new int[] { R.drawable.dh_quanbu,
			R.drawable.dh_chaoliu, R.drawable.dh_shipin, R.drawable.dh_gehu,
			R.drawable.dh_jujia, R.drawable.dh_piju, R.drawable.dh_shenghuo,
			R.drawable.dh_phone, R.drawable.dh_jingpin };
	private PopupWindow distancePopup = null;
	private String[] allFenlei=new String[]{"全部分类","潮流服饰","食品保健","个护化妆","居家百货"
			,"皮具箱包","生活家电","手机数码","精品鞋城"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sbq_frame);
		ViewUtils.inject(this);
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.zw174);
		init();
	}

	@Override
	protected void onStart() {
		HotOnClick(hot);
		super.onStart();
	}
	@OnClick(R.id.imgtitle_320)
	public void toTheir(View view){
		Util.showIntent(this, TheirDuiHuanFrame.class);
	}

	public void init() {
		Util.initTop(this, "兑换排行", Integer.MIN_VALUE, null);
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		// 获取分类
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<Category>> map = (HashMap<String, List<Category>>) runData;
				rootCate = map.get("list");
				Category allCate = new Category();
				allCate.setCategoryId("-1");
				allCate.setCategoryName("全部分类");
				allCate.setCategoryParentID("-1");
				rootCate.add(0, allCate);
				String cName = SBQFrame.this.getIntent().getStringExtra("name");
				if (!Util.isNull(cName)) {
					for (Category cate : rootCate) {
						if (cate.getCategoryName().equals(cName)) {
							cid = cate.getCategoryId();
							dataLine.setVisibility(ScrollView.GONE);
							fenlei.setText(cate.getCategoryName());
							fenlei.setBackgroundColor(Color.WHITE);
							backCateId = Integer.parseInt(cid);
							page = 1;
							if (null != adapter)
								adapter.clearData();
							page();
						}
					}
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getAllClass, "parentID=-1");
				HashMap<String, List<Category>> map = new HashMap<String, List<Category>>();
				map.put("list", web.getList(Category.class));
				return map;
			}
		});

		String cName = SBQFrame.this.getIntent().getStringExtra("name");
		if (Util.isNull(cName))
			page();
		listView.setOnScrollListener(new PauseOnScrollListener(bmUtil, false,
				true, new OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view,
							int scrollState) {
						// 判断是否已滑动或者处于底部
						if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
								&& isFoot) {
							// 滚动到底部
							if (view.getLastVisiblePosition() == (view
									.getCount() - 1)) {
								page();
								System.gc();
							}
						}
					}

					@Override
					public void onScroll(AbsListView view,
							int firstVisibleItem, int visibleItemCount,
							int totalItemCount) {
						isFoot = (firstVisibleItem + visibleItemCount == totalItemCount);
					}
				}));
	}

	private void page() {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		Util.asynTask(this, "正在获取商品...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<Product>> map = (HashMap<String, List<Product>>) runData;
				List<Product> list = map.get("list");
				if (null == list || 0 == list.size())
					Util.show("没有获取到商品！", SBQFrame.this);
				else {
					if (null == adapter) {
						adapter = new SBQFrameListAdapter(SBQFrame.this, list,
								bmUtil);
						listView.setAdapter(adapter);
					} else {
						if (1 == page)
							adapter.clearData();
						adapter.addData(list);
						adapter.updateUI();
					}
				}
				page++;
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getProductBySBQ, "categoryId=" + cid
						+ "&page=" + page + "&pageSize=11&ascOrDesc="
						+ ascOrDesc);
				List<Product> list = web.getList(Product.class);
				HashMap<String, List<Product>> map = new HashMap<String, List<Product>>();
				map.put("list", list);
				return map;
			}
		});
	}

	private void clearStyle() {
		fenlei.setBackgroundColor(defaultColor);
		jia.setBackgroundColor(defaultColor);
		hot.setBackgroundColor(defaultColor);
	}

	/**
	 * 点击价格排序
	 * 
	 * @param v
	 */
	@OnClick(R.id.sbq_frame_jiage)
	public void jiaClick(final View v) {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		v.setEnabled(false);
		clearStyle();
		Drawable img_on, img_hot_on, img_fl_on, img_right_on, img_up_to_down, img_down_to_up;
		Resources res = getResources();
		img_on = res.getDrawable(R.drawable.jiage_duihuan);
		img_on.setBounds(0, 0, img_on.getMinimumWidth(),
				img_on.getMinimumHeight());
		img_up_to_down = res.getDrawable(R.drawable.price_down_to_up);
		img_up_to_down.setBounds(0, 0, Util.dpToPx(this, 5),
				Util.dpToPx(this, 30));
		img_down_to_up = res.getDrawable(R.drawable.price_up_to_down);
		img_down_to_up.setBounds(0, 0, Util.dpToPx(this, 5),
				Util.dpToPx(this, 30));
		final Drawable up = img_up_to_down;
		final Drawable down = img_down_to_up;
		final Drawable img_on_fi = img_on;
		dataLine.setVisibility(ScrollView.GONE);
		jia.setBackgroundColor(Color.WHITE);
		hot.setBackgroundColor(getResources().getColor(R.color.new_duihuan));
		fenlei.setBackgroundColor(getResources().getColor(R.color.new_duihuan));
		// 当时升序还是降序
		// up降序，down升序
		final boolean isUp = "up".equals((v.getTag() + ""));
		TextView paixu = (TextView) v;
		// 这里要反着显示
		if (isUp) {
			ascOrDesc = "desc";
			paixu.setText("价格高到低");
			jia.setCompoundDrawables(img_on_fi, null, up, null);
		} else {
			ascOrDesc = "asc";
			jia.setCompoundDrawables(img_on_fi, null, down, null);
			paixu.setText("价格低到高");
		}
		v.setTag(isUp ? "down" : "up");
		page = 1;
		page();
		v.setEnabled(true);
	}

	/**
	 * 点击热卖
	 */
	@OnClick(R.id.sbq_frame_rehuan)
	public void hotClick(View v) {
		HotOnClick(v);
	}

	private void HotOnClick(View v) {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		v.setEnabled(false);
		Drawable img_on, img_fl_on, img_price_on, img_right_on, img_up_to_down, img_down_to_up;
		Resources res = getResources();
		img_price_on = res.getDrawable(R.drawable.jiage_duihuan);
		img_price_on.setBounds(0, 0, img_price_on.getMinimumWidth(),

		img_price_on.getMinimumHeight());
		dataLine.setVisibility(ScrollView.GONE);
		jia.setCompoundDrawables(img_price_on, null, null, null);
		hot.setBackgroundColor(Color.WHITE);
		jia.setBackgroundColor(getResources().getColor(R.color.new_duihuan));
		fenlei.setBackgroundColor(getResources().getColor(R.color.new_duihuan));
		img_up_to_down = res.getDrawable(R.drawable.price_down_to_up_white);
		img_up_to_down.setBounds(0, 0, Util.dpToPx(this, 10),
				Util.dpToPx(this, 20));
		img_down_to_up = res.getDrawable(R.drawable.price_up_to_down_white);
		img_down_to_up.setBounds(0, 0, Util.dpToPx(this, 10),
				Util.dpToPx(this, 20));
		final boolean isUp = "up".equals((jia.getTag() + ""));
		// 这里要反着显示
		if (!jia.getText().toString().equals("价格排序")) {
			if (isUp) {
				jia.setCompoundDrawables(img_price_on, null, img_down_to_up,
						null);
				ascOrDesc = "desc";
			} else {
				jia.setCompoundDrawables(img_price_on, null, img_up_to_down,
						null);
				ascOrDesc = "asc";
			}
			if (!Util.isNetworkConnected(this)) {
				Util.show("没有检测到网络，请检查您的网络连接...", this);
				return;
			}
		}
		Util.asynTask(this, "正在获取热卖商品...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if (adapter != null) {
					adapter.clearData();
				}

			}

			@Override
			public Serializable run() {
				return null;
			}
		});
		v.setEnabled(true);
	}

	/**
	 * 点击分类
	 * 
	 * @param v
	 */
	@OnClick(R.id.sbq_frame_fenlei)
	public void fenleiClick(View v) {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		if (rootCate.size()>0) {
			getPopupWindow();
			startPopupWindow(rootCate);
			distancePopup.showAsDropDown(v);
		}
		Drawable img_on, img_hot_on, img_price_on, img_right_on, img_up_to_down, img_down_to_up;
		Resources res = getResources();
		img_price_on = res.getDrawable(R.drawable.jiage_duihuan);
		img_price_on.setBounds(0, 0, img_price_on.getMinimumWidth(),

		img_price_on.getMinimumHeight());
		v.setEnabled(false);
		img_up_to_down = res.getDrawable(R.drawable.price_down_to_up_white);
		img_up_to_down.setBounds(0, 0, Util.dpToPx(this, 6),
				Util.dpToPx(this, 20));
		img_down_to_up = res.getDrawable(R.drawable.price_up_to_down_white);
		img_down_to_up.setBounds(0, 0, Util.dpToPx(this, 6),
				Util.dpToPx(this, 20));
		final boolean isUp = "up".equals((jia.getTag() + ""));
		// 这里要反着显示
		if (!jia.getText().toString().equals("价格排序")) {
			if (isUp) {
				jia.setCompoundDrawables(img_price_on, null, img_down_to_up,
						null);
				ascOrDesc = "desc";
			} else {
				jia.setCompoundDrawables(img_price_on, null, img_up_to_down,
						null);
				ascOrDesc = "asc";
			}
			if (!Util.isNetworkConnected(this)) {
				Util.show("没有检测到网络，请检查您的网络连接...", this);
				return;
			}
		}
		if (dataLine.getVisibility() == ScrollView.VISIBLE) {
			dataLine.setVisibility(ScrollView.GONE);
			dataLineList.setVisibility(LinearLayout.GONE);
			v.setEnabled(true);
			return;
		} else {
			dataLine.setVisibility(ScrollView.GONE);
			dataLineList.setVisibility(LinearLayout.GONE);
		}
		fenlei.setBackgroundColor(Color.WHITE);
		dataLineList.removeAllViews();
		dataLineList.removeAllViewsInLayout();
		TextView text = null;
		int _5dp = Util.dpToPx(this, 5F);
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		ll.setMargins(_5dp, _5dp, _5dp, _5dp);
		for (Category cate : rootCate) {
			text = new TextView(this);
			text.setText(cate.getCategoryName());
			text.setTag(cate.getCategoryId());
			text.setTextColor(Color.parseColor("#535353"));
			if ((backCateId + "").equals(cate.getCategoryId())) {
				text.setTextColor(Color.parseColor("#2ae0c8"));
			}
			text.setTextSize(16F);
			text.setBackgroundResource(R.drawable.liner2_border_eeeeee);

			text.setPadding(_5dp, _5dp, _5dp, _5dp);
			text.setLayoutParams(ll);
			text.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dataLine.setVisibility(ScrollView.GONE);
					cid = v.getTag() + "";
					TextView view = (TextView) v;
					fenlei.setText(view.getText());
					backCateId = Integer.parseInt(cid);
					page = 1;
					adapter.clearData();
					page();

				}
			});
			text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
			dataLineList.addView(text);
		}
		jia.setCompoundDrawables(img_price_on, null, null, null);
		fenlei.setBackgroundColor(Color.WHITE);
		jia.setBackgroundColor(getResources().getColor(R.color.new_duihuan));
		hot.setBackgroundColor(getResources().getColor(R.color.new_duihuan));
		v.setEnabled(true);
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(List<Category> categories) {
		View pview = getLayoutInflater().inflate(R.layout.new_duihuan_ph_more,
				null, false);
		LinearLayout lin=(LinearLayout) pview.findViewById(R.id.lin);
		RelativeLayout re_fen1=(RelativeLayout) pview.findViewById(R.id.re_fen1);
		RelativeLayout re_fen2=(RelativeLayout) pview.findViewById(R.id.re_fen2);
		RelativeLayout re_fen3=(RelativeLayout) pview.findViewById(R.id.re_fen3);
		RelativeLayout re_fen4=(RelativeLayout) pview.findViewById(R.id.re_fen4);
		RelativeLayout re_fen5=(RelativeLayout) pview.findViewById(R.id.re_fen5);
		RelativeLayout re_fen6=(RelativeLayout) pview.findViewById(R.id.re_fen6);
		RelativeLayout re_fen7=(RelativeLayout) pview.findViewById(R.id.re_fen7);
		RelativeLayout re_fen8=(RelativeLayout) pview.findViewById(R.id.re_fen8);
		RelativeLayout re_fen9=(RelativeLayout) pview.findViewById(R.id.re_fen9);
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width=dm.widthPixels;
		int height=dm.heightPixels;
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) lin
				.getLayoutParams(); // 取控件textView当前的布局参数
		linearParams.width = width;
		linearParams.height = height;
		lin.getLayoutParams();
		View[] views=new View[8];
		views[0]=re_fen1;
		views[1]=re_fen2;
		views[2]=re_fen3;
		views[3]=re_fen4;
		views[4]=re_fen5;
		views[5]=re_fen6;
		views[6]=re_fen7;
		views[7]=re_fen8;
		for (int i = 0; i < views.length; i++) {
			
			
				if (i>=6) {
					views[i].setTag(categories.get(i+1).getCategoryId()+"");	
					views[i].setOnClickListener(new PopupWindowOnclick(categories.get(i+1).getCategoryName()));
				}else {
					views[i].setTag(categories.get(i).getCategoryId()+"");	
					views[i].setOnClickListener(new PopupWindowOnclick(categories.get(i).getCategoryName()));
				}
			
		}
		for (Category category:categories) {
			if (category.getCategoryName().equals("精品鞋城")) {
				re_fen9.setTag(category.getCategoryId()+"");
				re_fen9.setOnClickListener(new PopupWindowOnclick(category.getCategoryName()));
			}
		}
		initpoputwindow(pview);
	}
	class PopupWindowOnclick implements OnClickListener{
		private String string;
		PopupWindowOnclick(String string){
			this.string=string;
		};
		@Override
		public void onClick(View v) {
			cid = v.getTag() + "";
			backCateId = Integer.parseInt(cid);
			page = 1;
			fenlei.setText(string);
			adapter.clearData();
			distancePopup.dismiss();
			page();
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

}

class SBQFrameListAdapter extends BaseAdapter {

	private SBQFrame frame;
	private List<Product> list;
	private LayoutInflater flater;
	private BitmapUtils bmUtil;
	private BitmapDisplayConfig config;
	private int _90dp = 90;
	private int _100dp = 100;

	public SBQFrameListAdapter(SBQFrame frame, List<Product> list,
			BitmapUtils bmUtil) {
		super();
		this.frame = frame;
		this.list = list;
		flater = LayoutInflater.from(frame);
		this.bmUtil = bmUtil;
		_90dp = Util.dpToPx(frame, 90F);
		_100dp = Util.dpToPx(frame, 100F);
		config = new BitmapDisplayConfig();
		config.setBitmapMaxSize(new BitmapSize(_100dp, _90dp));
	}

	public List<Product> getList() {
		return list;
	}

	public void clearData() {
		this.list.clear();
	}

	public void addData(List<Product> list) {
		this.list.addAll(list);
	}

	public void updateUI() {
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
	public View getView(int arg0, View view, ViewGroup arg2) {
		if (0 == list.size())
			return view;
		final Product pro = list.get(arg0);
		SBQHodler holder = null;
		if (null == view) {
			view = flater.inflate(R.layout.sbq_frame_list_item, null);
			holder = new SBQHodler();
			holder.line = (LinearLayout) view
					.findViewById(R.id.sbq_list_item_line);
			holder.img = (ImageView) view.findViewById(R.id.sbq_list_item_img);
			holder.img_null = (ImageView) view
					.findViewById(R.id.sbq_list_item_img_null);
			holder.name = (TextView) view.findViewById(R.id.sbq_list_item_name);
			holder.scj = (TextView) view
					.findViewById(R.id.sbq_list_item_shoujia);
			holder.ydj = (TextView) view
					.findViewById(R.id.sbq_list_item_yuanda);
			holder.jf = (TextView) view.findViewById(R.id.sbq_list_item_jifen);
			holder.sbj = (TextView) view.findViewById(R.id.sbq_list_item_sbj);
			view.setTag(holder);
		} else
			holder = (SBQHodler) view.getTag();
		if (pro.getStocks().startsWith("0"))
			holder.img_null.setVisibility(View.VISIBLE);
		else
			holder.img_null.setVisibility(View.GONE);
		DecimalFormat df = new DecimalFormat("#0.00");
		holder.sbj.setText("商币兑换：" + pro.getSbj());
		holder.scj.setText("市场售价：￥"
				+ df.format(Double.parseDouble(pro.getPriceMarket())));
		holder.ydj.setText("远大售价：￥"
				+ df.format(Double.parseDouble(pro.getPrice())));
		holder.jf.setText("可用消费券：￥"
				+ df.format(Util.getDouble(pro.getPrice())
						- Util.getDouble(pro.getExpPrice1())));
		holder.name.setText(pro.getName());
		final String href = pro.getThumb().replace("img.mall666.cn",
				Web.imgServer);
		final ImageView logo = holder.img;
		bmUtil.display(logo, href, config,
				new DefaultBitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						arg2 = Util.zoomBitmap(arg2, _100dp, _90dp);
						super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
					}

					@Override
					public void onLoadFailed(View arg0, String arg1,
							Drawable arg2) {
						logo.setImageResource(R.drawable.zw174);
					}
				});
		holder.line.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Data.setProductClass(11);
				Util.showIntent(frame, ProductDeatilFream.class,
						new String[] { "url" }, new String[] { pro.getPid() });
			}
		});
		return view;
	}
}

class SBQHodler {
	public LinearLayout line;
	public ImageView img;
	public ImageView img_null;
	public TextView name;
	public TextView scj;
	public TextView ydj;
	public TextView jf;
	public TextView sbj;

}
