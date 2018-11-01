package com.mall.yyrg.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.mall.net.Web;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.YYRGBaskSingleMessage;
import com.mall.yyrg.model.BaskSingle;

public class BaskSingleAdapter extends BaseAdapter {
	
	
	public static List<BaskSingle> list=new ArrayList<BaskSingle>();
	private Context context;
	private LayoutInflater flater;
	private int width;	
	private BitmapUtils bmUtil;
	public BaskSingleAdapter(Context context ,int width) {
		super();
		this.width=width;
		this.context = context;
		flater = LayoutInflater.from(context);
		bmUtil = new BitmapUtils(context);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
	}
	 public void setList(List<BaskSingle> list){
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
			convertView = flater.inflate(R.layout.yyrg_goods_shaidan_item, null);
		}
		TextView shaidan_userId=(TextView) convertView.findViewById(R.id.shaidan_userId);
		TextView shaidan_time=(TextView) convertView.findViewById(R.id.shaidan_time);
		TextView shaidan_message=(TextView) convertView.findViewById(R.id.shaidan_message);
		TextView shaidan_praise=(TextView) convertView.findViewById(R.id.shaidan_praise);
		TextView shaidan_comment=(TextView) convertView.findViewById(R.id.shaidan_comment);
		ImageView user_logo=(ImageView) convertView.findViewById(R.id.user_logo);
		ImageView shaidan_forward=(ImageView) convertView.findViewById(R.id.shaidan_forward);
		ImageView image1=(ImageView) convertView.findViewById(R.id.image1);
		ImageView image2=(ImageView) convertView.findViewById(R.id.image2);
		ImageView image3=(ImageView) convertView.findViewById(R.id.image3);
		shaidan_userId.setText(list.get(position).getShowName());
		shaidan_time.setText(list.get(position).getSharetime());
		shaidan_message.setText(Html.fromHtml(list.get(position).getContent()));
		shaidan_praise.setText(list.get(position).getPeriodName());
		shaidan_comment.setText(list.get(position).getCommCount());
		if (!TextUtils.isEmpty(list.get(position).getShareimgs())) {
//			if (list.get(position).getShareimgs().indexOf("/upload/s_")!=-1) {
//				String[] images=list.get(position).getShareimgs().substring(1,list.get(position).getShareimgs().length() ).toString().split(",");
//				if (images.length>0) {
//					if (!TextUtils.isEmpty(images[0])) {
//						setImage(image1,  Web.imgServer2 + "/"+images[0], width*2/7, width*2/7,1);
//					}
//				}
//				if (images.length>1) {
//					if (!TextUtils.isEmpty(images[1])) {
//						setImage(image2, Web.imgServer2 + "/"+images[1], width*2/7, width*2/7,1);
//					}
//				}
//				if (images.length>2) {
//					if (!TextUtils.isEmpty(images[2])) {
//						setImage(image3, Web.imgServer2+ "/"+images[2], width*2/7, width*2/7,1);
//					}
//				}
//			}else if(list.get(position).getShareimgs().indexOf("/upload/")!=-1){
				String[] images=list.get(position).getShareimgs().substring(1,list.get(position).getShareimgs().length() ).toString().replace(",", "Y").split("Y");
				if (images.length==1) {
					if (!TextUtils.isEmpty(images[0])) {
						image1.setVisibility(View.VISIBLE);
						setImage(image1,  Web.imgServer2 + "/"+images[0], width*2/7, width*2/7,1);
						image2.setVisibility(View.INVISIBLE);
						image3.setVisibility(View.INVISIBLE);
					}
					
				}else if (images.length==2) {
					if (!TextUtils.isEmpty(images[0])) {
						setImage(image1,  Web.imgServer2 + "/"+images[0], width*2/7, width*2/7,1);
					}
					if (!TextUtils.isEmpty(images[1])) {
						image2.setVisibility(View.VISIBLE);
						setImage(image2, Web.imgServer2 + "/"+images[1], width*2/7, width*2/7,1);
					}
					image3.setVisibility(View.INVISIBLE);
				}else if (images.length==3) {
					if (!TextUtils.isEmpty(images[0])) {
						setImage(image1,  Web.imgServer2 + "/"+images[0], width*2/7, width*2/7,1);
					}
					if (!TextUtils.isEmpty(images[1])) {
						image2.setVisibility(View.VISIBLE);
						setImage(image2, Web.imgServer2 + "/"+images[1], width*2/7, width*2/7,1);
					}
					if (!TextUtils.isEmpty(images[2])) {
						image3.setVisibility(View.VISIBLE);
						setImage(image3, Web.imgServer2+ "/"+images[2], width*2/7, width*2/7,1);
					}
				}
			}
			
			
		//}
		
		if (!TextUtils.isEmpty(list.get(position).getLogo())) {
			setImage(user_logo, "http://" + Web.webImage + "/"+list.get(position).getLogo(), 40, 40,2);
		}
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				YYRGUtil.baskSingle=list.get(position);
				Intent intent=new Intent(context, YYRGBaskSingleMessage.class);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
	private void setImage(final ImageView logo, String href, final int width,
			final int height ,final int state) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				arg2 = Util.zoomBitmap(arg2, width, height);
				if (state==2) {
					arg2=YYRGUtil.createCircleImage(arg2, 40);
				}
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}
}
