package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("NewApi")
public class ProductListFrame extends FragmentActivity implements OnClickListener {

	private Intent intent = null;
	private BitmapUtils bmUtil;
	public int sreachPage = 1;
	private String cid = "-1";
	private String type="";
	private String cateName = "";
	@ViewInject(R.id.product_list_xinpin)
	private TextView product_list_xinpin;
	@ViewInject(R.id.product_list_xiaoliang)
	private TextView product_list_xiaoliang;
	@ViewInject(R.id.product_list_jiage)
	private TextView product_list_jiage;
	@ViewInject(R.id.product_list_pingjia)
	private TextView product_list_pingjia;
	@ViewInject(R.id.product_list_fragment_gridView)
	private GridView gridView;
	@ViewInject(R.id.goods_type_layout)
	private FrameLayout goods_type_layout;
	@ViewInject(R.id.topCenter)
	private TextView topCenter;
	@ViewInject(R.id.topright_shaixuan)
	private TextView ywjj_saixuan;

	private View topback;
	@ViewInject(R.id.topright_pingpu)
	private View topright_pingpu;
	@ViewInject(R.id.topright_liebao)
	private View topright_liebao;
	
	@ViewInject(R.id.topback1)
	private ImageView topback1;
	
	@ViewInject(R.id.search1)
	private TextView search1;
	String[] str = null;
	
	
	@ViewInject(R.id.ll1)
	private View ll1;
	
	private Object lockObject = new Object();
	private boolean isLoading = false;

	 private BaseMallAdapter<JSONObject> adapter = null;
	private int page = 1;
	private int size = 12;
	private int getLastVisiblePosition = 0, lastVisiblePositionY = 0;
	private String params = "";
	private boolean isShowCate = false;
	private String ajax = "";
	private String ascOrDesc = "desc";
	int numce=0;
	String from = "";
	
	String state="";
	
	

	private boolean ishowiamge=true;
	
	private static final Map<String, String> cateNames = new HashMap<String, String>();

	static {
		cateNames.put("419", "潮流服饰");
		cateNames.put("1471", "精品鞋城");
		cateNames.put("589", "精品箱包");
		cateNames.put("498", "个护化妆");
		cateNames.put("1178", "珠宝饰品");
		cateNames.put("526", "居家百货");
		cateNames.put("707", "汽车配件");
		cateNames.put("469", "食品保健");
		cateNames.put("1147", "手机电脑");
		cateNames.put("1093", "生活家电");
		cateNames.put("1079", "创业工具");
		cateNames.put("1085", "联盟商家工具");
	}
	
	private Context context;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_list_frame);
		ViewUtils.inject(this);
		context=this;
		intent = getIntent();
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.zw174);
		// 得到传入的参数--分类ID
		cid = "-1";
		if (!Util.isNull(intent.getStringExtra("catId")))
			cid = intent.getStringExtra("catId");
		
		
		if (!Util.isNull(intent.getStringExtra("type")))
			type = intent.getStringExtra("type");
		
		
		
		if (!Util.isNull(intent.getStringExtra("state")))
			state = intent.getStringExtra("state");
		
		if (cid.equals("-2")) {
			cid="";
			
		}
		
		Log.e("获取到cid",cid+"^^^^^^");
		ajax = intent.getStringExtra("ajax");
		topright_liebao.setVisibility(View.VISIBLE);

		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		init();
		initcoloer();
