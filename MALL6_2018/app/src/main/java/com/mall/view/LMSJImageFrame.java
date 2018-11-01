package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.BaseMallAdapter;
import com.lyc.pic.MyGallery;
import com.lyc.pic.MyImageView;
import com.mall.net.Web;
import com.mall.util.Util;
import com.mall.util.WHD;

import java.util.ArrayList;
import java.util.List;

public class LMSJImageFrame extends Activity implements OnTouchListener {


	private String[] images;
	@ViewInject(R.id.topCenter)
	private TextView centerView;
	private List<String> list = new ArrayList<String>();
	private BitmapUtils bmUtil;
	private BitmapDisplayConfig config;

	// 屏幕的宽度
	public static int screenWidth;
	// 屏幕的高度
	public static int screenHeight;
	@ViewInject(R.id.mygallery)
	private MyGallery gallery;
	private boolean isScale = false; // 是否缩放
	private float beforeLenght = 0.0f; // 两触点距离
	private float afterLenght = 0.0f;
	private float currentScale = 1.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.lmsj_image_frame);
		ViewUtils.inject(this);
		bmUtil = new BitmapUtils(this);
		config = new BitmapDisplayConfig();
		final WHD whd = Util.getScreenSize(this);
		config.setBitmapMaxSize(new BitmapSize(whd.getWidth(), whd.getHeight()));
		Intent intent = getIntent();
		ImageView back = (ImageView) this.findViewById(R.id.topback);
		if (null != back) {
			back.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LMSJImageFrame.this.finish();
				}
			});
		}

		gallery.setVerticalFadingEdgeEnabled(false);
		gallery.setHorizontalFadingEdgeEnabled(false);// );//
														// 设置view在水平滚动时，水平边不淡出。

		// 获取屏幕的大小
		screenWidth = getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		screenHeight = getWindow().getWindowManager().getDefaultDisplay()
				.getHeight()-Util.dpToPx(this, 50F);

		int index = Integer.parseInt(intent.getStringExtra("index"));
		String imagePaths = intent.getStringExtra("images");
		Log.i("images", imagePaths);

		images = imagePaths.split("\\|凸\\|");
		List<MyImageView> imageList = new ArrayList<MyImageView>();
		centerView.setText("商家图片  " + (index + 1) + "/" + images.length);
		if (images.length > 0) {
			for (int i = 0; i < images.length; i++) {
				list.add(images[i]);
				MyImageView view = new MyImageView(this);
				view.setLayoutParams(new Gallery.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				imageList.add(view);
				bmUtil.display(view,
						"http://" + Web.webServer + "/" + list.get(i));

			}
			gallery.setAdapter(new GalleryAdapter(this, imageList));
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					currentScale = 1.0f;
					isScale = false;
					beforeLenght = 0.0f;
					afterLenght = 0.0f;
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});
		}
	}

	/**
	 * 计算两点间的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			beforeLenght = spacing(event);
			if (beforeLenght > 5f) {
				isScale = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			/* 处理拖动 */
			if (isScale) {
				afterLenght = spacing(event);
				if (afterLenght < 5f)
					break;
				float gapLenght = afterLenght - beforeLenght;
				if (gapLenght == 0) {
					break;
				} else if (Math.abs(gapLenght) > 5f) {
					float scaleRate = gapLenght / 854;
					Animation myAnimation_Scale = new ScaleAnimation(
							currentScale, currentScale + scaleRate,
							currentScale, currentScale + scaleRate,
							Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					myAnimation_Scale.setDuration(100);
					myAnimation_Scale.setFillAfter(true);
					myAnimation_Scale.setFillEnabled(true);
					currentScale = currentScale + scaleRate;
					gallery.getSelectedView().setLayoutParams(
							new Gallery.LayoutParams(
									(int) (480 * (currentScale)),
									(int) (854 * (currentScale))));
					beforeLenght = afterLenght;
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			isScale = false;
			break;
		}
		return false;
	}
}

class GalleryAdapter extends BaseMallAdapter<MyImageView> {

	public GalleryAdapter(Context context, List<MyImageView> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent,
			MyImageView t) {
		return t;
	}

}
