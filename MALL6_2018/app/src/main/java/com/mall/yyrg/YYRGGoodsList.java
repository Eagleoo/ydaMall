package com.mall.yyrg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.yyrg.adapter.YYRGGoodsListAdapter;
import com.mall.yyrg.model.GoodBrandRecord;
import com.mall.yyrg.model.GoodClassRecord;
import com.mall.yyrg.model.NewAnnounce;

/**
 * 一元热购商品列表<br>
 * 
 * @author Administrator
 * 
 */
public class YYRGGoodsList extends Activity {
	protected static final int GETALLFENLEI = 0;
	protected static final int GETPINLAI = 1;
	@ViewInject(R.id.goodslist)
	private ListView goodslist;
	private int currentPageShop = 0;
	private YYRGGoodsListAdapter adapter;
	private int width;
	private static String message = "&classid=&Brandid=&Operation=&ordertype=";
	private PopupWindow distancePopup = null;
	@ViewInject(R.id.fenlei_name)
	private TextView fenlei_name;
	@ViewInject(R.id.goods_fenlei)
	private RelativeLayout goods_fenlei;
	@ViewInject(R.id.zuixin)
	private RelativeLayout goods_zuixin;
	// 所有的商品分类
	private List<GoodClassRecord> goodClassRecords = new ArrayList<GoodClassRecord>();
	// 所有的商品
	private List<NewAnnounce> newAnnounces = new ArrayList<NewAnnounce>();
	// 商品的分类
	private List<List<GoodBrandRecord>> allGoodBrandRecords = new ArrayList<List<GoodBrandRecord>>();
	private Handler handler;
	private TextView shop_car_t7;
	private BitmapUtils bmUtil;
	private View user_activity;
	@ViewInject(R.id.top_back)
	private TextView top_back;
	@ViewInject(R.id.goods_type)
	private TextView goods_type;
	private String type;
	private int count=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		user_activity = LayoutInflater.from(this).inflate(
				R.layout.yyrg_all_goods, null);
		setContentView(user_activity);
		ViewUtils.inject(this);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case GETALLFENLEI:
					System.out.println("开始for循环");
					String[] strings=new String[goodClassRecords.size()+1];
					for (int i = 0; i < goodClassRecords.size(); i++) {
						if (i == 0) {

							strings[i]="全部";
						} else {
							strings[i]=goodClassRecords.get(i)
									.getCategoryId();
						}

					}
					getGoodBrandRecord(strings);
					break;
				case 3:
					NewAnnounce newAnnounce = (NewAnnounce) msg.obj;
					if (null == UserData.getUser()) {
						Util.showIntent("您还没登录，现在去登录吗？", YYRGGoodsList.this,
								LoginFrame.class, null);
						return;
					} else {
						shouPop(newAnnounce);
					}
					break;
				}
			}
		};

		type = getIntent().getStringExtra("type");
		if ("zuixin".equals(type)) {
			goods_type.setText("最新");
			message = "&classid=&Brandid=&Operation=createTime&ordertype=1";
			changeColor(goods_type);
			top_back.setVisibility(View.VISIBLE);
		} else if ("renqi".equals(type)) {
			goods_type.setText("人气");
			message = "&classid=&Brandid=&Operation=periodName&ordertype=1";
			changeColor(goods_type);
			top_back.setVisibility(View.VISIBLE);
		} else if ("jjjx".equals(type)) {
			goods_type.setText("即将揭晓");
			message = "&classid=&Brandid=&Operation=&ordertype=1";
			changeColor(goods_type);
			top_back.setVisibility(View.VISIBLE);
		} else {
			goods_type.setText("最新");
			changeColor(goods_type);
			top_back.setVisibility(View.GONE);
		}
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		if (adapter == null) {
			adapter = new YYRGGoodsListAdapter(this, width, handler);
			goodslist.setAdapter(adapter);
		}
		firstpageshop();
		scrollPageshop();
		GoodClassRecord goodClassRecord = new GoodClassRecord();
		goodClassRecord.setCategoryName("全部");
		goodClassRecords.add(goodClassRecord);

	}
	private void changeColor(TextView view) {
		fenlei_name.setTextColor(getResources().getColor(R.color.yyrg_topcolor));
		goods_type.setTextColor(getResources().getColor(
				R.color.yyrg_topcolor));
		
		view.setTextColor(getResources().getColor(R.color.bg));
		fenlei_name.setBackgroundColor(getResources().getColor(R.color.bg));
		goods_type.setBackgroundColor(getResources().getColor(R.color.bg));
		view.setBackgroundColor(getResources().getColor(R.color.yyrg_topcolor));
	}
	@OnClick(R.id.top_back)
	public void returnBack(View view) {
		finish();
	}

	public class OnChildClickListenerImpl implements OnChildClickListener {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			if (goodClassRecords.get(groupPosition).getCategoryName()
					.equals("全部")) {
				fenlei_name.setText("全部分类");
			} else {
				fenlei_name.setText(goodClassRecords.get(groupPosition)
						.getCategoryName());
			}
			message = "&classid="
					+ goodClassRecords.get(groupPosition).getCategoryId()
					+ "&Brandid="
					+ allGoodBrandRecords.get(groupPosition).get(childPosition)
							.getTradeMarkid() + "&Operation=&ordertype=";
			newAnnounces.clear();
			currentPageShop = 0;
			adapter = new YYRGGoodsListAdapter(YYRGGoodsList.this, width,
					handler);
			goodslist.setAdapter(adapter);
			firstpageshop();
			scrollPageshop();
			distancePopup.dismiss();
			return false;
		}
	}

	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
		int type = ExpandableListView
				.getPackedPositionType(info.packedPosition);
		int group = ExpandableListView
				.getPackedPositionGroup(info.packedPosition);

		int child = ExpandableListView
				.getPackedPositionChild(info.packedPosition);
		Toast.makeText(YYRGGoodsList.this,
				"type=" + type + ", group=" + group + ", child=" + child,
				Toast.LENGTH_SHORT).show();

	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */

	private void startPopupWindow() {
		View pview = getLayoutInflater().inflate(
				R.layout.yyrg_item_goods_fenlei, null, false);
		ExpandableListView elistview = null;
		ExpandableListAdapter adapters = null;
		elistview = (ExpandableListView) pview.findViewById(R.id.elistview1);
		adapters = new LeftExpandableListAdapter(this);
		elistview.setAdapter(adapters);
		registerForContextMenu(elistview);
		elistview.setOnChildClickListener(new OnChildClickListenerImpl());
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
	@SuppressWarnings("deprecation")
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		// distancePopup.setAnimationStyle(R.style.popupanimation);
	}

	@OnClick({ R.id.sousuo, R.id.zuixin, R.id.goods_fenlei })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.sousuo:// 搜索商品

			break;

		case R.id.zuixin:// 显示最新的商品
			changeColor(goods_type);
			if ("zuixin".equals(type)) {
				message = "&classid=&Brandid=&Operation=createTime&ordertype=1";
			} else if ("renqi".equals(type)) {
				message = "&classid=&Brandid=&Operation=periodName&ordertype=1";
			} else if ("jjjx".equals(type)) {
				message = "&classid=&Brandid=&Operation=&ordertype=1";
			} else {
				message = "&classid=&Brandid=&Operation=&ordertype=1";
			}
			newAnnounces.clear();
			currentPageShop = 0;
			adapter = new YYRGGoodsListAdapter(YYRGGoodsList.this, width,
					handler);
			goodslist.setAdapter(adapter);
			firstpageshop();
			scrollPageshop();
			break;
		case R.id.goods_fenlei:// 显示商品的分类
			changeColor(fenlei_name);
			if (goodClassRecords.size() > 1) {
				getPopupWindow();
				startPopupWindow();
				distancePopup.showAsDropDown(view);
			} else {
				getGoodClassRecord(view);
			}
			break;

		}
	}

	/**
	 * 获得商品分类
	 */
	private void getGoodClassRecord(final View view) {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<GoodClassRecord> list = new ArrayList<GoodClassRecord>();
					list = ((HashMap<Integer, List<GoodClassRecord>>) runData)
							.get(1);
					if (list.size() > 0) {
						goodClassRecords.clear();
						GoodClassRecord goodClassRecord = new GoodClassRecord();
						goodClassRecord.setCategoryName("全部");
						goodClassRecord.setCategoryId("全部");
						goodClassRecords.add(goodClassRecord);
						goodClassRecords.addAll(list);
						Message msg = new Message();
						msg.what = GETALLFENLEI;
						handler.sendMessage(msg);
					}
				} else {
					Toast.makeText(YYRGGoodsList.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.getGoodClassRecord, "UTF-8");
				List<GoodClassRecord> list = web.getList(GoodClassRecord.class);
				HashMap<Integer, List<GoodClassRecord>> map = new HashMap<Integer, List<GoodClassRecord>>();
				map.put(1, list);
				return map;
			}
		});
	}

	/**
	 * 获得商品列表信息
	 */
	private void getGoodProductListRecord() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<NewAnnounce> list = new ArrayList<NewAnnounce>();
					list = ((HashMap<Integer, List<NewAnnounce>>) runData)
							.get(index++);
					if (list.size() > 0) {
						newAnnounces.addAll(list);
						adapter.setList(list);

					}else {
						Toast.makeText(YYRGGoodsList.this, "网络不给力哦，请检查网络是否连接后，再试一下",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(YYRGGoodsList.this, "网络不给力哦，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}
			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress,
						Web.getGoodProductListRecord, "pagesize=10"
								+ "&pageindex=" + (++currentPageShop) + message);
				List<NewAnnounce> list = web.getList(NewAnnounce.class);
				HashMap<Integer, List<NewAnnounce>> map = new HashMap<Integer, List<NewAnnounce>>();
				map.put(index++, list);
				return map;
			}
		});
	}

	public void firstpageshop() {
		getGoodProductListRecord();
	}

	public void scrollPageshop() {
		goodslist.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getGoodProductListRecord();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	@OnItemClick(R.id.goodslist)
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, YYRGGoodsMessage.class);
		intent.putExtra("ypid", newAnnounces.get(arg2).getYpid());
		intent.putExtra("goodsid", newAnnounces.get(arg2).getYppid());
		startActivity(intent);
	}

	/**
	 * 商品品牌
	 */
	private void getGoodBrandRecord(final String[] classid) {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<GoodBrandRecord> list = new ArrayList<GoodBrandRecord>();
					list = ((HashMap<Integer, List<GoodBrandRecord>>) runData)
							.get(1);
					if (list.size() > 0) {
						GoodBrandRecord goodBrandRecord = new GoodBrandRecord();
						goodBrandRecord.setTrademarkName("全部");
						List<GoodBrandRecord> goodBrandRecords = new ArrayList<GoodBrandRecord>();
						goodBrandRecords.add(goodBrandRecord);
						goodBrandRecords.addAll(list);
						allGoodBrandRecords.add(goodBrandRecords);
						count++;
					}
				} else {
					Toast.makeText(YYRGGoodsList.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
				if (count==(classid.length-1)) {
					getPopupWindow();
					startPopupWindow();
					distancePopup.showAsDropDown(goods_fenlei);
				}else {
					getGoodBrandRecord(classid);
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = null;
				System.out.println(classid[count] + "    dangqiande");
				if (classid[count].equals("全部")) {
					web = new Web(Web.yyrgAddress, Web.getGoodBrandRecord,
							"classid=");
				} else {
					web = new Web(Web.yyrgAddress, Web.getGoodBrandRecord,
							"classid=" + classid[count]);
				}

				List<GoodBrandRecord> list = web.getList(GoodBrandRecord.class);
				HashMap<Integer, List<GoodBrandRecord>> map = new HashMap<Integer, List<GoodBrandRecord>>();
				map.put(1, list);
				return map;
			}
		});
	}

	/**
	 * 树状的商品分类品牌的显示
	 */
	class LeftExpandableListAdapter extends BaseExpandableListAdapter {
		private Context context = null;

		private LayoutInflater inflater;

		public LeftExpandableListAdapter(Context context) {
		//	Toast.makeText(YYRGGoodsList.this, "数", duration)
			this.context = context;
			inflater = LayoutInflater.from(context);

		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return allGoodBrandRecords.get(groupPosition).get(childPosition)
					.getTrademarkName();
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		public TextView bulidTextView() {
			AbsListView.LayoutParams param = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, 35);
			TextView textView = new TextView(this.context);
			ImageView imageView = new ImageView(this.context);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View converView, ViewGroup parent) {
			if (converView == null) {
				converView = inflater.inflate(R.layout.yyrg_pinpai_item, null);
			}
			TextView textView = (TextView) converView.findViewById(R.id.item);
			textView.setTextSize(13);
			textView.setText(getChild(groupPosition, childPosition).toString());
			textView.setPadding(30, 0, 0, 0);
			return converView;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder groupHolder = null;
			if (convertView == null) {
				groupHolder = new GroupHolder();
				convertView = inflater.inflate(
						R.layout.yyrg_goods_fenlei_tree_item, null);
				groupHolder.textView = (TextView) convertView
						.findViewById(R.id.text);
				groupHolder.imageL = (ImageView) convertView
						.findViewById(R.id.icon);
				groupHolder.textView.setTextSize(15);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}

			groupHolder.textView.setText(getGroup(groupPosition).toString());
			if (isExpanded)// ture is Expanded or false is not isExpanded
				groupHolder.imageL.setImageResource(R.drawable.tree_down);
			else
				groupHolder.imageL.setImageResource(R.drawable.tree_right);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return allGoodBrandRecords.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return goodClassRecords.get(groupPosition).getCategoryName();
		}

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return goodClassRecords.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return true;
		}

		class GroupHolder {
			TextView textView;
			ImageView imageL;
		}

	}

	/**
	 * 添加购物车
	 */
	private void addHotShopCar(final String yypid, final String temp,
			final String ypid) {
		Util.asynTask(this, "正在加入购物车……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					if ("success".equals(runData + "")) {
						Toast.makeText(YYRGGoodsList.this, "恭喜你，添加到购物车成功",
								Toast.LENGTH_SHORT).show();

					} else {
						Util.show(runData + "", YYRGGoodsList.this);
					}
				} else {
					Toast.makeText(YYRGGoodsList.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.addHotShopCar, "yppid="
						+ yypid + "&userID=" + UserData.getUser().getUserId()
						+ "&userPaw=" + UserData.getUser().getMd5Pwd()
						+ "&ypid=" + ypid + "&personTims=" + temp);
				return web.getPlan();
			}
		});
	}

	private void setImage(final ImageView logo, String href, final int width,
			final int height) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				arg2 = Util.zoomBitmap(arg2, width, height);
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
			if(null != mPopupWindow)
			mPopupWindow.dismiss();
			} else if (keyCode == KeyEvent.KEYCODE_MENU) {
				if(null != mPopupWindow)
				mPopupWindow.dismiss();
			} 
			return super.onKeyDown(keyCode, event);
	}
	/**
	 * 显示父类的POP 推出
	 */
	PopupWindow mPopupWindow;

	public void shouPop(final NewAnnounce newAnnounce) { 
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View pview = inflater.inflate(R.layout.yyrg_select_goods_count, null);
		pview.getBackground().setAlpha(220);
		ImageView one_goods_img = (ImageView) pview
				.findViewById(R.id.one_goods_img);
		TextView goods_price = (TextView) pview.findViewById(R.id.goods_price);
		final TextView goods_zhifu = (TextView) pview
				.findViewById(R.id.goods_zhifu);
		MyProgressView progress = (MyProgressView) pview
				.findViewById(R.id.progress);
		TextView canyu = (TextView) pview.findViewById(R.id.canyu);
		TextView zongxu = (TextView) pview.findViewById(R.id.zongxu);
		final TextView shengyu = (TextView) pview.findViewById(R.id.shengyu);
		Button shop_car_button1 = (Button) pview
				.findViewById(R.id.shop_car_button1);
		Button shop_car_button2 = (Button) pview
				.findViewById(R.id.shop_car_button2);
		shop_car_t7 = (TextView) pview.findViewById(R.id.shop_car_t7);
		TextView t1 = (TextView) pview.findViewById(R.id.t1);
		ImageView guanbi = (ImageView) pview.findViewById(R.id.guanbi);
		Button add_shopcart = (Button) pview.findViewById(R.id.add_shopcart);
		goods_price.setText("价值：￥" + newAnnounce.getPrice());
		progress.setMaxCount(Integer.parseInt(newAnnounce.getTotalPersonTimes()) * 1.00f);
		progress.setCurrentCount(Integer.parseInt(newAnnounce.getPersonTimes()) * 1.00f);
		canyu.setText(newAnnounce.getPersonTimes());
		zongxu.setText(newAnnounce.getTotalPersonTimes());
		setImage(one_goods_img, newAnnounce.getPhotoThumb(), width / 5,
				width / 5);
		shengyu.setText(""
				+ ((Integer.parseInt(newAnnounce.getTotalPersonTimes()) - Integer
						.parseInt(newAnnounce.getPersonTimes()))));

		shop_car_button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int count = Integer.parseInt(shop_car_t7.getText().toString()
						.trim());
				int allcount = Integer.parseInt(shengyu.getText().toString()
						.trim());
				// TODO Auto-generated method stub
				if (count > 1) {
					goods_zhifu.setText("需支付：￥" + (count - 1) + "");
					shop_car_t7.setText(count - 1 + "");
				}
			}
		});
		shop_car_button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int count = Integer.parseInt(shop_car_t7.getText().toString()
						.trim());
				int allcount = Integer.parseInt(shengyu.getText().toString()
						.trim());
				// TODO Auto-generated method stub
				if (allcount > count) {
					goods_zhifu.setText("需支付：￥" + (count + 1) + "");
					shop_car_t7.setText(count + 1 + "");
				} else {
					Toast.makeText(YYRGGoodsList.this,
							"当前只能热购" + allcount + "次", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		guanbi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();

			}
		});
		add_shopcart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();
				if (Integer.parseInt(shengyu.getText().toString().trim())==0) {
					Toast.makeText(YYRGGoodsList.this, "已满员", Toast.LENGTH_SHORT).show();
					return;
				}
				addHotShopCar(newAnnounce.getYppid(), shop_car_t7.getText()
						.toString().trim(), newAnnounce.getYpid());
			}
		});
		t1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();

			}
		});
		mPopupWindow = showPop(LayoutParams.FILL_PARENT, 0, false,
				Gravity.BOTTOM, pview, pview);
	}

	public PopupWindow showPop(int witch, int hight_bottom, boolean isDown,
			int gravity, View... view) {
		PopupWindow mPopupWindow = new PopupWindow(view[0], witch,
				LayoutParams.FILL_PARENT);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 点击空白时popupwindow关闭
		int[] location = new int[2];
		try {
			view[1].getLocationOnScreen(location);
			mPopupWindow.showAtLocation(view[1], gravity, 10, hight_bottom);
			mPopupWindow.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mPopupWindow;
	}

}
