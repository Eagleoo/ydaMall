package com.mall.view;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HuangouFrame extends Activity {

	@ViewInject(R.id.huangou_frame_listview)
	private ListView listView;
	private List<Category> rootCate;
	@ViewInject(R.id.hgq_frame_rehuan)
	private RadioButton hot;
	@ViewInject(R.id.hgq_frame_fenlei)
	private RadioButton fenlei;
	@ViewInject(R.id.hgq_frame_jiage)
	private RadioButton jia;
	@ViewInject(R.id.hgq_selected_line)
	private ScrollView dataLine;
	@ViewInject(R.id.hgq_selected_dataLine)
	private LinearLayout dataLineList;
	private HGQFrameListAdapter adapter;
	private int defaultColor = Color.parseColor("#cccccc");
	private int page = 1;
	private boolean isFoot = false; // 用来判断是否已滑动到底部
	private String cid = "-1";
	private int backCateId = -1;
	private BitmapUtils bmUtil;
	private String method = Web.getHuanGouProductByCateId;
	private String ascOrDesc = "";
	private String salePrice = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Data.setProductClass(1);
		this.setContentView(R.layout.huangou_frame);
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

	public void init() {
		Util.initTop(this, "微商产品", Integer.MIN_VALUE, null);
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
				String cName = HuangouFrame.this.getIntent().getStringExtra(
						"name");
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
							salePrice = "";
						}
					}
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.get_RelatedCategory,
						"categoryID=-1");
				HashMap<String, List<Category>> map = new HashMap<String, List<Category>>();
				map.put("list", web.getList(Category.class));
				return map;
			}
		});
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
						if (firstVisibleItem + visibleItemCount == totalItemCount) {
							isFoot = true;
						}
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
				if (null == list || 0 == list.size()) {
					Util.show("没有更多微商商品信息！", HuangouFrame.this);
				}
				if (null == adapter) {
					adapter = new HGQFrameListAdapter(HuangouFrame.this, list,
							bmUtil);
					listView.setAdapter(adapter);
				} else {
					adapter.addData(list);
					adapter.updateUI();
				}
				page++;
			}

			@Override
			public Serializable run() {
				Web web = new Web(
						Web.yyrgAddress,
						Web.get_RelatedProductList,
						"categoryID_="
								+ cid
								+ "&aOrDesc_="
								+ ascOrDesc
								+ "&orderField_="
								+ salePrice
								+ "&KeyWord_=&priceFrom_=&priceTo_=&tradeid_=&price_=&PageSize_=20"
								+ "&page_=" + page);
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
	@OnClick(R.id.hgq_frame_jiage)
	public void jiaClick(final View v) {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		v.setEnabled(false);

		dataLine.setVisibility(FrameLayout.GONE);
		Drawable img_on, img_hot_on, img_fl_on, img_right_on, img_up_to_down, img_down_to_up;
		Resources res = getResources();
		img_on = res.getDrawable(R.drawable.lmsj_frame_px);
		img_right_on = res.getDrawable(R.drawable.lmsj_frame_jt);
		img_right_on.setBounds(0, 0, Util.dpToPx(this, 6),
				Util.dpToPx(this, 25));
		img_on.setBounds(0, 0, img_on.getMinimumWidth(),
				img_on.getMinimumHeight());
		img_hot_on = res.getDrawable(R.drawable.lmsj_frame_jl_white);
		img_hot_on.setBounds(0, 0, img_hot_on.getMinimumWidth(),
				img_hot_on.getMinimumHeight());
		img_fl_on = res.getDrawable(R.drawable.lmsj_frame_fl_white);
		img_fl_on.setBounds(0, 0, img_fl_on.getMinimumWidth(),
				img_fl_on.getMinimumHeight());

		img_up_to_down = res.getDrawable(R.drawable.price_down_to_up);
		img_up_to_down.setBounds(0, 0, img_up_to_down.getMinimumWidth(),
				img_up_to_down.getMinimumHeight());
		img_down_to_up = res.getDrawable(R.drawable.price_up_to_down);
		img_down_to_up.setBounds(0, 0, img_down_to_up.getMinimumWidth(),
				img_down_to_up.getMinimumHeight());
		final Drawable up = img_up_to_down;
		final Drawable down = img_down_to_up;
		final Drawable img_on_fi = img_on;
		dataLine.setVisibility(ScrollView.GONE);
		jia.setBackgroundColor(Color.WHITE);
		jia.setTextColor(getResources().getColor(R.color.black));
		hot.setBackgroundColor(getResources().getColor(R.color.new_goods_list));
		hot.setTextColor(getResources().getColor(R.color.bg));
		fenlei.setBackgroundColor(getResources().getColor(
				R.color.new_goods_list));
		fenlei.setCompoundDrawables(img_fl_on, null, null, null);
		hot.setCompoundDrawables(img_hot_on, null, null, null);
		fenlei.setTextColor(getResources().getColor(R.color.bg));
		adapter.clearData();
		// 当时升序还是降序
		// up降序，down升序
		final boolean isUp = "up".equals((v.getTag() + ""));
		TextView paixu = (TextView) v;
		// 这里要反着显示
		if (isUp) {
			paixu.setText("价格高到低");
			salePrice = "salePrice";
			ascOrDesc = "";
			jia.setCompoundDrawables(img_on_fi, null, up, null);
		} else {
			salePrice = "salePrice";
			paixu.setText("价格低到高");
			jia.setCompoundDrawables(img_on_fi, null, down, null);
			ascOrDesc = "asc";
		}
		v.setTag(isUp ? "down" : "up");
		page = 1;
		page();
		v.setEnabled(true);
	}

	/**
	 * 点击热卖
	 */
	@OnClick(R.id.hgq_frame_rehuan)
	public void hotClick(View v) {
		HotOnClick(v);
	}

	private void HotOnClick(View v) {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		v.setEnabled(false);
		dataLine.setVisibility(ScrollView.GONE);
		dataLineList.setVisibility(LinearLayout.GONE);
		Drawable img_on, img_fl_on, img_price_on, img_right_on, img_up_to_down, img_down_to_up;
		Resources res = getResources();
		img_right_on = res.getDrawable(R.drawable.lmsj_frame_jt);
		img_right_on.setBounds(0, 0, img_right_on.getMinimumWidth(),

		img_right_on.getMinimumWidth());
		img_on = res.getDrawable(R.drawable.lmsj_frame_jl);
		img_on.setBounds(0, 0, img_on.getMinimumWidth(),
				img_on.getMinimumHeight());
		img_fl_on = res.getDrawable(R.drawable.lmsj_frame_fl_white);
		img_fl_on.setBounds(0, 0, img_fl_on.getMinimumWidth(),
				img_fl_on.getMinimumHeight());
		img_price_on = res.getDrawable(R.drawable.lmsj_frame_px_white);
		img_price_on.setBounds(0, 0, img_price_on.getMinimumWidth(),

		img_price_on.getMinimumHeight());
		dataLine.setVisibility(ScrollView.GONE);
		hot.setCompoundDrawables(img_on, null, null, null);
		jia.setCompoundDrawables(img_price_on, null, null, null);
		fenlei.setCompoundDrawables(img_fl_on, null, null, null);
		hot.setBackgroundColor(Color.WHITE);
		hot.setTextColor(getResources().getColor(R.color.black));
		jia.setBackgroundColor(getResources().getColor(R.color.new_goods_list));
		jia.setTextColor(getResources().getColor(R.color.bg));
		fenlei.setBackgroundColor(getResources().getColor(
				R.color.new_goods_list));
		fenlei.setTextColor(getResources().getColor(R.color.bg));
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
				ascOrDesc = "";
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
		if (adapter != null) {
			adapter.clearData();
		}
		method = Web.getHotHGQ;
		page = 1;
		page();
		v.setEnabled(true);
	}

	/**
	 * 点击分类
	 * 
	 * @param v
	 */
	@OnClick(R.id.hgq_frame_fenlei)
	public void fenleiClick(View v) {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}
		v.setEnabled(true);
		Drawable img_on, img_hot_on, img_price_on, img_right_on, img_up_to_down, img_down_to_up;
		Resources res = getResources();
		img_on = res.getDrawable(R.drawable.lmsj_frame_fl);
		img_right_on = res.getDrawable(R.drawable.lmsj_frame_jt);
		img_right_on.setBounds(0, 0, img_right_on.getMinimumWidth(),

		img_right_on.getMinimumWidth());
		img_on.setBounds(0, 0, img_on.getMinimumWidth(),
				img_on.getMinimumHeight());
		img_hot_on = res.getDrawable(R.drawable.lmsj_frame_jl_white);
		img_hot_on.setBounds(0, 0, img_hot_on.getMinimumWidth(),
				img_hot_on.getMinimumHeight());
		img_price_on = res.getDrawable(R.drawable.lmsj_frame_px_white);
		img_price_on.setBounds(0, 0, img_price_on.getMinimumWidth(),

		img_price_on.getMinimumHeight());

		if (dataLine.getVisibility() == ScrollView.VISIBLE) {
			dataLine.setVisibility(ScrollView.GONE);
			dataLineList.setVisibility(LinearLayout.GONE);
			v.setEnabled(true);
			return;
		} else {
			dataLine.setVisibility(ScrollView.VISIBLE);
			dataLineList.setVisibility(LinearLayout.VISIBLE);
		}
		fenlei.setCompoundDrawables(img_on, null, null, null);
		hot.setCompoundDrawables(img_hot_on, null, null, null);
		jia.setCompoundDrawables(img_price_on, null, null, null);
		fenlei.setBackgroundColor(Color.WHITE);
		fenlei.setTextColor(getResources().getColor(R.color.black));
		jia.setBackgroundColor(getResources().getColor(R.color.new_goods_list));
		jia.setTextColor(getResources().getColor(R.color.bg));
		hot.setBackgroundColor(getResources().getColor(R.color.new_goods_list));
		hot.setTextColor(getResources().getColor(R.color.bg));
		img_up_to_down = res.getDrawable(R.drawable.price_down_to_up_white);
		img_up_to_down.setBounds(0, 0, Util.dpToPx(this, 6),
				Util.dpToPx(this, 20));
		img_down_to_up = res.getDrawable(R.drawable.price_up_to_down_white);
		img_down_to_up.setBounds(0, 0, Util.dpToPx(this, 6),
				Util.dpToPx(this, 20));
		final boolean isUp = "up".equals((jia.getTag() + ""));
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
		dataLineList.removeAllViews();
		dataLineList.removeAllViewsInLayout();
		TextView text = null;
		method = Web.getHuanGouProductByCateId;
		int _5dp = Util.dpToPx(this, 5F);
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
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			ll.setMargins(_5dp, _5dp, _5dp, _5dp);
			text.setPadding(_5dp, _5dp, _5dp, _5dp);
			text.setLayoutParams(ll);
			text.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cid = v.getTag() + "";
					dataLine.setVisibility(ScrollView.GONE);
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
		v.setEnabled(true);
	}
}

