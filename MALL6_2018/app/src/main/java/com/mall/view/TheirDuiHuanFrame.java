package com.mall.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;
import com.mall.yyrg.model.TheirDuiHuan;

public class TheirDuiHuanFrame extends Activity {
	@ViewInject(R.id.listView)
	private ListView listView;
	private List<TheirDuiHuan> theirDuiHuans = new ArrayList<TheirDuiHuan>();
	private int width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.their_duihuan);
		ViewUtils.inject(this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		getTheir();
	}

	@OnClick(R.id.top_back)
	public void back(View view) {
		finish();
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

	public class TheirDuiHuanAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private AsyncImageLoader imageLoader;

		public TheirDuiHuanAdapter() {
			inflater = LayoutInflater.from(TheirDuiHuanFrame.this);
			ImageCacheManager cacheMgr = new ImageCacheManager(
					TheirDuiHuanFrame.this);
			imageLoader = new AsyncImageLoader(TheirDuiHuanFrame.this,
					cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return theirDuiHuans.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return theirDuiHuans.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.their_duihuan_item,
						null);
				holder = new Holder();
				holder.goodimg = (ImageView) convertView
						.findViewById(R.id.goodsimg);
				holder.userid = (TextView) convertView
						.findViewById(R.id.userid);
				holder.count = (TextView) convertView.findViewById(R.id.count);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			setImageWidthAndHeight(holder.goodimg, 1, width / 4,
					width * 1 / 4 - 20);
			Bitmap bmp = imageLoader.loadBitmap(holder.goodimg, theirDuiHuans
					.get(position).getProductThumb(), true, width * 2 / 5,
					width * 2 / 5);
			if (bmp == null) {
				holder.goodimg.setImageResource(R.drawable.new_yda__top_zanwu);
			} else {
				holder.goodimg.setImageBitmap(bmp);
			}
			holder.userid.setText(theirDuiHuans.get(position).getsUserId());
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(TheirDuiHuanFrame.this,
							ProductDeatilFream.class);
					intent.putExtra("url", theirDuiHuans.get(position)
							.getProductid());
					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	/**
	 * 获得他们都在换的信息
	 */
	private void getTheir() {
		Util.asynTask(this, "载入中……", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				if (runData != null) {
					List<TheirDuiHuan> list = new ArrayList<TheirDuiHuan>();
					list = ((HashMap<Integer, List<TheirDuiHuan>>) runData)
							.get(1);
					if (list.size() > 0) {
						theirDuiHuans = list;
						listView.setAdapter(new TheirDuiHuanAdapter());
					}
				} else {
					Toast.makeText(TheirDuiHuanFrame.this,
							"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				int index = 0;
				Web web = new Web(Web.yyrgAddress, Web.GetTheyExchang, "UTF-8");
				List<TheirDuiHuan> list = web.getList(TheirDuiHuan.class);
				HashMap<Integer, List<TheirDuiHuan>> map = new HashMap<Integer, List<TheirDuiHuan>>();
				map.put(1, list);
				return map;
			}
		});
	}

	class Holder {
		ImageView goodimg;
		TextView userid;
		TextView count;
	}
}
