package com.mall.adapter;

import java.util.List;

import com.mall.model.OrderTwo;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.view.ProductDeatilFream;
import com.mall.view.R;
import com.mall.view.StoreFrame;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderFuKuanTwo extends NewBaseAdapter {

	private ImageLoader imload;
	private List<OrderTwo> list;
	private String orderType;

	public OrderFuKuanTwo(Context c, List list,String o) {
		super(c, list);
		imload = AnimeUtil.getImageLoad();
		this.list = list;
		this.orderType=o;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.new_order_fukuan_two_item, null);
			vh.order_type = (TextView) convertView
					.findViewById(R.id.order_type);
			vh.order_title = (TextView) convertView
					.findViewById(R.id.order_title);
			vh.order_money = (TextView) convertView
					.findViewById(R.id.order_money);
			vh.order_num = (TextView) convertView.findViewById(R.id.order_num);
			vh.two_im = (ImageView) convertView.findViewById(R.id.two_im);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final OrderTwo two = list.get(position);
		vh.order_title.setText(two.productName);
		vh.order_money.setText("￥" + two.unitCost.replace(".00", ""));
		vh.order_num.setText("x" + two.quantity);
		vh.order_type.setText(two.colorAndSize.replace("颜色：", "").replace(
				"尺码：", ""));
		imload.displayImage(two.productThumb, vh.two_im,
				AnimeUtil.getImageRectOption());
		vh.two_im.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, ProductDeatilFream.class);
				intent.putExtra("url", two.productId);
				intent.putExtra("orderType",orderType);
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		private ImageView two_im;
		private TextView order_title;
		private TextView order_type;
		private TextView order_money;
		private TextView order_num;

	}
}