//		ll1.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//			showPopupWindow(ll1);	
//			}
//		});
		topback1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			finish();	
			}
		});
		
		search1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ProductListFrame.this, SPSearch.class);
				startActivityForResult(intent, 123);
			}
		});
			

	}
	

	


	private void showMessage(String message) {
		Dialog dialog = new Dialog(this);
		dialog.setTitle(null);
		View view = LayoutInflater.from(this).inflate(R.layout.tip_phone_account, null);
		TextView text = (TextView) view.findViewById(R.id.dialog_phone_text);
		text.setText(message);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.show();
	}

	// 逛商城 + 送话费
	private void AddRecord() {
		final User user = UserData.getUser();
		if (null == user)
			return;
		// Util.asynTask(new IAsynTask() {
		// @Override
		// public void updateUI(Serializable runData) {
		// if ((runData + "").contains("success:")) {
		// String[] ayy = (runData + "").split(":");
		// showMessage("找商品赠送话费：" + ayy[1] + "元");
		// }
		// }
		//
		// @Override
		// public Serializable run() {
//		 Web web = new Web(Web.AddRecord, "userID=" + user.getUserId() +
		// "&userPaw=" + UserData.getUser().getMd5Pwd() + "&listID=" + cid +
		// "&listType=" + cateName
		// + "&commodityID=-1&commodityName=-1");
		// return web.getPlan();
		// }
		// });
	}
	
	
	public void initcoloer(){
		product_list_xinpin.setBackground(null);
		product_list_xiaoliang.setBackground(null);
		product_list_jiage.setBackground(null);
		product_list_pingjia.setBackground(null);
		product_list_xinpin.setTextColor(Color.parseColor("#cf0000"));
		product_list_xiaoliang.setTextColor(Color.parseColor("#535353"));
		product_list_jiage.setTextColor(Color.parseColor("#535353"));
		product_list_pingjia.setTextColor(Color.parseColor("#535353"));
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.triangledown);
		Matrix matrix = new Matrix();
		matrix.setRotate(0);
		Bitmap bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		Drawable draw = new BitmapDrawable(bm1);
		draw.setBounds(0, 0, bm1.getWidth(), bm1.getHeight());
		product_list_xinpin.setCompoundDrawables(null, null, draw, null);
		product_list_xiaoliang.setCompoundDrawables(null, null, draw, null);
		product_list_jiage.setCompoundDrawables(null, null, draw, null);
		product_list_pingjia.setCompoundDrawables(null, null, draw, null);
		
	}

	@SuppressLint("NewApi")
	@OnClick({ R.id.product_list_xinpin, R.id.product_list_xiaoliang, R.id.product_list_jiage,
			R.id.product_list_pingjia })
	public void titleClick(View view) {
		product_list_xinpin.setBackground(null);
		product_list_xiaoliang.setBackground(null);
		product_list_jiage.setBackground(null);
		product_list_pingjia.setBackground(null);
		product_list_xinpin.setTextColor(Color.parseColor("#535353"));
		product_list_xiaoliang.setTextColor(Color.parseColor("#535353"));
		product_list_jiage.setTextColor(Color.parseColor("#535353"));
		product_list_pingjia.setTextColor(Color.parseColor("#535353"));
//		view.setBackgroundResource(R.drawable.base_tabpager_indicator_selected);
		((TextView) view).setTextColor(Color.parseColor("#cf0000"));
		if (null != adapter) {
			adapter.clear();
			adapter = null;
			page = 1;
			if ("asc".equals(ascOrDesc))
				ascOrDesc = "desc";
			else
				ascOrDesc = "asc";
		}
		String  s="1";
		params = "type=新品&ascOrDesc=" + ascOrDesc + "&cid=" + cid + "&ajax=" + ajax;
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.sort_reddown);
		Matrix matrix = new Matrix();
		if ("asc".equals(ascOrDesc)){
			matrix.setRotate(180);
			s="2";
		}
		
		Bitmap bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		Drawable draw = new BitmapDrawable(bm1);
		draw.setBounds(0, 0, bm1.getWidth(), bm1.getHeight());
		if (view.getId() == product_list_xiaoliang.getId()) {
			params = "type=销量&ascOrDesc=" + ascOrDesc + "&cid=" + cid + "&ajax=" + ajax;
			product_list_xiaoliang.setCompoundDrawables(null, null, draw, null);
//			if (topright_liebao.getVisibility() == View.GONE) {
//				loadData(params, R.layout.product_item_fragment_list_item,"1");
//			} else {
//				loadData(params, R.layout.product_item_fragment_item,"1");
//
//			}
			showPopupWindow(ll1,"销量"+s);	
		} else if (view.getId() == product_list_jiage.getId()) {
			params = "type=价格&ascOrDesc=" + ascOrDesc + "&cid=" + cid + "&ajax=" + ajax;
			product_list_jiage.setCompoundDrawables(null, null, draw, null);
//			if (topright_liebao.getVisibility() == View.GONE) {
//				loadData(params, R.layout.product_item_fragment_list_item,"1");
//			} else {
//				loadData(params, R.layout.product_item_fragment_item,"1");
//
//			}
			showPopupWindow(ll1,"价格"+s);	
		} else if (view.getId() == product_list_pingjia.getId()) {
			params = "type=评价&ascOrDesc=" + ascOrDesc + "&cid=" + cid + "&ajax=" + ajax;
			product_list_pingjia.setCompoundDrawables(null, null, draw, null);
//			if (topright_liebao.getVisibility() == View.GONE) {
//				loadData(params, R.layout.product_item_fragment_list_item,"1");
//			} else {
//				loadData(params, R.layout.product_item_fragment_item,"1");
//
//			}
			showPopupWindow(ll1,"评价"+s);	
		}else if(view.getId() == R.id.product_list_xinpin){
			Bitmap bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.sort_reddown);
			Matrix matrix1 = new Matrix();
			matrix1.setRotate(0);
			Bitmap bm11 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(), bitmap1.getHeight(), matrix1, true);
			Drawable draw11 = new BitmapDrawable(bm11);
			draw11.setBounds(0, 0, bm11.getWidth(), bm11.getHeight());
			product_list_xinpin.setCompoundDrawables(null, null, draw11, null);
			showPopupWindow(ll1,"类别");	
		}
	}

	public void initType() {

		topCenter.setText(cateNames.get(cid));

		String topCentertext = topCenter.getText().toString();
		if (!topCentertext.contains("创业工具") || !topCentertext.contains("联盟商家工具") || !topCentertext.contains("全部工具")) {
			ywjj_saixuan.setVisibility(View.GONE);
		}

		if (!"1079".equals(cid) && !"1085".equals(cid) && !"全部工具".equals(cid)) {
			ywjj_saixuan.setVisibility(View.GONE);
		} else
			ywjj_saixuan.setVisibility(View.VISIBLE);

		page = 1;
		
		if (type.equals("-2")) {
			type = "";
			ascOrDesc="desc";
			topback1.setVisibility(View.INVISIBLE);
		}else{
			type = "销量";
			
		}
		if (state.equals("1")||state.equals("2")) {
			params = "type="+"&ascOrDesc=" + ascOrDesc + "&cid=" +"&state="+state;
		}else{
			params = "type="+type+"&ascOrDesc=" + ascOrDesc + "&cid=" + cid;
		}
		
		goods_type_layout.setVisibility(View.GONE);
		isShowCate = false;
		loadData(params, R.layout.product_item_fragment_item,"5");

	}

	public void initGoodsTypeView() {

		topCenter.setText("商品分类");

		isShowCate = true;
		goods_type_layout.setVisibility(View.VISIBLE);
		topright_liebao.setVisibility(View.GONE);
		topright_pingpu.setVisibility(View.GONE);
		String topCentertext = topCenter.getText().toString();
		if (!topCentertext.contains("创业工具") || !topCentertext.contains("联盟商家工具") || !topCentertext.contains("全部工具")) {
			ywjj_saixuan.setVisibility(View.GONE);
		}
		LinearLayout main_layout1_clfs = (LinearLayout) this.findViewById(R.id.main_layout1_clfs);
		LinearLayout main_layout1_gxhz = (LinearLayout) this.findViewById(R.id.main_layout1_gxhz);
		LinearLayout main_layout1_jjbh = (LinearLayout) this.findViewById(R.id.main_layout1_jjbh);
		LinearLayout main_layout1_jpxc = (LinearLayout) this.findViewById(R.id.main_layout1_jpxc);
		LinearLayout main_layout1_qcpj = (LinearLayout) this.findViewById(R.id.main_layout1_qcpj);
		LinearLayout main_layout1_shjd = (LinearLayout) this.findViewById(R.id.main_layout1_shjd);
		LinearLayout main_layout1_sjsm = (LinearLayout) this.findViewById(R.id.main_layout1_sjsm);
		LinearLayout main_layout1_zbsp = (LinearLayout) this.findViewById(R.id.main_layout1_zbsp);
		LinearLayout main_layout1_spbj = (LinearLayout) this.findViewById(R.id.main_layout1_spbj);
		LinearLayout main_layout1_jpxb = (LinearLayout) this.findViewById(R.id.main_layout1_jpxb);
		LinearLayout main_layout1_ywgj = (LinearLayout) this.findViewById(R.id.main_layout1_ywgj);
		main_layout1_clfs.setOnClickListener(this);
		main_layout1_gxhz.setOnClickListener(this);
		main_layout1_jjbh.setOnClickListener(this);
		main_layout1_jpxc.setOnClickListener(this);
		main_layout1_qcpj.setOnClickListener(this);
		main_layout1_shjd.setOnClickListener(this);
		main_layout1_sjsm.setOnClickListener(this);
		main_layout1_zbsp.setOnClickListener(this);
		main_layout1_spbj.setOnClickListener(this);
		main_layout1_jpxb.setOnClickListener(this);
		main_layout1_ywgj.setOnClickListener(this);

	}
	
	private int a1 = 0;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    a1 = getWindowManager().getDefaultDisplay().getWidth() / 2;

	}

	@Override
	public void onClick(View v) {
		cid = v.getTag() + "";
		if (!"1079".equals(cid) && !"1085".equals(cid) && !"全部工具".equals(cid)) {
			ywjj_saixuan.setVisibility(View.GONE);
		} else
			ywjj_saixuan.setVisibility(View.VISIBLE);

		topCenter.setText(cateNames.get(cid));
		page = 1;
		params = "type=新品&ascOrDesc=" + ascOrDesc + "&cid=" + cid;
		goods_type_layout.setVisibility(View.GONE);
		isShowCate = false;

		topright_liebao.setVisibility(View.VISIBLE);// 列表模式设置可见

		loadData(params, R.layout.product_item_fragment_item,"1");

	}

	private void loadData(String params, final int layoutid,final String str) {
		
		Log.e("cid",cid);
		if ("-1".equals(cid))
			return;
		synchronized (lockObject) {
			if (isLoading)
				return;
			isLoading = true;
		}
		final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
		NewWebAPI.getNewInstance().getWebRequest(
				"/Product.aspx?call=getProductList&" + params + "&page=" + page + "&size=" + size,
				new WebRequestCallBack() {
					@Override
					public void success(Object result) {
						if (Util.isNull(result)) {
							Util.show("网络错误，请重试！", ProductListFrame.this);
							return;
						}
						JSONObject json = JSON.parseObject(result.toString());
						if (200 != json.getIntValue("code")) {
							Util.show(json.getString("message"), ProductListFrame.this);
							return;
						}
						JSONObject[] objs = json.getJSONArray("list").toArray(new JSONObject[] {});
					
						if (null == adapter || 1 == page) {
							Log.e("状态","1");
							 adapter = (new
							 BaseMallAdapter<JSONObject>(layoutid,
							 ProductListFrame.this, objs) {
							 @Override
							 public View getView(int position, View
							 convertView, ViewGroup parent, final JSONObject
							 t) {
							 switch (layoutid) {
							 case R.layout.product_item_fragment_item:
								  Log.e("数据6666",Util.aa +"");
								
//									AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(Util.aa/2, Util.aa/2);
							        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) convertView.getLayoutParams();
									if (layoutParams == null) {
										layoutParams = new AbsListView.LayoutParams(Util.aa/2, Util.aa/2);
							            convertView.setLayoutParams(layoutParams);
							        } else {
							        	layoutParams.height = Util.aa/2;
							        	layoutParams.width = Util.aa/2;
							        }

					
									
//									convertView.setLayoutParams(layoutParams);
									
									
									ImageView imageView =((ImageView) convertView.findViewById(R.id.badgeiv));
									
									
									if (t.getString("jifen").equals("0.00")||t.getString("jifen").equals("0")) {
										imageView.setVisibility(View.INVISIBLE);
									}else{
										imageView.setVisibility(View.VISIBLE);
									}
								 imageView.setVisibility(View.INVISIBLE);
					
							 setImage(R.id.product_list_item_img,
							 t.getString("thumb"),Util.aa/2);
							 setText(R.id.product_list_item_name,
							 t.getString("name"));
							 setText(R.id.product_list_item_sbj,
							 t.getString("sbj"));
							 setText(R.id.product_list_item_yuanda_price,
							 t.getString("price"));
							 setText(R.id.product_list_item_yuanda_jifen,
							 t.getString("jifen"));
							 break;
							
							 case R.layout.product_item_fragment_list_item:
							 setImage(R.id.huangou_list_item_img,
							 t.getString("thumb").replaceAll("230_230_",
							 "174_174_"));
							 setText(R.id.huangou_list_item_name,
							 t.getString("name"));
							 setText(R.id.huangou_list_item_sbdh,
							 t.getString("sbj"));
							 setText(R.id.huangou_list_item_yuanda,
							 t.getString("price"));
							 setText(R.id.huangou_list_item_jifen,
							 t.getString("jifen"));
							
							 break;
							 }
							 OnClickListener click = new OnClickListener() {
							 @Override
							 public void onClick(View v) {
								 Log.e("Itme点击","点击");
							 Util.showIntent(context,
							 ProductDeatilFream.class, new String[] { "url" ,"tagkk"},
							 new String[] { t.getString("pid") ,ishowiamge+""});
							 }
							 };
							
							 convertView.setOnClickListener(click);
							 return convertView;
							 }
							 });
							gridView.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							
						} else if(str.equals("2")){
							Log.e("状态","2");
							adapter.clear();
							adapter.add(objs);
							adapter.showimage(ishowiamge);
							adapter.updateUI();
							
						}
						
						else {
							Log.e("状态","3");
							adapter.add(objs);
							adapter.updateUI();
						}
						page++;
						
					
						if (layoutid == R.layout.product_item_fragment_item)
							
							gridView.setNumColumns(2);
						else
							gridView.setNumColumns(1);
					}

					@Override
					public void requestEnd() {
						super.requestEnd();
						cpd.dismiss();
						isLoading = false;
					}

					@Override
					public void fail(Throwable e) {
						super.fail(e);
					}
				});
		// AddRecord();
	}

	public void init() {
		if (!Util.isNetworkConnected(this)) {
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}

		gridView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// 滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						View v = (View) view.getChildAt(view.getChildCount() - 1);
						if (null == v)
							return;
						int[] location = new int[2];
						v.getLocationOnScreen(location);// 获取在整个屏幕内的绝对坐标
						int y = location[1];

						if (view.getLastVisiblePosition() != getLastVisiblePosition && lastVisiblePositionY != y) {
							if (topright_liebao.getVisibility() == View.VISIBLE)

								loadData(params, R.layout.product_item_fragment_item,"1");
							else
								loadData(params, R.layout.product_item_fragment_list_item,"1");

							return;
						}
					}
					getLastVisiblePosition = 0;
					lastVisiblePositionY = 0;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});

		cateName = intent.getStringExtra("catName");
		Log.e("cateName",cateName+"KKKKKKKK");
		if (TextUtils.isEmpty(cateName)) {
			Log.e("cateName","1");
			cateName = "商品分类";
			initGoodsTypeView();
		} else {
			Log.e("cateName","2");
			topCenter.setText(cateName);
			initType();
			goods_type_layout.setVisibility(View.GONE);
		}
		goods_type_layout.setVisibility(View.GONE);
		topCenter.setText(cateName);

		this.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.e("意图",intent.getAction());
				if ("com.lin.actions.cate".equals(intent.getAction())) {
					initGoodsTypeView();
				}
			}
		}, new IntentFilter("com.lin.actions.cate"));

