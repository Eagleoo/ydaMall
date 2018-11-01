package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.BaseMallAdapter;
import com.mall.model.ServiceProduct;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServiceProductDeatilFream extends Activity {

	private ServiceProduct spro;
	@ViewInject(R.id.service_product_images)
	private Gallery gallery;
	@ViewInject(R.id.service_priduct_scj)
	private TextView scj;
	@ViewInject(R.id.service_product_price)
	private TextView ydj;
	@ViewInject(R.id.service_product_title)
	private TextView title;
	@ViewInject(R.id.service_product_name)
	private TextView name;
	@ViewInject(R.id.service_product_shortTitle)
	private TextView shortTitle;

	@ViewInject(R.id.service_product_zssb)
	private TextView zssb;
	@ViewInject(R.id.service_product_zsjf)
	private TextView zsjf;
	@ViewInject(R.id.service_product_sbdh)
	private TextView sbdh;
	@ViewInject(R.id.service_product_jfhg)
	private TextView jfhg;

	@ViewInject(R.id.service_product_address)
	private TextView address;

	@ViewInject(R.id.service_product_info)
	private TextView info;
	@ViewInject(R.id.service_product_shopInfo)
	private TextView shopInfo;
	@ViewInject(R.id.service_product_service)
	private TextView service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.service_product_deatil_fream);
		ViewUtils.inject(this);
		init();
	}

	private void init() {
		//废弃
		Util.initTop(this, "商品详情", Integer.MIN_VALUE, null);
		scj.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		final String pid = this.getIntent().getStringExtra("pid");
		final String lid = this.getIntent().getStringExtra("lid");
		Util.asynTask(this, "正在获取商品信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				spro = (ServiceProduct) runData;
				if(null == spro){
					Util.show("网络错误...", ServiceProductDeatilFream.this);
					return ;
				}
				scj.setText(spro.getScj());
				ydj.setText(spro.getYdj());
				title.setText(spro.getName());
				name.setText(spro.getName());
				shortTitle.setText(spro.getShortTitle());
				address.setText(spro.getAddress());
				info.setText(Html.fromHtml(spro.getExplain()
						.replaceAll("_\\|_", "&").replaceAll("_lt_", "<")
						.replaceAll("_rt_", ">")));
				shopInfo.setText(Html.fromHtml(spro.getShop()
						.replaceAll("_\\|_", "&").replaceAll("_lt_", "<")
						.replaceAll("_rt_", ">")));
				service.setText(Html.fromHtml(spro.getService()
						.replaceAll("_\\|_", "&").replaceAll("_lt_", "<")
						.replaceAll("_rt_", ">")));

				String zhichi2 = spro.getZhichi2();
				Resources res = ServiceProductDeatilFream.this.getResources();
				Drawable zhichi = res.getDrawable(R.drawable.ic_arrow);
				Drawable buzhichi = res.getDrawable(R.drawable.buzhichi);
				zhichi.setBounds(0, 0, zhichi.getMinimumWidth(),
						zhichi.getMinimumHeight());
				buzhichi.setBounds(0, 0, buzhichi.getMinimumWidth(),
						buzhichi.getMinimumHeight());
				zssb.setCompoundDrawables(buzhichi, null, null, null);
				zsjf.setCompoundDrawables(buzhichi, null, null, null);
				sbdh.setCompoundDrawables(buzhichi, null, null, null);
				jfhg.setCompoundDrawables(buzhichi, null, null, null);
				if (!Util.isNull(zhichi2)) {
					String[] zc2 = zhichi2.split(",");
					for (String tag : zc2) {
						if (tag.equals(zssb.getTag() + "")) {
							zssb.setCompoundDrawables(zhichi, null, null, null);
						}
						if (tag.equals(zsjf.getTag() + "")) {
							zsjf.setCompoundDrawables(zhichi, null, null, null);
						}
						if (tag.equals(sbdh.getTag() + "")) {
							sbdh.setCompoundDrawables(zhichi, null, null, null);
						}
						if (tag.equals(jfhg.getTag() + "")) {
							jfhg.setCompoundDrawables(zhichi, null, null, null);
						}
					}
				}
				initPhoto();
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getServiceProductByLidAndPid, "lid="
						+ lid + "&pid=" + pid);
				return web.getObject(ServiceProduct.class);
			}
		});
	}

	/**
	 * 初始化商品图片
	 */
	private void initPhoto() {
		List<String> list = new ArrayList<String>();
		if (!Util.isNull(spro.getImg1()))
			list.add(spro.getImg1().replaceFirst("img.mall666.cn",
					Web.imgServer));
		if (!Util.isNull(spro.getImg2()))
			list.add(spro.getImg2().replaceFirst("img.mall666.cn",
					Web.imgServer));
		if (!Util.isNull(spro.getImg3()))
			list.add(spro.getImg3().replaceFirst("img.mall666.cn",
					Web.imgServer));
		if (!Util.isNull(spro.getImg4()))
			list.add(spro.getImg4().replaceFirst("img.mall666.cn",
					Web.imgServer));
		if (!Util.isNull(spro.getImg5()))
			list.add(spro.getImg5().replaceFirst("img.mall666.cn",
					Web.imgServer));
		gallery.setAdapter(new ServiceProductAdapter(this, list));
	}

	@OnClick(R.id.service_product_shop)
	public void shopClick(View v) {
		final String pid = this.getIntent().getStringExtra("pid");
		final String lid = this.getIntent().getStringExtra("lid");
		if (null == UserData.getUser()) {
			Util.showIntent("您还没登录，现在去登录吗？", this, LoginFrame.class,
					new String[] { "pid", "lid" }, new String[] { pid, lid });
			return;
		}
		Util.asynTask(this, "正在购买...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if ("success".equals(runData + "")) {
					Util.showIntent("加入购物车成功，是否需要去付款？",
							ServiceProductDeatilFream.this, "去付款", "再逛逛",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Util.showIntent(
											ServiceProductDeatilFream.this,
											ShopCarFrame.class);
								}
							}, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									dialog.dismiss();
								}
							});
				} else
					Util.show("加入购物车失败：" + runData,
							ServiceProductDeatilFream.this);
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.addShopCard, "id="
						+ pid + "&uName="
						+ UserData.getUser().getUserId() + "&pwd="
						+ UserData.getUser().getMd5Pwd() + "&colorAndSize=&pfrom=0&amount=1");
				return web.getPlan();
			}
		});
	}

	@OnClick(R.id.service_product_phone)
	public void phoneClicl(View v) {
		if (!Util.isNull(spro.getPhone()))
			Util.doPhone(spro.getPhone(), this);
		else
			Util.show("该商家没有服务电话。", this);
	}
}

