package com.mall.yyrg.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.MyProgressView;
import com.mall.yyrg.model.NewAnnounce;

public class YYRGGoodsListAdapter extends BaseAdapter {
	
	
	private List<NewAnnounce> list=new ArrayList<NewAnnounce>();
	private Context context;
	private LayoutInflater flater;
	private int width;	
	private BitmapUtils bmUtil;
	private Handler handler;
	public YYRGGoodsListAdapter(Context context ,int width,Handler handler) {
		super();
		this.handler=handler;
		this.width=width;
		this.context = context;
		flater = LayoutInflater.from(context);
		bmUtil = new BitmapUtils(context);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
	}
	 public void setList(List<NewAnnounce> list){
	    	this.list.addAll(list);
			this.notifyDataSetChanged();
	    }
	
	public void clear(){
		this.list.clear();
	}
	
	public void updateUI(){
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = flater.inflate(R.layout.yyrg_goods_item, null);
		}
		ImageView goods_img=(ImageView) convertView.findViewById(R.id.goods_img);
		TextView goodsname=(TextView) convertView.findViewById(R.id.goodsname);
		TextView goods_price=(TextView) convertView.findViewById(R.id.goods_price);
		MyProgressView progress=(MyProgressView) convertView.findViewById(R.id.progress);
		ImageView addcart=(ImageView) convertView.findViewById(R.id.addcart);
		TextView canyu = (TextView) convertView.findViewById(R.id.canyu);
		TextView zongxu = (TextView) convertView.findViewById(R.id.zongxu);
		TextView shengyu = (TextView) convertView.findViewById(R.id.shengyu);
		
		setImage(goods_img, list.get(position).getPhotoThumb(), width/4, width/4);
		goodsname.setText(list.get(position).getProductName());
		DecimalFormat df = new DecimalFormat("#0.00");
		ImageView imageView=new ImageView(context);
		setImageWidthAndHeight(goods_img, 1, width/4, width/4);
		goods_price.setText("￥"+df.format(Double.parseDouble(list.get(position).getPrice())));
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
		addcart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Message msg=new Message();
				msg.what=3;
				msg.obj=list.get(position);
				handler.sendMessage(msg);
			}
		});
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

	}
}
