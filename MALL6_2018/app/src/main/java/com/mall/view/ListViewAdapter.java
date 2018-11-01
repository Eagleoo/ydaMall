package com.mall.view;

import java.text.DecimalFormat;
import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.mall.model.Product;
import com.mall.net.Web;
import com.mall.util.Data;
import com.mall.util.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author SunnyCoffee
 * @date 2014-2-2
 * @version 1.0
 * @desc 适配器
 * 
 */
public class ListViewAdapter extends BaseAdapter {

	private ViewHolder holder;
	private List<Product> list;
	private Context context;
	private BitmapUtils bmUtil;
	private BitmapDisplayConfig config;

	public ListViewAdapter(Context context, List<Product> list) {
		this.list = list;
		this.context = context;
		bmUtil = new BitmapUtils(context);
		config = new BitmapDisplayConfig();
		config.setBitmapMaxSize(new BitmapSize(Util.dpToPx(context, 500F), Util
				.dpToPx(context, 500F)));
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listview_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.newprice = (TextView) convertView
					.findViewById(R.id.newprice);
			holder.oldprice = (TextView) convertView
					.findViewById(R.id.oldprice);
			holder.oldprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			holder.point = (TextView) convertView.findViewById(R.id.point);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DecimalFormat df = new DecimalFormat("#0.00");
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Data.setProductClass(1);
				Util.showIntent(context, ProductDeatilFream.class,
						new String[] { "url" },
						new String[] { list.get(position).getProductid() });
			}
		});
		final String href = list.get(position).getProductThumb()
				.replaceFirst("img.mall666.cn", Web.imgServer);
		final ImageView logo = holder.image;
		bmUtil.display(logo, href, config,
				new DefaultBitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						arg2 = Util.zoomBitmap(arg2, 500, 500);
						super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
					}

					@Override
					public void onLoadFailed(View arg0, String arg1,
							Drawable arg2) {
						logo.setImageResource(R.drawable.zw174);
					}
				});
		holder.title.setText(list.get(position).getProductName());
		holder.newprice.setText("¥"
				+ df.format(Double.parseDouble(list.get(position)
						.getSalePrice())));
		holder.oldprice.setText("¥"
				+ df.format(Double.parseDouble((list.get(position)
						.getPriceMarket() + ""))));
		holder.point.setText("可用消费券:¥" + list.get(position).getPrice());
		return convertView;
	}

	private static class ViewHolder {
		ImageView image;
		TextView title;
		TextView newprice;
		TextView oldprice;
		TextView point;
	}

}
