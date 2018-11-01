package com.mall.officeonline;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * 查看大图的Activity界面。
 * 
 * @author guolin
 */
public class ImageDetailsActivity extends Activity implements
		OnPageChangeListener {

	/**
	 * 用于管理图片的滑动
	 */
	private ViewPager viewPager;

	/**
	 * 显示当前图片的页数
	 */
	private TextView pageText;
	private List<String> urls=new ArrayList<String>();
	private BitmapUtils bmUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.image_details);
		bmUtils=new BitmapUtils(this);
		urls=getIntent().getStringArrayListExtra("urls");
		int imagePosition = getIntent().getIntExtra("image_position", 0);
		System.out.println("image_position========="+imagePosition+"  urls.size()===="+urls.size());
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		ViewPagerAdapter adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(imagePosition);
		viewPager.setOnPageChangeListener(this);
		viewPager.setEnabled(false);  
		// 设定当前的页数和总页数
		Util.initTop(this, "相册", Integer.MIN_VALUE, new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageDetailsActivity.this.finish();
			}
		});
	}

	/**
	 * ViewPager的适配器
	 * 
	 * @author guolin
	 */
	class ViewPagerAdapter extends PagerAdapter {
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			System.out.println("----------------instantiateItem-----------------");
			View view = LayoutInflater.from(ImageDetailsActivity.this).inflate(R.layout.zoom_image_layout, null);
			final ZoomImageView zoomImageView = (ZoomImageView) view.findViewById(R.id.zoom_image_view);
			bmUtils.display(zoomImageView, urls.get(position), new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View arg0, String arg1,
						Bitmap arg2, BitmapDisplayConfig arg3,
						BitmapLoadFrom arg4) {
					zoomImageView.setImageBitmap(arg2);
				}
				@Override
				public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
					Resources r = ImageDetailsActivity.this.getResources();
					InputStream is = r.openRawResource(R.drawable.ic_launcher);
					BitmapDrawable bmpDraw = new BitmapDrawable(is);
					zoomImageView.setImageBitmap(bmpDraw.getBitmap());
				}
			});
			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			return urls.size();
		}   
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			container.removeView(view);
		}
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int currentPage) {
		// 每当页数发生改变时重新设定一遍当前的页数和总页数
	}

}