class ServiceProductAdapter extends BaseMallAdapter<String> {

	private int _174dp = 174;
	private int _130dp = 130;
	private BitmapUtils bmUtil;

	public ServiceProductAdapter(Context context, List<String> list) {
		super(context, list);
		_174dp = Util.dpToPx(context, 174F);
		_130dp = Util.dpToPx(context, 130F);
		bmUtil = new BitmapUtils(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent,
			String t) {
		ImageView imageView = null;
		if (null == convertView) {
			imageView = new ImageView(context);
			convertView = imageView;
		} else
			imageView = (ImageView) convertView;

		// 设置imageView大小 ，也就是最终显示的图片大小
		Gallery.LayoutParams lp = new Gallery.LayoutParams(
				Gallery.LayoutParams.WRAP_CONTENT,
				Gallery.LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(lp);
		imageView.setImageResource(R.drawable.progress_round);
		final AnimationDrawable anim = (AnimationDrawable) imageView.getDrawable();
		final ImageView temp = imageView;
		imageView.post(new Runnable() {
			@Override
			public void run() {
				anim.start();
			}
		});
		bmUtil.display(imageView, t, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View container, String uri, Bitmap bitmap,
					BitmapDisplayConfig config, BitmapLoadFrom from) {
				super.onLoadCompleted(container, uri, Util.zoomBitmap(bitmap, _174dp, _130dp), config, from);
				anim.stop();
			}
			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				Bitmap bm = Util.drawable2Bitmap(context.getResources()
						.getDrawable(R.drawable.zw174));
				temp.setImageBitmap(Util.zoomBitmap(bm, _174dp, _130dp));
				anim.stop();
			}
		});
		return convertView;
	}
}