class HGQFrameListAdapter extends BaseAdapter {

	private Activity frame;
	private List<Product> list;
	private LayoutInflater flater;
	private BitmapUtils bmUtil;
	private BitmapDisplayConfig config;
	private int _90dp = 90;
	private int _100dp = 100;

	public HGQFrameListAdapter(Activity frame, List<Product> list,
			BitmapUtils bmUtil) {
		super();
		this.frame = frame;
		this.list = list;
		if (null == this.list)
			this.list = new ArrayList<Product>();
		flater = LayoutInflater.from(frame);
		this.bmUtil = bmUtil;
		_90dp = Util.dpToPx(frame, 90F);
		_100dp = Util.dpToPx(frame, 100F);
		config = new BitmapDisplayConfig();
		config.setBitmapMaxSize(new BitmapSize(_100dp, _90dp));
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

	public List<Product> getList() {
		return list;
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
		return list.get(arg0).hashCode();
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		if (0 == list.size())
			return view;
		final Product pro = list.get(arg0);
		HGQHodler holder = null;
		if (null == view) {
			view = flater.inflate(R.layout.huangou_frame_list_item, null);
			holder = new HGQHodler();
			holder.line = (LinearLayout) view
					.findViewById(R.id.huangou_list_item_line);
			holder.img = (ImageView) view
					.findViewById(R.id.huangou_list_item_img);
			holder.img_null = (ImageView) view
					.findViewById(R.id.huangou_list_item_img_null);
			holder.name = (TextView) view
					.findViewById(R.id.huangou_list_item_name);
			holder.shichang = (TextView) view
					.findViewById(R.id.huangou_list_item_shoujia);
			holder.yuanda = (TextView) view
					.findViewById(R.id.huangou_list_item_yuanda);
			holder.jifen = (TextView) view
					.findViewById(R.id.huangou_list_item_jifen);
			holder.sb = (TextView) view
					.findViewById(R.id.huangou_list_item_sbdh);
			view.setTag(holder);
		} else {
			holder = (HGQHodler) view.getTag();
		}
		holder.line.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Data.setProductClass(1);
				Util.showIntent(frame, ProductDeatilFream.class,
						new String[] { "url" },
						new String[] { pro.getProductid() });
			}
		});
		final String href = pro.getProductThumb().replaceFirst(
				"img.mall666.cn", Web.imgServer);
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
		DecimalFormat df = new DecimalFormat("#0.00");
		holder.name.setText(pro.getProductName());
		double scsj = Util.getDouble(pro.getPriceMarket());
		double ydsj = Util.getDouble(pro.getUser_pay());
		double jfsj = Util.getDouble(pro.getPrice());
		holder.shichang.getPaint().setFlags(
				Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);// 设置中划线
		holder.shichang.setText("市场售价：￥" + df.format(scsj));
		holder.yuanda.setText("￥" + df.format(ydsj + jfsj));
		holder.jifen.setText("￥"
				+ df.format(Double.parseDouble(pro.getPrice())) + "");
		holder.sb.setText(pro.getPriceOriginal());
		return view;
	}
}

class HGQHodler {
	public LinearLayout line;
	public ImageView img;
	public ImageView img_null;
	public TextView name;
	public TextView shichang;
	public TextView yuanda;
	public TextView jifen;
	public TextView sb;
}