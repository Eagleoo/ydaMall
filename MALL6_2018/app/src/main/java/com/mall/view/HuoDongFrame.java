package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.MainProduct;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HuoDongFrame extends Activity {
	@ViewInject(R.id.prodShow)
	private ListView listView;
	private int page = 0;
	private boolean isFoot = false; // 用来判断是否已滑动到底部
	private HuodongProductAdapter adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.huodong);
		ViewUtils.inject(this);
		Util.initTitle(this, "活动专区", new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.showIntent(HuoDongFrame.this, Lin_MainFrame.class);
			}
		});
		page();
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 判断是否已滑动或者处于底部
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && isFoot) {
					// 滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						page();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem + visibleItemCount == totalItemCount) {
					isFoot = true;
				} else
					isFoot = false;
			}
		});
	}

	private void page() {
		Util.asynTask(this, "正在加载数据...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<MainProduct>> map = (HashMap<String, List<MainProduct>>) runData;
				List<MainProduct> list = map.get("list");
				if (list != null && list.size() != 0) {
					if (null != adapter) {
						adapter.add(list);
						adapter.notifyDataSetChanged();
					} else {
						adapter = new HuodongProductAdapter(HuoDongFrame.this, list);
						listView.setAdapter(adapter);
					}
				} else
					Util.show("没有找到您要的商品...", HuoDongFrame.this);
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getActivityProduct, "cate="
						+ "&size=11&page=" +(++page));
				List<MainProduct> list = web.getList(MainProduct.class);
				HashMap<String, List<MainProduct>> map = new HashMap<String, List<MainProduct>>();
				map.put("list", list);
				return map; 
			}
		});
	}
}

class HuodongProduct {
	public LinearLayout line;
	public ImageView logo;
	public TextView name;
	public TextView scsj;
	public TextView ydsj;
	public TextView kyjf;
	public ImageView rightImg;
	public TextView dicount;
}

class HuodongProductAdapter extends BaseAdapter {

	private Context c;
	private int _90dp = 90;
	private int _100dp = 100;
	private List<MainProduct> data = new ArrayList<MainProduct>();
	private BitmapUtils bmUtil;
	private BitmapDisplayConfig config;
	public HuodongProductAdapter(Context c, List<MainProduct> data) {
		super();
		this.c = c;
		this.data = data;
		_90dp = Util.dpToPx(c, 90F);
		_100dp = Util.dpToPx(c, 100F);
		config = new BitmapDisplayConfig();
		config.setBitmapMaxSize(new BitmapSize(_100dp, _90dp));
		bmUtil=new BitmapUtils(c);
	}

	public void add(List<MainProduct> list) {
		data.addAll(list);
	}

	public List<MainProduct> getData() {
		return data;
	}

	public void clearData() {
		data.clear();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).hashCode();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		HuodongProduct holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(c).inflate(R.layout.huodong_item,
					null);
			holder = new HuodongProduct();
			holder.line = (LinearLayout) convertView
					.findViewById(R.id.product_item_line_lin);
			holder.logo = (ImageView) convertView.findViewById(R.id.prod_img);
			holder.name = (TextView) convertView.findViewById(R.id.prod_name);
			holder.scsj = (TextView) convertView.findViewById(R.id.prod_ypri);
			holder.ydsj = (TextView) convertView.findViewById(R.id.prod_pri);
			holder.kyjf = (TextView) convertView.findViewById(R.id.prod_jjff);
			holder.rightImg = (ImageView) convertView
					.findViewById(R.id.product_item_right_img);
			holder.dicount = (TextView) convertView
					.findViewById(R.id.huodong_dicount);
			convertView.setTag(holder);
		} else
			holder = (HuodongProduct) convertView.getTag();
		holder.logo.setImageDrawable(c.getResources().getDrawable(
				R.drawable.zw174));
		final MainProduct pro = data.get(position);
		holder.rightImg.setImageDrawable(c.getResources().getDrawable(
				R.drawable.img_ljgm));

		holder.name.setText(pro.getName());
		holder.scsj.setText("市场售价：￥" + pro.getPriceMarket());
		holder.ydsj.setText("远大售价：￥" + pro.getPrice());
		double kyjf = (Double.parseDouble(pro.getPrice()) - Double
				.parseDouble(pro.getExpPrice1()));
		holder.kyjf.setText("可用消费券：" + Util.getDouble(kyjf, 2));
		holder.dicount.setText(pro.getZhe());

		final String href = pro.getThumb().replaceFirst("img.mall666.cn",
				Web.imgServer);
		final ImageView logo = holder.logo;
		
		
		bmUtil.display(logo, href,config,
				new DefaultBitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View arg0, String arg1,
							Bitmap arg2, BitmapDisplayConfig arg3,
							BitmapLoadFrom arg4) {
						arg2 = Util.zoomBitmap(arg2, _100dp, _90dp);
						super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
					}

					@Override
					public void onLoadFailed(View arg0, String arg1,
							Drawable arg2) {
						logo.setImageResource(R.drawable.zw174);
					}
				});
		// 添加事件---点击事件
		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(c, ProductDeatilFream.class);
				intent.putExtra("className", c.getClass().toString());
				intent.putExtra("url", pro.getPid());
				intent.putExtra("act", "act");
				c.startActivity(intent);
			}
		});
		return convertView;
	}
}
