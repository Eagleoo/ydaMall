package com.mall.yyrg.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.MyProgressView;
import com.mall.yyrg.model.MyHotShopReward;

public class MyAllRegouAdapter extends BaseAdapter {
	private List<MyHotShopReward> list = new ArrayList<MyHotShopReward>();
	private Context context;
	private LayoutInflater flater;
	private int width;
	private BitmapUtils bmUtil;

	public MyAllRegouAdapter(Context context, int width) {
		super();
		this.width = width;
		this.context = context;
		flater = LayoutInflater.from(context);
		bmUtil = new BitmapUtils(context);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
	}
	public MyAllRegouAdapter(Context context, int width,List<MyHotShopReward> list) {
		super();
		this.list=list;
		this.width = width;
		this.context = context;
		flater = LayoutInflater.from(context);
		bmUtil = new BitmapUtils(context);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
	}

	public void setList(List<MyHotShopReward> list) {
		this.list.addAll(list);
		this.notifyDataSetChanged();
	}

	public void clear() {
		this.list.clear();
	}

	public void updateUI() {
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = flater.inflate(R.layout.yyrg_my_regou_item, null);
		}
		// 已揭晓的控件
		ImageView goods_img = (ImageView) convertView
				.findViewById(R.id.goods_img);
		TextView goods_name = (TextView) convertView
				.findViewById(R.id.goods_name);
		TextView huode = (TextView) convertView.findViewById(R.id.huode);
		TextView jiexiao_time = (TextView) convertView
				.findViewById(R.id.jiexiao_time);
		TextView text1=(TextView) convertView.findViewById(R.id.text1);
		TextView text2=(TextView) convertView.findViewById(R.id.text2);
		// 进行中的控件
		TextView goodsname = (TextView) convertView
				.findViewById(R.id.goodsname);
		TextView goodsprice = (TextView) convertView
				.findViewById(R.id.goodsprice);
		MyProgressView progress = (MyProgressView) convertView
				.findViewById(R.id.progress);
		TextView canyu = (TextView) convertView.findViewById(R.id.canyu);
		TextView zongxu = (TextView) convertView.findViewById(R.id.zongxu);
		TextView shengyu = (TextView) convertView.findViewById(R.id.shengyu);
		LinearLayout jinxingzhong = (LinearLayout) convertView
				.findViewById(R.id.jinxingzhong);
		LinearLayout yijiexiao = (LinearLayout) convertView
				.findViewById(R.id.yijiexiao);
		setImage(goods_img, list.get(position).getProductPhoto(), width / 3,
				width / 3);
		DecimalFormat df = new DecimalFormat("#.00");
		if (list.get(position).getStatus().equals("1")) {// 进行中
			yijiexiao.setVisibility(View.GONE);
			jinxingzhong.setVisibility(View.VISIBLE);
			goodsname.setText(list.get(position).getProductName());
			goodsprice.setText("￥"+df.format(Double.parseDouble(list.get(position)
					.getPrice())));
			canyu.setText(list.get(position).getPersonTimes());
			zongxu.setText(list.get(position).getTotalPersonTimes());
			shengyu.setText(""
					+ ((Integer.parseInt(list.get(position)
							.getTotalPersonTimes()) - Integer.parseInt(list
							.get(position).getPersonTimes()))));
			progress.setMaxCount(Integer.parseInt(list.get(position)
					.getTotalPersonTimes()) * 1.00f);
			progress.setCurrentCount(Integer.parseInt(list.get(position)
					.getPersonTimes()) * 1.00f);
		} else if (list.get(position).getStatus().equals("2")) {// 揭晓中
			yijiexiao.setVisibility(View.VISIBLE);
			jinxingzhong.setVisibility(View.GONE);
			goods_name.setText(list.get(position).getProductName());
			text2.setText("揭晓中");
			text1.setVisibility(View.INVISIBLE);
			huode.setVisibility(View.INVISIBLE);
			jiexiao_time.setVisibility(View.INVISIBLE);
			
		} else if (list.get(position).getStatus().equals("3")) {// 已揭晓
			goods_name.setText(list.get(position).getProductName());
			yijiexiao.setVisibility(View.VISIBLE);
			jinxingzhong.setVisibility(View.GONE);
			huode.setText(list.get(position).getAwardUserid());
			jiexiao_time.setText(list.get(position).getAnnTime());
		}
		return convertView;
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
