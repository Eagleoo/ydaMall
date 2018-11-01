package com.mall.serving.community.view.picviewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.progresswheel.ProgressWheel;
import com.mall.view.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PicViewpagerPopup extends PopupWindow {
	private static final int PAGER_MARGIN_DP = 40;
	private ViewPager mViewPager;
	private ImagePagerAdapter mPagerAdapter;
	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private TextView picindicator;
	private ProgressWheel progressBar;
	private boolean mPaused;
	private boolean mOnScale = false;
	private boolean mOnPagerScoll = false;
	private boolean mControlsShow = false;

	private int currentPic = 0;
//	private int pagerMarginPixels = 0;
	private List<String> pics;

	private Context context;
	private HashMap<String, Bitmap> map;

	private int index;

	public PicViewpagerPopup(final Context context, final List<String> pics,
			int currentPic, final boolean isSave, final List<String> drr) {
		super(context);
		this.context = context;
		map = new HashMap<String, Bitmap>();
		this.currentPic = currentPic;
		View v = LayoutInflater.from(context).inflate(
				R.layout.community_custom_picviewpager_popup, null);

		final float scale = context.getResources().getDisplayMetrics().density;
//		pagerMarginPixels = (int) (PAGER_MARGIN_DP * scale + 0.5f);
		mViewPager = (ViewPager) v
				.findViewById(R.id.message_board_pic_show_pager);
		picindicator = (TextView) v.findViewById(R.id.picindicator);
		progressBar = (ProgressWheel) v.findViewById(R.id.progressBar);

		final View bt_close = v.findViewById(R.id.bt_close);
		final View bt_del = v.findViewById(R.id.bt_del);
		if (isSave) {
			bt_del.setBackgroundResource(R.drawable.community_uploadpic_save);
		} else {
			bt_del.setBackgroundResource(R.drawable.community_uploadpic_bt_shanchu);
		}

		this.pics = pics;
		if (pics == null || pics.size() < 2) {
			picindicator.setVisibility(View.GONE);
		} else {
			picindicator.setText((currentPic + 1) + "/" + pics.size());
		}

		downLoadBitmap(pics);

		setAnimationStyle(R.style.picviewpager_animation);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(v);
		showAtLocation(v, Gravity.CENTER, 0, 0);

		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(final View v) {

				AnimeUtil.startAnimation(context, v, R.anim.small_2_big,
						new OnAnimEnd() {

							@Override
							public void start() {
								// TODO Auto-generated method stub

							}

							@Override
							public void repeat() {
								// TODO Auto-generated method stub

							}

							@Override
							public void end() {
								if (v == bt_del) {

									if (isSave) {

										String file = pics.get(mViewPager
												.getCurrentItem());
										Bitmap bi = map.get(file);
//										 if (file.contains("://")) {
//										 file = file.substring(
//										 file.indexOf("://")
//										 + "://".length(),
//										 file.length());
//										 }

										long millis = Calendar.getInstance().getTimeInMillis();
								

//										 file = file.replace("/", "_");
										if (!Util.isNull(bi)
												&& Util.saveBitmapToSdCard(
														millis+".jpg" , bi)) {

											Util.show("当前图片已经保存到/sdcard/DCIM/远大图片/");

										} else {
											Util.show("图片保存失败！");
										}

									} else {

										if (drr != null) {
											drr.remove(mViewPager
													.getCurrentItem());
										}

										pics.remove(mViewPager.getCurrentItem());

										mPagerAdapter = new ImagePagerAdapter();
										mViewPager.setAdapter(mPagerAdapter);
										if (pics == null || pics.size() < 2) {
											picindicator
													.setVisibility(View.GONE);
										} else {
											picindicator.setText(1 + "/"
													+ pics.size());
										}
									}

								}

							}
						});

			}

		};
		bt_del.setOnClickListener(onClickListener);
		bt_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();

			}
		});
		
		
	setOnDismissListener(new OnDismissListener() {
		
		@Override
		public void onDismiss() {
			if (map!=null&&map.size()>0) {
				
				Set<String> key = map.keySet();
				 for (Iterator it = key.iterator(); it.hasNext();) {
			            String s = (String) it.next();
			            
			            Bitmap bitmap = map.get(s);
			            if (bitmap!=null&&!bitmap.isRecycled()) {
							bitmap.recycle();
							bitmap=null;
						}
			        }
			}
			
			
		}
	});

	}

	private void downLoadBitmap(final List<String> pics) {
//		mViewPager.setPageMargin(pagerMarginPixels);
		mPagerAdapter = new ImagePagerAdapter();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
		mViewPager.setCurrentItem(currentPic);
		setupOnTouchListeners(mViewPager);
	}

	private void setupOnTouchListeners(View rootView) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
			mScaleGestureDetector = new ScaleGestureDetector(context,
					new MyOnScaleGestureListener());
		}
		mGestureDetector = new GestureDetector(context, new MyGestureListener());
		OnTouchListener rootListener = new OnTouchListener() {
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
					if (null == imageView.mBitmapDisplayed.getBitmap()) {
						// Util.show("图片正在加载中...");
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

		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				for (ImageViewTouch imageView : mPagerAdapter.views.values()) {
					if (imageView != null) {
						imageView.mBitmapDisplayed.recycle();
						imageView.setImageBitmapResetBase(null, true);
						imageView.clear();
					}
				}

			}
		});
	}

	private ImageViewTouch getCurrentImageView() {
		if (mPagerAdapter != null && mPagerAdapter.views != null) {
			if (mPagerAdapter.views != null) {
				if (mPagerAdapter.views.get(mViewPager.getCurrentItem()) != null) {
					return (ImageViewTouch) mPagerAdapter.views.get((mViewPager
							.getCurrentItem()));
				} else {
					ImageViewTouch img = new ImageViewTouch(context);
					img.setImageBitmap(BitmapFactory.decodeResource(
							context.getResources(),
							R.drawable.community_image_defult_fail));
					return img;
				}
			} else {
				ImageViewTouch img = new ImageViewTouch(context);
				img.setImageBitmap(BitmapFactory.decodeResource(
						context.getResources(),
						R.drawable.community_image_defult_fail));
				return img;
			}
		} else {
			ImageViewTouch img = new ImageViewTouch(context);
			img.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(),
					R.drawable.community_image_defult_fail));
			return img;
		}
	}

	ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int position, int prePosition) {
			picindicator.setText((position + 1) + "/" + pics.size());
			mViewPager.setCurrentItem(position);
			index = position;
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
			int index = 0;
			BitmapDisplayConfig config = new BitmapDisplayConfig();
			config.setLoadFailedDrawable(context.getResources().getDrawable(
					R.drawable.community_image_defult_fail));
			config.setAutoRotation(true);
			config.setShowOriginal(true);

			for (final String url : pics) {
				final ImageViewTouch imageView = new ImageViewTouch(context);
				imageView.setTag(index + "");
				imageView.setLayoutParams(new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				imageView.setFocusableInTouchMode(true);

				views.put(index, imageView);
				index++;
			}
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@SuppressWarnings("null")
		@Override
		public Object instantiateItem(View container, final int position) {

			final ImageViewTouch imageView = views.get(position);

			String path = pics.get(position);

			Bitmap bitmap = null;
			if (map.containsKey(path)) {
				bitmap = map.get(path);

			}

			if (bitmap != null && !bitmap.isRecycled()) {

				imageView.setImageBitmapResetBase(bitmap, true);

			} else {
				DisplayImageOptions display=AnimeUtil.getImageSimpleOption();
				if (pics!=null&&pics.size()==1) {
					display=AnimeUtil.getImageViewPagerSimpleOption();
				}

				AnimeUtil.getImageLoad().displayImage(path, imageView,
						display,
						new ImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {

								if (index == position) {
									progressBar.setVisibility(View.VISIBLE);

								}

								imageView.setVisibility(View.GONE);
								
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {

								System.out.println(failReason.toString());
								Drawable drawable = context
										.getResources()
										.getDrawable(
												R.drawable.community_image_defult_fail);
								Bitmap bitmap = Util.drawable2Bitmap(drawable);

								if (bitmap!=null&&!bitmap.isRecycled()) {
									imageView.setImageBitmapResetBase(bitmap,
											true);
								}

								if (index == position) {
									progressBar.setVisibility(View.GONE);

								}

								imageView.setVisibility(View.VISIBLE);
								
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View container, Bitmap loadedImage) {

								map.put(pics.get(position), loadedImage);
								if (index == position) {
									progressBar.setVisibility(View.GONE);

								}

								imageView.setVisibility(View.VISIBLE);
								if (loadedImage!=null&&!loadedImage.isRecycled()) {
									((ImageViewTouch) container)
											.setImageBitmapResetBase(
													loadedImage, true);
								}

								
							}

							@Override
							public void onLoadingCancelled(String arg0,
									View arg1) {
								// TODO Auto-generated method stub
								
							}
						}, new ImageLoadingProgressListener() {
							int cicyle = 0;
							int cicyleOld = 0;

							@Override
							public void onProgressUpdate(String arg0,
									View arg1, int pogress, int total) {

								if (index == position) {
									int pro = (int) ((pogress / (double) total) * 100);
									cicyle = (int) ((pogress / (double) total) * 360);

									progressBar.setText(pro + "%");
									if (pro == 0) {
										imageView.setVisibility(View.GONE);
										progressBar.resetCount();
										progressBar.setVisibility(View.VISIBLE);
									}
									if (pro == 1) {
										progressBar.setVisibility(View.GONE);
										imageView.setVisibility(View.VISIBLE);
									}
									int leng = cicyle - cicyleOld;
									for (int i = 0; i < leng; i++) {
										progressBar.incrementProgress();
									}
									cicyleOld = cicyle;
								}

							}
						});
			}

			((ViewPager) container).addView(imageView);

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
}
