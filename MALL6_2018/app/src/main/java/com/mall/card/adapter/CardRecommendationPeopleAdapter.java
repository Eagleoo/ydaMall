package com.mall.card.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mall.card.bean.CallBackListener;
import com.mall.card.bean.CardLinkman;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;

public class CardRecommendationPeopleAdapter extends BaseAdapter{
	private CallBackListener callBackListener;
	private Context context;
	private List<CardLinkman> list=new ArrayList<CardLinkman>();
	private LayoutInflater inflater;
	private AsyncImageLoader imageLoader;
	private int width;
	public CardRecommendationPeopleAdapter(Context context,int width){
		this.context=context;
		this.width=width;
		inflater=LayoutInflater.from(context);
		ImageCacheManager cacheMgr = new ImageCacheManager(
				context);
		imageLoader = new AsyncImageLoader(context,
				cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
	}
	public void setList(List<CardLinkman> list){
		this.list.addAll(list);
		this.notifyDataSetChanged();
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
		if (convertView==null) {
			convertView=inflater.inflate(R.layout.card_exchange_card_request_item, null);
		}
		ImageView user_img = (ImageView) convertView
				.findViewById(R.id.user_img);
		TextView city = (TextView) convertView.findViewById(R.id.city);
		TextView username = (TextView) convertView
				.findViewById(R.id.username);
		TextView exchange = (TextView) convertView
				.findViewById(R.id.exchange);
		TextView message = (TextView) convertView
				.findViewById(R.id.message);
		TextView exchange1=(TextView) convertView.findViewById(R.id.exchange1);
		city.setVisibility(View.VISIBLE);
		username.setText(list.get(position).getName());
		exchange.setText("交换名片");
		if (CardUtil.exchangeUser!=null) {
			
		for (int i = 0; i < CardUtil.exchangeUser.size(); i++) {

			if (list.get(position).getUserid()
					.equals(CardUtil.exchangeUser.get(i).getTouserid())) {
				System.out.println("已交换");
				exchange1.setVisibility(View.VISIBLE);
				exchange.setVisibility(View.GONE);
			}
		}
		}
		if (CardUtil.faqiRe!=null) {
			
		for (int i = 0; i < CardUtil.faqiRe.size(); i++) {

			if (list.get(position).getUserid()
					.equals(CardUtil.faqiRe.get(i).getTouserid())) {
				System.out.println("已发起交换");
				exchange1.setVisibility(View.VISIBLE);
				exchange1.setText("已发起交换");
				exchange.setVisibility(View.GONE);
			}
		}
		}
		if (CardUtil.shoudaoRe!=null) {
		for (int i = 0; i < CardUtil.shoudaoRe.size(); i++) {

			if (list.get(position).getUserid()
					.equals(CardUtil.shoudaoRe.get(i).getTouserid())) {
				System.out.println("收到请求");
				exchange1.setVisibility(View.VISIBLE);
				exchange1.setText("收到请求");
				exchange.setVisibility(View.GONE);
			}
		}
		}
		message.setText(list.get(position).getDuty()+"\t"+list.get(position).getCompanyName());
		setImageWidthAndHeight(user_img, 1, width * 1 / 5, width * 1 / 5);
		ImageView imageView = new ImageView(context);
		Bitmap bmp = imageLoader.loadBitmap(imageView, list.get(position)
				.getUserFace(), true, width * 2 / 5, width * 2 / 5);
		if (bmp == null) {
			user_img.setImageResource(R.drawable.addgroup_item_icon);
		} else {
			user_img.setImageBitmap(bmp);
		}
		Bitmap bitmap = ((BitmapDrawable) user_img.getDrawable())
				.getBitmap();
		bitmap = Util.toRoundCorner(bitmap, 10);
		user_img.setImageBitmap(bitmap);
		exchange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callBackListener.callBack(list.get(position));
			}
		});
		return convertView;
	}
	public void setCallBack(CallBackListener callBackListener){
		this.callBackListener=callBackListener;
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
