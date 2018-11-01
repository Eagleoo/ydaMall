package com.mall.yyrg;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.ShopAddress;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.ShopAddressFrame;
import com.mall.yyrg.adapter.YYRGUtil;
import com.mall.yyrg.model.HotShopCar;

/**
 * 一元热购的付款确认
 * @author Administrator
 *
 */
public class YYRGConfirmPayment extends Activity{
	private String addId = "";
	@ViewInject(R.id.shop_address_line)
	private RadioGroup shop_address_line;
	private int width;
	private BitmapUtils bmUtil;
	private ListView all_yyrg_goods;
	private String yscids;
	@ViewInject(R.id.all_goods)
	private TextView all_goods;
	@ViewInject(R.id.all_price)
	private TextView all_price;
	private double zhifu=0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_confirm_payment);
		ViewUtils.inject(this);
		all_yyrg_goods=(ListView) findViewById(R.id.all_yyrg_goods);
		yscids=getIntent().getStringExtra("yscid");
		getShopadd();
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width=dm.widthPixels;
		all_yyrg_goods.setAdapter(new YYRGGoodsCartAdapter(this, YYRGUtil.hotShopCars));
		for (int i = 0; i < YYRGUtil.hotShopCars.size(); i++) {
			zhifu=zhifu+1*Double.parseDouble(YYRGUtil.hotShopCars.get(i).getPersonTimes());
		}
		DecimalFormat df = new DecimalFormat("#.00");
		all_price.setText("￥ "+df.format(zhifu));
		all_goods.setText(YYRGUtil.hotShopCars.size()+" 件");
	}
	@OnClick(R.id.top_back)
	public void returnclick(View view){
		finish();
	}
	/**
	 * 确认付款
	 */
	@OnClick(R.id.order_commit_submit)
	public void fukuan(View view){
		Intent intent=new Intent(this, YYRGPayMoneyFrame.class);
		intent.putExtra("yscids", yscids);
		startActivity(intent);
		finish();
	}
	/**
	 * 添加收货地址
	 * 
	 * @param v
	 */
	@OnClick(R.id.order_pay_add_address)
	public void addAddress(View v) {
		Util.showIntent(this, ShopAddressFrame.class);
	}
	/**
	 * 选择其他收货地址
	 * @param view
	 */
	@OnClick(R.id.order_pay_sel_ohtherAddress)
	public void selAddress(View view){
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		final View rootView = LayoutInflater.from(this).inflate(R.layout.sel_all_address, null);
		Util.asynTask(this, "正在获取更多收货地址...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String,List<ShopAddress>> map = (HashMap<String, List<ShopAddress>>)runData;
				List<ShopAddress> list = map.get("list");
				if(null == list){
					Util.show("网络错误，请重试！",YYRGConfirmPayment.this);
					return ;
				}
				if(0 == list.size()){
					Util.show("没有获取到收货地址！",YYRGConfirmPayment.this);
					return;
				}
				OnClickListener click = new OnClickListener() {
					@Override
					public void onClick(View v) {
						addId = v.getTag()+"";
						RadioGroup addList = (RadioGroup) YYRGConfirmPayment.this.findViewById(R.id.shop_address_line);
						int selRadioId = addList.getCheckedRadioButtonId();
						if(-1 != selRadioId){
							RadioButton radio = (RadioButton) addList.findViewById(selRadioId);
							if (radio.isChecked()) {
								radio.setChecked(false);
								radio.invalidate();
							}
						}
					//	setZoneid(v.getTag(-7)+"");
						dialog.cancel();
						dialog.dismiss();
					}
				};
				LinearLayout root = (LinearLayout)rootView.findViewById(R.id.sel_address_container);
				for(ShopAddress sa : list){
					View itemView = LayoutInflater.from(YYRGConfirmPayment.this).inflate(R.layout.sel_all_address_item, null);
					TextView item = (TextView)itemView.findViewById(R.id.sel_item_address);
					item.setText(sa.getName() + " - "+ sa.getMobilePhone() + " - " + sa.getRegion() + " - " + sa.getAddress());
					item.setTag(sa.getShoppingAddId());
					item.setTag(-7,sa.getZone());
					if(sa.getShoppingAddId().equals(addId)){
						Resources res = YYRGConfirmPayment.this.getResources();
						Drawable checked = res.getDrawable(R.drawable.pay_item_checked);
						
						checked.setBounds(0, 0, checked.getMinimumWidth(), checked.getMinimumHeight());
						item.setCompoundDrawables(checked, null, null, null);
					}
					item.setOnClickListener(click);
					root.addView(itemView);
				}
				dialog.setView(rootView, 0, 0, 0, 0);
				dialog.show();
			}
			
			@Override
			public Serializable run() {
				User user = UserData.getUser();
				Web web = new Web(Web.getShopAddress, 
						"userId="+user.getUserId()
						+"&md5Pwd="+user.getMd5Pwd()
						+"&size=1024");
				List<ShopAddress> list = web.getList(ShopAddress.class);
				HashMap<String,List<ShopAddress>> map = new HashMap<String, List<ShopAddress>>();
				map.put("list", list);
				return map;
			}
		});
	}
	private void getShopadd() {
		Util.asynTask(this, "正在获取收货地址...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<ShopAddress>> map = (HashMap<String, List<ShopAddress>>) runData;
				List<ShopAddress> list = map.get("result");
				if (null != list && 0 != list.size()) {
					RadioGroup addList = (RadioGroup) YYRGConfirmPayment.this
							.findViewById(R.id.shop_address_line);
					addList.removeAllViews();
					addList.removeAllViewsInLayout();
					int temp=0;
					
					for (final ShopAddress sa : list) {
						RadioButton radio = new RadioButton(YYRGConfirmPayment.this);
						radio.setTag(sa.getShoppingAddId());
						radio.setText(sa.getName() + " - "
								+ sa.getMobilePhone() + " - " + sa.getRegion()
								+ " - " + sa.getAddress());
						radio.setSingleLine(false);
						radio.setMaxLines(2);
						radio.setEllipsize(TruncateAt.END);
						radio.setTextColor(getResources().getColor(R.color.yyrg_qianhui));
						radio.setTextSize(13.0f);
						if (temp==0) {
							radio.setChecked(true);
							temp=1;
						}
						addList.addView(radio);
						radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								if (isChecked){
								//	setZoneid(sa.getZone());
								}
							}
						});
					}
				} else {
					AlertDialog.Builder builder = new Builder(
							YYRGConfirmPayment.this);
					builder.setMessage("对不起，没有获取到收货地址。");
					builder.setTitle("提示");
					builder.setPositiveButton("去添加",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									intent.setClass(YYRGConfirmPayment.this,
											ShopAddressFrame.class);
								//	intent.putExtra("guid", guidAll);
									startActivity(intent);
								}
							});
					builder.setNegativeButton("重新获取",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									getShopadd();
								}
							});
					builder.create().show();
				}
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getShopAddress, "userId="
						+ UserData.getUser().getUserId() + "&md5Pwd="
						+ UserData.getUser().getMd5Pwd() + "&size=3");
				List<ShopAddress> list = web.getList(ShopAddress.class);
				HashMap<String, List<ShopAddress>> map = new HashMap<String, List<ShopAddress>>();
				map.put("result", list);
				return map;
			}
		});
	}
	 class YYRGGoodsCartAdapter extends BaseAdapter {
			private Context context;
			private List<HotShopCar> list=new ArrayList<HotShopCar>();
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
			public View getView(final int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				if (convertView == null) {
					convertView = flater.inflate(R.layout.yyrg_confirm_payment_item,
							null);
				}
				final DecimalFormat df = new DecimalFormat("#.00");
				TextView goodsname = (TextView) convertView
						.findViewById(R.id.goodsname);
				ImageView goods_img=(ImageView) convertView.findViewById(R.id.goods_img);
				TextView goodaprice = (TextView) convertView
						.findViewById(R.id.goodsprice);
				String href = "http://" + Web.imgServer + "/"
						+ list.get(position).getPhotoThumb();
				setImage(goods_img, href, width*2/5, width*2/5-10);
				final TextView allprice = (TextView) convertView.findViewById(R.id.allprice);
				TextView shop_car_t7=(TextView) convertView.findViewById(R.id.shop_car_t7);
				shop_car_t7.setText(list.get(position).getSumPrice());
				goodaprice.setText("原价：￥"+df.format(Double.parseDouble(list.get(position)
						.getPrice())));
				allprice.setText("需支付：￥"+df.format(Double.parseDouble(list.get(position)
						.getUnitPrice())
						* Integer.parseInt(list.get(position).getSumPrice())));
				//goodsname.setText(list.get(position).getPeriodName());
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
		 
		 
		 
}
