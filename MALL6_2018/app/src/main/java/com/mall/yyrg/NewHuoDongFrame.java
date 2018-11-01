package com.mall.yyrg;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.view.ProductDeatilFream;
import com.mall.view.R;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;
import com.mall.yyrg.model.HuoDong;

public class NewHuoDongFrame extends Activity{
	@ViewInject(R.id.goods_list)
	private GridView goods_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_huodong_frame);
	}
	public class NewHuoDongAdapter extends BaseAdapter {
		private List<HuoDong> list = new ArrayList<HuoDong>();
		private LayoutInflater inflater;
		private AsyncImageLoader imageLoader;
		public NewHuoDongAdapter() {
			 inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        ImageCacheManager cacheMgr = new ImageCacheManager(NewHuoDongFrame.this);
		        imageLoader = new AsyncImageLoader(NewHuoDongFrame.this, cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		}

		public void setList(List<HuoDong> list) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
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
		public View getView(final int position, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (view == null) {
				view = inflater.inflate(R.layout.yyrg_new_forum_activitys_item,
						null);
			}
			// final List<HuoDong> thisList = list.get(position);
			DecimalFormat df = new DecimalFormat("#.00");
			final ImageView goods_image = (ImageView) view
					.findViewById(R.id.goods_image);
			TextView goods_name = (TextView) view.findViewById(R.id.goods_name);
			TextView now_price = (TextView) view.findViewById(R.id.now_price);
			TextView old_price = (TextView) view.findViewById(R.id.old_price);
			TextView sb_price = (TextView) view.findViewById(R.id.sb_price);
			TextView zhekou = (TextView) view.findViewById(R.id.zhekou);
			TextView reviewNum = (TextView) view.findViewById(R.id.reviewNum);
			LinearLayout lin1 = (LinearLayout) view.findViewById(R.id.lin1);
		//	setImageWidthAndHeight(goods_image, 1, width * 2 / 5, width * 2 / 5);
		//	Bitmap bmp = imageLoader.loadBitmap(goods_image, list.get(position).getProductThumb(), true, width * 2 / 5, width * 2 / 5);
	      /*  if(bmp == null) {
	        	goods_image.setImageResource(R.drawable.new_yda__top_zanwu);
	        } else {
	        	goods_image.setImageBitmap(bmp);
	        }*/
			goods_name.setText(list.get(position).getShortTitle());
			now_price.setText("￥"
					+ df.format(Double.parseDouble(list.get(position)
							.getPrice())));
			old_price.setText("￥"
					+ df.format(Double.parseDouble(list.get(position)
							.getPriceMarket())));
			old_price.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
			sb_price.setText("商币兑换：" + list.get(position).getJfPrice());
			zhekou.setText(list.get(position).getZhekou() + "折");
			reviewNum.setText(list.get(position).getReviewNum() + "条");
			lin1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(NewHuoDongFrame.this,
							ProductDeatilFream.class);
					intent.putExtra("url", list.get(position).getProductID());
					startActivity(intent);
				}
			});

			return view;
		}

	}
}
