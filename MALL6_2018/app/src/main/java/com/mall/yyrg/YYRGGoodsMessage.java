package com.mall.yyrg;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
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
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.ProductDetailHtml;
import com.mall.view.R;
import com.mall.yyrg.adapter.AllIssueAdapter;
import com.mall.yyrg.model.BaskSingle;
import com.mall.yyrg.model.HotShopCar;
import com.mall.yyrg.model.HotShopInfo;
import com.mall.yyrg.model.LastPeriod;
import com.mall.yyrg.model.ShopPeriods;

/**
 * 商品详情的界面( 废弃)
 * 
 * @author Administrator
 * 
 */
public class YYRGGoodsMessage extends Activity {
//	 商品期数
	@ViewInject(R.id.goods_qishu)
	private TextView gooos_qishu;
	// 商品图片
	@ViewInject(R.id.goods_img)
	private ImageView goods_img;
	// 商品名称
	@ViewInject(R.id.goods_name)
	private TextView goods_name;
	// 商品价格
	@ViewInject(R.id.goods_price)
	private TextView goods_price;

	@ViewInject(R.id.goods_canyu)
	private TextView goods_canyu;
	@ViewInject(R.id.goods_zongxu)
	private TextView goods_zongxu;
	@ViewInject(R.id.goods_shengyu)
	private TextView goods_shengyu;
	private HotShopInfo info;
	private ProgressBar goods_progress;
	
	
	private BitmapUtils bmUtil;
	private int width;
	private int height;
	@ViewInject(R.id.goods_shaidan)
	private TextView goods_shaidan;
	private Intent intent;
	@ViewInject(R.id.get_last_goods)
	private LinearLayout get_last_goods;
	@ViewInject(R.id.goods_hj_name)
	private TextView goods_hj_name;
	@ViewInject(R.id.goods_hj_address)
	private TextView goods_hj_address;
	@ViewInject(R.id.goods_hj_jiexiao)
	private TextView goods_hj_jiexiao;
	@ViewInject(R.id.goods_hj_regou)
	private TextView goods_hj_regou;
	@ViewInject(R.id.goods_xy_number)
	private TextView goods_xy_number;
	@ViewInject(R.id.goods_user_img)
	private ImageView goods_user_img;
	private PopupWindow distancePopup = null;
	private String tuwens = "";
	@ViewInject(R.id.top)
	private LinearLayout top;
	private TextView tuwen;
	private ProgressDialog pd;
	// 热购的人次
	@ViewInject(R.id.shop_car_t7)
	private TextView shop_car_t7;
	// 当前热购的人次
	private int temp = 1;
	// 最多可热购的次数
	private int allRegou;
	private Intent data = null;
	private static String goodsid ;
	private static String ypid;
	//购物车中的数量
	@ViewInject(R.id.goodscount)
	private TextView goodscount;
	private List<ShopPeriods> shopPeriods=new ArrayList<ShopPeriods>();
	@ViewInject(R.id.re1)
	private RelativeLayout re1;
	private List<String> lastTuPianList=new ArrayList<String>();
	@ViewInject(R.id.cdv_limit)
	private CountdownView cdv_limit;
	private int countState;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_goods_message);
		ViewUtils.inject(this);
		shop_car_t7.setText(temp + "");
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		bmUtil = new BitmapUtils(this);
		data = this.getIntent();
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		goods_progress = (ProgressBar) findViewById(R.id.goods_progress);
		
		ypid=getIntent().getStringExtra("ypid");
		goodsid= getIntent().getStringExtra("goodsid");
		setImageWidthAndHeight(goods_img, 1, width*2/5, width*2/5);
		if (TextUtils.isEmpty(goodsid)) {
			Toast.makeText(this, "暂无商品详细信息", Toast.LENGTH_SHORT).show();
		} else {
			getHotShopInfo(goodsid);
			if (null == UserData.getUser()) {
			}else {
				getHotShopCart();
				}
		}
		String time =  getIntent().getStringExtra("time");
		if (!Util.isNull(time)) {
			SimpleDateFormat formatDate = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			try {
				Date date = formatDate.parse(time);
				cdv_limit.setVisibility(View.VISIBLE);
				cdv_limit.startCountDown(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	@OnClick({ R.id.goods_qishu, R.id.goods_tuwen, R.id.goods_all_rg,
			R.id.top_back, R.id.goods_img, R.id.add_shopcart,
			R.id.shop_car_button1, R.id.shop_car_button2 ,R.id.goods_shaidan,R.id.gouwu,R.id.gouwuche})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.gouwuche:
			if(null == UserData.getUser())
				Util.showIntent("对不起，请先登录！",this, LoginFrame.class);
			else
				Util.showIntent(YYRGGoodsMessage.this, YYRGMyRegouCart.class);
			break;
		case R.id.goods_qishu://显示当前商品的所有期次
			if (shopPeriods.size()>0) {
				getPopupWindow();
				startPopupWindow(2);
				distancePopup.showAsDropDown(re1);
			}
			break;
		case R.id.gouwu://立即一元热购
			if (null == UserData.getUser()) {
				String ccateid = getIntent().getStringExtra("catId");
				if (Util.isNull(getIntent().getStringExtra("catId"))) {
					ccateid = goodsid;
				}
				Util.showIntent("您还没登录，现在去登录吗？", YYRGGoodsMessage.this,
						LoginFrame.class, new String[] { "url", "catId" },
						new String[] { data.getStringExtra("url"), ccateid });
				return;
			}
			if (allRegou==0) {
				Toast.makeText(this, "已满员", Toast.LENGTH_SHORT).show();
				return ;
			}
			addHotShopCar(goodsid,1);
			break;
		case R.id.shop_car_button2:// 加
			if (temp < allRegou) {
				temp++;
				shop_car_t7.setText(temp + "");
			} else {
				Util.show("当前最多可以热购" + allRegou + "次     ", this);
			}
			break;
		case R.id.shop_car_button1:// 减
			if (temp > 1) {
				temp--;
				shop_car_t7.setText(temp + "");
			} else {
				Util.show("热购的次数不能低于1次！", this);
			}
			break;
		case R.id.add_shopcart:
			if (null == UserData.getUser()) {
				String ccateid = getIntent().getStringExtra("catId");
				if (Util.isNull(getIntent().getStringExtra("catId"))) {
					ccateid = goodsid;
				}
				Util.showIntent("您还没登录，现在去登录吗？", YYRGGoodsMessage.this,
						LoginFrame.class, new String[] { "url", "catId" },
						new String[] { data.getStringExtra("url"), ccateid });
				return;
			}
			if (allRegou==0) {
				Toast.makeText(this, "已满员", Toast.LENGTH_SHORT).show();
				return ;
			}
			addHotShopCar(goodsid,0);

			break;
		case R.id.top_back:
			finish();
			break;

		case R.id.goods_tuwen:// 图文详情
			if (TextUtils.isEmpty(tuwens)) {
				Util.show("暂无图文详情", this);
			} else {
				Intent intent=new Intent(YYRGGoodsMessage.this,ProductDetailHtml.class);
				intent.putExtra("content", tuwens);
			startActivity(intent);
			}

			break;
		case R.id.goods_all_rg:// 所有热购记录
			intent=new Intent(this, YYRGAllRegouRecord.class);
			intent.putExtra("yppid", goodsid);
			startActivity(intent);
			break;
		case R.id.goods_shaidan:// 商品分享记录
			intent=new Intent(this, YYRGBaskSingle.class);
			intent.putExtra("yppid", goodsid);
			startActivity(intent);
			break;
		case R.id.goods_img:
			if (TextUtils.isEmpty(info.getProPics())) {

			} else {
				intent = new Intent(this, YYRGGoodsMessageImg.class);
				intent.putExtra("goodsimg", info.getProPics());
				startActivity(intent);
			}
			break;
		}
	}
	/**
	 * 获得商品详细信息
	 * @param yypid
	 */
	private void getHotShopInfo(final String yypid) {
		Util.asynTask( new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<HotShopInfo> list = new ArrayList<HotShopInfo>();
					list = ((HashMap<Integer, List<HotShopInfo>>) runData)
							.get(1);
					if (list.size() > 0) {
						info = list.get(0);
						goods_name.setText(info.getProductName());
						System.out.println();
						DecimalFormat df = new DecimalFormat("#.00");
						goods_price.setText("价格：￥"
								+ df.format(Double.parseDouble(info.getPrice())));
//						goods_progress.setMaxCount(Integer.parseInt(info
//								.getTotalPersonTimes()) * 1.00f);
//						goods_progress.setCurrentCount();
						
						float m=Float.parseFloat(info
								.getPersonTimes())/Float.parseFloat(info
										.getTotalPersonTimes())*100;
								
						
						System.out.println("*************"+m);
								
						goods_progress.setProgress((int)m);
						
						goods_progress.setMax(100);
						goods_canyu.setText(info.getPersonTimes());
						goods_zongxu.setText(info.getTotalPersonTimes());
						allRegou = Integer.parseInt(info.getLastPersonTimes());
						goods_shengyu.setText(info.getLastPersonTimes());
						gooos_qishu.setText("第" + info.getCurrentPeriod() + "期");
						String href = "http://" + Web.imgServer + "/"
								+ info.getProductPhoto();
						ImageView imageView=new ImageView(YYRGGoodsMessage.this);
						bmUtil.display(imageView, href, new DefaultBitmapLoadCallBack<View>() {
							@Override
							public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
									BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
								System.out.println("当前图片的宽度------"+arg2.getWidth());
								System.out.println("当前图片的高度------"+arg2.getHeight());
								int thiswidth=arg2.getWidth();
								arg2 = Util.zoomBitmap(arg2, thiswidth, arg2.getHeight());
								goods_img.setImageBitmap(arg2);
								super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
							}

							@Override
							public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
								goods_img.setImageResource(R.drawable.new_yda__top_zanwu);
							}
						});
						
						tuwens = info.getContent();
						getLastPeriod(goodsid);
						
					} else {
						Toast.makeText(YYRGGoodsMessage.this, "暂无商品信息",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(YYRGGoodsMessage.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.getHotShopInfo, "yppid="
						+ yypid);
				List<HotShopInfo> list = web.getList(HotShopInfo.class);
				HashMap<Integer, List<HotShopInfo>> map = new HashMap<Integer, List<HotShopInfo>>();
				map.put(1, list);
				return map;
			}

		});
	}

	private void setImage(final ImageView logo, String href, final int width,
			final int height) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				System.out.println("当前图片的宽度------"+arg2.getWidth());
				System.out.println("当前图片的高度------"+arg2.getHeight());
				arg2 = Util.zoomBitmap(arg2, width, height);
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}
	private void setImage(final ImageView logo, String href) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				System.out.println("当前图片的宽度------"+arg2.getWidth());
				System.out.println("当前图片的高度------"+arg2.getHeight());
				int thiswidth=arg2.getWidth();
				arg2 = Util.zoomBitmap(arg2, thiswidth, arg2.getHeight());
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}

	/**
	 * 获得上期获奖者的信息
	 * 
	 */
	private void getLastPeriod(final String yypid) {
		Util.asynTask( new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				getShopPeriods(re1);
				
				if (runData != null) {
					List<LastPeriod> list = new ArrayList<LastPeriod>();
					list = ((HashMap<Integer, List<LastPeriod>>) runData)
							.get(1);
					if (list.size() > 0) {
						goods_hj_name.setText(list.get(0).getUserId().replace("_p", ""));
						goods_hj_address.setText("("
								+ list.get(0).getBuyIPAddr() + ")");
						goods_hj_jiexiao.setText("揭晓时间："
								+ list.get(0).getAnnTime());
						goods_hj_regou.setText("热购时间："
								+ list.get(0).getBuyTime());
						goods_xy_number.setText("幸运购码："
								+ list.get(0).getAnnNum());
						setImage(goods_user_img, "http://"+Web.webImage+list.get(0).getLogo(), width/4, width/4-10);
					} else {
						get_last_goods.setVisibility(View.GONE);
					}

				} else {
					Toast.makeText(YYRGGoodsMessage.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.getLastPeriod, "yppid="
						+ yypid);
				List<LastPeriod> list = web.getList(LastPeriod.class);
				HashMap<Integer, List<LastPeriod>> map = new HashMap<Integer, List<LastPeriod>>();
				map.put(1, list);
				return map;
			}

		});
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(int state) {
		if (state==1) {
			View pview = getLayoutInflater().inflate(R.layout.yyrg_goods_tuwen,
					null, false);
			TextView topbacks = (TextView) pview.findViewById(R.id.top_back);
			WebView tuwen=(WebView) findViewById(R.id.tuwen);
			tuwen.loadDataWithBaseURL(null, tuwens + "", "text/html","utf-8", "");
			topbacks.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					distancePopup.dismiss();
				}
			});
			initpoputwindow(pview);
			pd.dismiss();
		}else if(state==2){
			View pview = getLayoutInflater().inflate(R.layout.yyrg_qici_list,
					null, false);
			ListView show_all_qici=(ListView) pview.findViewById(R.id.show_all_qici);
			show_all_qici.setAdapter(new AllIssueAdapter(shopPeriods,YYRGGoodsMessage.this));
			show_all_qici.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (shopPeriods.get(arg2).getStatus().equals("1")) {
						ypid=shopPeriods.get(arg2).getYpid();
						goodsid=shopPeriods.get(arg2).getYppid();
						if (TextUtils.isEmpty(goodsid)) {
							Toast.makeText(YYRGGoodsMessage.this, "暂无商品详细信息", Toast.LENGTH_SHORT).show();
						} else {
							getHotShopInfo(goodsid);
							getLastPeriod(goodsid);
							if (null == UserData.getUser()) {
							}else {
								getHotShopCart();
								}
						}
					}else {
						intent=new Intent(YYRGGoodsMessage.this, YYRGHistoryGoodsMessage.class);
						intent.putExtra("goodsid", shopPeriods.get(arg2).getYppid());
						startActivity(intent);
					}
					
				}
			});
			initpoputwindow(pview);
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

	/**
	 * 显示父类的POP 推出
	 */
	PopupWindow mPopupWindow;

	public void shouPop() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.yyrg_addshopcart, null);
		layout.getBackground().setAlpha(220);
		LinearLayout colse=(LinearLayout)layout.findViewById(R.id.colse);
		colse.setOnKeyListener(new OnKeyListener()
		{
		    public boolean onKey(View v, int keyCode, KeyEvent event)
		    {
		        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
		        	mPopupWindow.dismiss();
		        return false;
		    }
		});
		TextView zhifu = (TextView) layout.findViewById(R.id.zhifu);
		TextView fanhui = (TextView) layout.findViewById(R.id.fanhui);
		TextView t1 = (TextView) layout.findViewById(R.id.t1);
		TextView t2 = (TextView) layout.findViewById(R.id.t2);
		zhifu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
				Util.showIntent(YYRGGoodsMessage.this, YYRGMyRegouCart.class);
				finish();
			}
		});
		fanhui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();

			}
		});
		t1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();

			}
		});
		t2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPopupWindow.dismiss();
			}
		});
		mPopupWindow = showPop(LayoutParams.FILL_PARENT, 0, false,
				Gravity.BOTTOM, layout, layout);
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
			// mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
			mPopupWindow.showAtLocation(view[1], gravity, 10, hight_bottom);
			mPopupWindow.update();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mPopupWindow;
	}

	/**
	 * 添加购物车
	 */
	private void addHotShopCar(final String yypid,final int id) {
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					if ("success".equals(runData + "")) {
						if (id==0) {
							shouPop();
						}else {
							Util.showIntent(YYRGGoodsMessage.this, YYRGMyRegouCart.class);
						}
						
						goodscount.setText("1");
					} else {
						Util.show(runData + "", YYRGGoodsMessage.this);
					}
				} else {
					Toast.makeText(YYRGGoodsMessage.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.addHotShopCar, "yppid="
						+ yypid + "&userID=" + UserData.getUser().getUserId()
						+ "&userPaw=" + UserData.getUser().getMd5Pwd()+"&ypid="+ypid+"&personTims="+temp);
				return web.getPlan();
			}

		});
	}
	
	/**
	 * 获得购物车的信息
	 */
	private void getHotShopCart() {
		Util.asynTask( new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<HotShopCar> list = new ArrayList<HotShopCar>();
					list = ((HashMap<Integer, List<HotShopCar>>) runData)
							.get(1);
					if (list.size() > 0) {
						goodscount.setText(list.size()+"");
					} else {
					}
				} else {
					Toast.makeText(YYRGGoodsMessage.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}
			

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.getHotShopCarOnLogin,
						"userID=" + UserData.getUser().getUserId()
								+ "&userPaw=" + UserData.getUser().getMd5Pwd());
				List<HotShopCar> list = web.getList(HotShopCar.class);
				HashMap<Integer, List<HotShopCar>> map = new HashMap<Integer, List<HotShopCar>>();
				map.put(1, list);
				return map;
			}
		});
	}
	/**
	 * 获取所有晒单记录
	 */
	private void getAllBaskSingle() {
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<BaskSingle> list = new ArrayList<BaskSingle>();
					list = ((HashMap<Integer, List<BaskSingle>>) runData)
							.get(1);
					if(null != list)
						goods_shaidan.setText("晒单(" + list.size() + ")");
					else
						goods_shaidan.setText("晒单(0)");
				} else {
					Toast.makeText(YYRGGoodsMessage.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.getBaskSingle,
						"pageNum=" + 1 + "&size=" + 99999990+"&yppid="+goodsid);
				List<BaskSingle> list = web.getList(BaskSingle.class);
				HashMap<Integer, List<BaskSingle>> map = new HashMap<Integer, List<BaskSingle>>();
				map.put(1, list);
				return map;
			}

		});
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
	/**
	 * 获得购买期次
	 */
	private void getShopPeriods(final View view){
		Util.asynTask( new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				getAllBaskSingle() ;
				if (runData != null) {
					List<ShopPeriods> list = new ArrayList<ShopPeriods>();
					list = ((HashMap<Integer, List<ShopPeriods>>) runData)
							.get(1);
					if (list.size() > 0) {
						Collections.sort(list, new Comparator<ShopPeriods>() {
							@Override
							public int compare(ShopPeriods arg0,
									ShopPeriods arg1) {
								if (arg0.getPeriodName().length()==1) {
									arg0.setPeriodName("0"+arg0.getPeriodName());
								}
								if (arg1.getPeriodName().length()==1) {
									arg1.setPeriodName("0"+arg1.getPeriodName());
								}
								// TODO Auto-generated method stub
								return arg1.getPeriodName().compareTo(arg0.getPeriodName());
							}
						});
						ypid=list.get(0).getYpid();
						shopPeriods=list;
					} else {
					}
				} else {
					Toast.makeText(YYRGGoodsMessage.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}
			

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.getShopPeriods,
						"yppid=" + goodsid);
				List<ShopPeriods> list = web.getList(ShopPeriods.class);
				HashMap<Integer, List<ShopPeriods>> map = new HashMap<Integer, List<ShopPeriods>>();
				map.put(1, list);
				return map;
			}
		});
	}
	class ImageAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private List<String> list;
		ImageAdapter(List<String> list){
			this.list=list;
			inflater=LayoutInflater.from(YYRGGoodsMessage.this);
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
		public View getView(int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (view==null) {
				view=inflater.inflate(R.layout.yyrg_goods_img_item, null);
			}
			ImageView goods_img=(ImageView) view.findViewById(R.id.goods_img);
			TextView goods_imgs=(TextView ) view.findViewById(R.id.goods_imgs);
			
			setImage(goods_img, list.get(position));
			return view;
		}
	}
	private void goLogin() {
		// TODO Auto-generated method stub
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.tuichu_lmsj_dialog, null);
		final Dialog dialog = new AlertDialog.Builder(this)
				.create();
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
		update_count.setText("您还没有登录哦，是否前去登录？");
		yihou_update.setText("取\t\t消");
		now_update.setText("确\t\t定");
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
				Util.showIntent(YYRGGoodsMessage.this, LoginFrame.class);
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		cdv_limit.stopCountDown();
	}
	
}
