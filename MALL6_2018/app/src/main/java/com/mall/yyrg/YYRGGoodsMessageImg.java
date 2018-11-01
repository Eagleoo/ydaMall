package com.mall.yyrg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.mall.net.Web;
import com.mall.view.R;
import com.mall.view.messageboard.GestureDetector;
import com.mall.view.messageboard.ImageViewTouch;
import com.mall.view.messageboard.PagerAdapter;
import com.mall.view.messageboard.ScaleGestureDetector;
import com.mall.view.messageboard.ViewPager;


@SuppressLint("UseSparseArrays")
public class YYRGGoodsMessageImg extends Activity {
	private static final int PAGER_MARGIN_DP = 40;
	private ViewPager mViewPager;
	private int pagerMarginPixels = 0;
	private ImagePagerAdapter mPagerAdapter;
	private GestureDetector mGestureDetector;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	private int currentPic = 0;
	private boolean mPaused;
	private boolean mOnScale = false;
	private boolean mOnPagerScoll = false;
	private boolean mControlsShow = false;
	private ScaleGestureDetector mScaleGestureDetector;
	public Map<Integer, ImageViewTouch> viewss = new HashMap<Integer, ImageViewTouch>();
	public BitmapUtils bmUtil;
	private String goodsimgId="";
	private String[] imgs=null;
	private TextView top_back;
	private TextView picindicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_goods_message_img);
		top_back=(TextView) findViewById(R.id.top_back);
		picindicator=(TextView) findViewById(R.id.picindicator);
		picindicator.setText("");
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		bmUtil = new BitmapUtils(this);
		bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
		goodsimgId=getIntent().getStringExtra("goodsimg");
		
		if (TextUtils.isEmpty(goodsimgId)) {
			Toast.makeText(this, "暂无商品详细图片", Toast.LENGTH_SHORT).show();
		}else {
			imgs=goodsimgId.split("\\|");
		}
		for (int i = 0; i < imgs.length; i++) {
			ImageView imageView=new ImageView(this);
			setImage(imageView, "http://" + Web.imgServer+"/"+imgs[i].split(",")[1]);
		}
		
	}

	private void init() {
		mViewPager = (ViewPager) findViewById(R.id.message_board_pic_show_pager);
		final float scale = getResources().getDisplayMetrics().density;
		pagerMarginPixels = (int) (PAGER_MARGIN_DP * scale + 0.5f);
		mViewPager.setPageMargin(pagerMarginPixels);
		mPagerAdapter = new ImagePagerAdapter();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPager.setCurrentItem(currentPic);
		setupOnTouchListeners(mViewPager);
	}
	private void setImage(final ImageView logo, String href) {
		bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
					BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
				bitmaps.add(arg2);
				if (bitmaps.size()==imgs.length) {
					picindicator.setText("1/"+bitmaps.size());
					init();
				}
				super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
			}

			@Override
			public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
				logo.setImageResource(R.drawable.zw174);
			}
		});
	}
	@SuppressLint("UseSparseArrays")
	private class ImagePagerAdapter extends PagerAdapter {
		public Map<Integer, ImageViewTouch> views = new HashMap<Integer, ImageViewTouch>();

		@Override
		public int getCount() {
			return bitmaps.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ImageViewTouch imageView = new ImageViewTouch(YYRGGoodsMessageImg.this);
			imageView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			imageView.setFocusableInTouchMode(true);
			Bitmap b = bitmaps.get(position);
			imageView.setImageBitmapResetBase(b, true);
			imageView.setTag(position);
			((ViewPager) container).addView(imageView);
			views.put(position, imageView);
			viewss.put(position, imageView);
			return imageView;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			ImageViewTouch imageView = (ImageViewTouch) object;
			imageView.setImageBitmapResetBase(null, true);
			imageView.clear();
			((ViewPager) container).removeView(imageView);
			views.remove(position);
		}

		@Override
		public void startUpdate(View container) {
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageViewTouch) object);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}
	}

	private ImageViewTouch getCurrentImageView() {
		if (mPagerAdapter.views != null) {
			if (mPagerAdapter.views.get(mViewPager.getCurrentItem()) != null) {
				return (ImageViewTouch) mPagerAdapter.views.get((mViewPager
						.getCurrentItem()));
			} else {
				ImageViewTouch img = new ImageViewTouch(YYRGGoodsMessageImg.this);
				img.setImageBitmap(BitmapFactory.decodeResource(
						YYRGGoodsMessageImg.this.getResources(),
						R.drawable.ic_launcher));
				return img;
			}
		} else {
			ImageViewTouch img = new ImageViewTouch(YYRGGoodsMessageImg.this);
			img.setImageBitmap(BitmapFactory.decodeResource(
					YYRGGoodsMessageImg.this.getResources(), R.drawable.ic_launcher));
			return img;
		}
	}

	ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int position, int prePosition) {
			ImageView imageView=viewss.get(position);
			int temp=Integer.parseInt(imageView.getTag().toString())+1;
			picindicator.setText(temp+"/"+bitmaps.size());
		//	Toast.makeText(YYRGGoodsMessageImg.this, "这是第"+imageView.getTag()+"个", Toast).show();
			mViewPager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			mOnPagerScoll = true;
		} 

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_DRAGGING) {
				mOnPagerScoll = true;
			} else if (state == ViewPager.SCROLL_STATE_SETTLING) {
				mOnPagerScoll = false;
			} else {
				mOnPagerScoll = false;
			}
		}
	};

	private void setupOnTouchListeners(View rootView) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
			mScaleGestureDetector = new ScaleGestureDetector(this,
					new MyOnScaleGestureListener());
		}
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		OnTouchListener rootListener = new OnTouchListener() {
			@SuppressLint("NewApi")
			public boolean onTouch(View v, MotionEvent event) {
				if (!mOnScale) {
					if (!mOnPagerScoll) {
						mGestureDetector.onTouchEvent(event);
					}
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
					if (!mOnPagerScoll) {
						mScaleGestureDetector.onTouchEvent(event);
					}
				}

				ImageViewTouch imageView = getCurrentImageView();
				if (imageView == null) {
					return true;
				}
				if (!mOnScale) {
					Matrix m = imageView.getImageViewMatrix();
					RectF rect = new RectF(0, 0, imageView.mBitmapDisplayed
							.getBitmap().getWidth(), imageView.mBitmapDisplayed
							.getBitmap().getHeight());
					m.mapRect(rect);
					if (!(rect.right > imageView.getWidth() + 0.1 && rect.left < -0.1)) {
						try {
							mViewPager.onTouchEvent(event);
						} catch (ArrayIndexOutOfBoundsException e) {
						}
					}
				}
				return true;
			}
		};
		rootView.setOnTouchListener(rootListener);
	}

	private class MyOnScaleGestureListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		float currentScale;
		float currentMiddleX;
		float currentMiddleY;

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			final ImageViewTouch imageView = getCurrentImageView();
			if (imageView == null) {
				return;
			}
			if (currentScale > imageView.mMaxZoom) {
				imageView
						.zoomToNoCenterWithAni(currentScale
								/ imageView.mMaxZoom, 1, currentMiddleX,
								currentMiddleY);
				currentScale = imageView.mMaxZoom;
				imageView.zoomToNoCenterValue(currentScale, currentMiddleX,
						currentMiddleY);
			} else if (currentScale < imageView.mMinZoom) {
				imageView.zoomToNoCenterWithAni(currentScale,
						imageView.mMinZoom, currentMiddleX, currentMiddleY);
				currentScale = imageView.mMinZoom;
				imageView.zoomToNoCenterValue(currentScale, currentMiddleX,
						currentMiddleY);
			} else {
				imageView.zoomToNoCenter(currentScale, currentMiddleX,
						currentMiddleY);
			}
			imageView.center(true, true);
			imageView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mOnScale = false;
				}
			}, 300);
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			mOnScale = true;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector, float mx, float my) {
			ImageViewTouch imageView = getCurrentImageView();
			if (imageView == null) {
				return true;
			}
			float ns = imageView.getScale() * detector.getScaleFactor();
			currentScale = ns;
			currentMiddleX = mx;
			currentMiddleY = my;
			if (detector.isInProgress()) {
				imageView.zoomToNoCenter(ns, mx, my);
			}
			return true;
		}
	}

	private class MyGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// Log.d(TAG, "gesture onScroll");
			if (mOnScale) {
				return true;
			}
			if (mPaused) {
				return false;
			}
			ImageViewTouch imageView = getCurrentImageView();
			if (imageView == null) {
				return true;
			}
			imageView.panBy(-distanceX, -distanceY);
			imageView.center(true, true);

			// 超出边界效果去掉这个
			imageView.center(true, true);

			return true;
		}

		@Override
		public boolean onUp(MotionEvent e) {
			return super.onUp(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mControlsShow) {
			} else {
			}

			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (mPaused) {
				return false;
			}
			ImageViewTouch imageView = getCurrentImageView();
			if (imageView == null) {
				return true;
			}
			if (imageView.mBaseZoom < 1) {
				if (imageView.getScale() > 2F) {
					imageView.zoomTo(1f);
				} else {
					imageView.zoomToPoint(3f, e.getX(), e.getY());
				}
			} else {
				if (imageView.getScale() > (imageView.mMinZoom + imageView.mMaxZoom) / 2f) {
					imageView.zoomTo(imageView.mMinZoom);
				} else {
					imageView.zoomToPoint(imageView.mMaxZoom, e.getX(),
							e.getY());
				}
			}
			return true;
		}
	}

}
