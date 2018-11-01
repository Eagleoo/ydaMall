package com.mall.yyrg;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.mall.view.R;
import com.mall.yyrg.adapter.YYRGUtil;
import com.mall.yyrg.model.HotShopCar;

/**
 * 一元热购购物车<br>
 * 
 * @author Administrator
 * 
 */
public class YYRGMyRegouCart extends Activity {
	@ViewInject(R.id.listview)
	private ListView listview;
	@ViewInject(R.id.return_main)
	private LinearLayout return_main;
	public static List<HotShopCar> hotShopCars = new ArrayList<HotShopCar>();
	private int width;
	private BitmapUtils bmUtil;
	private Set<Integer> checked = new HashSet<Integer>();
	@ViewInject(R.id.to_regou)
	private Button to_regou;
	private Set<String> yscids = new HashSet<String>();
	private Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_my_grgou_cart);
		ViewUtils.inject(this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		bmUtil = new BitmapUtils(this);
		hotShopCars.clear();
		checked.clear();
		yscids.clear();
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		if (UserData.getUser() == null) {
			goLogin();
			listview.setVisibility(View.GONE);
			return_main.setVisibility(View.VISIBLE);
		} else {
			listview.setVisibility(View.VISIBLE);
			return_main.setVisibility(View.GONE);
			getHotShopCart();
		}
	}

	@OnClick(R.id.top_back)
	public void topback(View view) {
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		hotShopCars.clear();
		checked.clear();
		yscids.clear();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		if (UserData.getUser() == null) {
			if (dialog==null) {
				goLogin();
			}
			listview.setVisibility(View.GONE);
			return_main.setVisibility(View.VISIBLE);
		} else {
			listview.setVisibility(View.VISIBLE);
			return_main.setVisibility(View.GONE);
			getHotShopCart();
		}
	}

	@OnClick({ R.id.return_main, R.id.delete, R.id.to_regou, R.id.to_fukuan })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.return_main:
			finish();
			Util.showIntent(this, YYRGMainFrame.class);
			break;

		case R.id.delete:// 删除选中的购物车
			for (int i : checked) {
				deleteHotShopCart(hotShopCars.get(i).getYscid());
			}
			break;
		case R.id.to_regou:
			finish();
			Util.showIntent(this, YYRGFrame.class);
			break;
		case R.id.to_fukuan:
			if (checked.size() < 1) {
				Toast.makeText(this, "没有选中的商品", Toast.LENGTH_SHORT).show();
				return;
			}
			YYRGUtil.hotShopCars.clear();
			String yscid = "";
			for (int i : checked) {
				HotShopCar hotShopCar = hotShopCars.get(i);
				YYRGUtil.hotShopCars.add(hotShopCar);
				System.out.println("选中：----------" + i);
			}
			for (String id : yscids) {
				yscid = yscid + id + ",";
			}
			System.out.println("未选中的：-----" + yscid);
			Intent intent = new Intent(this, YYRGConfirmPayment.class);
			intent.putExtra("yscid", yscid);
			startActivity(intent);
			finish();
			break;
		}
	}

	/**
	 * 获得购物车的信息
	 */
	private void getHotShopCart() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					List<HotShopCar> list = new ArrayList<HotShopCar>();
					list = ((HashMap<Integer, List<HotShopCar>>) runData)
							.get(1);
					if (list.size() > 0) {
						hotShopCars = list;
						listview.setAdapter(new YYRGGoodsCartAdapter(
								YYRGMyRegouCart.this, list));
					} else {
						listview.setVisibility(View.GONE);
						return_main.setVisibility(View.VISIBLE);
					}
				} else {
					Toast.makeText(YYRGMyRegouCart.this,
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

	class YYRGGoodsCartAdapter extends BaseAdapter {
		private Context context;
		private List<HotShopCar> list = new ArrayList<HotShopCar>();
		private LayoutInflater flater;

		public YYRGGoodsCartAdapter(Context content, List<HotShopCar> list) {
			this.context = content;
			this.list = list;
			flater = LayoutInflater.from(context);
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
				convertView = flater.inflate(R.layout.yyrg_goods_shopcart_list,
						null);
			}
			final DecimalFormat df = new DecimalFormat("#.00");
			TextView goodsname = (TextView) convertView
					.findViewById(R.id.goodsname);
			ImageView goods_img = (ImageView) convertView
					.findViewById(R.id.goods_img);
			TextView goodaprice = (TextView) convertView
					.findViewById(R.id.goodsprice);
			String href = "http://" + Web.imgServer + "/"
					+ list.get(position).getPhotoThumb();
			setImage(goods_img, href, width * 3 / 8, width * 3 / 8 - 10);
			final TextView allprice = (TextView) convertView
					.findViewById(R.id.allprice);
			final Button shop_car_button1 = (Button) convertView
					.findViewById(R.id.shop_car_button1);
			final TextView shop_car_t7 = (TextView) convertView
					.findViewById(R.id.shop_car_t7);
			Button shop_car_button2 = (Button) convertView
					.findViewById(R.id.shop_car_button2);
			MyProgressView myProgress = (MyProgressView) convertView
					.findViewById(R.id.myProgress);
			TextView canyu = (TextView) convertView
					.findViewById(R.id.goods_canyu);
			TextView zongxu = (TextView) convertView.findViewById(R.id.zongxu);
			TextView shengyu = (TextView) convertView
					.findViewById(R.id.shengyu);
			final CheckBox check_goods = (CheckBox) convertView
					.findViewById(R.id.check_goods);
			goodaprice.setText("原价：￥"
					+ df.format(Double.parseDouble(list.get(position)
							.getPrice())));
			allprice.setText("需支付：￥"
					+ df.format(Double.parseDouble(list.get(position)
							.getUnitPrice())
							* Integer
									.parseInt(list.get(position).getSumPrice())));
			shop_car_t7.setText(list.get(position).getSumPrice());
			myProgress.setMaxCount(Integer.parseInt(list.get(position)
					.getTotalPersonTimes()) * 1.00f);
			
			zongxu.setText(list.get(position).getTotalPersonTimes());
			shengyu.setText(list.get(position).getLastPersonTimes());
			String string = Integer.parseInt(list.get(position)
					.getTotalPersonTimes())
					- Integer.parseInt(list.get(position).getLastPersonTimes())
					+ "";
			myProgress.setCurrentCount(Integer.parseInt(string) * 1.00f);
			canyu.setText(string);
			if (Integer.parseInt(shop_car_t7.getText().toString()
					.trim())>Integer.parseInt(shengyu.getText().toString())) {
				updateHotShopCart(shengyu.getText().toString(), list.get(position).getYscid());
			}
			if (Integer.parseInt(shop_car_t7.getText().toString())>1) {
				shop_car_button1.setBackgroundResource(R.drawable.yyrg_jian0);
			}
			if (check_goods.isChecked()) {
				checked.add(position);
			} else {
				yscids.add(list.get(position).getYscid());
			}
			check_goods.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (check_goods.isChecked()) {
						checked.add(position);
						yscids.remove(list.get(position).getYscid());
					} else {
						yscids.add(list.get(position).getYscid());
						checked.remove(position);
					}
				}
			});
			shop_car_button1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					int temp = Integer.parseInt(shop_car_t7.getText()
							.toString().trim());
					if (temp > 1) {
						shop_car_t7.setText(temp - 1 + "");
						allprice.setText("需支付：￥"
								+ df.format(Double.parseDouble(list.get(
										position).getUnitPrice())
										* (temp - 1)));
						updateHotShopCart(shop_car_t7.getText().toString()
								.trim(), list.get(position).getYscid());
					} else {
						shop_car_button1.setBackgroundResource(R.drawable.yyrg_jian);
					}
				}
			});
			shop_car_button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					int temp = Integer.parseInt(shop_car_t7.getText()
							.toString().trim());
					if (temp < Integer.parseInt(list.get(position)
							.getLastPersonTimes())) {
						shop_car_t7.setText(temp + 1 + "");
						allprice.setText("需支付：￥"
								+ df.format(Double.parseDouble(list.get(
										position).getUnitPrice())
										* (temp + 1)));
						updateHotShopCart(shop_car_t7.getText().toString()
								.trim(), list.get(position).getYscid());
					}
				}
			});
			return convertView;
		}
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
	 * 修改购物车的数量
	 */
	private void updateHotShopCart(final String num, final String yscids) {
		Util.asynTask(this, "正在更新购物车数量……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					if ("success".equals(runData.toString().trim())) {
						getHotShopCart();
					} else {
						Util.show("更新失败", YYRGMyRegouCart.this);
					}
				} else {
					Toast.makeText(YYRGMyRegouCart.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.updateHotShopCarNum,
						"userID=" + UserData.getUser().getUserId()
								+ "&userPaw=" + UserData.getUser().getMd5Pwd()
								+ "&num=" + num + "&yscids=" + yscids);
				return web.getPlan();
			}
		});
	}

	private void deleteHotShopCart(final String yscids) {
		Util.asynTask(this, "正在删除选中的购物车……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				if (runData != null) {
					if ("success".equals(runData.toString().trim())) {
						getHotShopCart();
					} else {
						Util.show("更新失败", YYRGMyRegouCart.this);
					}
				} else {
					Toast.makeText(YYRGMyRegouCart.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.yyrgAddress, Web.delHotShopCar, "userID="
						+ UserData.getUser().getUserId() + "&userPaw="
						+ UserData.getUser().getMd5Pwd() + "&yscids=" + yscids);
				return web.getPlan();
			}
		});
	}

	private void goLogin() {
		// TODO Auto-generated method stub
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.tuichu_lmsj_dialog, null);
		 dialog = new AlertDialog.Builder(this)
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
				dialog=null;
			}
		});
		now_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.showIntent(YYRGMyRegouCart.this, LoginFrame.class);
				dialog.dismiss();
				dialog=null;
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}
}
