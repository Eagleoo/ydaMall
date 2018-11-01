package com.mall.yyrg;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.YYRGUtil;

/**
 * 晒单的详细信息
 * 
 * @author Administrator
 * 
 */
public class YYRGBaskSingleMessage extends Activity {
	@ViewInject(R.id.share_short_name)
	private TextView share_short_name;
	@ViewInject(R.id.user_logo)
	private ImageView user_logo;
	@ViewInject(R.id.shaidan_userId)
	private TextView shaidan_userId;
	@ViewInject(R.id.shaidan_time)
	private TextView shaidan_time;
	@ViewInject(R.id.shaidan_message)
	private TextView shaidan_message;
	@ViewInject(R.id.shaidan_praise)
	private TextView shaidan_praise;
	@ViewInject(R.id.shaidan_comment)
	private TextView shaidan_comment;
	@ViewInject(R.id.message_board_priase_user)
	private LinearLayout message_board_priase_user;
	@ViewInject(R.id.shaidan_forward)
	private ImageView shaidan_forward;
	private BitmapUtils bmUtil;
	@ViewInject(R.id.listView1)
	private ListView listView1;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private AllImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_bask_single_message);
		ViewUtils.inject(this);
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		share_short_name.setText(YYRGUtil.baskSingle.getTitle());
		shaidan_userId.setText(YYRGUtil.baskSingle.getUserId().replace("_p", ""));
		shaidan_time.setText(YYRGUtil.baskSingle.getSharetime());
		shaidan_message
				.setText(Html.fromHtml(YYRGUtil.baskSingle.getContent()));
		shaidan_praise.setText(YYRGUtil.baskSingle.getPeriodName());
		shaidan_comment.setText(YYRGUtil.baskSingle.getCommCount());
		if (!TextUtils.isEmpty(YYRGUtil.baskSingle.getLogo())) {
			setImage(user_logo, "http://" + Web.webImage + "/"+YYRGUtil.baskSingle.getLogo(), 40, 40);
		}
		if (TextUtils.isEmpty(YYRGUtil.baskSingle.getShareimgs())) {

		} else {
				String[] images = YYRGUtil.baskSingle.getShareimgs()
						.substring(1, YYRGUtil.baskSingle.getShareimgs().length())
						.toString().replace(",", "Y").split("Y");
				System.out.println(images.length + "=================");
				for (int i = 0; i < images.length; i++) {
					if (!TextUtils.isEmpty(images[i])) {
						ImageView vImageView = new ImageView(this);
						System.out.println(Web.imgServer2 + "/"
								+ images[i]);
						setImage(vImageView, Web.imgServer2+ "/" + images[i],
								images.length);
					}
				}
		}
	}

	@OnClick(R.id.top_back)
	public void returnOnclick(View view) {
		finish();
	}

	private void setImage(final ImageView logo, String href,
			final int imageLength) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				arg2 = Util.zoomBitmap(arg2, arg2.getWidth(), arg2.getHeight());
				bitmaps.add(arg2);
				if (bitmaps.size() == imageLength) {
					listView1.setAdapter(new AllImageAdapter(bitmaps));
				}
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}
	private void setImages(final ImageView logo, final String href) {
		bmUtil.display(logo, Web.imageip+"/"+href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				arg2 = Util.zoomBitmap(arg2, arg2.getWidth(), arg2.getHeight());
				bitmaps.add(arg2);
					listView1.setAdapter(new AllImageAdapter(bitmaps));
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				bmUtil.display(logo,Web.imgServer2+"/"+ href, new DefaultBitmapLoadCallBack<View>() {
					@Override
					public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
							BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
						arg2 = Util.zoomBitmap(arg2, arg2.getWidth(), arg2.getHeight());
						bitmaps.add(arg2);
							listView1.setAdapter(new AllImageAdapter(bitmaps));
						super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
					}

					@Override
					public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
						logo.setImageResource(R.drawable.new_yda__top_zanwu);
					}
				});
			}
		});
	}

	private void setImage(final ImageView logo, String href, final int width,
			final int height) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				arg2 = Util.zoomBitmap(arg2, width, height);
				arg2 = YYRGUtil.createCircleImage(arg2, 40);
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.new_yda__top_zanwu);
			}
		});
	}

	class AllImageAdapter extends BaseAdapter {
		private List<Bitmap> list = new ArrayList<Bitmap>();
		private LayoutInflater flater;

		AllImageAdapter(List<Bitmap> list) {
			this.list = list;
			flater = LayoutInflater.from(YYRGBaskSingleMessage.this);
		}

		public void setList(List<Bitmap> list) {
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
		public View getView(int arg0, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = flater.inflate(R.layout.yyrg_bask_item, null);
			}
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.image);
			imageView.setImageBitmap(list.get(arg0));
			return convertView;
		}
	}
}
