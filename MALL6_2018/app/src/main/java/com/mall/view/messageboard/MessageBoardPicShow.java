package com.mall.view.messageboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lin.component.CustomProgressDialog;
import com.mall.util.Util;
import com.mall.view.R;

public class MessageBoardPicShow extends Activity {
	private static final int PAGER_MARGIN_DP = 40;
	private ViewPager mViewPager;
	private ImagePagerAdapter mPagerAdapter;
	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private TextView picindicator;
	private boolean mPaused;
	private boolean mOnScale = false;
	private boolean mOnPagerScoll = false;
	private boolean mControlsShow = false;
	private String picFiles = "";
	private int currentPic = 0;
	private int pagerMarginPixels = 0;
	private String[] pics = null;
	private BitmapUtils bmUtils;
	private List<Bitmap> bitmaps=new ArrayList<Bitmap>();
	private String fileName="";
	private Bitmap bi;
	private PopupWindow distancePopup;
	private HashMap<String, Bitmap> map=new HashMap<String, Bitmap>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_board_pic_show);
		init();
	}
	private void init() {
		bmUtils = new BitmapUtils(this);
	    Util.initTop2(this, "心情图片", "保存图片", new OnClickListener() {
			@Override
			public void onClick(View v) {
				MessageBoardPicShow.this.finish();
			}
		}, new OnClickListener() {
			@Override
			public void onClick(View v) {
				String file=pics[mViewPager.getCurrentItem()];
				bi=map.get(file);
				file=file.substring(file.indexOf("mood"),file.length());
				file=file.replace("/", "_");
				if(!Util.isNull(bi)){
					if(Util.saveBitmapToSdCard(file, bi)){
						Toast.makeText(MessageBoardPicShow.this, "当前图片已经保存到/storage/emnulated/0/DCIM/心情图片/"+file, 1).show();
					}else{
						Toast.makeText(MessageBoardPicShow.this, "保存失败", 1).show();
					}
				}
			}
		});
		final float scale = getResources().getDisplayMetrics().density;
		pagerMarginPixels = (int) (PAGER_MARGIN_DP * scale + 0.5f);
		mViewPager = (ViewPager) findViewById(R.id.message_board_pic_show_pager);
		picindicator = (TextView) findViewById(R.id.picindicator);
		currentPic = Integer.parseInt(this.getIntent().getStringExtra("currentPic").trim());
		if(!Util.isNull( this.getIntent().getStringExtra("picFiles"))){
			picFiles = this.getIntent().getStringExtra("picFiles");
			pics = picFiles.split("\\|,\\|");
			picindicator.setText((currentPic + 1) + "/" + pics.length);
			downLoadBitmap(pics);
		}else{
			picFiles = this.getIntent().getStringExtra("imgfiles");
			pics = picFiles.split("spkxqadapter");
			picindicator.setText((currentPic + 1) + "/" + pics.length);
			downLoadBitmap(pics);
		}
	}
    
	private void downLoadBitmap(final String[] pics) {
		mViewPager.setPageMargin(pagerMarginPixels);
		mPagerAdapter = new ImagePagerAdapter();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPager.setCurrentItem(currentPic);
		setupOnTouchListeners(mViewPager);
	}
	@Override
	public void onStart() {
		super.onStart();
		mPaused = false;
	}
	@Override
	public void onStop() {
		super.onStop();
		mPaused = true;
	}
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
					if(null == imageView.mBitmapDisplayed.getBitmap()){
						Util.show("图片正在加载中...", MessageBoardPicShow.this);
						return true;
					}
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
	@Override
	public boolean dispatchTouchEvent(MotionEvent m) {
		if (mPaused)
			return true;
		return super.dispatchTouchEvent(m);
	}
	@Override
	protected void onDestroy() {
		for(ImageViewTouch imageView : mPagerAdapter.views.values()){
			if (imageView != null) {
//				imageView.mBitmapDisplayed.recycle();
				imageView.setImageBitmapResetBase(null, true);
				imageView.clear();
			}
		}
		bmUtils.cancel();
		super.onDestroy();
	}
	private ImageViewTouch getCurrentImageView() {
		if (mPagerAdapter != null && mPagerAdapter.views != null) {
			if (mPagerAdapter.views != null) {
				if (mPagerAdapter.views.get(mViewPager.getCurrentItem()) != null) {
					return (ImageViewTouch) mPagerAdapter.views.get((mViewPager
							.getCurrentItem()));
				} else {
					ImageViewTouch img = new ImageViewTouch(
							MessageBoardPicShow.this);
					img.setImageBitmap(BitmapFactory.decodeResource(
							MessageBoardPicShow.this.getResources(),
							R.drawable.no_get_banner));
					return img;
				}
			}else{
				ImageViewTouch img = new ImageViewTouch(MessageBoardPicShow.this);
				img.setImageBitmap(BitmapFactory.decodeResource(
						MessageBoardPicShow.this.getResources(),
						R.drawable.no_get_banner));
				return img;
			}
		} else {
			ImageViewTouch img = new ImageViewTouch(MessageBoardPicShow.this);
			img.setImageBitmap(BitmapFactory.decodeResource(
					MessageBoardPicShow.this.getResources(),
					R.drawable.no_get_banner));
			return img;
		}
	}
	ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int position, int prePosition) {
			if(pics!=null){
				picindicator.setText((position + 1) + "/" + pics.length);
			}else if(bitmaps!=null){
				picindicator.setText((position + 1) + "/" + bitmaps.size());
			}
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
	private class ImagePagerAdapter extends PagerAdapter {
		public Map<Integer, ImageViewTouch> views = new HashMap<Integer, ImageViewTouch>();
		public ImagePagerAdapter() {
			super();
			if(pics!=null&&pics.length>0){
				int index = 0;
				BitmapDisplayConfig config = new BitmapDisplayConfig();
				config.setLoadFailedDrawable(MessageBoardPicShow.this.getResources().getDrawable(R.drawable.no_get_banner));
				config.setAutoRotation(true);
				config.setShowOriginal(true);
				final CustomProgressDialog dialog = Util.showProgress("图片正在加载中...", MessageBoardPicShow.this);
				for(final String url : pics){
					final ImageViewTouch imageView = new ImageViewTouch(MessageBoardPicShow.this);
					imageView.setTag(index+"");
					imageView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
					imageView.setFocusableInTouchMode(true);
					bmUtils.display(imageView, url,config,new DefaultBitmapLoadCallBack<View>(){
						@Override
						public void onLoadCompleted(View container, String uri,final Bitmap bitmap, BitmapDisplayConfig config,BitmapLoadFrom from) {
							super.onLoadCompleted(container, uri, bitmap, config, from);
							((ImageViewTouch)container).setImageBitmapResetBase(bitmap, true);
							imageView.setTag(-7,bitmap);
							map.put(url, bitmap);
							if("0".equals(container.getTag()+"")){
								dialog.cancel();
								dialog.dismiss();
							}
						}
						@Override
						public void onLoadFailed(View container, String uri,
								Drawable drawable) {
							Resources res = MessageBoardPicShow.this.getResources();
							Bitmap bitma = BitmapFactory.decodeResource(res, R.drawable.no_get_banner);
							imageView.setImageResource(R.drawable.no_get_banner);
							imageView.setTag(-7,bitma);
							if("0".equals(container.getTag()+"")){
								dialog.cancel();
								dialog.dismiss();
							}
						}
					});
					views.put(index, imageView);
					index++;
				}
			}else{
				if(bitmaps!=null&&bitmaps.size()>0){
					for(int i=0;i<bitmaps.size();i++){
						final ImageViewTouch imageView = new ImageViewTouch(MessageBoardPicShow.this);
						imageView.setTag(i+"");
						imageView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
						imageView.setFocusableInTouchMode(true);
						imageView.setImageBitmap(bitmaps.get(i));
						views.put(i, imageView);
					}
				}
			}
		}

		@Override
		public int getCount() {
			return views.size();
		}
		@Override
		public Object instantiateItem(View container, int position) {
			ImageViewTouch imageView = views.get(position);
			((ViewPager) container).addView(imageView);
			bi=(Bitmap) imageView.getTag(-7);
			return imageView;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			ImageViewTouch imageView = (ImageViewTouch) object;
			((ViewPager) container).removeView(imageView);
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
	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
	}
	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param i
	 */
	private void startPopupWindow(final Bitmap bitmap,final String file) {
		View pview = getLayoutInflater().inflate(
				R.layout.xq_pic_store, null, false);
		TextView store = (TextView) pview.findViewById(R.id.store);
		TextView cancel = (TextView) pview.findViewById(R.id.cancel);
		store.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Util.saveBitmapToSdCard(file, bitmap)){
					Toast.makeText(MessageBoardPicShow.this, "保存成功", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(MessageBoardPicShow.this, "保存失败", Toast.LENGTH_LONG).show();
				}
				distancePopup.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
			}
		});
		initpoputwindow(pview);
	} 
	/**
	 * 新建一个popupwindow实例
	 * 
	 * @param view
	 */
	private void initpoputwindow(View view) {
		distancePopup = new PopupWindow(view,
				android.view.WindowManager.LayoutParams.MATCH_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
		distancePopup.setOutsideTouchable(true);
		distancePopup.setFocusable(true);
		distancePopup.setBackgroundDrawable(new BitmapDrawable());
		distancePopup.setAnimationStyle(R.style.popupanimationupanddown);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			MessageBoardPicShow.this.finish();
		}
		return true;
	}
}
