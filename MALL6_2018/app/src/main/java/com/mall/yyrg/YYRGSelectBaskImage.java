package com.mall.yyrg;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.view.R;
import com.mall.view.messageboard.GestureDetector;
import com.mall.view.messageboard.ImageViewTouch;
import com.mall.view.messageboard.PagerAdapter;
import com.mall.view.messageboard.ScaleGestureDetector;
import com.mall.view.messageboard.ViewPager;
import com.mall.yyrg.adapter.BitmapUtil;
import com.mall.yyrg.adapter.CheckImageLoaderConfiguration;
import com.mall.yyrg.adapter.YYRGUtil;
import com.mall.yyrg.model.MyApplication;
import com.mall.yyrg.model.PhotoFolderFragment;
import com.mall.yyrg.model.PhotoFolderFragment.OnPageLodingClickListener;
import com.mall.yyrg.model.PhotoFragment;
import com.mall.yyrg.model.PhotoFragment.OnPhotoSelectClickListener;
import com.mall.yyrg.model.PhotoInfo;
import com.mall.yyrg.model.PhotoSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 选择图片的功能 2014年8月29日 10:37:44
 * 
 * @author Administrator
 * 
 */
public class YYRGSelectBaskImage extends FragmentActivity implements
		OnPageLodingClickListener, OnPhotoSelectClickListener {

	private PhotoFolderFragment photoFolderFragment;

	private TextView btnback, btnright;

	private TextView title;

	private List<PhotoInfo> hasList;

	private FragmentManager manager;
	private RelativeLayout re_yulan;
	private int backInt = 0;
	private PopupWindow distancePopup = null;
	private List<ImageView> arrImages;
	private PagerAdapter adapter;
	private TextView yulan;
	private LinearLayout top_view;
	private TextView show_image_count;
	private Dialog dialog;
	
	private List<PhotoInfo> allPhotoInfos=new ArrayList<PhotoInfo>();
	
	/**
	 * 已选择图片数量
	 */
	private int count;
	//当前是第几个viewpager
	private String nowView="1";
	
	
	private static final int PAGER_MARGIN_DP = 40;
	private com.mall.viewpager.ViewPager mViewPager;
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
	private int maxCount=6;
	public Map<Integer, ImageViewTouch> viewss = new HashMap<Integer, ImageViewTouch>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yyrg_select_bask_image);

		getWindowManager().getDefaultDisplay().getMetrics(
				MyApplication.getDisplayMetrics());

		count = getIntent().getIntExtra("count", 0);
        maxCount=getIntent().getIntExtra("maxCount", 6);
		manager = getSupportFragmentManager();

		hasList = new ArrayList<PhotoInfo>();
		final float scale = getResources().getDisplayMetrics().density;
		pagerMarginPixels = (int) (PAGER_MARGIN_DP * scale + 0.5f);
		btnback = (TextView) findViewById(R.id.btnback);
		btnright = (TextView) findViewById(R.id.btnright);
		title = (TextView) findViewById(R.id.title);
		re_yulan = (RelativeLayout) findViewById(R.id.re_yulan);
		yulan = (TextView) findViewById(R.id.yulan);
		top_view = (LinearLayout) findViewById(R.id.top_view);
		yulan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (hasList.size()==0) {
					Toast.makeText(YYRGSelectBaskImage.this, "请选择图片", Toast.LENGTH_SHORT).show();
					return;
				}
				// TODO Auto-generated method stub
				getPopupWindow();
				startPopupWindow();
				distancePopup.showAsDropDown(top_view);
			}
		});
		btnback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (backInt == 0) {
					finish();
				} else if (backInt == 1) {
					re_yulan.setVisibility(View.INVISIBLE);
					backInt--;
					hasList.clear();
					title.setText("请选择相册");
					FragmentTransaction transaction = manager.beginTransaction();
					transaction.show(photoFolderFragment).commit();
					manager.popBackStack(0, 0);
				}
			}
		});
		btnright.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (hasList.size() > 0) {
					BitmapUtil.getPhotoInfos=hasList;
					Intent intent = new Intent();
	                setResult(1001, intent);
	                //    结束当前这个Activity对象的生命
	                finish();
				} else {
					Toast.makeText(YYRGSelectBaskImage.this, "至少选择一张图片",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		title.setText("请选择相册");

		photoFolderFragment = new PhotoFolderFragment();

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.body, photoFolderFragment);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
	}

	@Override
	public void onPageLodingClickListener(List<PhotoInfo> list) {
		// TODO Auto-generated method stub
		title.setText("已选择0张");
		re_yulan.setVisibility(View.VISIBLE);
		FragmentTransaction transaction = manager.beginTransaction();
		PhotoFragment photoFragment = new PhotoFragment();
		Bundle args = new Bundle();
		PhotoSerializable photoSerializable = new PhotoSerializable();
		for (PhotoInfo photoInfoBean : list) {
			photoInfoBean.setChoose(false);
		}
		photoSerializable.setList(list);
		args.putInt("count", count);
		args.putInt("maxCount", maxCount);
		args.putSerializable("list", photoSerializable);
		photoFragment.setArguments(args);
		transaction = manager.beginTransaction();
		transaction.hide(photoFolderFragment).commit();
		transaction = manager.beginTransaction();
		transaction.add(R.id.body, photoFragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
		backInt++;
	}

	@Override
	public void onPhotoSelectClickListener(List<PhotoInfo> list) {
		// TODO Auto-generated method stub
		hasList.clear();
		for (PhotoInfo photoInfoBean : list) {
			if (photoInfoBean.isChoose()) {
				hasList.add(photoInfoBean);
			}
		}
		title.setText("已选择" + hasList.size() + "张");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && backInt == 0) {
			finish();
		} else if (keyCode == KeyEvent.KEYCODE_BACK && backInt == 1) {
			re_yulan.setVisibility(View.INVISIBLE);
			backInt--;
			hasList.clear();
			title.setText("请选择相册");
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.show(photoFolderFragment).commit();
			manager.popBackStack(0, 0);
		}
		return false;
	}

	/**
	 * 初始化并弹出popupwindow
	 * 
	 * @param
	 */
	private void startPopupWindow() {
		allPhotoInfos=hasList;
		View pview = getLayoutInflater().inflate(R.layout.yyrg_show_select_image,
				null, false);
		TextView returns = (TextView) pview.findViewById(R.id.top_back);
		TextView delete = (TextView) pview.findViewById(R.id.delete);
		show_image_count = (TextView) pview.findViewById(R.id.show_image_count);
		TextView show_wancheng=(TextView) pview.findViewById(R.id.show_wancheng);
		show_wancheng.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BitmapUtil.getPhotoInfos=allPhotoInfos;
				Intent intent = new Intent();
                setResult(1001, intent);
                //    结束当前这个Activity对象的生命
                finish();
			}
		});
		returns.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				distancePopup.dismiss();
			}
		});
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteImage();
			}
		});
		mViewPager = (com.mall.viewpager.ViewPager) pview.findViewById(R.id.vp);
		arrImages = new ArrayList<ImageView>();
		bitmaps.clear();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		for (int i = 0; i < hasList.size(); i++) {
			ImageView imageView = new ImageView(this);
			Bitmap bitmap = YYRGUtil.compressImageFromFile(allPhotoInfos.get(i)
					.getPath_absolute());
			allPhotoInfos.get(i).setPath_file(allPhotoInfos.get(i).getPath_absolute());
			imageView.setImageBitmap(bitmap);
			imageView.setTag(i + 1);
			arrImages.add(imageView);
			Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
			bitmaps.add(image);
		}
		mViewPager.setPageMargin(pagerMarginPixels);
		mPagerAdapter = new ImagePagerAdapter();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPager.setCurrentItem(currentPic);
		setupOnTouchListeners(mViewPager);
		show_image_count.setText("1/" + arrImages.size());
		initpoputwindow(pview);
	}

	private void getPopupWindow() {
		if (distancePopup != null && distancePopup.isShowing()) {
			distancePopup.dismiss();
		}
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
		distancePopup.setAnimationStyle(R.style.popupanimation);
	}
	private void deleteImage() {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.tuichu_login, null);
		dialog = new AlertDialog.Builder(this).create();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView update_number = (TextView) layout
				.findViewById(R.id.update_number);
		update_number.setText("要删除这张图片吗？");
		TextView no_tuichu = (TextView) layout.findViewById(R.id.no_tuichu);
		no_tuichu.setText("否");
		TextView queding_tuichu = (TextView) layout
				.findViewById(R.id.queding_tuichu);
		queding_tuichu.setText("是");
		no_tuichu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		queding_tuichu.setOnClickListener(new OnClickListener() {

			
			@Override
			public void onClick(View v) {
				if (allPhotoInfos.size()<1) {
					dialog.dismiss();
					distancePopup.dismiss();
					return;
				}
				// TODO Auto-generated method stub
				int temp=Integer.parseInt(nowView)-1;
				allPhotoInfos.remove(temp);
				bitmaps.clear();
				arrImages = new ArrayList<ImageView>();
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
				int height = dm.heightPixels;
				for (int i = 0; i < allPhotoInfos.size(); i++) {
					ImageView imageView = new ImageView(YYRGSelectBaskImage.this);
					Bitmap bitmap = YYRGUtil.compressImageFromFile(allPhotoInfos.get(i)
							.getPath_absolute());
					allPhotoInfos.get(i).setPath_file(allPhotoInfos.get(i).getPath_absolute());
					imageView.setImageBitmap(bitmap);
					imageView.setTag(i + 1);
					arrImages.add(imageView);
					Bitmap image = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
					bitmaps.add(image);
				}
				mViewPager.setPageMargin(pagerMarginPixels);
				mPagerAdapter = new ImagePagerAdapter();
				mViewPager.setAdapter(mPagerAdapter);
				mViewPager.setOnPageChangeListener(mPageChangeListener);
				mViewPager.setCurrentItem(currentPic);
				setupOnTouchListeners(mViewPager);
				if (temp+1!=1) {
					mViewPager.setCurrentItem(temp);
					show_image_count.setText(temp+1+"/" + arrImages.size());
				}else if (temp+1==arrImages.size()) {
					show_image_count.setText(arrImages.size()+"/" + arrImages.size());
				}
				else {
					show_image_count.setText("1/" + arrImages.size());
				}
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}
	private class ImagePagerAdapter extends com.mall.viewpager.PagerAdapter {
		public Map<Integer, ImageViewTouch> views = new HashMap<Integer, ImageViewTouch>();

		@Override
		public int getCount() {
			return bitmaps.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ImageViewTouch imageView = new ImageViewTouch(YYRGSelectBaskImage.this);
			imageView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			imageView.setFocusableInTouchMode(true);
			Bitmap b = bitmaps.get(position);
			imageView.setImageBitmapResetBase(b, true);
			imageView.setTag(position+1);
			((com.mall.viewpager.ViewPager) container).addView(imageView);
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
				ImageViewTouch img = new ImageViewTouch(YYRGSelectBaskImage.this);
				img.setImageBitmap(BitmapFactory.decodeResource(
						YYRGSelectBaskImage.this.getResources(),
						R.drawable.ic_launcher));
				return img;
			}
		} else {
			ImageViewTouch img = new ImageViewTouch(YYRGSelectBaskImage.this);
			img.setImageBitmap(BitmapFactory.decodeResource(
					YYRGSelectBaskImage.this.getResources(), R.drawable.ic_launcher));
			return img;
		}
	}
	com.mall.viewpager.ViewPager.OnPageChangeListener mPageChangeListener = new com.mall.viewpager.ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int position, int prePosition) {
			ImageView imageView=viewss.get(position);
			nowView=""+imageView.getTag();
			show_image_count.setText(imageView.getTag() + "/" + arrImages.size());
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