//		Util.initTop(this, cateName, R.drawable.card_srarch, new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				initGoodsTypeView();
//			}
//		}, new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//das
//				Intent intent = new Intent(ProductListFrame.this, SPSearch.class);
//				startActivity(intent);
//			}
//		});

		if ("1079".equals(cid)) {
			ywjj_saixuan.setVisibility(View.VISIBLE);
		} else {
			ywjj_saixuan.setVisibility(View.GONE);
		}
//		if (null != product_list_xinpin)
//			product_list_xinpin.performClick();
	}

	@OnClick(R.id.topright_shaixuan)
	public void saiXuanClick(View view2) {
		View view = getLayoutInflater().inflate(R.layout.list_item_pop, null, false);

		final PopupWindow distancePopup = new PopupWindow(view, android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
		view.findViewById(R.id.rl_chye_gj).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setTag("1079");
				ProductListFrame.this.onClick(v);
				distancePopup.dismiss();
			}
		});
		view.findViewById(R.id.rl_lm_gj).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setTag("1085");
				ProductListFrame.this.onClick(v);
				distancePopup.dismiss();
			}
		});
		view.findViewById(R.id.rl_all_gj).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setTag("全部工具");

				ProductListFrame.this.onClick(v);
				topCenter.setText("全部工具");
				ywjj_saixuan.setVisibility(View.VISIBLE);
				distancePopup.dismiss();
			}
		});
		distancePopup.showAsDropDown(view2);
	}

	@OnClick({ R.id.topright_liebao, R.id.topright_pingpu })
	public void OnClick(View v) {
		page = 1;
		switch (v.getId()) {
		case R.id.topright_liebao:
			topright_pingpu.setVisibility(View.VISIBLE);
			topright_liebao.setVisibility(View.GONE);
			loadData(params, R.layout.product_item_fragment_list_item,"1");
			break;
		case R.id.topright_pingpu:
			topright_pingpu.setVisibility(View.GONE);
			topright_liebao.setVisibility(View.VISIBLE);
			loadData(params, R.layout.product_item_fragment_item,"1");

			break;

		}
	}

	public void hideCate() {
		goods_type_layout.setVisibility(View.GONE);
		isShowCate = false;
	}

	public boolean isShowCate() {
		return isShowCate;
	}

	@Override
	protected void onResume() {
		topback = this.findViewById(R.id.topback);

		if ("phone".equals(from)) {
			topback.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					finish();

				}
			});

		} else {
			topback.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(ProductListFrame.this, StoreMainFrame.class);
					startActivity(intent);
				}
			});

		}

		super.onResume();
	}
	
	 private void showPopupWindow(View view,final String titlename) {

	        // 一个自定义的布局，作为显示的内容
	        View contentView = LayoutInflater.from(context).inflate(
	                R.layout.pop_window1, null);
	        // 设置按钮的点击事件
	        ListView mylist = (ListView) contentView.findViewById(R.id.itemlist);
	        
	        
	
	        final String[] str0={"全部","服饰鞋帽","箱包饰品","个护化妆","居家百货","生活电器","电子数码","食品保健","远大专属"};
	        final String[] cid1={"","419","589","498","526","1093","1147","469","1079"};
	        
	        final String[] str1={"以销量从高到低排序","以销量从低到高排序"};
	        final String[] str2={"以价格从高到低排序","以价格从低到高排序"};
	        final String[] str3={"以评价从高到低排序","以评价从低到高排序"};
        	 str = null;
	        if (titlename.equals("类别")) {
	        	str=str0;
			}else if(titlename.equals("销量1")||titlename.equals("销量2")){
				 str=str1;
			}else if(titlename.equals("价格1")||titlename.equals("价格2")){
				 str=str2;
			}else if(titlename.equals("评价1")||titlename.equals("评价2")){
				 str=str3;
			}
	        
	        

	        
	        mylist.setAdapter(new BaseAdapter() {
				
				@Override
				public View getView(int arg0, View arg1, ViewGroup arg2) {
					// TODO Auto-generated method stub
					View view=LayoutInflater.from(context).inflate(R.layout.itemtextleft,arg2,false);
					TextView textView= (TextView) view.findViewById(R.id.tv);
					textView.setText(str[arg0]);
					
					return view;
				}
				
				@Override
				public long getItemId(int arg0) {
					// TODO Auto-generated method stub
					return arg0;
				}
				
				@Override
				public Object getItem(int arg0) {
					// TODO Auto-generated method stub
					return arg0;
				}
				
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return str.length;
				}
			});

	        final PopupWindow popupWindow = new PopupWindow(contentView,
	                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

	        popupWindow.setTouchable(true);

	        popupWindow.setTouchInterceptor(new OnTouchListener() {

	            @Override
	            public boolean onTouch(View v, MotionEvent event) {

	                Log.i("mengdd", "onTouch : ");

	                return false;
	                // 这里如果返回true的话，touch事件将被拦截
	                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
	            }

			
	        });
	        
	        mylist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					popupWindow.dismiss();
					
					
					String order= str[arg2];
					
					
					if (order.indexOf("从高到低")!=-1) {
						ascOrDesc = "desc";
					}else if (order.indexOf("从低到高")!=-1) {
						ascOrDesc = "asc";
					}
//					numce++;
//					String ascOrDesc="";
//					if (numce%2!=0) {
//						ascOrDesc = "desc";
//					}else{
//						ascOrDesc = "asc";
//					}
//					
				
					
					
					if (titlename.equals("类别")) {
						
						Log.e("是否选择类别","是");
						String cid11="1";
						try{
							cid11=cid1[arg2];
							
						}catch(Exception e){
					cid11="1";
							
						}
						if (!cid11.equals("1")||!cid11.equals("")) {
							Log.e("cid111",cid11+"");
							cid=cid11;
						}
					}
			
					Log.e("点击的cid",cid+"");
					
					params = "type=新品&ascOrDesc=" + ascOrDesc + "&cid=" + cid;
					if (str[arg2].equals("远大专属")) {
						ishowiamge=false;
					}else{
						ishowiamge=true;
					}
					
					loadData(params, R.layout.product_item_fragment_item,"2");
					
				}
			});

	        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
	        // 我觉得这里是API的一个bug
	        popupWindow.setBackgroundDrawable(new BitmapDrawable());

	        // 设置好参数之后再show
	        popupWindow.showAsDropDown(view);

	    }


}
