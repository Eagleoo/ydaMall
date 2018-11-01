package com.mall.yyrg;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.AllIssueAdapter;
import com.mall.yyrg.model.BaskSingle;
import com.mall.yyrg.model.HistoryHotShopInfo;
import com.mall.yyrg.model.ShopPeriods;
import com.mall.yyrg.model.WinPrizeInfo;

/**
 * 商品详情的界面
 * 
 * @author Administrator
 * 
 */
public class YYRGHistoryGoodsMessage extends Activity {
	// 商品期数
	@ViewInject(R.id.gooos_qishu)
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
	private View user_activity;
	private String goodsid;
	private HistoryHotShopInfo historyHotShopInfo;
	private String yungouma;
	private List<ShopPeriods> shopPeriods = new ArrayList<ShopPeriods>();
	@ViewInject(R.id.re1)
	private RelativeLayout re1;
	@ViewInject(R.id.lin)
	private RelativeLayout lin;
	@ViewInject(R.id.to_shaidan)
	private Button to_shaidan;
	private String ypid;
	private String huojiang ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user_activity = LayoutInflater.from(this).inflate(
				R.layout.yyrg_history_goods_message, null);
		setContentView(user_activity);
		ViewUtils.inject(this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		goodsid = getIntent().getStringExtra("goodsid");
		if (TextUtils.isEmpty(goodsid)) {
			Toast.makeText(this, "暂无商品信息", Toast.LENGTH_SHORT).show();
		} else {
			getAllBaskSingle();
			getShopPeriods(re1);
			gethistoryHotShopInfo(goodsid);
			getWinPrizeInfo(goodsid);
		}
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);

	}

	@OnClick({ R.id.gooos_qishu, R.id.goods_all_rg, R.id.top_back,
			R.id.goods_shaidan, R.id.jisuan_jieguo,R.id.to_shaidan })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.to_shaidan:
			if (historyHotShopInfo!=null&&ypid!=null) {
				intent=new Intent(this, YYRGAddBsakSingle.class);
				intent.putExtra("yppid", goodsid);
				intent.putExtra("ypid", ypid);	
				intent.putExtra("hidperiodName", historyHotShopInfo.getPeriodName());
				
				startActivity(intent);
			}else {
			
			}
			break;
		case R.id.gooos_qishu:
			if (shopPeriods.size() > 0) {
				getPopupWindow();
				startPopupWindow();
				distancePopup.showAsDropDown(re1);
			} else {
				getShopPeriods(re1);
			}
			break;
		case R.id.jisuan_jieguo:
			intent = new Intent(this, YYRGComputationResult.class);
			intent.putExtra("yppid", goodsid);
			intent.putExtra("time", historyHotShopInfo.getBuyTime());
			intent.putExtra("renshu", historyHotShopInfo.getPrice());
			intent.putExtra("yungou", yungouma);
			intent.putExtra("huojiang", huojiang);
			startActivity(intent);
			break;
		case R.id.top_back:
			finish();
			break;
		case R.id.goods_all_rg:// 所有热购记录
			intent = new Intent(this, YYRGAllRegouRecord.class);
			intent.putExtra("yppid", goodsid);
			startActivity(intent);
			break;
		case R.id.goods_shaidan:// 商品分享记录
			intent = new Intent(this, YYRGBaskSingle.class);
			intent.putExtra("yppid", goodsid);
			startActivity(intent);
			break;
		}
	}

	/**
	 * 获得商品详细信息
	 * 
	 * @param yypid
	 */
	private void gethistoryHotShopInfo(final String yypid) {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<HistoryHotShopInfo> list = new ArrayList<HistoryHotShopInfo>();
					list = ((HashMap<Integer, List<HistoryHotShopInfo>>) runData)
							.get(1);
					if (list.size() > 0) {
						historyHotShopInfo = list.get(0);
						if (null == UserData.getUser()) {
							lin.setVisibility(View.GONE);
						} else {
							System.out.println("当前的userid--"+UserData.getUser().getNoUtf8UserId());
							System.out.println("查询出的获奖者--"+list.get(0).getAwardUserid());
							huojiang=list.get(0).getAwardUserid();
							if (UserData.getUser().getNoUtf8UserId()
									.equals(list.get(0).getAwardUserid())) {
								
								lin.setVisibility(View.VISIBLE);
							} else {
								lin.setVisibility(View.GONE);
							}
						}
						DecimalFormat df = new DecimalFormat("#.00");
						goods_name.setText(list.get(0).getProductName());
						goods_price.setText("价值：￥"
								+ df.format(Double.parseDouble(list.get(0)
										.getPrice())));
						gooos_qishu.setText("第" + list.get(0).getPeriodName()
								+ "期");
						String href = "http://" + Web.imgServer + "/"
								+ list.get(0).getPhotoThumb();
						setImage(goods_img, href, width * 2 / 5, width * 2 / 5);
						goods_shaidan.setText("晒单("
								+ list.get(0).getTotalComments() + ")");
						goods_hj_jiexiao.setText("揭晓时间："
								+ list.get(0).getAnnTime());
						goods_hj_regou.setText("热购时间："
								+ list.get(0).getBuyTime());
						goods_hj_name.setText(list.get(0).getAwardUserid());
						goods_hj_address.setText("("
								+ list.get(0).getBuyIPAddr() + ")");
						String face=list.get(0).getUserFace();
						if (TextUtils.isEmpty(face)) {
							
						}
						if (!TextUtils.isEmpty(face)&&!face.contains("http")) {
							face="http://"+Web.webImage+"/"+face;
						}
						
						System.out.println("face*********"+face);
						
						setImage(goods_user_img, face, width/4, width/4);
					} else {
						Toast.makeText(YYRGHistoryGoodsMessage.this, "暂无商品信息",
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(YYRGHistoryGoodsMessage.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.gethistoryHotShopInfo,
						"yppid=" + yypid);
				List<HistoryHotShopInfo> list = web
						.getList(HistoryHotShopInfo.class);
				HashMap<Integer, List<HistoryHotShopInfo>> map = new HashMap<Integer, List<HistoryHotShopInfo>>();
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
				arg2 = Util.zoomBitmap(arg2, width, height);
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}
	/**
	 * 获取所有晒单记录
	 */
	private void getAllBaskSingle() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<BaskSingle> list = new ArrayList<BaskSingle>();
					list = ((HashMap<Integer, List<BaskSingle>>) runData)
							.get(1);
					if (list!=null&&list.size() > 0) {
						goods_shaidan.setText("晒单(" + list.size()
								+ ")");
					} else {
//						Toast.makeText(YYRGHistoryGoodsMessage.this, "没有更多的分享信息",
//								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(YYRGHistoryGoodsMessage.this, "操作失败，请检查网络是否连接后，再试一下",
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
	/**
	 * 获得上期获奖者的信息
	 * 
	 */
	private void getWinPrizeInfo(final String yypid) {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<WinPrizeInfo> list = new ArrayList<WinPrizeInfo>();
					list = ((HashMap<Integer, List<WinPrizeInfo>>) runData)
							.get(1);
					if (list.size() > 0) {
						goods_xy_number.setText("幸运购码："
								+ list.get(0).getAnnNum());
						yungouma = list.get(0).getAnnNum();
						
					}
				} else {
					Toast.makeText(YYRGHistoryGoodsMessage.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.getWinPrizeInfo,
						"yppid=" + yypid);
				List<WinPrizeInfo> list = web.getList(WinPrizeInfo.class);
				HashMap<Integer, List<WinPrizeInfo>> map = new HashMap<Integer, List<WinPrizeInfo>>();
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
	private void startPopupWindow() {
		View pview = getLayoutInflater().inflate(R.layout.yyrg_qici_list, null,
				false);
		ListView show_all_qici = (ListView) pview
				.findViewById(R.id.show_all_qici);
		show_all_qici.setAdapter(new AllIssueAdapter(shopPeriods,
				YYRGHistoryGoodsMessage.this));
		show_all_qici.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (shopPeriods.get(arg2).getStatus().equals("1")) {
					intent = new Intent(YYRGHistoryGoodsMessage.this,
							YYRGGoodsMessage.class);
					intent.putExtra("ypid", shopPeriods.get(arg2).getYpid());
					intent.putExtra("goodsid", shopPeriods.get(arg2).getYppid());
					startActivity(intent);
					finish();
					if (TextUtils.isEmpty(goodsid)) {
						Toast.makeText(YYRGHistoryGoodsMessage.this,
								"暂无商品详细信息", Toast.LENGTH_SHORT).show();
					} else {
					}
				} else {
					goodsid = shopPeriods.get(arg2).getYppid();
					if (TextUtils.isEmpty(goodsid)) {
						Toast.makeText(YYRGHistoryGoodsMessage.this, "暂无商品信息",
								Toast.LENGTH_SHORT).show();
					} else {
						distancePopup.dismiss();
						gethistoryHotShopInfo(goodsid);
						getWinPrizeInfo(goodsid);
					}
				}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			final Animation mScaleAnimation = AnimationUtils.loadAnimation(
					YYRGHistoryGoodsMessage.this, R.anim.recoil2);
			mScaleAnimation.setDuration(800);
			mScaleAnimation.setFillAfter(true);
			user_activity.startAnimation(mScaleAnimation);
			user_activity.clearAnimation();
		}
		return super.onKeyDown(keyCode, event);

	}

	/**
	 * 获得购买期次
	 */
	private void getShopPeriods(final View view) {
		Util.asynTask(new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<ShopPeriods> list = new ArrayList<ShopPeriods>();
					list = ((HashMap<Integer, List<ShopPeriods>>) runData)
							.get(1);
					if (list.size() > 0) {
						Collections.sort(list, new Comparator<ShopPeriods>() {

							@Override
							public int compare(ShopPeriods arg0,
									ShopPeriods arg1) {
								if (arg0.getPeriodName().length() == 1) {
									arg0.setPeriodName("0"
											+ arg0.getPeriodName());
								}
								if (arg1.getPeriodName().length() == 1) {
									arg1.setPeriodName("0"
											+ arg1.getPeriodName());
								}
								return arg1.getPeriodName().compareTo(
										arg0.getPeriodName());
							}
						});
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i).getYppid().equals(goodsid)) {
								ypid=list.get(i).getYpid();
							}
						}
						shopPeriods = list;
					} else {
					}
				} else {
					Toast.makeText(YYRGHistoryGoodsMessage.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.getShopPeriods, "yppid="
						+ goodsid);
				List<ShopPeriods> list = web.getList(ShopPeriods.class);
				HashMap<Integer, List<ShopPeriods>> map = new HashMap<Integer, List<ShopPeriods>>();
				map.put(1, list);
				return map;
			}
		});
	}
}
