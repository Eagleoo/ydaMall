package com.lin.component;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.mall.model.Product;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.ServiceProductDeatil;

public class LMSJDetailAdapter extends BaseAdapter{
	private List<Product> list;
	private Context context;
	private int width;
	private LayoutInflater inflater;
	private BitmapUtils bmUtils = null;
	public LMSJDetailAdapter(List<Product> list,Context context,int width){
		this.list=list;
		bmUtils = new BitmapUtils(context);
		this.context=context;
		this.width=width;
		inflater=LayoutInflater.from(context);
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
			convertView=inflater.inflate(R.layout.lmsj_detail_list_item, null);
		}
		TextView name=(TextView) convertView.findViewById(R.id.name);
		TextView sb_jiage=(TextView) convertView.findViewById(R.id.sb_jiage);
		sb_jiage.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG ); 
		TextView diannei_jia=(TextView) convertView.findViewById(R.id.diannei_jia);
		final ImageView image=(ImageView) convertView.findViewById(R.id.image);
		Log.e("商品名", ""+list.get(position).getName());
		name.setText(list.get(position).getName());
		Log.e("价格","市场价"+list.get(position).getPriceMarket()+"~~~~~~~ 店内价"+list.get(position).getSbj());
		sb_jiage.setText("￥"+list.get(position).getPriceMarket());
		diannei_jia.setText("￥"+list.get(position).getPrice());
		setImageWidthAndHeight(image, 1, width /4, width /4);
		bmUtils.display(image, list.get(position).getThumb(),new DefaultBitmapLoadCallBack<ImageView>(){
			@Override
			public void onLoadCompleted(ImageView container, String uri,
					Bitmap bitmap, BitmapDisplayConfig config,
					BitmapLoadFrom from) {
				bitmap = Util.toRoundCorner(bitmap, 10);
				super.onLoadCompleted(container, uri, bitmap, config, from);
			}

			@Override
			public void onLoadFailed(ImageView container, String uri,
					Drawable drawable) {
				super.onLoadFailed(container, uri, drawable);
				image.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!Util.isNull(list.get(position).getPid()))
				Util.showIntent(context, ServiceProductDeatil.class,
						new String[] { "url" }, new String[] { list.get(position).getPid() });
			}
		});
		return convertView;
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
